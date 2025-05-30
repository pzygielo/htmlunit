/*
 * Copyright (c) 2002-2025 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.javascript.configuration;

import static org.htmlunit.javascript.configuration.SupportedBrowser.CHROME;
import static org.htmlunit.javascript.configuration.SupportedBrowser.EDGE;
import static org.htmlunit.javascript.configuration.SupportedBrowser.FF;
import static org.htmlunit.javascript.configuration.SupportedBrowser.FF_ESR;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlunit.BrowserVersion;
import org.htmlunit.corejs.javascript.SymbolKey;
import org.htmlunit.javascript.HtmlUnitScriptable;
import org.htmlunit.javascript.JavaScriptEngine;

/**
 * An abstract container for all the JavaScript configuration information.
 *
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Chris Erskine
 * @author Ahmed Ashour
 * @author Ronald Brill
 * @author Frank Danek
 */
public abstract class AbstractJavaScriptConfiguration {

    private static final Log LOG = LogFactory.getLog(AbstractJavaScriptConfiguration.class);

    private Map<Class<?>, Class<? extends HtmlUnitScriptable>> domJavaScriptMap_;

    private final ArrayList<ClassConfiguration> configuration_;
    private ClassConfiguration scopeConfiguration_;

    /**
     * Constructor.
     * @param browser the browser version to use
     * @param scopeClass the scope class for faster access
     */
    protected AbstractJavaScriptConfiguration(final BrowserVersion browser, final Class<?> scopeClass) {
        configuration_ = new ArrayList<>(getClasses().length);

        for (final Class<? extends HtmlUnitScriptable> klass : getClasses()) {
            final ClassConfiguration config = getClassConfiguration(klass, browser);
            if (config != null) {
                configuration_.add(config);
                if (klass == scopeClass) {
                    scopeConfiguration_ = config;
                }
            }
        }
    }

    /**
     * @return the classes configured by this configuration
     */
    protected abstract Class<? extends HtmlUnitScriptable>[] getClasses();

    /**
     * Gets all the configurations.
     * @return the class configurations
     */
    public Iterable<ClassConfiguration> getAll() {
        return configuration_;
    }

    /**
     * Returns the class configuration of the given {@code klass}.
     *
     * @param klass the class
     * @param browserVersion the browser version
     * @return the class configuration
     */
    public static ClassConfiguration getClassConfiguration(final Class<? extends HtmlUnitScriptable> klass,
        final BrowserVersion browserVersion) {
        if (browserVersion != null) {
            final SupportedBrowser expectedBrowser;
            if (browserVersion.isChrome()) {
                expectedBrowser = CHROME;
            }
            else if (browserVersion.isEdge()) {
                expectedBrowser = EDGE;
            }
            else if (browserVersion.isFirefoxESR()) {
                expectedBrowser = FF_ESR;
            }
            else if (browserVersion.isFirefox()) {
                expectedBrowser = FF;
            }
            else {
                expectedBrowser = CHROME;  // our current fallback
            }

            final String hostClassName = klass.getName();
            final JsxClasses jsxClasses = klass.getAnnotation(JsxClasses.class);
            if (jsxClasses != null) {
                if (klass.getAnnotation(JsxClass.class) != null) {
                    throw new RuntimeException("Invalid JsxClasses/JsxClass annotation; class '"
                        + hostClassName + "' has both.");
                }
                final JsxClass[] jsxClassValues = jsxClasses.value();
                if (jsxClassValues.length == 1) {
                    throw new RuntimeException("No need to specify JsxClasses with a single JsxClass for "
                            + hostClassName);
                }
                final Set<Class<?>> domClasses = new HashSet<>();

                boolean isJsObject = false;
                String className = null;

                final String extendedClassName;
                final Class<?> superClass = klass.getSuperclass();
                if (superClass.getAnnotation(JsxClass.class) == null
                        && superClass.getAnnotation(JsxClasses.class) == null) {
                    extendedClassName = "";
                }
                else {
                    extendedClassName = superClass.getSimpleName();
                }

                for (final JsxClass jsxClass : jsxClassValues) {
                    if (jsxClass != null && isSupported(jsxClass.value(), expectedBrowser)) {
                        domClasses.add(jsxClass.domClass());
                        if (jsxClass.isJSObject()) {
                            isJsObject = true;
                        }
                        if (!jsxClass.className().isEmpty()) {
                            className = jsxClass.className();
                        }
                    }
                }

                final ClassConfiguration classConfiguration =
                        new ClassConfiguration(klass, domClasses.toArray(new Class<?>[0]), isJsObject,
                                className, extendedClassName);

                process(classConfiguration, expectedBrowser);
                return classConfiguration;
            }

            final JsxClass jsxClass = klass.getAnnotation(JsxClass.class);
            if (jsxClass != null && isSupported(jsxClass.value(), expectedBrowser)) {

                final Set<Class<?>> domClasses = new HashSet<>();
                final Class<?> domClass = jsxClass.domClass();
                if (domClass != null && domClass != Object.class) {
                    domClasses.add(domClass);
                }

                String className = jsxClass.className();
                if (className.isEmpty()) {
                    className = null;
                }

                final String extendedClassName;
                final Class<?> superClass = klass.getSuperclass();
                if (superClass.getAnnotation(JsxClass.class) == null
                        && superClass.getAnnotation(JsxClasses.class) == null) {
                    extendedClassName = "";
                }
                else {
                    extendedClassName = superClass.getSimpleName();
                }

                final ClassConfiguration classConfiguration
                    = new ClassConfiguration(klass,
                            domClasses.toArray(new Class<?>[0]),
                            jsxClass.isJSObject(),
                            className,
                            extendedClassName);

                process(classConfiguration, expectedBrowser);
                return classConfiguration;
            }
        }
        return null;
    }

    private static void process(final ClassConfiguration classConfiguration, final SupportedBrowser expectedBrowser) {
        final Map<String, Method> allGetters = new ConcurrentHashMap<>();
        final Map<String, Method> allSetters = new ConcurrentHashMap<>();

        try {
            // do this as first step to be able to overwrite the symbol later if needed
            classConfiguration.addSymbolConstant(SymbolKey.TO_STRING_TAG, classConfiguration.getHostClassSimpleName());

            for (final Method method : classConfiguration.getHostClass().getDeclaredMethods()) {
                for (final Annotation annotation : method.getAnnotations()) {
                    if (annotation instanceof JsxGetter) {
                        final JsxGetter jsxGetter = (JsxGetter) annotation;
                        if (isSupported(jsxGetter.value(), expectedBrowser)) {
                            String property;
                            if (jsxGetter.propertyName().isEmpty()) {
                                final int prefix = method.getName().startsWith("is") ? 2 : 3;
                                property = method.getName().substring(prefix);
                                property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
                            }
                            else {
                                property = jsxGetter.propertyName();
                            }
                            allGetters.put(property, method);
                        }
                    }
                    else if (annotation instanceof JsxSetter) {
                        final JsxSetter jsxSetter = (JsxSetter) annotation;
                        if (isSupported(jsxSetter.value(), expectedBrowser)) {
                            String property;
                            if (jsxSetter.propertyName().isEmpty()) {
                                property = method.getName().substring(3);
                                property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
                            }
                            else {
                                property = jsxSetter.propertyName();
                            }
                            allSetters.put(property, method);
                        }
                    }
                    if (annotation instanceof JsxSymbol) {
                        final JsxSymbol jsxSymbol = (JsxSymbol) annotation;
                        if (isSupported(jsxSymbol.value(), expectedBrowser)) {
                            final String symbolKeyName;
                            if (jsxSymbol.symbolName().isEmpty()) {
                                symbolKeyName = method.getName();
                            }
                            else {
                                symbolKeyName = jsxSymbol.symbolName();
                            }

                            final SymbolKey symbolKey;
                            if ("iterator".equalsIgnoreCase(symbolKeyName)) {
                                symbolKey = SymbolKey.ITERATOR;
                            }
                            else {
                                throw new RuntimeException("Invalid JsxSymbol annotation; unsupported '"
                                        + symbolKeyName + "' symbol name.");
                            }
                            classConfiguration.addSymbol(symbolKey, method);
                        }
                    }
                    else if (annotation instanceof JsxFunction) {
                        final JsxFunction jsxFunction = (JsxFunction) annotation;
                        if (isSupported(jsxFunction.value(), expectedBrowser)) {
                            final String name;
                            if (jsxFunction.functionName().isEmpty()) {
                                name = method.getName();
                            }
                            else {
                                name = jsxFunction.functionName();
                            }
                            classConfiguration.addFunction(name, method);
                        }
                    }
                    else if (annotation instanceof JsxStaticGetter) {
                        final JsxStaticGetter jsxStaticGetter = (JsxStaticGetter) annotation;
                        if (isSupported(jsxStaticGetter.value(), expectedBrowser)) {
                            final int prefix = method.getName().startsWith("is") ? 2 : 3;
                            String property = method.getName().substring(prefix);
                            property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
                            classConfiguration.addStaticProperty(property, method, null);
                        }
                    }
                    else if (annotation instanceof JsxStaticFunction) {
                        final JsxStaticFunction jsxStaticFunction = (JsxStaticFunction) annotation;
                        if (isSupported(jsxStaticFunction.value(), expectedBrowser)) {
                            final String name;
                            if (jsxStaticFunction.functionName().isEmpty()) {
                                name = method.getName();
                            }
                            else {
                                name = jsxStaticFunction.functionName();
                            }
                            classConfiguration.addStaticFunction(name, method);
                        }
                    }
                    else if (annotation instanceof JsxConstructor) {
                        final JsxConstructor jsxConstructor = (JsxConstructor) annotation;
                        if (isSupported(jsxConstructor.value(), expectedBrowser)) {
                            final String name;
                            if (jsxConstructor.functionName().isEmpty()) {
                                name = classConfiguration.getClassName();
                            }
                            else {
                                name = jsxConstructor.functionName();
                            }
                            classConfiguration.setJSConstructor(name, method);
                        }
                    }
                    else if (annotation instanceof JsxConstructorAlias) {
                        final JsxConstructorAlias jsxConstructorAlias = (JsxConstructorAlias) annotation;
                        if (isSupported(jsxConstructorAlias.value(), expectedBrowser)) {
                            classConfiguration.setJSConstructorAlias(jsxConstructorAlias.alias());
                        }
                    }
                }
            }

            for (final Entry<String, Method> getterEntry : allGetters.entrySet()) {
                final String property = getterEntry.getKey();
                classConfiguration.addProperty(property, getterEntry.getValue(), allSetters.get(property));
            }

            // JsxConstant/JsxSymbolConstant
            for (final Field field : classConfiguration.getHostClass().getDeclaredFields()) {
                for (final Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof JsxConstant) {
                        final JsxConstant jsxConstant = (JsxConstant) annotation;
                        if (isSupported(jsxConstant.value(), expectedBrowser)) {
                            try {
                                classConfiguration.addConstant(field.getName(), field.get(null));
                            }
                            catch (final IllegalAccessException e) {
                                throw JavaScriptEngine.reportRuntimeError(
                                        "Cannot get field '" + field.getName()
                                        + "' for type: " + classConfiguration.getHostClass().getName()
                                        + "reason: " + e.getMessage());
                            }
                        }
                    }
                    if (annotation instanceof JsxSymbolConstant) {
                        final JsxSymbolConstant jsxSymbolConstant = (JsxSymbolConstant) annotation;
                        if (isSupported(jsxSymbolConstant.value(), expectedBrowser)) {
                            final SymbolKey symbolKey;
                            if ("TO_STRING_TAG".equalsIgnoreCase(field.getName())) {
                                symbolKey = SymbolKey.TO_STRING_TAG;
                            }
                            else {
                                throw new RuntimeException("Invalid JsxSymbol annotation; unsupported '"
                                        + field.getName() + "' symbol name.");
                            }
                            classConfiguration.addSymbolConstant(symbolKey, field.get(null).toString());
                        }
                    }
                }
            }
        }
        catch (final Throwable e) {
            throw new RuntimeException(
                    "Processing classConfiguration for class "
                            + classConfiguration.getHostClassSimpleName() + "failed."
                            + " Reason: " + e, e);
        }
    }

    private static boolean isSupported(final SupportedBrowser[] browsers, final SupportedBrowser expectedBrowser) {
        for (final SupportedBrowser browser : browsers) {
            if (browser == expectedBrowser) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the two {@link SupportedBrowser} are compatible or not.
     * @param browser1 the first {@link SupportedBrowser}
     * @param browser2 the second {@link SupportedBrowser}
     * @return whether the two {@link SupportedBrowser} are compatible or not
     *
     * @deprecated as of version 4.8.0; will be removed without replacement
     */
    @Deprecated
    public static boolean isCompatible(final SupportedBrowser browser1, final SupportedBrowser browser2) {
        return browser1 == browser2;
    }

    /**
     * Returns an immutable map containing the DOM to JavaScript mappings. Keys are
     * java classes for the various DOM classes (e.g. HtmlInput.class) and the values
     * are the JavaScript class names (e.g. "HTMLAnchorElement").
     * @param clazz the class to get the scriptable for
     * @return the mappings
     */
    public Class<? extends HtmlUnitScriptable> getDomJavaScriptMappingFor(final Class<?> clazz) {
        if (domJavaScriptMap_ == null) {
            final Map<Class<?>, Class<? extends HtmlUnitScriptable>> map =
                    new ConcurrentHashMap<>(configuration_.size());

            final boolean debug = LOG.isDebugEnabled();
            for (final ClassConfiguration classConfig : configuration_) {
                for (final Class<?> domClass : classConfig.getDomClasses()) {
                    // preload and validate that the class exists
                    if (debug) {
                        LOG.debug("Mapping " + domClass.getName() + " to " + classConfig.getClassName());
                    }
                    map.put(domClass, classConfig.getHostClass());
                }
            }

            domJavaScriptMap_ = map;
        }

        return domJavaScriptMap_.get(clazz);
    }

    protected ClassConfiguration getScopeConfiguration() {
        return scopeConfiguration_;
    }
}

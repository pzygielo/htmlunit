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

import static org.htmlunit.BrowserVersion.FIREFOX;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.RandomStringGenerator;
import org.htmlunit.BrowserVersion;
import org.htmlunit.MockWebConnection;
import org.htmlunit.WebClient;
import org.htmlunit.WebTestCase;
import org.htmlunit.javascript.HtmlUnitScriptable;
import org.htmlunit.javascript.JavaScriptEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JavaScriptConfiguration}.
 *
 * @author Chris Erskine
 * @author Ahmed Ashour
 * @author Ronald Brill
 * @author Frank Danek
 * @author Joerg Werner
 */
public class JavaScriptConfigurationTest {

    private static final Log LOG = LogFactory.getLog(JavaScriptConfigurationTest.class);

    /**
     * Test if configuration map expands with each new instance of BrowserVersion used.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void configurationMapExpands() throws Exception {
        // get a reference to the leaky map
        final Field field = JavaScriptConfiguration.class.getDeclaredField("CONFIGURATION_MAP_");
        field.setAccessible(true);
        final Map<?, ?> leakyMap = (Map<?, ?>) field.get(null);

        leakyMap.clear();
        final int knownBrowsers = leakyMap.size();

        BrowserVersion browserVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_ESR)
                                                .setApplicationVersion("App")
                                                .setApplicationVersion("Version")
                                                .setUserAgent("User agent")
                                                .build();
        JavaScriptConfiguration.getInstance(browserVersion);

        browserVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_ESR)
                            .setApplicationVersion("App2")
                            .setApplicationVersion("Version2")
                            .setUserAgent("User agent2")
                            .build();
        JavaScriptConfiguration.getInstance(browserVersion);

        assertEquals(knownBrowsers + 1, leakyMap.size());
    }

    /**
     * Regression test for Bug #899.
     * This test was throwing an OutOfMemoryError when the bug existed.
     * @throws Exception if an error occurs
     */
    @Test
    public void memoryLeak() throws Exception {
        final RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').get();

        long count = 0;
        while (count++ < 3000) {
            final BrowserVersion browserVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_ESR)
                                                    .setApplicationVersion("App" + generator.generate(20))
                                                    .setApplicationVersion("Version" + generator.generate(20))
                                                    .setUserAgent("User Agent" + generator.generate(20))
                                                    .build();
            JavaScriptConfiguration.getInstance(browserVersion);
            if (LOG.isInfoEnabled()) {
                LOG.info("count: " + count + "; memory stats: " + getMemoryStats());
            }
        }
        System.gc();
    }

    private static String getMemoryStats() {
        final Runtime rt = Runtime.getRuntime();
        final long free = rt.freeMemory() / 1024;
        final long total = rt.totalMemory() / 1024;
        final long max = rt.maxMemory() / 1024;
        final long used = total - free;
        final String format = "used: {0,number,0}K, free: {1,number,0}K, total: {2,number,0}K, max: {3,number,0}K";
        return MessageFormat.format(format,
                Long.valueOf(used), Long.valueOf(free), Long.valueOf(total), Long.valueOf(max));
    }

    /**
     * Tests that all classes in *.javascript.* which have {@link JsxClasses}/{@link JsxClass} annotation,
     * are included in {@link JavaScriptConfiguration#CLASSES_}.
     */
    @Test
    public void jsxClasses() {
        final List<String> foundJsxClasses = new ArrayList<>();
        for (final String className : getClassesForPackage(JavaScriptEngine.class)) {
            if (!className.contains("$")) {
                Class<?> klass = null;
                try {
                    klass = Class.forName(className);
                }
                catch (final Throwable e) {
                    continue;
                }
                if ("org.htmlunit.javascript.host.intl".equals(klass.getPackage().getName())

                        // Worker
                        || "WorkerGlobalScope".equals(klass.getSimpleName())
                        || "DedicatedWorkerGlobalScope".equals(klass.getSimpleName())
                        || "WorkerLocation".equals(klass.getSimpleName())
                        || "WorkerNavigator".equals(klass.getSimpleName())

                        // ProxyConfig
                        || "ProxyAutoConfig".equals(klass.getSimpleName())) {
                    continue;
                }
                if (klass.getAnnotation(JsxClasses.class) != null) {
                    foundJsxClasses.add(className);
                }
                else if (klass.getAnnotation(JsxClass.class) != null) {
                    foundJsxClasses.add(className);
                }
            }
        }

        final List<String> definedClasses = new ArrayList<>();
        for (final Class<?> klass : JavaScriptConfiguration.CLASSES_) {
            definedClasses.add(klass.getName());
        }
        foundJsxClasses.removeAll(definedClasses);
        if (!foundJsxClasses.isEmpty()) {
            Assertions.fail("Class " + foundJsxClasses.get(0) + " is not in JavaScriptConfiguration.CLASSES_");
        }
    }

    /**
     * Return the classes inside the specified package and its sub-packages.
     * @param klass a class inside that package
     * @return a list of class names
     */
    public static List<String> getClassesForPackage(final Class<?> klass) {
        final List<String> list = new ArrayList<>();

        File directory = null;
        final String relPath = klass.getName().replace('.', '/') + ".class";

        final URL resource = JavaScriptConfiguration.class.getClassLoader().getResource(relPath);

        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
        final String fullPath = resource.getFile();

        try {
            directory = new File(resource.toURI()).getParentFile();
        }
        catch (final URISyntaxException e) {
            throw new RuntimeException(klass.getName() + " (" + resource + ") does not appear to be a valid URL", e);
        }
        catch (final IllegalArgumentException e) {
            directory = null;
        }

        if (directory != null && directory.exists()) {
            addClasses(directory, klass.getPackage().getName(), list);
        }
        else {
            try {
                String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
                if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
                    jarPath = jarPath.replace("%20", " ");
                }
                try (JarFile jarFile = new JarFile(jarPath)) {
                    for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                        final String entryName = entries.nextElement().getName();
                        if (entryName.endsWith(".class")) {
                            list.add(entryName.replace('/', '.').replace('\\', '.').replace(".class", ""));
                        }
                    }
                }
            }
            catch (final IOException e) {
                throw new RuntimeException(klass.getPackage().getName() + " does not appear to be a valid package", e);
            }
        }
        return list;
    }

    private static void addClasses(final File directory, final String packageName, final List<String> list) {
        final File[] files = directory.listFiles();
        if (files != null) {
            for (final File file : files) {
                final String name = file.getName();
                if (name.endsWith(".class")) {
                    list.add(packageName + '.' + name.substring(0, name.length() - 6));
                }
                else if (file.isDirectory() && !".git".equals(file.getName())) {
                    addClasses(file, packageName + '.' + file.getName(), list);
                }
            }
        }
    }

    /**
     * Tests that all classes included in {@link JavaScriptConfiguration#CLASSES_} defining an
     * {@link JsxClasses}/{@link JsxClass} annotation for at least one browser.
     */
    @Test
    public void obsoleteJsxClasses() {
        final JavaScriptConfiguration config = JavaScriptConfiguration.getInstance(FIREFOX);

        for (final Class<? extends HtmlUnitScriptable> klass : config.getClasses()) {
            boolean found = false;
            for (final BrowserVersion browser : BrowserVersion.ALL_SUPPORTED_BROWSERS) {
                if (AbstractJavaScriptConfiguration.getClassConfiguration(klass, browser) != null) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found, "Class " + klass
                    + " is member of JavaScriptConfiguration.CLASSES_ but does not define @JsxClasses/@JsxClass");
        }
    }

    /**
     * Test of order.
     */
    @Test
    public void treeOrder() {
        final List<String> defined = new ArrayList<>(JavaScriptConfiguration.CLASSES_.length);

        final HashMap<Integer, List<String>> levels = new HashMap<>();
        for (final Class<?> c : JavaScriptConfiguration.CLASSES_) {
            defined.add(c.getSimpleName());

            int level = 1;
            Class<?> parent = c.getSuperclass();
            while (parent != HtmlUnitScriptable.class) {
                level++;
                parent = parent.getSuperclass();
            }

            List<String> clsAtLevel = levels.get(level);
            if (clsAtLevel == null) {
                clsAtLevel = new ArrayList<>();
                levels.put(level, clsAtLevel);
            }
            clsAtLevel.add(c.getSimpleName());
        }

        final List<String> all = new ArrayList<>(JavaScriptConfiguration.CLASSES_.length);
        for (int level = 1; level <= levels.size(); level++) {
            final List<String> clsAtLevel = levels.get(level);
            Collections.sort(clsAtLevel);
            all.addAll(clsAtLevel);

            // dump
            /*
            final String indent = "       ";
            System.out.println(indent + " // level " + level);

            System.out.print(indent);
            int chars = indent.length();
            for (final String cls : clsAtLevel) {
                final String toPrint = " " + cls + ".class,";
                chars += toPrint.length();
                if (chars > 120) {
                    System.out.println();
                    System.out.print(indent);
                    chars = indent.length() + toPrint.length();
                }
                System.out.print(toPrint);
            }
            System.out.println();
            */
        }
        assertEquals(all, defined);
    }

    /**
     * See issue 1890.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void original() throws Exception {
        final BrowserVersion browserVersion = BrowserVersion.CHROME;

        test(browserVersion);
    }

    /**
     * See issue 1890.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void cloned() throws Exception {
        final BrowserVersion browserVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX)
                                                    .build();

        test(browserVersion);
    }

    /**
     * See issue 1890.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void clonedAndModified() throws Exception {
        final BrowserVersion browserVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX)
                                                    .setUserAgent("foo")
                                                    .build();

        test(browserVersion);
    }

    private static void test(final BrowserVersion browserVersion) throws IOException {
        try (WebClient webClient = new WebClient(browserVersion)) {
            final MockWebConnection conn = new MockWebConnection();
            conn.setDefaultResponse(WebTestCase.DOCTYPE_HTML
                    + "<html><body onload='document.body.firstChild'></body></html>");
            webClient.setWebConnection(conn);

            webClient.getPage("http://localhost/");
        }
    }

}

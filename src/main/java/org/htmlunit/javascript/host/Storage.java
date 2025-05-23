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
package org.htmlunit.javascript.host;

import static org.htmlunit.BrowserVersionFeatures.JS_STORAGE_PRESERVED_INCLUDED;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.htmlunit.corejs.javascript.Scriptable;
import org.htmlunit.javascript.HtmlUnitScriptable;
import org.htmlunit.javascript.JavaScriptEngine;
import org.htmlunit.javascript.configuration.JsxClass;
import org.htmlunit.javascript.configuration.JsxConstructor;
import org.htmlunit.javascript.configuration.JsxFunction;
import org.htmlunit.javascript.configuration.JsxGetter;
import org.w3c.dom.DOMException;

/**
 * The JavaScript object that represents a Storage.
 *
 * @author Ahmed Ashour
 * @author Marc Guillemot
 * @author Ronald Brill
 */
@JsxClass
public class Storage extends HtmlUnitScriptable {

    private static final HashSet<String> RESERVED_NAMES_ = new HashSet<>(Arrays.asList(
        "clear", "key", "getItem", "length", "removeItem",
        "setItem", "constructor", "toString", "toLocaleString", "valueOf", "hasOwnProperty", "propertyIsEnumerable",
        "isPrototypeOf", "__defineGetter__", "__defineSetter__", "__lookupGetter__", "__lookupSetter__"));

    private static final long STORE_SIZE_KIMIT = 5_200_000;

    private final Map<String, String> store_;
    private long storeSize_;

    /**
     * Public default constructor only for the prototype.
     */
    public Storage() {
        super();
        store_ = null;
    }

    /**
     * JavaScript constructor.
     */
    @JsxConstructor
    public void jsConstructor() {
        // nothing to do
    }

    /**
     * Constructor.
     * @param window the parent scope
     * @param store the storage itself
     */
    public Storage(final Window window, final Map<String, String> store) {
        super();
        store_ = store;
        storeSize_ = 0L;
        setParentScope(window);
        setPrototype(window.getPrototype(Storage.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String name, final Scriptable start, final Object value) {
        final boolean isReserved = RESERVED_NAMES_.contains(name);
        if (store_ == null || isReserved) {
            super.put(name, start, value);
        }
        if (store_ != null && (!isReserved || getBrowserVersion().hasFeature(JS_STORAGE_PRESERVED_INCLUDED))) {
            setItem(name, JavaScriptEngine.toString(value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final String name, final Scriptable start) {
        if (store_ == null || RESERVED_NAMES_.contains(name)) {
            return super.get(name, start);
        }
        final Object value = getItem(name);
        if (value != null) {
            return value;
        }
        return super.get(name, start);
    }

    /**
     * Returns the length property.
     * @return the length property
     */
    @JsxGetter
    public int getLength() {
        return store_.size();
    }

    /**
     * Removes the specified key.
     * @param key the item key
     */
    @JsxFunction
    public void removeItem(final String key) {
        final String removed = store_.remove(key);
        if (removed != null) {
            storeSize_ -= removed.length();
        }
    }

    /**
     * Returns the key of the specified index.
     * @param index the index
     * @return the key
     */
    @JsxFunction
    public String key(final int index) {
        if (index >= 0) {
            int counter = 0;
            for (final String key : store_.keySet()) {
                if (counter == index) {
                    return key;
                }
                counter++;
            }
        }
        return null;
    }

    /**
     * Returns the value of the specified key.
     * @param key the item key
     * @return the value
     */
    @JsxFunction
    public Object getItem(final String key) {
        return store_.get(key);
    }

    /**
     * Sets the item value.
     * @param key the item key
     * @param data the value
     */
    @JsxFunction
    public void setItem(final String key, final String data) {
        final long storeSize = storeSize_ + data.length();
        if (storeSize > STORE_SIZE_KIMIT) {
            throw JavaScriptEngine.throwAsScriptRuntimeEx(
                    new DOMException((short) 22, "QuotaExceededError: Failed to execute 'setItem' on 'Storage': "
                            + "Setting the value of '" + key + "' exceeded the quota."));
        }
        storeSize_ = storeSize;
        store_.put(key, data);
    }

    /**
     * Clears all items.
     */
    @JsxFunction
    public void clear() {
        store_.clear();
        storeSize_ = 0;
    }
}

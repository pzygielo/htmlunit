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
package org.htmlunit.javascript.host.html;

import static org.htmlunit.BrowserVersionFeatures.HTMLCOLLECTION_NAMED_ITEM_ID_FIRST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.htmlunit.BrowserVersion;
import org.htmlunit.corejs.javascript.Scriptable;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.javascript.JavaScriptEngine;
import org.htmlunit.javascript.configuration.JsxClass;
import org.htmlunit.javascript.configuration.JsxConstructor;
import org.htmlunit.javascript.configuration.JsxFunction;
import org.htmlunit.javascript.configuration.JsxGetter;
import org.htmlunit.javascript.configuration.JsxSymbol;
import org.htmlunit.javascript.host.dom.AbstractList;

/**
 * An array of elements. Used for the element arrays returned by <code>document.all</code>,
 * <code>document.all.tags('x')</code>, <code>document.forms</code>, <code>window.frames</code>, etc.
 * Note that this class must not be used for collections that can be modified, for example
 * <code>map.areas</code> and <code>select.options</code>.
 * <br>
 * This class (like all classes in this package) is specific for the JavaScript engine.
 * Users of HtmlUnit shouldn't use it directly.
 *
 * @author Daniel Gredler
 * @author Marc Guillemot
 * @author Chris Erskine
 * @author Ahmed Ashour
 * @author Frank Danek
 * @author Ronald Brill
 */
@JsxClass
public class HTMLCollection extends AbstractList {

    /**
     * Creates an instance.
     */
    public HTMLCollection() {
        super();
    }

    /**
     * JavaScript constructor.
     */
    @JsxConstructor
    public void jsConstructor() {
        // nothing to do
    }

    /**
     * Creates an instance.
     * @param domNode parent scope
     * @param attributeChangeSensitive indicates if the content of the collection may change when an attribute
     *        of a descendant node of parentScope changes (attribute added, modified or removed)
     */
    public HTMLCollection(final DomNode domNode, final boolean attributeChangeSensitive) {
        super(domNode, attributeChangeSensitive, null);
    }

    /**
     * Constructs an instance with an initial cache value.
     * @param domNode the parent scope, on which we listen for changes
     * @param initialElements the initial content for the cache
     */
    HTMLCollection(final DomNode domNode, final List<DomNode> initialElements) {
        super(domNode, true, new ArrayList<>(initialElements));
    }

    private HTMLCollection(final DomNode domNode, final boolean attributeChangeSensitive,
            final List<DomNode> initialElements) {
        super(domNode, attributeChangeSensitive, new ArrayList<>(initialElements));
    }

    /**
     * Gets an empty collection.
     * @param domNode the DOM node
     * @return an empty collection
     */
    public static HTMLCollection emptyCollection(final DomNode domNode) {
        return new HTMLCollection(domNode, false, Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HTMLCollection create(final DomNode parentScope, final List<DomNode> initialElements) {
        return new HTMLCollection(parentScope, initialElements);
    }

    /**
     * @return the Iterator symbol
     */
    @JsxSymbol
    public Scriptable iterator() {
        return JavaScriptEngine.newArrayIteratorTypeValues(getParentScope(), this);
    }

    /**
     * Returns the length.
     * @return the length
     */
    @JsxGetter
    @Override
    public final int getLength() {
        return super.getLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getWithPreemptionByName(final String name, final List<DomNode> elements) {
        final List<DomNode> matchingElements = new ArrayList<>();
        final boolean searchName = isGetWithPreemptionSearchName();
        for (final DomNode next : elements) {
            if (next instanceof DomElement
                    && (searchName || next instanceof HtmlInput || next instanceof HtmlForm)) {
                final String nodeName = ((DomElement) next).getAttributeDirect(DomElement.NAME_ATTRIBUTE);
                if (name.equals(nodeName)) {
                    matchingElements.add(next);
                }
            }
        }

        if (matchingElements.isEmpty()) {
            return NOT_FOUND;
        }

        if (matchingElements.size() == 1) {
            return getScriptableForElement(matchingElements.get(0));
        }

        // many elements => build a sub collection
        final DomNode domNode = getDomNodeOrNull();
        final HTMLCollection collection = new HTMLCollection(domNode, matchingElements);
        collection.setAvoidObjectDetection(true);
        return collection;
    }

    /**
     * Returns whether {@link #getWithPreemption(String)} should search by name or not.
     * @return whether {@link #getWithPreemption(String)} should search by name or not
     */
    protected boolean isGetWithPreemptionSearchName() {
        return true;
    }

    /**
     * Returns the item or items corresponding to the specified index or key.
     * @param index the index or key corresponding to the element or elements to return
     * @return the element or elements corresponding to the specified index or key
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms536460.aspx">MSDN doc</a>
     */
    @JsxFunction
    public Object item(final Object index) {
        int idx = 0;
        final double doubleValue = JavaScriptEngine.toNumber(index);
        if (!Double.isNaN(doubleValue)) {
            idx = (int) doubleValue;
        }

        final Object object = get(idx, this);
        if (object == NOT_FOUND) {
            return null;
        }
        return object;
    }

    /**
     * Retrieves the item or items corresponding to the specified name (checks ids, and if
     * that does not work, then names).
     * @param name the name or id the element or elements to return
     * @return the element or elements corresponding to the specified name or id
     * @see <a href="http://msdn.microsoft.com/en-us/library/ms536634.aspx">MSDN doc</a>
     */
    @JsxFunction
    public Scriptable namedItem(final String name) {
        final List<DomNode> elements = getElements();
        final BrowserVersion browserVersion = getBrowserVersion();
        if (browserVersion.hasFeature(HTMLCOLLECTION_NAMED_ITEM_ID_FIRST)) {
            for (final Object next : elements) {
                if (next instanceof DomElement) {
                    final DomElement elem = (DomElement) next;
                    final String id = elem.getId();
                    if (name.equals(id)) {
                        return getScriptableForElement(elem);
                    }
                }
            }
        }
        for (final Object next : elements) {
            if (next instanceof DomElement) {
                final DomElement elem = (DomElement) next;
                final String nodeName = elem.getAttributeDirect(DomElement.NAME_ATTRIBUTE);
                if (name.equals(nodeName)) {
                    return getScriptableForElement(elem);
                }

                final String id = elem.getId();
                if (name.equals(id)) {
                    return getScriptableForElement(elem);
                }
            }
        }
        return null;
    }
}

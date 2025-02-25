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
package org.htmlunit.javascript.host.css;

import static org.htmlunit.BrowserVersionFeatures.JS_CSSRULELIST_ENUM_ITEM_LENGTH;

import java.util.ArrayList;
import java.util.List;

import org.htmlunit.corejs.javascript.Scriptable;
import org.htmlunit.javascript.HtmlUnitScriptable;
import org.htmlunit.javascript.JavaScriptEngine;
import org.htmlunit.javascript.configuration.JsxClass;
import org.htmlunit.javascript.configuration.JsxConstructor;
import org.htmlunit.javascript.configuration.JsxFunction;
import org.htmlunit.javascript.configuration.JsxGetter;

/**
 * A JavaScript object for {@code CSSRuleList}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 * @author Frank Danek
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/CSSRuleList">MDN doc</a>
 */
@JsxClass
public class CSSRuleList extends HtmlUnitScriptable {

    private final List<CSSRule> rules_ = new ArrayList<>();

    /**
     * Creates a new instance.
     */
    public CSSRuleList() {
        super();
    }

    /**
     * Creates an instance.
     */
    @JsxConstructor
    public void jsConstructor() {
        throw JavaScriptEngine.typeErrorIllegalConstructor();
    }

    /**
     * Creates a new instance.
     * @param stylesheet the stylesheet
     */
    public CSSRuleList(final CSSStyleSheet stylesheet) {
        super();
        setParentScope(stylesheet.getParentScope());
        setPrototype(getPrototype(getClass()));
    }

    /**
     * Creates a new instance.
     * @param groupingRule the grouping rule
     */
    public CSSRuleList(final CSSGroupingRule groupingRule) {
        super();
        setParentScope(groupingRule.getParentScope());
        setPrototype(getPrototype(getClass()));
    }

    /**
     * Add a rule.
     * @param rule the rule to add
     */
    protected void addRule(final CSSRule rule) {
        rules_.add(rule);
    }

    /**
     * Clear the listOfRules.
     */
    protected void clearRules() {
        rules_.clear();
    }

    /**
     * Returns the length of this list.
     * @return the length of this list.
     */
    @JsxGetter
    public int getLength() {
        return rules_.size();
    }

    /**
     * Returns the item in the given index.
     * @param index the index
     * @return the item in the given index
     */
    @JsxFunction
    public Object item(final int index) {
        final Object item = get(index, this);
        if (NOT_FOUND == item) {
            return null;
        }
        return item;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object[] getIds() {
        final List<String> idList = new ArrayList<>();

        final int length = getLength();
        for (int i = 0; i < length; i++) {
            idList.add(Integer.toString(i));
        }

        if (getBrowserVersion().hasFeature(JS_CSSRULELIST_ENUM_ITEM_LENGTH)) {
            idList.add("item");
            idList.add("length");
        }
        else {
            idList.add("length");
            idList.add("item");
        }
        return idList.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(final int index, final Scriptable start) {
        return index >= 0 && index < getLength();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean has(final String name, final Scriptable start) {
        if ("length".equals(name) || "item".equals(name)) {
            return true;
        }
        try {
            return has(Integer.parseInt(name), start);
        }
        catch (final Exception ignored) {
            // ignore
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final int index, final Scriptable start) {
        if (index < 0 || getLength() <= index) {
            return NOT_FOUND;
        }
        return rules_.get(index);
    }

}

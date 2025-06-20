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

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.junit.annotation.HtmlUnitNYI;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CSSStyleRule}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 * @author Frank Danek
 */
public class CSSStyleRuleTest extends WebDriverTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("TypeError")
    public void ctor() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "try {\n"
            + "  var rule = new CSSStyleRule();\n"
            + "  log(rule);\n"
            + "} catch(e) { logEx(e); }\n"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(DEFAULT = {"[object CSSStyleRule]", "1", "[object CSSStyleSheet]", "null", "h1", "", "10px", "", "red"},
            FF = {"[object CSSStyleRule]", "1", "[object CSSStyleSheet]", "null", "H1", "", "10px", "", "red"},
            FF_ESR = {"[object CSSStyleRule]", "1", "[object CSSStyleSheet]", "null", "H1", "", "10px", "", "red"})
    public void test() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  P  { font: 10pt Arial; text-indent: 0.5in; }\n"
                + "  A  { text-decoration: none; color: blue; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "  function test() {\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[1];\n"
                + "    log(r);\n"
                + "    if (r.type) {\n"
                + "      log(r.type);\n"
                + "      log(r.parentStyleSheet);\n"
                + "      log(r.parentRule);\n"
                + "      log(r.selectorText);\n"
                + "    } else {\n"
                + "      log(r.selectorText);\n"
                + "    }\n"
                + "    log(r.style.marginTop);\n"
                + "    r.style.marginTop = '10px';\n"
                + "    log(r.style.marginTop);\n"
                + "    log(r.style.backgroundColor);\n"
                + "    r.style.backgroundColor = 'red';\n"
                + "    log(r.style.backgroundColor);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"4px", "4px", "4px", "4px"})
    public void styleSheet() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { margin: 4px; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "  function test() {\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[0];\n"
                + "    log(r.style.marginTop);\n"
                + "    log(r.style.marginRight);\n"
                + "    log(r.style.marginBottom);\n"
                + "    log(r.style.marginLeft);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void readOnly() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  P  { font: 10pt Arial; text-indent: 0.5in; }\n"
                + "  A  { text-decoration: none; color: blue; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "  function test() {\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[1];\n"
                + "    log(r.readOnly);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts("1")
    public void type() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  P  { font: 10pt Arial; text-indent: 0.5in; }\n"
                + "  A  { text-decoration: none; color: blue; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "  function test() {\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[1];\n"
                + "    log(r.type);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(DEFAULT = {"body", "h1", "a.foo", ".foo", ".foo .foo2", ".myFoo", "#byId"},
            FF = {"BoDY", "H1", "A.foo", ".foo", ".foo .foo2", ".myFoo", "#byId"},
            FF_ESR = {"BoDY", "H1", "A.foo", ".foo", ".foo .foo2", ".myFoo", "#byId"})
    public void selectorText() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BoDY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  A.foo  { color: blue; }\n"
                + "  .foo  { color: blue; }\n"
                + "  .foo .foo2 { font: 8pt; }\n"
                + "  .myFoo { font: 10pt; }\n"
                + "  #byId { font: 8pt; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "  function test() {\n"
                + "    var sheet = document.styleSheets[0];\n"
                + "    var rules = sheet.cssRules || sheet.rules;\n"
                + "    for (var i = 0; i < rules.length; i++)\n"
                + "      log(rules[i].selectorText);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"1", ""})
    @HtmlUnitNYI(CHROME = {"1",
                           "progid:DXImageTransform.Microsoft.AlphaImageLoader"
                                   + "(src=rightCorner.gif, sizingMethod=crop)"},
            EDGE = {"1",
                    "progid:DXImageTransform.Microsoft.AlphaImageLoader(src=rightCorner.gif, sizingMethod=crop)"},
            FF = {"1",
                  "progid:DXImageTransform.Microsoft.AlphaImageLoader(src=rightCorner.gif, sizingMethod=crop)"},
            FF_ESR = {"1",
                      "progid:DXImageTransform.Microsoft.AlphaImageLoader(src=rightCorner.gif, sizingMethod=crop)"})
    public void oldIEStyleFilter() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader"
                + "(src='rightCorner.gif', sizingMethod='crop'); }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "function test() {\n"
                + "  try {\n"
                + "    var sheet = document.styleSheets[0];\n"
                + "    var rules = sheet.cssRules || sheet.rules;\n"
                + "    log(rules.length);\n"
                + "    log(rules[0].style.filter);\n"
                + "  } catch(e) { logEx(e); }\n"
                + "}\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"1", "none"})
    public void filter() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "<style>\n"
                + "  BODY { filter: none; }\n"
                + "</style>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "function test() {\n"
                + "  try {\n"
                + "    var sheet = document.styleSheets[0];\n"
                + "    var rules = sheet.cssRules || sheet.rules;\n"
                + "    log(rules.length);\n"
                + "    log(rules[0].style.filter);\n"
                + "  } catch(e) { logEx(e); }\n"
                + "}\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }
}

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
package org.htmlunit.javascript.regexp.mozilla.js1_2;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.junit.jupiter.api.Test;

/**
 * Tests originally in '/js/src/tests/js1_2/regexp/alphanumeric.js'.
 *
 * @author Ahmed Ashour
 * @author Frank Danek
 * @author Ronald Brill
 */
public class AlphanumericTest extends WebDriverTestCase {

    private static final String NON_ALPHANUMERIC = "~`!@#$%^&*()-+={[}]|\\\\:;\\'<,>./?\\f\\n\\r\\t \"\\v";
    private static final String ALPHANUMERIC     = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    /**
     * Be sure all alphanumerics are matched by \w.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(ALPHANUMERIC)
    public void test1() throws Exception {
        test("'" + ALPHANUMERIC + "'.match(new RegExp('\\\\w+'))", false);
    }

    /**
     * Be sure all non-alphanumerics are matched by \W.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("7E-60-21-40-23-24-25-5E-26-2A-28-29-2D-2B-3D-7B-5B-7D-5D-7C-5C-"
                + "3A-3B-27-3C-2C-3E-2E-2F-3F-C-A-D-9-20-22-B-")
    public void test2() throws Exception {
        test("'" + NON_ALPHANUMERIC + "'.match(new RegExp('\\\\W+'))", true);
    }

    /**
     * Be sure all non-alphanumerics are not matched by \w.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("null")
    public void test3() throws Exception {
        test("'" + NON_ALPHANUMERIC + "'.match(new RegExp('\\\\w+'))", false);
    }

    /**
     * Be sure all alphanumerics are not matched by \W.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("null")
    public void test4() throws Exception {
        test("'" + ALPHANUMERIC + "'.match(new RegExp('\\\\W+'))", false);
    }

    /**
     * Be sure all alphanumerics are matched by \w.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(ALPHANUMERIC)
    public void test5() throws Exception {
        test("'" + NON_ALPHANUMERIC + ALPHANUMERIC + "'.match(new RegExp('\\\\w+'))", false);
    }

    /**
     * Be sure all non-alphanumerics are matched by \W.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("7E-60-21-40-23-24-25-5E-26-2A-28-29-2D-2B-3D-7B-5B-7D-5D-7C-5C-"
                + "3A-3B-27-3C-2C-3E-2E-2F-3F-C-A-D-9-20-22-B-")
    public void test6() throws Exception {
        test("'" + ALPHANUMERIC + NON_ALPHANUMERIC + "'.match(new RegExp('\\\\W+'))", true);
    }

    /**
     * Be sure all alphanumerics are matched by \w (using literals).
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(ALPHANUMERIC)
    public void test7() throws Exception {
        test("'" + ALPHANUMERIC + NON_ALPHANUMERIC + "'.match(/\\w+/)", false);
    }

    /**
     * Be sure all non-alphanumerics are matched by \W (using literals).
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("7E-60-21-40-23-24-25-5E-26-2A-28-29-2D-2B-3D-7B-5B-7D-5D-7C-5C-"
                + "3A-3B-27-3C-2C-3E-2E-2F-3F-C-A-D-9-20-22-B-")
    public void test8() throws Exception {
        test("'" + ALPHANUMERIC + NON_ALPHANUMERIC + "'.match(/\\W+/)", true);
    }

    /**
     * Be sure the following test behaves consistently.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("abcd*&^%$$,abcd,%$$")
    public void test9() throws Exception {
        test("'" + "abcd*&^%$$" + "'.match(/(\\w+)...(\\W+)/)", false);
    }

    /**
     * Be sure all alphanumeric characters match individually.
     * @throws Exception if the test fails
     */
    @Test
    public void test10() throws Exception {
        for (int i = 0; i < ALPHANUMERIC.length(); i++) {
            setExpectedAlerts(String.valueOf(ALPHANUMERIC.charAt(i)));
            test("'" + "#$" + ALPHANUMERIC.charAt(i) + "%^" + "'.match(new RegExp('\\\\w'))", false);
        }
    }

    /**
     * Be sure all non_alphanumeric characters match individually.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("\n")
    public void test11() throws Exception {
        final String[] exp = getExpectedAlerts();
        for (int i = 0; i < NON_ALPHANUMERIC.length() - 1; i++) {
            final char ch = NON_ALPHANUMERIC.charAt(i);
            String expected = String.valueOf(ch);
            String input = expected;
            switch (ch) {
                case '\\':
                    input = "\\" + ch;
                    break;

                case '\'':
                    input = "\\" + ch;
                    break;

                case 'f':
                    expected = "\f";
                    input = "\\" + ch;
                    break;

                case 'n':
                    expected = "\n";
                    input = "\\" + ch;
                    break;

                case 'r':
                    expected = exp[0];
                    input = "\\" + ch;
                    break;

                case 't':
                    expected = "\t";
                    input = "\\" + ch;
                    break;

                case 'v':
                    expected = "\u000B";
                    input = "\\" + ch;
                    break;

                default:
            }

            setExpectedAlerts(expected);

            final String s = "sd" + input + String.valueOf((i + 10) * (i + 10) - 2 * (i + 10));
            test("'" + s + "'.match(new RegExp('\\\\W'))", false);
        }
    }

    private void test(final String script, final boolean charCode) throws Exception {
        String html
            = "<html><head><script>\n";
        if (charCode) {
            html += "  var string = " + script + ".toString();\n"
                + "  var output = '';\n"
                + "  for (var i = 0; i < string.length; i++) {\n"
                + "    output += string.charCodeAt(i).toString(16).toUpperCase() + '-';\n"
                + "  }\n"
                + "  alert(output);\n";
        }
        else {
            html += "  alert(" + script + ");\n";
        }
        html += "</script></head><body>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }
}

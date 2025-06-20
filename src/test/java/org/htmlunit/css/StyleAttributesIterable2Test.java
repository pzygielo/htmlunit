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
package org.htmlunit.css;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.javascript.host.css.CSSStyleDeclaration;
import org.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import org.htmlunit.junit.annotation.Alerts;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link StyleAttributes}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
public class StyleAttributesIterable2Test extends WebDriverTestCase {

    /**
     * Some properties exist only in {@link CSSStyleDeclaration}, not in {@link ComputedCSSStyleDeclaration}.
     * Seems Rhino checks for prototype, so they will always be available, even for {@link ComputedCSSStyleDeclaration}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("done")
    public void notInComputed() throws Exception {
        styleVsComputed("pixelBottom");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"in style", "in computed style", "done"})
    public void inComputed() throws Exception {
        styleVsComputed("wordBreak");
    }

    private void styleVsComputed(final String property) throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var e = document.getElementById('myDiv');\n"
            + "    for (var i in e.style) {\n"
            + "      if (i == '" + property + "') {\n"
            + "        log('in style');\n"
            + "      }\n"
            + "    }\n"
            + "    for (var i in window.getComputedStyle(e, null)) {\n"
            + "      if (i == '" + property + "') {\n"
            + "        log('in computed style');\n"
            + "      }\n"
            + "    }\n"
            + "    log('done');\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "  <div id='myDiv'></div>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }
}

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

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HTMLProgressElement}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
public class HTMLProgressElementTest extends WebDriverTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0", "2", "1", "2", "1", "1"})
    public void labels() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "  <script>\n"
            + LOG_TITLE_FUNCTION
            + "    function test() {\n"
            + "      debug(document.getElementById('e1'));\n"
            + "      debug(document.getElementById('e2'));\n"
            + "      debug(document.getElementById('e3'));\n"
            + "      debug(document.getElementById('e4'));\n"
            + "      var labels = document.getElementById('e4').labels;\n"
            + "      document.body.removeChild(document.getElementById('l4'));\n"
            + "      debug(document.getElementById('e4'));\n"
            + "      log(labels ? labels.length : labels);\n"
            + "    }\n"
            + "    function debug(e) {\n"
            + "      log(e.labels ? e.labels.length : e.labels);\n"
            + "    }\n"
            + "  </script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "  <progress id='e1'>e 1</progress><br>\n"
            + "  <label>something <label> click here <progress id='e2'>e 2</progress></label></label><br>\n"
            + "  <label for='e3'> and here</label>\n"
            + "  <progress id='e3'>e 3</progress><br>\n"
            + "  <label id='l4' for='e4'> what about</label>\n"
            + "  <label> this<progress id='e4'>e 4</progress></label><br>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0", "number"})
    public void value() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "  <script>\n"
            + LOG_TITLE_FUNCTION
            + "    function test() {\n"
            + "      var e1 = document.getElementById('e1');\n"
            + "      log(e1.value);\n"
            + "      log(typeof e1.value);\n"
            + "    }\n"
            + "  </script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "  <progress id='e1'>e 1</progress>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }
}

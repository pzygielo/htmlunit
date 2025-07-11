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
package org.htmlunit.html;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Tests for elements inside {@link HtmlNoScript}.
 *
 * @author Ahmed Ashour
 * @author Marc Guillemot
 * @author Frank Danek
 * @author Ronald Brill
 */
public class HtmlNoScriptTest extends WebDriverTestCase {

    /**
     * Verifies getVisibleText().
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("")
    public void getVisibleText() throws Exception {
        final String htmlContent = DOCTYPE_HTML
            + "<html>\n"
            + "<head></head>\n"
            + "<body>\n"
            + "  <noscript id='tester'>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(htmlContent);
        final String text = driver.findElement(By.id("tester")).getText();
        assertEquals(getExpectedAlerts()[0], text);

        if (driver instanceof HtmlUnitDriver) {
            final HtmlPage page = (HtmlPage) getEnclosedPage();
            assertEquals(getExpectedAlerts()[0], page.getBody().getVisibleText());
        }
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("null")
    public void getElementById() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.getElementById('second'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <input type='text' id='first' name='textfield'/>\n"
            + "  <noscript>\n"
            + "  <input type='text' id='second' name='button'/>\n"
            + "  </noscript>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"1", "[object Text]"})
    public void childNodes() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var noscript = document.getElementById('myDiv').childNodes.item(0);\n"
            + "    log(noscript.childNodes.length);\n"
            + "    log(noscript.firstChild);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <div id='myDiv'><noscript>\n"
            + "    <input type='text' name='button'/>\n"
            + "  </noscript></div>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("1")
    public void testJavaScript() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + "  alert(1);\n"
            + "</script>\n"
            + "<noscript>\n"
            + "  <script>\n"
            + "    alert(2);\n"
            + "  </script>\n"
            + "</noscript>\n"
            + "</head><body>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void formValues() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<form name='item' method='get'>\n"
            + "  <noscript>\n"
            + "    <input type=hidden name='__webpage_no_js__' value='1'>\n"
            + "  </noscript>\n"
            + "  <input type=hidden name='myParam' value='myValue'>\n"
            + "  <input type='submit' id='clickMe'>\n"
            + "</form>\n"
            + "</body></html>";

        final WebDriver webDriver = loadPage2(html);
        webDriver.findElement(By.id("clickMe")).click();

        assertFalse(webDriver.getCurrentUrl().contains("__webpage_no_js__"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("Has Script")
    public void jsDetection() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<noscript>\n"
            + "  <meta http-equiv='refresh' content='0;url=" + URL_SECOND + "'>\n"
            + "</noscript>\n"
            + "<title>start</title>\n"
            + "</head>\n"
            + "<body onload='document.form.submit()'>\n"
            + "<form name='form' action='" + URL_THIRD + "'></form>\n"
            + "</body>\n"
            + "</html>";

        final String htmlNoScript = DOCTYPE_HTML
            + "<html>\n"
            + "<head><title>No Script\u00A7</title></head>\n"
            + "<body></body>\n"
            + "</html>";
        getMockWebConnection().setResponse(URL_SECOND, htmlNoScript);

        final String htmlHasScript = DOCTYPE_HTML
                + "<html>\n"
                + "<head><title>Has Script\u00A7</title></head>\n"
                + "<body></body>\n"
                + "</html>";
        getMockWebConnection().setResponse(URL_THIRD, htmlHasScript);

        loadPage2(html);
        verifyTitle2(DEFAULT_WAIT_TIME, getWebDriver(), getExpectedAlerts());
    }
}

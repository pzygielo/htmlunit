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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Tests for {@link HtmlSearchInput}.
 *
 * @author Marc Guillemot
 * @author Anton Demydenko
 * @author Ronald Brill
 */
public class HtmlSearchInputTest extends WebDriverTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void type() throws Exception {
        final String html = DOCTYPE_HTML + "<html><head></head><body><input id='t' type='search'/></body></html>";

        final WebDriver webDriver = loadPage2(html);
        final WebElement input = webDriver.findElement(By.id("t"));

        input.sendKeys("abc");
        assertNull(input.getDomAttribute("value"));
        assertEquals("abc", input.getDomProperty("value"));

        input.sendKeys(Keys.BACK_SPACE);
        assertNull(input.getDomAttribute("value"));
        assertEquals("ab", input.getDomProperty("value"));

        input.sendKeys(Keys.BACK_SPACE);
        assertNull(input.getDomAttribute("value"));
        assertEquals("a", input.getDomProperty("value"));

        input.sendKeys(Keys.BACK_SPACE);
        assertNull(input.getDomAttribute("value"));
        assertEquals("", input.getDomProperty("value"));

        input.sendKeys(Keys.BACK_SPACE);
        assertNull(input.getDomAttribute("value"));
        assertEquals("", input.getDomProperty("value"));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"--null", "--null", "--null"})
    public void defaultValuesAfterClone() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var input = document.getElementById('text1');\n"
            + "    input = input.cloneNode(false);\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"

            + "    try {\n"
            + "      input = document.createElement('input');\n"
            + "      input.type = 'search';\n"
            + "      input = input.cloneNode(false);\n"
            + "      log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"
            + "    } catch(e)  { logEx(e); }\n"

            + "    var builder = document.createElement('div');\n"
            + "    builder.innerHTML = '<input type=\"search\">';\n"
            + "    input = builder.firstChild;\n"
            + "    input = input.cloneNode(false);\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='search' id='text1'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

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
            + "<form id='form1'>\n"
            + "  <input type='search' name='tester' id='tester' value='1234567' >\n"
            + "</form>\n"
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
     * Verifies clear().
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"1234567", ""})
    public void clearInput() throws Exception {
        final String htmlContent = DOCTYPE_HTML
                + "<html>\n"
                + "<head></head>\n"
                + "<body>\n"
                + "<form id='form1'>\n"
                + "  <input type='search' name='tester' id='tester' value='1234567'>\n"
                + "</form>\n"
                + "</body></html>";

        final WebDriver driver = loadPage2(htmlContent);
        final WebElement element = driver.findElement(By.id("tester"));

        assertEquals(getExpectedAlerts()[0], element.getDomAttribute("value"));
        assertEquals(getExpectedAlerts()[0], element.getDomProperty("value"));

        element.clear();
        assertEquals(getExpectedAlerts()[0], element.getDomAttribute("value"));
        assertEquals(getExpectedAlerts()[1], element.getDomProperty("value"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void typing() throws Exception {
        final String htmlContent = DOCTYPE_HTML
            + "<html><head><title>foo</title></head><body>\n"
            + "<form id='form1'>\n"
            + "  <input type='search' id='foo'>\n"
            + "</form></body></html>";

        final WebDriver driver = loadPage2(htmlContent);

        final WebElement input = driver.findElement(By.id("foo"));

        input.sendKeys("hello");
        assertNull(input.getDomAttribute("value"));
        assertEquals("hello", input.getDomProperty("value"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("--")
    public void minMaxStep() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var input = document.getElementById('tester');\n"
            + "    log(input.min + '-' + input.max + '-' + input.step);\n"
            + "  }\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='search' id='tester'>\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0987654321!",
             "0987654321!",
             "false",
             "false-false-true-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void patternValidationInvalid() throws Exception {
        validation("<input type='search' pattern='[0-9a-zA-Z]{10,40}' id='e1' value='0987654321!' name='k'>\n",
                    "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"68746d6c756e69742072756c657a21",
             "68746d6c756e69742072756c657a21",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=68746d6c756e69742072756c657a21", "2"})
    public void patternValidationValid() throws Exception {
        validation("<input type='search' pattern='[0-9a-zA-Z]{10,40}' "
                + "id='e1' value='68746d6c756e69742072756c657a21' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"",
             "",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=", "2"})
    public void patternValidationEmpty() throws Exception {
        validation("<input type='search' pattern='[0-9a-zA-Z]{10,40}' id='e1' value='' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({" ",
             " ",
             "false",
             "false-false-true-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void patternValidationBlank() throws Exception {
        validation("<input type='search' pattern='[0-9a-zA-Z]{10,40}' id='e1' value=' ' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"  \t",
             "  \t",
             "false",
             "false-false-true-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void patternValidationWhitespace() throws Exception {
        validation("<input type='search' pattern='[0-9a-zA-Z]{10,40}' id='e1' value='  \t' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({" 210 ",
             " 210 ",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=+210+", "2"})
    public void patternValidationTrimInitial() throws Exception {
        validation("<input type='search' pattern='[ 012]{3,10}' id='e1' name='k' value=' 210 '>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             " 210 ",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=+210+", "2"})
    public void patternValidationTrimType() throws Exception {
        validation("<input type='search' pattern='[ 012]{3,10}' id='e1' name='k'>\n", "", " 210 ");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "abcd",
             "false",
             "false-false-false-false-false-false-false-true-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void minLengthValidationInvalid() throws Exception {
        validation("<input type='search' minlength='5' id='e1' name='k'>\n", "", "abcd");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"1234",
             "1234",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=1234", "2"})
    public void minLengthValidationInvalidInitial() throws Exception {
        validation("<input type='search' minlength='20' id='e1' name='k' value='1234'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=", "2"})
    public void minLengthValidationInvalidNoInitial() throws Exception {
        validation("<input type='search' minlength='5' id='e1' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "abcdefghi",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=abcdefghi", "2"})
    public void minLengthValidationValid() throws Exception {
        validation("<input type='search' minlength='5' id='e1' name='k'>\n", "", "abcdefghi");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "abcd",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=abcd", "2"})
    public void maxLengthValidationValid() throws Exception {
        validation("<input type='search' maxlength='5' id='e1' name='k'>\n", "", "abcd");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "abcde",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=abcde", "2"})
    public void maxLengthValidationInvalid() throws Exception {
        validation("<input type='search' maxlength='5' id='e1' name='k'>\n", "", "abcdefghi");
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"abcdefghi",
             "abcdefghi",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=abcdefghi", "2"})
    public void maxLengthValidationInvalidInitial() throws Exception {
        validation("<input type='search' maxlength='5' id='e1' value='abcdefghi' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"true", "false", "true", "false", "true"})
    public void willValidate() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function test() {\n"
                + "      log(document.getElementById('o1').willValidate);\n"
                + "      log(document.getElementById('o2').willValidate);\n"
                + "      log(document.getElementById('o3').willValidate);\n"
                + "      log(document.getElementById('o4').willValidate);\n"
                + "      log(document.getElementById('o5').willValidate);\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "  <form>\n"
                + "    <input type='search' id='o1'>\n"
                + "    <input type='search' id='o2' disabled>\n"
                + "    <input type='search' id='o3' hidden>\n"
                + "    <input type='search' id='o4' readonly>\n"
                + "    <input type='search' id='o5' style='display: none'>\n"
                + "  </form>\n"
                + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=", "2"})
    public void validationEmpty() throws Exception {
        validation("<input type='search' id='e1' name='k'>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "false",
             "false-true-false-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void validationCustomValidity() throws Exception {
        validation("<input type='search' id='e1' name='k'>\n", "elem.setCustomValidity('Invalid');", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "false",
             "false-true-false-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void validationBlankCustomValidity() throws Exception {
        validation("<input type='search' id='e1' name='k'>\n", "elem.setCustomValidity(' ');\n", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=", "2"})
    public void validationResetCustomValidity() throws Exception {
        validation("<input type='search' id='e1' name='k'>\n",
                "elem.setCustomValidity('Invalid');elem.setCustomValidity('');", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "false",
             "false-false-false-false-false-false-false-false-false-false-true",
             "true",
             "§§URL§§", "1"})
    public void validationRequired() throws Exception {
        validation("<input type='search' id='e1' name='k' required>\n", "", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=one", "2"})
    public void validationRequiredValueSet() throws Exception {
        validation("<input type='search' id='e1' name='k' required>\n", "elem.value='one';", null);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"null",
             "",
             "false",
             "false-false-true-false-false-false-false-false-false-false-false",
             "true",
             "§§URL§§", "1"})
    public void validationPattern() throws Exception {
        validation("<input type='search' id='e1' name='k' pattern='abc'>\n", "elem.value='one';", null);
    }

    private void validation(final String htmlPart, final String jsPart, final String sendKeys) throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function logValidityState(s) {\n"
                + "      log(s.badInput"
                        + "+ '-' + s.customError"
                        + "+ '-' + s.patternMismatch"
                        + "+ '-' + s.rangeOverflow"
                        + "+ '-' + s.rangeUnderflow"
                        + "+ '-' + s.stepMismatch"
                        + "+ '-' + s.tooLong"
                        + "+ '-' + s.tooShort"
                        + " + '-' + s.typeMismatch"
                        + " + '-' + s.valid"
                        + " + '-' + s.valueMissing);\n"
                + "    }\n"
                + "    function test() {\n"
                + "      var elem = document.getElementById('e1');\n"
                + jsPart
                + "      log(elem.checkValidity());\n"
                + "      logValidityState(elem.validity);\n"
                + "      log(elem.willValidate);\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <form>\n"
                + htmlPart
                + "    <button id='myTest' type='button' onclick='test()'>Test</button>\n"
                + "    <button id='myButton' type='submit'>Submit</button>\n"
                + "  </form>\n"
                + "</body></html>";

        final String secondContent = DOCTYPE_HTML
                + "<html><head><title>second</title></head><body>\n"
                + "  <p>hello world</p>\n"
                + "</body></html>";

        getMockWebConnection().setResponse(URL_SECOND, secondContent);
        expandExpectedAlertsVariables(URL_FIRST);

        final WebDriver driver = loadPage2(html, URL_FIRST);

        final WebElement foo = driver.findElement(By.id("e1"));
        if (sendKeys != null) {
            foo.sendKeys(sendKeys);
        }
        assertEquals(getExpectedAlerts()[0], "" + foo.getDomAttribute("value"));
        assertEquals(getExpectedAlerts()[1], foo.getDomProperty("value"));

        driver.findElement(By.id("myTest")).click();
        verifyTitle2(driver, getExpectedAlerts()[2], getExpectedAlerts()[3], getExpectedAlerts()[4]);

        driver.findElement(By.id("myButton")).click();
        if (useRealBrowser()) {
            Thread.sleep(400);
        }
        assertEquals(getExpectedAlerts()[5], getMockWebConnection().getLastWebRequest().getUrl());
        assertEquals(Integer.parseInt(getExpectedAlerts()[6]), getMockWebConnection().getRequestCount());
    }
}

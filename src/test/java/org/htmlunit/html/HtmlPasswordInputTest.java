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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Tests for {@link HtmlPasswordInput}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 * @author Anton Demydenko
 */
public class HtmlPasswordInputTest extends WebDriverTestCase {

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
            + "  <input type='password' name='tester' id='tester' value='bla'>\n"
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
     * @throws Exception if the test fails
     */
    @Test
    public void type() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head></head><body><input type='password' id='p'/></body></html>";
        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));

        p.sendKeys("abc");
        assertNull(p.getDomAttribute("value"));
        assertEquals("abc", p.getDomProperty("value"));

        p.sendKeys(Keys.BACK_SPACE);
        assertNull(p.getDomAttribute("value"));
        assertEquals("ab", p.getDomProperty("value"));

        p.sendKeys(Keys.BACK_SPACE);
        assertNull(p.getDomAttribute("value"));
        assertEquals("a", p.getDomProperty("value"));

        p.sendKeys(Keys.BACK_SPACE);
        assertNull(p.getDomAttribute("value"));
        assertEquals("", p.getDomProperty("value"));

        p.sendKeys(Keys.BACK_SPACE);
        assertNull(p.getDomAttribute("value"));
        assertEquals("", p.getDomProperty("value"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void typeWhileDisabled() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><body><input type='password' id='p' disabled='disabled'/></body></html>";
        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));
        try {
            p.sendKeys("abc");
            fail();
        }
        catch (final InvalidElementStateException e) {
            // as expected
        }
        assertNull(p.getDomAttribute("value"));
        assertEquals("", p.getDomProperty("value"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"null", "null"})
    public void typeDoesNotChangeValueAttribute() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "<script>" + LOG_TITLE_FUNCTION + "</script>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <input type='password' id='p'/>\n"
                + "  <button id='check' onclick='log(document.getElementById(\"p\").getAttribute(\"value\"));'>"
                        + "DoIt</button>\n"
                + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));

        final WebElement check = driver.findElement(By.id("check"));
        check.click();
        verifyTitle2(driver, getExpectedAlerts()[0]);

        p.sendKeys("abc");
        check.click();
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"HtmlUnit", "HtmlUnit"})
    public void typeDoesNotChangeValueAttributeWithInitialValue() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "<script>" + LOG_TITLE_FUNCTION + "</script>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <input type='password' id='p' value='HtmlUnit'/>\n"
                + "  <button id='check' onclick='log(document.getElementById(\"p\").getAttribute(\"value\"));'>"
                        + "DoIt</button>\n"
                + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));

        final WebElement check = driver.findElement(By.id("check"));
        check.click();
        verifyTitle2(driver, getExpectedAlerts()[0]);

        p.sendKeys("abc");
        check.click();
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void preventDefault_OnKeyDown() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + "  function handler(e) {\n"
            + "    if (e && e.target.value.length > 2)\n"
            + "      e.preventDefault();\n"
            + "    else if (!e && window.event.srcElement.value.length > 2)\n"
            + "      return false;\n"
            + "  }\n"
            + "  function init() {\n"
            + "    document.getElementById('p').onkeydown = handler;\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='init()'>\n"
            + "<input type='password' id='p'></input>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));

        p.sendKeys("abcd");
        assertNull(p.getDomAttribute("value"));
        assertEquals("abc", p.getDomProperty("value"));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void preventDefault_OnKeyPress() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + "  function handler(e) {\n"
            + "    if (e && e.target.value.length > 2)\n"
            + "      e.preventDefault();\n"
            + "    else if (!e && window.event.srcElement.value.length > 2)\n"
            + "      return false;\n"
            + "  }\n"
            + "  function init() {\n"
            + "    document.getElementById('p').onkeypress = handler;\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='init()'>\n"
            + "<input type='password' id='p'></input>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));

        p.sendKeys("abcd");
        assertNull(p.getDomAttribute("value"));
        assertEquals("abc", p.getDomProperty("value"));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"foo", "change", "boo", "blur", "boo", "blur"})
    public void typeOnChange() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>" + LOG_TITLE_FUNCTION + "</script>\n"
            + "</head>\n"
            + "<body>\n"
            + "<input type='password' id='p' value='Hello world'"
                + " onChange='log(\"foo\");log(event.type);'"
                + " onBlur='log(\"boo\");log(event.type);'>\n"
            + "<button id='b'>some button</button>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement p = driver.findElement(By.id("p"));
        p.sendKeys("HtmlUnit");

        assertTrue(getCollectedAlerts(driver, 1).isEmpty());

        // trigger lost focus
        driver.findElement(By.id("b")).click();
        verifyTitle2(driver, getExpectedAlerts()[0], getExpectedAlerts()[1],
                getExpectedAlerts()[2], getExpectedAlerts()[3]);

        // set only the focus but change nothing
        p.click();
        assertTrue(getCollectedAlerts(driver, 1).isEmpty());

        // trigger lost focus
        driver.findElement(By.id("b")).click();
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void setValueOnChange() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html>\n"
              + "<head>\n"
              + "<script>" + LOG_TITLE_FUNCTION + "</script>\n"
              + "</head>\n"
              + "<body>\n"
              + "  <input type='password' id='p' value='Hello world'"
                    + " onChange='log(\"foo\");log(event.type);'>\n"
              + "  <button id='b'>some button</button>\n"
              + "  <button id='set' onclick='document.getElementById(\"p\").value=\"HtmlUnit\"'>setValue</button>\n"
              + "</body></html>";

        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("set")).click();

        assertEquals(Collections.emptyList(), getCollectedAlerts(driver));

        // trigger lost focus
        driver.findElement(By.id("b")).click();
        assertEquals(Collections.emptyList(), getCollectedAlerts(driver));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void setDefaultValueOnChange() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html>\n"
              + "<head></head>\n"
              + "<body>\n"
              + "  <input type='password' id='p' value='Hello world'"
                    + " onChange='log(\"foo\");log(event.type);'>\n"
              + "  <button id='b'>some button</button>\n"
              + "  <button id='set' onclick='document.getElementById(\"p\").defaultValue=\"HtmlUnit\"'>"
                      + "setValue</button>\n"
              + "</body></html>";

        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("set")).click();

        verifyTitle2(driver);

        // trigger lost focus
        driver.findElement(By.id("b")).click();
        verifyTitle2(driver);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"--null", "--null", "--null"})
    public void defaultValues() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var input = document.getElementById('password1');\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"

            + "    input = document.createElement('input');\n"
            + "    input.type = 'password';\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"

            + "    var builder = document.createElement('div');\n"
            + "    builder.innerHTML = '<input type=\"password\">';\n"
            + "    input = builder.firstChild;\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='password1'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"--null", "--null", "--null"})
    public void defaultValuesAfterClone() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var input = document.getElementById('password1');\n"
            + "    input = input.cloneNode(false);\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"

            + "    input = document.createElement('input');\n"
            + "    input.type = 'password';\n"
            + "    input = input.cloneNode(false);\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"

            + "    var builder = document.createElement('div');\n"
            + "    builder.innerHTML = '<input type=\"password\">';\n"
            + "    input = builder.firstChild;\n"
            + "    input = input.cloneNode(false);\n"
            + "    log(input.value + '-' + input.defaultValue + '-' + input.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='password1'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"initial-initial-initial", "initial-initial-initial",
                "newValue-initial-initial", "newValue-initial-initial",
                "newValue-newDefault-newDefault", "newValue-newDefault-newDefault"})
    public void resetByClick() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var password = document.getElementById('testId');\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.getElementById('testReset').click;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.value = 'newValue';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.getElementById('testReset').click;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.defaultValue = 'newDefault';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.forms[0].reset;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='testId' value='initial'>\n"
            + "  <input type='reset' id='testReset'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"initial-initial-initial", "initial-initial-initial",
                "newValue-initial-initial", "newValue-initial-initial",
                "newValue-newDefault-newDefault", "newValue-newDefault-newDefault"})
    public void resetByJS() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var password = document.getElementById('testId');\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.forms[0].reset;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.value = 'newValue';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.forms[0].reset;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.defaultValue = 'newDefault';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    document.forms[0].reset;\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='testId' value='initial'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"initial-initial-initial", "default-default-default",
                "newValue-default-default", "newValue-attribValue-attribValue",
                "newValue-newDefault-newDefault"})
    public void value() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var password = document.getElementById('testId');\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.defaultValue = 'default';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.value = 'newValue';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.setAttribute('value', 'attribValue');\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"

            + "    password.defaultValue = 'newDefault';\n"
            + "    log(password.value + '-' + password.defaultValue + '-' + password.getAttribute('value'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='testId' value='initial'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "textLength not available",
            FF = "7",
            FF_ESR = "7")
    public void textLength() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var text = document.getElementById('testId');\n"
            + "    if(text.textLength) {\n"
            + "      log(text.textLength);\n"
            + "    } else {\n"
            + "      log('textLength not available');\n"
            + "    }\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<form>\n"
            + "  <input type='password' id='testId' value='initial'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("0")
    public void selection() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(getSelection(document.getElementById('text1')).length);\n"
            + "  }\n"
            + "  function getSelection(element) {\n"
            + "    return element.value.substring(element.selectionStart, element.selectionEnd);\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='test()'>\n"
            + "  <input type='password' id='text1'/>\n"
            + "</body></html>";
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if test fails
     */
    @Test
    @Alerts({"0,0", "11,11", "3,11", "3,10"})
    public void selection2_1() throws Exception {
        selection2(3, 10);
    }

    /**
     * @throws Exception if test fails
     */
    @Test
    @Alerts({"0,0", "11,11", "11,11", "11,11"})
    public void selection2_2() throws Exception {
        selection2(-3, 15);
    }

    /**
     * @throws Exception if test fails
     */
    @Test
    @Alerts({"0,0", "11,11", "10,11", "5,5"})
    public void selection2_3() throws Exception {
        selection2(10, 5);
    }

    private void selection2(final int selectionStart, final int selectionEnd) throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<body>\n"
            + "<input id='myTextInput' value='Bonjour' type='password'>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  var input = document.getElementById('myTextInput');\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "  input.value = 'Hello there';\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "  input.selectionStart = " + selectionStart + ";\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "  input.selectionEnd = " + selectionEnd + ";\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "</script>\n"
            + "</body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if test fails
     */
    @Test
    @Alerts({"0,0", "4,5", "10,10", "4,4", "1,1"})
    public void selectionOnUpdate() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<body>\n"
            + "<input id='myTextInput' value='Hello' type='password'>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  var input = document.getElementById('myTextInput');\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"

            + "  input.selectionStart = 4;\n"
            + "  input.selectionEnd = 5;\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "  input.value = 'abcdefghif';\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"

            + "  input.value = 'abcd';\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"

            + "  input.selectionStart = 0;\n"
            + "  input.selectionEnd = 4;\n"

            + "  input.value = 'a';\n"
            + "  log(input.selectionStart + ',' + input.selectionEnd);\n"
            + "</script>\n"
            + "</body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("password")
    public void upperCase() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.getElementById('myId').type);\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='test()'>\n"
            + "  <input TYPE='password' id='myId'>\n"
            + "</body></html>";
        final WebDriver driver = loadPageVerifyTitle2(html);
        if (driver instanceof HtmlUnitDriver) {
            final HtmlPage page = (HtmlPage) getEnclosedPage();
            assertTrue(HtmlPasswordInput.class.isInstance(page.getHtmlElementById("myId")));
        }
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
            + "  <input type='password' id='tester'>\n"
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
        validation("<input type='password' pattern='[0-9a-zA-Z]{10,40}' id='e1' name='k' value='0987654321!'>\n",
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
        validation("<input type='password' pattern='[0-9a-zA-Z]{10,40}' "
                + "id='e1' name='k' value='68746d6c756e69742072756c657a21'>\n", "", null);
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
        validation("<input type='password' pattern='[0-9a-zA-Z]{10,40}' id='e1' name='k' value=''>\n", "", null);
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
        validation("<input type='password' pattern='[0-9a-zA-Z]{10,40}' id='e1' name='k' value=' '>\n", "", null);
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
        validation("<input type='password' pattern='[0-9a-zA-Z]{10,40}' id='e1' name='k' value='  \t'>\n", "", null);
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
        validation("<input type='password' pattern='[ 012]{3,10}' id='e1' name='k' value=' 210 '>\n", "", null);
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
        validation("<input type='password' pattern='[ 012]{3,10}' id='e1' name='k'>\n", "", " 210 ");
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
        validation("<input type='password' minlength='5' id='e1' name='k'>\n", "", "abcd");
    }


    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"ab",
             "ab",
             "true",
             "false-false-false-false-false-false-false-false-false-true-false",
             "true",
             "§§URL§§?k=ab", "2"})
    public void minLengthValidationInvalidInitial() throws Exception {
        validation("<input type='password' minlength='5' id='e1' name='k' value='ab'>\n", "", null);
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
        validation("<input type='password' minlength='5' id='e1' name='k'>\n", "", null);
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
        validation("<input type='password' minlength='5' id='e1' name='k'>\n", "", "abcdefghi");
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
        validation("<input type='password' maxlength='5' id='e1' name='k'>\n", "", "abcd");
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
        validation("<input type='password' maxlength='5' id='e1' name='k'>\n", "", "abcdefghi");
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
        validation("<input type='password' maxlength='5' id='e1' name='k' value='abcdefghi'>\n", "", null);
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
                + "    <input type='password' id='o1'>\n"
                + "    <input type='password' id='o2' disabled>\n"
                + "    <input type='password' id='o3' hidden>\n"
                + "    <input type='password' id='o4' readonly>\n"
                + "    <input type='password' id='o5' style='display: none'>\n"
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
        validation("<input type='password' id='e1' name='k'>\n", "", null);
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
        validation("<input type='password' id='e1' name='k'>\n", "elem.setCustomValidity('Invalid');", null);
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
        validation("<input type='password' id='e1' name='k'>\n", "elem.setCustomValidity(' ');\n", null);
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
        validation("<input type='password' id='e1' name='k'>\n",
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
        validation("<input type='password' id='e1' name='k' required>\n", "", null);
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
             "§§URL§§?k=victoria", "2"})
    public void validationRequiredValueSet() throws Exception {
        validation("<input type='password' id='e1' name='k' required>\n", "elem.value='victoria';", null);
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
        validation("<input type='password' id='e1' name='k' pattern='abc'>\n", "elem.value='one';", null);
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

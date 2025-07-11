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

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.junit.annotation.HtmlUnitNYI;
import org.htmlunit.util.MimeType;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Unit tests for {@link HTMLScriptElement}.
 * TODO: check event order with defer in real browser WITHOUT using alert(...) as it impacts ordering.
 * Some expectations seems to be incorrect.
 * @author Daniel Gredler
 * @author Ahmed Ashour
 * @author Marc Guillemot
 * @author Frank Danek
 * @author Ronald Brill
 */
public class HTMLScriptElementTest extends WebDriverTestCase {

    /**
     * Verifies that the <tt>onreadystatechange</tt> handler is invoked correctly.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"1", "2", "3", "4", "onload"})
    public void onReadyStateChangeHandler() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function test() {\n"
            + "        var script = document.createElement('script');\n"
            + "        script.id = 'b';\n"
            + "        script.type = 'text/javascript';\n"
            + "        script.onreadystatechange = null;\n"
            + "        script.onreadystatechange = function() {\n"
            + "          log('onreadystatechange ' + script.readyState);\n"
            + "        }\n"
            + "        script.onload = function() {\n"
            + "          log('onload');\n"
            + "        }\n"
            + "        log('1');\n"
            + "        script.src = 'script.js';\n"
            + "        log('2');\n"
            + "        document.getElementsByTagName('head')[0].appendChild(script);\n"
            + "        log('3');\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  </body></html>";

        getMockWebConnection().setDefaultResponse("log('4');", MimeType.TEXT_JAVASCRIPT);

        loadPage2(html);
        verifyTitle2(DEFAULT_WAIT_TIME, getWebDriver(), getExpectedAlerts());
    }

    /**
     * Test for bug
     * <a href="https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1782719&group_id=47038">issue 514</a>.
     * @throws Exception if the test fails
     */
    @Test
    public void srcWithJavaScriptProtocol_Static() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head><script src='javascript:\"alert(1)\"'></script></head><body></body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"§§URL§§foo.js", "foo.js", "§§URL§§", ""})
    public void srcPropertyShouldBeAFullUrl() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function test() {\n"
                + "      var script = document.getElementById('my');\n"
                + "      log(script.src);\n"
                + "      log(script.getAttribute('src'));\n"

                + "      var script2 = document.getElementById('my2');\n"
                + "      log(script2.src);\n"
                + "      log(script2.getAttribute('src'));\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "  <script id='my' src='foo.js'></script>\n"
                + "  <script id='my2' src=''></script>\n"
                + "</body></html>";

        getMockWebConnection().setDefaultResponse("", "text/javascript");

        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"", "null", "", "null"})
    public void srcPropertyNoSource() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function test() {\n"
                + "      var script = document.getElementById('my');\n"
                + "      log(script.src);\n"
                + "      log(script.getAttribute('src'));\n"

                + "      var script2 = document.createElement('script');\n"
                + "      log(script2.src);\n"
                + "      log(script2.getAttribute('src'));\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "  <script id='my'></script>\n"
                + "</body></html>";

        getMockWebConnection().setDefaultResponse("", "text/javascript");

        loadPageVerifyTitle2(html);
    }

    /**
     * Test for bug
     * <a href="https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1782719&group_id=47038">issue 514</a>.
     * @throws Exception if the test fails
     */
    @Test
    public void srcWithJavaScriptProtocol_Dynamic() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    var script=document.createElement('script');\n"
            + "    script.src = \"javascript: 'log(1)'\";\n"
            + "    document.getElementsByTagName('head')[0].appendChild(script);\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Test for bug 2993940.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void reexecuteModifiedScript() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head></head><body>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  log('start');\n"
            + "  var script = document.getElementsByTagName('script')[0];\n"
            + "  script.text = \"log('executed');\";\n"
            + "  log('end');\n"
            + "</script>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"hello", "hello", "world", "-"})
    public void scriptInCdataXHtml() throws Exception {
        final String html =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE html PUBLIC \n"
            + "  \"-//W3C//DTD XHTML 1.0 Strict//EN\" \n"
            + "  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
            + "<html xmlns='http://www.w3.org/1999/xhtml' xmlns:xhtml='http://www.w3.org/1999/xhtml'>\n"
            + "<head></head>\n"
            + "<body>\n"
            + "<script>\n"
            // do not use LOG_TITLE_FUNCTION here
            + "    function log(msg) { window.document.title += msg + '\\u00a7'; }\n"
            + "</script>\n"
            + "  <script>\n"
            + "    //<![CDATA[\n"
            + "    log('hello');\n"
            + "    //]]>\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    /*<![CDATA[*/log('hello');/*]]>*/\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    <![CDATA[\n"
            + "    log('world');\n"
            + "    ]]>\n"
            + "  </script>\n"
            + "  <script>log('-');</script>\n"
            + "</body></html>";

        if (getWebDriver() instanceof HtmlUnitDriver) {
            getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        }

        final WebDriver driver = loadPage2(html, URL_FIRST, MimeType.APPLICATION_XHTML, StandardCharsets.UTF_8);
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"hello", "hello", "world", "-"})
    public void scriptInCdataXml() throws Exception {
        final String html =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE html PUBLIC \n"
            + "  \"-//W3C//DTD XHTML 1.0 Strict//EN\" \n"
            + "  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
            + "<html xmlns='http://www.w3.org/1999/xhtml' xmlns:xhtml='http://www.w3.org/1999/xhtml'>\n"
            + "<head></head>\n"
            + "<body>\n"
            + "<script>\n"
            // do not use LOG_TITLE_FUNCTION here
            + "    function log(msg) { window.document.title += msg + '\\u00a7'; }\n"
            + "</script>\n"
            + "  <script>\n"
            + "    //<![CDATA[\n"
            + "    log('hello');\n"
            + "    //]]>\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    /*<![CDATA[*/log('hello');/*]]>*/\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    <![CDATA[\n"
            + "    log('world');\n"
            + "    ]]>\n"
            + "  </script>\n"
            + "  <script>log('-');</script>\n"
            + "</body></html>";

        if (getWebDriver() instanceof HtmlUnitDriver) {
            getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        }
        final WebDriver driver = loadPage2(html, URL_FIRST, MimeType.TEXT_XML, StandardCharsets.UTF_8);
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"hello", "hello", "-"})
    public void scriptInCdataHtml() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head></head>\n"
            + "<body>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "</script>\n"
            + "  <script>\n"
            + "    //<![CDATA[\n"
            + "    log('hello');\n"
            + "    //]]>\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    /*<![CDATA[*/log('hello');/*]]>*/\n"
            + "  </script>\n"
            + "  <script>\n"
            + "    <![CDATA[\n"
            + "    log('world');\n"
            + "    ]]>\n"
            + "  </script>\n"
            + "  <script>log('-');</script>\n"
            + "</body></html>";

        if (getWebDriver() instanceof HtmlUnitDriver) {
            getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        }
        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>createTextNode</code> and <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void createElementWithCreateTextNode() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>createTextNode</code> and <code>appendChild</code>.
     * After that it appends the script element to the body.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "middle", "executed", "end"})
    public void createElementWithCreateTextNodeAndAppend() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('middle');\n"
              + "  document.body.appendChild(script);\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void createElementWithSetText() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>.text</code>.
     * After that it appends the script element to the body.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "middle", "executed", "end"})
    public void createElementWithSetTextAndAppend() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('middle');\n"
              + "  document.body.appendChild(script);\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>.src</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void createElementWithSetSrc() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        loadPageVerifyTitle2(html);
    }

    /**
     * Creates a new script element and adds the source using <code>.src</code>.
     * After that it appends the script element to the body.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "middle", "end", "executed"})
    public void createElementWithSetSrcAndAppend() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.createElement('script');\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('middle');\n"
              + "  document.body.appendChild(script);\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        final WebDriver driver = loadPage2(html);
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * Replaces the source of the current script element using <code>createTextNode</code> and <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceSelfWithCreateTextNode() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementsByTagName('script')[0];\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source of the current script element using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceSelfWithSetText() throws Exception {
        // TODO this test is the same as #reexecuteModifiedScriptWhenReappending()
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementsByTagName('script')[0];\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source of the current script element using <code>.src</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceSelfWithSetSrc() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementsByTagName('script')[0];\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the empty source of another script element using <code>createTextNode</code> and <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "executed", "end"})
    public void replaceWithCreateTextNodeEmpty() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'></script>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing just a blank of another script element using <code>createTextNode</code> and <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceWithCreateTextNodeBlank() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'> </script>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing a script of another script element using <code>createTextNode</code> and <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"script", "start", "end"})
    public void replaceWithCreateTextNodeScript() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'>\n"
              + LOG_TITLE_FUNCTION
              + "  log('script');\n"
              + "</script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {logEx(e); }\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the empty source of another script element using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "executed", "end"})
    public void replaceWithSetTextEmpty() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'></script>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing just a blank of another script element using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceWithSetTextBlank() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'> </script>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing a script of another script element using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"script", "start", "end"})
    public void replaceWithSetTextScript() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'>\n"
              + LOG_TITLE_FUNCTION
              + "  log('script');\n"
              + "</script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.text = \"log('executed');\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the empty source of another script element using <code>.src</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end", "executed"})
    public void replaceWithSetSrcEmpty() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "</script>\n"
              + "<script id='js1'></script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing just a blank of another script element using <code>.src</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"start", "end"})
    public void replaceWithSetSrcBlank() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'> </script>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        loadPageVerifyTitle2(html);
    }

    /**
     * Replaces the source containing a script of another script element using <code>.text</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"script", "start", "end"})
    public void replaceWithSetSrcScript() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script id='js1'>\n"
              + LOG_TITLE_FUNCTION
              + "  log('script');\n"
              + "</script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  script.src = \"" + URL_SECOND + "\";\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</body></html>";

        final String js = "log('executed');";
        getMockWebConnection().setResponse(URL_SECOND, js);

        loadPageVerifyTitle2(html);
    }

    /**
     * Moves a script element from a div element to the body element using <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"executed", "start", "end"})
    public void moveWithAppend() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<div>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "</script>\n"
              + "<script id='js1'>log('executed');</script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  document.body.appendChild(script);\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</div>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Moves a script element from a div element to the body element using <code>insertBefore</code>.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"executed", "start", "end"})
    public void moveWithInsert() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<div>\n"
              + "<script id='js1'>\n"
              + LOG_TITLE_FUNCTION
              + "  log('executed');\n"
              + "</script>\n"
              + "<script>\n"
              + "  log('start');\n"
              + "  var script = document.getElementById('js1');\n"
              + "  document.body.insertBefore(script, null);\n"
              + "  log('end');\n"
              + "</script>\n"
              + "</div>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"script-for", "TypeError", "script-body"})
    public void scriptForEvent() throws Exception {
        // IE accepts it with () or without
        scriptForEvent("onload");
        scriptForEvent("onload()");
    }

    private void scriptForEvent(final String eventName) throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "</script>\n"
            + "<script FOR='window' EVENT='" + eventName + "' LANGUAGE='javascript'>\n"
            + "  log('script-for');\n"
            + "  try {\n"
            + "    document.form1.txt.value = 'hello';\n"
            + "    log(document.form1.txt.value);\n"
            + "  } catch(e) {logEx(e); }\n"
            + "</script></head>\n"
            + "<body>\n"
            + "  <form name='form1'><input type='text' name='txt'></form>\n"
            + "  <script>\n"
            + "    log('script-body');\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Verifies the correct the ordering of script element execution, deferred script element
     * execution, script ready state changes, deferred script ready state changes, and onload
     * handlers.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"3", "4", "2", "5"})
    public void onReadyStateChange_Order() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "    </script>\n"
            + "    <script defer=''>log('3');</script>\n"
            + "    <script defer onreadystatechange='if(this.readyState==\"complete\") log(\"6\");'>log('4');</script>\n"
            + "    <script src='//:' onreadystatechange='if(this.readyState==\"complete\") log(\"1\");'></script>\n"
            + "    <script defer='' src='//:' onreadystatechange='if(this.readyState==\"complete\") log(\"7\");'></script>\n"
            + "    <script>log('2')</script>\n"
            + "  </head>\n"
            + "  <body onload='log(5)'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void onReadyStateChange_EventAvailable() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body><script>\n"
            + LOG_TITLE_FUNCTION
            + "var s = document.createElement('script');\n"
            + "s.src = '//:';\n"
            + "s.onreadystatechange = function() {log(window.event);};\n"
            + "document.body.appendChild(s);\n"
            + "</script></body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Verifies the correct the ordering of script element execution, deferred script element
     * execution, script ready state changes, deferred script ready state changes, and onload
     * handlers when the document doesn't have an explicit <tt>body</tt> element.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"3", "4", "2"})
    public void onReadyStateChange_Order_NoBody() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "    </script>\n"
            + "    <script defer=''>log('3');</script>\n"
            + "    <script defer='' onreadystatechange='if(this.readyState==\"complete\") log(\"5\");'>log('4');</script>\n"
            + "    <script src='//:' onreadystatechange='if(this.readyState==\"complete\") log(\"1\");'></script>\n"
            + "    <script defer='' src='//:' onreadystatechange='if(this.readyState==\"complete\") log(\"6\");'></script>\n"
            + "    <script>log('2')</script>\n"
            + "  </head>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("1")
    public void text() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function test() {\n"
            + "        execMe('log(1)');\n"
            + "      }\n"
            + "      function execMe(text) {\n"
            + "        document.head = document.getElementsByTagName('head')[0];\n"
            + "        var script = document.createElement('script');\n"
            + "        script.text = text;\n"
            + "        document.head.appendChild(script);\n"
            + "        document.head.removeChild(script);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("onload")
    public void onload_after_deferReadStateComplete() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "    </script>\n"
            + "    <script onreadystatechange='if(this.readyState==\"complete\") log(\"defer\");' defer></script>\n"
            + "  </head>\n"
            + "  <body onload='log(\"onload\")'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Regression test for bug 47038.
     * <a href="http://sourceforge.net/tracker/?func=detail&atid=448266&aid=3403860&group_id=47038">issue</a>
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"1", "2", "3"})
    public void scriptType() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "</script>\n"
            + "<script type='text/javascript'>log(1)</script>\n"
            + "<script type=' text/javascript'>log(2)</script>\n"
            + "<script type=' text/javascript '>log(3)</script>\n"
            + "<script type=' text / javascript '>log(4)</script>\n"
            + "</head>\n"
            + "<body>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("\\n\\s\\s<ul>{{for\\speople}}\\n\\s\\s\\s\\s<li>Name:\\s{{:name}}</li>\\n\\s\\s{{/for}}</ul>\\n")
    public void specialScriptType() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION_NORMALIZE
            + "</script>\n"
            + "<script id='template' type='text/x-jsrender'>\n"
            + "  <ul>{{for people}}\n"
            + "    <li>Name: {{:name}}</li>\n"
            + "  {{/for}}</ul>\n"
            + "</script>\n"

            + "<script>\n"
            + "function doTest() {\n"
            + "  script = document.getElementById('template');\n"
            + "  log(script.innerHTML);\n"
            + "}\n"
            + "</script>\n"

            + "</head>\n"
            + "<body onload='doTest()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Test exception throw by IE when calling <code>appendChild</code>.
     * @throws Exception if the test fails
     */
    @Test
    public void appendChild_UnexpectedCall() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  var script = document.createElement('script');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.appendChild(source);\n"
              + "  } catch(e) {\n"
              + "    log(e.message.slice(0,44));\n"
              + "  }\n"
              + "</script>\n"
              + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Test exception throw by IE when calling <code>insertBefore</code>.
     * @throws Exception if the test fails
     */
    @Test
    public void insertBeforeUnexpectedCall() throws Exception {
        final String html = DOCTYPE_HTML
              + "<html><head><title>foo</title></head><body>\n"
              + "<script>\n"
              + LOG_TITLE_FUNCTION
              + "  var script = document.createElement('script');\n"
              + "  var source = document.createTextNode(\"log('executed');\");\n"
              + "  try {\n"
              + "    script.insertBefore(source, null);\n"
              + "  } catch(e) {\n"
              + "    log(e.message.slice(0,44));\n"
              + "  }\n"
              + "</script>\n"
              + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Firefox should not run scripts with "event" and "for" attributes.
     *
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("onload for window")
    public void scriptEventFor() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "</script>\n"
            + "</head><body>\n"
            + "  <script event='onload' for='window'>\n"
            + "    log('onload for window');\n"
            + "  </script>\n"
            + "  <div id='div1'>the div 1</div>\n"
            + "  <div id='div2'>the div 2</div>\n"
            + "  <script event='onclick' for='div1'>\n"
            + "    log('onclick for div1');\n"
            + "  </script>\n"
            + "  <script event='onclick' for='document.all.div2'>\n"
            + "    log('onclick for div2');\n"
            + "  </script>\n"
            + "</body></html>";

        final WebDriver webDriver = loadPage2(html);
        webDriver.findElement(By.id("div1")).click();
        webDriver.findElement(By.id("div2")).click();

        verifyTitle2(webDriver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"function foo() { return a > b}", "function mce() { return a &gt; b}"})
    public void innerHtml() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"

            + "<script id='script1'>function foo() { return a > b}</script>\n"

            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  script = document.getElementById('script1');\n"
            + "  log(script.innerHTML);\n"

            + "  script = document.getElementById('mce');\n"
            + "  log(script.innerHTML);\n"

            + "}\n"
            + "</script>\n"
            + "</head><body onload='doTest()'>\n"
            // this is done by TinyMce
            + "<script>document.write('<mce:script id=\"mce\">function mce() { return a > b}</mce:script>');</script>\n"

            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("\\n\\s\\s\\s\\s<script\\sid=\"testScript\">function\\sfoo()\\s{\\sreturn\\sa\\s>\\sb}</script>\\n\\s\\s")
    public void innerHTMLGetSet() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head></head>\n"
            + "<body>\n"

            + "  <div id='tester'>\n"
            + "    <script id='testScript'>function foo() { return a > b}</script>\n"
            + "  </div>\n"

            + "  <script type='text/javascript'>\n"
            + LOG_TITLE_FUNCTION_NORMALIZE
            + "    var div = document.getElementById('tester');\n"
            + "    try {\n"
            + "      div.innerHTML = div.innerHTML;\n"
            + "    } catch(e) { logEx(e); }\n"
            + "    log(div.innerHTML);\n"
            + "  </script>\n"

            + "</body>\n"
            + "</html>\n";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("1 3 2")
    public void async() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script src='js1.js'></script>\n"
            + "<script src='js2.js' async></script>\n"
            + "<script src='js3.js'></script>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "document.title += ' 1';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "document.title += ' 2';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js3.js"), "document.title += ' 3';");

        final WebDriver driver = loadPage2(html);
        assertTitle(driver, getExpectedAlerts()[0]);
    }

    /**
     * The async attribute must not be used if the src attribute is absent (i.e. for inline scripts)
     * for classic scripts, in this case it would have no effect.
     *
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("1 two 3 5 4")
    public void asyncWithoutSrc() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script src='js1.js'></script>\n"
            + "<script async>document.title += ' two';</script>\n"
            + "<script src='js3.js'></script>\n"
            + "<script src='js4.js' async>document.title += ' four';</script>\n"
            + "<script src='js5.js'></script>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "document.title += ' 1';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js3.js"), "document.title += ' 3';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js4.js"), "document.title += ' 4';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js5.js"), "document.title += ' 5';");

        final WebDriver driver = loadPage2(html);
        assertTitle(driver, getExpectedAlerts()[0]);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("2 1")
    @HtmlUnitNYI(CHROME = "1 2",
            EDGE = "1 2",
            FF = "1 2",
            FF_ESR = "1 2")
    public void async2() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var s1 = document.createElement('script');\n"
            + "  s1.src = 'js1.js';\n"
            + "  s1.async = true;\n"
            + "  document.body.appendChild(s1);\n"
            + "</script>\n"
            + "<script src='js2.js'></script>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "document.title += ' 1';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "document.title += ' 2';");

        final WebDriver driver = loadPage2(html);
        assertTitle(driver, getExpectedAlerts()[0]);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("0 3 2 1")
    @HtmlUnitNYI(CHROME = "0 3 1 2",
            EDGE = "0 3 1 2",
            FF = "0 3 1 2",
            FF_ESR = "0 3 1 2")
    public void syncLoadsAsync() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  document.title += ' 0';"
            + "  var s1 = document.createElement('script');\n"
            + "  s1.src = 'js1.js';\n"
            + "  s1.async = true;\n"
            + "  document.body.appendChild(s1);\n"
            + "  document.title += ' 3';"
            + "</script>\n"
            + "<script src='js2.js'></script>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "document.title += ' 1';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "document.title += ' 2';");

        final WebDriver driver = loadPage2(html);
        assertTitle(driver, getExpectedAlerts()[0]);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "2 0 3 1",
            FF_ESR = "0 3 2 1")
    @HtmlUnitNYI(FF_ESR = "2 0 3 1")
    public void asyncLoadsAsync() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script src='script.js' async></script>\n"
            + "<script src='js2.js'></script>\n"
            + "</body></html>\n";

        final String script =
                "  document.title += ' 0';"
                + "  var s1 = document.createElement('script');\n"
                + "  s1.src = 'js1.js';\n"
                + "  s1.async = true;\n"
                + "  document.body.appendChild(s1);\n"
                + "  document.title += ' 3';\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "script.js"), script);

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "document.title += ' 1';");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "document.title += ' 2';");

        final WebDriver driver = loadPage2(html);
        assertTitle(driver, getExpectedAlerts()[0]);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"1", "2", "3"})
    public void syncFromAsyncTask() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body><script>\n"
            + LOG_TITLE_FUNCTION
            + "function addScript() {\n"
            + "  var script = document.createElement('script');\n"
            + "  script.src = 'js.js';\n"
            + "  document.head.appendChild(script);\n"
            + "  log('2');\n"
            + "}\n"
            + "setTimeout(addScript, 5);\n"
            + "  log('1');\n"
            + "</script></body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js.js"), "log('3')");

        final WebDriver driver = loadPage2(html);
        verifyTitle2(DEFAULT_WAIT_TIME, driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"1", "2", "3"})
    public void asyncFromAsyncTask() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body><script>\n"
            + LOG_TITLE_FUNCTION
            + "function addAsyncScript() {\n"
            + "  var script = document.createElement('script');\n"
            + "  script.src = 'js.js';\n"
            + "  script.async = true;\n"
            + "  document.head.appendChild(script);\n"
            + "  log('2');\n"
            + "}\n"
            + "setTimeout(addAsyncScript, 5);\n"
            + "  log('1');\n"
            + "</script></body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js.js"), "log('3')");

        final WebDriver driver = loadPage2(html);
        verifyTitle2(DEFAULT_WAIT_TIME, driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"undefined", "append", "append done", "from script", "undefined"})
    public void asyncOnLoad() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><body>\n"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "</script>\n"
                + "<script>\n"
                + "  var script = document.createElement('script');\n"
                + "  log(script.readyState);\n"
                + "  script.src = 'js.js';\n"
                + "  script.async = true;\n"
                + "  script.onload = function () {\n"
                + "    log(this.readyState);\n"
                + "  };\n"
                + "  log('append');\n"
                + "  document.body.appendChild(script);\n"
                + "  log('append done');\n"
                + "</script>\n"
                + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js.js"), "log('from script');");

        final WebDriver driver = loadPage2(html);
        verifyTitle2(driver, getExpectedAlerts());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"false", "null", "true", "", "true", "", "false", "null"})
    public void asyncProperty() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script id='script1' src='js1.js'></script>\n"
            + "<script id='script2' src='js2.js' async></script>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  var script = document.getElementById('script1');\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script.async = true;\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script = document.getElementById('script2');\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script.async = false;\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "}\n"
            + "</script>\n"
            + "</head><body onload='doTest()'>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "");

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"false", "null", "true", "true", "true", "", "true", "true", "false", "null"})
    public void asyncAttribute() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script id='script1' src='js1.js'></script>\n"
            + "<script id='script2' src='js2.js' async></script>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  var script = document.getElementById('script1');\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script.setAttribute('async', true);\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script = document.getElementById('script2');\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script.setAttribute('async', true);\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"

            + "  script.removeAttribute('async');\n"
            + "  log(script.async);\n"
            + "  log(script.getAttribute('async'));\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='doTest()'>\n"
            + "</body></html>\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "js1.js"), "");
        getMockWebConnection().setResponse(new URL(URL_FIRST, "js2.js"), "");

        loadPageVerifyTitle2(html);
    }

    /**
     * <a href="https://github.com/HtmlUnit/htmlunit/issues/11">issue #11</a>.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("inside script.js")
    public void loadScriptDynamicallyAdded() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function test() {\n"
            + "        var script = document.createElement('script');\n"
            + "        script.type = 'text/javascript';\n"
            + "        script.async = true;\n"
            + "        script.src = 'script.js';\n"

            + "        var s = document.getElementsByTagName('script')[0];\n"
            + "        s.parentNode.insertBefore(script, s);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  </body></html>";

        final String js = "log('inside script.js');";

        getMockWebConnection().setDefaultResponse(js, MimeType.TEXT_JAVASCRIPT);

        loadPageVerifyTitle2(html);
    }

    /**
     * JQuery disables script execution this way.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"change type", "type changed"})
    public void loadScriptDynamicallyAddedUnsupportedType() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function test() {\n"
            + "        var script = document.createElement('script');\n"
            + "        script.type = 'true/text/javascript';\n"
            + "        script.src = 'script.js';\n"

            + "        var s = document.getElementsByTagName('script')[0];\n"
            + "        s.parentNode.insertBefore(script, s);\n"

            + "        log('change type');\n"
            + "        s.type = 'text/javascript';\n"
            + "        log('type changed');\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  </body></html>";

        final String js = "log('inside script.js');";

        getMockWebConnection().setDefaultResponse(js, MimeType.TEXT_JAVASCRIPT);

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"out", "\\n\\s\\s\\s\\s<!--\\syy\\s--!>\\n\\s\\s\\s\\slog('out');\\n\\s\\s"})
    public void incorrectlyClosedComment() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "  <script>\n"
            + LOG_TITLE_FUNCTION_NORMALIZE
            + "  </script>\n"
            + "  <script id='testScript'>\n"
            + "    <!-- yy --!>\n"
            + "    log('out');\n"
            + "  </script>\n"
            + "</head>\n"
            + "<body>\n"
            + "  <!-- xx -->\n"

            + "  <script >\n"
            + "    log(document.getElementById('testScript').innerHTML);\n"
            + "  </script>\n"

            + "</body>\n"
            + "</html>\n";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"null-", "testType-testType", "-"})
    public void modifyType() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "  <script id='testScript'></script>\n"
            + "</head>\n"
            + "<body>\n"

            + "  <script >\n"
            + LOG_TITLE_FUNCTION
            + "    var script = document.getElementById('testScript');\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "    script.type = 'testType';\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "    script.type = '';\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "  </script>\n"

            + "</body>\n"
            + "</html>\n";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"typeAttr-typeAttr", "null-", "newType-newType", "null-null"})
    public void modifyTypeAttribute() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "  <script id='testScript' type='typeAttr'></script>\n"
            + "</head>\n"
            + "<body>\n"

            + "  <script >\n"
            + LOG_TITLE_FUNCTION
            + "    var script = document.getElementById('testScript');\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "    script.removeAttribute('type');\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "    script.setAttribute('type', 'newType');\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "    script.setAttribute('type', null);\n"
            + "    log(script.getAttribute('type') + '-' + script.type);\n"

            + "  </script>\n"

            + "</body>\n"
            + "</html>\n";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"typeAttr", "text/javascript"})
    public void modifyTypeToJs() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "<head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "</script>\n"
            + "  <script id='testScript' type='typeAttr'>log('exec');</script>\n"
            + "</head>\n"
            + "<body>\n"

            + "  <script >\n"
            + "    var script = document.getElementById('testScript');\n"
            + "    log(script.getAttribute('type'));\n"

            + "    script.type = 'text/javascript';\n"
            + "    log(script.getAttribute('type'));\n"
            + "  </script>\n"

            + "</body>\n"
            + "</html>\n";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("onerror")
    public void onErrorHandler() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "    </script>\n"
            + "    <script src='http://www.unknown-host.xyz' onload='log(\"onload\")' onerror='log(\"onerror\")'></script>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "  </body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"var x = 'HtmlUnit'", "</> htmx rocks!"})
    public void innerHtml1() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <title>Page Title</title>\n"
            + "    <script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "      function test() {\n"
            + "        var script = document.getElementsByTagName('script')[1];\n"
            + "        log(script.innerHTML);\n"
            + "        script.innerHTML = '</> htmx rocks!';\n"
            + "        log(script.innerHTML);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>"
            + "    <script>var x = 'HtmlUnit'</script>\n"
            + LOG_TEXTAREA
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTextArea2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"var x = 'HtmlUnit'", "<div>htmx rocks</div>"})
    public void innerHtmlTag() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <title>Page Title</title>\n"
            + "    <script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "      function test() {\n"
            + "        var script = document.getElementsByTagName('script')[1];\n"
            + "        log(script.innerHTML);\n"
            + "        script.innerHTML = '<div>htmx rocks</div>';\n"
            + "        log(script.innerHTML);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <script>var x = 'HtmlUnit'</script>\n"
            + LOG_TEXTAREA
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTextArea2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"", "&lt;/> htmx rocks!"})
    public void innerHtmlEscaping() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <title>Page Title</title>\n"
            + "    <script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "      function test() {\n"
            + "        var script = document.getElementsByTagName('script')[1];\n"
            + "        log(script.innerHTML);\n"
            + "        script.innerHTML = '&lt;/> htmx rocks!';\n"
            + "        log(script.innerHTML);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <script></script>\n"
            + LOG_TEXTAREA
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTextArea2(html);
    }
}

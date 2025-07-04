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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlunit.CollectingAlertHandler;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.MockWebConnection;
import org.htmlunit.SimpleWebTestCase;
import org.htmlunit.WebClient;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.util.MimeType;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HtmlScript}.
 *
 * @author Marc Guillemot
 * @author Daniel Gredler
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
public class HtmlScriptTest extends SimpleWebTestCase {

    /**
     * Verifies that a failing HTTP status code for a JavaScript file request (like a 404 response)
     * results in a {@link FailingHttpStatusCodeException}, depending on how the client has been
     * configured.
     *
     * @see org.htmlunit.WebClientOptions#isThrowExceptionOnFailingStatusCode()
     * @throws Exception if an error occurs
     */
    @Test
    public void badExternalScriptReference() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head><title>foo</title>\n"
                + "<script src='inexistent.js'></script>\n"
                + "</head><body></body></html>";

        final WebClient client = getWebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setDefaultResponse("inexistent", 404, "Not Found", MimeType.TEXT_HTML);
        webConnection.setResponse(URL_FIRST, html);
        client.setWebConnection(webConnection);

        try {
            client.getPage(URL_FIRST);
            fail("Should throw.");
        }
        catch (final FailingHttpStatusCodeException e) {
            final String url = URL_FIRST.toExternalForm();
            assertTrue("exception contains URL of failing script", e.getMessage().indexOf(url) > -1);
            assertEquals(404, e.getStatusCode());
            assertEquals("Not Found", e.getStatusMessage());
        }

        client.getOptions().setThrowExceptionOnFailingStatusCode(false);

        try {
            client.getPage(URL_FIRST);
        }
        catch (final FailingHttpStatusCodeException e) {
            fail("Should not throw.");
        }
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void asNormalizedText() throws Exception {
        final String html = DOCTYPE_HTML + "<html><body><script id='s'>var foo = 132;</script></body></html>";
        final HtmlPage page = loadPage(html);
        final HtmlScript script = page.getHtmlElementById("s");
        assertEquals("", script.asNormalizedText());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("hello")
    public void asXml() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><title>foo</title></head><body>\n"
            + "<script id='script1'>\n"
            + "  alert('hello');\n"
            + "</script></body></html>";

        final HtmlPage page = loadPageWithAlerts(html);

        // asXml() should be reusable
        final String xml = page.asXml();
        assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n"
                + "<html>\r\n"
                + "  <head>\r\n"
                + "    <title>foo</title>\r\n"
                + "  </head>\r\n"
                + "  <body>\r\n"
                + "    <script id=\"script1\">\r\n"
                + "//<![CDATA[\r\n"
                + "\n"
                + "  alert('hello');\n"
                + "\r\n"
                + "//]]>\r\n"
                + "    </script>\r\n"
                + "  </body>\r\n"
                + "</html>",
                xml);

        loadPageWithAlerts(xml);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void asXml_scriptNestedInCData() throws Exception {
        final String script = "//<![CDATA[\n"
            + "var foo = 132;\n"
            + "//]]>";
        final String html = DOCTYPE_HTML + "<html><body><script id='s'>" + script + "</script></body></html>";
        final HtmlPage page = loadPage(html);
        final HtmlScript scriptElement = page.getHtmlElementById("s");
        assertEquals("<script id=\"s\">\r\n" + script + "\r\n</script>",
                scriptElement.asXml());
    }

    /**
     * Verifies that cloned script nodes do not reload or re-execute their content (bug 1954869).
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("loaded")
    public void scriptCloneDoesNotReloadScript() throws Exception {
        final String html = DOCTYPE_HTML + "<html><body><script src='" + URL_SECOND + "'></script></body></html>";
        final String js = "alert('loaded')";

        final WebClient client = getWebClient();

        final MockWebConnection conn = new MockWebConnection();
        conn.setResponse(URL_FIRST, html);
        conn.setResponse(URL_SECOND, js, MimeType.TEXT_JAVASCRIPT);
        client.setWebConnection(conn);

        final List<String> actual = new ArrayList<>();
        client.setAlertHandler(new CollectingAlertHandler(actual));

        final HtmlPage page = client.getPage(URL_FIRST);
        assertEquals(2, conn.getRequestCount());

        page.cloneNode(true);
        assertEquals(2, conn.getRequestCount());

        assertEquals(getExpectedAlerts(), actual);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"c", "f"})
    public void addEventListener_error_clientThrows() throws Exception {
        addEventListener_error(true);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"c", "f"})
    public void addEventListener_error_clientDoesNotThrow() throws Exception {
        addEventListener_error(false);
    }

    private void addEventListener_error(final boolean throwOnFailingStatusCode) throws Exception {
        final URL fourOhFour = new URL(URL_FIRST, "/404");
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    var s1 = document.createElement('script');\n"
            + "    s1.text = 'var foo';\n"
            + "    s1.addEventListener('error', function() {alert('a')}, false);\n"
            + "    document.body.insertBefore(s1, document.body.firstChild);\n"
            + "    \n"
            + "    var s2 = document.createElement('script');\n"
            + "    s2.text = 'varrrr foo';\n"
            + "    s2.addEventListener('error', function() {alert('b')}, false);\n"
            + "    document.body.insertBefore(s2, document.body.firstChild);\n"
            + "    \n"
            + "    var s3 = document.createElement('script');\n"
            + "    s3.src = '//:';\n"
            + "    s3.addEventListener('error', function() {alert('c')}, false);\n"
            + "    document.body.insertBefore(s3, document.body.firstChild);\n"
            + "    \n"
            + "    var s4 = document.createElement('script');\n"
            + "    s4.src = '" + URL_SECOND + "';\n"
            + "    s4.addEventListener('error', function() {alert('d')}, false);\n"
            + "    document.body.insertBefore(s4, document.body.firstChild);\n"
            + "    \n"
            + "    var s5 = document.createElement('script');\n"
            + "    s5.src = '" + URL_THIRD + "';\n"
            + "    s5.addEventListener('error', function() {alert('e')}, false);\n"
            + "    document.body.insertBefore(s5, document.body.firstChild);\n"
            + "    \n"
            + "    var s6 = document.createElement('script');\n"
            + "    s6.src = '" + fourOhFour + "';\n"
            + "    s6.addEventListener('error', function() {alert('f')}, false);\n"
            + "    document.body.insertBefore(s6, document.body.firstChild);\n"
            + "  }\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'></body>\n"
            + "</html>";
        final WebClient client = getWebClient();
        client.getOptions().setThrowExceptionOnFailingStatusCode(throwOnFailingStatusCode);
        client.getOptions().setThrowExceptionOnScriptError(false);

        final MockWebConnection conn = new MockWebConnection();
        conn.setResponse(URL_FIRST, html);
        conn.setResponse(URL_SECOND, "var foo;", MimeType.TEXT_JAVASCRIPT);
        conn.setResponse(URL_THIRD, "varrrr foo;", MimeType.TEXT_JAVASCRIPT);
        conn.setResponse(fourOhFour, "", 404, "Missing", MimeType.TEXT_JAVASCRIPT, new ArrayList<>());
        client.setWebConnection(conn);
        final List<String> actual = new ArrayList<>();
        client.setAlertHandler(new CollectingAlertHandler(actual));
        client.getPage(URL_FIRST);
        assertEquals(getExpectedAlerts(), actual);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void isDisplayed() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head><title>Page A</title></head><body><script>var x = 1;</script></body></html>";
        final HtmlPage page = loadPageWithAlerts(html);
        final HtmlScript script = page.getFirstByXPath("//script");
        assertFalse(script.isDisplayed());
    }

    /**
     * Verifies that if a script element executes "window.location.href=someotherpage", then subsequent
     * script tags, and any onload handler for the original page do not run.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"First script executes", "Second page loading"})
    public void changingLocationSkipsFurtherScriptsOnPage() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head></head>\n"
            + "<body onload='alert(\"body onload executing but should be skipped\")'>\n"
            + "<script>alert('First script executes')</script>\n"
            + "<script>window.location.href='" + URL_SECOND + "'</script>\n"
            + "<script>alert('Third script executing but should be skipped')</script>\n"
            + "</body></html>";

        final String secondPage = DOCTYPE_HTML
            + "<html><head></head><body>\n"
            + "<script>alert('Second page loading')</script>\n"
            + "</body></html>";

        getMockWebConnection().setResponse(URL_SECOND, secondPage);
        loadPageWithAlerts(html);
    }
}

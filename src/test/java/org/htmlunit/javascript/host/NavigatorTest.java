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
package org.htmlunit.javascript.host;

import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.junit.annotation.HtmlUnitNYI;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Navigator}.
 *
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Daniel Gredler
 * @author Marc Guillemot
 * @author Ahmed Ashour
 * @author Frank Danek
 * @author Ronald Brill
 */
public class NavigatorTest extends WebDriverTestCase {

    /**
     * Tests the {@code appCodeName} property.
     * @throws Exception on test failure
     */
    @Test
    public void appCodeName() throws Exception {
        attribute("appCodeName", getBrowserVersion().getApplicationCodeName());
    }

    /**
     * Tests the {@code appMinorVersion} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void appMinorVersion() throws Exception {
        attribute("appMinorVersion", getExpectedAlerts()[0]);
    }

    /**
     * Tests the {@code appName} property.
     * @throws Exception on test failure
     */
    @Test
    public void appName() throws Exception {
        attribute("appName", getBrowserVersion().getApplicationName());
    }

    /**
     * Tests the {@code appVersion} property.
     * @throws Exception on test failure
     */
    @Test
    public void appVersion() throws Exception {
        attribute("appVersion", getBrowserVersion().getApplicationVersion(),
                "SLCC2; ", ".NET CLR 2.0.50727; ", ".NET CLR 3.5.30729; ", ".NET CLR 3.0.30729; ",
                "Media Center PC 6.0; ", ".NET4.0C; ", ".NET4.0E; ");
    }

    /**
     * Tests the {@code browserLanguage} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void browserLanguage() throws Exception {
        attribute("browserLanguage", getExpectedAlerts()[0]);
    }

    /**
     * Tests the {@code productSub} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts(DEFAULT = {"string", "20100101"},
            CHROME = {"string", "20030107"},
            EDGE = {"string", "20030107"})
    public void productSub() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  log(typeof(navigator.productSub));\n"
            + "  log(navigator.productSub);\n"
            + "</script>\n"
            + "</head><body></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Tests the {@code cpuClass} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void cpuClass() throws Exception {
        attribute("cpuClass", getExpectedAlerts()[0]);
    }

    /**
     * Tests the {@code onLine} property.
     * @throws Exception on test failure
     */
    @Test
    public void onLine() throws Exception {
        attribute("onLine", String.valueOf(getBrowserVersion().isOnLine()));
    }

    /**
     * Tests the {@code platform} property.
     * @throws Exception on test failure
     */
    @Test
    public void platform() throws Exception {
        attribute("platform", getBrowserVersion().getPlatform());
    }

    /**
     * Tests the {@code systemLanguage} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void systemLanguage() throws Exception {
        attribute("systemLanguage", getExpectedAlerts()[0]);
    }

    /**
     * Tests the {@code userAgent} property.
     * @throws Exception on test failure
     */
    @Test
    public void userAgent() throws Exception {
        attribute("userAgent", getBrowserVersion().getUserAgent(),
                "SLCC2; ", ".NET CLR 2.0.50727; ", ".NET CLR 3.5.30729; ", ".NET CLR 3.0.30729; ",
                "Media Center PC 6.0; ", ".NET4.0C; ", ".NET4.0E; ");
    }

    /**
     * Tests the {@code userLanguage} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("undefined")
    public void userLanguage() throws Exception {
        attribute("userLanguage", getExpectedAlerts()[0]);
    }

    /**
     * Tests the {@code plugins} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"5",
        "PDF Viewer", "Portable Document Format", "internal-pdf-viewer", "undefined",
        "Chrome PDF Viewer", "Portable Document Format", "internal-pdf-viewer", "undefined",
        "Chromium PDF Viewer", "Portable Document Format", "internal-pdf-viewer", "undefined",
        "Microsoft Edge PDF Viewer", "Portable Document Format", "internal-pdf-viewer", "undefined",
        "WebKit built-in PDF", "Portable Document Format", "internal-pdf-viewer", "undefined"})
    public void plugins() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function doTest() {\n"
                + "      log(window.navigator.plugins.length);\n"
                + "      for (var i = 0; i < window.navigator.plugins.length; i++) {\n"
                + "        let pl = window.navigator.plugins[i];\n"
                + "        log(pl.name);\n"
                + "        log(pl.description);\n"
                + "        log(pl.filename);\n"
                + "        log(pl.version);\n"
                + "      }\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='doTest()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Tests the Shockwave Flash plugin property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts("Shockwave Flash not available")
    public void pluginsShockwaveFlash() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "  function doTest() {\n"
                + "    var flash = false;\n"
                + "    for (var i = 0; i < window.navigator.plugins.length; i++) {\n"
                + "      var plugin = window.navigator.plugins[i];\n"
                + "      if ('Shockwave Flash' == window.navigator.plugins[i].name) {\n"
                + "        flash = true;\n"
                + "        log(plugin.name);\n"
                + "        log(plugin.description);\n"
                + "        log(plugin.version);\n"
                + "        log(plugin.filename);\n"
                + "      }\n"
                + "    }\n"
                + "    if (!flash) {\n"
                + "      log('Shockwave Flash not available');\n"
                + "    }\n"
                + "  }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='doTest()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"2",
             "application/pdf", "Portable Document Format", "pdf", "[object Plugin]", "true",
             "text/pdf", "Portable Document Format", "pdf", "[object Plugin]", "true"})
    public void mimeTypes() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function doTest() {\n"
                + "      log(window.navigator.mimeTypes.length);\n"
                + "      for (var i = 0; i < window.navigator.mimeTypes.length; i++) {\n"
                + "        let mt = window.navigator.mimeTypes[i];\n"
                + "        log(mt.type);\n"
                + "        log(mt.description);\n"
                + "        log(mt.suffixes);\n"
                + "        log(mt.enabledPlugin);\n"
                + "        log(mt.enabledPlugin === window.navigator.plugins[0]);"
                + "      }\n"
                + "    }\n"
                + "  </script>\n"
            + "</head><body onload='doTest()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Tests the {@code taintEnabled} property.
     * @throws Exception on test failure
     */
    @Test
    @Alerts(DEFAULT = "false",
            CHROME = "TypeError",
            EDGE = "TypeError")
    public void taintEnabled() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "  function doTest() {\n"
                + "    try {\n"
                + "      log(window.navigator.taintEnabled());\n"
                + "    } catch(e) { logEx(e); }\n"
                + "  }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='doTest()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Generic method for testing the value of a specific navigator attribute.
     * @param name the name of the attribute to test
     * @param value the expected value for the named attribute
     * @throws Exception on test failure
     */
    void attribute(final String name, final String value, final String... ignore) throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "  <script>\n"
                + LOG_TITLE_FUNCTION
                + "    function doTest() {\n"
                + "      log(window.navigator." + name + ");\n"
                + "    }\n"
                + "  </script>\n"
                + "</head>\n"
                + "<body onload='doTest()'>\n"
                + "</body>\n"
                + "</html>";

        setExpectedAlerts(value);
        loadPageVerifyTitle2(html);
    }

    /**
     * Test {@code language} property.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("en-US")
    public void language() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "  <script>\n"
            + LOG_TITLE_FUNCTION
            + "  </script>\n"
            + "</head>\n"
            + "<body onload='log(window.navigator.language)'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Test {@code language} property.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "en-US",
            FF = "en-US,en",
            FF_ESR = "en-US,en")
    @HtmlUnitNYI(CHROME = "en-US,en",
            EDGE = "en-US,en")
    public void languages() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "  <script>\n"
            + LOG_TITLE_FUNCTION
            + "  </script>\n"
            + "</head>\n"
            + "<body onload='log(window.navigator.languages)'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"number", "number"})
    public void mozilla() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(typeof window.navigator.mimeTypes.length);\n"
            + "  log(typeof window.navigator.plugins.length);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("Gecko")
    public void product() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(navigator.product);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("[object Geolocation]")
    public void geolocation() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(navigator.geolocation);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "undefined",
            FF = "20181001000000",
            FF_ESR = "20181001000000")
    public void buildID() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(navigator.buildID);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"Google Inc.", ""},
            FF = {"", ""},
            FF_ESR = {"", ""})
    public void vendor() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(navigator.vendor);\n"
            + "  log(navigator.vendorSub);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "false",
            FF = "true",
            FF_ESR = "true")
    public void oscpu() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  log(navigator.oscpu != undefined);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"undefined", "undefined", "undefined"},
            CHROME = {"[object NetworkInformation]", "undefined", "undefined"},
            EDGE = {"[object NetworkInformation]", "undefined", "undefined"})
    public void connection() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(navigator.connection);\n"
            + "    log(navigator.mozConnection);\n"
            + "    log(navigator.webkitConnection);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"unspecified", "undefined", "undefined"},
            CHROME = {"null", "undefined", "undefined"},
            EDGE = {"null", "undefined", "undefined"})
    public void doNotTrack() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(navigator.doNotTrack);\n"
            + "    log(navigator.msDoNotTrack);\n"
            + "    log(window.doNotTrack);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"[object MediaDevices]", "true"})
    public void mediaDevices() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(navigator.mediaDevices);\n"
            + "    log(navigator.mediaDevices === navigator.mediaDevices);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("true")
    public void pdfViewerEnabled() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(navigator.pdfViewerEnabled);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }
}

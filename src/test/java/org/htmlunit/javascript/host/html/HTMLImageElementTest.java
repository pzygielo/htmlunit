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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.htmlunit.MockWebConnection;
import org.htmlunit.WebDriverTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.junit.annotation.BuggyWebDriver;
import org.htmlunit.junit.annotation.HtmlUnitNYI;
import org.htmlunit.util.MimeType;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Tests for {@link HTMLImageElement}.
 *
 * @author <a href="mailto:george@murnock.com">George Murnock</a>
 * @author Marc Guillemot
 * @author Ronald Brill
 * @author Frank Danek
 * @author Ahmed Ashour
 */
public class HTMLImageElementTest extends WebDriverTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"[object HTMLImageElement]", "[object HTMLImageElement]"})
    public void simpleScriptable() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.getElementById('myId1'));\n"
            + "    log(document.getElementById('myId2'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myId1'>\n"
            + "  <image id='myId2'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"IMG", "IMG"})
    public void nodeName() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.getElementById('myId1').nodeName);\n"
            + "    log(document.getElementById('myId2').nodeName);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myId1'>\n"
            + "  <image id='myId2'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"IMG", "IMG"})
    public void tagName() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.getElementById('myId1').tagName);\n"
            + "    log(document.getElementById('myId2').tagName);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myId1'>\n"
            + "  <image id='myId2'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HTMLImageElement]", "[object HTMLUnknownElement]", "IMG", "IMAGE",
                       "[object HTMLImageElement]", "[object HTMLImageElement]", "IMG", "IMG"},
            FF = {"[object HTMLImageElement]", "[object HTMLElement]", "IMG", "IMAGE",
                  "[object HTMLImageElement]", "[object HTMLImageElement]", "IMG", "IMG"},
            FF_ESR = {"[object HTMLImageElement]", "[object HTMLElement]", "IMG", "IMAGE",
                      "[object HTMLImageElement]", "[object HTMLImageElement]", "IMG", "IMG"})
    public void image() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.createElement('img'));\n"
            + "    log(document.createElement('image'));\n"
            + "    log(document.createElement('img').nodeName);\n"
            + "    log(document.createElement('image').nodeName);\n"
            + "    log(document.getElementById('myId1'));\n"
            + "    log(document.getElementById('myId2'));\n"
            + "    log(document.getElementById('myId1').nodeName);\n"
            + "    log(document.getElementById('myId2').nodeName);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myId1'>\n"
            + "  <image id='myId2'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"", "undefined", "", ""})
    public void src() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    log(document.createElement('img').src);\n"
            + "    log(document.createElement('image').src);\n"
            + "    log(document.getElementById('myId1').src);\n"
            + "    log(document.getElementById('myId2').src);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myId1'>\n"
            + "  <image id='myId2'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * This test verifies that JavaScript can be used to get the <tt>src</tt> attribute of an <tt>&lt;img&gt;</tt> tag.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("§§URL§§foo.gif")
    public void getSrc() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  log(document.getElementById('anImage').src);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "<img src='foo.gif' id='anImage'/>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse(""); // to have a dummy response for the image
        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("")
    public void getSrc_newImage_srcNotSet() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  var oImg = new Image();\n"
            + "  log(oImg.src);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * This test verifies that when JavaScript is used to modify the <tt>src</tt> attribute, the value is
     * persisted to the corresponding <tt>&lt;img&gt;</tt> node in the DOM tree.
     * @throws Exception if the test fails
     */
    @Test
    public void setSrc() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><script>\n"
            + "function doTest() {\n"
            + "  document.getElementById('anImage').src = 'bar.gif';\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "<img src='foo.gif' id='anImage'/>\n"
            + "</body></html>";
        getMockWebConnection().setDefaultResponse("Error: not found", 404, "Not Found", MimeType.TEXT_HTML);

        final WebDriver driver = loadPage2(html);
        final WebElement image = driver.findElement(By.id("anImage"));
        assertEquals(URL_FIRST + "bar.gif", image.getAttribute("src"));
    }

    /**
     * JavaScript can be used to preload images, as follows:
     * <code>var newImage = new Image(); newImage.src = 'foo.gif';</code>.
     * When <code>new Image()</code> is called, HtmlUnit creates a new JavaScript
     * Image object. However, no corresponding DOM node is created, which is
     * just as well, since browsers don't create one either.
     * This test verifies that the above JavaScript code can be invoked without
     * throwing an "IllegalStateException: DomNode has not been set for this
     * SimpleScriptable."
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"onLoad", "§§URL§§bar.gif"})
    public void setSrc_newImage() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  var preloadImage = new Image();\n"
            + "  preloadImage.onload = log('onLoad');\n"
            + "  preloadImage.src = 'bar.gif';\n"
            + "  log(preloadImage.src);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse(""); // to have a dummy response for the image
        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("foo")
    public void attributeName() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function test() {\n"
            + "  var oImg = document.getElementById('myImage');\n"
            + "  oImg.name = 'foo';\n"
            + "  log(oImg.name);\n"
            + "}\n"
            + "</script></head><body onload='test()'>\n"
            + "<img src='foo.png' id='myImage'>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse(""); // to have a dummy response for the image
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void onLoad_BadUrl() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html><body><img src='http:// [/url]http://x.com/a/b' onload='alert(1)'/></body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"true", "relative", "", ""})
    public void newImage() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "function doTest() {\n"
            + "  var i = new Image();\n"
            + "  log(i.style != null);\n"
            + "  i.style.position = 'relative';\n"
            + "  log(i.style.position);\n"
            + "  log(i.border);\n"
            + "  log(i.alt);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(DEFAULT = {"left", "right", "center", "justify", "bottom", "middle",
                       "top", "absbottom", "absmiddle", "baseline", "texttop", "wrong", ""},
            FF = {"left", "right", "middle", "justify", "bottom", "middle",
                  "top", "absbottom", "absmiddle", "bottom", "texttop", "wrong", ""},
            FF_ESR = {"left", "right", "middle", "justify", "bottom", "middle",
                      "top", "absbottom", "absmiddle", "bottom", "texttop", "wrong", ""})
    @HtmlUnitNYI(FF = {"left", "right", "center", "justify", "bottom", "middle",
                       "top", "absbottom", "absmiddle", "baseline", "texttop", "wrong", ""},
            FF_ESR = {"left", "right", "center", "justify", "bottom", "middle",
                      "top", "absbottom", "absmiddle", "baseline", "texttop", "wrong", ""})
    public void getAlign() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "  <img id='i1' align='left' />\n"
            + "  <img id='i2' align='right' />\n"
            + "  <img id='i3' align='center' />\n"
            + "  <img id='i4' align='justify' />\n"
            + "  <img id='i5' align='bottom' />\n"
            + "  <img id='i6' align='middle' />\n"
            + "  <img id='i7' align='top' />\n"
            + "  <img id='i8' align='absbottom' />\n"
            + "  <img id='i9' align='absmiddle' />\n"
            + "  <img id='i10' align='baseline' />\n"
            + "  <img id='i11' align='texttop' />\n"
            + "  <img id='i12' align='wrong' />\n"
            + "  <img id='i13' />\n"

            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  for (var i = 1; i <= 13; i++) {\n"
            + "    log(document.getElementById('i' + i).align);\n"
            + "  }\n"
            + "</script>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(DEFAULT = {"CenTer", "8", "foo", "left", "right", "center", "justify",
                       "bottom", "middle", "top", "absbottom", "absmiddle", "baseline", "texttop"},
            FF = {"CenTer", "8", "foo", "left", "right", "middle", "justify",
                  "bottom", "middle", "top", "absbottom", "absmiddle", "bottom", "texttop"},
            FF_ESR = {"CenTer", "8", "foo", "left", "right", "middle", "justify",
                      "bottom", "middle", "top", "absbottom", "absmiddle", "bottom", "texttop"})
    @HtmlUnitNYI(FF = {"CenTer", "8", "foo", "left", "right", "center", "justify",
                       "bottom", "middle", "top", "absbottom", "absmiddle", "baseline", "texttop"},
            FF_ESR = {"CenTer", "8", "foo", "left", "right", "center", "justify",
                      "bottom", "middle", "top", "absbottom", "absmiddle", "baseline", "texttop"})
    public void setAlign() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "  <img id='i1' align='left' />\n"

            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function setAlign(elem, value) {\n"
            + "    try {\n"
            + "      elem.align = value;\n"
            + "    } catch(e) { logEx(e); }\n"
            + "    log(elem.align);\n"
            + "  }\n"

            + "  var elem = document.getElementById('i1');\n"
            + "  setAlign(elem, 'CenTer');\n"

            + "  setAlign(elem, '8');\n"
            + "  setAlign(elem, 'foo');\n"

            + "  setAlign(elem, 'left');\n"
            + "  setAlign(elem, 'right');\n"
            + "  setAlign(elem, 'center');\n"
            + "  setAlign(elem, 'justify');\n"
            + "  setAlign(elem, 'bottom');\n"
            + "  setAlign(elem, 'middle');\n"
            + "  setAlign(elem, 'top');\n"
            + "  setAlign(elem, 'absbottom');\n"
            + "  setAlign(elem, 'absmiddle');\n"
            + "  setAlign(elem, 'baseline');\n"
            + "  setAlign(elem, 'texttop');\n"
            + "</script>\n"
            + "</body></html>";
        loadPageVerifyTitle2(html);
    }

    /**
     * Test image's width and height.
     * Regression test for bug
     * <a href="http://sourceforge.net/tracker/?func=detail&atid=448266&aid=2861064&group_id=47038">issue 915</a>
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"number: 300", "number: 200", "number: 0", "number: 0", "number: 0", "number: 0"})
    public void widthHeightWithoutSource() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(typeof(img.width) + ': ' + img.width);\n"
            + "    log(typeof(img.height) + ': ' + img.height);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' width='300' height='200'>\n"
            + "  <img id='myImage2'>\n"
            + "  <img id='myImage3' width='hello' height='hello'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Test that image's width and height are numbers.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"number: 300", "number: 200", "number: 1", "number: 1", "number: 1", "number: 1"})
    public void widthHeightWithSource() throws Exception {
        getMockWebConnection().setDefaultResponse("");

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(typeof(img.width) + ': ' + img.width);\n"
            + "    log(typeof(img.height) + ': ' + img.height);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' src='" + URL_SECOND + "' width='300' height='200'>\n"
            + "  <img id='myImage2' src='" + URL_SECOND + "' >\n"
            + "  <img id='myImage3' src='" + URL_SECOND + "' width='hello' height='hello'>\n"
            + "</body></html>";

        final URL url = getClass().getClassLoader().getResource("testfiles/tiny-jpg.img");
        try (FileInputStream fis = new FileInputStream(new File(url.toURI()))) {
            final byte[] directBytes = IOUtils.toByteArray(fis);

            final MockWebConnection webConnection = getMockWebConnection();
            webConnection.setResponse(URL_SECOND, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        loadPageVerifyTitle2(html);
    }

    /**
     * Test that image's width and height are numbers.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"number: 300", "number: 200", "number: 0", "number: 0", "number: 0", "number: 0"})
    public void widthHeightEmptySource() throws Exception {
        getMockWebConnection().setDefaultResponse("");

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(typeof(img.width) + ': ' + img.width);\n"
            + "    log(typeof(img.height) + ': ' + img.height);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' src='' width='300' height='200'>\n"
            + "  <img id='myImage2' src='' >\n"
            + "  <img id='myImage3' src='' width='hello' height='hello'>\n"
            + "</body></html>";

        final URL url = getClass().getClassLoader().getResource("testfiles/tiny-jpg.img");
        try (FileInputStream fis = new FileInputStream(new File(url.toURI()))) {
            final byte[] directBytes = IOUtils.toByteArray(fis);
            final MockWebConnection webConnection = getMockWebConnection();
            webConnection.setResponse(URL_SECOND, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        loadPageVerifyTitle2(html);
    }

    /**
     * Test that image's width and height are numbers.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"number: 300", "number: 200", "number: 24", "number: 24", "number: 24", "number: 24"},
            CHROME = {"number: 300", "number: 200", "number: 0", "number: 0", "number: 0", "number: 0"},
            EDGE = {"number: 300", "number: 200", "number: 0", "number: 0", "number: 0", "number: 0"})
    public void widthHeightBlankSource() throws Exception {
        getMockWebConnection().setDefaultResponse("");

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(typeof(img.width) + ': ' + img.width);\n"
            + "    log(typeof(img.height) + ': ' + img.height);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' src=' ' width='300' height='200'>\n"
            + "  <img id='myImage2' src=' ' >\n"
            + "  <img id='myImage3' src=' ' width='hello' height='hello'>\n"
            + "</body></html>";

        final URL url = getClass().getClassLoader().getResource("testfiles/tiny-jpg.img");
        try (FileInputStream fis = new FileInputStream(new File(url.toURI()))) {
            final byte[] directBytes = IOUtils.toByteArray(fis);
            final MockWebConnection webConnection = getMockWebConnection();
            webConnection.setResponse(URL_SECOND, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        loadPageVerifyTitle2(html);
    }

    /**
     * Test that image's width and height are numbers.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"number: 300", "number: 200", "number: 24", "number: 24", "number: 24", "number: 24"},
            CHROME = {"number: 300", "number: 200", "number: 16", "number: 16", "number: 16", "number: 16"},
            EDGE = {"number: 300", "number: 200", "number: 16", "number: 16", "number: 16", "number: 16"})
    public void widthHeightInvalidSource() throws Exception {
        getMockWebConnection().setDefaultResponse("");

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(typeof(img.width) + ': ' + img.width);\n"
            + "    log(typeof(img.height) + ': ' + img.height);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' src='" + URL_SECOND + "' width='300' height='200'>\n"
            + "  <img id='myImage2' src='" + URL_SECOND + "' >\n"
            + "  <img id='myImage3' src='" + URL_SECOND + "' width='hello' height='hello'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"true", "true", "true", "true"})
    public void complete() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            final URL urlImage = new URL(URL_SECOND, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
            getMockWebConnection().setDefaultResponse("Test");
        }

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function showInfo(imageId) {\n"
            + "    var img = document.getElementById(imageId);\n"
            + "    log(img.complete);\n"
            + "  }\n"
            + "  function test() {\n"
            + "    showInfo('myImage1');\n"
            + "    showInfo('myImage2');\n"
            + "    showInfo('myImage3');\n"
            + "    showInfo('myImage4');\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage1' >\n"
            + "  <img id='myImage2' src=''>\n"
            + "  <img id='myImage3' src='" + URL_SECOND + "'>\n"
            + "  <img id='myImage4' src='" + URL_SECOND + "img.jpg'>\n"
            + "</body></html>";
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"error2;error3;error4;load5;", "3"},
            FF = {"error2;error3;error4;load5;", "4"},
            FF_ESR = {"error2;error3;error4;load5;", "4"})
    // at the moment we do not check the image content
    @HtmlUnitNYI(CHROME = {"error2;error3;load4;load5;", "3"},
            EDGE = {"error2;error3;load4;load5;", "3"},
            FF = {"error2;load3;load4;load5;", "4"},
            FF_ESR = {"error2;load3;load4;load5;", "4"})
    public void onload() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            final URL urlImage = new URL(URL_SECOND, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
            getMockWebConnection().setResponse(URL_SECOND, "Test", 200, "OK",
                    MimeType.TEXT_HTML, Collections.emptyList());
            getMockWebConnection().setDefaultResponse("Error: not found", 404, "Not Found", MimeType.TEXT_HTML);
        }

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + "  function showInfo(text) {\n"
            + "    document.title += text + ';';\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body>\n"
            + "  <img id='myImage1' onload='showInfo(\"load1\")' onerror='showInfo(\"error1\")'>\n"
            + "  <img id='myImage2' src='' onload='showInfo(\"load2\")' onerror='showInfo(\"error2\")'>\n"
            + "  <img id='myImage3' src='  ' onload='showInfo(\"load3\")' onerror='showInfo(\"error3\")'>\n"
            + "  <img id='myImage4' src='" + URL_SECOND + "' onload='showInfo(\"load4\")' "
                    + "onerror='showInfo(\"error4\")'>\n"
            + "  <img id='myImage5' src='" + URL_SECOND + "img.jpg' onload='showInfo(\"load5\")' "
                    + "onerror='showInfo(\"error5\")'>\n"
            + "</body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"load;", "2"})
    public void emptyMimeType() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            final URL urlImage = new URL(URL_SECOND, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "", Collections.emptyList());
            getMockWebConnection().setDefaultResponse("Test");
        }

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + "  function showInfo(text) {\n"
            + "    document.title += text + ';';\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body>\n"
            + "  <img id='myImage5' src='" + URL_SECOND + "img.jpg' onload='showInfo(\"load\")' "
                    + "onerror='showInfo(\"error\")'>\n"
            + "</body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"load;", "2"})
    public void wrongMimeType() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            final URL urlImage = new URL(URL_SECOND, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "text/html", Collections.emptyList());
            getMockWebConnection().setDefaultResponse("Test");
        }

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + "  function showInfo(text) {\n"
            + "    document.title += text + ';';\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body>\n"
            + "  <img id='myImage5' src='" + URL_SECOND + "img.jpg' onload='showInfo(\"load\")' "
                    + "onerror='showInfo(\"error\")'>\n"
            + "</body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"test", "string", "hui", "", "null", "false", "true", ""})
    public void alt() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function test() {\n"
            + "        var testImg = document.getElementById('myImage');\n"
            + "        log(testImg.alt);\n"
            + "        log(typeof testImg.alt);\n"

            + "        testImg.alt = 'hui';\n"
            + "        log(testImg.alt);\n"

            + "        testImg.alt = '';\n"
            + "        log(testImg.alt);\n"

            + "        testImg.alt = null;\n"
            + "        log(testImg.alt);\n"
            + "        log(testImg.alt === null);\n"
            + "        log(testImg.alt === 'null');\n"

            + "        var testImg = document.getElementById('myImageWithoutAlt');\n"
            + "        log(testImg.alt);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <img id='myImage' src='" + URL_SECOND + "' alt='test'>\n"
            + "    <img id='myImageWithoutAlt' src='" + URL_SECOND + "'>\n"
            + "  </body>\n"
            + "</html>";
        getMockWebConnection().setDefaultResponse("Error: not found", 404, "Not Found", MimeType.TEXT_HTML);

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"myImage clicked", "myImageNone clicked"})
    public void click() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    document.getElementById('myImage').click();\n"
            + "    document.getElementById('myImageNone').click();\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImage' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAA"
                                    + "HElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=='"
                            + " onclick='log(\"myImage clicked\");'>\n"
            + "  <img id='myImageNone' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAA"
                                    + "HElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=='"
                            + " style='display: none' onclick='log(\"myImageNone clicked\");'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("myImageWithMap clicked")
    public void clickWithMap() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "<script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    document.getElementById('myImageWithMap').click();\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <img id='myImageWithMap' usemap='#dot'"
                                + " src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAA"
                                + "HElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=='"
                            + " onclick='log(\"myImageWithMap clicked\");'>\n"
                + "  <map name='dot'>\n"
                + "    <area id='a0' shape='rect' coords='0 0 7 7' onclick='log(\"a0 clicked\");'/>\n"
                + "    <area id='a1' shape='rect' coords='0,0,1,1' onclick='log(\"a1 clicked\");'/>\n"
                + "  <map>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;", "2"})
    public void img_download() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);
            final URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var i = new Image();\n"
            + "  i.src = 'img.jpg';\n"
            + "  document.title += 'done;';\n"
            + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;onload;", "2"})
    public void img_download_onloadBefore() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);
            final URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var i = new Image();\n"
            + "  i.onload = function() { document.title += 'onload;'; };\n"
            + "  i.src = 'img.jpg';\n"
            + "  document.title += 'done;';\n"
            + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;onload;", "2"})
    public void img_download_onloadAfter() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);
            final URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var i = new Image();\n"
            + "  i.src = 'img.jpg';\n"
            + "  document.title += 'done;';\n"
            + "  i.onload = function() { document.title += 'onload;'; };\n"
            + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;onload;", "2"})
    public void img_onLoad_calledWhenImageDownloaded_dynamic() throws Exception {
        // this seems to need a fresh browser to pass
        shutDownAll();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());

            urlImage = new URL(URL_FIRST, "img2.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var i = new Image();\n"
            + "  i.src = 'img.jpg';\n"
            + "  i.src = 'img2.jpg';\n"
            + "  document.title += 'done;';\n"
            + "  i.onload = function() { document.title += 'onload;'; };\n"
            + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img2.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;onload;", "2"})
    public void img_onLoad_calledWhenImageDownloaded_dynamic2() throws Exception {
        // this seems to need a fresh browser to pass
        shutDownAll();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());

            urlImage = new URL(URL_FIRST, "img2.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + "<script>\n"
            + "  var i = new Image();\n"
            + "  i.onload = function() { document.title += 'onload;'; };\n"
            + "  i.src = 'img.jpg';\n"
            + "  i.src = 'img2.jpg';\n"
            + "  document.title += 'done;';\n"
            + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img2.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image is created if the page is already
     * finished, the onload handler is called.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"done;onload;onload2;", "3"})
    public void img_onLoad_calledWhenImageDownloaded_dynamic_twoSteps() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());

            urlImage = new URL(URL_FIRST, "img2.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
                + "<html><body>\n"
                + "<script>\n"
                + "  var i = new Image();\n"
                + "  i.src = 'img.jpg';\n"
                + "  i.onload = function() {\n"
                + "    document.title += 'onload;';\n"
                + "    var i2 = document.createElement('img');\n"
                + "    i2.src = 'img2.jpg';\n"
                + "    i2.onload = function() {\n"
                + "      document.title += 'onload2;';\n"
                + "    };\n"
                + "  };\n"
                + "  document.title += 'done;';\n"
                + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);
        assertEquals(URL_FIRST + "img2.jpg", getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute set from a script, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"image one;image two;", "3"})
    public void img_onLoad_calledWhenImageDownloaded_mixed() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());

            urlImage = new URL(URL_FIRST, "img2.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
                + "<html><body><img id='img' name='img'/><script>\n"
                + "  var i = new Image();\n"
                + "  i.onload = function() {\n"
                + "    document.title += 'image one;';\n"
                + "    i.onload = function() {\n"
                + "      document.title += 'image two;';\n"
                + "    };\n"
                + "    i.src = 'img2.jpg';\n"
                + "  };\n"
                + "  i.setAttribute('src','img.jpg');\n"
                + "  var t = setTimeout(function() {clearTimeout(t);}, 500);\n"
                + "</script></body></html>";

        final int count = getMockWebConnection().getRequestCount();
        final WebDriver driver = getWebDriver();
        if (driver instanceof HtmlUnitDriver) {
            ((HtmlUnitDriver) driver).setDownloadImages(true);
        }
        loadPage2(html);

        assertTitle(driver, getExpectedAlerts()[0]);
        assertEquals(Integer.parseInt(getExpectedAlerts()[1]), getMockWebConnection().getRequestCount() - count);

        final List<String> requestedUrls = getMockWebConnection().getRequestedUrls(URL_FIRST);
        assertEquals("", requestedUrls.get(0));
        assertEquals("img.jpg", requestedUrls.get(1));
        assertEquals("img2.jpg", requestedUrls.get(2));
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("[object HTMLImageElement]")
    public void ctorImage() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function doTest() {\n"
            + "        var htmlImageElement = new Image(1, 1);"
            + "        log('' + htmlImageElement);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='doTest()'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("TypeError")
    public void ctorHTMLImageElement() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function doTest() {\n"
            + "        try {\n"
            + "          var htmlImageElement = new HTMLImageElement(1, 1);"
            + "          log('' + htmlImageElement);\n"
            + "        } catch(e) { logEx(e); }\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='doTest()'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("{\"enumerable\":true,\"configurable\":true}")
    public void imagePrototype() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + LOG_TITLE_FUNCTION
            + "      function doTest() {\n"
            + "        var desc = Object.getOwnPropertyDescriptor(Image.prototype, 'src');"
            + "        log(JSON.stringify(desc));\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='doTest()'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0", "1"})
    public void onLoad_calledWhenImageDownloaded_static() throws Exception {
        final URL urlImage = new URL(URL_FIRST, "img.jpg");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<body>\n"
                + "  <img src='img.jpg' onload='test()'>\n"
                + LOG_TEXTAREA

                + "  <script>\n"
                + LOG_TEXTAREA_FUNCTION
                    // first script to be sure that img onload doesn't get executed after first JS execution
                + "    log(0);\n"
                + "  </script>\n"
                + "  <script>\n"
                + "    function test() {\n"
                + "      log(1);\n"
                + "    }\n"
                + "  </script>\n"
                + "</body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(urlImage, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("1")
    public void onLoad_calledWhenImageDownloaded_dynamic() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
            getMockWebConnection().setResponse(URL_THIRD, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  var i = document.createElement('img');\n"
            + "  i.src = '" + URL_SECOND + "';\n"
            + "  i.src = '" + URL_THIRD + "';\n"
            + "  i.onload = function() { log(1); };\n"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(URL_THIRD, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("1")
    public void onLoad_calledWhenImageDownloaded_dynamic_onLoad_already_set() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  var i = document.createElement('img');\n"
            + "  i.onload = function() { log(1); };\n"
            + "  i.src = '" + URL_SECOND + "';\n"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(URL_SECOND, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image is created if the page is already
     * finished, the onload handler is called.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"1", "2"})
    public void onLoad_calledWhenImageDownloaded_dynamic_twoSteps() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
            getMockWebConnection().setResponse(URL_THIRD, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  var i = document.createElement('img');\n"
            + "  i.src = '" + URL_SECOND + "';\n"
            + "  i.onload = function() {\n"
            + "    log(1);\n"
            + "    var i2 = document.createElement('img');\n"
            + "    i2.src = '" + URL_THIRD + "';\n"
            + "    i2.onload = function() {\n"
            + "      log(2);\n"
            + "    };\n"
            + "  };\n"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(URL_THIRD, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute set from a script, it gets downloaded
     * and the <tt>onload</tt> handler gets invoked.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"image one", "image two"})
    public void onLoad_calledWhenImageDownloaded_mixed() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
            getMockWebConnection().setResponse(URL_THIRD, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<img id='img' name='img'/>\n"
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  var i = document.getElementById('img');\n"
            + "  i.onload = function() {\n"
            + "    log('image one');\n"
            + "    i.onload = function() {\n"
            + "      log('image two');\n"
            + "    };\n"
            + "    i.src = '" + URL_THIRD + "';\n"
            + "  };\n"
            + "  i.setAttribute('src','" + URL_SECOND + "');\n"
            + "  var t = setTimeout(function() {clearTimeout(t);}, 500);\n"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);

        final List<String> requestedUrls = getMockWebConnection().getRequestedUrls(URL_FIRST);
        assertEquals(requestedUrls.size(), 3);
        assertEquals("", requestedUrls.get(0));
        assertEquals("second/", requestedUrls.get(1));
        assertEquals(URL_THIRD.toString(), requestedUrls.get(2));
        assertEquals(URL_THIRD, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * Verifies that if an image has an <tt>onload</tt> attribute, the <tt>onload</tt> handler
     * does not get invoked if we can't download the image.
     * @throws Exception if an error occurs
     */
    @Test
    public void onLoad_notCalledWhenImageNotDownloaded() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "</script>\n"
            + "<img src='" + URL_SECOND + "' onload='log(1)'>\n"
            + "</body></html>";

        final MockWebConnection conn = getMockWebConnection();
        conn.setResponse(URL_SECOND, "foo", 404, "Not Found", MimeType.TEXT_HTML, new ArrayList<>());

        loadPageVerifyTextArea2(html);
        assertEquals(URL_SECOND, conn.getLastWebRequest().getUrl());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0", "2", "1"})
    public void onLoad_order() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  log('0');"
            + "  var i = document.createElement('img');\n"
            + "  i.onload = function() {\n"
            + "    log(1);\n"
            + "  };\n"
            + "  i.src = '" + URL_SECOND + "';\n"
            + "  log('2');"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(URL_SECOND, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"0", "2", "1"})
    public void onLoad_orderNotAttached() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><body>\n"
            + LOG_TEXTAREA
            + "<script>\n"
            + LOG_TEXTAREA_FUNCTION
            + "  log('0');"
            + "  var i = new Image();\n"
            + "  i.onload = function() {\n"
            + "    log(1);\n"
            + "  };\n"
            + "  i.src = '" + URL_SECOND + "';\n"
            + "  log('2');"
            + "</script></body></html>";

        loadPageVerifyTextArea2(html);
        assertEquals(URL_SECOND, getMockWebConnection().getLastWebRequest().getUrl());
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"in", "out", "Image.onload(0)",
             "mousedown", "in", "out", "Image.onload(1)",
             "mouseup", "in", "out",
             "click", "in", "out", "Image.onload(2)", "Image.onload(3)"})
    @BuggyWebDriver({"in", "out", "Image.onload(0)",
        "mousedown", "in", "out",
        "mouseup", "in", "out",
        "click", "in", "out", "Image.onload(1)", "Image.onload(2)", "Image.onload(3)"})
    public void onload_complex() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);

            getMockWebConnection().setResponse(URL_SECOND, directBytes, 200, "ok",
                    MimeType.IMAGE_JPEG, Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head>\n"
                + "<script>\n"
                + LOG_TEXTAREA_FUNCTION

                + "  function test(i) {\n"
                + "    log('in');\n"
                + "    var image = new Image();\n"
                + "    image.onload = function () { log(\"Image.onload(\" + i + \")\") };\n"
                + "    image.src = '" + URL_SECOND + "';\n"
                + "    log('out');\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload=\"test(0)\">\n"
                + "<button id='myId'"
                +           "onmousedown=\"log('mousedown'); test(1)\" "
                +           "onmouseup=\"log('mouseup'); test(2)\" "
                +           "onclick=\"log('click'); test(3)\"></button>\n"
                + LOG_TEXTAREA
                + "</body>\n"
                + "</html>\n";

        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("myId")).click();
        verifyTextArea2(driver, getExpectedAlerts());
    }
}

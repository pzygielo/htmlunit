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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.htmlunit.MockWebConnection;
import org.htmlunit.SimpleWebTestCase;
import org.htmlunit.junit.annotation.Alerts;
import org.htmlunit.util.NameValuePair;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HtmlImageInput}.
 *
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Marc Guillemot
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
public class HtmlImageInput2Test extends SimpleWebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"button.x#100", "button.y#200"})
    public void click_WithPosition() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><title>foo</title></head><body>\n"
            + "<form id='form1' method='post'>\n"
            + "<input type='image' name='aButton' value='foo'/>\n"
            + "<input type='image' name='button' value='foo'/>\n"
            + "<input type='image' name='anotherButton' value='foo'/>\n"
            + "</form></body></html>";
        final HtmlPage page = loadPage(html);
        final MockWebConnection webConnection = getMockConnection(page);

        final HtmlForm form = page.getHtmlElementById("form1");

        final HtmlImageInput imageInput = form.getInputByName("button");
        final HtmlPage secondPage = imageInput.click(100, 200);
        assertNotNull(secondPage);

        final List<String> params = new ArrayList<>();
        for (final NameValuePair nameValuePair : webConnection.getLastParameters()) {
            params.add(nameValuePair.getName() + "#" + nameValuePair.getValue());
        }
        assertEquals(getExpectedAlerts(), params);
    }

    /**
     * If an image button without name is clicked, it should send only "x" and "y" parameters.
     * Regression test for bug #217.
     * @throws Exception if the test fails
     */
    @Test
    public void noNameClick_WithPosition() throws Exception {
        final String html = DOCTYPE_HTML
            + "<html><head><title>foo</title></head><body>\n"
            + "<form id='form1' method='post'>\n"
            + "<input type='image' value='foo'/>\n"
            + "</form></body></html>";
        final HtmlPage page = loadPageWithAlerts(html);
        final MockWebConnection webConnection = getMockConnection(page);

        final HtmlForm form = page.getHtmlElementById("form1");

        final HtmlImageInput imageInput = form.getInputByValue("foo");
        final HtmlPage secondPage = imageInput.click(100, 200);
        assertNotNull(secondPage);

        final List<NameValuePair> expectedPairs = Arrays.asList(new NameValuePair[]{
            new NameValuePair("x", "100"),
            new NameValuePair("y", "200")
        });

        assertEquals(expectedPairs, webConnection.getLastParameters());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void saveAs() throws Exception {
        try (InputStream is = getClass().getClassLoader().
                getResourceAsStream("testfiles/tiny-jpg.img")) {
            final byte[] directBytes = IOUtils.toByteArray(is);
            final URL urlImage = new URL(URL_FIRST, "img.jpg");
            getMockWebConnection().setResponse(urlImage, directBytes, 200, "ok", "image/jpg", Collections.emptyList());
        }

        final String html = DOCTYPE_HTML
            + "<html><head>\n"
            + "</head>\n"
            + "<body>\n"
            + "  <input type='image' src='img.jpg' >\n"
            + "</body></html>";

        final HtmlPage page = loadPage(html);

        final HtmlImageInput input = page.querySelector("input");
        final File tempFile = File.createTempFile("img", ".tmp");
        input.saveAs(tempFile);
        FileUtils.deleteQuietly(tempFile);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts({"4x7.jpg", "§§URL§§4x7.jpg"})
    public void src() throws Exception {
        final String html = DOCTYPE_HTML
                + "<html>\n"
                + "<head></head>\n"
                + "<body>\n"
                + "  <input type='image' id='myImage' src='4x7.jpg' >\n"
                + "</body></html>";

        final HtmlPage page = loadPage(html);
        final HtmlImageInput img = page.getHtmlElementById("myImage");

        expandExpectedAlertsVariables(URL_FIRST);
        assertEquals(getExpectedAlerts()[0], img.getSrcAttribute());
        assertEquals(getExpectedAlerts()[1], img.getSrc());
    }
}

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
package org.htmlunit.doc;

import org.htmlunit.BrowserVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for code from the documentation.
 *
 * @author Ronald Brill
 */
public class DocumentationTests {

    /**
     * See {@link BrowserVersion} class comment.
     * @throws Exception if the test fails
     */
    @Test
    public void binary() throws Exception {
        final String applicationName = "APPNAME";
        final String applicationVersion = "APPVERSION";
        final String userAgent = "USERAGENT";

        final BrowserVersion browser =
                new BrowserVersion.BrowserVersionBuilder(BrowserVersion.FIREFOX_ESR)
                    .setApplicationName(applicationName)
                    .setApplicationVersion(applicationVersion)
                    .setUserAgent(userAgent)
                    .build();

        Assertions.assertEquals(applicationName, browser.getApplicationName());
        Assertions.assertEquals(applicationVersion, browser.getApplicationVersion());
        Assertions.assertEquals(userAgent, browser.getUserAgent());
    }
}

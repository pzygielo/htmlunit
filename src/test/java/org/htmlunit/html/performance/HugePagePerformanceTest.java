/*
 * Copyright (c) 2002-2024 Gargoyle Software Inc.
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
package org.htmlunit.html.performance;

import java.io.IOException;
import java.net.URL;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;

/**
 * Tests for handling huge content.
 *
 * @author Ronald Brill
 */
public final class HugePagePerformanceTest {

    private HugePagePerformanceTest() {
    }

    public static void main(final String[] args) throws IOException {
        final URL fileURL = WebClient.class.getClassLoader()
                .getResource("testfiles/huge-pages/html-standard-2024-10-17.html");

        try (WebClient webClient = new WebClient(BrowserVersion.CHROME, false, null, -1)) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            final long start = System.currentTimeMillis();
            for (int i = 0; i < 20; i++) {
                webClient.getPage(fileURL);
                System.out.println("  " + i);
            }
            System.out.println("## " + (System.currentTimeMillis() - start));
        }
    }
}
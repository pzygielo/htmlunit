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
package org.htmlunit.attachment;

import org.htmlunit.HttpHeader;
import org.htmlunit.Page;
import org.htmlunit.WebResponse;

/**
 * An attachment represents a page received from the server which contains a
 * {@code Content-Disposition=attachment} header.
 *
 * @author Bruce Chapman
 * @author Sudhan Moghe
 * @author Daniel Gredler
 * @author Ronald Brill
 * @author Lai Quang Duong
 */
public class Attachment {

    /** The attached page. */
    private final Page page_;

    /** The file name of this attachment. */
    private final String attachmentFilename_;

    /**
     * Creates a new attachment for the specified page.
     * @param page the attached page
     */
    public Attachment(final Page page) {
        this(page, null);
    }

    /**
     * Creates a new attachment for the specified page.
     * @param page the attached page
     * @param attachmentFilename the file name of this attachment
     */
    public Attachment(final Page page, final String attachmentFilename) {
        page_ = page;
        attachmentFilename_ = attachmentFilename;
    }

    /**
     * Returns the attached page.
     * @return the attached page
     */
    public Page getPage() {
        return page_;
    }

    /**
     * Returns the attachment's filename, as suggested by the <code>Content-Disposition</code>
     * header, or {@code null} if no filename was suggested.
     * @return the attachment's suggested filename, or {@code null} if none was suggested
     */
    public String getSuggestedFilename() {
        if (attachmentFilename_ != null) {
            return attachmentFilename_;
        }

        final WebResponse response = page_.getWebResponse();
        final String disp = response.getResponseHeaderValue(HttpHeader.CONTENT_DISPOSITION);
        return getSuggestedFilename(disp);
    }

    /**
     * Returns the attachment's filename, as suggested by the <code>Content-Disposition</code>
     * header, or {@code null} if no filename was suggested.
     * @param contentDispositionHeader the <code>Content-Disposition</code> header
     *
     * @return the attachment's suggested filename, or {@code null} if none was suggested
     */
    public static String getSuggestedFilename(final String contentDispositionHeader) {
        if (contentDispositionHeader == null) {
            return null;
        }

        int start = contentDispositionHeader.indexOf("filename=");
        if (start == -1) {
            return null;
        }
        start += "filename=".length();
        if (start >= contentDispositionHeader.length()) {
            return null;
        }

        int end = contentDispositionHeader.indexOf(';', start);
        if (end == -1) {
            end = contentDispositionHeader.length();
        }
        if (contentDispositionHeader.charAt(start) == '"' && contentDispositionHeader.charAt(end - 1) == '"') {
            start++;
            end--;
        }
        return contentDispositionHeader.substring(start, end);
    }
}

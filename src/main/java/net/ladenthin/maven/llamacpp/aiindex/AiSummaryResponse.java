// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

/** Immutable AI response carrying the summary text and comma-separated keyword list. */
@ConvertToRecord
public class AiSummaryResponse {
    private final String summary;
    private final String keywords;

    /**
     * Creates a new {@link AiSummaryResponse}.
     *
     * @param summary  AI-generated summary text
     * @param keywords AI-generated comma-separated keyword list
     */
    public AiSummaryResponse(String summary, String keywords) {
        Objects.requireNonNull(summary, "summary");
        Objects.requireNonNull(keywords, "keywords");
        this.summary = summary;
        this.keywords = keywords;
    }

    /**
     * Returns the AI-generated summary text.
     *
     * @return summary text
     */
    public String summary() {
        return summary;
    }

    /**
     * Returns the AI-generated keyword list.
     *
     * @return comma-separated keyword list
     */
    public String keywords() {
        return keywords;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiSummaryResponse that = (AiSummaryResponse) obj;
        return Objects.equals(this.summary, that.summary) && Objects.equals(this.keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, keywords);
    }

    @Override
    public String toString() {
        return "AiSummaryResponse[" + "summary=" + summary + ", " + "keywords=" + keywords + ']';
    }
}

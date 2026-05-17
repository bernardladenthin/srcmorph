// @formatter:off

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

@ConvertToRecord
public class AiSummaryResponse {
    private final String summary;
    private final String keywords;

    public AiSummaryResponse(String summary, String keywords) {
        Objects.requireNonNull(summary, "summary");
        Objects.requireNonNull(keywords, "keywords");
        this.summary = summary;
        this.keywords = keywords;
    }

    public String summary() {
        return summary;
    }

    public String keywords() {
        return keywords;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiSummaryResponse that = (AiSummaryResponse) obj;
        return Objects.equals(this.summary, that.summary) &&
                Objects.equals(this.keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, keywords);
    }

    @Override
    public String toString() {
        return "AiSummaryResponse[" +
                "summary=" + summary + ", " +
                "keywords=" + keywords + ']';
    }

}
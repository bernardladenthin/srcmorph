// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

@ConvertToRecord
public class AiMdDocument {
    private final AiMdHeader header;
    private final String body;


    public AiMdDocument(AiMdHeader header, String body) {
        Objects.requireNonNull(header, "header");
        Objects.requireNonNull(body, "body");
        this.header = header;
        this.body = body;
    }

    public AiMdHeader header() {
        return header;
    }

    public String body() {
        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiMdDocument that = (AiMdDocument) obj;
        return Objects.equals(this.header, that.header) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body);
    }

    @Override
    public String toString() {
        return "AiMdDocument[" +
                "header=" + header + ", " +
                "body=" + body + ']';
    }

}
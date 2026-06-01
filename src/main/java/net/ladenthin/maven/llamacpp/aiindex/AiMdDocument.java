// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Immutable representation of an {@code .ai.md} document consisting of a header and a body. */
@ConvertToRecord
public class AiMdDocument {
    private final AiMdHeader header;
    private final String body;

    /**
     * Creates a new {@link AiMdDocument}.
     *
     * @param header metadata header
     * @param body   markdown body text
     */
    public AiMdDocument(AiMdHeader header, String body) {
        Objects.requireNonNull(header, "header");
        Objects.requireNonNull(body, "body");
        this.header = header;
        this.body = body;
    }

    /**
     * Returns the metadata header.
     *
     * @return metadata header
     */
    public AiMdHeader header() {
        return header;
    }

    /**
     * Returns the markdown body.
     *
     * @return markdown body
     */
    public String body() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiMdDocument that = (AiMdDocument) obj;
        return Objects.equals(this.header, that.header) && Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body);
    }

    @Override
    public String toString() {
        return "AiMdDocument[" + "header=" + header + ", " + "body=" + body + ']';
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Objects;

/**
 * Immutable result produced by {@link AiFieldGenerationSupport#processFieldGenerations}.
 *
 * <p>Carries the AI-generated document body text produced by a single processing pass.
 * Defaults to an empty string when no body target is present in the field generation
 * configuration.</p>
 *
 */
@ConvertToRecord
public class AiGenerationResult {
    private final String body;

    /**
     * Creates a new {@link AiGenerationResult}.
     *
     * @param body AI-generated body text destined for {@link AiMdDocument#body()}
     */
    public AiGenerationResult(
            String body
    ) {
        this.body = body;
    }

    /**
     * Returns the AI-generated body text.
     *
     * @return body text; may be empty if no body field was generated
     */
    public String body() {
        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AiGenerationResult that = (AiGenerationResult) obj;
        return Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "AiGenerationResult[" +
                "body=" + body + ']';
    }
}

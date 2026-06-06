// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import lombok.ToString;

/**
 * Parses raw LLM completion text to extract the model answer by stripping any
 * model-internal thinking block before the result is stored in an AI index file.
 *
 * <p>Gemma 4 (and compatible models) wrap their chain-of-thought reasoning in a
 * thinking block delimited by {@link #THINKING_BLOCK_START_MARKER} and
 * {@link #THINKING_BLOCK_END_MARKER}.  Only the text that follows the closing
 * marker is the actual model answer; everything inside the block is internal
 * reasoning that must not be persisted.</p>
 *
 * <p>This class is non-final to allow subclassing and mocking in tests.</p>
 */
@ToString
public class AiCompletionParser {

    /** Creates a new {@link AiCompletionParser}. */
    public AiCompletionParser() {
        // no-op
    }

    /**
     * Token that opens a Gemma-4 thinking block.
     * The model emits {@code <|channel>thought\n[reasoning]<channel|>[final answer]};
     * everything from this marker up to and including {@link #THINKING_BLOCK_END_MARKER}
     * is internal reasoning that must be stripped before storing the body.
     */
    public static final String THINKING_BLOCK_START_MARKER = "<|channel>thought";

    /**
     * Token that closes a Gemma-4 thinking block.
     * Text appearing after this marker (exclusive) is the model's actual answer.
     *
     * @see #THINKING_BLOCK_START_MARKER
     */
    public static final String THINKING_BLOCK_END_MARKER = "<channel|>";

    /**
     * Strips any Gemma-4 thinking block from {@code response} and returns the
     * clean answer text.
     *
     * <ul>
     *   <li>If {@link #THINKING_BLOCK_END_MARKER} is present, everything up to
     *       and including that marker is removed; only the trimmed text that
     *       follows is returned.</li>
     *   <li>If {@link #THINKING_BLOCK_START_MARKER} is present but
     *       {@link #THINKING_BLOCK_END_MARKER} is absent, the token budget was
     *       exhausted while the model was still reasoning.  An {@link IOException}
     *       is thrown with an actionable message so the caller can surface the
     *       problem rather than persist garbage.</li>
     *   <li>If neither marker is present the response is returned trimmed.</li>
     *   <li>{@code null} input is treated as an empty response.</li>
     * </ul>
     *
     * @param response the raw completion text from the model, may be {@code null}
     * @return the cleaned answer text, never {@code null}
     * @throws IOException if a thinking block was started but the token budget was
     *                     exhausted before the closing marker was emitted
     */
    public String parseCompletion(final String response) throws IOException {
        if (response == null) {
            return "";
        }
        final int thinkingEnd = response.lastIndexOf(THINKING_BLOCK_END_MARKER);
        if (thinkingEnd >= 0) {
            return response.substring(thinkingEnd + THINKING_BLOCK_END_MARKER.length())
                    .trim();
        }
        if (response.contains(THINKING_BLOCK_START_MARKER)) {
            throw new IOException("Model token budget exhausted inside thinking block: "
                    + THINKING_BLOCK_START_MARKER + " was emitted but "
                    + THINKING_BLOCK_END_MARKER + " was not. "
                    + "Increase maxOutputTokens for this model definition. "
                    + "(response length=" + response.length() + " chars)");
        }
        return response.trim();
    }
}

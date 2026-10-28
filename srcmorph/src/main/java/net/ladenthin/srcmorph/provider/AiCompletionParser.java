// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

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
 * <p>A thinking block is recognized only at the <b>start</b> of the response, where the model
 * emits it. A marker further in is part of the answer: a summary of this very class quotes both
 * markers, and stripping up to the last end marker anywhere in the text would throw most of that
 * answer away.</p>
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
     * Strips a leading Gemma-4 thinking block from {@code response} and returns the
     * clean answer text.
     *
     * <ul>
     *   <li>If the trimmed response starts with {@link #THINKING_BLOCK_START_MARKER},
     *       everything up to and including the first {@link #THINKING_BLOCK_END_MARKER}
     *       is removed; only the trimmed text that follows is returned, markers it quotes
     *       included.</li>
     *   <li>If it starts with {@link #THINKING_BLOCK_START_MARKER} but
     *       {@link #THINKING_BLOCK_END_MARKER} is absent, the token budget was
     *       exhausted while the model was still reasoning.  An {@link IOException}
     *       is thrown with an actionable message so the caller can surface the
     *       problem rather than persist garbage.</li>
     *   <li>Otherwise the response is returned trimmed, any marker in it kept as text.</li>
     *   <li>{@code null} input is treated as an empty response.</li>
     * </ul>
     *
     * @param response the raw completion text from the model, may be {@code null}
     * @return the cleaned answer text, never {@code null}
     * @throws IOException if a leading thinking block was started but the token budget
     *                     was exhausted before the closing marker was emitted
     */
    public String parseCompletion(final String response) throws IOException {
        if (response == null) {
            return "";
        }
        final String trimmed = response.trim();
        if (!trimmed.startsWith(THINKING_BLOCK_START_MARKER)) {
            return trimmed;
        }
        // The first end marker after the start marker closes the block.
        final String thinking = trimmed.substring(THINKING_BLOCK_START_MARKER.length());
        final int thinkingEnd = thinking.indexOf(THINKING_BLOCK_END_MARKER);
        if (thinkingEnd < 0) {
            throw new IOException("Model token budget exhausted inside thinking block: "
                    + THINKING_BLOCK_START_MARKER + " was emitted but "
                    + THINKING_BLOCK_END_MARKER + " was not. "
                    + "Increase maxOutputTokens for this model definition. "
                    + "(response length=" + response.length() + " chars)");
        }
        return thinking.substring(thinkingEnd + THINKING_BLOCK_END_MARKER.length())
                .trim();
    }
}

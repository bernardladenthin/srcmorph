// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import lombok.ToString;

/**
 * Parses raw LLM completion text to extract the model answer by stripping any
 * model-internal thinking block before the result is stored in an AI index file.
 *
 * <p>Gemma 4 (and compatible models) wrap their chain-of-thought reasoning in a
 * thinking block delimited by {@link #THINKING_BLOCK_START_MARKER} and
 * {@link #THINKING_BLOCK_END_MARKER}; DeepSeek-R1, Qwen3 and others in
 * {@link #THINK_TAG_START_MARKER} and {@link #THINK_TAG_END_MARKER}. Only the text that follows the
 * closing marker is the actual model answer; everything inside the block is internal
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
public class CompletionParser {

    /** Creates a new {@link CompletionParser}. */
    public CompletionParser() {
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

    /** Opens the thinking block of DeepSeek-R1, Qwen3 and the other models that write {@code <think>}. */
    public static final String THINK_TAG_START_MARKER = "<think>";

    /** Closes a {@link #THINK_TAG_START_MARKER} block. */
    public static final String THINK_TAG_END_MARKER = "</think>";

    /** The backtick of a Markdown code span: a marker right after one is quoted, not emitted. */
    private static final char CODE_QUOTE = '`';

    /** The kinds of thinking block, in the order they are tried. */
    private enum ThinkingBlock {
        /** Gemma 4's channel markers. */
        GEMMA(THINKING_BLOCK_START_MARKER, THINKING_BLOCK_END_MARKER),
        /** The {@code <think>} tag. */
        THINK_TAG(THINK_TAG_START_MARKER, THINK_TAG_END_MARKER);

        private final String start;
        private final String end;

        ThinkingBlock(final String start, final String end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Strips a leading thinking block from {@code response} and returns the clean answer text. A block is
     * a Gemma-4 {@link #THINKING_BLOCK_START_MARKER} ... {@link #THINKING_BLOCK_END_MARKER} or a
     * {@link #THINK_TAG_START_MARKER} ... {@link #THINK_TAG_END_MARKER}.
     *
     * <ul>
     *   <li>If the trimmed response starts with a start marker, everything up to and including the first
     *       matching end marker is removed; only the trimmed text that follows is returned, markers it
     *       quotes included.</li>
     *   <li>If it starts with a start marker whose end marker is absent, the token budget was exhausted
     *       while the model was still reasoning, and an {@link IncompleteAnswerException} says so
     *       rather than letting the reasoning be stored as the answer.</li>
     *   <li>If a chat template opened the block itself (Qwen3, DeepSeek-R1), the response holds only the
     *       closing {@link #THINK_TAG_END_MARKER}: a first one with no {@link #THINK_TAG_START_MARKER}
     *       before it and not quoted in backticks ends the reasoning, and the trimmed text after it is
     *       returned.</li>
     *   <li>Otherwise the response is returned trimmed, any marker in it kept as text.</li>
     *   <li>{@code null} input is treated as an empty response.</li>
     * </ul>
     *
     * @param response the raw completion text from the model, may be {@code null}
     * @return the cleaned answer text, never {@code null}
     * @throws IncompleteAnswerException if a leading thinking block was started but the token budget
     *                                     was exhausted before its closing marker was emitted
     */
    public String parseCompletion(final String response) throws IncompleteAnswerException {
        if (response == null) {
            return "";
        }
        final String trimmed = response.trim();
        for (final ThinkingBlock block : ThinkingBlock.values()) {
            final String start = block.start;
            final String end = block.end;
            if (!trimmed.startsWith(start)) {
                continue;
            }
            // The first end marker after the start marker closes the block.
            final String thinking = trimmed.substring(start.length());
            final int thinkingEnd = thinking.indexOf(end);
            if (thinkingEnd < 0) {
                throw new IncompleteAnswerException(
                        IncompleteAnswerException.Reason.TRUNCATED,
                        "Model token budget exhausted inside thinking block: " + start + " was emitted but " + end
                                + " was not. Increase maxOutputTokens for this model definition. (response length="
                                + response.length() + " chars)");
            }
            return thinking.substring(thinkingEnd + end.length()).trim();
        }
        // a template that opened the block itself (Qwen3, DeepSeek-R1) leaves only the closing tag: a first
        // </think> with no <think> before it, and not quoted in backticks, ends the reasoning
        final int injectedEnd = trimmed.indexOf(THINK_TAG_END_MARKER);
        if (injectedEnd >= 0
                && !trimmed.substring(0, injectedEnd).contains(THINK_TAG_START_MARKER)
                && (injectedEnd == 0 || trimmed.charAt(injectedEnd - 1) != CODE_QUOTE)) {
            return trimmed.substring(injectedEnd + THINK_TAG_END_MARKER.length())
                    .trim();
        }
        return trimmed;
    }
}

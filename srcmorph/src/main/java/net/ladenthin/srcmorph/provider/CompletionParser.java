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

    /** How a summary's lead line starts; reasoning does not. */
    private static final String LEAD_MARKER = ">";

    /** How a backtick code fence opens. */
    private static final String FENCE = "```";

    /** How a tilde code fence opens. */
    private static final String TILDE_FENCE = "~~~";

    /** The characters before a closing tag that make it a tag the text names: quotes and a backtick. */
    private static final String NOT_GLUING = "\"'`";

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
     *       closing {@link #THINK_TAG_END_MARKER}, and the trimmed text after it is returned. With no
     *       {@link #THINK_TAG_START_MARKER} before it, the first one that stands alone on its line ends the
     *       reasoning; without such a line, the first one that ends its line glued to the reasoning's last
     *       word ({@code I will write it.</think>}), when no line before it starts like an answer (a
     *       {@code >} lead, a code fence). A tag inside a line, or after a space, a quote or a backtick (in
     *       a sentence, in quotes, after a list item's label) is text the answer or the reasoning writes
     *       about it.</li>
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
        // a template that opened the block itself (Qwen3, DeepSeek-R1) leaves only the closing tag: the first
        // </think> alone on its line, with no <think> before it, ends the reasoning, else the first one glued to
        // the end of a line of reasoning; one inside a line is text about the tag
        int injectedEnd = injectedEnd(trimmed, true);
        if (injectedEnd < 0) {
            injectedEnd = injectedEnd(trimmed, false);
        }
        return injectedEnd < 0
                ? trimmed
                : trimmed.substring(injectedEnd + THINK_TAG_END_MARKER.length()).trim();
    }

    /**
     * Finds the closing tag that ends the reasoning a chat template opened: the first one with no
     * {@link #THINK_TAG_START_MARKER} before it that stands alone on its line, or that ends its line glued
     * to the word before it.
     *
     * @param text  the response, trimmed
     * @param alone {@code true} for a tag alone on its line, {@code false} for one glued to a line's end
     * @return where the tag starts, or {@code -1}
     */
    private static int injectedEnd(final String text, final boolean alone) {
        int index = text.indexOf(THINK_TAG_END_MARKER);
        while (index >= 0 && !text.substring(0, index).contains(THINK_TAG_START_MARKER)) {
            if (alone ? aloneOnItsLine(text, index) : gluedToALineEnd(text, index)) {
                return index;
            }
            index = text.indexOf(THINK_TAG_END_MARKER, index + 1);
        }
        return -1;
    }

    /**
     * Returns whether the closing tag at {@code index} ends its line, but for spaces and tabs, and follows
     * a character that is no space, quote or backtick: the end of a sentence of reasoning, not a tag the
     * text names.
     *
     * @param text  the response, trimmed
     * @param index where the tag starts
     * @return {@code true} when the tag is glued to the end of its line's text
     */
    private static boolean gluedToALineEnd(final String text, final int index) {
        if (index == 0 || !restOfLine(text, index).trim().isEmpty() || answerBefore(text.substring(0, index))) {
            return false;
        }
        final char before = text.charAt(index - 1);
        return !Character.isWhitespace(before) && NOT_GLUING.indexOf(before) < 0;
    }

    /**
     * Returns whether the text before a glued tag already holds an answer: a line that starts with the
     * lead's {@code >} or opens a code fence. Reasoning has neither, so a tag after one is text the answer
     * writes ({@code - End marker:</think>}, a template quoted in a fence).
     *
     * @param before the text before the tag
     * @return {@code true} when a line of it starts like an answer
     */
    private static boolean answerBefore(final String before) {
        for (final String line : before.split("\n", -1)) {
            final String start = line.trim();
            if (start.startsWith(LEAD_MARKER) || start.startsWith(FENCE) || start.startsWith(TILDE_FENCE)) {
                return true;
            }
        }
        return false;
    }

    private static String restOfLine(final String text, final int index) {
        final String after = text.substring(index + THINK_TAG_END_MARKER.length());
        final int newline = after.indexOf('\n');
        return newline < 0 ? after : after.substring(0, newline);
    }

    /**
     * Returns whether the closing tag at {@code index} is the only thing on its line, but for spaces and
     * tabs.
     *
     * @param text  the response, trimmed
     * @param index where the tag starts
     * @return {@code true} when nothing else stands on the tag's line
     */
    private static boolean aloneOnItsLine(final String text, final int index) {
        final String before = text.substring(0, index);
        return before.substring(before.lastIndexOf('\n') + 1).trim().isEmpty()
                && restOfLine(text, index).trim().isEmpty();
    }
}

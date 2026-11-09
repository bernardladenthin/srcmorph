// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.IOException;
import java.util.Objects;

/**
 * A model answer that is no summary: nothing at all, a text cut off at the output budget, a text the
 * provider stopped (an error or its content filter), or a response without an answer in it. A provider throws it instead of returning such a text, so the
 * indexer can leave the file for the next run instead of writing the text as if it were done.
 */
public final class IncompleteAnswerException extends IOException {

    private static final long serialVersionUID = 1L;

    /** Why the answer is incomplete. */
    public enum Reason {
        /** The answer is empty or whitespace, for example a reasoning-only answer. */
        EMPTY,
        /** The model stopped at the output budget, or inside its reasoning, not because it was done. */
        TRUNCATED,
        /** The response carries no answer at all (no choice, an error object or not a completion). */
        NO_ANSWER,
        /**
         * The provider stopped the answer, not the model: finish reason {@code error} (it failed in the
         * middle of the generation) or {@code content_filter}. What came back ends wherever it stopped.
         */
        ABORTED
    }

    /** Why the answer is incomplete. */
    private final Reason reason;

    /** What happened, naming the file; the exception's message. */
    private final String detail;

    /** The text the model did write before it stopped, {@code ""} when there is none. */
    private final String partialText;

    /**
     * Creates the exception for an answer without usable text.
     *
     * @param reason  why the answer is incomplete
     * @param message what happened, naming the file
     */
    public IncompleteAnswerException(final Reason reason, final String message) {
        this(reason, message, "");
    }

    /**
     * Creates the exception for an answer the model stopped writing, with what it wrote.
     *
     * @param reason      why the answer is incomplete
     * @param message     what happened, naming the file
     * @param partialText the text written before the model stopped
     */
    public IncompleteAnswerException(final Reason reason, final String message, final String partialText) {
        super(message);
        this.reason = Objects.requireNonNull(reason, "reason");
        this.detail = message;
        this.partialText = Objects.requireNonNull(partialText, "partialText");
    }

    /**
     * Returns why the answer is incomplete.
     *
     * @return the reason
     */
    public Reason reason() {
        return reason;
    }

    /**
     * Returns what happened, naming the file: the message, never {@code null}.
     *
     * @return the detail
     */
    public String detail() {
        return detail;
    }

    /**
     * Returns the text the model wrote before it stopped.
     *
     * @return the partial answer, {@code ""} when there is none
     */
    public String partialText() {
        return partialText;
    }
}

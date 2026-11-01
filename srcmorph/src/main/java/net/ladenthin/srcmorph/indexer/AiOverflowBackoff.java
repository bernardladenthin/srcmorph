// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import net.ladenthin.srcmorph.provider.AiContextOverflowException;
import org.jspecify.annotations.Nullable;

/**
 * The safety net of one generation whose prompt the model refused as longer than its context: the
 * char-based window is an estimate, and on token-dense content it can undershoot even outside
 * map-reduce (a dense file that just "fits", a {@code sample} head), so the prompt is trimmed to a
 * smaller window and sent again a few times before the overflow fails the file.
 */
public final class AiOverflowBackoff {

    /** How often one generation is sent again with a smaller window. */
    public static final int MAX_RETRIES = 3;

    /** The window of a retry, in percent of the one before. */
    public static final int BACKOFF_PERCENT = 75;

    /** The smallest window a retry is sent with. */
    public static final int MIN_WINDOW_CHARS = AiMapReduceSupport.MIN_CHUNK_CHARS;

    /** What {@link #nextWindow(int, int)} returns when there is no smaller window to try. */
    public static final int NO_SMALLER_WINDOW = -1;

    /**
     * The overflow of a local model in the message of its exception (llama.cpp: "request (N tokens)
     * exceeds the available context size (M tokens)"); matched on the message, which keeps the indexer
     * free of the JNI binding. A remote model's overflow is an {@link AiContextOverflowException}.
     */
    public static final String CONTEXT_OVERFLOW_MARKER = "exceeds the available context size";

    /** Percent divisor. */
    private static final int PERCENT = 100;

    /** Utility class; not instantiable. */
    private AiOverflowBackoff() {
        // no-op
    }

    /**
     * Returns the window of the retry after an overflow, or {@link #NO_SMALLER_WINDOW} once the retries
     * are used up or the window is already at {@link #MIN_WINDOW_CHARS}.
     *
     * @param attempt the attempt that overflowed, from {@code 0}
     * @param window  its window in characters
     * @return the smaller window, or {@link #NO_SMALLER_WINDOW}
     */
    public static int nextWindow(final int attempt, final int window) {
        // in long, so a window above Integer.MAX_VALUE / 75 does not overflow
        final int reduced = (int) Math.max(MIN_WINDOW_CHARS, (long) window * BACKOFF_PERCENT / PERCENT);
        return attempt >= MAX_RETRIES || reduced >= window ? NO_SMALLER_WINDOW : reduced;
    }

    /**
     * Returns whether an error, or any cause in its chain, is a context overflow: an
     * {@link AiContextOverflowException}, or a message containing {@link #CONTEXT_OVERFLOW_MARKER}.
     *
     * @param error the thrown error
     * @return {@code true} when the prompt was longer than the model's context
     */
    public static boolean isContextOverflow(final @Nullable Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof AiContextOverflowException) {
                return true;
            }
            final String message = cause.getMessage();
            if (message != null && message.contains(CONTEXT_OVERFLOW_MARKER)) {
                return true;
            }
        }
        return false;
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

/**
 * The arithmetic of a window measured in tokens, for a provider that counts a prompt's tokens
 * ({@link net.ladenthin.srcmorph.provider.GenerationProvider#promptTokens}): how many prompt tokens a call
 * may take, and the window in characters a prompt of a measured density fits. {@code charsPerToken} is
 * only an estimate, and source code, SQL and data tokenize denser than it assumes; the count is the
 * model's own tokenizer over the prompt as the chat template renders it.
 */
public final class TokenWindow {

    /** The token limit of a model whose window is not known: no call is checked. */
    public static final int NO_LIMIT = 0;

    /**
     * Tokens a prompt may take beyond its count: the beginning- and end-of-sequence tokens a vocabulary
     * adds at prefill, which the tokenizer's count of the rendered template leaves out.
     */
    public static final int UNCOUNTED_TOKENS = 4;

    /** Percent below the limit a smaller window aims at, so a re-trimmed prompt lands under it. */
    public static final int MARGIN_PERCENT = 2;

    /** How many smaller windows a call's count may ask for before the prompt is refused. */
    public static final int MAX_SMALLER_WINDOWS = 4;

    /** Percent divisor. */
    private static final int PERCENT = 100;

    /** Utility class; not instantiable. */
    private TokenWindow() {
        // no-op
    }

    /**
     * Returns how many tokens a call's prompt may take: the window less the tokens the answer may take and
     * the {@link #UNCOUNTED_TOKENS}.
     *
     * @param contextSize     the model's window in tokens, {@code 0} or less when not known
     * @param maxOutputTokens the tokens the answer may take
     * @return the limit, at least {@code 1}, or {@link #NO_LIMIT} when the window is not known
     */
    public static int promptTokenLimit(final int contextSize, final int maxOutputTokens) {
        if (contextSize <= 0) {
            return NO_LIMIT;
        }
        return Math.max(1, contextSize - Math.max(0, maxOutputTokens) - UNCOUNTED_TOKENS);
    }

    /**
     * Returns the window in characters that holds {@code tokenLimit} tokens of a prompt whose
     * {@code promptChars} characters took {@code promptTokens} tokens: the limit times the prompt's
     * characters per token.
     *
     * @param tokenLimit   the tokens a prompt may take
     * @param promptChars  the measured prompt's characters
     * @param promptTokens the measured prompt's tokens, above {@code 0}
     * @return the window in characters
     */
    public static int measuredWindow(final int tokenLimit, final int promptChars, final int promptTokens) {
        return (int) Math.min(Integer.MAX_VALUE, (long) tokenLimit * promptChars / promptTokens);
    }

    /**
     * Returns the next window for a prompt that took more tokens than the limit: the
     * {@link #measuredWindow measured window} less {@link #MARGIN_PERCENT}, and always below {@code window},
     * so a retry never asks the same again and a re-trimmed prompt lands under the limit.
     *
     * @param window       the window the prompt was prepared for
     * @param tokenLimit   the tokens a prompt may take
     * @param promptChars  the prompt's characters
     * @param promptTokens the prompt's tokens, above {@code tokenLimit}
     * @return the smaller window, {@code 0} or less when no window is smaller
     */
    public static int smallerWindow(
            final int window, final int tokenLimit, final int promptChars, final int promptTokens) {
        final long measured = measuredWindow(tokenLimit, promptChars, promptTokens);
        return (int) Math.min(window - 1L, measured * (PERCENT - MARGIN_PERCENT) / PERCENT);
    }
}

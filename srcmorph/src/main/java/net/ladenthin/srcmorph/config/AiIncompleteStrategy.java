// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.config;

import org.jspecify.annotations.Nullable;

/**
 * What a rule does when the model's answer is no summary: empty, cut off at the output budget, or a
 * response without an answer. Configured per rule as {@code onIncomplete}. An incomplete answer is
 * not written (only {@link #KEEP} writes a cut-off answer that has text): the file keeps its older
 * summary, or none, the run counts it and fails at its end, and the next run tries it again.
 */
public enum AiIncompleteStrategy {

    /** Count the file as failed at once (the default). */
    FAIL("fail"),

    /** Ask the model once more with the same request; count the file as failed if that answer is no better. */
    RETRY_ONCE("retryOnce"),

    /**
     * Write an answer that was cut off at the output budget as it is, when it has text; an empty answer
     * or none at all still fails.
     */
    KEEP("keep");

    /** The strategy of a rule that sets none. */
    public static final AiIncompleteStrategy DEFAULT = FAIL;

    private final String configValue;

    AiIncompleteStrategy(final String configValue) {
        this.configValue = configValue;
    }

    /**
     * Returns the {@code onIncomplete} config token (e.g. {@code "retryOnce"}).
     *
     * @return the config token
     */
    public String configValue() {
        return configValue;
    }

    /**
     * Returns how many times the answer is requested in all.
     *
     * @return {@code 2} for {@link #RETRY_ONCE}, {@code 1} otherwise
     */
    public int attempts() {
        return this == RETRY_ONCE ? 2 : 1;
    }

    /**
     * Parses an {@code onIncomplete} config value (case-insensitive). {@code null} or blank yields
     * {@link #DEFAULT}.
     *
     * @param value the configured value, or {@code null}
     * @return the matching strategy
     * @throws IllegalArgumentException if {@code value} is non-blank and matches no strategy
     */
    public static AiIncompleteStrategy fromConfig(final @Nullable String value) {
        if (value == null) {
            return DEFAULT;
        }
        final String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return DEFAULT;
        }
        for (final AiIncompleteStrategy strategy : values()) {
            if (strategy.configValue.equalsIgnoreCase(trimmed)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException(
                "Unknown onIncomplete strategy: '" + value + "' (expected one of fail/retryOnce/keep)");
    }
}

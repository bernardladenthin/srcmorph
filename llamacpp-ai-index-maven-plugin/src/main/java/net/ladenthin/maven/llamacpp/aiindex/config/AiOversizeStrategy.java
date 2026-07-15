// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex.config;

import org.jspecify.annotations.Nullable;

/**
 * What to do when a source file is larger than its routed model's context window.
 *
 * <p>Configured per routing rule via {@code <onOversize>} (a string, parsed by
 * {@link #fromConfig(String)}). The default is {@link #FAIL}: the build aborts and the user must route
 * oversized files to a larger-context model (no automatic model choice). The other strategies let a rule
 * deliberately handle oversized files:</p>
 *
 * <ul>
 *   <li>{@link #SAMPLE} — feed only the head of the file (trimmed to the window) in one call; fast,
 *       bounded, good for repetitive data where the head represents the whole.</li>
 *   <li>{@link #MAP_REDUCE} — split the file into window-sized chunks at line boundaries, summarize each,
 *       then combine the partial summaries in one final call; processes far more of the file than a
 *       single window, with a {@code maxChunks} cap to bound the time.</li>
 *   <li>{@link #DETERMINISTIC} — no model at all: emit a deterministic body (size, line count, head/tail
 *       sample); instant, for pure data where no AI analysis is needed.</li>
 * </ul>
 */
public enum AiOversizeStrategy {

    /** Abort the build; the user must configure a larger-context model (default). */
    FAIL("fail"),

    /** Trim the source to the window and summarize the head in a single call. */
    SAMPLE("sample"),

    /** Chunk at line boundaries, summarize each chunk, then combine the partial summaries. */
    MAP_REDUCE("mapReduce"),

    /** Emit a model-free deterministic body (metadata + head/tail sample). */
    DETERMINISTIC("deterministic");

    /** The strategy applied when none is configured. */
    public static final AiOversizeStrategy DEFAULT = FAIL;

    /** The {@code <onOversize>} config token for this strategy. */
    private final String configValue;

    AiOversizeStrategy(final String configValue) {
        this.configValue = configValue;
    }

    /**
     * Returns the {@code <onOversize>} config token (e.g. {@code "mapReduce"}).
     *
     * @return the config token
     */
    public String configValue() {
        return configValue;
    }

    /**
     * Parses a {@code <onOversize>} config value (case-insensitive). {@code null} or blank yields
     * {@link #DEFAULT}.
     *
     * @param value the configured value, or {@code null}
     * @return the matching strategy
     * @throws IllegalArgumentException if {@code value} is non-blank and matches no strategy
     */
    public static AiOversizeStrategy fromConfig(final @Nullable String value) {
        if (value == null) {
            return DEFAULT;
        }
        final String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return DEFAULT;
        }
        for (final AiOversizeStrategy strategy : values()) {
            if (strategy.configValue.equalsIgnoreCase(trimmed)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException(
                "Unknown onOversize strategy: '" + value + "' (expected one of fail/sample/mapReduce/deterministic)");
    }
}

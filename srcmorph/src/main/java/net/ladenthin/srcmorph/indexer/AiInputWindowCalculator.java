// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.indexer;

import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.prompt.AiPromptPreparationSupport;

/**
 * Pure calculator for the input window: how many source characters fit a model's context window
 * before the source must be trimmed, and whether a given source would exceed it.
 *
 * <p>This is the single source of truth for the trim threshold. Both the run path
 * ({@link AiFieldGenerationSupport}, which trims and warns) and the planning path
 * ({@link SourceFileIndexer#classify}, which flags over-window files up front) use it, so the plan's
 * prediction matches what the run actually does.</p>
 *
 * <p>The budget mirrors the runtime trim check in
 * {@link AiPromptPreparationSupport#preparePrompt}: the rendered prompt (template + source) must fit
 * {@link #maxInputChars(AiGenerationConfig, int)}; since the template ({@code basePromptLength}) is
 * fixed, the source budget is {@link #availableSourceChars(AiGenerationConfig, int)} and a source is
 * trimmed when its length exceeds it.</p>
 */
public final class AiInputWindowCalculator {

    /**
     * Rounding granularity (in characters) applied to the computed maximum input size, kept
     * conservative (rounds down) so the value is a safe, human-readable multiple.
     */
    public static final int MAX_INPUT_CHARS_ROUNDING = 100;

    /** Utility class; not instantiable. */
    private AiInputWindowCalculator() {
        // no-op
    }

    /**
     * Returns the maximum number of characters of the rendered prompt (template + source) that fit the
     * model's context window before trimming. Mirrors the runtime computation: when
     * {@link AiGenerationConfig#getCharsPerToken()} is {@code <= 0} the static
     * {@link AiGenerationConfig#getMaxInputChars()} fallback is used (e.g. mock-based tests); otherwise
     * it is {@code (contextSize x charsPerToken)} minus the prompt-template, EOF-marker, reserved-output
     * and safety-margin overhead, rounded down to a {@link #MAX_INPUT_CHARS_ROUNDING} multiple.
     *
     * @param config           the model generation config
     * @param basePromptLength the length of the rendered prompt template with an empty source
     * @return the maximum rendered-prompt length in characters (never negative)
     */
    public static int maxInputChars(final AiGenerationConfig config, final int basePromptLength) {
        if (config.getCharsPerToken() <= 0) {
            return config.getMaxInputChars();
        }
        final int totalChars = config.getContextSize() * config.getCharsPerToken();
        final int overhead = basePromptLength
                + AiPromptPreparationSupport.EOF_MARKER_LENGTH
                + config.getMaxOutputTokens() * config.getCharsPerToken()
                + AiGenerationConfig.DEFAULT_SAFETY_MARGIN_CHARS;
        final int available = totalChars - overhead;
        return Math.max(0, (available / MAX_INPUT_CHARS_ROUNDING) * MAX_INPUT_CHARS_ROUNDING);
    }

    /**
     * Returns how many characters of <em>source</em> fit before trimming: the
     * {@link #maxInputChars(AiGenerationConfig, int)} budget minus the fixed prompt template
     * ({@code basePromptLength}). Never negative.
     *
     * @param config           the model generation config
     * @param basePromptLength the length of the rendered prompt template with an empty source
     * @return the source-character budget (never negative)
     */
    public static long availableSourceChars(final AiGenerationConfig config, final int basePromptLength) {
        return Math.max(0L, (long) maxInputChars(config, basePromptLength) - basePromptLength);
    }

    /**
     * Returns whether a source of the given length would be trimmed because it does not fit the model's
     * window. Equivalent to the runtime trim trigger: {@code sourceChars > availableSourceChars}.
     *
     * @param config           the model generation config
     * @param basePromptLength the length of the rendered prompt template with an empty source
     * @param sourceChars      the source length in characters
     * @return {@code true} when the source would be trimmed (exceeds the window)
     */
    public static boolean exceedsWindow(
            final AiGenerationConfig config, final int basePromptLength, final long sourceChars) {
        return sourceChars > availableSourceChars(config, basePromptLength);
    }
}

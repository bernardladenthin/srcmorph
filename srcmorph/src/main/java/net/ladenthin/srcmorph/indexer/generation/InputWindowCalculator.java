// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.indexer.generation;

import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.prompt.PromptPreparer;

/**
 * Pure calculator for the input window: how many source characters fit a model's context window
 * before the source must be trimmed, and whether a given source would exceed it.
 *
 * <p>This is the single source of truth for the trim threshold. Both the run path
 * ({@link SummaryGenerator}, which trims and warns) and the planning path
 * ({@code SourceFileIndexer.classify}, which flags over-window files up front) use it, so the plan's
 * prediction matches what the run actually does.</p>
 *
 * <p>The budget mirrors the runtime trim check in
 * {@link PromptPreparer#preparePrompt}: the rendered prompt (template + source) must fit
 * {@link #maxInputChars(ModelDefinition)}; since the template ({@code basePromptLength}) is
 * fixed, the source budget is {@link #availableSourceChars(ModelDefinition, int)} and a source is
 * trimmed when its length exceeds it.</p>
 */
public final class InputWindowCalculator {

    /**
     * Rounding granularity (in characters) applied to the computed maximum input size, kept
     * conservative (rounds down) so the value is a safe, human-readable multiple.
     */
    public static final int MAX_INPUT_CHARS_ROUNDING = 100;

    /** Utility class; not instantiable. */
    private InputWindowCalculator() {
        // no-op
    }

    /**
     * Returns the maximum number of characters of the rendered prompt (template + source) that fit the
     * model's context window before trimming: what {@code PromptPreparer.preparePrompt} compares the whole
     * prompt with. When {@link ModelDefinition#getCharsPerToken()} is {@code <= 0} the static
     * {@link ModelDefinition#DEFAULT_MAX_INPUT_CHARS} fallback is used (e.g. mock-based tests); otherwise
     * it is {@code (contextSize x charsPerToken)} minus the reserved output and the safety margin,
     * rounded down to a {@link #MAX_INPUT_CHARS_ROUNDING} multiple. The template is part of the prompt,
     * so it takes its room from this budget once, in {@link #availableSourceChars}.
     *
     * @param config the model generation config
     * @return the maximum rendered-prompt length in characters (never negative)
     */
    public static int maxInputChars(final ModelDefinition config) {
        if (config.getCharsPerToken() <= 0) {
            return ModelDefinition.DEFAULT_MAX_INPUT_CHARS;
        }
        final int totalChars = config.getContextSize() * config.getCharsPerToken();
        final int overhead =
                config.getMaxOutputTokens() * config.getCharsPerToken() + ModelDefinition.DEFAULT_SAFETY_MARGIN_CHARS;
        final int available = totalChars - overhead;
        return Math.max(0, (available / MAX_INPUT_CHARS_ROUNDING) * MAX_INPUT_CHARS_ROUNDING);
    }

    /**
     * Returns how many characters of <em>source</em> fit before trimming: the
     * {@link #maxInputChars(ModelDefinition)} budget minus the fixed prompt template
     * ({@code basePromptLength}). Never negative.
     *
     * @param config           the model generation config
     * @param basePromptLength the length of the rendered prompt template with an empty source
     * @return the source-character budget (never negative)
     */
    public static long availableSourceChars(final ModelDefinition config, final int basePromptLength) {
        return Math.max(0L, (long) maxInputChars(config) - basePromptLength);
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
            final ModelDefinition config, final int basePromptLength, final long sourceChars) {
        return sourceChars > availableSourceChars(config, basePromptLength);
    }
}

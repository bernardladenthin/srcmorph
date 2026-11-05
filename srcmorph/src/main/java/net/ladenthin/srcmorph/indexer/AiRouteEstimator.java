// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import lombok.ToString;
import net.ladenthin.srcmorph.config.AiCalibration;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.config.AiOversizeStrategy;
import net.ladenthin.srcmorph.support.AiGenerationTimeEstimator;

/**
 * The time estimate of a routed file, the one both the plan ({@link SourceFileIndexer}) and the live
 * per-file line of a run ({@link AiFieldGenerationSupport}) use, so the two agree: a model's
 * {@code calibration} when it has one, otherwise the built-in model of
 * {@link AiGenerationTimeEstimator}.
 */
@ToString
public final class AiRouteEstimator {

    private final AiGenerationTimeEstimator timeEstimator = new AiGenerationTimeEstimator();

    /** Creates a new {@link AiRouteEstimator}. */
    public AiRouteEstimator() {
        // no-op
    }

    /**
     * Whether a model's estimate comes from its own {@code calibration}: one with a measured prefill and a
     * measured decode throughput. Otherwise the estimate is the built-in one, based on GPT-6 Luna.
     *
     * @param modelConfig the routed model's config
     * @return {@code true} when the model's calibration is used
     */
    public static boolean isCalibrated(final AiGenerationConfig modelConfig) {
        final AiCalibration calibration = modelConfig.getCalibration();
        return calibration != null
                && calibration.getPrefillTokensPerSecond() > 0.0d
                && calibration.getDecodeTokensPerSecond() > 0.0d;
    }

    /**
     * Estimates one generation's seconds for a character count, from the model's {@code calibration}
     * (measured per-machine throughput) when present, otherwise the estimator's built-in model.
     *
     * @param chars       the character count to estimate for; more than {@link Integer#MAX_VALUE} counts
     *                    as that
     * @param modelConfig the routed model's config (may carry a calibration)
     * @return the estimated seconds
     */
    public long estimateSeconds(final long chars, final AiGenerationConfig modelConfig) {
        final int cappedChars = (int) Math.min(chars, Integer.MAX_VALUE);
        final AiCalibration calibration = modelConfig.getCalibration();
        if (calibration == null) {
            return timeEstimator.estimateSeconds(cappedChars);
        }
        return timeEstimator.estimateSecondsCalibrated(
                cappedChars,
                calibration.getPrefillTokensPerSecond(),
                calibration.getDecodeTokensPerSecond(),
                calibration.getCharsPerToken());
    }

    /**
     * Estimates the generation time of one routed file, by the rule's oversize strategy, so the plan's
     * time stays realistic for huge files (a call costs at most one window, not the whole file). A file
     * that fits the window costs one generation of its size; an over-window file costs nothing under
     * {@code deterministic}, one window under {@code sample} and {@code fail}, and its map calls plus its
     * reduce calls under {@code mapReduce} (every chunk) and {@code sampleChunks} ({@code maxChunks} of
     * them). The chunks are counted with the budget and overlap of the run
     * ({@link AiMapReduceSupport}), the reduce calls by its fan-in.
     *
     * @param rule                 the routing rule
     * @param modelConfig          the routed model's config
     * @param fileSizeBytes        the file size in bytes, the estimate of a file that fits
     * @param sourceChars          the source length in characters
     * @param availableSourceChars the per-call source-character budget of the routed model and prompt
     * @param basePromptLength     the length of the prompt template with an empty source
     * @return the estimated seconds
     */
    public long estimateRouteSeconds(
            final AiFieldGenerationConfig rule,
            final AiGenerationConfig modelConfig,
            final long fileSizeBytes,
            final long sourceChars,
            final long availableSourceChars,
            final int basePromptLength) {
        return estimateRouteSeconds(
                rule,
                modelConfig,
                fileSizeBytes,
                sourceChars,
                availableSourceChars,
                basePromptLength,
                basePromptLength);
    }

    /**
     * Estimates a route as {@link #estimateRouteSeconds(AiFieldGenerationConfig, AiGenerationConfig, long, long,
     * long, int)} does, with the chunks sized as the run sizes them: next to {@code chunkPromptLength}, the
     * longer of the rule's map and reduce prompts with a chunk's label reserve.
     *
     * @param rule                 the routing rule
     * @param modelConfig          the routed model's config
     * @param fileSizeBytes        the file size in bytes, the estimate of a file that fits
     * @param sourceChars          the source length in characters
     * @param availableSourceChars the per-call source-character budget of the routed model and prompt
     * @param basePromptLength     the length of the rule's prompt template with an empty source
     * @param chunkPromptLength    the prompt length a chunk is sized next to
     * @return the estimated seconds
     */
    public long estimateRouteSeconds(
            final AiFieldGenerationConfig rule,
            final AiGenerationConfig modelConfig,
            final long fileSizeBytes,
            final long sourceChars,
            final long availableSourceChars,
            final int basePromptLength,
            final int chunkPromptLength) {
        if (sourceChars <= availableSourceChars) {
            return estimateSeconds(fileSizeBytes, modelConfig);
        }
        final AiOversizeStrategy strategy = rule.oversizeStrategy();
        if (strategy == AiOversizeStrategy.DETERMINISTIC) {
            return 0L;
        }
        final long windowSeconds = estimateSeconds(availableSourceChars, modelConfig);
        if (strategy != AiOversizeStrategy.MAP_REDUCE && strategy != AiOversizeStrategy.SAMPLE_CHUNKS) {
            return windowSeconds;
        }
        // the window is the rule's: its source budget and its prompt; the chunks are sized next to the
        // chunk prompt, as the run sizes them
        final int sourceBudget = AiMapReduceSupport.sourceBudget(
                AiMapReduceSupport.safeWindow(
                        (int) Math.min(Integer.MAX_VALUE, availableSourceChars + basePromptLength)),
                chunkPromptLength);
        final long allChunks =
                AiMapReduceSupport.chunkCount(sourceChars, sourceBudget, AiMapReduceSupport.overlap(sourceBudget));
        final long chunks =
                strategy == AiOversizeStrategy.SAMPLE_CHUNKS ? Math.min(rule.getMaxChunks(), allChunks) : allChunks;
        return (chunks + AiMapReduceSupport.reduceCalls(chunks)) * windowSeconds;
    }
}

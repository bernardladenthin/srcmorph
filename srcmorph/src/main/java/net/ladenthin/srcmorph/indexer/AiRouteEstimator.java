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
 * {@code <calibration>} when it has one, otherwise the built-in model of
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
     * Estimates one generation's seconds for a character count, from the model's {@code <calibration>}
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
     * {@code deterministic}, one window under {@code sample} and {@code fail}, and (chunks + 1 reduce)
     * windows under {@code mapReduce}, the chunks capped by {@code maxChunks}.
     *
     * @param rule                 the routing rule
     * @param modelConfig          the routed model's config
     * @param fileSizeBytes        the file size in bytes, the estimate of a file that fits
     * @param sourceChars          the source length in characters
     * @param availableSourceChars the per-call source-character budget of the routed model and prompt
     * @return the estimated seconds
     */
    public long estimateRouteSeconds(
            final AiFieldGenerationConfig rule,
            final AiGenerationConfig modelConfig,
            final long fileSizeBytes,
            final long sourceChars,
            final long availableSourceChars) {
        if (sourceChars <= availableSourceChars) {
            return estimateSeconds(fileSizeBytes, modelConfig);
        }
        final AiOversizeStrategy strategy = rule.getOversizeStrategy();
        if (strategy == AiOversizeStrategy.DETERMINISTIC) {
            return 0L;
        }
        final long windowSeconds = estimateSeconds(availableSourceChars, modelConfig);
        if (strategy != AiOversizeStrategy.MAP_REDUCE) {
            return windowSeconds;
        }
        // over the window, so at least two chunks; a window of 0 counts as 1 (no division by zero)
        final long window = Math.max(1L, availableSourceChars);
        final long naturalChunks = (sourceChars + window - 1) / window;
        final long chunks = rule.getMaxChunks() > 0 ? Math.min(rule.getMaxChunks(), naturalChunks) : naturalChunks;
        return (chunks + 1) * windowSeconds;
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import lombok.ToString;
import net.ladenthin.srcmorph.config.Calibration;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.OversizeStrategy;
import net.ladenthin.srcmorph.config.RoutingRule;

/**
 * The time estimate of a routed file, the one both the plan ({@code SourceFileIndexer}) and the live
 * per-file line of a run ({@link SummaryGenerator}) use, so the two agree: a model's
 * {@code calibration} when it has one, otherwise the built-in model of
 * {@link GenerationTimeEstimator}. Both count characters, not bytes. The plan sizes a chunk by the
 * estimated window; a provider that counts a prompt's tokens may size it by its measured window at run
 * time, which the plan cannot know.
 */
@ToString
public final class RouteEstimator {

    private final GenerationTimeEstimator timeEstimator = new GenerationTimeEstimator();

    /** Creates a new {@link RouteEstimator}. */
    public RouteEstimator() {
        // no-op
    }

    /**
     * Whether a model's estimate comes from its own {@code calibration}: one with a measured prefill and a
     * measured decode throughput. Otherwise the estimate is the built-in one, based on GPT-6 Luna.
     *
     * @param modelConfig the routed model's config
     * @return {@code true} when the model's calibration is used
     */
    public static boolean isCalibrated(final ModelDefinition modelConfig) {
        final Calibration calibration = modelConfig.getCalibration();
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
    public long estimateSeconds(final long chars, final ModelDefinition modelConfig) {
        final int cappedChars = (int) Math.min(chars, Integer.MAX_VALUE);
        final Calibration calibration = modelConfig.getCalibration();
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
     * ({@link MapReduceMath}), the reduce calls by its fan-in. A window that leaves less than
     * {@link MapReduceMath#MIN_CHUNK_CHARS} for a chunk costs nothing: the run refuses such a file without a
     * call.
     *
     * @param rule                 the routing rule
     * @param modelConfig          the routed model's config
     * @param sourceChars          the source length in characters
     * @param availableSourceChars the per-call source-character budget of the routed model and prompt
     * @param basePromptLength     the length of the prompt template with an empty source
     * @return the estimated seconds
     */
    public long estimateRouteSeconds(
            final RoutingRule rule,
            final ModelDefinition modelConfig,
            final long sourceChars,
            final long availableSourceChars,
            final int basePromptLength) {
        return estimateRouteSeconds(
                rule, modelConfig, sourceChars, availableSourceChars, basePromptLength, basePromptLength);
    }

    /**
     * Estimates a route as {@link #estimateRouteSeconds(RoutingRule, ModelDefinition, long, long, int)}
     * does, with the chunks sized as the run sizes them: next to {@code chunkPromptLength}, the
     * longer of the rule's map and reduce prompts with a chunk's label reserve.
     *
     * @param rule                 the routing rule
     * @param modelConfig          the routed model's config
     * @param sourceChars          the source length in characters
     * @param availableSourceChars the per-call source-character budget of the routed model and prompt
     * @param basePromptLength     the length of the rule's prompt template with an empty source
     * @param chunkPromptLength    the prompt length a chunk is sized next to
     * @return the estimated seconds
     */
    public long estimateRouteSeconds(
            final RoutingRule rule,
            final ModelDefinition modelConfig,
            final long sourceChars,
            final long availableSourceChars,
            final int basePromptLength,
            final int chunkPromptLength) {
        if (sourceChars <= availableSourceChars) {
            return estimateSeconds(sourceChars, modelConfig);
        }
        final OversizeStrategy strategy = rule.oversizeStrategy();
        if (strategy == OversizeStrategy.DETERMINISTIC) {
            return 0L;
        }
        final long windowSeconds = estimateSeconds(availableSourceChars, modelConfig);
        if (strategy != OversizeStrategy.MAP_REDUCE && strategy != OversizeStrategy.SAMPLE_CHUNKS) {
            return windowSeconds;
        }
        // the window is the rule's: its source budget and its prompt; the chunks are sized next to the
        // chunk prompt, as the run sizes them, and a room below the minimum chunk the run refuses
        final int room =
                MapReduceMath.safeWindow((int) Math.min(Integer.MAX_VALUE, availableSourceChars + basePromptLength))
                        - chunkPromptLength;
        if (room < MapReduceMath.MIN_CHUNK_CHARS) {
            return 0L;
        }
        final int sourceBudget = room;
        final long allChunks = MapReduceMath.chunkCount(sourceChars, sourceBudget, MapReduceMath.overlap(sourceBudget));
        final long chunks =
                strategy == OversizeStrategy.SAMPLE_CHUNKS ? Math.min(rule.getMaxChunks(), allChunks) : allChunks;
        // a reduce batch holds as many part summaries as fit the window, as the run packs them, not always 16
        final double charsPerToken = modelConfig.getCharsPerToken() > 0
                ? modelConfig.getCharsPerToken()
                : GenerationTimeEstimator.ESTIMATION_CHARS_PER_TOKEN;
        final int fanIn = MapReduceMath.expectedFanIn(
                sourceBudget, charsPerToken, GenerationTimeEstimator.DEFAULT_EXPECTED_OUTPUT_TOKENS);
        return (chunks + MapReduceMath.reduceCalls(chunks, fanIn)) * windowSeconds;
    }
}

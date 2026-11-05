// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.Calibration;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import org.junit.jupiter.api.Test;

public class RouteEstimatorTest {

    private final RouteEstimator estimator = new RouteEstimator();

    private final GenerationTimeEstimator reference = new GenerationTimeEstimator();

    private final ModelDefinition uncalibrated = new ModelDefinition();

    private static RoutingRule rule(final String onOversize, final int maxChunks) {
        final RoutingRule rule = new RoutingRule();
        rule.setOnOversize(onOversize);
        rule.setMaxChunks(maxChunks);
        return rule;
    }

    private long window(final long chars) {
        return reference.estimateSeconds((int) chars);
    }

    // <editor-fold defaultstate="collapsed" desc="estimateSeconds">
    @Test
    public void estimateSeconds_withoutCalibration_isTheBuiltInModel() {
        // act, assert
        assertThat(estimator.estimateSeconds(12_345L, uncalibrated), is(equalTo(reference.estimateSeconds(12_345))));
    }

    @Test
    public void estimateSeconds_withCalibration_usesTheMeasuredThroughput() {
        // arrange
        final Calibration calibration = new Calibration();
        calibration.setPrefillTokensPerSecond(400.0d);
        calibration.setDecodeTokensPerSecond(9.0d);
        calibration.setCharsPerToken(3.5d);
        final ModelDefinition calibrated = new ModelDefinition();
        calibrated.setCalibration(calibration);

        // act
        final long seconds = estimator.estimateSeconds(50_000L, calibrated);

        // assert
        assertThat(seconds, is(equalTo(reference.estimateSecondsCalibrated(50_000, 400.0d, 9.0d, 3.5d))));
        assertThat(seconds, is(not(equalTo(reference.estimateSeconds(50_000)))));
    }

    @Test
    public void estimateSeconds_moreThanAnIntOfChars_countsAsTheLargestInt() {
        // act, assert
        assertThat(
                estimator.estimateSeconds(Long.MAX_VALUE, uncalibrated),
                is(equalTo(reference.estimateSeconds(Integer.MAX_VALUE))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isCalibrated">
    // isCalibrated: whether the plan and the log name the model's calibration or GPT-6 Luna as the basis
    private static ModelDefinition calibratedWith(final double prefill, final double decode) {
        final Calibration calibration = new Calibration();
        calibration.setPrefillTokensPerSecond(prefill);
        calibration.setDecodeTokensPerSecond(decode);
        final ModelDefinition config = new ModelDefinition();
        config.setCalibration(calibration);
        return config;
    }

    @Test
    public void isCalibrated_bothThroughputsMeasured_isTrue() {
        // act, assert
        assertThat(RouteEstimator.isCalibrated(calibratedWith(400.0d, 9.0d)), is(true));
    }

    @Test
    public void isCalibrated_withoutACalibration_isFalse() {
        // act, assert
        assertThat(RouteEstimator.isCalibrated(uncalibrated), is(false));
    }

    /** A throughput of 0 is "not measured": the estimate falls back to the built-in model, and so does the label. */
    @Test
    public void isCalibrated_aThroughputOfZero_isFalse() {
        // act, assert
        assertThat(RouteEstimator.isCalibrated(calibratedWith(0.0d, 9.0d)), is(false));
        assertThat(RouteEstimator.isCalibrated(calibratedWith(400.0d, 0.0d)), is(false));
        assertThat(
                estimator.estimateSeconds(12_345L, calibratedWith(0.0d, 9.0d)),
                is(equalTo(reference.estimateSeconds(12_345))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateRouteSeconds">
    @Test
    public void estimateRouteSeconds_aFileThatFits_costsOneGenerationOfItsSize() {
        // act, assert
        // exactly the window still fits; the estimate is of the file size, not the window (one large enough
        // that the linear, flat estimate tells the two apart)
        assertThat(
                estimator.estimateRouteSeconds(rule("fail", 0), uncalibrated, 777L, 200_000L, 200_000L, 0),
                is(equalTo(window(777L))));
    }

    @Test
    public void estimateRouteSeconds_overTheWindow_deterministicIsFree() {
        // act, assert
        assertThat(
                estimator.estimateRouteSeconds(rule("deterministic", 0), uncalibrated, 400_000L, 200_001L, 200_000L, 0),
                is(0L));
    }

    @Test
    public void estimateRouteSeconds_overTheWindow_sampleAndFailCostOneWindow() {
        // act, assert
        assertThat(
                estimator.estimateRouteSeconds(rule("sample", 0), uncalibrated, 400_000L, 200_001L, 200_000L, 0),
                is(equalTo(window(200_000L))));
        assertThat(
                estimator.estimateRouteSeconds(rule("fail", 0), uncalibrated, 400_000L, 200_001L, 200_000L, 0),
                is(equalTo(window(200_000L))));
    }

    /*
     * A window of 10 000 characters and no prompt: the run's chunk budget is 8 000 (20 % headroom), the
     * overlap 400, so a chunk advances 7 600 characters. 15 600 characters are exactly 2 chunks, 15 601
     * are 3. A reduce batch of that budget holds 2 part summaries (950 tokens at 4 characters each and a
     * label): 2 partials take one reduce, 3 take two and then one more.
     */
    @Test
    public void estimateRouteSeconds_mapReduce_everyChunkPlusTheReduce() {
        // act, assert
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 15_600L, 10_000L, 0),
                is(equalTo(3 * window(10_000L))));
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 15_601L, 10_000L, 0),
                is(equalTo(6 * window(10_000L))));
    }

    @Test
    public void estimateRouteSeconds_mapReduceOfManyChunks_reducesInRounds() {
        // act, assert
        // 304 400 characters are 40 chunks; two a batch: 20, 10, 5, 3, 2 and 1 reduce calls
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 304_400L, 10_000L, 0),
                is(equalTo(81 * window(10_000L))));
    }

    /** A model's own characters per token size its part summaries; without one, the estimate's 4 does. */
    @Test
    public void estimateRouteSeconds_mapReduce_theFanInReadsTheModelsCharactersPerToken() {
        // arrange
        final ModelDefinition dense = new ModelDefinition();
        dense.setCharsPerToken(2);
        final ModelDefinition unset = new ModelDefinition();
        unset.setCharsPerToken(0);

        // act, assert
        // 3 chunks; at 2 characters a part summary is 1 964 characters, 4 fit the 8 000: one reduce call
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), dense, 1L, 15_601L, 10_000L, 0),
                is(equalTo(4 * window(10_000L))));
        // unset reads as 4 characters a token: 2 fit, so 2 and then 1 reduce calls
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), unset, 1L, 15_601L, 10_000L, 0),
                is(equalTo(6 * window(10_000L))));
    }

    @Test
    public void estimateRouteSeconds_mapReduceOfAWideWindow_reducesSixteenABatch() {
        // act, assert
        // a window of 100 000: budget 80 000 holds 20 part summaries, so a batch is the 16 of the run;
        // 1 520 000 characters are 20 chunks (76 000 a chunk): 2 reduce calls and one of their 2 results
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 1_520_000L, 100_000L, 0),
                is(equalTo(23 * window(100_000L))));
    }

    @Test
    public void estimateRouteSeconds_aPrompt_shrinksTheChunks() {
        // act, assert
        // a prompt of 2 000: window 12 000, budget 7 600, overlap 380, so 15 600 characters are 3 chunks
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 15_600L, 10_000L, 2_000),
                is(equalTo(6 * window(10_000L))));
    }

    @Test
    public void estimateRouteSeconds_sampleChunks_readsMaxChunksOfThem() {
        // act, assert
        // 20 000 characters are 3 chunks
        assertThat(
                estimator.estimateRouteSeconds(rule("sampleChunks", 2), uncalibrated, 1L, 20_000L, 10_000L, 0),
                is(equalTo(3 * window(10_000L))));
        // a cap above the natural count changes nothing
        assertThat(
                estimator.estimateRouteSeconds(rule("sampleChunks", 9), uncalibrated, 1L, 20_000L, 10_000L, 0),
                is(equalTo(6 * window(10_000L))));
    }

    @Test
    public void estimateRouteSeconds_aWindowOfZero_isTheMinimumChunk() {
        // act, assert
        // no room at all: the run still chunks at the 1 000-character minimum
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 1_000L, 0L, 0),
                is(equalTo(window(0L))));
    }

    @Test
    public void estimateRouteSeconds_chunksSizedNextToALongerChunkPrompt_areMore() {
        // arrange: a map-reduce of a source ten windows long
        final RoutingRule rule = new RoutingRule();
        rule.setOnOversize("mapReduce");
        final ModelDefinition model = new ModelDefinition();

        // act
        final long sameAsTheRule = estimator.estimateRouteSeconds(rule, model, 100_000, 100_000, 10_000, 1_000, 1_000);
        final long longer = estimator.estimateRouteSeconds(rule, model, 100_000, 100_000, 10_000, 1_000, 5_000);

        // assert
        assertThat(longer > sameAsTheRule, is(true));
        assertThat(estimator.estimateRouteSeconds(rule, model, 100_000, 100_000, 10_000, 1_000), is(sameAsTheRule));
    }
    // </editor-fold>
}

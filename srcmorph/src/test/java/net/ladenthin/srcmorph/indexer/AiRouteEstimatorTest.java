// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.AiCalibration;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.support.AiGenerationTimeEstimator;
import org.junit.jupiter.api.Test;

public class AiRouteEstimatorTest {

    private final AiRouteEstimator estimator = new AiRouteEstimator();

    private final AiGenerationTimeEstimator reference = new AiGenerationTimeEstimator();

    private final AiGenerationConfig uncalibrated = new AiGenerationConfig();

    private static AiFieldGenerationConfig rule(final String onOversize, final int maxChunks) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
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
        assertThat(estimator.estimateSeconds(12_345L, uncalibrated), is(equalTo(reference.estimateSeconds(12_345))));
    }

    @Test
    public void estimateSeconds_withCalibration_usesTheMeasuredThroughput() {
        final AiCalibration calibration = new AiCalibration();
        calibration.setPrefillTokensPerSecond(400.0d);
        calibration.setDecodeTokensPerSecond(9.0d);
        calibration.setCharsPerToken(3.5d);
        final AiGenerationConfig calibrated = new AiGenerationConfig();
        calibrated.setCalibration(calibration);

        final long seconds = estimator.estimateSeconds(50_000L, calibrated);

        assertThat(seconds, is(equalTo(reference.estimateSecondsCalibrated(50_000, 400.0d, 9.0d, 3.5d))));
        assertThat(seconds, is(not(equalTo(reference.estimateSeconds(50_000)))));
    }

    @Test
    public void estimateSeconds_moreThanAnIntOfChars_countsAsTheLargestInt() {
        assertThat(
                estimator.estimateSeconds(Long.MAX_VALUE, uncalibrated),
                is(equalTo(reference.estimateSeconds(Integer.MAX_VALUE))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateRouteSeconds">
    @Test
    public void estimateRouteSeconds_aFileThatFits_costsOneGenerationOfItsSize() {
        // exactly the window still fits; the estimate is of the file size, not the window
        assertThat(
                estimator.estimateRouteSeconds(rule("fail", 0), uncalibrated, 777L, 5_000L, 5_000L),
                is(equalTo(window(777L))));
    }

    @Test
    public void estimateRouteSeconds_overTheWindow_deterministicIsFree() {
        assertThat(
                estimator.estimateRouteSeconds(rule("deterministic", 0), uncalibrated, 9_000L, 5_001L, 5_000L), is(0L));
    }

    @Test
    public void estimateRouteSeconds_overTheWindow_sampleAndFailCostOneWindow() {
        assertThat(
                estimator.estimateRouteSeconds(rule("sample", 0), uncalibrated, 9_000L, 5_001L, 5_000L),
                is(equalTo(window(5_000L))));
        assertThat(
                estimator.estimateRouteSeconds(rule("fail", 0), uncalibrated, 9_000L, 5_001L, 5_000L),
                is(equalTo(window(5_000L))));
    }

    @Test
    public void estimateRouteSeconds_mapReduce_costsTheChunksPlusOneReduce() {
        // 2000 chars in windows of 1000 are exactly 2 chunks (a ceiling, not 3), 2001 are 3
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 2_000L, 1_000L),
                is(equalTo(3 * window(1_000L))));
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 2_001L, 1_000L),
                is(equalTo(4 * window(1_000L))));
    }

    @Test
    public void estimateRouteSeconds_mapReduce_maxChunksCapsTheChunks() {
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 2), uncalibrated, 1L, 5_000L, 1_000L),
                is(equalTo(3 * window(1_000L))));
        // a cap above the natural count changes nothing
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 9), uncalibrated, 1L, 5_000L, 1_000L),
                is(equalTo(6 * window(1_000L))));
    }

    @Test
    public void estimateRouteSeconds_mapReduce_aWindowOfZero_countsOneCharChunks() {
        assertThat(
                estimator.estimateRouteSeconds(rule("mapReduce", 0), uncalibrated, 1L, 3L, 0L),
                is(equalTo(4 * window(0L))));
    }
    // </editor-fold>
}

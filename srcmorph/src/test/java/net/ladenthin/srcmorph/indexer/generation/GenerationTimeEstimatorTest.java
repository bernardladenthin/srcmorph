// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.jupiter.api.Test;

public class GenerationTimeEstimatorTest {

    private final GenerationTimeEstimator estimator = new GenerationTimeEstimator();

    // <editor-fold defaultstate="collapsed" desc="estimatePromptTokens">
    @Test
    public void estimatePromptTokens_sourceChars_addsTemplateOverheadToScaledChars() {
        // act, assert
        // 4200 / 4.0 = 1050 source tokens, + 1000 template overhead = 2050.
        assertThat(estimator.estimatePromptTokens(4200), is(2050));
    }

    @Test
    public void estimatePromptTokens_zeroChars_isJustTemplateOverhead() {
        // act, assert
        assertThat(estimator.estimatePromptTokens(0), is(GenerationTimeEstimator.PROMPT_TEMPLATE_TOKEN_OVERHEAD));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimatePrefillMillis">
    @Test
    public void estimatePrefillMillis_promptTokens_isTheLatencyPlusThePrompt() {
        // act, assert
        // 2100 + 0.105*1000 = 2205.
        assertThat(estimator.estimatePrefillMillis(1000), is(closeTo(2205.0d, 1e-6d)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateDecodeMillis">
    @Test
    public void estimateDecodeMillis_outputTokens_scalesWithTheOutput() {
        // act, assert
        // 6.45 * 800 = 5160.
        assertThat(estimator.estimateDecodeMillis(800), is(closeTo(5160.0d, 1e-6d)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateSeconds">
    @Test
    public void estimateSeconds_sourceAndOutput_combinesPrefillAndDecodeRoundedToSeconds() {
        // act, assert
        // n=2050: prefill 2100 + 215.25 = 2315.25 ms + decode(800) 5160 ms = 7475.25 ms; x1.15 / 1000 -> 9 s.
        assertThat(estimator.estimateSeconds(4200, 800), is(9L));
    }

    @Test
    public void estimateSeconds_defaultOverload_usesDefaultExpectedOutputTokens() {
        // act, assert
        assertThat(
                estimator.estimateSeconds(4200),
                is(estimator.estimateSeconds(4200, GenerationTimeEstimator.DEFAULT_EXPECTED_OUTPUT_TOKENS)));
    }

    /** A big prompt stays cheap: 400 KB of source (100K tokens) is about 12 s more than none. */
    @Test
    public void estimateSeconds_aBigPrompt_growsLinearlyAndSlowly() {
        // act
        final long small = estimator.estimateSeconds(0, 950);
        final long big = estimator.estimateSeconds(400_000, 950);

        // assert
        assertThat(small, is(10L));
        assertThat(big, is(22L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateSecondsCalibrated">
    @Test
    public void estimateSecondsCalibrated_measuredRates_usesLinearMeasuredThroughput() {
        // act, assert
        // sourceChars 4800 / cpt 4.8 = 1000 + 1000 template = 2000 prompt tokens.
        // (2000/1000 prefill + 800/100 decode) = 10 s; x1.15 margin = 11.5 -> 12 s.
        assertThat(estimator.estimateSecondsCalibrated(4800, 800, 1000.0d, 100.0d, 4.8d), is(12L));
    }

    @Test
    public void estimateSecondsCalibrated_zeroPrefillRate_fallsBackToBuiltInModel() {
        // act, assert
        assertThat(
                estimator.estimateSecondsCalibrated(4800, 800, 0.0d, 100.0d, 4.8d),
                is(estimator.estimateSeconds(4800, 800)));
    }

    @Test
    public void estimateSecondsCalibrated_zeroDecodeRate_fallsBackToBuiltInModel() {
        // act, assert
        assertThat(
                estimator.estimateSecondsCalibrated(4800, 800, 1000.0d, 0.0d, 4.8d),
                is(estimator.estimateSeconds(4800, 800)));
    }

    @Test
    public void estimateSecondsCalibrated_zeroCharsPerToken_usesDefaultEstimationRate() {
        // act, assert
        assertThat(
                estimator.estimateSecondsCalibrated(4800, 800, 1000.0d, 100.0d, 0.0d),
                is(estimator.estimateSecondsCalibrated(4800, 800, 1000.0d, 100.0d, 4.0d)));
    }

    @Test
    public void estimateSecondsCalibrated_defaultOverload_usesDefaultExpectedOutputTokens() {
        // act, assert
        assertThat(
                estimator.estimateSecondsCalibrated(4800, 1000.0d, 100.0d, 4.8d),
                is(estimator.estimateSecondsCalibrated(
                        4800, GenerationTimeEstimator.DEFAULT_EXPECTED_OUTPUT_TOKENS, 1000.0d, 100.0d, 4.8d)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="formatDuration">
    @Test
    public void formatDuration_belowThreshold_rendersSeconds() {
        // act, assert
        assertThat(estimator.formatDuration(89L), is("~89 s"));
    }

    @Test
    public void formatDuration_atThreshold_rendersMinutesRoundedHalfUp() {
        // act, assert
        // 90 s / 60 = 1.5 -> rounds to 2.
        assertThat(estimator.formatDuration(90L), is("~2 min"));
    }

    @Test
    public void formatDuration_zero_rendersSeconds() {
        // act, assert
        assertThat(estimator.formatDuration(0L), is("~0 s"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="measurement proof">
    /**
     * The model was fitted to 1423 single-call GPT-6 Luna files of the 2026-11 measurements. Per prompt
     * size it must meet the measured median time within 10 %, at the median prompt and output of that
     * size, otherwise the constants have drifted.
     */
    @Test
    public void estimate_measuredMediansOfGpt6Luna_reproducedWithinTenPercent() {
        // act, assert
        assertCallWithinTenPercent(1408, 618, 6.0d);
        assertCallWithinTenPercent(3504, 1002, 8.9d);
        assertCallWithinTenPercent(11943, 1707, 14.2d);
        assertCallWithinTenPercent(21315, 1320, 12.1d);
        assertCallWithinTenPercent(97616, 456, 16.8d);
    }

    private void assertCallWithinTenPercent(final int promptTokens, final int outputTokens, final double seconds) {
        final double predicted =
                (estimator.estimatePrefillMillis(promptTokens) + estimator.estimateDecodeMillis(outputTokens))
                        / 1000.0d;
        assertThat(predicted, is(closeTo(seconds, seconds * 0.1d)));
    }
    // </editor-fold>
}

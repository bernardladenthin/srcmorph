// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.jupiter.api.Test;

public class AiGenerationTimeEstimatorTest {

    private final AiGenerationTimeEstimator estimator = new AiGenerationTimeEstimator();

    // <editor-fold defaultstate="collapsed" desc="estimatePromptTokens">
    @Test
    public void estimatePromptTokens_addsTemplateOverheadToScaledChars() {
        // 4200 / 4.8 = 875 source tokens, + 700 template overhead = 1575.
        assertThat(estimator.estimatePromptTokens(4200), is(1575));
    }

    @Test
    public void estimatePromptTokens_zeroChars_isJustTemplateOverhead() {
        assertThat(estimator.estimatePromptTokens(0), is(AiGenerationTimeEstimator.PROMPT_TEMPLATE_TOKEN_OVERHEAD));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="prefill / decode arithmetic">
    @Test
    public void estimatePrefillMillis_appliesLinearPlusQuadraticTerms() {
        // 24.4*1000 + 0.000674*1000^2 = 24400 + 674 = 25074.
        assertThat(estimator.estimatePrefillMillis(1000), is(closeTo(25074.0d, 1e-6d)));
    }

    @Test
    public void estimateDecodeMillis_scalesOutputByLinearPlusContextTerm() {
        // 800 * (56.8 + 0.01568*1000) = 800 * 72.48 = 57984.
        assertThat(estimator.estimateDecodeMillis(1000, 800), is(closeTo(57984.0d, 1e-6d)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="estimateSeconds">
    @Test
    public void estimateSeconds_combinesPrefillAndDecodeRoundedToSeconds() {
        // n=1575: prefill=40101.94 ms + decode(800)=65196.8 ms = 105298.74 ms; ×1.15 margin / 1000 -> 121 s.
        assertThat(estimator.estimateSeconds(4200, 800), is(121L));
    }

    @Test
    public void estimateSeconds_defaultOverload_usesDefaultExpectedOutputTokens() {
        assertThat(
                estimator.estimateSeconds(4200),
                is(estimator.estimateSeconds(4200, AiGenerationTimeEstimator.DEFAULT_EXPECTED_OUTPUT_TOKENS)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="formatDuration">
    @Test
    public void formatDuration_belowThreshold_rendersSeconds() {
        assertThat(estimator.formatDuration(89L), is("~89 s"));
    }

    @Test
    public void formatDuration_atThreshold_rendersMinutesRoundedHalfUp() {
        // 90 s / 60 = 1.5 -> rounds to 2.
        assertThat(estimator.formatDuration(90L), is("~2 min"));
    }

    @Test
    public void formatDuration_zero_rendersSeconds() {
        assertThat(estimator.formatDuration(0L), is("~0 s"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="measurement proof">
    @Test
    public void estimatePrefillMillis_reproducesMeasuredReferencePoints() {
        // The quadratic model was fitted to three end-to-end CPU measurements
        // (Ryzen 7 5800H, gpt-oss-20b UD-Q4_K_XL). It must reproduce each measured
        // prefill within 5 %, otherwise the calibration constants have drifted.
        assertPrefillWithinFivePercent(3309, 88254.16d);
        assertPrefillWithinFivePercent(24081, 978040.23d);
        assertPrefillWithinFivePercent(61484, 4053373.24d);
    }

    private void assertPrefillWithinFivePercent(final int promptTokens, final double measuredMillis) {
        final double predicted = estimator.estimatePrefillMillis(promptTokens);
        final double tolerance = measuredMillis * 0.05d;
        assertThat(predicted, is(closeTo(measuredMillis, tolerance)));
    }
    // </editor-fold>
}

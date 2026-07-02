// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.maven.llamacpp.aiindex.CommonTestFixtures;
import net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationTimings;
import net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider;
import org.junit.jupiter.api.Test;

public class AiCalibrationRunnerTest {

    private static AiPromptPreparationSupport prep() {
        return new AiPromptPreparationSupport(new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions()));
    }

    private static AiGenerationConfig config() {
        final AiGenerationConfig config = new AiGenerationConfig();
        config.setContextSize(2048);
        config.setCharsPerToken(4);
        config.setMaxOutputTokens(64);
        return config;
    }

    @Test
    public void measure_withMockProvider_reportsTheProvidersSyntheticThroughput() throws Exception {
        final AiCalibrationRunner runner = new AiCalibrationRunner();
        final AiCalibrationMeasurement m = runner.measure(
                new MockAiGenerationProvider(), config(), CommonTestFixtures.PROMPT_KEY_FILE_BODY, prep());

        // The mock reports 1000 prefill / 100 decode tok/s and ~4 chars/token (the synthetic source rounds
        // up to whole lines, so it is a hair under 4); the runner surfaces them.
        assertThat(m.prefillTokensPerSecond(), is(1000.0d));
        assertThat(m.decodeTokensPerSecond(), is(100.0d));
        assertThat(m.charsPerToken() > 3.9d && m.charsPerToken() <= 4.0d, is(true));
        assertThat(m.loadSeconds() >= 0.0d, is(true));
    }

    @Test
    public void measure_zeroRateProvider_takesWallClockFallback() throws Exception {
        // A provider that reports zero rates (like the real JNI path) forces the wall-clock fallback; the
        // measured charsPerToken then comes from the config (4), proving the fallback branch ran.
        final AiGenerationProvider zeroRateProvider = new AiGenerationProvider() {
            @Override
            public String generate(final AiGenerationRequest request) {
                return "t";
            }

            @Override
            public AiGenerationTimings generateWithTimings(final AiGenerationRequest request) {
                return new AiGenerationTimings("t", 0, 0.0d, 0, 0.0d);
            }
        };
        final AiCalibrationMeasurement m = new AiCalibrationRunner()
                .measure(zeroRateProvider, config(), CommonTestFixtures.PROMPT_KEY_FILE_BODY, prep());
        assertThat(m.charsPerToken(), is(4.0d));
        assertThat(m.prefillTokensPerSecond() >= 0.0d, is(true));
        assertThat(m.decodeTokensPerSecond() >= 0.0d, is(true));
    }

    @Test
    public void windowChars_isPositiveForANormalWindow() {
        final long window =
                new AiCalibrationRunner().windowChars(config(), CommonTestFixtures.PROMPT_KEY_FILE_BODY, prep());
        assertThat(window > 0, is(true));
    }
}

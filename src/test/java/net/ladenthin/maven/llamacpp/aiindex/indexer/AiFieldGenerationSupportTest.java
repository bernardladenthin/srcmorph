// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.ladenthin.maven.llamacpp.aiindex.CommonTestFixtures;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationResult;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AiFieldGenerationSupportTest {

    /**
     * Simple {@link Log} implementation that captures all messages passed to
     * {@link #warn(CharSequence)} and {@link #info(CharSequence)} for later assertion.
     */
    private static class WarnCapturingLog extends SystemStreamLog {

        private final List<String> capturedWarnings = new ArrayList<>();
        private final List<String> capturedInfos = new ArrayList<>();

        @Override
        public void warn(final CharSequence content) {
            capturedWarnings.add(content.toString());
            super.warn(content);
        }

        @Override
        public void info(final CharSequence content) {
            capturedInfos.add(content.toString());
            super.info(content);
        }

        public List<String> getCapturedWarnings() {
            return capturedWarnings;
        }

        public List<String> getCapturedInfos() {
            return capturedInfos;
        }
    }

    private WarnCapturingLog capturingLog;

    @BeforeEach
    public void setUp() {
        capturingLog = new WarnCapturingLog();
    }

    // <editor-fold defaultstate="collapsed" desc="processFieldGenerations">
    @Test
    public void processFieldGenerations_providerReturnsNonEmpty_noWarningLogged() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("Test", ".java");
        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiGenerationProvider nonEmptyProvider = request -> "A real summary.";
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                nonEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(capturingLog.getCapturedWarnings().isEmpty(), is(true));
    }

    @Test
    public void processFieldGenerations_providerReturnsEmpty_warningContainsContextFile() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("AiGenerationConfig", ".java");
        final AiMdHeader header = new AiMdHeader(
                "AiGenerationConfig.java",
                "1.0",
                "A8CBFAAA",
                "2026-03-20T21:32:55Z",
                "2026-03-20T21:58:26Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiGenerationProvider emptyProvider = request -> "";
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                emptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(),
                contextFile,
                "file",
                "public class AiGenerationConfig {}",
                header);

        // assert
        assertThat(capturingLog.getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(capturingLog.getCapturedWarnings().get(0), containsString(contextFile.toString()));
    }

    @Test
    public void processFieldGenerations_providerReturnsEmpty_warningContainsPromptKey() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("Test", ".java");
        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiGenerationProvider emptyProvider = request -> "";
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                emptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(capturingLog.getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(capturingLog.getCapturedWarnings().get(0), containsString(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
    }

    @Test
    public void processFieldGenerations_providerReturnsEmpty_resultBodyIsEmpty() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("Test", ".java");
        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiGenerationProvider emptyProvider = request -> "";
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                emptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final AiGenerationResult result = support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(result.body(), is(equalTo("")));
    }

    @Test
    public void processFieldGenerations_providerReturnsEmpty_callsProviderOnceWarnsOnceAndBodyEmpty() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("Test", ".java");
        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AtomicInteger callCount = new AtomicInteger(0);
        // The retry mechanism was removed: an empty body now fails fast with a single warning
        // instead of re-inferring (see docs/ai-index-benchmark/gpt-oss-tuning.md, E2).
        final AiGenerationProvider alwaysEmptyProvider = new AiGenerationProvider() {
            @Override
            public String generate(final AiGenerationRequest request) {
                callCount.incrementAndGet();
                return "";
            }
        };
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                alwaysEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final AiGenerationResult result = support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert — provider invoked exactly once (no retries), one warning, empty body
        assertThat(callCount.get(), is(equalTo(1)));
        assertThat(capturingLog.getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(result.body(), is(equalTo("")));
    }

    @Test
    public void processFieldGenerations_providerReturnsEmpty_logsProcessingAndGenerationOnlyNoRetry() throws Exception {
        // arrange
        final Path contextFile = Files.createTempFile("Test", ".java");
        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiGenerationProvider alwaysEmptyProvider = new AiGenerationProvider() {
            @Override
            public String generate(final AiGenerationRequest request) {
                return "";
            }
        };
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                capturingLog,
                alwaysEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert — exactly three INFO lines: the per-file processing/ETA line, the generation line,
        // and the measured actual-duration line. No retry lines (the retry mechanism was removed).
        final List<String> infos = capturingLog.getCapturedInfos();
        assertThat(infos.size(), is(equalTo(3)));

        // First message: the per-file processing line with size + token + duration estimate
        final String processingMsg = infos.get(0);
        assertThat(processingMsg, containsString("Processing file"));
        assertThat(processingMsg, containsString("tokens"));
        assertThat(processingMsg, containsString("estimated"));

        // Second message: the single generation line — temperature + maxInputChars, no retry config
        final String generatingMsg = infos.get(1);
        assertThat(generatingMsg, containsString("Generating field '" + CommonTestFixtures.PROMPT_KEY_FILE_BODY + "'"));
        assertThat(generatingMsg, containsString("temperature=0.15"));
        assertThat(generatingMsg, containsString("maxInputChars="));
        assertThat(generatingMsg.contains("maxRetries"), is(false));
        assertThat(generatingMsg.contains("retryTemperatureIncrement"), is(false));

        // Third message: the measured actual-duration line (real wall time vs the estimate)
        final String generatedMsg = infos.get(2);
        assertThat(generatedMsg, containsString("Generated file"));
        assertThat(generatedMsg, containsString("in "));
        assertThat(generatedMsg, containsString("(actual; estimated "));

        // No INFO line mentions a retry
        for (final String info : infos) {
            assertThat(info.contains("Retrying"), is(false));
        }
    }
    // </editor-fold>
}

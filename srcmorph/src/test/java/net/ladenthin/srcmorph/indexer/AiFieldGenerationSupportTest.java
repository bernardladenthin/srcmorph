// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiFactCounter;
import net.ladenthin.srcmorph.config.AiFactExtractor;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiGenerationResult;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptPreparationSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class AiFieldGenerationSupportTest {

    /**
     * Lower bound on chunks needed to force a second reduce round: strictly above the production
     * {@code MAX_REDUCE_FANIN} (16) so the reduce cannot combine every partial in a single call.
     */
    private static final long MIN_CHUNKS_FOR_TWO_REDUCE_ROUNDS = 16L;

    /**
     * Captures the SLF4J log output of {@link AiFieldGenerationSupport} via a logback
     * {@link ListAppender} attached directly to its logger, replacing the constructor-injected
     * Maven {@code Log} the production class used previously.
     */
    private ListAppender<ILoggingEvent> logAppender;

    private static Logger loggerUnderTest() {
        return (Logger) LoggerFactory.getLogger(AiFieldGenerationSupport.class);
    }

    @BeforeEach
    public void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        final Logger logger = loggerUnderTest();
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        logger.addAppender(logAppender);
    }

    @AfterEach
    public void tearDown() {
        loggerUnderTest().detachAppender(logAppender);
    }

    private List<String> capturedMessages(final Level level) {
        final List<String> messages = new ArrayList<>();
        for (final ILoggingEvent event : logAppender.list) {
            if (event.getLevel() == level) {
                messages.add(event.getFormattedMessage());
            }
        }
        return messages;
    }

    private List<String> getCapturedWarnings() {
        return capturedMessages(Level.WARN);
    }

    private List<String> getCapturedInfos() {
        return capturedMessages(Level.INFO);
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
                nonEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(getCapturedWarnings().isEmpty(), is(true));
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
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(contextFile.toString()));
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
                emptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
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
                alwaysEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final AiGenerationResult result = support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert — provider invoked exactly once (no retries), one warning, empty body
        assertThat(callCount.get(), is(equalTo(1)));
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
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
                alwaysEmptyProvider,
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.processFieldGenerations(
                CommonTestFixtures.createFileFieldGenerations(), contextFile, "file", "public class Test {}", header);

        // assert — exactly three INFO lines: the per-file processing/ETA line, the generation line,
        // and the measured actual-duration line. No retry lines (the retry mechanism was removed).
        final List<String> infos = getCapturedInfos();
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

    // <editor-fold defaultstate="collapsed" desc="onOversize strategies">

    /** A model whose tiny static input window forces every non-trivial source to be over-window. */
    private static AiModelDefinitionSupport tinyWindowModel(final String key) {
        final AiModelDefinition def = new AiModelDefinition();
        def.setKey(key);
        def.setModelPath("unused.gguf");
        // contextSize 1 x charsPerToken 1 minus overhead -> computed maxInputChars clamps to 0, so any
        // non-empty source is over-window and triggers the onOversize strategy.
        def.setContextSize(1);
        def.setCharsPerToken(1);
        return new AiModelDefinitionSupport(Arrays.asList(def));
    }

    private AiFieldGenerationSupport supportWith(final AiGenerationProvider provider, final String modelKey) {
        return supportWithModels(provider, tinyWindowModel(modelKey));
    }

    private AiFieldGenerationSupport supportWithModels(
            final AiGenerationProvider provider, final AiModelDefinitionSupport models) {
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        return new AiFieldGenerationSupport(provider, new AiPromptPreparationSupport(promptSupport), models);
    }

    /** A model with a normal window, so a small source fits and is NOT treated as over-window. */
    private static AiModelDefinitionSupport normalWindowModel(final String key) {
        final AiModelDefinition def = new AiModelDefinition();
        def.setKey(key);
        def.setModelPath("unused.gguf");
        def.setContextSize(2048);
        def.setCharsPerToken(4);
        def.setMaxOutputTokens(64);
        return new AiModelDefinitionSupport(Arrays.asList(def));
    }

    private static AiFieldGenerationConfig oversizeRule(
            final String modelKey, final String onOversize, final int maxChunks) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(modelKey);
        rule.setOnOversize(onOversize);
        rule.setMaxChunks(maxChunks);
        return rule;
    }

    private static String largeSource(final int lines) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            sb.append("line ").append(i).append('\n');
        }
        return sb.toString();
    }

    @Test
    public void onOversize_deterministic_skipsModelAndEmitsMetadataBody() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "MODEL OUTPUT";
        };
        final AiFieldGenerationSupport support = supportWith(provider, "m");

        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(oversizeRule("m", "deterministic", 0)),
                contextFile,
                "file",
                largeSource(500),
                anyHeader());

        assertThat(calls.get(), is(equalTo(0)));
        assertThat(result.body(), containsString("deterministically (no AI)"));
    }

    @Test
    public void onOversize_mapReduce_callsModelPerChunkPlusReduce() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };
        final AiFieldGenerationSupport support = supportWith(provider, "m");

        // ~3600 chars > 1000-char chunk budget -> several chunks; maxChunks=2 caps to 2 mapped + 1 reduce.
        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(oversizeRule("m", "mapReduce", 2)),
                contextFile,
                "file",
                largeSource(600),
                anyHeader());

        assertThat(calls.get(), is(equalTo(3)));
        assertThat(result.body(), is(equalTo("PARTIAL")));
    }

    @Test
    public void onOversize_mapReduce_withFacts_prependsExactWholeFileCountsToBody() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AiGenerationProvider provider = request -> "PARTIAL";
        final AiFieldGenerationSupport support = supportWith(provider, "m");

        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 2);
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        // largeSource(600) has exactly 600 lines beginning with "line "; the AI (mock) never sees them all,
        // but the deterministic facts block counts every one over the FULL source and is prepended.
        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(rule), contextFile, "file", largeSource(600), anyHeader());

        assertThat(result.body(), startsWith(AiFactExtractor.FACTS_HEADER));
        assertThat(result.body(), containsString("lines: 600"));
        assertThat(result.body(), containsString("PARTIAL"));
    }

    @Test
    public void facts_prependedOnNonOversizeFile() throws Exception {
        // A small source that fits the window (NOT oversize) still gets the exact facts block prepended,
        // so downstream agents get authoritative counts in every summary, not just huge-file ones.
        final Path contextFile = Files.createTempFile("Small", ".java");
        final AiGenerationProvider provider = request -> "AI SUMMARY";
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey("m");
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel("boolean fields");
        counter.setPattern("\\bboolean\\b");
        rule.setFacts(Collections.singletonList(counter));

        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(rule), contextFile, "file", "boolean a; boolean b; int c;", anyHeader());

        assertThat(result.body(), startsWith(AiFactExtractor.FACTS_HEADER));
        assertThat(result.body(), containsString("boolean fields: 2"));
        assertThat(result.body(), containsString("AI SUMMARY"));
    }

    private static AiFieldGenerationConfig plainRoute(final String modelKey) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(modelKey);
        return rule;
    }

    @Test
    public void generate_contextOverflowThenSuccess_retriesWithSmallerWindowAndSucceeds() throws Exception {
        final Path contextFile = Files.createTempFile("Dense", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        // Fits the char window but the provider rejects the first prompt as over-context (token-dense);
        // the retry with a smaller window then succeeds.
        final AiGenerationProvider provider = request -> {
            if (calls.getAndIncrement() == 0) {
                throw new RuntimeException("request (16549 tokens) exceeds the available context size (16384 tokens)");
            }
            return "OK";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(plainRoute("m")), contextFile, "file", "small source", anyHeader());

        assertThat(result.body(), is(equalTo("OK")));
        assertThat(calls.get(), is(equalTo(2)));
    }

    @Test
    public void generate_persistentContextOverflow_rethrowsAfterRetrying() throws Exception {
        final Path contextFile = Files.createTempFile("Dense", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new RuntimeException("request exceeds the available context size (16384 tokens)");
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        assertThrows(
                RuntimeException.class,
                () -> support.processFieldGenerations(
                        Collections.singletonList(plainRoute("m")), contextFile, "file", "small source", anyHeader()));
        // Retried before giving up (more than the single initial attempt).
        assertThat(calls.get() > 1, is(true));
    }

    @Test
    public void generate_nonOverflowRuntimeException_propagatesWithoutRetry() throws Exception {
        final Path contextFile = Files.createTempFile("X", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new RuntimeException("some unrelated failure");
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        assertThrows(
                RuntimeException.class,
                () -> support.processFieldGenerations(
                        Collections.singletonList(plainRoute("m")), contextFile, "file", "small source", anyHeader()));
        assertThat(calls.get(), is(equalTo(1)));
    }

    @Test
    public void onOversize_mapReduce_unbounded_reducesHierarchicallyInMultipleRounds() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };
        final AiFieldGenerationSupport support = supportWith(provider, "m");

        // maxChunks=0 (unbounded) over a large source -> far more than MAX_REDUCE_FANIN chunks, so the
        // reduce cannot combine every partial in one call and must recurse: a second reduce round appears.
        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(oversizeRule("m", "mapReduce", 0)),
                contextFile,
                "file",
                largeSource(3000),
                anyHeader());

        final List<String> infos = getCapturedInfos();
        final long mapChunks =
                infos.stream().filter(m -> m.contains("summarized (")).count();
        final boolean hasRound1 = infos.stream().anyMatch(m -> m.contains("reduce round 1:"));
        final boolean hasRound2 = infos.stream().anyMatch(m -> m.contains("reduce round 2:"));

        // Whole file mapped (many chunks) and the reduce ran in at least two rounds (hierarchical).
        assertThat(mapChunks > MIN_CHUNKS_FOR_TWO_REDUCE_ROUNDS, is(true));
        assertThat(hasRound1, is(true));
        assertThat(hasRound2, is(true));
        // More provider calls than a single-level reduce (chunks + 1) would make.
        assertThat(calls.get() > mapChunks + 1, is(true));
        assertThat(result.body(), is(equalTo("PARTIAL")));
    }

    @Test
    public void onOversize_sample_singleCallOnTrimmedHead() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "SAMPLED";
        };
        final AiFieldGenerationSupport support = supportWith(provider, "m");

        final AiGenerationResult result = support.processFieldGenerations(
                Collections.singletonList(oversizeRule("m", "sample", 0)),
                contextFile,
                "file",
                largeSource(500),
                anyHeader());

        assertThat(calls.get(), is(equalTo(1)));
        assertThat(result.body(), is(equalTo("SAMPLED")));
    }

    private static AiMdHeader anyHeader() {
        return new AiMdHeader(
                "Huge.java",
                "1.0",
                "ABCD1234",
                "2026-01-01T00:00:00Z",
                "2026-01-01T00:01:00Z",
                "0.1.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
    }
    // </editor-fold>
}

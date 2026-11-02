// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
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
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;
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
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import net.ladenthin.srcmorph.prompt.AiPromptPreparationSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiContextOverflowException;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.AiIncompleteAnswerException;
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

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_providerReturnsNonEmpty_noWarningLogged() throws Exception {
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
        support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(getCapturedWarnings().isEmpty(), is(true));
    }

    @Test
    public void generate_providerReturnsEmpty_warningContainsContextFile() throws Exception {
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
        support.generate(
                CommonTestFixtures.createFileFieldGeneration(),
                contextFile,
                "file",
                "public class AiGenerationConfig {}",
                header);

        // assert
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(contextFile.toString()));
    }

    @Test
    public void generate_providerReturnsEmpty_warningContainsPromptKey() throws Exception {
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
        support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}", header);

        // assert
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
    }

    @Test
    public void generate_providerReturnsEmpty_resultBodyIsEmpty() throws Exception {
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
        final AiGenerationResult result = support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}", header);

        // assert -- an empty answer is no summary: nothing to write, and the result says why
        assertThat(result.body(), is(equalTo("")));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), containsString("answered nothing"));
    }

    @Test
    public void generate_providerReturnsEmpty_callsProviderOnceWarnsOnceAndBodyEmpty() throws Exception {
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
        final AiGenerationResult result = support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}", header);

        // assert — provider invoked exactly once (no retries), one warning, empty body
        assertThat(callCount.get(), is(equalTo(1)));
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(result.body(), is(equalTo("")));
    }

    @Test
    public void generate_providerReturnsEmpty_logsProcessingAndGenerationButNoGeneratedLine() throws Exception {
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
        support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}", header);

        // assert — exactly two INFO lines: the per-file processing/ETA line and the generation line.
        // An empty answer generated nothing, so there is no measured actual-duration line, and no retry.
        final List<String> infos = getCapturedInfos();
        assertThat(infos.size(), is(equalTo(2)));

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

        final AiGenerationResult result = support.generate(
                (oversizeRule("m", "deterministic", 0)), contextFile, "file", largeSource(500), anyHeader());

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
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // ~70 KB over a window of about 7 KB -> several chunks that fit; sampleChunks 2 maps 2 of them + 1 reduce.
        final AiGenerationResult result = support.generate(
                (oversizeRule("m", "sampleChunks", 2)), contextFile, "file", largeSource(7000), anyHeader());

        assertThat(calls.get(), is(equalTo(3)));
        assertThat(result.body(), startsWith("**Coverage:** 2 of "));
        assertThat(result.body(), containsString(" chunks summarized (sampled)\n\nPARTIAL"));
    }

    @Test
    public void onOversize_mapReduce_readsEveryChunk_andSaysSo() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };
        // a maxChunks of the rule does not sample under mapReduce
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (oversizeRule("m", "mapReduce", 2)),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(7000),
                        anyHeader());

        final long mapped = getCapturedInfos().stream()
                .filter(m -> m.contains("summarized ("))
                .count();
        assertThat(mapped > 2, is(true));
        assertThat(
                result.body(),
                is(equalTo("**Coverage:** " + mapped + " of " + mapped + " chunks summarized\n\nPARTIAL")));
        assertThat(calls.get(), is(equalTo((int) mapped + 1)));
    }

    @Test
    public void onOversize_mapReduce_withFacts_prependsExactWholeFileCountsToBody() throws Exception {
        final Path contextFile = Files.createTempFile("Huge", ".java");
        final AiGenerationProvider provider = request -> "PARTIAL";
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 0);
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        // largeSource(7000) has exactly 7000 lines beginning with "line "; no call of the mock sees them all,
        // but the deterministic facts block counts every one over the FULL source and is prepended.
        final AiGenerationResult result = support.generate((rule), contextFile, "file", largeSource(7000), anyHeader());

        assertThat(result.body(), startsWith(AiFactExtractor.FACTS_HEADER));
        assertThat(result.body(), containsString("lines: 7000"));
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

        final AiGenerationResult result =
                support.generate((rule), contextFile, "file", "boolean a; boolean b; int c;", anyHeader());

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

        final AiGenerationResult result =
                support.generate((plainRoute("m")), contextFile, "file", "small source", anyHeader());

        assertThat(result.body(), is(equalTo("OK")));
        assertThat(calls.get(), is(equalTo(2)));
    }

    @Test
    public void generate_remoteContextOverflowThenSuccess_retriesWithSmallerWindowAndSucceeds() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            if (calls.incrementAndGet() == 1) {
                throw new AiContextOverflowException("http://h answered HTTP 413: ");
            }
            return "OK";
        };
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", null)),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.body(), is(equalTo("OK")));
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
                () -> support.generate((plainRoute("m")), contextFile, "file", "small source", anyHeader()));
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
                () -> support.generate((plainRoute("m")), contextFile, "file", "small source", anyHeader()));
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
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // maxChunks=0 (unbounded) over a large source -> far more than MAX_REDUCE_FANIN chunks, so the
        // reduce cannot combine every partial in one call and must recurse: a second reduce round appears.
        final AiGenerationResult result = support.generate(
                (oversizeRule("m", "mapReduce", 0)), contextFile, "file", largeSource(30000), anyHeader());

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
        assertThat(result.body(), endsWith(" chunks summarized\n\nPARTIAL"));
    }

    @Test
    public void onOversize_mapReduce_aWindowWithoutRoomForAChunk_failsTheFileNamingTheRoom() throws Exception {
        // arrange: the tiny window holds less than the minimum chunk next to the prompts, so every chunk call
        // would be cut and most of the source never read
        final AtomicInteger calls = new AtomicInteger();
        final AiFieldGenerationSupport support = supportWith(
                request -> {
                    calls.incrementAndGet();
                    return "PARTIAL";
                },
                "m");

        // act
        final java.io.IOException failed = org.junit.jupiter.api.Assertions.assertThrows(
                java.io.IOException.class,
                () -> support.generate(
                        (oversizeRule("m", "mapReduce", 0)),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(600),
                        anyHeader()));

        // assert
        assertThat(failed.getMessage(), containsString("next to its map and reduce prompts, fewer than 1000"));
        assertThat(calls.get(), is(0));
    }

    @Test
    public void contextOverflow_ofASourceThatFits_isDecidedByOnOversize() throws Exception {
        // arrange: the source fits the window by characters, but the provider refuses it as a context
        // overflow until a smaller window has cut a thousand characters off
        final String source = largeSource(600);
        final AiGenerationProvider provider = request -> {
            if (request.sourceText().length() > source.length() - 1000) {
                throw new java.io.IOException("the request exceeds the available context size");
            }
            return "BODY";
        };

        // act
        final java.io.IOException failed = org.junit.jupiter.api.Assertions.assertThrows(
                java.io.IOException.class,
                () -> supportWithModels(provider, normalWindowModel("m"))
                        .generate(
                                oversizeRule("m", "fail", 0),
                                Files.createTempFile("A", ".java"),
                                "file",
                                source,
                                anyHeader()));
        final AiGenerationResult sampled = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        oversizeRule("m", "sample", 0),
                        Files.createTempFile("B", ".java"),
                        "file",
                        source,
                        anyHeader());

        // assert: fail refuses instead of writing a cut summary; sample says how much of the head it read
        assertThat(failed.getMessage(), containsString("onOversize: fail"));
        assertThat(sampled.body(), startsWith("**Coverage:** the first "));
        assertThat(sampled.body(), containsString(" of " + source.length() + " characters (sampled head)"));
        assertThat(sampled.body(), endsWith("BODY"));
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

        final AiGenerationResult result =
                support.generate((oversizeRule("m", "sample", 0)), contextFile, "file", largeSource(500), anyHeader());

        assertThat(calls.get(), is(equalTo(1)));
        assertThat(result.body(), startsWith("**Coverage:** the first "));
        assertThat(result.body(), endsWith(" of 4390 characters (sampled head)\n\nSAMPLED"));
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

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    private static AiFieldGenerationConfig normalRule(final String modelKey, final String onIncomplete) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(modelKey);
        rule.setOnIncomplete(onIncomplete);
        return rule;
    }

    private static AiIncompleteAnswerException cutOff(final String partialText) {
        return new AiIncompleteAnswerException(AiIncompleteAnswerException.Reason.TRUNCATED, "cut off", partialText);
    }

    @Test
    public void mapReduce_aPartSummaryCutAtTheOutputBudget_isKeptAndNamed() throws Exception {
        // arrange: every map answer is cut off (a model listing entries one by one); the reduce answers
        final AiGenerationProvider provider = request -> {
            if (!request.sourceText().contains(AiMapReduceSupport.PART_LABEL + "1:")) {
                throw cutOff("- `A_00001` -> one\n- `A_00002` -> two");
            }
            return "FINAL";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert
        assertThat(result.complete(), is(true));
        assertThat(result.body(), containsString(" part summaries cut at the output budget)\n\nFINAL"));
    }

    @Test
    public void mapReduce_aFinalAnswerCutAtTheOutputBudget_stillFailsTheFile() {
        // arrange: the parts are complete, the final reduce is cut off
        final AiGenerationProvider provider = request -> {
            if (request.sourceText().contains(AiMapReduceSupport.PART_LABEL + "1:")) {
                throw cutOff("half a summ");
            }
            return "PARTIAL";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act + assert
        final AiGenerationResult result = assertDoesNotThrowGenerate(support);
        assertThat(result.complete(), is(false));
    }

    private static AiGenerationResult assertDoesNotThrowGenerate(final AiFieldGenerationSupport support) {
        return org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader()));
    }

    @Test
    public void incomplete_retryOnce_asksAgainAndTakesTheSecondAnswer() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> calls.incrementAndGet() == 1 ? "" : "A summary.";
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", "retryOnce")),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.complete(), is(true));
        assertThat(result.body(), is(equalTo("A summary.")));
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString("asking once more"));
    }

    @Test
    public void incomplete_retryOnce_secondIncompleteAnswerFails() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return " ";
        };
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", "retryOnce")),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.complete(), is(false));
    }

    @Test
    public void incomplete_failByDefault_cutOffAnswerIsNotTaken() throws Exception {
        final AiGenerationProvider provider = request -> {
            throw cutOff("half a sum");
        };
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", null)),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is(equalTo("cut off")));
        assertThat(
                getCapturedWarnings().get(0),
                containsString("(field '" + CommonTestFixtures.PROMPT_KEY_FILE_BODY + "', TRUNCATED)"));
    }

    @Test
    public void incomplete_keep_takesTheTextOfACutOffAnswer() throws Exception {
        final AiGenerationProvider provider = request -> {
            throw cutOff("half a sum");
        };
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", "keep")),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(result.complete(), is(true));
        assertThat(result.body(), is(equalTo("half a sum")));
        assertThat(getCapturedWarnings().get(0), containsString("kept as it is"));
    }

    @Test
    public void incomplete_keep_cutOffAnswerWithoutTextAndAnEmptyAnswerStillFail() throws Exception {
        final AiGenerationProvider cutOffEmpty = request -> {
            throw cutOff(" ");
        };
        final AiGenerationProvider empty = request -> "";
        for (final AiGenerationProvider provider : Arrays.asList(cutOffEmpty, empty)) {
            final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                    .generate(
                            (normalRule("m", "keep")),
                            Files.createTempFile("Test", ".java"),
                            "file",
                            "public class Test {}",
                            anyHeader());
            assertThat(result.complete(), is(false));
        }
    }

    @Test
    public void incomplete_responseWithoutAnswer_isIncompleteAndNotRetriedUnderFail() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new AiIncompleteAnswerException(AiIncompleteAnswerException.Reason.NO_ANSWER, "no choice");
        };
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        (normalRule("m", "fail")),
                        Files.createTempFile("Test", ".java"),
                        "file",
                        "public class Test {}",
                        anyHeader());

        assertThat(calls.get(), is(equalTo(1)));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is(equalTo("no choice")));
    }

    /** Records every request and answers the n-th (from 1) as the script says. */
    private static final class ScriptedProvider implements AiGenerationProvider {

        /** The answer to one call. */
        interface Script {
            String answer(int call) throws java.io.IOException;
        }

        private final Script script;
        private final List<AiGenerationRequest> generated = new ArrayList<>();

        ScriptedProvider(final Script script) {
            this.script = script;
        }

        @Override
        public String generate(final AiGenerationRequest request) throws java.io.IOException {
            generated.add(request);
            return script.answer(generated.size());
        }

        /** The source of the n-th call (from 1) without its chunk label line. */
        String chunkOf(final int call) {
            final String text = generated.get(call - 1).sourceText();
            return text.substring(text.indexOf('\n') + 1);
        }
    }

    @Test
    public void incomplete_mapReduceChunkWithAnEmptyAnswer_isReadInTwoHalves() throws Exception {
        // arrange: the second chunk's answer is empty (a model that spent its whole budget reasoning)
        final ScriptedProvider provider = new ScriptedProvider(n -> n == 2 ? "" : "PARTIAL" + n);
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert: the chunk was read again as its two halves, and every answer reached the reduce
        assertThat(result.complete(), is(true));
        assertThat(provider.generated.size(), is(5));
        assertThat(provider.generated.get(2).sourceText(), containsString(", half 1:\n"));
        assertThat(provider.generated.get(3).sourceText(), containsString(", half 2:\n"));
        assertThat(provider.chunkOf(3) + provider.chunkOf(4), is(equalTo(provider.chunkOf(2))));
        final String reduce = provider.generated.get(4).sourceText();
        assertThat(reduce, containsString("PARTIAL1"));
        assertThat(reduce, containsString("PARTIAL3"));
        assertThat(reduce, containsString("PARTIAL4"));
    }

    @Test
    public void incomplete_mapReduceChunkCutBeforeAnyText_isReadInTwoHalves() throws Exception {
        // arrange: the first map answer is cut at the output budget before it wrote anything
        final ScriptedProvider provider = new ScriptedProvider(n -> {
            if (n == 1) {
                throw cutOff("");
            }
            return "PARTIAL" + n;
        });
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert
        assertThat(result.complete(), is(true));
        assertThat(provider.generated.get(1).sourceText(), containsString(", half 1:\n"));
        assertThat(provider.generated.get(2).sourceText(), containsString(", half 2:\n"));
        assertThat(provider.chunkOf(2) + provider.chunkOf(3), is(equalTo(provider.chunkOf(1))));
    }

    @Test
    public void incomplete_mapReduceChunkEmptyDownToItsSmallestHalf_makesTheFileIncomplete() throws Exception {
        // arrange: every answer is empty; a half below twice the minimum chunk is not split again
        final ScriptedProvider provider = new ScriptedProvider(n -> "");
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert: each call read the first half of the one before, down to a piece too small to split
        assertThat(result.complete(), is(false));
        final int calls = provider.generated.size();
        for (int call = 2; call <= calls; call++) {
            assertThat(provider.chunkOf(call - 1), startsWith(provider.chunkOf(call)));
            assertThat(provider.chunkOf(call - 1).length() >= 2 * AiMapReduceSupport.MIN_CHUNK_CHARS, is(true));
        }
        assertThat(provider.chunkOf(calls).length() < 2 * AiMapReduceSupport.MIN_CHUNK_CHARS, is(true));
    }

    @Test
    public void incomplete_withFacts_theFactsLineDoesNotMaskAnEmptyAnswer() throws Exception {
        final AiFieldGenerationConfig rule = normalRule("m", null);
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel("classes");
        counter.setPattern("\\bclass\\b");
        rule.setFacts(Collections.singletonList(counter));
        final AiGenerationResult result = supportWithModels(request -> "", normalWindowModel("m"))
                .generate((rule), Files.createTempFile("Test", ".java"), "file", "public class Test {}", anyHeader());

        assertThat(result.complete(), is(false));
        assertThat(result.body(), is(equalTo("")));
    }
    // </editor-fold>

    @Test
    public void onOversize_failAtRunTime_isAnErrorNotATrimmedSummary() throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "MODEL OUTPUT";
        };
        final java.io.IOException thrown = assertThrows(
                java.io.IOException.class,
                () -> supportWith(provider, "m")
                        .generate(
                                (oversizeRule("m", "fail", 0)),
                                Files.createTempFile("Huge", ".java"),
                                "file",
                                largeSource(500),
                                anyHeader()));

        assertThat(calls.get(), is(equalTo(0)));
        assertThat(thrown.getMessage(), containsString("onOversize: fail"));
    }

    @Test
    public void window_isComputedForEveryFile_fromItsOwnName() throws Exception {
        // The prompt contains the file name, so a long name leaves less room for the source. A short-named
        // file whose source fills exactly its own budget is not trimmed, even after a long-named one.
        final AiModelDefinitionSupport models = normalWindowModel("m");
        final AiPromptPreparationSupport preparation =
                new AiPromptPreparationSupport(new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions()));
        final Path shortName = Files.createTempFile("A", ".java");
        final Path longName = Files.createTempFile("A_very_long_file_name_that_eats_into_the_prompt_budget", ".java");
        final int budget = (int) AiInputWindowCalculator.availableSourceChars(
                models.getConfig("m"),
                preparation.getBasePromptLength(CommonTestFixtures.PROMPT_KEY_FILE_BODY, shortName));
        final AiFieldGenerationSupport support = supportWithModels(request -> "OK", models);
        final AiFieldGenerationConfig rule = normalRule("m", null);

        support.generate((rule), longName, "file", "x", anyHeader());
        final String fits = largeSource(budget).substring(0, budget);
        final AiGenerationResult result = support.generate((rule), shortName, "file", fits, anyHeader());

        assertThat(result.body(), is(equalTo("OK")));
        for (final String warning : getCapturedWarnings()) {
            assertThat(warning.contains("Trimmed AI input"), is(false));
        }
    }

    @Test
    public void takeUsage_sumsTheCallsOfAFile_andStartsAnew() throws Exception {
        final AiGenerationProvider provider = new AiGenerationProvider() {
            @Override
            public String generate(final net.ladenthin.srcmorph.document.AiGenerationRequest request) {
                return "PARTIAL";
            }

            @Override
            public net.ladenthin.srcmorph.provider.AiGenerationUsage lastUsage() {
                return new net.ladenthin.srcmorph.provider.AiGenerationUsage(1, 100, 10, 0, "stop");
            }
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        support.generate(
                oversizeRule("m", "sampleChunks", 2),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());
        final net.ladenthin.srcmorph.provider.AiGenerationUsage usage = support.takeUsage();

        // two chunks and one reduce
        assertThat(usage.calls(), is(3));
        assertThat(usage.promptTokens(), is(300L));
        assertThat(usage.completionTokens(), is(30L));
        assertThat(usage.finishReason(), is(equalTo("stop")));
        assertThat(support.takeUsage(), is(net.ladenthin.srcmorph.provider.AiGenerationUsage.NONE));
    }

    @Test
    public void takeUsage_anEmptyAnswer_stillCounts() throws Exception {
        final AiFieldGenerationSupport support = supportWithModels(request -> "", normalWindowModel("m"));

        support.generate(normalRule("m", null), Files.createTempFile("Small", ".java"), "file", "x", anyHeader());

        // the empty answer was one call; a lambda provider reports no tokens
        assertThat(support.takeUsage().calls(), is(1));
    }

    // <editor-fold defaultstate="collapsed" desc="map and reduce prompts">
    private static AiPromptDefinition prompt(final String key, final String template) {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey(key);
        definition.setTemplate(template);
        return definition;
    }

    private static List<AiGenerationRequest> mapReduceRequests(final String mapKey, final String reduceKey)
            throws Exception {
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final List<AiPromptDefinition> prompts = new ArrayList<>(CommonTestFixtures.createFilePromptDefinitions());
        prompts.add(prompt("map", "MAP"));
        prompts.add(prompt("reduce", "REDUCE"));
        final AiFieldGenerationSupport support = new AiFieldGenerationSupport(
                provider, new AiPromptPreparationSupport(new AiPromptSupport(prompts)), normalWindowModel("m"));
        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 0);
        rule.setMapPromptKey(mapKey);
        rule.setReducePromptKey(reduceKey);
        support.generate(rule, Files.createTempFile("Huge", ".java"), "file", largeSource(7000), anyHeader());
        return requests;
    }

    @Test
    public void mapReduce_mapCallsTakeTheMapPromptAndAChunkLabel() throws Exception {
        // act
        final List<AiGenerationRequest> requests = mapReduceRequests("map", "reduce");

        // assert: the first call maps chunk 1, every map call is labelled
        final AiGenerationRequest first = requests.get(0);
        assertThat(first.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY + "+map")));
        assertThat(first.sourceText(), startsWith("Chunk 1 of "));
        assertThat(first.sourceText(), containsString("\nline 0\n"));
    }

    @Test
    public void mapReduce_reduceCallsTakeTheReducePromptAndThePartSummaries() throws Exception {
        // act
        final List<AiGenerationRequest> requests = mapReduceRequests("map", "reduce");

        // assert: the last call reduces
        final AiGenerationRequest last = requests.get(requests.size() - 1);
        assertThat(last.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY + "+reduce")));
        assertThat(last.sourceText(), startsWith(AiMapReduceSupport.PART_LABEL + "1:\nS\n"));
    }

    @Test
    public void mapReduce_withoutMapAndReducePrompts_takesTheRulePromptForEveryCall() throws Exception {
        // act
        final List<AiGenerationRequest> requests = mapReduceRequests(null, null);

        // assert
        for (final AiGenerationRequest request : requests) {
            assertThat(request.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY)));
        }
        assertThat(requests.get(0).sourceText(), startsWith("Chunk 1 of "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="counted prompt tokens">
    /** A provider that counts a prompt's tokens with {@code tokens} and records every request it answers. */
    private static final class CountingProvider implements AiGenerationProvider {

        private final ToIntFunction<AiGenerationRequest> tokens;
        private final List<AiGenerationRequest> generated = new ArrayList<>();

        CountingProvider(final ToIntFunction<AiGenerationRequest> tokens) {
            this.tokens = tokens;
        }

        @Override
        public String generate(final AiGenerationRequest request) {
            generated.add(request);
            return "S";
        }

        @Override
        public OptionalInt promptTokens(final AiGenerationRequest request) {
            return OptionalInt.of(tokens.applyAsInt(request));
        }
    }

    /** The prompt tokens a call of {@link #normalWindowModel} may take: its 2048 less its 64 of answer. */
    private static final int NORMAL_WINDOW_TOKEN_LIMIT = 2048 - 64 - AiTokenWindow.UNCOUNTED_TOKENS;

    @Test
    public void countedTokens_aSourceOverTheEstimateThatFitsInTokens_takesOneCall() throws Exception {
        // arrange: about 14 000 chars are over the estimated window (2048 tokens x 4 chars), at 8 chars per token
        // they fit
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length() / 8);
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));
        final String source = largeSource(1_500);

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "mapReduce", 0), Files.createTempFile("Dense", ".java"), "file", source, anyHeader());

        // assert
        assertThat(result.body(), is(equalTo("S")));
        assertThat(provider.generated.size(), is(1));
        assertThat(provider.generated.get(0).sourceText(), is(equalTo(source)));
    }

    @Test
    public void countedTokens_aPromptOverTheLimit_isNeverSent() throws Exception {
        // arrange: one char per token, so about 3 500 chars fit the estimate (2048 tokens x 4 chars) but not the
        // model's 1984 prompt tokens
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length());
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Dense", ".java"),
                "file",
                largeSource(400),
                anyHeader());

        // assert: the file is map-reduced, and every call the provider answered fits
        assertThat(result.complete(), is(true));
        assertThat(provider.generated.size() > 1, is(true));
        for (final AiGenerationRequest request : provider.generated) {
            assertThat(request.sourceText().length() <= NORMAL_WINDOW_TOKEN_LIMIT, is(true));
        }
    }

    @Test
    public void countedTokens_aSourceOverTheEstimate_isChunkedByTheMeasuredDensity() throws Exception {
        // arrange: one char per token, about 8 900 chars: a chunk of the estimate (about 5 800 chars) would be three
        // times the limit, a chunk of the measured window is not
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length());
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Dense", ".java"),
                "file",
                largeSource(1_000),
                anyHeader());

        // assert: no map call needed a smaller window, so every chunk read the whole of itself
        for (final AiGenerationRequest request : provider.generated) {
            assertThat(request.sourceText().length() <= NORMAL_WINDOW_TOKEN_LIMIT, is(true));
            assertThat(request.sourceText().contains("[EOF - source was truncated]"), is(false));
        }
    }

    @Test
    public void countedTokens_aProviderThatCannotCount_sendsASourceThatFitsTheEstimateWhole() throws Exception {
        // arrange: the default promptTokens is empty
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));
        final String source = largeSource(300);

        // act
        support.generate(
                oversizeRule("m", "mapReduce", 0), Files.createTempFile("Small", ".java"), "file", source, anyHeader());

        // assert: the source fits the estimate and goes whole
        assertThat(requests.size(), is(1));
        assertThat(requests.get(0).sourceText(), is(equalTo(source)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="facts in the reduce">
    @Test
    public void mapReduce_everyReduceCallReadsTheFactsOfTheWholeSource_noMapCall() throws Exception {
        // arrange
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));
        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 0);
        final AiFactCounter counter = new AiFactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        // act
        support.generate(rule, Files.createTempFile("Huge", ".java"), "file", largeSource(7000), anyHeader());

        // assert: the map calls read their chunk, the reduce call the facts, then the parts
        final AiGenerationRequest reduce = requests.get(requests.size() - 1);
        assertThat(reduce.sourceText(), startsWith(AiFactExtractor.FACTS_HEADER + "lines: 7000"));
        assertThat(reduce.sourceText(), containsString(AiMapReduceSupport.PART_LABEL + "1:\nS\n"));
        for (final AiGenerationRequest map : requests.subList(0, requests.size() - 1)) {
            assertThat(map.sourceText(), startsWith("Chunk "));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="review round 2">
    private static String linesOf(final int chars) {
        final StringBuilder sb = new StringBuilder(chars + 16);
        while (sb.length() < chars) {
            sb.append("- `name_").append(sb.length()).append("` -> a long part summary line\n");
        }
        return sb.toString();
    }

    @Test
    public void reduce_partsTooLongToPair_areShortenedAndAllRead() throws Exception {
        // arrange: every map answer is longer than half a reduce batch, so no two fit one; the reduce answers
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AtomicInteger maps = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return request.sourceText().startsWith("Chunk ")
                    ? "MAP" + maps.incrementAndGet() + "\n" + linesOf(4_000)
                    : "S";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert: the first reduce reads two shortened parts, and the coverage line counts them
        final AiGenerationRequest firstReduce = requests.stream()
                .filter(r -> r.sourceText().startsWith(AiMapReduceSupport.PART_LABEL))
                .findFirst()
                .orElseThrow(AssertionError::new);
        assertThat(firstReduce.sourceText(), containsString(AiMapReduceSupport.PART_LABEL + "2:"));
        assertThat(firstReduce.sourceText(), containsString(AiMapReduceSupport.SHORTENED_MARKER));
        assertThat(result.complete(), is(true));
        assertThat(result.body(), containsString(" part summaries shortened to fit the reduce)"));
        // every map answer reached a reduce
        assertThat(maps.get() > 2, is(true));
        for (int map = 1; map <= maps.get(); map++) {
            final String tag = "MAP" + map + "\n";
            assertThat(
                    tag,
                    requests.stream()
                            .anyMatch(r -> !r.sourceText().startsWith("Chunk ")
                                    && r.sourceText().contains(tag)),
                    is(true));
        }
    }

    @Test
    public void mapReduce_aSingleChunkCutAtTheOutputBudget_isTheFinalAnswerAndFailsTheFile() throws Exception {
        // arrange: one chunk is read, so its answer is the final one; it is cut off
        final AiGenerationProvider provider = request -> {
            throw cutOff("half a summ");
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "sampleChunks", 1),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert
        assertThat(result.complete(), is(false));
    }

    @Test
    public void countedTokens_aPromptThatStaysOverTheLimit_isNeverSent() throws Exception {
        // arrange: every prompt counts far over the limit, however small its window; sample may cut the source
        final CountingProvider provider = new CountingProvider(r -> 1_000_000);
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final java.io.IOException failed = org.junit.jupiter.api.Assertions.assertThrows(
                java.io.IOException.class,
                () -> support.generate(
                        oversizeRule("m", "sample", 0),
                        Files.createTempFile("Dense", ".java"),
                        "file",
                        largeSource(300),
                        anyHeader()));

        // assert
        assertThat(failed.getMessage(), containsString("; not sent"));
        assertThat(provider.generated.isEmpty(), is(true));
    }

    @Test
    public void countedTokens_aChunkDenserThanTheFile_isReadInTwoHalves() throws Exception {
        // arrange: two chars per token, but every '#' takes three tokens more; one line of them in the middle
        // makes its chunk too dense for the window it was sized for
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length() / 2
                        + 3 * (int) r.sourceText().chars().filter(c -> c == '#').count());
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));
        final StringBuilder dense = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            dense.append('#');
        }
        final String source = largeSource(3000) + dense + "\n" + largeSource(3000);

        // act
        final AiGenerationResult result = support.generate(
                oversizeRule("m", "mapReduce", 0), Files.createTempFile("Dense", ".java"), "file", source, anyHeader());

        // assert: the dense chunk went in two halves, nothing was cut
        assertThat(provider.generated.stream().anyMatch(r -> r.sourceText().contains(", half 1:\n")), is(true));
        assertThat(provider.generated.stream().anyMatch(r -> r.sourceText().contains(", half 2:\n")), is(true));
        for (final AiGenerationRequest request : provider.generated) {
            assertThat(request.sourceText().contains("[EOF - source was truncated]"), is(false));
        }
        assertThat(result.body().contains("cut to fit the window"), is(false));
    }

    @Test
    public void incomplete_keep_countsThePartSummariesCutAtTheOutputBudget() throws Exception {
        // arrange: under keep every map answer is cut, the reduce answers
        final AiGenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- a part");
            }
            return "FINAL";
        };
        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 0);
        rule.setOnIncomplete("keep");

        // act
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(rule, Files.createTempFile("Huge", ".java"), "file", largeSource(7000), anyHeader());

        // assert
        assertThat(result.body(), containsString(" part summaries cut at the output budget)\n\nFINAL"));
    }

    @Test
    public void incomplete_aCutPart_reachesTheReduceWithoutItsCutLineAndMarked() throws Exception {
        // arrange: every map answer is cut in its third line
        final List<String> reduces = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- one\n- two\n- thr");
            }
            reduces.add(request.sourceText());
            return "FINAL";
        };

        // act
        supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        oversizeRule("m", "mapReduce", 0),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(7000),
                        anyHeader());

        // assert
        assertThat(reduces.get(0), containsString("- one\n- two" + AiMapReduceSupport.SHORTENED_MARKER));
        assertThat(reduces.get(0), not(containsString("- thr")));
    }

    @Test
    public void incomplete_retryOnce_keepsACutPartWithoutAskingAgain() throws Exception {
        // arrange
        final AtomicInteger maps = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                maps.incrementAndGet();
                throw cutOff("- a part");
            }
            return "FINAL";
        };
        final AiFieldGenerationConfig rule = oversizeRule("m", "mapReduce", 0);
        rule.setOnIncomplete("retryOnce");

        // act
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(rule, Files.createTempFile("Huge", ".java"), "file", largeSource(7000), anyHeader());

        // assert: one call per chunk, as many as the coverage line names
        final String chunks = result.body().replaceAll("(?s).*?of (\\d+) chunks summarized.*", "$1");
        assertThat(maps.get(), is(Integer.parseInt(chunks)));
    }
    // </editor-fold>

    @Test
    public void sampleChunks_theReduceReadsThatThePartsAreASample() throws Exception {
        // arrange
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        oversizeRule("m", "sampleChunks", 2),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(7000),
                        anyHeader());

        // assert
        final AiGenerationRequest reduce = requests.get(requests.size() - 1);
        assertThat(reduce.sourceText(), startsWith("**Sample:** the parts summarize 2 of "));
        assertThat(reduce.sourceText(), containsString(AiMapReduceSupport.PART_LABEL + "2:"));
    }

    // <editor-fold defaultstate="collapsed" desc="test review">
    @Test
    public void takeUsage_aFailedCall_stillCounts() throws Exception {
        // arrange: the request fails, its usage was reported
        final AiGenerationProvider provider = new AiGenerationProvider() {
            @Override
            public String generate(final AiGenerationRequest request) throws java.io.IOException {
                throw new java.io.IOException("the server is down");
            }

            @Override
            public net.ladenthin.srcmorph.provider.AiGenerationUsage lastUsage() {
                return new net.ladenthin.srcmorph.provider.AiGenerationUsage(1, 100, 0, 0, "");
            }
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));

        // act
        assertThrows(
                java.io.IOException.class,
                () -> support.generate(
                        normalRule("m", null), Files.createTempFile("Small", ".java"), "file", "x", anyHeader()));

        // assert
        assertThat(support.takeUsage().promptTokens(), is(100L));
    }

    @Test
    public void countedTokens_aProviderThatCannotCount_chunksByTheEstimate() throws Exception {
        // arrange
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        oversizeRule("m", "mapReduce", 0),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(7000),
                        anyHeader());

        // assert: chunks of the estimated window, none cut
        assertThat(requests.size() > 2, is(true));
        assertThat(result.body().contains("cut to fit the window"), is(false));
    }

    @Test
    public void sampleChunks_labelsEveryChunkWithTheSample() throws Exception {
        // arrange
        final List<AiGenerationRequest> requests = new ArrayList<>();
        final AiGenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        final AiGenerationResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(
                        oversizeRule("m", "sampleChunks", 2),
                        Files.createTempFile("Huge", ".java"),
                        "file",
                        largeSource(7000),
                        anyHeader());

        // assert: two chunks sampled from all of them, as the coverage line names them
        final String total = result.body().replaceAll("(?s).*?2 of (\\d+) chunks summarized.*", "$1");
        assertThat(requests.get(0).sourceText(), startsWith("Chunk 1 of 2, sampled from " + total + " chunks:\n"));
        assertThat(requests.get(1).sourceText(), startsWith("Chunk 2 of 2, sampled from " + total + " chunks:\n"));
    }

    @Test
    public void countedTokens_aPromptAtTheLimit_isSent_oneAboveIsNot() throws Exception {
        // arrange
        final CountingProvider atLimit = new CountingProvider(r -> NORMAL_WINDOW_TOKEN_LIMIT);
        final CountingProvider aboveLimit = new CountingProvider(r -> NORMAL_WINDOW_TOKEN_LIMIT + 1);
        final String source = largeSource(300);

        // act
        supportWithModels(atLimit, normalWindowModel("m"))
                .generate(normalRule("m", null), Files.createTempFile("Small", ".java"), "file", source, anyHeader());
        final java.io.IOException failed = assertThrows(
                java.io.IOException.class,
                () -> supportWithModels(aboveLimit, normalWindowModel("m"))
                        .generate(
                                oversizeRule("m", "sample", 0),
                                Files.createTempFile("Small", ".java"),
                                "file",
                                source,
                                anyHeader()));

        // assert
        assertThat(atLimit.generated.size(), is(1));
        assertThat(atLimit.generated.get(0).sourceText(), is(equalTo(source)));
        assertThat(aboveLimit.generated.isEmpty(), is(true));
        assertThat(failed.getMessage(), containsString("; not sent"));
    }

    @Test
    public void coverage_ofTheNextFile_startsCountingAnew() throws Exception {
        // arrange: the first file's map answers are cut, the second file's are not
        final AtomicInteger file = new AtomicInteger();
        final AiGenerationProvider provider = request -> {
            if (file.get() == 0 && request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- a part");
            }
            return "S";
        };
        final AiFieldGenerationSupport support = supportWithModels(provider, normalWindowModel("m"));
        final AiGenerationResult first = support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());
        file.incrementAndGet();

        // act
        final AiGenerationResult second = support.generate(
                oversizeRule("m", "mapReduce", 0),
                Files.createTempFile("Huge", ".java"),
                "file",
                largeSource(7000),
                anyHeader());

        // assert
        assertThat(first.body(), containsString("cut at the output budget"));
        assertThat(second.body().contains("cut at the output budget"), is(false));
        assertThat(second.body(), startsWith(AiMapReduceSupport.COVERAGE_HEADER));
    }
    // </editor-fold>
}

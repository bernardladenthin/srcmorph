// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.facts.FactExtractor;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import net.ladenthin.srcmorph.provider.ContextOverflowException;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationUsage;
import net.ladenthin.srcmorph.provider.IncompleteAnswerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

public class SummaryGeneratorTest {

    @TempDir
    Path tempDir;

    /**
     * A context file of a fixed name: its path heads the prompt, so a random temp-file name would make the
     * prompt, and with it the window, a little longer or shorter from run to run.
     */
    private Path tempFile(final String prefix, final String suffix) throws IOException {
        final Path file = tempDir.resolve(prefix + suffix);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        return file;
    }

    /**
     * Lower bound on chunks needed to force a second reduce round: strictly above the production
     * {@code MAX_REDUCE_FANIN} (16) so the reduce cannot combine every partial in a single call.
     */
    private static final long MIN_CHUNKS_FOR_TWO_REDUCE_ROUNDS = 16L;

    /**
     * Captures the SLF4J log output of {@link SummaryGenerator} via a logback
     * {@link ListAppender} attached directly to its logger, replacing the constructor-injected
     * Maven {@code Log} the production class used previously.
     */
    private ListAppender<ILoggingEvent> logAppender;

    private static Logger loggerUnderTest() {
        return (Logger) LoggerFactory.getLogger(SummaryGenerator.class);
    }

    /** The logger's level before the test, restored after it (null: inherited). */
    private Level previousLevel;

    /** Whether the logger was additive before the test. */
    private boolean previousAdditive;

    @BeforeEach
    public void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        final Logger logger = loggerUnderTest();
        previousLevel = logger.getLevel();
        previousAdditive = logger.isAdditive();
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        logger.addAppender(logAppender);
    }

    @AfterEach
    public void tearDown() {
        final Logger logger = loggerUnderTest();
        logger.detachAppender(logAppender);
        logger.setLevel(previousLevel);
        logger.setAdditive(previousAdditive);
        logAppender.stop();
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
        final Path contextFile = tempFile("Test", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationProvider nonEmptyProvider = request -> "A real summary.";
        final SummaryGenerator support = new SummaryGenerator(
                nonEmptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.generate(CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}");

        // assert
        assertThat(getCapturedWarnings().isEmpty(), is(true));
    }

    @Test
    public void generate_providerReturnsEmpty_warningContainsContextFile() throws Exception {
        // arrange
        final Path contextFile = tempFile("ModelDefinition", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationProvider emptyProvider = request -> "";
        final SummaryGenerator support = new SummaryGenerator(
                emptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class ModelDefinition {}");

        // assert
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(contextFile.toString()));
    }

    @Test
    public void generate_providerReturnsEmpty_warningContainsPromptKey() throws Exception {
        // arrange
        final Path contextFile = tempFile("Test", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationProvider emptyProvider = request -> "";
        final SummaryGenerator support = new SummaryGenerator(
                emptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.generate(CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}");

        // assert
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
    }

    @Test
    public void generate_providerReturnsEmpty_resultBodyIsEmpty() throws Exception {
        // arrange
        final Path contextFile = tempFile("Test", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationProvider emptyProvider = request -> "";
        final SummaryGenerator support = new SummaryGenerator(
                emptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final SummaryResult result = support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}");

        // assert -- an empty answer is no summary: nothing to write, and the result says why
        assertThat(result.body(), is(equalTo("")));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), containsString("answered nothing"));
    }

    @Test
    public void generate_providerReturnsEmpty_callsProviderOnceWarnsOnceAndBodyEmpty() throws Exception {
        // arrange
        final Path contextFile = tempFile("Test", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final AtomicInteger callCount = new AtomicInteger(0);
        // The retry mechanism was removed: an empty body now fails fast with a single warning
        // instead of re-inferring (see docs/ai-index-benchmark/gpt-oss-tuning.md, E2).
        final GenerationProvider alwaysEmptyProvider = new GenerationProvider() {
            @Override
            public String generate(final GenerationRequest request) {
                callCount.incrementAndGet();
                return "";
            }
        };
        final SummaryGenerator support = new SummaryGenerator(
                alwaysEmptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final SummaryResult result = support.generate(
                CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}");

        // assert — provider invoked exactly once (no retries), one warning, empty body
        assertThat(callCount.get(), is(equalTo(1)));
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(result.body(), is(equalTo("")));
    }

    @Test
    public void generate_providerReturnsEmpty_logsProcessingAndGenerationButNoGeneratedLine() throws Exception {
        // arrange
        final Path contextFile = tempFile("Test", ".java");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationProvider alwaysEmptyProvider = new GenerationProvider() {
            @Override
            public String generate(final GenerationRequest request) {
                return "";
            }
        };
        final SummaryGenerator support = new SummaryGenerator(
                alwaysEmptyProvider,
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        support.generate(CommonTestFixtures.createFileFieldGeneration(), contextFile, "file", "public class Test {}");

        // assert — exactly two INFO lines: the per-file processing/ETA line and the generation line.
        // An empty answer generated nothing, so there is no measured actual-duration line, and no retry.
        final List<String> infos = getCapturedInfos();
        assertThat(infos.size(), is(equalTo(2)));

        // First message: the per-file processing line with size + token + duration estimate
        final String processingMsg = infos.get(0);
        assertThat(processingMsg, containsString("Processing file"));
        assertThat(processingMsg, containsString("tokens"));
        assertThat(processingMsg, containsString("estimated"));
        // the default model has no calibration: the time is the built-in one, and the line says on what
        assertThat(processingMsg, endsWith(" (rough; based on GPT-6 Luna)"));

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
    private static ModelRegistry tinyWindowModel(final String key) {
        final ModelDefinition def = new ModelDefinition();
        def.setKey(key);
        def.setModelPath("unused.gguf");
        // contextSize 1 x charsPerToken 1 minus overhead -> computed maxInputChars clamps to 0, so any
        // non-empty source is over-window and triggers the onOversize strategy.
        def.setContextSize(1);
        def.setCharsPerToken(1);
        return new ModelRegistry(Arrays.asList(def));
    }

    private SummaryGenerator supportWith(final GenerationProvider provider, final String modelKey) {
        return supportWithModels(provider, tinyWindowModel(modelKey));
    }

    private SummaryGenerator supportWithModels(final GenerationProvider provider, final ModelRegistry models) {
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        return new SummaryGenerator(provider, new PromptPreparer(promptSupport), models);
    }

    /** A model with a normal window, so a small source fits and is NOT treated as over-window. */
    private static ModelRegistry normalWindowModel(final String key) {
        final ModelDefinition def = new ModelDefinition();
        def.setKey(key);
        def.setModelPath("unused.gguf");
        def.setContextSize(2048);
        def.setCharsPerToken(4);
        def.setMaxOutputTokens(64);
        return new ModelRegistry(Arrays.asList(def));
    }

    private static RoutingRule oversizeRule(final String modelKey, final String onOversize, final int maxChunks) {
        final RoutingRule rule = new RoutingRule();
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
        // arrange
        final Path contextFile = tempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "MODEL OUTPUT";
        };
        final SummaryGenerator support = supportWith(provider, "m");

        // act
        final SummaryResult result =
                support.generate((oversizeRule("m", "deterministic", 0)), contextFile, "file", largeSource(500));

        // assert
        assertThat(calls.get(), is(equalTo(0)));
        assertThat(result.body(), containsString("deterministically (no AI)"));
    }

    @Test
    public void onOversize_mapReduce_callsModelPerChunkPlusReduce() throws Exception {
        // arrange
        final Path contextFile = tempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        // ~70 KB over a window of about 7 KB -> several chunks that fit; sampleChunks 2 maps 2 of them + 1 reduce.
        final SummaryResult result =
                support.generate((oversizeRule("m", "sampleChunks", 2)), contextFile, "file", largeSource(7000));

        // assert
        assertThat(calls.get(), is(equalTo(3)));
        assertThat(result.body(), startsWith("**Coverage:** 2 of "));
        assertThat(result.body(), containsString(" chunks summarized (sampled)\n\nPARTIAL"));
    }

    @Test
    public void onOversize_mapReduce_readsEveryChunkAndSaysSo() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };

        // act
        // a maxChunks of the rule does not sample under mapReduce
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((oversizeRule("m", "mapReduce", 2)), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
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
    public void onOversize_mapReduceWithFacts_prependsExactWholeFileCountsToBody() throws Exception {
        // arrange
        final Path contextFile = tempFile("Huge", ".java");
        final GenerationProvider provider = request -> "PARTIAL";
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        final FactCounter counter = new FactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        // act
        // largeSource(7000) has exactly 7000 lines beginning with "line "; no call of the mock sees them all,
        // but the deterministic facts block counts every one over the FULL source and is prepended.
        final SummaryResult result = support.generate((rule), contextFile, "file", largeSource(7000));

        // assert
        assertThat(result.body(), startsWith(FactExtractor.FACTS_HEADER));
        assertThat(result.body(), containsString("lines: 7000"));
        assertThat(result.body(), containsString("PARTIAL"));
    }

    @Test
    public void facts_nonOversizeFile_arePrepended() throws Exception {
        // arrange
        // A small source that fits the window (NOT oversize) still gets the exact facts block prepended,
        // so downstream agents get authoritative counts in every summary, not just huge-file ones.
        final Path contextFile = tempFile("Small", ".java");
        final GenerationProvider provider = request -> "AI SUMMARY";
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey("m");
        final FactCounter counter = new FactCounter();
        counter.setLabel("boolean fields");
        counter.setPattern("\\bboolean\\b");
        rule.setFacts(Collections.singletonList(counter));

        // act
        final SummaryResult result = support.generate((rule), contextFile, "file", "boolean a; boolean b; int c;");

        // assert
        assertThat(result.body(), startsWith(FactExtractor.FACTS_HEADER));
        assertThat(result.body(), containsString("boolean fields: 2"));
        assertThat(result.body(), containsString("AI SUMMARY"));
    }

    private static RoutingRule plainRoute(final String modelKey) {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(modelKey);
        return rule;
    }

    /**
     * The retry after an overflow really sends less: the whole source first, a quarter less of the window
     * the second time, so a source near the window is cut (a rule that may cut it, here sample).
     */
    @Test
    public void generate_contextOverflow_theRetrySendsTheSmallerWindow() throws Exception {
        // arrange
        final List<Integer> sent = new ArrayList<>();
        final GenerationProvider provider = request -> {
            sent.add(request.sourceText().length());
            if (sent.size() == 1) {
                throw new RuntimeException("request (2100 tokens) exceeds the available context size (2048 tokens)");
            }
            return "OK";
        };
        final String source = largeSource(7000).substring(0, 6500);

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "sample", 0), tempFile("Dense", ".sql"), "file", source);

        // assert: the first call fits whole, the second at most the window the warning names, three
        // quarters of the first one
        assertThat(result.body(), containsString("OK"));
        assertThat(sent.size(), is(2));
        assertThat(sent.get(0), is(source.length()));
        final String warning = getCapturedWarnings().stream()
                .filter(w -> w.contains("retrying with a smaller window"))
                .findFirst()
                .orElseThrow(AssertionError::new);
        final Matcher windows = Pattern.compile("\\((\\d+) -> (\\d+) chars\\)").matcher(warning);
        assertThat(windows.find(), is(true));
        final int before = Integer.parseInt(windows.group(1));
        final int after = Integer.parseInt(windows.group(2));
        assertThat(after, is(before * OverflowBackoff.BACKOFF_PERCENT / 100));
        assertThat(sent.get(1) <= after, is(true));
        assertThat(sent.get(1) < sent.get(0), is(true));
    }

    @Test
    public void generate_contextOverflowThenSuccess_retriesWithSmallerWindowAndSucceeds() throws Exception {
        // arrange
        final Path contextFile = tempFile("Dense", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        // Fits the char window but the provider rejects the first prompt as over-context (token-dense);
        // the retry with a smaller window then succeeds.
        final GenerationProvider provider = request -> {
            if (calls.getAndIncrement() == 0) {
                throw new RuntimeException("request (16549 tokens) exceeds the available context size (16384 tokens)");
            }
            return "OK";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate((plainRoute("m")), contextFile, "file", "small source");

        // assert
        assertThat(result.body(), is(equalTo("OK")));
        assertThat(calls.get(), is(equalTo(2)));
    }

    @Test
    public void generate_remoteContextOverflowThenSuccess_retriesWithSmallerWindowAndSucceeds() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            if (calls.incrementAndGet() == 1) {
                throw new ContextOverflowException("http://h answered HTTP 413: ");
            }
            return "OK";
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", null)), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.body(), is(equalTo("OK")));
    }

    @Test
    public void generate_persistentContextOverflow_rethrowsAfterRetrying() throws Exception {
        // arrange
        final Path contextFile = tempFile("Dense", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new RuntimeException("request exceeds the available context size (16384 tokens)");
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule route = plainRoute("m");

        // act
        assertThrows(RuntimeException.class, () -> support.generate(route, contextFile, "file", "small source"));

        // assert
        // Retried before giving up (more than the single initial attempt).
        assertThat(calls.get() > 1, is(true));
    }

    @Test
    public void generate_nonOverflowRuntimeException_propagatesWithoutRetry() throws Exception {
        // arrange
        final Path contextFile = tempFile("X", ".sql");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new RuntimeException("some unrelated failure");
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule route = plainRoute("m");

        // act
        assertThrows(RuntimeException.class, () -> support.generate(route, contextFile, "file", "small source"));

        // assert
        assertThat(calls.get(), is(equalTo(1)));
    }

    @Test
    public void onOversize_mapReduceUnbounded_reducesHierarchicallyInMultipleRounds() throws Exception {
        // arrange
        final Path contextFile = tempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "PARTIAL";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        // maxChunks=0 (unbounded) over a large source -> far more than MAX_REDUCE_FANIN chunks, so the
        // reduce cannot combine every partial in one call and must recurse: a second reduce round appears.
        final SummaryResult result =
                support.generate((oversizeRule("m", "mapReduce", 0)), contextFile, "file", largeSource(30000));

        // assert
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
    public void onOversize_mapReduceInAWindowWithoutRoomForAChunk_failsTheFileNamingTheRoom() throws Exception {
        // arrange: the tiny window holds less than the minimum chunk next to the prompts, so every chunk call
        // would be cut and most of the source never read
        final AtomicInteger calls = new AtomicInteger();
        final SummaryGenerator support = supportWith(
                request -> {
                    calls.incrementAndGet();
                    return "PARTIAL";
                },
                "m");
        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        final Path contextFile = tempFile("Huge", ".java");
        final String source = largeSource(600);

        // act
        final IOException failed =
                assertThrows(IOException.class, () -> support.generate((rule), contextFile, "file", source));

        // assert
        assertThat(failed.getMessage(), containsString("next to its map and reduce prompts, fewer than 1000"));
        assertThat(calls.get(), is(0));
    }

    @Test
    public void contextOverflow_ofASourceThatFits_isDecidedByOnOversize() throws Exception {
        // arrange: the source fits the window by characters, but the provider refuses it as a context
        // overflow until a smaller window has cut a thousand characters off
        final String source = largeSource(600);
        final GenerationProvider provider = request -> {
            if (request.sourceText().length() > source.length() - 1000) {
                throw new IOException("the request exceeds the available context size");
            }
            return "BODY";
        };
        final SummaryGenerator failing = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule failRule = oversizeRule("m", "fail", 0);
        final Path fileA = tempFile("A", ".java");

        // act
        final IOException failed =
                assertThrows(IOException.class, () -> failing.generate(failRule, fileA, "file", source));
        final SummaryResult sampled = supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "sample", 0), tempFile("B", ".java"), "file", source);

        // assert: fail refuses instead of writing a cut summary; sample says how much of the head it read
        assertThat(failed.getMessage(), containsString("onOversize: fail"));
        assertThat(sampled.body(), startsWith("**Coverage:** the first "));
        assertThat(sampled.body(), containsString(" of " + source.length() + " characters (sampled head)"));
        assertThat(sampled.body(), endsWith("BODY"));
    }

    @Test
    public void onOversize_sample_singleCallOnTrimmedHead() throws Exception {
        // arrange
        final Path contextFile = tempFile("Huge", ".java");
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "SAMPLED";
        };
        final SummaryGenerator support = supportWith(provider, "m");

        // act
        final SummaryResult result =
                support.generate((oversizeRule("m", "sample", 0)), contextFile, "file", largeSource(500));

        // assert
        assertThat(calls.get(), is(equalTo(1)));
        assertThat(result.body(), startsWith("**Coverage:** the first "));
        assertThat(result.body(), endsWith(" of 4390 characters (sampled head)\n\nSAMPLED"));
    }

    @Test
    public void onOversize_failAtRunTime_isAnErrorNotATrimmedSummary() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return "MODEL OUTPUT";
        };
        final SummaryGenerator support = supportWith(provider, "m");
        final RoutingRule rule = oversizeRule("m", "fail", 0);
        final Path contextFile = tempFile("Huge", ".java");
        final String source = largeSource(500);

        // act
        final IOException thrown =
                assertThrows(IOException.class, () -> support.generate((rule), contextFile, "file", source));

        // assert
        assertThat(calls.get(), is(equalTo(0)));
        assertThat(thrown.getMessage(), containsString("onOversize: fail"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parts">
    @Test
    public void parts_aMapReduceThenAOnePieceCall_areEveryMapAndReduceAnswerThenNone() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> "ANSWER " + calls.incrementAndGet();
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        support.generate(oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));
        final String chunked = support.parts();
        support.generate(oversizeRule("m", "sampleChunks", 2), tempFile("Small", ".java"), "file", "class Small {}");

        // assert
        assertThat(chunked, startsWith("## Chunk 1 of 2, sampled from "));
        assertThat(chunked, containsString("\n\nANSWER 1\n\n## Chunk 2 of 2, sampled from "));
        assertThat(chunked, endsWith("\n\nANSWER 2\n\n## Reduce round 1, batch 1 of 1 (2 parts)\n\nANSWER 3\n\n"));
        assertThat(support.parts(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    private static RoutingRule normalRule(final String modelKey, final String onIncomplete) {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(modelKey);
        rule.setOnIncomplete(onIncomplete);
        return rule;
    }

    private static IncompleteAnswerException cutOff(final String partialText) {
        return new IncompleteAnswerException(IncompleteAnswerException.Reason.TRUNCATED, "cut off", partialText);
    }

    @Test
    public void mapReduce_aPartSummaryCutAtTheOutputBudget_isKeptAndNamed() throws Exception {
        // arrange: every map answer is cut off (a model listing entries one by one); the reduce answers
        final GenerationProvider provider = request -> {
            if (!request.sourceText().contains(MapReduceMath.PART_LABEL + "1:")) {
                throw cutOff("- `A_00001` -> one\n- `A_00002` -> two");
            }
            return "FINAL";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        assertThat(result.complete(), is(true));
        assertThat(result.body(), containsString(" part summaries cut at the output budget)\n\nFINAL"));
    }

    @Test
    public void mapReduce_aFinalAnswerCutAtTheOutputBudget_stillFailsTheFile() {
        // arrange: the parts are complete, the final reduce is cut off
        final GenerationProvider provider = request -> {
            if (request.sourceText().contains(MapReduceMath.PART_LABEL + "1:")) {
                throw cutOff("half a summ");
            }
            return "PARTIAL";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act, assert
        final SummaryResult result = assertDoesNotThrowGenerate(support);
        assertThat(result.complete(), is(false));
    }

    private SummaryResult assertDoesNotThrowGenerate(final SummaryGenerator support) {
        return assertDoesNotThrow(() -> support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000)));
    }

    @Test
    public void incomplete_retryOnce_asksAgainAndTakesTheSecondAnswer() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> calls.incrementAndGet() == 1 ? "" : "A summary.";

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", "retryOnce")), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.complete(), is(true));
        assertThat(result.body(), is(equalTo("A summary.")));
        assertThat(getCapturedWarnings().size(), is(equalTo(1)));
        assertThat(getCapturedWarnings().get(0), containsString("asking once more"));
    }

    @Test
    public void incomplete_retryOnce_secondIncompleteAnswerFails() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            return " ";
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", "retryOnce")), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(calls.get(), is(equalTo(2)));
        assertThat(result.complete(), is(false));
    }

    @Test
    public void incomplete_failByDefault_cutOffAnswerIsNotTaken() throws Exception {
        // arrange
        final GenerationProvider provider = request -> {
            throw cutOff("half a sum");
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", null)), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is(equalTo("cut off")));
        assertThat(
                getCapturedWarnings().get(0),
                containsString("(field '" + CommonTestFixtures.PROMPT_KEY_FILE_BODY + "', TRUNCATED)"));
    }

    @Test
    public void incomplete_keep_takesTheTextOfACutOffAnswer() throws Exception {
        // arrange
        final GenerationProvider provider = request -> {
            throw cutOff("half a sum");
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", "keep")), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(result.complete(), is(true));
        // the summary says that it stops early
        assertThat(result.body(), is(equalTo("half a sum" + SummaryGenerator.KEPT_CUT_NOTE)));
        assertThat(getCapturedWarnings().get(0), containsString("kept as it is"));
    }

    @Test
    public void incomplete_keep_cutOffAnswerWithoutTextAndAnEmptyAnswerStillFail() throws Exception {
        // arrange
        final GenerationProvider cutOffEmpty = request -> {
            throw cutOff(" ");
        };
        final GenerationProvider empty = request -> "";

        // act, assert
        for (final GenerationProvider provider : Arrays.asList(cutOffEmpty, empty)) {
            final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                    .generate((normalRule("m", "keep")), tempFile("Test", ".java"), "file", "public class Test {}");
            assertThat(result.complete(), is(false));
        }
    }

    @Test
    public void incomplete_responseWithoutAnswer_isIncompleteAndNotRetriedUnderFail() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new IncompleteAnswerException(IncompleteAnswerException.Reason.NO_ANSWER, "no choice");
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate((normalRule("m", "fail")), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(calls.get(), is(equalTo(1)));
        assertThat(result.complete(), is(false));
        assertThat(result.incompleteReason(), is(equalTo("no choice")));
    }

    /** Records every request and answers the n-th (from 1) as the script says. */
    private static final class ScriptedProvider implements GenerationProvider {

        /** The answer to one call. */
        interface Script {
            String answer(int call) throws IOException;
        }

        private final Script script;
        private final List<GenerationRequest> generated = new ArrayList<>();

        ScriptedProvider(final Script script) {
            this.script = script;
        }

        @Override
        public String generate(final GenerationRequest request) throws IOException {
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
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));

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
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));

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
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: each call read the first half of the one before, down to a piece too small to split
        assertThat(result.complete(), is(false));
        final int calls = provider.generated.size();
        for (int call = 2; call <= calls; call++) {
            assertThat(provider.chunkOf(call - 1), startsWith(provider.chunkOf(call)));
            assertThat(provider.chunkOf(call - 1).length() >= 2 * MapReduceMath.MIN_CHUNK_CHARS, is(true));
        }
        assertThat(provider.chunkOf(calls).length() < 2 * MapReduceMath.MIN_CHUNK_CHARS, is(true));
    }

    @Test
    public void incomplete_withFacts_theFactsLineDoesNotMaskAnEmptyAnswer() throws Exception {
        // arrange
        final RoutingRule rule = normalRule("m", null);
        final FactCounter counter = new FactCounter();
        counter.setLabel("classes");
        counter.setPattern("\\bclass\\b");
        rule.setFacts(Collections.singletonList(counter));

        // act
        final SummaryResult result = supportWithModels(request -> "", normalWindowModel("m"))
                .generate((rule), tempFile("Test", ".java"), "file", "public class Test {}");

        // assert
        assertThat(result.complete(), is(false));
        assertThat(result.body(), is(equalTo("")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="window">
    @Test
    public void window_aShortNameAfterALongOne_isComputedForEveryFileFromItsOwnName() throws Exception {
        // arrange
        // The prompt contains the file name, so a long name leaves less room for the source. A short-named
        // file whose source fills exactly its own budget is not trimmed, even after a long-named one.
        final ModelRegistry models = normalWindowModel("m");
        final PromptPreparer preparation =
                new PromptPreparer(new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions()));
        final Path shortName = tempFile("A", ".java");
        final Path longName = tempFile("A_very_long_file_name_that_eats_into_the_prompt_budget", ".java");
        final int budget = (int) InputWindowCalculator.availableSourceChars(
                models.definition("m"),
                preparation.basePromptLength(CommonTestFixtures.PROMPT_KEY_FILE_BODY, shortName));
        final SummaryGenerator support = supportWithModels(request -> "OK", models);
        final RoutingRule rule = normalRule("m", null);

        // act
        support.generate((rule), longName, "file", "x");
        final String fits = largeSource(budget).substring(0, budget);
        final SummaryResult result = support.generate((rule), shortName, "file", fits);

        // assert
        assertThat(result.body(), is(equalTo("OK")));
        for (final String warning : getCapturedWarnings()) {
            assertThat(warning.contains("Trimmed AI input"), is(false));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="takeUsage">
    @Test
    public void takeUsage_aChunkedFile_sumsItsCallsAndStartsAnew() throws Exception {
        // arrange
        final GenerationProvider provider = new GenerationProvider() {
            @Override
            public String generate(final GenerationRequest request) {
                return "PARTIAL";
            }

            @Override
            public GenerationUsage lastUsage() {
                return new GenerationUsage(1, 100, 10, 0, "stop");
            }
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        support.generate(oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));
        final GenerationUsage usage = support.takeUsage();

        // assert
        // two chunks and one reduce
        assertThat(usage.calls(), is(3));
        assertThat(usage.promptTokens(), is(300L));
        assertThat(usage.completionTokens(), is(30L));
        assertThat(usage.finishReason(), is(equalTo("stop")));
        assertThat(support.takeUsage(), is(GenerationUsage.NONE));
    }

    @Test
    public void takeUsage_anEmptyAnswer_stillCounts() throws Exception {
        // arrange
        final SummaryGenerator support = supportWithModels(request -> "", normalWindowModel("m"));

        // act
        support.generate(normalRule("m", null), tempFile("Small", ".java"), "file", "x");

        // assert
        // the empty answer was one call; a lambda provider reports no tokens
        assertThat(support.takeUsage().calls(), is(1));
    }

    @Test
    public void takeUsage_aFailedCall_stillCounts() throws Exception {
        // arrange: the request fails, its usage was reported
        final GenerationProvider provider = new GenerationProvider() {
            @Override
            public String generate(final GenerationRequest request) throws IOException {
                throw new IOException("the server is down");
            }

            @Override
            public GenerationUsage lastUsage() {
                return new GenerationUsage(1, 100, 0, 0, "");
            }
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule rule = normalRule("m", null);
        final Path contextFile = tempFile("Small", ".java");

        // act
        assertThrows(IOException.class, () -> support.generate(rule, contextFile, "file", "x"));

        // assert
        assertThat(support.takeUsage().promptTokens(), is(100L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="map and reduce prompts">
    private static PromptDefinition prompt(final String key, final String template) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey(key);
        definition.setTemplate(template);
        return definition;
    }

    private List<GenerationRequest> mapReduceRequests(final String mapKey, final String reduceKey) throws Exception {
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final List<PromptDefinition> prompts = new ArrayList<>(CommonTestFixtures.createFilePromptDefinitions());
        prompts.add(prompt("map", "MAP"));
        prompts.add(prompt("reduce", "REDUCE"));
        final SummaryGenerator support =
                new SummaryGenerator(provider, new PromptPreparer(new PromptCatalog(prompts)), normalWindowModel("m"));
        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        rule.setMapPromptKey(mapKey);
        rule.setReducePromptKey(reduceKey);
        support.generate(rule, tempFile("Huge", ".java"), "file", largeSource(7000));
        return requests;
    }

    @Test
    public void mapReduce_mapCalls_takeTheMapPromptAndAChunkLabel() throws Exception {
        // act
        final List<GenerationRequest> requests = mapReduceRequests("map", "reduce");

        // assert: the first call maps chunk 1, every map call is labelled
        final GenerationRequest first = requests.get(0);
        assertThat(first.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY + "+map")));
        assertThat(first.sourceText(), startsWith("Chunk 1 of "));
        assertThat(first.sourceText(), containsString("\nline 0\n"));
    }

    @Test
    public void mapReduce_reduceCalls_takeTheReducePromptAndThePartSummaries() throws Exception {
        // act
        final List<GenerationRequest> requests = mapReduceRequests("map", "reduce");

        // assert: the last call reduces
        final GenerationRequest last = requests.get(requests.size() - 1);
        assertThat(last.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY + "+reduce")));
        assertThat(last.sourceText(), startsWith(MapReduceMath.PART_LABEL + "1:\nS\n"));
    }

    @Test
    public void mapReduce_withoutMapAndReducePrompts_takesTheRulePromptForEveryCall() throws Exception {
        // act
        final List<GenerationRequest> requests = mapReduceRequests(null, null);

        // assert
        for (final GenerationRequest request : requests) {
            assertThat(request.promptKey(), is(equalTo(CommonTestFixtures.PROMPT_KEY_FILE_BODY)));
        }
        assertThat(requests.get(0).sourceText(), startsWith("Chunk 1 of "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="counted prompt tokens">
    /** A provider that counts a prompt's tokens with {@code tokens} and records every request it answers. */
    private static final class CountingProvider implements GenerationProvider {

        private final ToIntFunction<GenerationRequest> tokens;
        private final List<GenerationRequest> generated = new ArrayList<>();

        CountingProvider(final ToIntFunction<GenerationRequest> tokens) {
            this.tokens = tokens;
        }

        @Override
        public String generate(final GenerationRequest request) {
            generated.add(request);
            return "S";
        }

        @Override
        public OptionalInt promptTokens(final GenerationRequest request) {
            return OptionalInt.of(tokens.applyAsInt(request));
        }
    }

    /** The prompt tokens a call of {@link #normalWindowModel} may take: its 2048 less its 64 of answer. */
    private static final int NORMAL_WINDOW_TOKEN_LIMIT = 2048 - 64 - TokenWindow.UNCOUNTED_TOKENS;

    @Test
    public void countedTokens_aSourceOverTheEstimateThatFitsInTokens_takesOneCall() throws Exception {
        // arrange: about 14 000 chars are over the estimated window (2048 tokens x 4 chars), at 8 chars per token
        // they fit
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length() / 8);
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final String source = largeSource(1_500);

        // act
        final SummaryResult result =
                support.generate(oversizeRule("m", "mapReduce", 0), tempFile("Dense", ".java"), "file", source);

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
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Dense", ".java"), "file", largeSource(400));

        // assert: the file is map-reduced, and every call the provider answered fits
        assertThat(result.complete(), is(true));
        assertThat(provider.generated.size() > 1, is(true));
        for (final GenerationRequest request : provider.generated) {
            assertThat(request.sourceText().length() <= NORMAL_WINDOW_TOKEN_LIMIT, is(true));
        }
    }

    @Test
    public void countedTokens_aSourceOverTheEstimate_isChunkedByTheMeasuredDensity() throws Exception {
        // arrange: one char per token, about 8 900 chars: a chunk of the estimate (about 5 800 chars) would be three
        // times the limit, a chunk of the measured window is not
        final CountingProvider provider =
                new CountingProvider(r -> r.sourceText().length());
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        support.generate(oversizeRule("m", "mapReduce", 0), tempFile("Dense", ".java"), "file", largeSource(1_000));

        // assert: no map call needed a smaller window, so every chunk read the whole of itself
        for (final GenerationRequest request : provider.generated) {
            assertThat(request.sourceText().length() <= NORMAL_WINDOW_TOKEN_LIMIT, is(true));
            assertThat(request.sourceText().contains("[EOF - source was truncated]"), is(false));
        }
    }

    @Test
    public void countedTokens_aProviderThatCannotCount_sendsASourceThatFitsTheEstimateWhole() throws Exception {
        // arrange: the default promptTokens is empty
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final String source = largeSource(300);

        // act
        support.generate(oversizeRule("m", "mapReduce", 0), tempFile("Small", ".java"), "file", source);

        // assert: the source fits the estimate and goes whole
        assertThat(requests.size(), is(1));
        assertThat(requests.get(0).sourceText(), is(equalTo(source)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="facts in the reduce">
    @Test
    public void mapReduce_everyReduceCallReadsTheFactsOfTheWholeSource_noMapCall() throws Exception {
        // arrange
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        final FactCounter counter = new FactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        // act
        support.generate(rule, tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: the map calls read their chunk, the reduce call the facts, then the parts
        final GenerationRequest reduce = requests.get(requests.size() - 1);
        assertThat(reduce.sourceText(), startsWith(FactExtractor.FACTS_HEADER + "lines: 7000"));
        assertThat(reduce.sourceText(), containsString(MapReduceMath.PART_LABEL + "1:\nS\n"));
        for (final GenerationRequest map : requests.subList(0, requests.size() - 1)) {
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
        final List<GenerationRequest> requests = new ArrayList<>();
        final AtomicInteger maps = new AtomicInteger();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return request.sourceText().startsWith("Chunk ")
                    ? "MAP" + maps.incrementAndGet() + "\n" + linesOf(4_000)
                    : "S";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: the first reduce reads two shortened parts, and the coverage line counts them
        final GenerationRequest firstReduce = requests.stream()
                .filter(r -> r.sourceText().startsWith(MapReduceMath.PART_LABEL))
                .findFirst()
                .orElseThrow(AssertionError::new);
        assertThat(firstReduce.sourceText(), containsString(MapReduceMath.PART_LABEL + "2:"));
        assertThat(firstReduce.sourceText(), containsString(MapReduceMath.SHORTENED_MARKER));
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
        final GenerationProvider provider = request -> {
            throw cutOff("half a summ");
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));

        // act
        final SummaryResult result = support.generate(
                oversizeRule("m", "sampleChunks", 1), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        assertThat(result.complete(), is(false));
    }

    @Test
    public void countedTokens_aPromptThatStaysOverTheLimit_isNeverSent() throws Exception {
        // arrange: every prompt counts far over the limit, however small its window; sample may cut the source
        final CountingProvider provider = new CountingProvider(r -> 1_000_000);
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final RoutingRule rule = oversizeRule("m", "sample", 0);
        final Path contextFile = tempFile("Dense", ".java");
        final String source = largeSource(300);

        // act
        final IOException failed =
                assertThrows(IOException.class, () -> support.generate(rule, contextFile, "file", source));

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
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final StringBuilder dense = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            dense.append('#');
        }
        final String source = largeSource(3000) + dense + "\n" + largeSource(3000);

        // act
        final SummaryResult result =
                support.generate(oversizeRule("m", "mapReduce", 0), tempFile("Dense", ".java"), "file", source);

        // assert: the dense chunk went in two halves, nothing was cut
        assertThat(provider.generated.stream().anyMatch(r -> r.sourceText().contains(", half 1:\n")), is(true));
        assertThat(provider.generated.stream().anyMatch(r -> r.sourceText().contains(", half 2:\n")), is(true));
        for (final GenerationRequest request : provider.generated) {
            assertThat(request.sourceText().contains("[EOF - source was truncated]"), is(false));
        }
        assertThat(result.body().contains("cut to fit the window"), is(false));
    }

    @Test
    public void incomplete_keep_countsThePartSummariesCutAtTheOutputBudget() throws Exception {
        // arrange: under keep every map answer is cut, the reduce answers
        final GenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- a part");
            }
            return "FINAL";
        };
        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        rule.setOnIncomplete("keep");

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(rule, tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        assertThat(result.body(), containsString(" part summaries cut at the output budget)\n\nFINAL"));
    }

    /**
     * An answer the provider stopped (finish reason {@code error}) is left for the next run: even under
     * keep its text is not the model's finished answer, unlike one cut at the output budget.
     */
    @Test
    public void incomplete_keepAndAnAnswerTheProviderStopped_isNotWritten() throws Exception {
        // arrange
        final AtomicInteger calls = new AtomicInteger();
        final GenerationProvider provider = request -> {
            calls.incrementAndGet();
            throw new IncompleteAnswerException(
                    IncompleteAnswerException.Reason.ABORTED, "ended with finish reason 'error'", "- test paging (");
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(normalRule("m", "keep"), tempFile("Paging", ".ts"), "file", "export class Paging {}");

        // assert
        assertThat(result.complete(), is(false));
        assertThat(calls.get(), is(1));
    }

    @Test
    public void incomplete_aCutPart_reachesTheReduceWithoutItsCutLineAndMarked() throws Exception {
        // arrange: every map answer is cut in its third line
        final List<String> reduces = new ArrayList<>();
        final GenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- one\n- two\n- thr");
            }
            reduces.add(request.sourceText());
            return "FINAL";
        };

        // act
        supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        assertThat(reduces.get(0), containsString("- one\n- two" + MapReduceMath.SHORTENED_MARKER));
        assertThat(reduces.get(0), not(containsString("- thr")));
    }

    @Test
    public void incomplete_retryOnce_keepsACutPartWithoutAskingAgain() throws Exception {
        // arrange
        final AtomicInteger maps = new AtomicInteger();
        final GenerationProvider provider = request -> {
            if (request.sourceText().startsWith("Chunk ")) {
                maps.incrementAndGet();
                throw cutOff("- a part");
            }
            return "FINAL";
        };
        final RoutingRule rule = oversizeRule("m", "mapReduce", 0);
        rule.setOnIncomplete("retryOnce");

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(rule, tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: one call per chunk, as many as the coverage line names
        final String chunks = result.body().replaceAll("(?s).*?of (\\d+) chunks summarized.*", "$1");
        assertThat(maps.get(), is(Integer.parseInt(chunks)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="test review">
    @Test
    public void sampleChunks_theReduce_readsThatThePartsAreASample() throws Exception {
        // arrange
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        final GenerationRequest reduce = requests.get(requests.size() - 1);
        assertThat(reduce.sourceText(), startsWith("**Sample:** the parts summarize 2 of "));
        assertThat(reduce.sourceText(), containsString(MapReduceMath.PART_LABEL + "2:"));
    }

    @Test
    public void countedTokens_aProviderThatCannotCount_chunksByTheEstimate() throws Exception {
        // arrange
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: chunks of the estimated window, none cut
        assertThat(requests.size() > 2, is(true));
        assertThat(result.body().contains("cut to fit the window"), is(false));
    }

    @Test
    public void sampleChunks_everyChunk_isLabelledWithTheSample() throws Exception {
        // arrange
        final List<GenerationRequest> requests = new ArrayList<>();
        final GenerationProvider provider = request -> {
            requests.add(request);
            return "S";
        };

        // act
        final SummaryResult result = supportWithModels(provider, normalWindowModel("m"))
                .generate(oversizeRule("m", "sampleChunks", 2), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert: two chunks sampled from all of them, as the coverage line names them
        final String total = result.body().replaceAll("(?s).*?2 of (\\d+) chunks summarized.*", "$1");
        assertThat(requests.get(0).sourceText(), startsWith("Chunk 1 of 2, sampled from " + total + " chunks:\n"));
        assertThat(requests.get(1).sourceText(), startsWith("Chunk 2 of 2, sampled from " + total + " chunks:\n"));
    }

    @Test
    public void countedTokens_aPromptAtOrAboveTheLimit_isSentOnlyAtTheLimit() throws Exception {
        // arrange
        final CountingProvider atLimit = new CountingProvider(r -> NORMAL_WINDOW_TOKEN_LIMIT);
        final CountingProvider aboveLimit = new CountingProvider(r -> NORMAL_WINDOW_TOKEN_LIMIT + 1);
        final String source = largeSource(300);
        final SummaryGenerator aboveLimitSupport = supportWithModels(aboveLimit, normalWindowModel("m"));
        final RoutingRule sampleRule = oversizeRule("m", "sample", 0);
        final Path contextFile = tempFile("Small", ".java");

        // act
        supportWithModels(atLimit, normalWindowModel("m"))
                .generate(normalRule("m", null), tempFile("Small", ".java"), "file", source);
        final IOException failed = assertThrows(
                IOException.class, () -> aboveLimitSupport.generate(sampleRule, contextFile, "file", source));

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
        final GenerationProvider provider = request -> {
            if (file.get() == 0 && request.sourceText().startsWith("Chunk ")) {
                throw cutOff("- a part");
            }
            return "S";
        };
        final SummaryGenerator support = supportWithModels(provider, normalWindowModel("m"));
        final SummaryResult first = support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));
        file.incrementAndGet();

        // act
        final SummaryResult second = support.generate(
                oversizeRule("m", "mapReduce", 0), tempFile("Huge", ".java"), "file", largeSource(7000));

        // assert
        assertThat(first.body(), containsString("cut at the output budget"));
        assertThat(second.body().contains("cut at the output budget"), is(false));
        assertThat(second.body(), startsWith(MapReduceMath.COVERAGE_HEADER));
    }
    // </editor-fold>
}

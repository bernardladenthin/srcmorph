// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiCondition;
import net.ladenthin.srcmorph.config.AiFactDefinition;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.indexer.AiFieldGenerationSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

public class GenerateEngineTest {

    @TempDir
    Path tempDir;

    /**
     * A routable rule matching {@code .java} files, with the {@code default} AI definition key. Unlike
     * {@link CommonTestFixtures#createFileFieldGenerations()} (whose rule carries no condition — fine for
     * the indexer-level tests that never call {@link net.ladenthin.srcmorph.config.AiFieldGenerationSelector#validate}),
     * {@link GenerateEngine#execute()} does call {@code validate}, so the rule here needs a real condition.
     */
    private static AiFieldGenerationConfig javaFileRule() {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final AiCondition condition = new AiCondition();
        condition.setExtensions(Arrays.asList(".java"));
        rule.setCondition(condition);
        return rule;
    }

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock provider,
     * the engine always builds a {@link net.ladenthin.srcmorph.provider.LlamaCppJniConfig} value object
     * before dispatching to {@link net.ladenthin.srcmorph.provider.AiGenerationProviderFactory}, and that
     * value object requires a non-null model path.
     */
    private static AiModelDefinition mockModelDefinition() {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        definition.setModelPath("mock.gguf");
        // Disable automatic maxInputChars calculation so the 13-byte test source never trips the
        // oversize path in the "happy path" tests below.
        definition.setCharsPerToken(0);
        return definition;
    }

    private SrcMorphConfiguration baseConfig() throws IOException {
        final Path sourceRoot = tempDir.resolve("src/main/java");
        Files.createDirectories(sourceRoot.resolve("com/example"));
        Files.write(sourceRoot.resolve("com/example/Foo.java"), "class Foo {}\n".getBytes(StandardCharsets.UTF_8));

        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(tempDir.resolve("out").toFile());
        config.setGenerationProvider("mock");
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setPromptDefinitions(CommonTestFixtures.createFilePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        config.setFieldGenerations(Collections.singletonList(javaFileRule()));
        return config;
    }

    @Test
    public void execute_missingFieldGenerationsThrowsSrcMorphException() throws IOException {
        final SrcMorphConfiguration config = baseConfig();
        config.setFieldGenerations(null);

        final SrcMorphException e = assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());
        assertThat(e.getMessage(), containsString("No <fieldGenerations> configured"));
    }

    @Test
    public void execute_planOnlyStopsBeforeGenerating() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        config.setPlanOnly(true);

        final GenerateResult result = new GenerateEngine(config).execute();

        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    @Test
    public void execute_writesMatchedFileAndReportsCounts() throws Exception {
        final SrcMorphConfiguration config = baseConfig();

        final GenerateResult result = new GenerateEngine(config).execute();

        assertThat(result.planOnly(), is(false));
        assertThat(result.written(), is(1));
        assertThat(result.unchanged(), is(0));
        assertThat(result.skipped(), is(0));
        // SourceFileIndexer relativises against the "src" root but keeps the "main/java/..." tail.
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
    }

    @Test
    public void execute_secondRunWithoutForceReportsUnchanged() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        new GenerateEngine(config).execute();

        final GenerateResult second = new GenerateEngine(config).execute();

        assertThat(second.written(), is(0));
        assertThat(second.unchanged(), is(1));
    }

    @Test
    public void execute_unmatchedFileWithNoFallbackThrowsSrcMorphException() throws IOException {
        final SrcMorphConfiguration config = baseConfig();
        final AiFieldGenerationConfig onlyMatchesTxt = new AiFieldGenerationConfig();
        onlyMatchesTxt.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        onlyMatchesTxt.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final AiCondition condition = new AiCondition();
        condition.setExtensions(Arrays.asList(".txt"));
        onlyMatchesTxt.setCondition(condition);
        config.setFieldGenerations(Arrays.asList(onlyMatchesTxt));

        final SrcMorphException e = assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());
        assertThat(e.getMessage(), containsString("matched no rule and no fallback"));
    }

    @Test
    public void execute_oversizeFailThrowsSrcMorphException() throws Exception {
        final SrcMorphConfiguration config = baseConfig();

        // Force the routed model's window to be smaller than the source, with the default
        // onOversize=fail strategy, so the file is a hard failure. The window check happens entirely
        // during planning, before any LlamaCppJniConfig/provider is built, so modelPath is irrelevant here.
        final AiModelDefinition tinyWindow = new AiModelDefinition();
        tinyWindow.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        tinyWindow.setCharsPerToken(1);
        tinyWindow.setContextSize(1);
        tinyWindow.setMaxOutputTokens(0);
        config.setAiDefinitions(Collections.singletonList(tinyWindow));

        final SrcMorphException e = assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());
        assertThat(e.getMessage(), containsString("exceed their routed model's context window"));
    }

    // <editor-fold defaultstate="collapsed" desc="user-facing failure paths">

    private ListAppender<ILoggingEvent> logAppender;

    /**
     * The loggers the tests read: the engine's, the engine support's, and the field generation's, which
     * reports what an oversize strategy did.
     */
    private static List<Logger> capturedLoggers() {
        return Arrays.asList(
                (Logger) LoggerFactory.getLogger(GenerateEngine.class),
                (Logger) LoggerFactory.getLogger(EngineSupport.class),
                (Logger) LoggerFactory.getLogger(AiFieldGenerationSupport.class));
    }

    @BeforeEach
    public void attachLogAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        for (final Logger logger : capturedLoggers()) {
            logger.setLevel(Level.DEBUG);
            logger.addAppender(logAppender);
        }
    }

    @AfterEach
    public void detachLogAppender() {
        for (final Logger logger : capturedLoggers()) {
            logger.detachAppender(logAppender);
        }
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

    /**
     * A configured subtree that does not exist fails the run before anything is planned. It used to
     * be filtered out with a warning, which left the list empty, so the engine fell back to the
     * default {@code src/main/java} and a typo'd {@code <subtree>} silently indexed another tree.
     */
    @Test
    public void execute_configuredSubtreeDoesNotExist_failsNamingIt() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        config.setSubtrees(Collections.singletonList("does/not/exist"));

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        assertThat(thrown.getMessage(), containsString("Subtree 'does/not/exist' does not exist"));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    /**
     * The counterpart: a subtree that DOES exist is used, and no warning is emitted. Without it, a
     * mutant that warns unconditionally -- or that never resolves a subtree at all -- survives.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void execute_configuredSubtreeExists_isUsedWithoutWarning() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setSubtrees(Collections.singletonList("src/main/java"));

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(1));
        for (final String message : capturedMessages(Level.WARN)) {
            assertThat("no subtree warning expected", message.contains("Skipping missing subtree"), is(false));
        }
    }

    /**
     * A {@code factsKey} that matches no {@code factDefinitions} group is a configuration error and
     * must surface as a {@link SrcMorphException} naming the offending setting -- the message is what
     * the user sees, so it is part of the contract.
     *
     * @throws IOException if the fixture cannot be built
     */
    @Test
    public void execute_unknownFactsKey_throwsWithTheConfigurationHint() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final AiFieldGenerationConfig rule = javaFileRule();
        rule.setFactsKey("no-such-group");
        config.setFieldGenerations(Collections.singletonList(rule));

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        // assert
        assertThat(thrown.getMessage(), containsString("Invalid factDefinitions/factsKey configuration"));
    }

    // <editor-fold defaultstate="collapsed" desc="profile directory">
    private Path profile(final String factsFileText) throws IOException {
        final Path profile = Files.createDirectories(tempDir.resolve("profile"));
        Files.write(
                profile.resolve(CommonTestFixtures.PROMPT_KEY_FILE_BODY + ".md"),
                "You are a Java code indexer.\n".getBytes(StandardCharsets.UTF_8));
        Files.write(profile.resolve("java-facts.tsv"), factsFileText.getBytes(StandardCharsets.UTF_8));
        return profile;
    }

    /**
     * A configuration that names a profile directory needs no {@code promptDefinitions} and no
     * {@code factDefinitions}: the rule's keys are files there.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void execute_keysOfTheRule_areReadFromTheProfileDirectory() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setPromptDefinitions(null);
        config.setProfileDirectory(
                profile("# counters\nclasses\t\\bclass\\s+\\w\n").toFile());
        final AiFieldGenerationConfig rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setFieldGenerations(Collections.singletonList(rule));

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(1));
        final String written = new String(
                Files.readAllBytes(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")),
                StandardCharsets.UTF_8);
        assertThat(written, containsString("classes: 1"));
    }

    @Test
    public void execute_malformedFactsFileInTheProfile_throwsWithTheConfigurationHint() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setProfileDirectory(profile("no tab on this line\n").toFile());
        final AiFieldGenerationConfig rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setFieldGenerations(Collections.singletonList(rule));

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        // assert
        assertThat(thrown.getMessage(), containsString("Invalid factDefinitions/factsKey configuration"));
        assertThat(thrown.getMessage(), containsString("java-facts.tsv line 1"));
    }

    @Test
    public void execute_unreadableFactsFile_throwsWithTheConfigurationHint() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final AiFactDefinition definition = new AiFactDefinition();
        definition.setKey("java-facts");
        definition.setFactsFile(tempDir.resolve("missing.tsv").toString());
        config.setFactDefinitions(Collections.singletonList(definition));
        final AiFieldGenerationConfig rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setFieldGenerations(Collections.singletonList(rule));

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        // assert
        assertThat(thrown.getMessage(), containsString("cannot be read"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="planOnly stays model-free">

    /**
     * A plan-only run must not require the model file to exist.
     *
     * <p>{@code planOnly} is documented to "stop before loading any model" (plugin README) and the
     * CLI's {@code Plan} command promises "no model is loaded" — and it is the CLI's default. The
     * workflow it exists for is configuring routing on a machine where the GGUFs are not present and
     * running for real on the box that has them. Stat'ing the file is not loading it, but failing the
     * plan on a missing model breaks that workflow all the same.
     *
     * <p>This is the test the suite did not have: every other fixture here sets the {@code mock}
     * provider, so the routed-model-path check never ran through {@link GenerateEngine} at all. It
     * was briefly ordered before the plan-only return, which made a plan on a model-free machine
     * throw.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void execute_planOnlyWithRealProviderAndMissingModel_stillPlans() throws Exception {
        // arrange -- the real provider name, and a model path that deliberately does not exist
        final SrcMorphConfiguration config = baseConfig();
        config.setGenerationProvider(AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        config.setPlanOnly(true);

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert -- planned, not thrown, and nothing written
        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    /**
     * The counterpart: the same configuration WITHOUT {@code planOnly} must still fail fast on the
     * missing model, so moving the check below the plan-only return did not disable it.
     *
     * @throws IOException if the fixture cannot be built
     */
    @Test
    public void execute_realProviderAndMissingModel_failsFastBeforeLoading() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setGenerationProvider(AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI);

        // act
        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        // assert
        assertThat(thrown.getMessage(), containsString("does not exist"));
        assertThat(thrown.getMessage(), containsString("mock.gguf"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    @Test
    public void execute_fileWithAnIncompleteAnswer_writesTheOthersAndFailsAtTheEnd() throws Exception {
        // a loopback OpenAI-compatible stub: an empty answer for Bar.java, a summary for every other file
        final HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/v1/chat/completions", exchange -> {
            final String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            final String content = request.contains("Bar.java") ? "" : "A summary.";
            final byte[] response = ("{\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\","
                            + "\"content\":\"" + content + "\"},\"finish_reason\":\"stop\"}]}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response);
            }
        });
        server.start();
        try {
            final SrcMorphConfiguration config = baseConfig();
            Files.write(
                    tempDir.resolve("src/main/java/com/example/Bar.java"),
                    "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
            final AiModelDefinition remote = mockModelDefinition();
            remote.setProvider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
            remote.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1");
            remote.setRemoteModel("stub");
            config.setAiDefinitions(Collections.singletonList(remote));

            final SrcMorphException thrown =
                    assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

            assertThat(thrown.getMessage(), containsString("1 file(s) were not written"));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Bar.java.ai.md")), is(false));
        } finally {
            server.stop(0);
        }
    }
    // </editor-fold>

    @Test
    public void execute_fileWhoseRequestFails_writesTheOthersAndFailsAtTheEnd() throws Exception {
        // a loopback stub that refuses every request for Bar.java (401, not retried) and answers the others
        final HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/v1/chat/completions", exchange -> {
            final String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            final boolean refused = request.contains("Bar.java");
            final byte[] response = (refused
                            ? "{\"error\":\"no\"}"
                            : "{\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\","
                                    + "\"content\":\"A summary.\"},\"finish_reason\":\"stop\"}]}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(refused ? 401 : 200, response.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response);
            }
        });
        server.start();
        try {
            final SrcMorphConfiguration config = baseConfig();
            Files.write(
                    tempDir.resolve("src/main/java/com/example/Bar.java"),
                    "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
            final AiModelDefinition remote = mockModelDefinition();
            remote.setProvider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
            remote.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1");
            remote.setRemoteModel("stub");
            config.setAiDefinitions(Collections.singletonList(remote));

            final SrcMorphException thrown =
                    assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

            assertThat(thrown.getMessage(), containsString("1 file(s) were not written"));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Bar.java.ai.md")), is(false));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void execute_twoSourcesWithTheSameSummary_failNamingBoth() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        // src/main/java/com/example/Foo.java (from baseConfig) and main/java/com/example/Foo.java both
        // become main/java/com/example/Foo.java.ai.md
        final Path twin = tempDir.resolve("main/java/com/example/Foo.java");
        Files.createDirectories(twin.getParent());
        Files.write(twin, "class Foo {}\n".getBytes(StandardCharsets.UTF_8));
        config.setSubtrees(Arrays.asList("src/main/java", "main/java"));

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        assertThat(thrown.getMessage(), containsString("would both be summarized into"));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    @Test
    public void execute_promptThatFillsTheWindow_failsNamingModelAndPrompt() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        final AiModelDefinition tiny = mockModelDefinition();
        tiny.setContextSize(1);
        tiny.setCharsPerToken(1);
        config.setAiDefinitions(Collections.singletonList(tiny));
        // sample, so the over-window check of the plan lets the file through to this one
        config.getFieldGenerations().get(0).setOnOversize("sample");

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        assertThat(thrown.getMessage(), containsString("leaves no room for the source in the window of model"));
    }

    // <editor-fold defaultstate="collapsed" desc="oversize strategies, from the plan to the written file">

    /**
     * {@link #baseConfig()} plus {@code Big.java}, about 8 800 characters, on a model with a window of
     * about 3 200, routed by one rule with the given {@code onOversize} ({@code maxChunks} 2 under
     * {@code sampleChunks}).
     */
    private SrcMorphConfiguration oversizeConfig(final String onOversize) throws IOException {
        final SrcMorphConfiguration config = baseConfig();
        final AiModelDefinition small = mockModelDefinition();
        // a window of about 3 200 characters: 4 000 tokens of one character, less the output and margin
        small.setCharsPerToken(1);
        small.setContextSize(4_000);
        config.setAiDefinitions(Collections.singletonList(small));
        final AiFieldGenerationConfig rule = javaFileRule();
        rule.setOnOversize(onOversize);
        if ("sampleChunks".equals(onOversize)) {
            rule.setMaxChunks(2);
        }
        config.setFieldGenerations(Collections.singletonList(rule));
        final StringBuilder big = new StringBuilder();
        for (int i = 0; i < 400; i++) {
            big.append("int field").append(i).append(" = ").append(i).append(";\n");
        }
        Files.write(
                tempDir.resolve("src/main/java/com/example/Big.java"),
                big.toString().getBytes(StandardCharsets.UTF_8));
        return config;
    }

    private String writtenBody(final String fileName) throws IOException {
        return new AiMdDocumentCodec()
                .read(tempDir.resolve("out/main/java/com/example/" + fileName + ".ai.md"))
                .body();
    }

    private boolean logged(final Level level, final String part) {
        for (final String message : capturedMessages(level)) {
            if (message.contains(part)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void execute_deterministic_writesTheBodyWithoutAModel() throws Exception {
        final GenerateResult result = new GenerateEngine(oversizeConfig("deterministic")).execute();

        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("summarized deterministically (no AI)"));
        assertThat(writtenBody("Big.java"), containsString("First 20 lines"));
        assertThat(writtenBody("Foo.java"), containsString("Mock summary for Foo.java"));
    }

    @Test
    public void execute_sample_writesTheSummaryOfTheTrimmedHead() throws Exception {
        final GenerateResult result = new GenerateEngine(oversizeConfig("sample")).execute();

        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("Mock summary for Big.java"));
        assertThat(logged(Level.WARN, "Trimmed AI input for file"), is(true));
    }

    @Test
    public void execute_mapReduce_summarizesTheChunksAndReducesThem() throws Exception {
        final GenerateResult result = new GenerateEngine(oversizeConfig("mapReduce")).execute();

        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("Mock summary for Big.java"));
        // every chunk of the file, so as many summarized as there are
        assertThat(writtenBody("Big.java"), containsString("**Coverage:** 4 of 4 chunks summarized\n\n"));
        assertThat(logged(Level.INFO, "-> mapReduce: 7780 chars, 4 of 4 chunk(s)"), is(true));
        assertThat(logged(Level.INFO, "mapReduce reduce round 1: 4 -> 1 partial(s)"), is(true));
    }

    @Test
    public void execute_sampleChunks_readsMaxChunksAndSaysSo() throws Exception {
        final GenerateResult result = new GenerateEngine(oversizeConfig("sampleChunks")).execute();

        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("**Coverage:** 2 of 4 chunks summarized (sampled)\n\n"));
    }

    @Test
    public void execute_maxChunksWithoutSampleChunks_isAConfigurationError() throws Exception {
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        config.getFieldGenerations().get(0).setMaxChunks(6);

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());

        assertThat(thrown.getMessage(), containsString("Invalid fieldGenerations: maxChunks applies only to"));
    }

    @Test
    public void execute_fail_stopsAtThePlan() throws Exception {
        final SrcMorphConfiguration config = oversizeConfig("fail");

        assertThrows(SrcMorphException.class, () -> new GenerateEngine(config).execute());
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }
    // </editor-fold>

    /**
     * A working copy with {@code \r\n} line endings (Windows with {@code core.autocrlf}) has the
     * checksum of one with {@code \n}: a run over it after a run over the {@code \n} copy finds the
     * summary up to date.
     *
     * @throws Exception if a run fails
     */
    @Test
    public void execute_aCrLfCopyOfASummarizedSource_isUnchanged() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        assertThat(new GenerateEngine(config).execute().written(), is(1));
        Files.write(
                tempDir.resolve("src/main/java/com/example/Foo.java"),
                "class Foo {}\r\n".getBytes(StandardCharsets.UTF_8));

        final GenerateResult again = new GenerateEngine(config).execute();

        assertThat(again.written(), is(0));
        assertThat(again.unchanged(), is(1));
    }

    // <editor-fold defaultstate="collapsed" desc="run record">
    /** A factory whose providers summarize every file but one, for which they throw {@code failure}. */
    private static AiGenerationProviderFactory failingFor(final String fileName, final RuntimeException failure) {
        return new AiGenerationProviderFactory() {
            @Override
            public AiGenerationProvider create(
                    final String providerName,
                    final net.ladenthin.srcmorph.provider.LlamaCppJniConfig llamaConfig,
                    final net.ladenthin.srcmorph.prompt.AiPromptSupport promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals(fileName)) {
                        throw failure;
                    }
                    return "A summary.";
                };
            }
        };
    }

    @Test
    public void execute_anUncheckedProviderError_failsThatFileOnly_andTheRecordHasIt() throws Exception {
        // arrange: the llama.cpp binding throws unchecked exceptions only
        final SrcMorphConfiguration config = baseConfig();
        Files.write(
                tempDir.resolve("src/main/java/com/example/Bar.java"),
                "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());

        // act
        final SrcMorphException thrown = assertThrows(
                SrcMorphException.class,
                () -> new GenerateEngine(config, failingFor("Bar.java", new IllegalStateException("native")))
                        .execute());

        // assert
        assertThat(thrown.getMessage(), containsString("1 file(s) were not written"));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
        final String record = new String(Files.readAllBytes(recordFile), StandardCharsets.UTF_8);
        assertThat(record, containsString("\"failed\""));
        assertThat(record, containsString("\"written\""));
    }

    @Test
    public void execute_anAbortedRun_stillWritesTheRecordOfTheFilesDone() throws Exception {
        // arrange: an Error is no file's failure; it ends the run
        final SrcMorphConfiguration config = baseConfig();
        Files.write(
                tempDir.resolve("src/main/java/com/example/Bar.java"),
                "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final AiGenerationProviderFactory aborting = new AiGenerationProviderFactory() {
            @Override
            public AiGenerationProvider create(
                    final String providerName,
                    final net.ladenthin.srcmorph.provider.LlamaCppJniConfig llamaConfig,
                    final net.ladenthin.srcmorph.prompt.AiPromptSupport promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals("Foo.java")) {
                        // what an interrupted blocking call does: the thread's flag is set, then it throws
                        Thread.currentThread().interrupt();
                        throw new java.io.InterruptedIOException("interrupted");
                    }
                    return "A summary.";
                };
            }
        };

        // act
        try {
            assertThrows(java.io.InterruptedIOException.class, () -> new GenerateEngine(config, aborting).execute());
        } finally {
            // the test thread goes on, without the flag
            Thread.interrupted();
        }

        // assert: the record exists and holds what was done before the interrupt
        assertThat(Files.exists(recordFile), is(true));
    }

    @Test
    public void execute_aTimeoutOfOneFile_failsThatFileNotTheRun() throws Exception {
        // arrange: a socket timeout is an InterruptedIOException too, but no interrupt
        final SrcMorphConfiguration config = baseConfig();
        Files.write(
                tempDir.resolve("src/main/java/com/example/Bar.java"),
                "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final AiGenerationProviderFactory timingOut = new AiGenerationProviderFactory() {
            @Override
            public AiGenerationProvider create(
                    final String providerName,
                    final net.ladenthin.srcmorph.provider.LlamaCppJniConfig llamaConfig,
                    final net.ladenthin.srcmorph.prompt.AiPromptSupport promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals("Foo.java")) {
                        throw new java.net.SocketTimeoutException("Read timed out");
                    }
                    return "A summary.";
                };
            }
        };

        // act
        final SrcMorphException failed =
                assertThrows(SrcMorphException.class, () -> new GenerateEngine(config, timingOut).execute());

        // assert: the run went on to the other file, and failed at its end naming the one
        assertThat(failed.getMessage(), containsString("1 file(s) were not written"));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Bar.java.ai.md")), is(true));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(false));
    }

    @Test
    public void execute_runRecordFile_recordsEveryRoutedFile() throws Exception {
        final SrcMorphConfiguration config = baseConfig();
        final Path recordFile = tempDir.resolve("records/run.json");
        config.setRunRecordFile(recordFile.toFile());

        new GenerateEngine(config).execute();
        final com.fasterxml.jackson.databind.JsonNode first =
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(recordFile.toFile());
        new GenerateEngine(config).execute();
        final com.fasterxml.jackson.databind.JsonNode second =
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(recordFile.toFile());

        final com.fasterxml.jackson.databind.JsonNode file = first.get("files").get(0);
        assertThat(file.get("file").asText(), is("src/main/java/com/example/Foo.java"));
        assertThat(file.get("promptKey").asText(), is(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
        assertThat(file.get("model").asText(), is(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        assertThat(file.get("outcome").asText(), is("written"));
        assertThat(file.get("calls").asInt(), is(1));
        assertThat(file.get("completionTokens").asLong() > 0, is(true));
        assertThat(file.get("finishReason").asText(), is("stop"));
        assertThat(file.get("promptHash").asText().matches("[0-9A-F]{8}"), is(true));
        assertThat(first.get("pluginVersion").asText(), is("1.0.0"));
        // the second run found it up to date: no call, no tokens
        assertThat(second.get("files").get(0).get("outcome").asText(), is("unchanged"));
        assertThat(second.get("totals").get("calls").asInt(), is(0));
    }

    @Test
    public void execute_aChangedPrompt_theRecordOfAnUnchangedFileShowsThePromptThatWroteIt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final com.fasterxml.jackson.databind.ObjectMapper json = new com.fasterxml.jackson.databind.ObjectMapper();

        // act: write, change the prompt, run again (unchanged), then force
        new GenerateEngine(config).execute();
        final String written = json.readTree(recordFile.toFile())
                .get("files")
                .get(0)
                .get("promptHash")
                .asText();
        config.getPromptDefinitions().get(0).setTemplate("Another prompt.\n\nFile: %s\n\nSource:\n%s\n");
        new GenerateEngine(config).execute();
        final com.fasterxml.jackson.databind.JsonNode unchanged =
                json.readTree(recordFile.toFile()).get("files").get(0);
        config.setForce(true);
        new GenerateEngine(config).execute();
        final String rewritten = json.readTree(recordFile.toFile())
                .get("files")
                .get(0)
                .get("promptHash")
                .asText();

        // assert
        assertThat(unchanged.get("outcome").asText(), is("unchanged"));
        assertThat(unchanged.get("promptHash").asText(), is(written));
        assertThat(rewritten.equals(written), is(false));
        final String summary = new String(
                Files.readAllBytes(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")),
                StandardCharsets.UTF_8);
        assertThat(summary, containsString("- P: " + rewritten + "\n"));
    }

    @Test
    public void execute_noRunRecordFile_writesNoRecord() throws Exception {
        new GenerateEngine(baseConfig()).execute();

        try (java.util.stream.Stream<Path> files = Files.walk(tempDir)) {
            assertThat(files.anyMatch(p -> p.toString().endsWith(".json")), is(false));
        }
    }

    @Test
    public void factsHash_changesWithEveryPartOfACounter() {
        final net.ladenthin.srcmorph.config.AiFactCounter counter = new net.ladenthin.srcmorph.config.AiFactCounter();
        counter.setLabel("tables");
        counter.setPattern("CREATE TABLE");
        final String plain =
                net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(Collections.singletonList(counter));
        counter.setIgnore(true);
        final String ignored =
                net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(Collections.singletonList(counter));
        counter.setIgnore(false);
        counter.setPattern("CREATE VIEW");
        final String otherPattern =
                net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(Collections.singletonList(counter));
        counter.setLabel("views");
        final String otherLabel =
                net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(Collections.singletonList(counter));

        assertThat(plain.matches("[0-9A-F]{8}"), is(true));
        assertThat(new java.util.HashSet<>(Arrays.asList(plain, ignored, otherPattern, otherLabel)).size(), is(4));
        assertThat(net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(null), is(""));
        assertThat(net.ladenthin.srcmorph.config.AiFactExtractor.factsHash(Collections.emptyList()), is(""));
    }
    // </editor-fold>
}

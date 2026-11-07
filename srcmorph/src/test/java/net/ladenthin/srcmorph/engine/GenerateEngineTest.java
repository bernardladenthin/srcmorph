// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.FactDefinition;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.facts.FactExtractor;
import net.ladenthin.srcmorph.indexer.HeaderHashes;
import net.ladenthin.srcmorph.indexer.generation.SummaryGenerator;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.GenerationProviderFactory;
import net.ladenthin.srcmorph.provider.ProviderConfig;
import net.ladenthin.srcmorph.support.ChecksumSupport;
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
     * {@link CommonTestFixtures#createFileRules()} (whose rule carries no condition — fine for
     * the indexer-level tests that never call {@link net.ladenthin.srcmorph.routing.RuleSelector#validate}),
     * {@link GenerateEngine#execute()} does call {@code validate}, so the rule here needs a real condition.
     */
    private static RoutingRule javaFileRule() {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final RuleCondition condition = new RuleCondition();
        condition.setExtensions(Arrays.asList(".java"));
        rule.setCondition(condition);
        return rule;
    }

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock provider,
     * the engine always builds a {@link net.ladenthin.srcmorph.provider.ProviderConfig} value object
     * before dispatching to {@link net.ladenthin.srcmorph.provider.GenerationProviderFactory}, and that
     * value object requires a non-null model path.
     */
    private static ModelDefinition mockModelDefinition() {
        final ModelDefinition definition = new ModelDefinition();
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
        config.setGeneratorVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setPromptDefinitions(CommonTestFixtures.createFilePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        config.setRules(Collections.singletonList(javaFileRule()));
        return config;
    }

    // <editor-fold defaultstate="collapsed" desc="execute">
    @Test
    public void execute_noRules_throwsSrcMorphException() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(null);
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("No rules configured"));
    }

    /** A list built in code may hold a null rule (the file's loader refuses one): it fails by its index. */
    @Test
    public void execute_aNullRule_throwsSrcMorphExceptionNamingItsIndex() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setRules(Arrays.asList(javaFileRule(), null));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), is("Invalid rules: srcMorph.rules[1] is null"));
    }

    @Test
    public void execute_unclosedGlobInFileNamesOrExcludes_throwsSrcMorphException() throws IOException {
        // arrange
        final SrcMorphConfiguration names = baseConfig();
        names.setFileNames(Collections.singletonList("Dockerfile.{dev"));
        final SrcMorphConfiguration excludes = baseConfig();
        excludes.setExcludes(Collections.singletonList("**/{target/**"));
        final GenerateEngine byNameEngine = new GenerateEngine(names);
        final GenerateEngine byExcludeEngine = new GenerateEngine(excludes);

        // act
        final SrcMorphException byName = assertThrows(SrcMorphException.class, byNameEngine::execute);
        final SrcMorphException byExclude = assertThrows(SrcMorphException.class, byExcludeEngine::execute);

        // assert
        assertThat(
                byName.getMessage(),
                containsString("Invalid excludes, fileNames, fileInterpreters or contentHeadChars: the glob"
                        + " 'Dockerfile.{dev'"));
        assertThat(
                byExclude.getMessage(),
                containsString("Invalid excludes, fileNames, fileInterpreters or contentHeadChars: the glob"
                        + " '**/{target/**'"));
    }

    @Test
    public void execute_invalidInterpreterOrContentHeadChars_throwsSrcMorphException() throws IOException {
        // arrange
        final SrcMorphConfiguration interpreter = baseConfig();
        interpreter.setFileInterpreters(Collections.singletonList("/bin/bash"));
        final SrcMorphConfiguration head = baseConfig();
        head.setContentHeadChars(0);
        final GenerateEngine byInterpreterEngine = new GenerateEngine(interpreter);
        final GenerateEngine byHeadEngine = new GenerateEngine(head);

        // act
        final SrcMorphException byInterpreter = assertThrows(SrcMorphException.class, byInterpreterEngine::execute);
        final SrcMorphException byHead = assertThrows(SrcMorphException.class, byHeadEngine::execute);

        // assert
        assertThat(byInterpreter.getMessage(), containsString("(bash, not /bin/bash): '/bin/bash'"));
        assertThat(
                byHead.getMessage(), containsString("contentHeadChars must be at least 256 (a whole #! line), not 0"));
    }

    @Test
    public void execute_fileInterpreters_indexAScriptWithoutAnExtension() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path script = tempDir.resolve("src/main/java/com/example/deploy");
        Files.write(script, "#!/usr/bin/env bash\necho deploy\n".getBytes(StandardCharsets.UTF_8));
        config.setFileInterpreters(Collections.singletonList("bash"));
        final RoutingRule fallback = new RoutingRule();
        fallback.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        fallback.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        fallback.setFallback(true);
        config.setRules(Arrays.asList(javaFileRule(), fallback));

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/deploy.ai.md")), is(true));
    }

    @Test
    public void execute_fileNames_indexAFileWithoutAnExtension() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path dockerfile = tempDir.resolve("src/main/java/com/example/Dockerfile");
        Files.write(dockerfile, "FROM eclipse-temurin:21\n".getBytes(StandardCharsets.UTF_8));
        config.setFileNames(Collections.singletonList("Dockerfile"));
        final RoutingRule fallback = new RoutingRule();
        fallback.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        fallback.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        fallback.setFallback(true);
        config.setRules(Arrays.asList(javaFileRule(), fallback));

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Dockerfile.ai.md")), is(true));
    }

    @Test
    public void execute_planOnly_stopsBeforeGenerating() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setPlanOnly(true);

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    @Test
    public void execute_matchedFile_writesItAndReportsCounts() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.planOnly(), is(false));
        assertThat(result.written(), is(1));
        assertThat(result.unchanged(), is(0));
        assertThat(result.skipped(), is(0));
        // SourceFileIndexer relativises against the "src" root but keeps the "main/java/..." tail.
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
    }

    @Test
    public void execute_secondRunWithoutForce_reportsUnchanged() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        new GenerateEngine(config).execute();

        // act
        final GenerateResult second = new GenerateEngine(config).execute();

        // assert
        assertThat(second.written(), is(0));
        assertThat(second.unchanged(), is(1));
    }

    @Test
    public void execute_unmatchedFileWithNoFallback_throwsSrcMorphException() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final RoutingRule onlyMatchesTxt = new RoutingRule();
        onlyMatchesTxt.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        onlyMatchesTxt.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final RuleCondition condition = new RuleCondition();
        condition.setExtensions(Arrays.asList(".txt"));
        onlyMatchesTxt.setCondition(condition);
        config.setRules(Arrays.asList(onlyMatchesTxt));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("matched no rule and no fallback"));
    }

    @Test
    public void execute_oversizeFail_throwsSrcMorphException() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();

        // Force the routed model's window to be smaller than the source, with the default
        // onOversize=fail strategy, so the file is a hard failure. The window check happens entirely
        // during planning, before any ProviderConfig/provider is built, so modelPath is irrelevant here.
        final ModelDefinition tinyWindow = new ModelDefinition();
        tinyWindow.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        tinyWindow.setCharsPerToken(1);
        tinyWindow.setContextSize(1);
        tinyWindow.setMaxOutputTokens(0);
        config.setAiDefinitions(Collections.singletonList(tinyWindow));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException e = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(e.getMessage(), containsString("exceed their routed model's context window"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="user-facing failure paths">

    private ListAppender<ILoggingEvent> logAppender;

    /**
     * The loggers the tests read: the engine's, the engine support's, and the summary generator's, which
     * reports what an oversize strategy did.
     */
    private static List<Logger> capturedLoggers() {
        return Arrays.asList(
                (Logger) LoggerFactory.getLogger(GenerateEngine.class),
                (Logger) LoggerFactory.getLogger(EngineSupport.class),
                (Logger) LoggerFactory.getLogger(SummaryGenerator.class));
    }

    /** Each captured logger's level before the test, restored after it (null: inherited). */
    private final Map<Logger, Level> previousLevels = new HashMap<>();

    @BeforeEach
    public void attachLogAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        for (final Logger logger : capturedLoggers()) {
            previousLevels.put(logger, logger.getLevel());
            logger.setLevel(Level.DEBUG);
            logger.addAppender(logAppender);
        }
    }

    @AfterEach
    public void detachLogAppender() {
        for (final Logger logger : capturedLoggers()) {
            logger.detachAppender(logAppender);
            logger.setLevel(previousLevels.get(logger));
        }
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

    /**
     * A configured subtree that does not exist fails the run before anything is planned. It used to
     * be filtered out with a warning, which left the list empty, so the engine fell back to the
     * default {@code src/main/java} and a typo'd {@code <subtree>} silently indexed another tree.
     */
    @Test
    public void execute_configuredSubtreeDoesNotExist_failsNamingIt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setSubtrees(Collections.singletonList("does/not/exist"));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
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
        final RoutingRule rule = javaFileRule();
        rule.setFactsKey("no-such-group");
        config.setRules(Collections.singletonList(rule));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

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
        final RoutingRule rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setRules(Collections.singletonList(rule));

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
        final RoutingRule rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setRules(Collections.singletonList(rule));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("Invalid factDefinitions/factsKey configuration"));
        assertThat(thrown.getMessage(), containsString("java-facts.tsv line 1"));
    }

    @Test
    public void execute_unreadableFactsFile_throwsWithTheConfigurationHint() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final FactDefinition definition = new FactDefinition();
        definition.setKey("java-facts");
        definition.setFactsFile(tempDir.resolve("missing.tsv").toString());
        config.setFactDefinitions(Collections.singletonList(definition));
        final RoutingRule rule = javaFileRule();
        rule.setFactsKey("java-facts");
        config.setRules(Collections.singletonList(rule));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

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
        config.setGenerationProvider(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
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
        config.setGenerationProvider(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("does not exist"));
        assertThat(thrown.getMessage(), containsString("mock.gguf"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    @Test
    public void execute_fileWithAnIncompleteAnswer_writesTheOthersAndFailsAtTheEnd() throws Exception {
        // arrange
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
            final ModelDefinition remote = mockModelDefinition();
            remote.setProvider(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
            remote.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1");
            remote.setRemoteModel("stub");
            config.setAiDefinitions(Collections.singletonList(remote));
            final GenerateEngine engine = new GenerateEngine(config);

            // act
            final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

            // assert
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
        // arrange
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
            final ModelDefinition remote = mockModelDefinition();
            remote.setProvider(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
            remote.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1");
            remote.setRemoteModel("stub");
            config.setAiDefinitions(Collections.singletonList(remote));
            final GenerateEngine engine = new GenerateEngine(config);

            // act
            final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

            // assert
            assertThat(thrown.getMessage(), containsString("1 file(s) were not written"));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
            assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Bar.java.ai.md")), is(false));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void execute_twoSourcesWithTheSameSummary_failNamingBoth() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        // src/main/java/com/example/Foo.java (from baseConfig) and main/java/com/example/Foo.java both
        // become main/java/com/example/Foo.java.ai.md
        final Path twin = tempDir.resolve("main/java/com/example/Foo.java");
        Files.createDirectories(twin.getParent());
        Files.write(twin, "class Foo {}\n".getBytes(StandardCharsets.UTF_8));
        config.setSubtrees(Arrays.asList("src/main/java", "main/java"));
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("would both be summarized into"));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    @Test
    public void execute_promptThatFillsTheWindow_failsNamingModelAndPrompt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final ModelDefinition tiny = mockModelDefinition();
        tiny.setContextSize(1);
        tiny.setCharsPerToken(1);
        config.setAiDefinitions(Collections.singletonList(tiny));
        // sample, so the over-window check of the plan lets the file through to this one
        config.getRules().get(0).setOnOversize("sample");
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("leaves no room for the source in the window of model"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="oversize strategies, from the plan to the written file">

    /**
     * A {@code contextSize} for {@link #oversizeConfig}'s model whose window holds source, but leaves a
     * chunk fewer than 1000 characters next to the map and reduce prompts.
     */
    private static final int CONTEXT_WITHOUT_ROOM_FOR_A_CHUNK = 2_000;

    /**
     * {@link #baseConfig()} plus {@code Big.java}, about 8 800 characters, on a model with a window of
     * about 3 200, routed by one rule with the given {@code onOversize} ({@code maxChunks} 2 under
     * {@code sampleChunks}).
     */
    private SrcMorphConfiguration oversizeConfig(final String onOversize) throws IOException {
        final SrcMorphConfiguration config = baseConfig();
        final ModelDefinition small = mockModelDefinition();
        // a window of about 3 200 characters: 4 000 tokens of one character, less the output and margin
        small.setCharsPerToken(1);
        small.setContextSize(4_000);
        config.setAiDefinitions(Collections.singletonList(small));
        final RoutingRule rule = javaFileRule();
        rule.setOnOversize(onOversize);
        if ("sampleChunks".equals(onOversize)) {
            rule.setMaxChunks(2);
        }
        config.setRules(Collections.singletonList(rule));
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
        // act
        final GenerateResult result = new GenerateEngine(oversizeConfig("deterministic")).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("summarized deterministically (no AI)"));
        assertThat(writtenBody("Big.java"), containsString("First 20 lines"));
        assertThat(writtenBody("Foo.java"), containsString("Mock summary for Foo.java"));
    }

    @Test
    public void execute_sample_writesTheSummaryOfTheTrimmedHead() throws Exception {
        // act
        final GenerateResult result = new GenerateEngine(oversizeConfig("sample")).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("Mock summary for Big.java"));
        assertThat(logged(Level.WARN, "Trimmed AI input for file"), is(true));
    }

    @Test
    public void execute_mapReduce_summarizesTheChunksAndReducesThem() throws Exception {
        // act
        final GenerateResult result = new GenerateEngine(oversizeConfig("mapReduce")).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("Mock summary for Big.java"));
        // every chunk of the file, so as many summarized as there are
        assertThat(writtenBody("Big.java"), containsString("**Coverage:** 4 of 4 chunks summarized\n\n"));
        assertThat(logged(Level.INFO, "-> mapReduce: 7780 chars, 4 of 4 chunk(s)"), is(true));
        assertThat(logged(Level.INFO, "mapReduce reduce round 1: 4 -> 1 partial(s)"), is(true));
    }

    @Test
    public void execute_partsDirectory_keepsTheMapAndReduceAnswersOfAChunkedFile() throws Exception {
        // arrange
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        final Path parts = tempDir.resolve("parts");
        config.setPartsDirectory(parts.toFile());

        // act
        new GenerateEngine(config).execute();

        // assert: Big.java was read in 4 chunks and one reduce, Foo.java in one piece
        final String kept = new String(
                Files.readAllBytes(parts.resolve("src/main/java/com/example/Big.java" + GenerateEngine.PARTS_SUFFIX)),
                StandardCharsets.UTF_8);
        assertThat(
                kept, startsWith("# Map-reduce answers of src/main/java/com/example/Big.java\n\n## Chunk 1 of 4\n\n"));
        assertThat(kept, containsString("## Chunk 4 of 4\n\n"));
        assertThat(kept, containsString("## Reduce round 1, batch 1 of 1 (4 parts)\n\n"));
        assertThat(
                Files.exists(parts.resolve("src/main/java/com/example/Foo.java" + GenerateEngine.PARTS_SUFFIX)),
                is(false));
        assertThat(logged(Level.INFO, "Kept the map-reduce answers of src/main/java/com/example/Big.java"), is(true));
    }

    @Test
    public void execute_partsDirectoryAndAnUnchangedFileAfterAChunkedOne_keepsNothingForTheUnchanged()
            throws Exception {
        // arrange: a first run writes both summaries; then only Big.java changes, so Foo.java, after it
        // in path order, is unchanged and never generated
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        final Path parts = tempDir.resolve("parts");
        config.setPartsDirectory(parts.toFile());
        new GenerateEngine(config).execute();
        final Path big = tempDir.resolve("src/main/java/com/example/Big.java");
        Files.write(big, "// changed\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        final Path fooParts = parts.resolve("src/main/java/com/example/Foo.java" + GenerateEngine.PARTS_SUFFIX);

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.unchanged(), is(1));
        assertThat(Files.exists(fooParts), is(false));
    }

    @Test
    public void execute_noPartsDirectory_keepsNoAnswers() throws Exception {
        // act
        new GenerateEngine(oversizeConfig("mapReduce")).execute();

        // assert
        assertThat(logged(Level.INFO, "Kept the map-reduce answers"), is(false));
    }

    @Test
    public void execute_aPartsDirectoryThatCannotBeWritten_failsNoFile() throws Exception {
        // arrange: the parts directory is a file, so no directory can be made below it
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        final Path blocked = Files.write(tempDir.resolve("blocked"), new byte[0]);
        config.setPartsDirectory(blocked.toFile());

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(
                logged(Level.WARN, "Keeping the map-reduce answers of src/main/java/com/example/Big.java in "),
                is(true));
    }

    @Test
    public void execute_sampleChunks_readsMaxChunksAndSaysSo() throws Exception {
        // act
        final GenerateResult result = new GenerateEngine(oversizeConfig("sampleChunks")).execute();

        // assert
        assertThat(result.written(), is(2));
        assertThat(writtenBody("Big.java"), containsString("**Coverage:** 2 of 4 chunks summarized (sampled)\n\n"));
    }

    /**
     * A map-reduce rule over a window that leaves a chunk fewer than 1000 characters next to the map and
     * reduce prompts: the run would refuse the file, so the plan fails it before a model loads.
     */
    @Test
    public void execute_aWindowWithTooLittleRoomForAChunk_failsThePlan() throws Exception {
        // arrange
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        config.getAiDefinitions().get(0).setContextSize(CONTEXT_WITHOUT_ROOM_FOR_A_CHUNK);
        config.setPlanOnly(true);
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(
                thrown.getMessage(),
                startsWith("1 source file(s) are chunked in a window that leaves a chunk"
                        + " fewer than 1000 characters next to the map and reduce prompts"));
    }

    /**
     * The same window on a model whose provider counts its prompt's tokens: the run sizes the window by the
     * count, which may leave the chunks room or let the file fit, so the plan warns instead of failing.
     */
    @Test
    public void execute_tooLittleRoomForAChunkOnACountingProvider_warnsInThePlan() throws Exception {
        // arrange
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        final ModelDefinition model = config.getAiDefinitions().get(0);
        model.setContextSize(CONTEXT_WITHOUT_ROOM_FOR_A_CHUNK);
        model.setProvider(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        config.setPlanOnly(true);

        // act
        final GenerateResult result = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(0));
        assertThat(logged(Level.INFO, "onOversize=mapReduce -> handled; WARNING: "), is(true));
        assertThat(logged(Level.INFO, "FAILS BUILD"), is(false));
    }

    @Test
    public void execute_maxChunksWithoutSampleChunks_isAConfigurationError() throws Exception {
        // arrange
        final SrcMorphConfiguration config = oversizeConfig("mapReduce");
        config.getRules().get(0).setMaxChunks(6);
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("Invalid rules: maxChunks applies only to"));
    }

    @Test
    public void execute_fail_stopsAtThePlan() throws Exception {
        // arrange
        final SrcMorphConfiguration config = oversizeConfig("fail");
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="line endings">
    /**
     * A working copy with {@code \r\n} line endings (Windows with {@code core.autocrlf}) has the
     * checksum of one with {@code \n}: a run over it after a run over the {@code \n} copy finds the
     * summary up to date.
     *
     * @throws Exception if a run fails
     */
    @Test
    public void execute_aCrLfCopyOfASummarizedSource_isUnchanged() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();

        // pre-assert
        assertThat(new GenerateEngine(config).execute().written(), is(1));

        // arrange
        Files.write(
                tempDir.resolve("src/main/java/com/example/Foo.java"),
                "class Foo {}\r\n".getBytes(StandardCharsets.UTF_8));

        // act
        final GenerateResult again = new GenerateEngine(config).execute();

        // assert
        assertThat(again.written(), is(0));
        assertThat(again.unchanged(), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="run record">
    /** A factory whose providers summarize every file but one, for which they throw {@code failure}. */
    private static GenerationProviderFactory failingFor(final String fileName, final RuntimeException failure) {
        return new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog promptSupport) {
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
    public void execute_anUncheckedProviderError_failsThatFileOnlyAndTheRecordHasIt() throws Exception {
        // arrange: the llama.cpp binding throws unchecked exceptions only
        final SrcMorphConfiguration config = baseConfig();
        Files.write(
                tempDir.resolve("src/main/java/com/example/Bar.java"),
                "class Bar {}\n".getBytes(StandardCharsets.UTF_8));
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final GenerateEngine engine =
                new GenerateEngine(config, failingFor("Bar.java", new IllegalStateException("native")));

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("1 file(s) were not written"));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
        final String written = new String(Files.readAllBytes(recordFile), StandardCharsets.UTF_8);
        assertThat(written, containsString("\"failed\""));
        assertThat(written, containsString("\"written\""));
    }

    /** A local model loads on its first file: the record names that time under the model, not the file. */
    @Test
    public void execute_aModelThatLoadsOnItsFirstFile_isRecordedAsTheModelsLoad() throws Exception {
        // arrange: a provider that reports 3 s of loading once
        final SrcMorphConfiguration config = baseConfig();
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final GenerationProviderFactory loading = new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog promptSupport) {
                return new GenerationProvider() {
                    private long load = 3_000_000_000L;

                    @Override
                    public String generate(final GenerationRequest request) {
                        return "A summary.";
                    }

                    @Override
                    public long takeLoadNanos() {
                        final long taken = load;
                        load = 0L;
                        return taken;
                    }
                };
            }
        };

        // act
        new GenerateEngine(config, loading).execute();

        // assert: the model's load in the totals, the file's own time without it
        final JsonNode json = new ObjectMapper().readTree(recordFile.toFile());
        assertThat(
                json.get("totals")
                        .get("modelLoadSeconds")
                        .get(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT)
                        .asDouble(),
                is(3.0d));
        assertThat(json.get("files").get(0).get("seconds").asDouble() < 3.0d, is(true));
        assertThat(json.get("totals").get("skipped").asInt(), is(0));
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
        final GenerationProviderFactory aborting = new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals("Foo.java")) {
                        // what an interrupted blocking call does: the thread's flag is set, then it throws
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException("interrupted");
                    }
                    return "A summary.";
                };
            }
        };
        final GenerateEngine engine = new GenerateEngine(config, aborting);

        // act
        try {
            assertThrows(InterruptedIOException.class, engine::execute);
        } finally {
            // the test thread goes on, without the flag
            Thread.interrupted();
        }

        // assert: the record exists and holds what was done before the interrupt
        assertThat(Files.exists(recordFile), is(true));
        assertThat(new String(Files.readAllBytes(recordFile), StandardCharsets.UTF_8), containsString("Bar.java"));
    }

    @Test
    public void execute_anInterruptAsAClosedChannel_endsTheRun() throws Exception {
        // arrange: an interrupted NIO channel throws ClosedByInterruptException, no InterruptedIOException
        final SrcMorphConfiguration config = baseConfig();
        Files.write(
                tempDir.resolve("src/main/java/com/example/Zed.java"),
                "class Zed {}\n".getBytes(StandardCharsets.UTF_8));
        final GenerationProviderFactory interrupting = new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals("Foo.java")) {
                        Thread.currentThread().interrupt();
                        throw new ClosedByInterruptException();
                    }
                    return "A summary.";
                };
            }
        };
        final GenerateEngine engine = new GenerateEngine(config, interrupting);

        // act
        final boolean interrupted;
        try {
            assertThrows(ClosedByInterruptException.class, engine::execute);
        } finally {
            interrupted = Thread.interrupted();
        }

        // assert: the run ended at the interrupt; the file after Foo.java was not summarized
        assertThat(interrupted, is(true));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Zed.java.ai.md")), is(false));
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
        final GenerationProviderFactory timingOut = new GenerationProviderFactory() {
            @Override
            public GenerationProvider create(
                    final String providerName, final ProviderConfig providerConfig, final PromptCatalog promptSupport) {
                return request -> {
                    if (request.sourceFile().getFileName().toString().equals("Foo.java")) {
                        throw new SocketTimeoutException("Read timed out");
                    }
                    return "A summary.";
                };
            }
        };
        final GenerateEngine engine = new GenerateEngine(config, timingOut);

        // act
        final SrcMorphException failed = assertThrows(SrcMorphException.class, engine::execute);

        // assert: the run went on to the other file, and failed at its end naming the one
        assertThat(failed.getMessage(), containsString("1 file(s) were not written"));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Bar.java.ai.md")), is(true));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(false));
    }

    @Test
    public void execute_runRecordFile_recordsEveryRoutedFile() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path recordFile = tempDir.resolve("records/run.json");
        config.setRunRecordFile(recordFile.toFile());

        // act
        new GenerateEngine(config).execute();
        final JsonNode first = new ObjectMapper().readTree(recordFile.toFile());
        new GenerateEngine(config).execute();
        final JsonNode second = new ObjectMapper().readTree(recordFile.toFile());

        // assert
        final JsonNode file = first.get("files").get(0);
        assertThat(file.get("file").asText(), is("src/main/java/com/example/Foo.java"));
        assertThat(file.get("promptKey").asText(), is(CommonTestFixtures.PROMPT_KEY_FILE_BODY));
        assertThat(file.get("model").asText(), is(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT));
        assertThat(file.get("outcome").asText(), is("written"));
        assertThat(file.get("calls").asInt(), is(1));
        assertThat(file.get("completionTokens").asLong() > 0, is(true));
        assertThat(file.get("finishReason").asText(), is("stop"));
        assertThat(file.get("promptHash").asText().matches("[0-9A-F]{8}"), is(true));
        assertThat(first.get("generatorVersion").asText(), is("1.0.0"));
        // the second run found it up to date: no call, no tokens
        assertThat(second.get("files").get(0).get("outcome").asText(), is("unchanged"));
        assertThat(second.get("totals").get("calls").asInt(), is(0));
    }

    @Test
    public void execute_subtreesListedOutOfPathOrder_summarizesTheFilesInPathOrder() throws Exception {
        // arrange: the second subtree's file sorts before the first one's
        final SrcMorphConfiguration config = baseConfig();
        Files.createDirectories(tempDir.resolve("src/extra/java/com/example"));
        Files.write(
                tempDir.resolve("src/extra/java/com/example/Zed.java"),
                "class Zed {}\n".getBytes(StandardCharsets.UTF_8));
        config.setSubtrees(Arrays.asList("src/main/java", "src/extra/java"));
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());

        // act
        new GenerateEngine(config).execute();

        // assert: the whole plan in path order, not one subtree after the other
        final JsonNode files = new ObjectMapper().readTree(recordFile.toFile()).get("files");
        assertThat(files.size(), is(2));
        assertThat(files.get(0).get("file").asText(), is("src/extra/java/com/example/Zed.java"));
        assertThat(files.get(1).get("file").asText(), is("src/main/java/com/example/Foo.java"));
    }

    @Test
    public void execute_aChangedPromptWithItsSwitchOff_theRecordShowsThePromptThatWroteIt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.getRegenerateOn().setPrompt(false);
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final ObjectMapper json = new ObjectMapper();

        // act: write, change the prompt, run again (unchanged), then force
        new GenerateEngine(config).execute();
        final String written = json.readTree(recordFile.toFile())
                .get("files")
                .get(0)
                .get("promptHash")
                .asText();
        config.getPromptDefinitions().get(0).setTemplate("Another prompt.\n\nFile: %s\n\nSource:\n%s\n");
        new GenerateEngine(config).execute();
        final JsonNode unchanged =
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
    public void execute_aChangedPrompt_rewritesTheSummaryWithTheNewPrompt() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final Path recordFile = tempDir.resolve("run.json");
        config.setRunRecordFile(recordFile.toFile());
        final ObjectMapper json = new ObjectMapper();
        new GenerateEngine(config).execute();
        final String written = json.readTree(recordFile.toFile())
                .get("files")
                .get(0)
                .get("promptHash")
                .asText();

        // act
        config.getPromptDefinitions().get(0).setTemplate("Another prompt.\n\nFile: %s\n\nSource:\n%s\n");
        final GenerateResult result = new GenerateEngine(config).execute();
        final JsonNode rewritten =
                json.readTree(recordFile.toFile()).get("files").get(0);
        final GenerateResult again = new GenerateEngine(config).execute();

        // assert
        assertThat(result.written(), is(1));
        assertThat(rewritten.get("outcome").asText(), is("written"));
        assertThat(rewritten.get("promptHash").asText().equals(written), is(false));
        assertThat(again.written(), is(0));
        final String summary = new String(
                Files.readAllBytes(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")),
                StandardCharsets.UTF_8);
        assertThat(summary, containsString("- P: " + rewritten.get("promptHash").asText() + "\n"));
    }

    @Test
    public void execute_noRunRecordFile_writesNoRecord() throws Exception {
        // act
        new GenerateEngine(baseConfig()).execute();

        // assert
        try (Stream<Path> files = Files.walk(tempDir)) {
            assertThat(files.anyMatch(p -> p.toString().endsWith(".json")), is(false));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="factsHash">
    @Test
    public void factsHash_everyPartOfACounterChanged_changesTheHash() {
        // arrange
        final FactCounter counter = new FactCounter();
        counter.setLabel("tables");
        counter.setPattern("CREATE TABLE");

        // act
        final String plain = FactExtractor.factsHash(Collections.singletonList(counter));
        counter.setIgnore(true);
        final String ignored = FactExtractor.factsHash(Collections.singletonList(counter));
        counter.setIgnore(false);
        counter.setPattern("CREATE VIEW");
        final String otherPattern = FactExtractor.factsHash(Collections.singletonList(counter));
        counter.setLabel("views");
        final String otherLabel = FactExtractor.factsHash(Collections.singletonList(counter));
        counter.setRaw(true);
        final String raw = FactExtractor.factsHash(Collections.singletonList(counter));
        counter.setRaw(false);
        counter.setDistinct(true);
        final String distinct = FactExtractor.factsHash(Collections.singletonList(counter));

        // assert
        assertThat(plain.matches("[0-9A-F]{8}"), is(true));
        assertThat(new HashSet<>(Arrays.asList(plain, ignored, otherPattern, otherLabel, raw, distinct)).size(), is(6));
        // a counter without raw and distinct hashes as before they existed: its summaries stay current
        assertThat(plain, is(new ChecksumSupport().calculateCrc32Hex("tables\tCREATE TABLE\tfalse\n")));
        assertThat(FactExtractor.factsHash(null), is(""));
        assertThat(FactExtractor.factsHash(Collections.emptyList()), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="promptHash">
    private static PromptDefinition promptOf(final String key, final String template) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey(key);
        definition.setTemplate(template);
        return definition;
    }

    @Test
    public void promptHash_mapOrReducePromptOrNone_tellsThemApart() {
        // arrange
        final PromptCatalog prompts = new PromptCatalog(Arrays.asList(promptOf("body", "BODY"), promptOf("x", "X")));
        final RoutingRule plain = new RoutingRule();
        plain.setPromptKey("body");
        final RoutingRule mapped = plain.copy();
        mapped.setMapPromptKey("x");
        final RoutingRule reduced = plain.copy();
        reduced.setReducePromptKey("x");

        // act
        final String none = HeaderHashes.prompt(prompts, plain);
        final String map = HeaderHashes.prompt(prompts, mapped);
        final String reduce = HeaderHashes.prompt(prompts, reduced);

        // assert
        assertThat(map.equals(reduce), is(false));
        assertThat(map.equals(none), is(false));
        assertThat(reduce.equals(none), is(false));
        assertThat(none, is(new ChecksumSupport().calculateCrc32Hex("BODY")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="check">
    /**
     * Where check() differs from the plan: planOnly does not apply to it, so the missing model file the
     * plan-only run above passes fails the check, before any model loads.
     *
     * @throws IOException if the fixture cannot be built
     */
    @Test
    public void check_planOnlyAndAMissingModel_failsOnTheModel() throws IOException {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        config.setGenerationProvider(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI);
        config.setPlanOnly(true);
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException thrown = assertThrows(SrcMorphException.class, engine::check);

        // assert
        assertThat(thrown.getMessage(), containsString("mock.gguf"));
        assertThat(thrown.getMessage(), containsString("does not exist"));
    }

    /**
     * A check that passes writes nothing and reports a plan, also without planOnly.
     *
     * @throws Exception if the check fails
     */
    @Test
    public void check_aConfigurationInOrder_passesAndWritesNothing() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();

        // act
        final GenerateResult result = new GenerateEngine(config).check();

        // assert
        assertThat(result.planOnly(), is(true));
        assertThat(result.written(), is(0));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    @Test
    public void check_aMapPromptWithoutATemplate_failsBeforeAModelLoads() throws Exception {
        // arrange
        final SrcMorphConfiguration config = baseConfig();
        final RoutingRule rule = config.getRules().get(0);
        rule.setOnOversize("mapReduce");
        rule.setMapPromptKey("no-such-prompt");
        final GenerateEngine engine = new GenerateEngine(config);

        // act
        final SrcMorphException failed = assertThrows(SrcMorphException.class, engine::check);

        // assert
        assertThat(failed.getMessage(), containsString("no-such-prompt"));
    }
    // </editor-fold>
}

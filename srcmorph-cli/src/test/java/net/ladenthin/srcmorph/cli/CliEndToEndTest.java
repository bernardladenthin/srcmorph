// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end exercise of the {@link CCommand#All} command against the {@code mock} provider (no
 * model load, no forked {@code java -jar} process — {@link Main#run()} is invoked directly), proving
 * the CLI drives all three engines and the expected {@code .ai.md} tree lands on disk.
 */
public class CliEndToEndTest {

    @TempDir
    Path tempDir;

    private CConfiguration buildAllCommandConfiguration() throws IOException {
        final Path sourceRoot = tempDir.resolve("src/main/java");
        Files.createDirectories(sourceRoot.resolve("com/example"));
        Files.write(sourceRoot.resolve("com/example/Foo.java"), "class Foo {}\n".getBytes(StandardCharsets.UTF_8));

        final CConfiguration configuration = new CConfiguration();
        configuration.command = CCommand.All;
        configuration.srcMorph.setBaseDirectory(tempDir.toFile());
        configuration.srcMorph.setOutputDirectory(tempDir.resolve("out").toFile());
        configuration.srcMorph.setGenerationProvider("mock");
        configuration.srcMorph.setPluginVersion("1.0.0-test");
        configuration.srcMorph.setAiVersion("0.0.0");

        final AiPromptDefinition prompt = new AiPromptDefinition();
        prompt.setKey("file-body");
        prompt.setTemplate("Summarize:\n%s");
        configuration.srcMorph.setPromptDefinitions(Collections.singletonList(prompt));

        final AiModelDefinition model = new AiModelDefinition();
        model.setKey("mock-model");
        model.setModelPath("mock.gguf");
        // Disable automatic maxInputChars calculation so the tiny test source never trips the
        // oversize path (mirrors GenerateEngineTest's own mock model definition).
        model.setCharsPerToken(0);
        configuration.srcMorph.setAiDefinitions(Collections.singletonList(model));

        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey("file-body");
        rule.setAiDefinitionKey("mock-model");
        rule.setFallback(true);
        configuration.srcMorph.setFieldGenerations(Collections.singletonList(rule));

        return configuration;
    }

    @Test
    public void run_allCommand_writesFileAndPackageAndProjectIndex() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();

        new Main(configuration).run();

        final Path outputRoot = tempDir.resolve("out");
        // Phase 1: SourceFileIndexer relativises against the "src" root but keeps "main/java/...".
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/Foo.java.ai.md")), is(true));
        // Phase 2: one package.ai.md per directory from the output root down to the leaf package.
        assertThat(
                Files.exists(outputRoot.resolve("main/java/com/example/" + AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)),
                is(true));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)), is(true));
        // Phase 3: the single project-level index.
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(true));
    }

    /**
     * The prompt comes from a file below {@code profileDirectory}, as every shipped example's does: the
     * run reads it before the first file and completes.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_allCommand_readsThePromptFromItsTemplateFile() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        final Path profileDirectory = tempDir.resolve("prompts");
        Files.createDirectories(profileDirectory.resolve("v1"));
        Files.write(
                profileDirectory.resolve("v1/file-body.md"),
                "You are a code indexer.\n".getBytes(StandardCharsets.UTF_8));
        final AiPromptDefinition prompt = new AiPromptDefinition();
        prompt.setKey("file-body");
        prompt.setTemplateFile("v1/file-body.md");
        configuration.srcMorph.setPromptDefinitions(Collections.singletonList(prompt));
        configuration.srcMorph.setProfileDirectory(profileDirectory.toFile());

        new Main(configuration).run();

        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
    }

    /**
     * An absolute path is always possible (the {@code absolute-path} requirement of the core's
     * {@code AbsolutePathRequirement}): an absolute {@code templateFile} is read as it is, with no
     * {@code profileDirectory} and with one pointing somewhere else.
     *
     * @throws Exception if the run fails
     */
    @Test
    @Tag("absolute-path")
    public void run_allCommand_absoluteTemplateFile_isReadWithOrWithoutAProfileDirectory() throws Exception {
        final Path absolute = Files.write(
                Files.createDirectories(tempDir.resolve("shared-prompts")).resolve("file-body.md"),
                "You are a code indexer.\n".getBytes(StandardCharsets.UTF_8));
        final Path unrelatedDirectory = tempDir.resolve("unrelated");
        for (final boolean withDirectory : new boolean[] {false, true}) {
            final CConfiguration configuration = buildAllCommandConfiguration();
            final AiPromptDefinition prompt = new AiPromptDefinition();
            prompt.setKey("file-body");
            prompt.setTemplateFile(absolute.toAbsolutePath().toString());
            configuration.srcMorph.setPromptDefinitions(Collections.singletonList(prompt));
            configuration.srcMorph.setProfileDirectory(withDirectory ? unrelatedDirectory.toFile() : null);
            configuration.srcMorph.setForce(true);

            new Main(configuration).run();

            assertThat(
                    "profileDirectory " + (withDirectory ? unrelatedDirectory : "unset"),
                    Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")),
                    is(true));
        }
    }

    /**
     * A missing prompt file stops the run before anything is written, naming the file.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void run_allCommand_missingTemplateFile_failsWithItsPath() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        final AiPromptDefinition prompt = new AiPromptDefinition();
        prompt.setKey("file-body");
        prompt.setTemplateFile("v1/missing.md");
        configuration.srcMorph.setPromptDefinitions(Collections.singletonList(prompt));
        configuration.srcMorph.setProfileDirectory(tempDir.resolve("prompts").toFile());

        final IllegalStateException thrown =
                assertThrows(IllegalStateException.class, () -> new Main(configuration).run());

        assertThat(String.valueOf(thrown.getCause()), containsString("missing.md cannot be read"));
        assertThat(Files.exists(tempDir.resolve("out")), is(false));
    }

    /** Every message of {@code thrown} and its causes, so a test does not depend on which layer wraps. */
    private static String causeChain(final Throwable thrown) {
        final StringBuilder chain = new StringBuilder();
        for (Throwable cause = thrown; cause != null; cause = cause.getCause()) {
            chain.append(cause).append('\n');
        }
        return chain.toString();
    }

    private static AiFieldGenerationConfig phaseRule(final String promptKey) {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey(promptKey);
        rule.setAiDefinitionKey("mock-model");
        return rule;
    }

    private static AiPromptDefinition inlinePrompt(final String key) {
        final AiPromptDefinition prompt = new AiPromptDefinition();
        prompt.setKey(key);
        prompt.setTemplate("Summarize.");
        return prompt;
    }

    /**
     * As the Maven plugin's aggregate executions do, the package and the project phase run with
     * rules of their own: here prompts the file phase's rule never names. The run completes, and the
     * shared configuration keeps the file phase's rules.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_allCommand_withPhaseRules_runsEachPhaseWithItsOwnRules() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.srcMorph.setPromptDefinitions(
                Arrays.asList(inlinePrompt("file-body"), inlinePrompt("package-body"), inlinePrompt("project-body")));
        final List<AiFieldGenerationConfig> fileRules = configuration.srcMorph.getFieldGenerations();
        configuration.packageFieldGenerations = Collections.singletonList(phaseRule("package-body"));
        configuration.projectFieldGenerations = Collections.singletonList(phaseRule("project-body"));

        new Main(configuration).run();

        final Path outputRoot = tempDir.resolve("out");
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)), is(true));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(true));
        assertThat(configuration.srcMorph.getFieldGenerations() == fileRules, is(true));
    }

    /**
     * The package phase really takes its own rule: one naming a prompt nobody defined stops it, while
     * the file phase before it, on the shared rules, completed.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void run_allCommand_packageRuleWithAnUnknownPrompt_failsInThePackagePhase() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.packageFieldGenerations = Collections.singletonList(phaseRule("no-such-prompt"));

        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> new Main(configuration).run());

        assertThat(causeChain(thrown), containsString("no-such-prompt"));
        assertThat(Files.exists(tempDir.resolve("out/main/java/com/example/Foo.java.ai.md")), is(true));
    }

    /**
     * The project phase likewise; {@link CCommand#AggregateProject} alone takes its rule too.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void run_aggregateProjectCommand_projectRuleWithAnUnknownPrompt_fails() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.command = CCommand.All;
        new Main(configuration).run();
        configuration.command = CCommand.AggregateProject;
        configuration.projectFieldGenerations = Collections.singletonList(phaseRule("no-such-prompt"));

        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> new Main(configuration).run());

        assertThat(causeChain(thrown), containsString("no-such-prompt"));
    }

    /**
     * And {@link CCommand#AggregatePackages} alone takes the package rule.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void run_aggregatePackagesCommand_packageRuleWithAnUnknownPrompt_fails() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.command = CCommand.GenerateFileIndex;
        new Main(configuration).run();
        configuration.command = CCommand.AggregatePackages;
        configuration.packageFieldGenerations = Collections.singletonList(phaseRule("no-such-prompt"));

        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> new Main(configuration).run());

        assertThat(causeChain(thrown), containsString("no-such-prompt"));
    }

    @Test
    public void run_planCommand_forcesPlanOnlyAndWritesNothing() throws Exception {
        final CConfiguration configuration = buildAllCommandConfiguration();
        // The file explicitly asks NOT to plan-only; Plan must force it anyway.
        configuration.command = CCommand.Plan;
        configuration.srcMorph.setPlanOnly(false);

        new Main(configuration).run();

        assertThat(Files.exists(tempDir.resolve("out")), is(false));
        // The original configuration object handed to Main must not have been mutated in place.
        assertThat(configuration.srcMorph.isPlanOnly(), is(false));
    }

    // <editor-fold defaultstate="collapsed" desc="each CCommand dispatch arm">

    /**
     * Runs one command against the shared mock fixture and returns the output root.
     *
     * @param command the command to dispatch
     * @return the output directory the run wrote into
     * @throws IOException if the fixture cannot be built
     */
    private Path runCommand(final CCommand command) throws IOException {
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.command = command;
        new Main(configuration).run();
        return tempDir.resolve("out");
    }

    /**
     * Runs {@code GenerateFileIndex} and then {@code command} over the same fixture.
     *
     * <p>The aggregation commands summarise an existing {@code .ai.md} tree, so running one against an
     * empty output directory legitimately writes nothing -- each has to follow the phases it reads,
     * the way {@code All} sequences them. Verified, not assumed: both aggregation tests failed exactly
     * this way first, and {@code AggregateProject} needed the package phase too, not just the file
     * phase, because it lists the {@code package.ai.md} files.
     *
     * @param command   the aggregation command whose own output is under test
     * @param preceding the phases it depends on, dispatched in order first
     * @return the output directory the runs wrote into
     * @throws IOException if the fixture cannot be built
     */
    private Path runAfter(final CCommand command, final CCommand... preceding) throws IOException {
        final CConfiguration configuration = buildAllCommandConfiguration();
        for (final CCommand phase : preceding) {
            configuration.command = phase;
            new Main(configuration).run();
        }
        configuration.command = command;
        new Main(configuration).run();
        return tempDir.resolve("out");
    }

    /**
     * Only {@code Plan} and {@code All} were ever dispatched by a test, leaving four arms of the
     * command switch dead. A copy-paste slip -- {@code case AggregatePackages:} constructing
     * {@code AggregateProjectEngine}, say -- would produce the wrong artifacts with nothing failing.
     * Each of the four tests below therefore asserts both what its arm DOES write and what it does
     * NOT, so a swapped engine is caught in either direction.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_generateFileIndexCommand_writesTheFileIndexButNoProjectIndex() throws Exception {
        // act
        final Path outputRoot = runCommand(CCommand.GenerateFileIndex);

        // assert
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/Foo.java.ai.md")), is(true));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(false));
    }

    /**
     * Package aggregation writes the per-directory index, not the project one.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_aggregatePackagesCommand_writesPackageIndexButNoProjectIndex() throws Exception {
        // act
        final Path outputRoot = runAfter(CCommand.AggregatePackages, CCommand.GenerateFileIndex);

        // assert
        assertThat(
                Files.exists(outputRoot.resolve("main/java/com/example/" + AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)),
                is(true));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(false));
    }

    /**
     * Project aggregation writes the project index at the output root.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_aggregateProjectCommand_writesTheProjectIndex() throws Exception {
        // act
        final Path outputRoot =
                runAfter(CCommand.AggregateProject, CCommand.GenerateFileIndex, CCommand.AggregatePackages);

        // assert
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(true));
    }

    /**
     * {@code Calibrate} is cheap and safe to dispatch here: the mock provider reports synthetic
     * throughput, so no GGUF and no native library are involved. Without this the arm is never
     * entered at all.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_calibrateCommand_completesAgainstTheMockProvider() throws Exception {
        // arrange
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.command = CCommand.Calibrate;

        // act / assert -- calibrate measures and reports; it writes no .ai.md tree
        new Main(configuration).run();
        assertThat(Files.exists(tempDir.resolve("out").resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(false));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Calibrate writes to stdout, not the log">

    /**
     * The {@code Calibrate} arm deliberately uses {@code System.out} rather than the logger: the
     * rendered block is a paste-ready {@code <calibration>} snippet the user copies into their POM,
     * so it must not carry a logger's timestamp/level prefix. That distinction is the whole point of
     * the line, and nothing asserted it — removing the {@code println} left every test green.
     *
     * @throws Exception if the run fails
     */
    @Test
    public void run_calibrateCommand_printsThePasteReadyBlockToStdoutRatherThanTheLog() throws Exception {
        // arrange
        final CConfiguration configuration = buildAllCommandConfiguration();
        configuration.command = CCommand.Calibrate;
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream captured = new ByteArrayOutputStream();

        // act
        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
            new Main(configuration).run();
        } finally {
            System.setOut(originalOut);
        }

        // assert -- the block reached stdout, and it is the real report, not an empty render
        final String stdout = captured.toString(StandardCharsets.UTF_8.name());
        assertThat(stdout.contains("<calibration>"), is(true));
        assertThat(stdout.contains("</calibration>"), is(true));
        assertThat(stdout.contains("mock-model"), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ListModels">

    /** A model list as the GitHub Copilot API Gateway sends it, cut to two models and an alias. */
    private static final String GATEWAY_MODELS = "{\"object\":\"list\",\"data\":["
            + "{\"id\":\"grok-4.7\",\"object\":\"model\",\"owned_by\":\"copilot\",\"max_input_tokens\":424794},"
            + "{\"id\":\"claude-haiku-4.5\",\"object\":\"model\",\"owned_by\":\"copilot\",\"max_input_tokens\":127790},"
            + "{\"id\":\"copilot-grok\",\"object\":\"model\",\"aliased_model_id\":\"grok-4.7\","
            + "\"max_input_tokens\":424794}]}";

    /**
     * {@code ListModels} asks the configured server for its models and prints, to stdout like
     * {@code Calibrate}, an {@code aiDefinitions} block that binds back into a configuration under
     * the CLI's strict mapper: the template is a configuration, not just text that looks like one.
     *
     * @throws Exception if the stub cannot start or the run fails
     */
    @Test
    public void run_listModelsCommand_printsATemplateThatBindsBackIntoAConfiguration() throws Exception {
        // arrange -- a loopback stub of the gateway's model list
        final HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/v1/models", exchange -> {
            final byte[] bytes = GATEWAY_MODELS.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        server.start();
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            final String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1";
            final CConfiguration configuration = Main.fromYaml("command: ListModels\n"
                    + "modelFilter: GROK\n"
                    + "srcMorph:\n"
                    + "  aiDefinitions:\n"
                    + "    - key: copilot\n"
                    + "      provider: openai-compatible\n"
                    + "      baseUrl: \"" + baseUrl + "\"\n"
                    + "      stream: true\n"
                    + "      requestsPerMinute: 30\n"
                    + "      contextSize: 120000\n"
                    + "      maxOutputTokens: 2048\n"
                    + "      temperature: 0.2\n");

            // act
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
            new Main(configuration).run();
        } finally {
            System.setOut(originalOut);
            server.stop(0);
        }

        // assert -- the filter kept one model, and the printed block is a configuration
        final String stdout = captured.toString(StandardCharsets.UTF_8.name());
        assertThat(stdout.contains("2 models, 1 matching 'GROK'"), is(true));
        final StringBuilder indented = new StringBuilder("srcMorph:\n");
        for (final String line : stdout.split("\n", -1)) {
            indented.append("  ").append(line).append('\n');
        }
        final SrcMorphConfiguration template = Main.fromYaml(indented.toString()).srcMorph;
        assertThat(template.getAiDefinitions().size(), is(1));
        final AiModelDefinition entry = template.getAiDefinitions().get(0);
        assertThat(entry.getKey(), is("copilot-grok-4.7"));
        assertThat(entry.getProvider(), is("openai-compatible"));
        assertThat(entry.getRemoteModel(), is("grok-4.7"));
        assertThat(entry.isStream(), is(true));
        assertThat(entry.getRequestsPerMinute(), is(30));
        assertThat(entry.getContextSize(), is(120000));
        assertThat(entry.getMaxOutputTokens(), is(2048));
        assertThat(entry.getTemperature(), is(0.2f));
    }

    // </editor-fold>
}

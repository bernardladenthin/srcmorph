// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
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
}

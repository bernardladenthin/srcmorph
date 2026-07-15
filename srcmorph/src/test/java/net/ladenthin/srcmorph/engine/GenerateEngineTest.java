// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiCondition;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
}

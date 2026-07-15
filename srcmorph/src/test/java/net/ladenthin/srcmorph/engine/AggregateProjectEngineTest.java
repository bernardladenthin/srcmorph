// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AggregateProjectEngineTest {

    @TempDir
    Path tempDir;

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock
     * provider, the engine always builds a
     * {@link net.ladenthin.srcmorph.provider.LlamaCppJniConfig} value object, which requires a
     * non-null model path.
     */
    private static AiModelDefinition mockModelDefinition() {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        definition.setModelPath("mock.gguf");
        definition.setCharsPerToken(0);
        return definition;
    }

    private void writePackageFile(final Path packageFile, final String title, final String body) throws Exception {
        Files.createDirectories(packageFile.getParent());
        final AiMdHeader header = new AiMdHeader(
                title,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE);
        documentCodec.write(packageFile, new AiMdDocument(header, body));
    }

    @Test
    public void execute_missingOutputDirectoryReturnsZero() throws Exception {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setOutputDirectory(tempDir.resolve("out-does-not-exist").toFile());

        assertThat(new AggregateProjectEngine(config).execute(), is(0));
    }

    @Test
    public void execute_deterministicNoOverview_writesProjectIndex() throws Exception {
        final Path outputRoot = tempDir.resolve("ai");
        writePackageFile(
                outputRoot.resolve("com/example/package.ai.md"), "com/example", "> Handles the example domain.\n");

        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setOutputDirectory(outputRoot.toFile());
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setProjectName("my-project");

        final int written = new AggregateProjectEngine(config).execute();

        assertThat(written, is(equalTo(1)));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        assertThat(Files.exists(projectFile), is(true));
        final AiMdDocument document = documentCodec.read(projectFile);
        assertThat(document.header().title(), is(equalTo("my-project")));
        assertThat(document.body(), containsString("Handles the example domain."));
    }

    @Test
    public void execute_blankProjectNameFallsBackToDefaultTitle() throws Exception {
        final Path outputRoot = tempDir.resolve("ai");
        writePackageFile(outputRoot.resolve("com/example/package.ai.md"), "com/example", "> Lead.\n");

        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setOutputDirectory(outputRoot.toFile());
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setProjectName("   ");

        new AggregateProjectEngine(config).execute();

        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.header().title(), is(equalTo("project")));
    }

    @Test
    public void execute_withOverviewFieldGeneration_generatesOverviewSection() throws Exception {
        final Path outputRoot = tempDir.resolve("ai");
        writePackageFile(outputRoot.resolve("com/example/package.ai.md"), "com/example", "> Handles billing.\n");

        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setOutputDirectory(outputRoot.toFile());
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setProjectName("my-project");
        config.setGenerationProvider("mock");
        config.setPromptDefinitions(CommonTestFixtures.createPackagePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        final AiFieldGenerationConfig overview = new AiFieldGenerationConfig();
        overview.setPromptKey(CommonTestFixtures.PROMPT_KEY_PACKAGE_BODY);
        overview.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        config.setFieldGenerations(Collections.singletonList(overview));

        new AggregateProjectEngine(config).execute();

        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), containsString("#### Overview"));
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AggregatePackagesEngineTest {

    @TempDir
    Path tempDir;

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    /**
     * A model definition with a (dummy, never loaded) {@code modelPath} set: even with the mock
     * provider, the engine always builds a
     * {@link net.ladenthin.srcmorph.provider.ProviderConfig} value object, which requires a
     * non-null model path.
     */
    private static AiModelDefinition mockModelDefinition() {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        definition.setModelPath("mock.gguf");
        definition.setCharsPerToken(0);
        return definition;
    }

    @Test
    public void execute_missingOutputDirectoryReturnsZeroWithoutError() throws Exception {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(tempDir.resolve("out-does-not-exist").toFile());
        config.setGenerationProvider("mock");
        config.setFieldGenerations(CommonTestFixtures.createPackageFieldGenerations());

        final int aggregated = new AggregatePackagesEngine(config).execute();

        assertThat(aggregated, is(0));
    }

    @Test
    public void execute_missingOutputDirectory_stillFailsAMissingRule() {
        // the rule is checked first, so a fresh tree does not hide a wrong configuration
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(tempDir.resolve("out-does-not-exist").toFile());
        config.setGenerationProvider("mock");

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new AggregatePackagesEngine(config).execute());

        assertThat(thrown.getMessage(), org.hamcrest.CoreMatchers.containsString("takes exactly one rule"));
    }

    @Test
    public void check_aRuleThatSamplesWithoutMaxChunks_isRefused() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(tempDir.resolve("out").toFile());
        config.setGenerationProvider("mock");
        config.setFieldGenerations(CommonTestFixtures.createPackageFieldGenerations());
        config.getFieldGenerations().get(0).setOnOversize("sampleChunks");

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new AggregatePackagesEngine(config).check());

        assertThat(thrown.getMessage(), org.hamcrest.CoreMatchers.containsString("Invalid rule of aggregate-packages"));
    }

    @Test
    public void execute_aggregatesOneChildIntoPackageAiMd() throws Exception {
        final Path outputRoot = tempDir.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("com/example");
        final Path childAiFile = packageDirectory.resolve("Test.java.ai.md");
        Files.createDirectories(packageDirectory);

        final AiMdHeader childHeader = new AiMdHeader(
                "Test.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        documentCodec.write(childAiFile, new AiMdDocument(childHeader, ""));

        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(outputRoot.toFile());
        config.setGenerationProvider("mock");
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setPromptDefinitions(CommonTestFixtures.createPackagePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        config.setFieldGenerations(CommonTestFixtures.createPackageFieldGenerations());

        final int aggregated = new AggregatePackagesEngine(config).execute();

        // One package.ai.md per directory from the output root down to the child's own package
        // (root "ai/", "com/", and "com/example/"), matching PackageIndexer's recursive aggregation.
        assertThat(aggregated, is(equalTo(3)));
        assertThat(Files.exists(packageDirectory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)), is(true));
    }

    @Test
    public void execute_aPackageOverItsWindow_isMapReducedByDefault() throws Exception {
        // arrange: forty child summaries of 2 000 characters each, a model with a window of a few thousand
        final Path outputRoot = tempDir.resolve("ai");
        final Path packageDirectory = Files.createDirectories(outputRoot.resolve("com/example"));
        final StringBuilder body = new StringBuilder("> A child.\n\n");
        while (body.length() < 2_000) {
            body.append("- a detail of this child file that the package summary has to read\n");
        }
        for (int i = 0; i < 40; i++) {
            final AiMdHeader header = new AiMdHeader(
                    "Child" + i + ".java",
                    AiMdHeaderCodec.HEADER_VERSION_1_0,
                    "AAAAAAAA",
                    "2026-03-16T00:00:00Z",
                    "2026-03-16T00:00:10Z",
                    "1.0.0",
                    "0.0.0",
                    AiMdHeaderCodec.NODE_TYPE_FILE);
            documentCodec.write(
                    packageDirectory.resolve("Child" + i + ".java.ai.md"), new AiMdDocument(header, body.toString()));
        }
        final AiModelDefinition smallWindow = mockModelDefinition();
        smallWindow.setContextSize(2048);
        smallWindow.setCharsPerToken(4);
        smallWindow.setMaxOutputTokens(64);
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(outputRoot.toFile());
        config.setGenerationProvider("mock");
        config.setPluginVersion("1.0.0");
        config.setAiVersion("0.0.0");
        config.setPromptDefinitions(CommonTestFixtures.createPackagePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(smallWindow));
        config.setFieldGenerations(CommonTestFixtures.createPackageFieldGenerations());

        // act
        new AggregatePackagesEngine(config).execute();

        // assert: the package summary read every chunk of its input, and says so
        final String written = new String(
                Files.readAllBytes(packageDirectory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)),
                java.nio.charset.StandardCharsets.UTF_8);
        assertThat(written, containsString("**Coverage:** "));
        assertThat(written.contains("(sampled"), is(false));
    }

    @Test
    public void execute_twoRules_areRefusedBeforeAnyPackageIsWritten() throws Exception {
        final Path outputRoot = tempDir.resolve("ai");
        final Path packageDirectory = Files.createDirectories(outputRoot.resolve("com/example"));
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(outputRoot.toFile());
        config.setGenerationProvider("mock");
        config.setPromptDefinitions(CommonTestFixtures.createPackagePromptDefinitions());
        config.setAiDefinitions(Collections.singletonList(mockModelDefinition()));
        // the file rules of a run used to reach this phase too: every package was generated once per rule
        config.setFieldGenerations(Arrays.asList(
                CommonTestFixtures.createPackageFieldGeneration(), CommonTestFixtures.createPackageFieldGeneration()));

        final SrcMorphException thrown =
                assertThrows(SrcMorphException.class, () -> new AggregatePackagesEngine(config).execute());

        assertThat(thrown.getMessage(), containsString("aggregate-packages takes exactly one rule"));
        assertThat(Files.exists(packageDirectory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME)), is(false));
    }
}

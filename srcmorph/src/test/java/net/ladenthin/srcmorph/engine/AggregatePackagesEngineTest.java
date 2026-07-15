// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
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

    @Test
    public void execute_missingOutputDirectoryReturnsZeroWithoutError() throws Exception {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(tempDir.toFile());
        config.setOutputDirectory(tempDir.resolve("out-does-not-exist").toFile());
        config.setGenerationProvider("mock");

        final int aggregated = new AggregatePackagesEngine(config).execute();

        assertThat(aggregated, is(0));
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
}

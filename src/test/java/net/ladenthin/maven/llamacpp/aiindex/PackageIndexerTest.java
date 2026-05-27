// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;

public class PackageIndexerTest {

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    // <editor-fold defaultstate="collapsed" desc="aggregate">
    @Test
    public void aggregate_singleChildFile_createsPackageAiMdFile() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path baseDirectory = temp;
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/java/com/example");
        final Path childAiFile = packageDirectory.resolve("Test.java.ai.md");
        final Path packageAiFile = packageDirectory.resolve("package.ai.md");

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

        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                new SystemStreamLog(),
                baseDirectory,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new MockAiGenerationProvider(),
                CommonTestFixtures.createPackageFieldGenerations(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final int aggregated = indexer.aggregate(outputRoot);

        // pre-assert
        assertThat(Files.exists(packageAiFile), is(true));

        // assert
        assertThat(aggregated, is(equalTo(5)));

        final AiMdDocument document = documentCodec.read(packageAiFile);

        // pre-assert
        assertThat(document, is(notNullValue()));

        // assert
        assertThat(document.header().title(), is(equalTo("main/java/com/example")));
        assertThat(document.header().h(), is(equalTo(AiMdHeaderCodec.HEADER_VERSION_1_0)));
        assertThat(document.header().x(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_PACKAGE)));
        assertThat(document.header().g(), is(equalTo("1.0.0")));
        assertThat(document.header().a(), is(equalTo("0.0.0")));
        assertThat(document.header().c().trim().isEmpty(), is(false));
        assertThat(document.header().d().trim().isEmpty(), is(false));
        assertThat(document.header().t().trim().isEmpty(), is(false));
        assertThat(document.body().trim().isEmpty(), is(false));
    }
    // </editor-fold>
}

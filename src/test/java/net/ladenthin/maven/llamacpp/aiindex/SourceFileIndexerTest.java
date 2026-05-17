// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class SourceFileIndexerTest {

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    // <editor-fold defaultstate="collapsed" desc="indexSourceRoot">
    @Test
    public void indexSourceRoot_singleJavaFile_createsAiMdFile() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path baseDirectory = temp;
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path sourceRoot = temp.resolve("src/main/java");
        final Path sourceFile = sourceRoot.resolve("com/example/Test.java");
        final Path aiFile = outputRoot.resolve("main/java/com/example/Test.java.ai.md");

        Files.createDirectories(sourceFile.getParent());
        Files.write(sourceFile, ("package com.example;\n" +
                                      "\n" +
                                      "public class Test {\n" +
                                      "    public String hello(final String name) {\n" +
                                      "        return \"Hello \" + name;\n" +
                                      "    }\n" +
                                      "}\n").getBytes(StandardCharsets.UTF_8));

        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final SourceFileIndexer indexer = new SourceFileIndexer(
                new SystemStreamLog(), baseDirectory, outputRoot,
                Arrays.asList(".java"), "1.0.0", "0.0.0", Collections.<Path>emptyList(), false,
                new MockAiGenerationProvider(),
                CommonTestFixtures.createFileFieldGenerations(), promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport()
        );

        // act
        final int indexed = indexer.indexSourceRoot(sourceRoot);

        // pre-assert
        assertThat(Files.exists(aiFile), is(true));

        // assert
        assertThat(indexed, is(equalTo(1)));

        final AiMdDocument document = documentCodec.read(aiFile);

        // pre-assert
        assertThat(document, is(notNullValue()));

        // assert
        assertThat(document.header().title(), is(equalTo("Test.java")));
        assertThat(document.header().h(), is(equalTo(AiMdHeaderCodec.HEADER_VERSION_1_0)));
        assertThat(document.header().x(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_FILE)));
        assertThat(document.header().g(), is(equalTo("1.0.0")));
        assertThat(document.header().a(), is(equalTo("0.0.0")));
        assertThat(document.header().c().trim().isEmpty(), is(false));
        assertThat(document.header().d().trim().isEmpty(), is(false));
        assertThat(document.header().t().trim().isEmpty(), is(false));
        assertThat(document.body().trim().isEmpty(), is(false));
    }
    // </editor-fold>
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.maven.llamacpp.aiindex.CommonTestFixtures;
import net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider;
import net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;

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
        Files.write(
                sourceFile,
                ("package com.example;\n" + "\n"
                                + "public class Test {\n"
                                + "    public String hello(final String name) {\n"
                                + "        return \"Hello \" + name;\n"
                                + "    }\n"
                                + "}\n")
                        .getBytes(StandardCharsets.UTF_8));

        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final SourceFileIndexer indexer = new SourceFileIndexer(
                new SystemStreamLog(),
                baseDirectory,
                outputRoot,
                Arrays.asList(".java"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                Collections.<String>emptyList(),
                false,
                new MockAiGenerationProvider(),
                CommonTestFixtures.createFileFieldGenerations(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

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

    // <editor-fold defaultstate="collapsed" desc="extension-based prompt selection">
    @Test
    public void indexSourceRoot_selectsPromptByFileExtension() throws Exception {
        // arrange: a .java source; config maps .java -> java prompt, .sql -> sql, plus a fallback
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path sourceRoot = temp.resolve("src/main/java");
        Files.createDirectories(sourceRoot.resolve("com/example"));
        Files.write(
                sourceRoot.resolve("com/example/Foo.java"),
                "package com.example;\nclass Foo {}\n".getBytes(StandardCharsets.UTF_8));

        final AiPromptSupport promptSupport = new AiPromptSupport(Arrays.asList(
                promptDefinition("file-body-java"),
                promptDefinition("file-body-sql"),
                promptDefinition("file-body-fallback")));
        final CapturingProvider provider = new CapturingProvider();
        final SourceFileIndexer indexer = new SourceFileIndexer(
                new SystemStreamLog(),
                temp,
                outputRoot,
                Arrays.asList(".java"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                Collections.<String>emptyList(),
                false,
                provider,
                Arrays.asList(
                        fieldGeneration("file-body-java", Arrays.asList(".java")),
                        fieldGeneration("file-body-sql", Arrays.asList(".sql")),
                        fieldGeneration("file-body-fallback", null)),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.indexSourceRoot(sourceRoot);

        // assert: only the java prompt ran for the .java file
        assertThat(provider.promptKeys(), is(equalTo(Arrays.asList("file-body-java"))));
    }

    @Test
    public void indexSourceRoot_noMatchingExtensionAndNoFallback_throws() throws Exception {
        // arrange: a .java file, but the only field generation targets .sql with no fallback
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path sourceRoot = temp.resolve("src/main/java");
        Files.createDirectories(sourceRoot.resolve("com/example"));
        Files.write(
                sourceRoot.resolve("com/example/Foo.java"),
                "package com.example;\nclass Foo {}\n".getBytes(StandardCharsets.UTF_8));

        final AiPromptSupport promptSupport =
                new AiPromptSupport(Collections.singletonList(promptDefinition("file-body-sql")));
        final SourceFileIndexer indexer = new SourceFileIndexer(
                new SystemStreamLog(),
                temp,
                outputRoot,
                Arrays.asList(".java"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                Collections.<String>emptyList(),
                false,
                new MockAiGenerationProvider(),
                Collections.singletonList(fieldGeneration("file-body-sql", Arrays.asList(".sql"))),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act + assert
        assertThrows(IllegalArgumentException.class, () -> indexer.indexSourceRoot(sourceRoot));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="exclude filter">
    @Test
    public void indexSourceRoot_excludedFileIsSkipped() throws Exception {
        // arrange: two .java files; exclude package-info.java by glob
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path sourceRoot = temp.resolve("src/main/java");
        Files.createDirectories(sourceRoot.resolve("com/example"));
        Files.write(
                sourceRoot.resolve("com/example/Foo.java"),
                "package com.example;\nclass Foo {}\n".getBytes(StandardCharsets.UTF_8));
        Files.write(
                sourceRoot.resolve("com/example/package-info.java"),
                "package com.example;\n".getBytes(StandardCharsets.UTF_8));

        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final SourceFileIndexer indexer = new SourceFileIndexer(
                new SystemStreamLog(),
                temp,
                outputRoot,
                Arrays.asList(".java"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                Arrays.asList("**/package-info.java"),
                false,
                new MockAiGenerationProvider(),
                CommonTestFixtures.createFileFieldGenerations(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final int indexed = indexer.indexSourceRoot(sourceRoot);

        // assert: only Foo.java was indexed; package-info.java was excluded
        assertThat(indexed, is(equalTo(1)));
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/Foo.java.ai.md")), is(true));
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/package-info.java.ai.md")), is(false));
    }
    // </editor-fold>

    private static AiPromptDefinition promptDefinition(final String key) {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey(key);
        definition.setTemplate("Summarize.\nFile: %s\nSource:\n%s\n");
        return definition;
    }

    private static AiFieldGenerationConfig fieldGeneration(final String promptKey, final List<String> extensions) {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setPromptKey(promptKey);
        config.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        config.setFileExtensions(extensions);
        return config;
    }

    /** Test provider that records the prompt key of each request so prompt selection can be asserted. */
    private static final class CapturingProvider implements AiGenerationProvider {
        private final List<String> promptKeys = new ArrayList<>();

        @Override
        public String generate(final AiGenerationRequest request) {
            promptKeys.add(request.promptKey());
            return "body for " + request.promptKey();
        }

        List<String> promptKeys() {
            return promptKeys;
        }
    }
}

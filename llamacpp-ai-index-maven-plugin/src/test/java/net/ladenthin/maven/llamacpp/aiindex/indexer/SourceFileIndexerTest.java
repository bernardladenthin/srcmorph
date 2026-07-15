// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.maven.llamacpp.aiindex.CommonTestFixtures;
import net.ladenthin.maven.llamacpp.aiindex.config.AiCondition;
import net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider;
import org.junit.jupiter.api.Test;

public class SourceFileIndexerTest {

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    private SourceFileIndexer indexer(
            final Path baseDirectory,
            final Path outputRoot,
            final List<String> excludes,
            final long minSizeBytes,
            final long maxSizeBytes) {
        return new SourceFileIndexer(
                baseDirectory,
                outputRoot,
                Arrays.asList(".java"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                excludes,
                minSizeBytes,
                maxSizeBytes,
                false);
    }

    private AiFieldGenerationSupport mockSupport() {
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        return new AiFieldGenerationSupport(
                new MockAiGenerationProvider(),
                new AiPromptPreparationSupport(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
    }

    private static void writeJava(final Path file, final String body) throws Exception {
        Files.createDirectories(file.getParent());
        Files.write(file, body.getBytes(StandardCharsets.UTF_8));
    }

    /** Writes a {@code .java} file padded with a line comment to exactly {@code totalBytes} bytes. */
    private static void writeSizedJava(final Path file, final int totalBytes) throws Exception {
        Files.createDirectories(file.getParent());
        final StringBuilder sb = new StringBuilder(totalBytes);
        sb.append("// ");
        while (sb.length() < totalBytes) {
            sb.append('x');
        }
        sb.setLength(totalBytes);
        Files.write(file, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static AiFieldGenerationConfig rule(
            final String promptKey, final List<String> extensions, final boolean skip, final boolean fallback) {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setPromptKey(promptKey);
        config.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        config.setSkip(skip);
        config.setFallback(fallback);
        // fallback rules carry no condition; route/skip rules match by extension
        if (!fallback) {
            final AiCondition condition = new AiCondition();
            condition.setExtensions(extensions);
            config.setCondition(condition);
        }
        return config;
    }

    // <editor-fold defaultstate="collapsed" desc="collectCandidates">
    @Test
    public void collectCandidates_returnsMatchingJavaFile() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path sourceRoot = temp.resolve("src/main/java");
        writeJava(sourceRoot.resolve("com/example/Foo.java"), "class Foo {}\n");

        final List<Path> candidates = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .collectCandidates(sourceRoot);

        assertThat(candidates.size(), is(equalTo(1)));
        assertThat(candidates.get(0).getFileName().toString(), is(equalTo("Foo.java")));
    }

    @Test
    public void collectCandidates_excludesGlobAndSizeBand() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path sourceRoot = temp.resolve("src/main/java");
        writeJava(sourceRoot.resolve("com/example/Foo.java"), "class Foo {}\n");
        writeJava(sourceRoot.resolve("com/example/package-info.java"), "package com.example;\n");
        writeSizedJava(sourceRoot.resolve("com/example/Big.java"), 5000);

        // exclude package-info, and a max band of 1000 bytes drops Big.java
        final List<Path> candidates = indexer(
                        temp, temp.resolve("out"), Arrays.asList("**/package-info.java"), 0L, 1000L)
                .collectCandidates(sourceRoot);

        assertThat(candidates.size(), is(equalTo(1)));
        assertThat(candidates.get(0).getFileName().toString(), is(equalTo("Foo.java")));
    }

    @Test
    public void collectCandidates_sizeBandBoundaryMinExclusiveMaxInclusive() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path sourceRoot = temp.resolve("src/main/java");
        writeSizedJava(sourceRoot.resolve("com/example/Edge.java"), 1000);

        // max=1000 includes it (inclusive); min=1000 excludes it (exclusive)
        assertThat(
                indexer(temp, temp.resolve("a"), Collections.<String>emptyList(), 0L, 1000L)
                        .collectCandidates(sourceRoot)
                        .size(),
                is(equalTo(1)));
        assertThat(
                indexer(temp, temp.resolve("b"), Collections.<String>emptyList(), 1000L, 0L)
                        .collectCandidates(sourceRoot)
                        .size(),
                is(equalTo(0)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="classify">
    @Test
    public void classify_routesByExtensionToPrompt() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "class Foo {}\n");

        final List<AiFieldGenerationConfig> rules =
                Arrays.asList(rule("java", Arrays.asList(".java"), false, false), rule("fallback", null, false, true));

        final AiIndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(foo), rules);

        assertThat(plan.unmatched().isEmpty(), is(true));
        assertThat(plan.routedCount(), is(equalTo(1)));
        assertThat(
                plan.routesByModel()
                        .get(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT)
                        .get(0)
                        .rule()
                        .getPromptKey(),
                is(equalTo("java")));
    }

    @Test
    public void classify_skipRuleSendsFileToSkipped() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path generated = temp.resolve("src/main/java/com/example/Generated.java");
        writeJava(generated, "class Generated {}\n");

        final List<AiFieldGenerationConfig> rules = Arrays.asList(
                rule(null, Arrays.asList(".java"), true, false), // skip all .java
                rule("fallback", null, false, true));

        final AiIndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(generated), rules);

        assertThat(plan.skipped(), hasItem(generated));
        assertThat(plan.routedCount(), is(equalTo(0)));
    }

    @Test
    public void classify_noRuleAndNoFallback_recordsUnmatched() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "class Foo {}\n");

        final List<AiFieldGenerationConfig> rules =
                Collections.singletonList(rule("sql", Arrays.asList(".sql"), false, false));

        final AiIndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(foo), rules);

        assertThat(plan.unmatched(), hasItem(foo));
        assertThat(plan.routedCount(), is(equalTo(0)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="indexFile">
    @Test
    public void indexFile_writesAiMd_thenSkipsUnchanged() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");

        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);
        final AiFieldGenerationConfig rule =
                CommonTestFixtures.createFileFieldGenerations().get(0);
        final AiFieldGenerationSupport support = mockSupport();

        // first write
        final boolean wrote = indexer.indexFile(foo, rule, support);
        final Path aiFile = outputRoot.resolve("main/java/com/example/Foo.java.ai.md");

        assertThat(wrote, is(true));
        assertThat(Files.exists(aiFile), is(true));
        final AiMdDocument document = documentCodec.read(aiFile);
        assertThat(document, is(notNullValue()));
        assertThat(document.header().title(), is(equalTo("Foo.java")));
        assertThat(document.body().trim().isEmpty(), is(false));

        // second write — unchanged source, so it is skipped
        final boolean wroteAgain = indexer.indexFile(foo, rule, support);
        assertThat(wroteAgain, is(false));
    }
    // </editor-fold>
}

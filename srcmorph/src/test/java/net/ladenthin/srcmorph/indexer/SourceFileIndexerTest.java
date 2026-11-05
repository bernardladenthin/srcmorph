// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.ConditionGroup;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RangeCondition;
import net.ladenthin.srcmorph.config.RegenerateOn;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.indexer.generation.SummaryGenerator;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import net.ladenthin.srcmorph.provider.MockGenerationProvider;
import org.junit.jupiter.api.Test;

public class SourceFileIndexerTest {

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    private SourceFileIndexer indexer(
            final Path baseDirectory,
            final Path outputRoot,
            final List<String> excludes,
            final long minSizeBytes,
            final long maxSizeBytes) {
        return indexer(baseDirectory, outputRoot, excludes, minSizeBytes, maxSizeBytes, new RegenerateOn());
    }

    private SourceFileIndexer indexer(
            final Path baseDirectory,
            final Path outputRoot,
            final List<String> excludes,
            final long minSizeBytes,
            final long maxSizeBytes,
            final RegenerateOn regenerateOn) {
        return new SourceFileIndexer(
                baseDirectory,
                outputRoot,
                Arrays.asList(".java"),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                excludes,
                minSizeBytes,
                maxSizeBytes,
                false,
                regenerateOn,
                SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS);
    }

    private SummaryGenerator mockSupport() {
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        return new SummaryGenerator(
                new MockGenerationProvider(),
                new PromptPreparer(promptSupport),
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

    private static RoutingRule rule(
            final String promptKey, final List<String> extensions, final boolean skip, final boolean fallback) {
        final RoutingRule config = new RoutingRule();
        config.setPromptKey(promptKey);
        config.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        config.setSkip(skip);
        config.setFallback(fallback);
        // fallback rules carry no condition; route/skip rules match by extension
        if (!fallback) {
            final RuleCondition condition = new RuleCondition();
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

        final List<RoutingRule> rules =
                Arrays.asList(rule("java", Arrays.asList(".java"), false, false), rule("fallback", null, false, true));

        final IndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
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

        final List<RoutingRule> rules = Arrays.asList(
                rule(null, Arrays.asList(".java"), true, false), // skip all .java
                rule("fallback", null, false, true));

        final IndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(generated), rules);

        assertThat(plan.skipped(), hasItem(generated));
        assertThat(plan.routedCount(), is(equalTo(0)));
    }

    @Test
    public void classify_noRuleAndNoFallback_recordsUnmatched() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "class Foo {}\n");

        final List<RoutingRule> rules = Collections.singletonList(rule("sql", Arrays.asList(".sql"), false, false));

        final IndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
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
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final SummaryGenerator support = mockSupport();

        // first write
        final IndexOutcome wrote = indexer.indexFile(foo, rule, support, "", "", "");
        final Path aiFile = outputRoot.resolve("main/java/com/example/Foo.java.ai.md");

        assertThat(wrote, is(IndexOutcome.WRITTEN));
        assertThat(Files.exists(aiFile), is(true));
        final AiMdDocument document = documentCodec.read(aiFile);
        assertThat(document, is(notNullValue()));
        assertThat(document.header().title(), is(equalTo("Foo.java")));
        assertThat(document.body().trim().isEmpty(), is(false));

        // second write — unchanged source, so it is skipped
        final IndexOutcome wroteAgain = indexer.indexFile(foo, rule, support, "", "", "");
        assertThat(wroteAgain, is(IndexOutcome.UNCHANGED));
    }

    /**
     * The checksum is over the bytes: two bytes that are no UTF-8 both read as U+FFFD, so the decoded text
     * stays the same, but the file changed and its summary is rewritten.
     */
    @Test
    public void indexFile_anInvalidByteChangedForAnother_rewritesTheSummary() throws Exception {
        // arrange: a Latin-1 file, its umlaut no UTF-8
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        Files.createDirectories(foo.getParent());
        Files.write(foo, new byte[] {'c', 'l', 'a', 's', 's', ' ', 'F', (byte) 0xE4, '{', '}', '\n'});
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final SummaryGenerator support = mockSupport();
        assertThat(indexer.indexFile(foo, rule, support, "", "", ""), is(IndexOutcome.WRITTEN));

        // act: another byte that is no UTF-8 either
        Files.write(foo, new byte[] {'c', 'l', 'a', 's', 's', ' ', 'F', (byte) 0xF6, '{', '}', '\n'});
        final IndexOutcome again = indexer.indexFile(foo, rule, support, "", "", "");

        // assert
        assertThat(again, is(IndexOutcome.WRITTEN));
        assertThat(indexer.indexFile(foo, rule, support, "", "", ""), is(IndexOutcome.UNCHANGED));
    }

    @Test
    public void indexFile_changedPromptOrFactsHash_rewritesTheSummary() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final SummaryGenerator support = mockSupport();
        assertThat(indexer.indexFile(foo, rule, support, "AAAA0001", "BBBB0002", ""), is(IndexOutcome.WRITTEN));

        // act
        final IndexOutcome samePrompt = indexer.indexFile(foo, rule, support, "AAAA0001", "BBBB0002", "");
        final IndexOutcome newPrompt = indexer.indexFile(foo, rule, support, "CCCC0003", "BBBB0002", "");
        final IndexOutcome newFacts = indexer.indexFile(foo, rule, support, "CCCC0003", "DDDD0004", "");

        // assert
        assertThat(samePrompt, is(IndexOutcome.UNCHANGED));
        assertThat(newPrompt, is(IndexOutcome.WRITTEN));
        assertThat(newFacts, is(IndexOutcome.WRITTEN));
        final AiMdHeader header = documentCodec
                .read(outputRoot.resolve("main/java/com/example/Foo.java.ai.md"))
                .header();
        assertThat(header.promptHash(), is("CCCC0003"));
        assertThat(header.factsHash(), is("DDDD0004"));
    }

    @Test
    public void indexFile_anotherModel_rewritesTheSummaryAndRecordsIt() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final SummaryGenerator support = mockSupport();
        assertThat(indexer.indexFile(foo, rule, support, "AAAA0001", "", "EEEE0005"), is(IndexOutcome.WRITTEN));

        // act
        final IndexOutcome sameModel = indexer.indexFile(foo, rule, support, "AAAA0001", "", "EEEE0005");
        final IndexOutcome otherModel = indexer.indexFile(foo, rule, support, "AAAA0001", "", "FFFF0006");

        // assert
        assertThat(sameModel, is(IndexOutcome.UNCHANGED));
        assertThat(otherModel, is(IndexOutcome.WRITTEN));
        final AiMdHeader header = documentCodec
                .read(outputRoot.resolve("main/java/com/example/Foo.java.ai.md"))
                .header();
        assertThat(header.modelHash(), is("FFFF0006"));
        assertThat(header.childrenHash(), is(""));
    }

    @Test
    public void indexFile_changedPromptAndFactsHashWithTheirSwitchesOff_keepsTheSummary() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");
        final RegenerateOn neither = new RegenerateOn();
        neither.setPrompt(false);
        neither.setFacts(false);
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L, neither);
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final SummaryGenerator support = mockSupport();
        assertThat(indexer.indexFile(foo, rule, support, "AAAA0001", "BBBB0002", ""), is(IndexOutcome.WRITTEN));

        // act
        final IndexOutcome outcome = indexer.indexFile(foo, rule, support, "CCCC0003", "DDDD0004", "");

        // assert
        assertThat(outcome, is(IndexOutcome.UNCHANGED));
        final AiMdHeader header = documentCodec
                .read(outputRoot.resolve("main/java/com/example/Foo.java.ai.md"))
                .header();
        assertThat(header.promptHash(), is("AAAA0001"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="classify -- line-based routing">

    /**
     * Builds a rule that matches {@code .java} files within a line-count range.
     *
     * @param promptKey the rule's prompt key (also its identity in the assertions)
     * @param minLines  exclusive lower bound ({@code <= 0} disables it)
     * @param maxLines  inclusive upper bound ({@code <= 0} disables it)
     * @return the rule
     */
    private static RoutingRule lineRangeRule(final String promptKey, final long minLines, final long maxLines) {
        final RoutingRule config = new RoutingRule();
        config.setPromptKey(promptKey);
        config.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        // A condition node evaluates exactly ONE leaf and returns on the first one present, with
        // extensions checked before lines -- so setting both on the same node would silently make the
        // line range dead. Combining them requires an <and> group, which is also what validate()
        // enforces. Worth stating: the first version of this fixture set both on one node and every
        // file matched on extension alone.
        final RuleCondition extensionLeaf = new RuleCondition();
        extensionLeaf.setExtensions(Arrays.asList(".java"));

        final RangeCondition lines = new RangeCondition();
        lines.setMin(minLines);
        lines.setMax(maxLines);
        final RuleCondition lineLeaf = new RuleCondition();
        lineLeaf.setLines(lines);

        final ConditionGroup both = new ConditionGroup();
        both.setConditions(Arrays.asList(extensionLeaf, lineLeaf));
        final RuleCondition condition = new RuleCondition();
        condition.setAnd(both);
        config.setCondition(condition);
        return config;
    }

    /**
     * Two files differing only in line count must reach different rules.
     *
     * <p>This covers the wiring, which nothing else did: {@code <lines>} is documented in the plugin
     * README and the condition layer is tested directly, but no test ever made
     * {@code anyRuleUsesLines} return true, so {@code countLines} never ran during planning. The
     * failure mode is silent -- if the indexer stopped counting lines, every file would simply fall
     * through to the fallback rule and nothing would fail.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void classify_lineRangeRules_routeFilesByTheirLineCount() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path small = temp.resolve("src/main/java/com/example/Small.java");
        final Path large = temp.resolve("src/main/java/com/example/Large.java");
        writeJava(small, "class Small {}\n");
        final StringBuilder manyLines = new StringBuilder("class Large {\n");
        for (int i = 0; i < 20; i++) {
            manyLines.append("    // line ").append(i).append('\n');
        }
        manyLines.append("}\n");
        writeJava(large, manyLines.toString());

        final List<RoutingRule> rules = Arrays.asList(
                lineRangeRule("java-small", 0L, 5L),
                lineRangeRule("java-large", 5L, 1000L),
                rule("fallback", null, false, true));

        // act
        final IndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(small, large), rules);

        // assert -- both routed, and to the rule matching their own size
        assertThat(plan.unmatched().isEmpty(), is(true));
        assertThat(promptKeyOf(plan, small), is(equalTo("java-small")));
        assertThat(promptKeyOf(plan, large), is(equalTo("java-large")));
    }

    /**
     * The negative counterpart: with a line range no file satisfies, the fallback rule takes over.
     * Without this, a mutant that always matched the first line rule would survive the test above.
     *
     * @throws Exception if the fixture cannot be written
     */
    @Test
    public void classify_lineRangeMatchingNothing_fallsBackToTheFallbackRule() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path small = temp.resolve("src/main/java/com/example/Small.java");
        writeJava(small, "class Small {}\n");

        final List<RoutingRule> rules =
                Arrays.asList(lineRangeRule("java-huge", 500L, 1000L), rule("fallback", null, false, true));

        // act
        final IndexPlan plan = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .classify(Arrays.asList(small), rules);

        // assert
        assertThat(plan.unmatched().isEmpty(), is(true));
        assertThat(promptKeyOf(plan, small), is(equalTo("fallback")));
    }

    /**
     * Finds the prompt key of the rule a given file was routed to.
     *
     * @param plan the classification result
     * @param file the routed file
     * @return the matched rule's prompt key
     */
    private static String promptKeyOf(final IndexPlan plan, final Path file) {
        for (final List<IndexPlan.Route> entries : plan.routesByModel().values()) {
            for (final IndexPlan.Route entry : entries) {
                if (entry.file().equals(file)) {
                    return entry.rule().getPromptKey();
                }
            }
        }
        throw new AssertionError("file was not routed: " + file);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileNames, contentMatches and byte order marks">
    private static SourceFileIndexer indexerWithNames(final Path baseDirectory, final List<String> fileNames) {
        return indexer(
                baseDirectory,
                fileNames,
                Collections.<String>emptyList(),
                SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS,
                0L);
    }

    private static SourceFileIndexer indexer(
            final Path baseDirectory,
            final List<String> fileNames,
            final List<String> fileInterpreters,
            final int contentHeadChars,
            final long maxSizeBytes) {
        return new SourceFileIndexer(
                baseDirectory,
                baseDirectory.resolve("out"),
                Arrays.asList(".java"),
                fileNames,
                fileInterpreters,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                Collections.singletonList("**/excluded/**"),
                0L,
                maxSizeBytes,
                false,
                new RegenerateOn(),
                contentHeadChars);
    }

    private static RoutingRule contentRule(final String promptKey, final String regex) {
        final RoutingRule config = new RoutingRule();
        config.setPromptKey(promptKey);
        config.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        final RuleCondition condition = new RuleCondition();
        condition.setContentMatches(regex);
        config.setCondition(condition);
        return config;
    }

    private static void writeBytes(final Path file, final byte[] bytes) throws Exception {
        Files.createDirectories(file.getParent());
        Files.write(file, bytes);
    }

    @Test
    public void constructor_aFileNameWithADirectory_fails() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");

        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> indexerWithNames(temp, Arrays.asList("Jenkinsfile", "docker/Dockerfile")));

        // assert
        assertThat(
                thrown.getMessage(),
                is("fileNames match a file's name without its directory, not 'docker/Dockerfile'; use pathGlob"));
    }

    @Test
    public void collectCandidates_fileNames_selectFilesWithoutAnExtensionInAnyCase() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        writeJava(temp.resolve("Foo.java"), "class Foo {}\n");
        writeJava(temp.resolve("Dockerfile"), "FROM eclipse-temurin:21\n");
        writeJava(temp.resolve("ci/Jenkinsfile"), "pipeline {}\n");
        writeJava(temp.resolve("README"), "read me\n");

        // act
        final List<Path> candidates = indexerWithNames(temp, Arrays.asList("dockerfile", " Jenkinsfile ", " "))
                .collectCandidates(temp);

        // assert
        final List<String> names = new ArrayList<>();
        for (final Path candidate : candidates) {
            names.add(candidate.getFileName().toString());
        }
        names.sort(null);
        assertThat(names, is(equalTo(Arrays.asList("Dockerfile", "Foo.java", "Jenkinsfile"))));
    }

    @Test
    public void collectCandidates_withoutFileNames_takesTheExtensionsOnly() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        writeJava(temp.resolve("Foo.java"), "class Foo {}\n");
        writeJava(temp.resolve("Dockerfile"), "FROM eclipse-temurin:21\n");

        final SourceFileIndexer withNull = new SourceFileIndexer(
                temp,
                temp.resolve("out"),
                Arrays.asList(".java"),
                null,
                null,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                null,
                0L,
                0L,
                false,
                new RegenerateOn(),
                SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS);

        assertThat(withNull.collectCandidates(temp).size(), is(1));
    }

    @Test
    public void collectCandidates_fileInterpreters_selectAScriptWithoutAnExtensionByItsFirstLine() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        writeJava(temp.resolve("bin/deploy"), "#!/usr/bin/env bash\nset -e\n");
        writeJava(temp.resolve("bin/report"), "#!/usr/bin/python3.12 -u\nprint(1)\n");
        writeJava(temp.resolve("bin/.profile"), "#!/bin/sh\n");
        writeJava(temp.resolve("bin/notes"), "deploy on Fridays\n");
        writeJava(temp.resolve("bin/run.txt"), "#!/bin/bash\n");
        writeJava(temp.resolve("bin/tcl"), "#!/usr/bin/tclsh\n");
        writeJava(temp.resolve("bin/excluded/hook"), "#!/bin/bash\n");
        writeJava(temp.resolve("src/A.java"), "class A {}\n");

        // act
        final List<String> selected =
                names(indexer(temp, Collections.<String>emptyList(), Arrays.asList("bash", "python", " sh "), 256, 0L)
                        .collectCandidates(temp));
        final List<String> none =
                names(indexerWithNames(temp, Collections.<String>emptyList()).collectCandidates(temp));

        // assert
        assertThat(selected, is(equalTo(Arrays.asList(".profile", "A.java", "deploy", "report"))));
        assertThat(none, is(equalTo(Arrays.asList("A.java"))));
    }

    @Test
    public void collectCandidates_fileInterpreters_keepTheSizeBand() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        writeJava(temp.resolve("bin/small"), "#!/bin/bash\n");
        writeJava(temp.resolve("bin/large"), "#!/bin/bash\n" + "x".repeat(200));

        final List<String> selected =
                names(indexer(temp, Collections.<String>emptyList(), Arrays.asList("bash"), 256, 100L)
                        .collectCandidates(temp));

        assertThat(selected, is(equalTo(Arrays.asList("small"))));
    }

    @Test
    public void classify_interpretersRule_routesAScriptByItsFirstLine() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path script = temp.resolve("bin/deploy");
        final Path other = temp.resolve("bin/report");
        writeJava(script, "#!/bin/ksh\nprint ok\n");
        writeJava(other, "#!/usr/bin/env perl\n");
        final RoutingRule shell = rule("shell", null, false, false);
        final RuleCondition condition = new RuleCondition();
        condition.setInterpreters(Arrays.asList("bash", "ksh"));
        shell.setCondition(condition);
        final List<RoutingRule> rules = Arrays.asList(shell, rule("fallback", null, false, true));

        // act
        final IndexPlan plan = indexer(temp, Collections.<String>emptyList(), Arrays.asList("ksh", "perl"), 256, 0L)
                .classify(Arrays.asList(script, other), rules);

        // assert
        assertThat(promptKeyOf(plan, script), is(equalTo("shell")));
        assertThat(promptKeyOf(plan, other), is(equalTo("fallback")));
    }

    @Test
    public void classify_contentHeadChars_setsHowMuchContentMatchesReads() throws Exception {
        // arrange: MARK ends at character 300
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path file = temp.resolve("src/Late.java");
        writeJava(file, "x".repeat(296) + "MARK" + "y".repeat(100));
        final List<RoutingRule> contentOnly =
                Arrays.asList(contentRule("marked", "MARK"), rule("fallback", null, false, true));
        final List<RoutingRule> withLines = Arrays.asList(
                contentRule("marked", "MARK"),
                lineRangeRule("never", 900L, 1000L),
                rule("fallback", null, false, true));
        final SourceFileIndexer reach =
                indexer(temp, Collections.<String>emptyList(), Collections.<String>emptyList(), 300, 0L);
        final SourceFileIndexer shortOne =
                indexer(temp, Collections.<String>emptyList(), Collections.<String>emptyList(), 299, 0L);

        // act and assert: the start alone, and the whole source a lines rule reads
        assertThat(promptKeyOf(reach.classify(Arrays.asList(file), contentOnly), file), is(equalTo("marked")));
        assertThat(promptKeyOf(shortOne.classify(Arrays.asList(file), contentOnly), file), is(equalTo("fallback")));
        assertThat(promptKeyOf(reach.classify(Arrays.asList(file), withLines), file), is(equalTo("marked")));
        assertThat(promptKeyOf(shortOne.classify(Arrays.asList(file), withLines), file), is(equalTo("fallback")));
    }

    @Test
    public void constructor_contentHeadCharsBelowAShebangLine_isRefused() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> indexer(temp, Collections.<String>emptyList(), Collections.<String>emptyList(), 255, 0L));

        assertThat(
                thrown.getMessage(), is(equalTo("contentHeadChars must be at least 256 (a whole #! line), not 255")));
        assertDoesNotThrow(
                () -> indexer(temp, Collections.<String>emptyList(), Collections.<String>emptyList(), 256, 0L));
    }

    @Test
    public void classify_aContentHeadCharsLargerThanTheFile_readsNoMoreThanTheFile() throws Exception {
        // Integer.MAX_VALUE characters would be an 8 GB buffer; the file's size bounds it
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path file = temp.resolve("src/Small.java");
        writeJava(file, "class Small {} // MARK");
        final List<RoutingRule> rules =
                Arrays.asList(contentRule("marked", "MARK"), rule("fallback", null, false, true));

        final IndexPlan plan = indexer(
                        temp, Collections.<String>emptyList(), Collections.<String>emptyList(), Integer.MAX_VALUE, 0L)
                .classify(Arrays.asList(file), rules);

        assertThat(promptKeyOf(plan, file), is(equalTo("marked")));
    }

    @Test
    public void collectCandidates_aFileWithoutAnExtensionThatCannotBeRead_isSkippedAndTheWalkGoesOn() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path locked = temp.resolve("bin/locked");
        writeJava(locked, "#!/bin/bash\n");
        writeJava(temp.resolve("bin/deploy"), "#!/bin/bash\n");
        final SourceFileIndexer indexer =
                indexer(temp, Collections.<String>emptyList(), Arrays.asList("bash"), 256, 0L);

        final List<String> selected;
        if (java.nio.file.FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            // no read permission (the test does not run as root)
            Files.setPosixFilePermissions(locked, java.util.Collections.emptySet());
            try {
                selected = names(indexer.collectCandidates(temp));
            } finally {
                Files.setPosixFilePermissions(
                        locked, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
            }
        } else {
            // Windows refuses a read of a range another handle locks
            try (java.nio.channels.FileChannel channel =
                            java.nio.channels.FileChannel.open(locked, java.nio.file.StandardOpenOption.WRITE);
                    java.nio.channels.FileLock lock = channel.lock()) {
                selected = names(indexer.collectCandidates(temp));
            }
        }

        assertThat(selected, is(equalTo(Arrays.asList("deploy"))));
    }

    @Test
    public void constructor_interpreterWithItsDirectory_isRefused() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> indexer(temp, Collections.<String>emptyList(), Arrays.asList("/bin/bash"), 256, 0L));

        assertThat(
                thrown.getMessage(),
                is(equalTo("an interpreter is named without its directory (bash, not /bin/bash): '/bin/bash'")));
    }

    private static List<String> names(final List<Path> files) {
        final List<String> names = new java.util.ArrayList<>();
        for (final Path file : files) {
            names.add(String.valueOf(file.getFileName()));
        }
        names.sort(null);
        return names;
    }

    @Test
    public void classify_contentMatchesRule_routesByTheStartOfTheFile() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path bean = temp.resolve("src/OrderBean.java");
        final Path plain = temp.resolve("src/Order.java");
        writeJava(bean, "import jakarta.ejb.Stateless;\n@Stateless\npublic class OrderBean {}\n");
        writeJava(plain, "public class Order {}\n");
        final List<RoutingRule> rules =
                Arrays.asList(contentRule("ejb", "(?m)^@Stateless\\b"), rule("fallback", null, false, true));

        // act
        final IndexPlan plan =
                indexerWithNames(temp, Collections.<String>emptyList()).classify(Arrays.asList(bean, plain), rules);

        // assert
        assertThat(promptKeyOf(plan, bean), is(equalTo("ejb")));
        assertThat(promptKeyOf(plan, plain), is(equalTo("fallback")));
    }

    @Test
    public void classify_contentMatches_readsTheFirst16KibCharactersOnly() throws Exception {
        // arrange: MARK ends at character 16,384 in one file and one character later in the other
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path inside = temp.resolve("src/Inside.java");
        final Path outside = temp.resolve("src/Outside.java");
        final int head = SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS;
        writeJava(inside, "x".repeat(head - 4) + "MARK" + "y".repeat(100));
        writeJava(outside, "x".repeat(head - 3) + "MARK" + "y".repeat(100));
        final List<RoutingRule> contentOnly =
                Arrays.asList(contentRule("marked", "MARK"), rule("fallback", null, false, true));
        final List<RoutingRule> withLines = Arrays.asList(
                contentRule("marked", "MARK"),
                lineRangeRule("never", 900L, 1000L),
                rule("fallback", null, false, true));
        final SourceFileIndexer indexer = indexerWithNames(temp, Collections.<String>emptyList());

        // act: the start alone, and the whole source a lines rule reads
        final IndexPlan headOnly = indexer.classify(Arrays.asList(inside, outside), contentOnly);
        final IndexPlan wholeSource = indexer.classify(Arrays.asList(inside, outside), withLines);

        // assert
        assertThat(promptKeyOf(headOnly, inside), is(equalTo("marked")));
        assertThat(promptKeyOf(headOnly, outside), is(equalTo("fallback")));
        assertThat(promptKeyOf(wholeSource, inside), is(equalTo("marked")));
        assertThat(promptKeyOf(wholeSource, outside), is(equalTo("fallback")));
    }

    @Test
    public void classify_contentMatches_readsAMultiByteHeadWhole() throws Exception {
        // 16,384 characters of three bytes each are 49,152 bytes: the head reads them all
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path wide = temp.resolve("src/Wide.java");
        final int head = SrcMorphConfiguration.DEFAULT_CONTENT_HEAD_CHARS;
        writeJava(wide, "€".repeat(head - 4) + "MARK" + "€".repeat(head));
        final List<RoutingRule> rules =
                Arrays.asList(contentRule("marked", "MARK"), rule("fallback", null, false, true));

        final IndexPlan plan =
                indexerWithNames(temp, Collections.<String>emptyList()).classify(Arrays.asList(wide), rules);

        assertThat(promptKeyOf(plan, wide), is(equalTo("marked")));
    }

    @Test
    public void classify_contentMatches_readsAFileAfterItsByteOrderMarkAndWithLfLineEndings() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path bom = temp.resolve("src/Bom.java");
        final byte[] text = "package a;\r\nclass Bom {}\r\n".getBytes(StandardCharsets.UTF_8);
        final byte[] withMark = new byte[text.length + 3];
        withMark[0] = (byte) 0xEF;
        withMark[1] = (byte) 0xBB;
        withMark[2] = (byte) 0xBF;
        System.arraycopy(text, 0, withMark, 3, text.length);
        writeBytes(bom, withMark);
        final List<RoutingRule> rules =
                Arrays.asList(contentRule("first", "\\Apackage a;\\nclass"), rule("fallback", null, false, true));
        final List<RoutingRule> withLines = Arrays.asList(
                contentRule("first", "\\Apackage a;\\nclass"),
                lineRangeRule("never", 900L, 1000L),
                rule("fallback", null, false, true));
        final SourceFileIndexer indexer = indexerWithNames(temp, Collections.<String>emptyList());

        // act
        final IndexPlan headOnly = indexer.classify(Arrays.asList(bom), rules);
        final IndexPlan wholeSource = indexer.classify(Arrays.asList(bom), withLines);

        // assert
        assertThat(promptKeyOf(headOnly, bom), is(equalTo("first")));
        assertThat(promptKeyOf(wholeSource, bom), is(equalTo("first")));
    }

    @Test
    public void indexFile_sourceWithAByteOrderMark_countsFromItsFirstWord() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        final byte[] text = "package com.example;\nclass Foo {}\n".getBytes(StandardCharsets.UTF_8);
        final byte[] withMark = new byte[text.length + 3];
        withMark[0] = (byte) 0xEF;
        withMark[1] = (byte) 0xBB;
        withMark[2] = (byte) 0xBF;
        System.arraycopy(text, 0, withMark, 3, text.length);
        writeBytes(foo, withMark);
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final FactCounter counter = new FactCounter();
        counter.setLabel("starts with package");
        counter.setPattern("\\Apackage\\b");
        rule.setFacts(Collections.singletonList(counter));

        // act
        final IndexOutcome outcome = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L)
                .indexFile(foo, rule, mockSupport(), "", "", "");

        // assert
        assertThat(outcome, is(IndexOutcome.WRITTEN));
        final String body = documentCodec
                .read(outputRoot.resolve("main/java/com/example/Foo.java.ai.md"))
                .body();
        assertThat(body.contains("starts with package: 1"), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    private SummaryGenerator emptyAnswerSupport() {
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        return new SummaryGenerator(
                request -> "",
                new PromptPreparer(promptSupport),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
    }

    private static RoutingRule ruleWithFacts() {
        final RoutingRule rule = CommonTestFixtures.createFileRules().get(0);
        final FactCounter counter = new FactCounter();
        counter.setLabel("classes");
        counter.setPattern("\\bclass\\b");
        rule.setFacts(Collections.singletonList(counter));
        return rule;
    }

    @Test
    public void indexFile_incompleteAnswer_writesNothing() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");

        final IndexOutcome outcome = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L)
                .indexFile(foo, ruleWithFacts(), emptyAnswerSupport(), "", "", "");

        // the facts line alone is no summary: nothing is written, so the next run tries again
        assertThat(outcome, is(IndexOutcome.FAILED));
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/Foo.java.ai.md")), is(false));
    }

    @Test
    public void indexFile_incompleteAnswerForAChangedSource_keepsTheOlderSummary() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final Path foo = temp.resolve("src/main/java/com/example/Foo.java");
        writeJava(foo, "package com.example;\nclass Foo {}\n");
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);
        final Path aiFile = outputRoot.resolve("main/java/com/example/Foo.java.ai.md");
        assertThat(indexer.indexFile(foo, ruleWithFacts(), mockSupport(), "", "", ""), is(IndexOutcome.WRITTEN));
        final byte[] older = Files.readAllBytes(aiFile);

        writeJava(foo, "package com.example;\nclass Foo { int x; }\n");
        final IndexOutcome outcome = indexer.indexFile(foo, ruleWithFacts(), emptyAnswerSupport(), "", "", "");

        assertThat(outcome, is(IndexOutcome.FAILED));
        assertThat(Arrays.equals(Files.readAllBytes(aiFile), older), is(true));
    }
    // </editor-fold>

    @Test
    public void targetFile_isTheSourcePathBelowSrcPlusAiMd() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("src/site/ai");
        final SourceFileIndexer indexer = indexer(temp, outputRoot, Collections.<String>emptyList(), 0L, 0L);

        assertThat(
                indexer.targetFile(temp.resolve("src/main/java/com/example/Foo.java")),
                is(equalTo(outputRoot.resolve("main/java/com/example/Foo.java.ai.md"))));
    }

    @Test
    public void indexFile_crLfSource_reachesTheModelAndTheChunksWithLineFeedsOnly() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path big = temp.resolve("src/main/java/com/example/Big.java");
        final StringBuilder source = new StringBuilder();
        for (int i = 0; i < 400; i++) {
            source.append("int field").append(i).append(" = ").append(i).append(";\r\n");
        }
        writeJava(big, source.toString());
        final ModelDefinition small = new ModelDefinition();
        small.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        small.setModelPath("mock.gguf");
        small.setCharsPerToken(1);
        small.setContextSize(4_000);
        final List<String> sent = new ArrayList<>();
        final SummaryGenerator recording = new SummaryGenerator(
                request -> {
                    sent.add(request.sourceText());
                    return "summary";
                },
                new PromptPreparer(new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions())),
                new ModelRegistry(Collections.singletonList(small)));
        final RoutingRule rule = CommonTestFixtures.createFileFieldGeneration();
        rule.setOnOversize("sampleChunks");
        rule.setMaxChunks(3);

        final IndexOutcome outcome = indexer(temp, temp.resolve("src/site/ai"), Collections.<String>emptyList(), 0L, 0L)
                .indexFile(big, rule, recording, "", "", "");

        assertThat(outcome, is(IndexOutcome.WRITTEN));
        // three chunks and one reduce
        assertThat(sent.size(), is(4));
        for (final String text : sent) {
            assertThat(text.indexOf('\r'), is(-1));
        }
    }

    @Test
    public void indexFile_theModelReadsThePathBelowTheBaseDirectory() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path web = temp.resolve("billing/src/main/webapp/WEB-INF/web.xml");
        writeJava(web, "<web-app/>");
        final ModelDefinition model = new ModelDefinition();
        model.setKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);
        model.setModelPath("mock.gguf");
        final PromptCatalog prompts = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final List<String> heads = new ArrayList<>();
        final SummaryGenerator recording = new SummaryGenerator(
                request -> {
                    final String message = prompts.userMessage(request.sourceFile(), request.sourceText());
                    heads.add(message.substring(0, message.indexOf('\n')));
                    return "summary";
                },
                new PromptPreparer(prompts),
                new ModelRegistry(Collections.singletonList(model)));

        final IndexOutcome outcome = indexer(temp, temp.resolve("out"), Collections.<String>emptyList(), 0L, 0L)
                .indexFile(web, CommonTestFixtures.createFileFieldGeneration(), recording, "", "", "");

        assertThat(outcome, is(IndexOutcome.WRITTEN));
        assertThat(heads, is(equalTo(Arrays.asList("billing/src/main/webapp/WEB-INF/web.xml"))));
    }
}

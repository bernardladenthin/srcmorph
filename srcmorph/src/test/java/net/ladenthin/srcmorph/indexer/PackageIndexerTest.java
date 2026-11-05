// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiGenerationRequest;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.MockAiGenerationProvider;
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
                baseDirectory,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                new MockAiGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final AiIndexTally aggregated = indexer.aggregate(outputRoot);

        // pre-assert
        assertThat(Files.exists(packageAiFile), is(true));

        // assert
        assertThat(aggregated.written(), is(equalTo(5)));
        assertThat(aggregated.failed(), is(equalTo(0)));

        final AiMdDocument document = documentCodec.read(packageAiFile);

        // pre-assert
        assertThat(document, is(notNullValue()));

        // assert
        assertThat(document.header().title(), is(equalTo("main/java/com/example")));
        assertThat(document.header().headerVersion(), is(equalTo(AiMdHeaderCodec.HEADER_VERSION_1_0)));
        assertThat(document.header().nodeType(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_PACKAGE)));
        assertThat(document.header().generatorVersion(), is(equalTo("1.0.0")));
        assertThat(document.header().aiVersion(), is(equalTo("0.0.0")));
        assertThat(document.header().checksum().trim().isEmpty(), is(false));
        assertThat(document.header().sourceDate().trim().isEmpty(), is(false));
        assertThat(document.header().generatedAt().trim().isEmpty(), is(false));
        assertThat(document.body().trim().isEmpty(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="child summaries in package source">
    @Test
    public void aggregate_childFileBodies_areEmbeddedInPackageSourceText() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(packageDirectory);

        writeChildFile(packageDirectory.resolve("Foo.java.ai.md"), "Foo.java", "FOO_BODY_MARKER summary of Foo.");
        writeChildFile(packageDirectory.resolve("Bar.java.ai.md"), "Bar.java", "BAR_BODY_MARKER summary of Bar.");

        final CapturingProvider provider = new CapturingProvider();
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the example package's AI source text contains both child bodies, each under a
        // per-file heading — not merely the file names.
        final String exampleSource = provider.sourceTextFor(packageDirectory.resolve("package.ai.md"));
        assertThat(exampleSource, is(notNullValue()));
        assertThat(exampleSource.contains("### Foo.java"), is(true));
        assertThat(exampleSource.contains("FOO_BODY_MARKER summary of Foo."), is(true));
        assertThat(exampleSource.contains("### Bar.java"), is(true));
        assertThat(exampleSource.contains("BAR_BODY_MARKER summary of Bar."), is(true));
        // the raw .ai.md file-name form must NOT leak into the labelled headings
        assertThat(exampleSource.contains("### Foo.java.ai.md"), is(false));
    }

    @Test
    public void aggregate_subPackageBody_isEmbeddedInParentSourceText() throws Exception {
        // arrange: a child file in the leaf package, whose generated body bubbles up into the parent.
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path leafDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(leafDirectory);

        writeChildFile(leafDirectory.resolve("Foo.java.ai.md"), "Foo.java", "FOO_BODY_MARKER summary of Foo.");

        final CapturingProvider provider = new CapturingProvider();
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the parent ("com") package source embeds the leaf sub-package's generated body
        // under a directory-suffixed heading.
        final Path comPackageFile = outputRoot.resolve("main/java/com").resolve("package.ai.md");
        final String comSource = provider.sourceTextFor(comPackageFile);
        assertThat(comSource, is(notNullValue()));
        assertThat(comSource.contains("### example/"), is(true));
        assertThat(comSource.contains(CapturingProvider.GENERATED_BODY), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="project.ai.md is ignored as content">
    @Test
    public void aggregate_projectAiMdInOutputRoot_isNotEmbeddedAsChild() throws Exception {
        // arrange: a leaf file (so the tree aggregates up to the root) plus a stray project.ai.md
        // already sitting in the output root from a previous aggregate-project run.
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path leafDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(leafDirectory);
        writeChildFile(leafDirectory.resolve("Foo.java.ai.md"), "Foo.java", "FOO_BODY summary of Foo.");

        final AiMdHeader projectHeader = new AiMdHeader(
                "my-project",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "BBBBBBBB",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PROJECT);
        documentCodec.write(
                outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME),
                new AiMdDocument(projectHeader, "PROJECT_BODY_MARKER must not be embedded as a child."));

        final CapturingProvider provider = new CapturingProvider();
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the root package was aggregated (its sub-package is embedded) but the stray
        // project.ai.md is neither embedded as a child body nor listed as content.
        final String rootSource = provider.sourceTextFor(outputRoot.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME));
        assertThat(rootSource, is(notNullValue()));
        assertThat(rootSource, containsString("### main/"));
        assertThat(rootSource, not(containsString("PROJECT_BODY_MARKER")));
        assertThat(rootSource, not(containsString(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="child links in header">
    @Test
    public void aggregate_packageHeader_listsChildLinksInHeader() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(packageDirectory);
        writeChildFile(packageDirectory.resolve("Foo.java.ai.md"), "Foo.java", "Foo summary.");
        writeChildFile(packageDirectory.resolve("Bar.java.ai.md"), "Bar.java", "Bar summary.");

        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                new MockAiGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the example package header carries one deterministic child link per file,
        // in ascending name order, pointing at each child .ai.md.
        final AiMdDocument document = documentCodec.read(packageDirectory.resolve("package.ai.md"));
        assertThat(
                document.header().children(),
                is(equalTo(Arrays.asList("[Bar.java](Bar.java.ai.md)", "[Foo.java](Foo.java.ai.md)"))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="regenerateOn: children">
    private PackageIndexer indexer(final Path temp, final net.ladenthin.srcmorph.config.AiRegenerateOn regenerateOn) {
        return new PackageIndexer(
                temp,
                temp.resolve("ai"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                regenerateOn,
                new MockAiGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions()),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
    }

    @Test
    public void aggregate_aChildSummaryRewrittenOverTheSameSource_rewritesThePackageWhileChildrenIsOn()
            throws Exception {
        // arrange: a file summary rewritten for a new prompt keeps its source checksum C
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path packageDirectory = temp.resolve("ai/main/java/com/example");
        Files.createDirectories(packageDirectory);
        final Path child = packageDirectory.resolve("Foo.java.ai.md");
        final Path packageFile = packageDirectory.resolve("package.ai.md");
        writeChildFile(child, "Foo.java", "> Old summary of Foo.\n");
        final net.ladenthin.srcmorph.config.AiRegenerateOn childrenOff =
                new net.ladenthin.srcmorph.config.AiRegenerateOn();
        childrenOff.setChildren(false);
        indexer(temp, new net.ladenthin.srcmorph.config.AiRegenerateOn()).aggregate(temp.resolve("ai"));
        final AiMdHeader first = documentCodec.read(packageFile).header();

        // act
        final AiIndexTally unchanged = indexer(temp, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(temp.resolve("ai"));
        writeChildFile(child, "Foo.java", "> New summary of Foo.\n");
        final AiIndexTally frozen = indexer(temp, childrenOff).aggregate(temp.resolve("ai"));
        final AiIndexTally rewritten = indexer(temp, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(temp.resolve("ai"));

        // assert
        assertThat(first.promptHash().isEmpty(), is(false));
        assertThat(first.modelHash().isEmpty(), is(false));
        assertThat(first.childrenHash().isEmpty(), is(false));
        assertThat(unchanged.written(), is(equalTo(0)));
        assertThat(frozen.written(), is(equalTo(0)));
        // only this package: the mock writes its parents the same body again, so they read unchanged children
        assertThat(rewritten.written(), is(equalTo(1)));
        final AiMdHeader second = documentCodec.read(packageFile).header();
        assertThat(second.checksum(), is(first.checksum()));
        assertThat(second.childrenHash().equals(first.childrenHash()), is(false));
    }
    // </editor-fold>

    private void writeChildFile(final Path file, final String title, final String body) throws IOException {
        final AiMdHeader header = new AiMdHeader(
                title,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        documentCodec.write(file, new AiMdDocument(header, body));
    }

    /**
     * Test {@link AiGenerationProvider} that records the source text passed for each context file
     * and returns a fixed non-blank body, so a generated {@code package.ai.md} body is never blank.
     */
    private static final class CapturingProvider implements AiGenerationProvider {

        static final String GENERATED_BODY = "CAPTURED_PACKAGE_SUMMARY";

        private final Map<Path, String> sourceTextByFile = new HashMap<>();

        @Override
        public String generate(final AiGenerationRequest request) {
            sourceTextByFile.put(request.sourceFile(), request.sourceText());
            return GENERATED_BODY;
        }

        String sourceTextFor(final Path file) {
            return sourceTextByFile.get(file);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    @Test
    public void aggregate_incompleteAnswer_writesNoPackageFileAndCountsItAsFailed() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(packageDirectory);
        writeChildFile(packageDirectory.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                request -> "",
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        final AiIndexTally tally = indexer.aggregate(outputRoot);

        // no deterministic Contents body stands in for the missing summary any more
        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(tally.written(), is(equalTo(0)));
        assertThat(Files.exists(packageDirectory.resolve("package.ai.md")), is(false));
    }
    // </editor-fold>

    @Test
    public void aggregate_requestThatFails_isCountedAsFailedAndTheOtherPackagesAreWritten() throws Exception {
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                request -> {
                    if (request.sourceFile().startsWith(failing)) {
                        throw new java.io.IOException("server unreachable");
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        final AiIndexTally tally = indexer.aggregate(outputRoot);

        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(Files.exists(failing.resolve("package.ai.md")), is(false));
        assertThat(Files.exists(working.resolve("package.ai.md")), is(true));
    }
}

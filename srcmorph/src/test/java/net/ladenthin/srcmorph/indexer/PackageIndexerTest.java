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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.RegenerateOn;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.MockGenerationProvider;
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

        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                baseDirectory,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                new MockGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally aggregated = indexer.aggregate(outputRoot);

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

    @Test
    public void sorted_aListingThatFailsMidway_failsWithItsIOException() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                temp.resolve("ai"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                new MockGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions()),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
        final IOException gone = new IOException("the share is gone");
        final Stream<Path> listing = Stream.of(temp.resolve("b"), temp.resolve("a"))
                .map(path -> {
                    if (path.endsWith("a")) {
                        throw new UncheckedIOException(gone);
                    }
                    return path;
                });

        // act
        final IOException thrown = assertThrows(IOException.class, () -> indexer.sorted(listing));

        // assert
        assertThat(thrown, is(gone));
    }

    @Test
    public void aggregate_aDirectoryNamedLikeASummary_isNone() throws Exception {
        // arrange: no summary, only a directory named like one, without a package of its own
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        Files.createDirectories(outputRoot.resolve("main/a/Odd.java.ai.md"));
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                new MockGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally aggregated = indexer.aggregate(outputRoot);

        // assert
        assertThat(aggregated.written(), is(equalTo(0)));
        assertThat(Files.exists(outputRoot.resolve("main/a/package.ai.md")), is(false));
    }

    @Test
    public void aggregate_aLinkToADirectory_isNotEntered() throws Exception {
        // arrange: a package, a link to it beside it and a link back up, a loop
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/a");
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
        documentCodec.write(packageDirectory.resolve("Test.java.ai.md"), new AiMdDocument(childHeader, ""));
        CommonTestFixtures.directoryLink(outputRoot.resolve("main/b"), packageDirectory);
        CommonTestFixtures.directoryLink(packageDirectory.resolve("loop"), outputRoot.resolve("main"));
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                new MockGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally aggregated = indexer.aggregate(outputRoot);

        // assert: main/a, main and the root; main names a/ only
        assertThat(aggregated.written(), is(equalTo(3)));
        assertThat(aggregated.failed(), is(equalTo(0)));
        final String main = Files.readString(outputRoot.resolve("main/package.ai.md"));
        assertThat(main, containsString("a/package.ai.md"));
        assertThat(main, not(containsString("b/package.ai.md")));
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
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the example package's AI source text contains both child bodies, each under a
        // per-file heading — not merely the file names.
        // the call names the package by its path below the output root
        final String exampleSource = provider.sourceTextFor(Paths.get("main/java/com/example"));
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
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the parent ("com") package source embeds the leaf sub-package's generated body
        // under a directory-suffixed heading.
        final String comSource = provider.sourceTextFor(Paths.get("main/java/com"));
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
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert: the root package was aggregated (its sub-package is embedded) but the stray
        // project.ai.md is neither embedded as a child body nor listed as content.
        // the root package has no path below the output root: it is named by the output directory
        final String rootSource = provider.sourceTextFor(Paths.get("ai"));
        assertThat(rootSource, is(notNullValue()));
        assertThat(rootSource, containsString("### main/"));
        assertThat(rootSource, not(containsString("PROJECT_BODY_MARKER")));
        assertThat(rootSource, not(containsString(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)));
    }

    @Test
    public void aggregate_projectAiMdBelowTheOutputRoot_isAChildSummary() throws Exception {
        // arrange: below the output root a project.ai.md is the summary of a source named project
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path leafDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(leafDirectory);
        writeChildFile(leafDirectory.resolve("Foo.java.ai.md"), "Foo.java", "FOO_BODY summary of Foo.");
        writeChildFile(leafDirectory.resolve("project.ai.md"), "project", "PROJECT_SOURCE_BODY of the script.");

        final CapturingProvider provider = new CapturingProvider();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                provider,
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        indexer.aggregate(outputRoot);

        // assert
        final String leafSource = provider.sourceTextFor(Paths.get("main/java/com/example"));
        assertThat(leafSource, is(notNullValue()));
        assertThat(leafSource, containsString("PROJECT_SOURCE_BODY"));
        assertThat(
                documentCodec
                        .read(leafDirectory.resolve(AiMdHeaderCodec.PACKAGE_AI_MD_FILENAME))
                        .header()
                        .children()
                        .toString(),
                containsString("project.ai.md"));
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

        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                new MockGenerationProvider(),
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
    private PackageIndexer indexer(final Path temp, final RegenerateOn regenerateOn) {
        return new PackageIndexer(
                temp,
                temp.resolve("ai"),
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                regenerateOn,
                new MockGenerationProvider(),
                CommonTestFixtures.createPackageFieldGeneration(),
                new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions()),
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
        final RegenerateOn childrenOff = new RegenerateOn();
        childrenOff.setChildren(false);
        indexer(temp, new RegenerateOn()).aggregate(temp.resolve("ai"));
        final AiMdHeader first = documentCodec.read(packageFile).header();

        // act
        final IndexTally unchanged = indexer(temp, new RegenerateOn()).aggregate(temp.resolve("ai"));
        writeChildFile(child, "Foo.java", "> New summary of Foo.\n");
        final IndexTally frozen = indexer(temp, childrenOff).aggregate(temp.resolve("ai"));
        final IndexTally rewritten = indexer(temp, new RegenerateOn()).aggregate(temp.resolve("ai"));

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
     * Test {@link GenerationProvider} that records the source text passed for each context file
     * and returns a fixed non-blank body, so a generated {@code package.ai.md} body is never blank.
     */
    private static final class CapturingProvider implements GenerationProvider {

        static final String GENERATED_BODY = "CAPTURED_PACKAGE_SUMMARY";

        private final Map<Path, String> sourceTextByFile = new HashMap<>();

        @Override
        public String generate(final GenerationRequest request) {
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
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path packageDirectory = outputRoot.resolve("main/java/com/example");
        Files.createDirectories(packageDirectory);
        writeChildFile(packageDirectory.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> "",
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally tally = indexer.aggregate(outputRoot);

        // assert
        // no deterministic Contents body stands in for the missing summary any more
        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(tally.written(), is(equalTo(0)));
        assertThat(Files.exists(packageDirectory.resolve("package.ai.md")), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="failing requests">
    @Test
    public void aggregate_requestThatFails_isCountedAsFailedAndTheOtherPackagesAreWritten() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> {
                    // the call names the package by its path below the output root
                    if (request.sourceFile().equals(Paths.get("main/java/com/example/failing"))) {
                        throw new IOException("server unreachable");
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally tally = indexer.aggregate(outputRoot);

        // assert
        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(Files.exists(failing.resolve("package.ai.md")), is(false));
        assertThat(Files.exists(working.resolve("package.ai.md")), is(true));
    }

    @Test
    public void aggregate_uncheckedProviderError_isCountedAsFailedAndTheOtherPackagesAreWritten() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> {
                    // the call names the package by its path below the output root
                    if (request.sourceFile().equals(Paths.get("main/java/com/example/failing"))) {
                        throw new IllegalStateException("the context is full");
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally tally = indexer.aggregate(outputRoot);

        // assert
        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(Files.exists(failing.resolve("package.ai.md")), is(false));
        assertThat(Files.exists(working.resolve("package.ai.md")), is(true));
    }

    @Test
    public void aggregate_timeoutWithoutAnInterrupt_isCountedAsFailedAndTheOtherPackagesAreWritten() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> {
                    // the call names the package by its path below the output root
                    if (request.sourceFile().equals(Paths.get("main/java/com/example/failing"))) {
                        throw new SocketTimeoutException("no answer");
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final IndexTally tally = indexer.aggregate(outputRoot);

        // assert
        assertThat(tally.failed(), is(equalTo(1)));
        assertThat(Files.exists(failing.resolve("package.ai.md")), is(false));
        assertThat(Files.exists(working.resolve("package.ai.md")), is(true));
    }

    @Test
    public void aggregate_interrupt_endsThePass() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions());
        final PackageIndexer indexer = new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> {
                    // the call names the package by its path below the output root
                    if (request.sourceFile().equals(Paths.get("main/java/com/example/failing"))) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException("interrupted");
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        final boolean interrupted;
        try {
            assertThrows(InterruptedIOException.class, () -> indexer.aggregate(outputRoot));
        } finally {
            // the flag the provider set; cleared so it does not reach the next test
            interrupted = Thread.interrupted();
        }

        // assert: the interrupt ends the pass instead of failing one package and going on
        assertThat(interrupted, is(true));
        assertThat(Files.exists(failing.resolve("package.ai.md")), is(false));
    }

    /** A package indexer over two packages whose first ({@code failing}) is answered by {@code failure}. */
    private PackageIndexer interruptedIndexer(final Path temp, final Path outputRoot, final IOException failure)
            throws IOException {
        final Path failing = outputRoot.resolve("main/java/com/example/failing");
        final Path working = outputRoot.resolve("main/java/com/example/working");
        Files.createDirectories(failing);
        Files.createDirectories(working);
        writeChildFile(failing.resolve("Foo.java.ai.md"), "Foo.java", "Summary of Foo.");
        writeChildFile(working.resolve("Bar.java.ai.md"), "Bar.java", "Summary of Bar.");
        return new PackageIndexer(
                temp,
                outputRoot,
                "1.0.0",
                "0.0.0",
                Collections.<Path>emptyList(),
                false,
                new RegenerateOn(),
                request -> {
                    if (request.sourceFile().equals(Paths.get("main/java/com/example/failing"))) {
                        // what an interrupted blocking call does: the thread's flag is set, then it throws
                        Thread.currentThread().interrupt();
                        throw failure;
                    }
                    return "A package summary.";
                },
                CommonTestFixtures.createPackageFieldGeneration(),
                new PromptCatalog(CommonTestFixtures.createPackagePromptDefinitions()),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
    }

    @Test
    public void aggregate_anInterruptAsAClosedChannel_endsThePass() throws Exception {
        // arrange: an interrupted NIO channel throws ClosedByInterruptException, no InterruptedIOException
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final PackageIndexer indexer = interruptedIndexer(temp, outputRoot, new ClosedByInterruptException());

        // act
        final boolean interrupted;
        try {
            assertThrows(ClosedByInterruptException.class, () -> indexer.aggregate(outputRoot));
        } finally {
            interrupted = Thread.interrupted();
        }

        // assert: the pass ended at the interrupt; the next package was not summarized
        assertThat(interrupted, is(true));
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/working/package.ai.md")), is(false));
    }

    @Test
    public void aggregate_anIOExceptionWithTheInterruptFlagSet_endsThePass() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        final PackageIndexer indexer = interruptedIndexer(temp, outputRoot, new IOException("stream closed"));

        // act
        final boolean interrupted;
        try {
            assertThrows(IOException.class, () -> indexer.aggregate(outputRoot));
        } finally {
            interrupted = Thread.interrupted();
        }

        // assert
        assertThat(interrupted, is(true));
        assertThat(Files.exists(outputRoot.resolve("main/java/com/example/working/package.ai.md")), is(false));
    }
    // </editor-fold>
}

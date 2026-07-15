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
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
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
                provider,
                CommonTestFixtures.createPackageFieldGenerations(),
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
                provider,
                CommonTestFixtures.createPackageFieldGenerations(),
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
                provider,
                CommonTestFixtures.createPackageFieldGenerations(),
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
                new MockAiGenerationProvider(),
                CommonTestFixtures.createPackageFieldGenerations(),
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
}

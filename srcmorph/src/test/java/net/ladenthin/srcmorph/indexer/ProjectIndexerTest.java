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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RegenerateOn;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.MockGenerationProvider;
import org.junit.jupiter.api.Test;

public class ProjectIndexerTest {

    private static final String PROJECT_TITLE = "my-project";

    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    // <editor-fold defaultstate="collapsed" desc="happy path: header + body">
    @Test
    public void aggregate_multiplePackages_writesProjectIndexHeaderAndListing() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/fiscal/package.ai.md"),
                "main/java/com/example/fiscal",
                "> Calculates VAT for invoices.\n\n#### Purpose\n- taxes\n");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/billing/package.ai.md"),
                "main/java/com/example/billing",
                "> Creates and sends invoices.\n\n#### Purpose\n- billing\n");

        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IndexOutcome written = indexer.aggregate(outputRoot);

        // pre-assert
        assertThat(written, is(IndexOutcome.WRITTEN));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        assertThat(Files.exists(projectFile), is(true));

        // assert: header
        final AiMdDocument document = documentCodec.read(projectFile);
        assertThat(document, is(notNullValue()));
        assertThat(document.header().title(), is(equalTo(PROJECT_TITLE)));
        assertThat(document.header().nodeType(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_PROJECT)));
        assertThat(document.header().generatorVersion(), is(equalTo("1.0.0")));
        assertThat(document.header().aiVersion(), is(equalTo("0.0.0")));
        assertThat(document.header().checksum().trim().isEmpty(), is(false));
        assertThat(document.header().sourceDate().trim().isEmpty(), is(false));
        assertThat(document.header().generatedAt().trim().isEmpty(), is(false));

        // assert: the body carries each package's path + lead (one line each, no link)
        final String body = document.body();
        assertThat(body, containsString("#### Packages"));
        assertThat(body, containsString("- main/java/com/example/fiscal — Calculates VAT for invoices."));
        assertThat(body, containsString("- main/java/com/example/billing — Creates and sends invoices."));

        // assert: the clickable links live in the header F list, sorted ascending by link
        assertThat(
                document.header().children(),
                is(equalTo(Arrays.asList(
                        "[main/java/com/example/billing](main/java/com/example/billing/package.ai.md)",
                        "[main/java/com/example/fiscal](main/java/com/example/fiscal/package.ai.md)"))));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="no packages">
    @Test
    public void aggregate_aDirectoryThatCannotBeListed_failsWithItsIOException() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        final Path locked = outputRoot.resolve("main/locked");
        Files.createDirectories(locked);
        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IOException thrown;
        try (AutoCloseable denied = CommonTestFixtures.unlistable(locked)) {
            thrown = assertThrows(IOException.class, () -> indexer.aggregate(outputRoot));
        }

        // assert
        assertThat(thrown.getMessage(), containsString("locked"));
    }

    @Test
    public void aggregate_aLinkToADirectory_isNotEntered() throws Exception {
        // arrange: a package, a link to it beside it and a link back up, a loop
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("main/a/package.ai.md"), "main/a", "> A package.\n");
        CommonTestFixtures.directoryLink(outputRoot.resolve("main/b"), outputRoot.resolve("main/a"));
        CommonTestFixtures.directoryLink(outputRoot.resolve("main/a/loop"), outputRoot.resolve("main"));
        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IndexOutcome written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(IndexOutcome.WRITTEN));
        final String project = Files.readString(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(project, containsString("main/a/package.ai.md"));
        assertThat(project, not(containsString("main/b/")));
        assertThat(project, not(containsString("loop")));
    }

    @Test
    public void aggregate_anOutputDirectoryThatIsALink_isEntered() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        writePackageFile(temp.resolve("real/main/a/package.ai.md"), "main/a", "> A package.\n");
        final Path outputRoot = temp.resolve("ai");
        CommonTestFixtures.directoryLink(outputRoot, temp.resolve("real"));
        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IndexOutcome written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(IndexOutcome.WRITTEN));
        assertThat(
                Files.readString(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)),
                containsString("main/a/package.ai.md"));
    }

    @Test
    public void aggregate_aJunctionWhoseTargetIsGone_isLeftOut() throws Exception {
        // arrange
        final Path temp = Files.createTempDirectory("ai-index-test");
        final Path outputRoot = temp.resolve("ai");
        writePackageFile(outputRoot.resolve("main/a/package.ai.md"), "main/a", "> A package.\n");
        final Path target = Files.createDirectory(temp.resolve("target"));
        CommonTestFixtures.junction(outputRoot.resolve("main/gone"), target);
        Files.delete(target);
        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IndexOutcome written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(IndexOutcome.WRITTEN));
    }

    @Test
    public void aggregate_noPackages_writesNothing() throws Exception {
        // arrange: the output root exists but holds no package.ai.md
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        Files.createDirectories(outputRoot);

        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        final IndexOutcome written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(IndexOutcome.UNCHANGED));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="incrementality">
    @Test
    public void aggregate_unchanged_isNotRewritten() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        // first run writes the file
        assertThat(
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot),
                is(IndexOutcome.WRITTEN));

        // act: a second run over the same packages must detect no change
        final IndexOutcome second =
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot);

        // assert
        assertThat(second, is(IndexOutcome.UNCHANGED));
    }

    @Test
    public void aggregate_force_rewritesEvenWhenUnchanged() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot);

        // act: force=true rewrites despite no content change
        final IndexOutcome forced =
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", true, new RegenerateOn()).aggregate(outputRoot);

        // assert
        assertThat(forced, is(IndexOutcome.WRITTEN));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="edge cases: missing lead, root package">
    @Test
    public void aggregate_packageWithoutLead_listsPathWithoutLeadSeparator() throws Exception {
        // arrange: a package whose body is blank yields no lead
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("main/java/com/example/package.ai.md"), "main/java/com/example", "");

        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        indexer.aggregate(outputRoot);

        // assert: the header links the package; the body lists the bare path with no lead separator
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(
                document.header().children(),
                is(equalTo(Arrays.asList("[main/java/com/example](main/java/com/example/package.ai.md)"))));
        assertThat(document.body(), containsString("- main/java/com/example\n"));
        assertThat(document.body(), not(containsString(" — ")));
    }

    @Test
    public void aggregate_rootPackage_isListedAsDot() throws Exception {
        // arrange: a package.ai.md directly in the output root (the recursive root aggregate)
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("package.ai.md"), "ai", "> The whole project.\n");

        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());

        // act
        indexer.aggregate(outputRoot);

        // assert: the root package shows the "." display path in the body and a self link in the header
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.header().children(), is(equalTo(Arrays.asList("[.](package.ai.md)"))));
        assertThat(document.body(), containsString("- . — The whole project."));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="optional AI overview">
    @Test
    public void aggregate_overviewEnabled_writesOverviewSectionAboveTheListing() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        // act
        final IndexOutcome written = overviewIndexer(false).aggregate(outputRoot);

        // assert: an #### Overview section with the generated text precedes the deterministic listing
        assertThat(written, is(IndexOutcome.WRITTEN));
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        final String body = document.body();
        assertThat(body, containsString(OVERVIEW_HEADING));
        assertThat(body, containsString("Mock summary for project.ai.md"));
        assertThat(body, containsString("#### Packages"));
        assertThat(body, containsString("- main/java/com/example — Example package."));
        assertThat(body.indexOf(OVERVIEW_HEADING) < body.indexOf("#### Packages"), is(true));
    }

    @Test
    public void aggregate_overviewDisabled_hasNoOverviewSection() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        // act: the deterministic constructor performs no AI call
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot);

        // assert
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), not(containsString(OVERVIEW_HEADING)));
    }

    @Test
    public void aggregate_overviewEnabled_unchangedProjectIsNotReInferred() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        // first run writes the overview
        assertThat(overviewIndexer(false).aggregate(outputRoot), is(IndexOutcome.WRITTEN));

        // act: a second run over the same packages and same overview config must detect no change
        // (the header records the overview's prompt and model, not the AI output)
        final IndexOutcome second = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(second, is(IndexOutcome.UNCHANGED));
    }

    @Test
    public void aggregate_enablingOverview_rewritesPreviouslyDeterministicIndex() throws Exception {
        // arrange: a deterministic project index already exists
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");
        assertThat(
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot),
                is(IndexOutcome.WRITTEN));

        // act: enabling the overview records a prompt and a model hash where there were none -> regenerate
        final IndexOutcome rewritten = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(rewritten, is(IndexOutcome.WRITTEN));
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), containsString(OVERVIEW_HEADING));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="regenerateOn: children, prompt, model">
    private static RegenerateOn childrenOff() {
        final RegenerateOn on = new RegenerateOn();
        on.setChildren(false);
        return on;
    }

    @Test
    public void aggregate_aChangedPackageLead_rewritesTheProjectWhileChildrenIsOn() throws Exception {
        // arrange: the same packages, the second time with another lead
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        final Path packageFile = outputRoot.resolve("main/java/com/example/package.ai.md");
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n");
        final ProjectIndexer indexer = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn());
        assertThat(indexer.aggregate(outputRoot), is(IndexOutcome.WRITTEN));
        final String firstK = documentCodec
                .read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME))
                .header()
                .childrenHash();
        writePackageFile(packageFile, "main/java/com/example", "> Another lead.\n");

        // act
        final IndexOutcome frozen =
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, childrenOff()).aggregate(outputRoot);
        final IndexOutcome rewritten = indexer.aggregate(outputRoot);

        // assert
        assertThat(firstK.isEmpty(), is(false));
        assertThat(frozen, is(IndexOutcome.UNCHANGED));
        assertThat(rewritten, is(IndexOutcome.WRITTEN));
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), containsString("Another lead."));
        assertThat(document.header().childrenHash().equals(firstK), is(false));
    }

    @Test
    public void aggregate_aChangedPackageChecksum_rewritesTheProjectAlsoWithChildrenOff() throws Exception {
        // arrange: the same package and lead, the second time over another source checksum
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        final Path packageFile = outputRoot.resolve("main/java/com/example/package.ai.md");
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n", "AAAAAAAA");
        final ProjectIndexer frozenChildren = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, childrenOff());
        assertThat(frozenChildren.aggregate(outputRoot), is(IndexOutcome.WRITTEN));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        final String firstC = documentCodec.read(projectFile).header().checksum();
        final IndexOutcome unchanged = frozenChildren.aggregate(outputRoot);
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n", "BBBBBBBB");

        // act
        final IndexOutcome rewritten = frozenChildren.aggregate(outputRoot);

        // assert
        assertThat(unchanged, is(IndexOutcome.UNCHANGED));
        assertThat(rewritten, is(IndexOutcome.WRITTEN));
        assertThat(documentCodec.read(projectFile).header().checksum().equals(firstC), is(false));
    }

    @Test
    public void aggregate_overviewVersusDeterministicIndex_onlyTheOverviewRecordsItsPromptAndModel() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"), "main/java/com/example", "> Example.\n");
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);

        // act
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new RegenerateOn()).aggregate(outputRoot);
        final AiMdHeader deterministic = documentCodec.read(projectFile).header();
        overviewIndexer(false).aggregate(outputRoot);
        final AiMdHeader withOverview = documentCodec.read(projectFile).header();

        // assert
        assertThat(deterministic.promptHash(), is(""));
        assertThat(deterministic.modelHash(), is(""));
        assertThat(withOverview.promptHash().isEmpty(), is(false));
        assertThat(withOverview.modelHash().isEmpty(), is(false));
        assertThat(withOverview.checksum(), is(deterministic.checksum()));
        assertThat(withOverview.childrenHash(), is(deterministic.childrenHash()));
    }
    // </editor-fold>

    /**
     * Markdown heading the overview section emits; duplicated here from the (private) constant in
     * {@code ProjectIndexer} so the tests assert on the literal contract rather than the symbol.
     */
    private static final String OVERVIEW_HEADING = "#### Overview";

    /**
     * Builds a {@link ProjectIndexer} with the AI overview enabled, backed by the deterministic
     * {@link MockGenerationProvider} (which returns {@code "Mock summary for project.ai.md"}).
     *
     * @param force the force flag to pass through
     * @return an overview-enabled indexer
     */
    private ProjectIndexer overviewIndexer(final boolean force) {
        return overviewIndexer(force, new MockGenerationProvider());
    }

    private ProjectIndexer overviewIndexer(final boolean force, final GenerationProvider provider) {
        final RoutingRule overview = new RoutingRule();
        overview.setPromptKey("project-body");
        overview.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);

        final PromptDefinition prompt = new PromptDefinition();
        prompt.setKey("project-body");
        prompt.setTemplate("Write a project overview.\nProject: %s\nLeads:\n%s\n");
        final PromptCatalog promptSupport = new PromptCatalog(Collections.singletonList(prompt));

        return new ProjectIndexer(
                PROJECT_TITLE,
                "1.0.0",
                "0.0.0",
                force,
                new RegenerateOn(),
                new ProjectOverview(
                        provider, overview, promptSupport, CommonTestFixtures.createDefaultAiModelDefinitionSupport()));
    }

    private void writePackageFile(final Path file, final String title, final String body) throws IOException {
        writePackageFile(file, title, body, "AAAAAAAA");
    }

    private void writePackageFile(final Path file, final String title, final String body, final String checksum)
            throws IOException {
        Files.createDirectories(file.getParent());
        final AiMdHeader header = new AiMdHeader(
                title,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                checksum,
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE);
        documentCodec.write(file, new AiMdDocument(header, body));
    }

    // <editor-fold defaultstate="collapsed" desc="incomplete answers">
    @Test
    public void aggregate_overviewWithAnIncompleteAnswer_writesNoProjectIndex() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        // act
        final IndexOutcome outcome = overviewIndexer(false, request -> "").aggregate(outputRoot);

        // assert
        assertThat(outcome, is(IndexOutcome.FAILED));
        assertThat(Files.exists(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME)), is(false));
    }

    @Test
    public void aggregate_upToDateIndexWithoutItsOverview_isWrittenAgain() throws Exception {
        // arrange: what 1.3.0 wrote after an empty overview answer, the same header without the overview
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");
        assertThat(overviewIndexer(false).aggregate(outputRoot), is(IndexOutcome.WRITTEN));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        final AiMdDocument complete = documentCodec.read(projectFile);
        final String body = complete.body();
        documentCodec.write(
                projectFile,
                new AiMdDocument(
                        complete.header(),
                        body.substring(0, body.indexOf(OVERVIEW_HEADING))
                                + body.substring(body.indexOf("#### Packages"))));

        // act
        final IndexOutcome outcome = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(outcome, is(IndexOutcome.WRITTEN));
        assertThat(documentCodec.read(projectFile).body(), containsString(OVERVIEW_HEADING));
    }
    // </editor-fold>
}

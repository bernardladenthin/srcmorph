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
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiPromptDefinition;
import net.ladenthin.srcmorph.document.AiMdDocument;
import net.ladenthin.srcmorph.document.AiMdDocumentCodec;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.MockAiGenerationProvider;
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

        final ProjectIndexer indexer = new ProjectIndexer(
                PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn());

        // act
        final AiIndexOutcome written = indexer.aggregate(outputRoot);

        // pre-assert
        assertThat(written, is(AiIndexOutcome.WRITTEN));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        assertThat(Files.exists(projectFile), is(true));

        // assert: header
        final AiMdDocument document = documentCodec.read(projectFile);
        assertThat(document, is(notNullValue()));
        assertThat(document.header().title(), is(equalTo(PROJECT_TITLE)));
        assertThat(document.header().x(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_PROJECT)));
        assertThat(document.header().g(), is(equalTo("1.0.0")));
        assertThat(document.header().a(), is(equalTo("0.0.0")));
        assertThat(document.header().c().trim().isEmpty(), is(false));
        assertThat(document.header().d().trim().isEmpty(), is(false));
        assertThat(document.header().t().trim().isEmpty(), is(false));

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
    public void aggregate_noPackages_writesNothing() throws Exception {
        // arrange: the output root exists but holds no package.ai.md
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        Files.createDirectories(outputRoot);

        final ProjectIndexer indexer = new ProjectIndexer(
                PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn());

        // act
        final AiIndexOutcome written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(AiIndexOutcome.UNCHANGED));
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
                new ProjectIndexer(
                                PROJECT_TITLE,
                                "1.0.0",
                                "0.0.0",
                                false,
                                new net.ladenthin.srcmorph.config.AiRegenerateOn())
                        .aggregate(outputRoot),
                is(AiIndexOutcome.WRITTEN));

        // act: a second run over the same packages must detect no change
        final AiIndexOutcome second = new ProjectIndexer(
                        PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(outputRoot);

        // assert
        assertThat(second, is(AiIndexOutcome.UNCHANGED));
    }

    @Test
    public void aggregate_force_rewritesEvenWhenUnchanged() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(outputRoot);

        // act: force=true rewrites despite no content change
        final AiIndexOutcome forced = new ProjectIndexer(
                        PROJECT_TITLE, "1.0.0", "0.0.0", true, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(outputRoot);

        // assert
        assertThat(forced, is(AiIndexOutcome.WRITTEN));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="edge cases: missing lead, root package">
    @Test
    public void aggregate_packageWithoutLead_listsPathWithoutLeadSeparator() throws Exception {
        // arrange: a package whose body is blank yields no lead
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("main/java/com/example/package.ai.md"), "main/java/com/example", "");

        final ProjectIndexer indexer = new ProjectIndexer(
                PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn());

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

        final ProjectIndexer indexer = new ProjectIndexer(
                PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn());

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
        final AiIndexOutcome written = overviewIndexer(false).aggregate(outputRoot);

        // assert: an #### Overview section with the generated text precedes the deterministic listing
        assertThat(written, is(AiIndexOutcome.WRITTEN));
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
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(outputRoot);

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
        assertThat(overviewIndexer(false).aggregate(outputRoot), is(AiIndexOutcome.WRITTEN));

        // act: a second run over the same packages and same overview config must detect no change
        // (the header records the overview's prompt and model, not the AI output)
        final AiIndexOutcome second = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(second, is(AiIndexOutcome.UNCHANGED));
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
                new ProjectIndexer(
                                PROJECT_TITLE,
                                "1.0.0",
                                "0.0.0",
                                false,
                                new net.ladenthin.srcmorph.config.AiRegenerateOn())
                        .aggregate(outputRoot),
                is(AiIndexOutcome.WRITTEN));

        // act: enabling the overview records a prompt and a model hash where there were none -> regenerate
        final AiIndexOutcome rewritten = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(rewritten, is(AiIndexOutcome.WRITTEN));
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), containsString(OVERVIEW_HEADING));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="regenerateOn: children, prompt, model">
    private static net.ladenthin.srcmorph.config.AiRegenerateOn childrenOff() {
        final net.ladenthin.srcmorph.config.AiRegenerateOn on = new net.ladenthin.srcmorph.config.AiRegenerateOn();
        on.setChildren(false);
        return on;
    }

    @Test
    public void aggregate_aChangedPackageLead_rewritesTheProjectWhileChildrenIsOn() throws Exception {
        // arrange: the same packages, the second time with another lead
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        final Path packageFile = outputRoot.resolve("main/java/com/example/package.ai.md");
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n");
        final ProjectIndexer indexer = new ProjectIndexer(
                PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn());
        assertThat(indexer.aggregate(outputRoot), is(AiIndexOutcome.WRITTEN));
        final String firstK = documentCodec
                .read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME))
                .header()
                .k();
        writePackageFile(packageFile, "main/java/com/example", "> Another lead.\n");

        // act
        final AiIndexOutcome frozen =
                new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, childrenOff()).aggregate(outputRoot);
        final AiIndexOutcome rewritten = indexer.aggregate(outputRoot);

        // assert
        assertThat(firstK.isEmpty(), is(false));
        assertThat(frozen, is(AiIndexOutcome.UNCHANGED));
        assertThat(rewritten, is(AiIndexOutcome.WRITTEN));
        final AiMdDocument document = documentCodec.read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME));
        assertThat(document.body(), containsString("Another lead."));
        assertThat(document.header().k().equals(firstK), is(false));
    }

    @Test
    public void aggregate_aChangedPackageChecksum_rewritesTheProjectAlsoWithChildrenOff() throws Exception {
        // arrange: the same package and lead, the second time over another source checksum
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        final Path packageFile = outputRoot.resolve("main/java/com/example/package.ai.md");
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n", "AAAAAAAA");
        final ProjectIndexer frozenChildren = new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, childrenOff());
        assertThat(frozenChildren.aggregate(outputRoot), is(AiIndexOutcome.WRITTEN));
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);
        final String firstC = documentCodec.read(projectFile).header().c();
        final AiIndexOutcome unchanged = frozenChildren.aggregate(outputRoot);
        writePackageFile(packageFile, "main/java/com/example", "> Example package.\n", "BBBBBBBB");

        // act
        final AiIndexOutcome rewritten = frozenChildren.aggregate(outputRoot);

        // assert
        assertThat(unchanged, is(AiIndexOutcome.UNCHANGED));
        assertThat(rewritten, is(AiIndexOutcome.WRITTEN));
        assertThat(documentCodec.read(projectFile).header().c().equals(firstC), is(false));
    }

    @Test
    public void aggregate_overview_recordsItsPromptAndModel_theDeterministicIndexNeither() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"), "main/java/com/example", "> Example.\n");
        final Path projectFile = outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME);

        // act
        new ProjectIndexer(PROJECT_TITLE, "1.0.0", "0.0.0", false, new net.ladenthin.srcmorph.config.AiRegenerateOn())
                .aggregate(outputRoot);
        final AiMdHeader deterministic = documentCodec.read(projectFile).header();
        overviewIndexer(false).aggregate(outputRoot);
        final AiMdHeader withOverview = documentCodec.read(projectFile).header();

        // assert
        assertThat(deterministic.p(), is(""));
        assertThat(deterministic.m(), is(""));
        assertThat(withOverview.p().isEmpty(), is(false));
        assertThat(withOverview.m().isEmpty(), is(false));
        assertThat(withOverview.c(), is(deterministic.c()));
        assertThat(withOverview.k(), is(deterministic.k()));
    }
    // </editor-fold>

    /**
     * Markdown heading the overview section emits; duplicated here from the (private) constant in
     * {@code ProjectIndexer} so the tests assert on the literal contract rather than the symbol.
     */
    private static final String OVERVIEW_HEADING = "#### Overview";

    /**
     * Builds a {@link ProjectIndexer} with the AI overview enabled, backed by the deterministic
     * {@link MockAiGenerationProvider} (which returns {@code "Mock summary for project.ai.md"}).
     *
     * @param force the force flag to pass through
     * @return an overview-enabled indexer
     */
    private ProjectIndexer overviewIndexer(final boolean force) {
        return overviewIndexer(force, new MockAiGenerationProvider());
    }

    private ProjectIndexer overviewIndexer(final boolean force, final AiGenerationProvider provider) {
        final AiFieldGenerationConfig overview = new AiFieldGenerationConfig();
        overview.setPromptKey("project-body");
        overview.setAiDefinitionKey(CommonTestFixtures.AI_DEFINITION_KEY_DEFAULT);

        final AiPromptDefinition prompt = new AiPromptDefinition();
        prompt.setKey("project-body");
        prompt.setTemplate("Write a project overview.\nProject: %s\nLeads:\n%s\n");
        final AiPromptSupport promptSupport = new AiPromptSupport(Collections.singletonList(prompt));

        return new ProjectIndexer(
                PROJECT_TITLE,
                "1.0.0",
                "0.0.0",
                force,
                new net.ladenthin.srcmorph.config.AiRegenerateOn(),
                provider,
                overview,
                promptSupport,
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());
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
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");

        final AiIndexOutcome outcome = overviewIndexer(false, request -> "").aggregate(outputRoot);

        assertThat(outcome, is(AiIndexOutcome.FAILED));
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
        assertThat(overviewIndexer(false).aggregate(outputRoot), is(AiIndexOutcome.WRITTEN));
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
        final AiIndexOutcome outcome = overviewIndexer(false).aggregate(outputRoot);

        // assert
        assertThat(outcome, is(AiIndexOutcome.WRITTEN));
        assertThat(documentCodec.read(projectFile).body(), containsString(OVERVIEW_HEADING));
    }
    // </editor-fold>
}

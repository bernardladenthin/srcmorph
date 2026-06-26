// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec;
import org.apache.maven.plugin.logging.SystemStreamLog;
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

        final ProjectIndexer indexer =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false);

        // act
        final int written = indexer.aggregate(outputRoot);

        // pre-assert
        assertThat(written, is(equalTo(1)));
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

        // assert: body harvests each package's lead and links to its package.ai.md
        final String body = document.body();
        assertThat(body, containsString("#### Packages"));
        assertThat(body, containsString("Calculates VAT for invoices."));
        assertThat(body, containsString("Creates and sends invoices."));
        assertThat(body, containsString("(main/java/com/example/fiscal/package.ai.md)"));
        assertThat(body, containsString("(main/java/com/example/billing/package.ai.md)"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="no packages">
    @Test
    public void aggregate_noPackages_writesNothing() throws Exception {
        // arrange: the output root exists but holds no package.ai.md
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        Files.createDirectories(outputRoot);

        final ProjectIndexer indexer =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false);

        // act
        final int written = indexer.aggregate(outputRoot);

        // assert
        assertThat(written, is(equalTo(0)));
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
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false).aggregate(outputRoot),
                is(equalTo(1)));

        // act: a second run over the same packages must detect no change
        final int second =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false).aggregate(outputRoot);

        // assert
        assertThat(second, is(equalTo(0)));
    }

    @Test
    public void aggregate_force_rewritesEvenWhenUnchanged() throws Exception {
        // arrange
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(
                outputRoot.resolve("main/java/com/example/package.ai.md"),
                "main/java/com/example",
                "> Example package.\n");
        new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false).aggregate(outputRoot);

        // act: force=true rewrites despite no content change
        final int forced =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", true).aggregate(outputRoot);

        // assert
        assertThat(forced, is(equalTo(1)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="edge cases: missing lead, root package">
    @Test
    public void aggregate_packageWithoutLead_listsLinkWithoutSeparator() throws Exception {
        // arrange: a package whose body is blank yields no lead
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("main/java/com/example/package.ai.md"), "main/java/com/example", "");

        final ProjectIndexer indexer =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false);

        // act
        indexer.aggregate(outputRoot);

        // assert: the listing links the package but carries no lead separator
        final String body = documentCodec
                .read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME))
                .body();
        assertThat(body, containsString("(main/java/com/example/package.ai.md)"));
        assertThat(body, not(containsString(" — ")));
    }

    @Test
    public void aggregate_rootPackage_isListedAsDot() throws Exception {
        // arrange: a package.ai.md directly in the output root (the recursive root aggregate)
        final Path outputRoot = Files.createTempDirectory("ai-index-test").resolve("ai");
        writePackageFile(outputRoot.resolve("package.ai.md"), "ai", "> The whole project.\n");

        final ProjectIndexer indexer =
                new ProjectIndexer(new SystemStreamLog(), PROJECT_TITLE, "1.0.0", "0.0.0", false);

        // act
        indexer.aggregate(outputRoot);

        // assert: the root package is shown with the "." display path and a self link
        final String body = documentCodec
                .read(outputRoot.resolve(AiMdHeaderCodec.PROJECT_AI_MD_FILENAME))
                .body();
        assertThat(body, containsString("[.](package.ai.md)"));
        assertThat(body, containsString("The whole project."));
    }
    // </editor-fold>

    private void writePackageFile(final Path file, final String title, final String body) throws IOException {
        Files.createDirectories(file.getParent());
        final AiMdHeader header = new AiMdHeader(
                title,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE);
        documentCodec.write(file, new AiMdDocument(header, body));
    }
}

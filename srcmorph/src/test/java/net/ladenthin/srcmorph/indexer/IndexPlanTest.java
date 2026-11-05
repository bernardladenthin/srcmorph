// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ladenthin.srcmorph.config.RoutingRule;
import org.junit.jupiter.api.Test;

public class IndexPlanTest {

    private static RoutingRule rule(final String promptKey) {
        final RoutingRule config = new RoutingRule();
        config.setPromptKey(promptKey);
        return config;
    }

    private static RoutingRule rule(final String id, final String promptKey) {
        final RoutingRule config = new RoutingRule();
        config.setId(id);
        config.setPromptKey(promptKey);
        return config;
    }

    // <editor-fold defaultstate="collapsed" desc="sentTokens">
    /** A file over its window sends what its strategy reads, not its source: 1 000 tokens, a window 250. */
    @Test
    public void sentTokens_routesOverTheirWindow_followTheOversizeStrategy() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        for (final String strategy : new String[] {"fail", "sample", "deterministic", "mapReduce", "sampleChunks"}) {
            final RoutingRule rule = rule(strategy, "p");
            rule.setOnOversize(strategy);
            rule.setMaxChunks("sampleChunks".equals(strategy) ? 2 : 0);
            plan.addRoute("m", Paths.get(strategy + ".sql"), rule, 1, 40_000L, 10_000L, 1_000L);
        }
        plan.addRoute("m", Paths.get("fits.sql"), rule("p"), 1, 4_000L, 10_000L, 100L);
        plan.addRoute("m", Paths.get("unchecked.sql"), rule("p"), 1);

        // act
        final List<Long> sent = new ArrayList<>();
        for (final IndexPlan.Route entry : plan.routesByModel().get("m")) {
            sent.add(IndexPlan.sentTokens(entry));
        }

        // assert
        assertThat(sent, is(equalTo(Arrays.asList(250L, 250L, 0L, 1_000L, 500L, 100L, 0L))));
        assertThat(plan.renderMarkdown(Paths.get("")), containsString(" ~5100 source tokens (~2100 sent), est. "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="routedCount">
    @Test
    public void routedCount_threeRoutesOverTwoModels_countsAndGroupsPerModel() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("a/Foo.java"), rule("p1"), 60);
        plan.addRoute("modelA", Paths.get("a/Bar.java"), rule("p1"), 120);
        plan.addRoute("modelB", Paths.get("b/Baz.java"), rule("p2"), 30);

        // act, assert
        assertThat(plan.routedCount(), is(equalTo(3)));
        assertThat(plan.routesByModel().get("modelA").size(), is(equalTo(2)));
        assertThat(plan.routesByModel().get("modelB").size(), is(equalTo(1)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="totalEstimatedSeconds">
    @Test
    public void totalEstimatedSeconds_threeRoutes_sumsAllEntries() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("a/Foo.java"), rule("p1"), 60);
        plan.addRoute("modelA", Paths.get("a/Bar.java"), rule("p1"), 120);
        plan.addRoute("modelB", Paths.get("b/Baz.java"), rule("p2"), 30);

        // act, assert
        assertThat(plan.totalEstimatedSeconds(), is(equalTo(210L)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="renderMarkdown">
    @Test
    public void renderMarkdown_routedSkippedAndUnmatchedFiles_containsModelSectionsFilesAndTotals() {
        // arrange
        final Path base = Paths.get("");
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Foo.java"), rule("java-small", "file-body-java"), 60);
        plan.addSkipped(Paths.get("Generated.java"));
        plan.addUnmatched(Paths.get("weird.txt"));

        // act
        final String md = plan.renderMarkdown(base);

        // assert
        assertThat(md, containsString("## AI index plan"));
        assertThat(md, containsString("**Total:**"));
        assertThat(md, containsString("### Model `modelA`"));
        assertThat(md, containsString("| File | Rule | Prompt | Window | Tokens | Est. |"));
        // a route without a window check has neither a window cell nor a token estimate
        assertThat(md, containsString("| file-body-java | - | - |"));
        assertThat(md, containsString("Foo.java"));
        assertThat(md, containsString("java-small")); // the rule id shows in its own column
        assertThat(md, containsString("file-body-java"));
        assertThat(md, containsString("Skipped (1)"));
        assertThat(md, containsString("Generated.java"));
        assertThat(md, containsString("Unmatched"));
        assertThat(md, containsString("weird.txt"));
    }

    /** A calibrated model's times are its own; the others' are the built-in estimate, and the header says which. */
    @Test
    public void renderMarkdown_calibratedAndUncalibratedModel_namesTheBasisOfEachModelsTime() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("local", Paths.get("A.java"), rule("p1"), 600);
        plan.addRoute("remote", Paths.get("B.java"), rule("p1"), 9);
        plan.markCalibrated("local");

        // act
        final String md = plan.renderMarkdown(Paths.get(""));

        // assert
        assertThat(
                md,
                containsString(
                        "### Model `local` - 1 file(s), ~0 source tokens (~0 sent), est. ~10 min (from the model's calibration)\n"));
        assertThat(
                md,
                containsString(
                        "### Model `remote` - 1 file(s), ~0 source tokens (~0 sent), est. ~9 s (based on GPT-6 Luna)\n"));
    }

    @Test
    public void renderMarkdown_overWindowFile_flagsOverWindowFilesAndCells() {
        // arrange
        final Path base = Paths.get("");
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Small.java"), rule("r1", "p1"), 10, 1000L, 5000L);
        plan.addRoute("modelA", Paths.get("Huge.java"), rule("r1", "p1"), 20, 9000L, 5000L);

        // act
        final String md = plan.renderMarkdown(base);

        // assert
        assertThat(md, containsString("1 over window"));
        assertThat(md, containsString("ok")); // the fitting file's window cell
        assertThat(md, containsString("(!) 9000>5000")); // the over-window file's window cell
        assertThat(md, containsString("Over window"));
        assertThat(md, containsString("Huge.java -> model `modelA`"));
    }

    @Test
    public void renderMarkdown_nothingExceeds_noWindowSection() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Small.java"), rule("r1", "p1"), 10, 1000L, 5000L);

        // act
        final String md = plan.renderMarkdown(Paths.get(""));

        // assert
        assertThat(md, containsString("0 over window"));
        assertThat(md.contains("### (!) Over window"), is(false));
    }

    @Test
    public void renderMarkdown_windowCheckedRoutes_tokensPerFilePerModelAndPerDirectory() {
        // arrange
        final Path base = Paths.get("base");
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", base.resolve("a/A1.java"), rule("p1"), 10, 4_000L, 9_000L, 1_000L);
        plan.addRoute("modelA", base.resolve("a/A2.java"), rule("p1"), 10, 2_000L, 9_000L, 500L);
        plan.addRoute("modelB", base.resolve("b/c/B.java"), rule("p2"), 10, 800L, 9_000L, 200L);
        plan.addRoute("modelB", base.resolve("Root.java"), rule("p2"), 10, 40L, 9_000L, 10L);
        // no window check: no tokens, and no part of the sums
        plan.addRoute("modelB", base.resolve("b/c/Plain.java"), rule("p2"), 10);

        // act
        final String md = plan.renderMarkdown(base);

        // assert
        assertThat(
                md,
                containsString(
                        "### Model `modelA` - 2 file(s), ~1500 source tokens (~1500 sent), est. ~20 s (based on GPT-6 Luna)\n"));
        assertThat(
                md,
                containsString(
                        "### Model `modelB` - 3 file(s), ~210 source tokens (~210 sent), est. ~30 s (based on GPT-6 Luna)\n"));
        assertThat(md, containsString("| ~1000 |"));
        assertThat(
                md,
                containsString("### Source tokens by directory\n\n| Directory | Files | Tokens |\n|---|---|---|\n"
                        + "| . | 1 | ~10 |\n| a | 2 | ~1500 |\n| b/c | 1 | ~200 |\n"));
    }

    @Test
    public void renderMarkdown_noWindowCheckedRoute_noTokenSection() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Foo.java"), rule("p1"), 10);

        // act, assert
        assertThat(plan.renderMarkdown(Paths.get("")).contains("Source tokens by directory"), is(false));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="oversize rendering and path display">

    /**
     * Builds a rule whose {@code onOversize} strategy is set explicitly.
     *
     * @param id         the rule id
     * @param onOversize the configured strategy value
     * @return the rule
     */
    private static RoutingRule oversizeRule(final String id, final String onOversize) {
        final RoutingRule config = new RoutingRule();
        config.setId(id);
        config.setPromptKey("p1");
        config.setOnOversize(onOversize);
        return config;
    }

    /**
     * An over-window entry whose rule fails the build must be rendered as such, and must trigger the
     * closing "how to fix it" paragraph.
     */
    @Test
    public void renderMarkdown_oversizeWithFailStrategy_marksBuildFailureAndAddsAdvice() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Big.java"), oversizeRule("r1", "fail"), 60, 9000L, 1000L);

        // act
        final String rendered = plan.renderMarkdown(Paths.get(""));

        // assert
        assertThat(rendered, containsString("FAILS BUILD"));
        assertThat(rendered, containsString("The onOversize=fail entries fail the build."));
    }

    /**
     * The same entry with a non-failing strategy renders as handled and must NOT emit the advice
     * paragraph. Without this counterpart, a mutant hardcoding either branch survives.
     */
    @Test
    public void renderMarkdown_oversizeWithHandlingStrategy_marksHandledAndOmitsAdvice() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Big.java"), oversizeRule("r1", "sample"), 60, 9000L, 1000L);

        // act
        final String rendered = plan.renderMarkdown(Paths.get(""));

        // assert
        assertThat(rendered, containsString("handled"));
        assertThat(rendered, not(containsString("FAILS BUILD")));
        assertThat(rendered, not(containsString("The onOversize=fail entries fail the build.")));
    }

    /**
     * A plan with no over-window entry at all must not emit the advice paragraph either -- this is the
     * zero case for the {@code windowFailCount() > 0} guard, distinct from the "handled" case above.
     */
    @Test
    public void renderMarkdown_noOversizeEntries_omitsAdvice() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Small.java"), rule("r1", "p1"), 60, 100L, 1000L);

        // act
        final String rendered = plan.renderMarkdown(Paths.get(""));

        // assert
        assertThat(rendered, not(containsString("The onOversize=fail entries fail the build.")));
    }

    /**
     * The per-entry window cell distinguishes three states, and each must be rendered distinctly:
     * unchecked, fits, exceeds.
     */
    @Test
    public void renderMarkdown_windowCell_rendersAllThreeStates() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Unchecked.java"), rule("r1", "p1"), 60);
        plan.addRoute("modelA", Paths.get("Fits.java"), rule("r2", "p1"), 60, 100L, 1000L);
        plan.addRoute("modelA", Paths.get("Exceeds.java"), oversizeRule("r3", "sample"), 60, 9000L, 1000L);

        // act
        final String rendered = plan.renderMarkdown(Paths.get(""));

        // assert -- match the whole table cell, not the bare word: "ok" also occurs in the prose
        // around the table, so a substring check would pass even against an empty window cell.
        assertThat(rendered, containsString("| ok |"));
        assertThat(rendered, containsString("| (!) 9000>1000"));
        assertThat(rendered, containsString("| p1 | - | - |"));
    }

    /**
     * A file that cannot be relativized against the base directory falls back to its own path rather
     * than throwing. {@code Path.relativize} raises {@link IllegalArgumentException} when one path is
     * absolute and the other relative, which is exactly what this constructs.
     */
    @Test
    public void renderMarkdown_fileNotRelativizableAgainstBase_fallsBackToTheFullPath() {
        // arrange -- absolute base, relative file: relativize cannot bridge the two
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("relative/Foo.java"), rule("r1", "p1"), 60);

        // act
        final String rendered = plan.renderMarkdown(Paths.get("/absolute/base").toAbsolutePath());

        // assert
        assertThat(rendered, containsString("relative/Foo.java"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="windowExceededCount">
    @Test
    public void windowExceededCount_mixedEntries_countsOnlyOverWindowEntries() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        // fits: source 1000 <= budget 5000
        plan.addRoute("modelA", Paths.get("Small.java"), rule("p1"), 10, 1000L, 5000L);
        // exceeds: source 9000 > budget 5000
        plan.addRoute("modelA", Paths.get("Huge.java"), rule("p1"), 20, 9000L, 5000L);
        // unchecked (4-arg) never counts as over-window
        plan.addRoute("modelB", Paths.get("Plain.java"), rule("p2"), 5);

        // act, assert
        assertThat(plan.windowExceededCount(), is(equalTo(1)));
        assertThat(plan.routesByModel().get("modelA").get(0).exceedsWindow(), is(false));
        assertThat(plan.routesByModel().get("modelA").get(1).exceedsWindow(), is(true));
        assertThat(plan.routesByModel().get("modelB").get(0).windowChecked(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="exceedsWindow">
    @Test
    public void exceedsWindow_aSourceThatFillsTheWindowExactly_fits() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Exact.java"), rule("p1"), 10, 5000L, 5000L);
        plan.addRoute("modelA", Paths.get("OneMore.java"), rule("p1"), 10, 5001L, 5000L);

        // act, assert
        assertThat(plan.routesByModel().get("modelA").get(0).exceedsWindow(), is(false));
        assertThat(plan.routesByModel().get("modelA").get(1).exceedsWindow(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addRoute">
    @Test
    public void addRoute_withoutTokens_estimatesThemAtTheDefaultRatio() {
        // arrange
        final IndexPlan plan = new IndexPlan();
        plan.addRoute("modelA", Paths.get("Foo.java"), rule("p1"), 10, 41L, 9_000L);

        // act, assert
        // 41 characters at 4.0 per token are 10.25, rounded up
        assertThat(plan.routesByModel().get("modelA").get(0).sourceTokens(), is(11L));
        assertThat(IndexPlan.estimatedTokens(48L, 4.8d), is(10L));
        assertThat(IndexPlan.estimatedTokens(0L, 3.0d), is(0L));
    }
    // </editor-fold>
}

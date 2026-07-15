// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AiConditionEvaluatorTest {

    private final AiConditionEvaluator evaluator = new AiConditionEvaluator();

    private static AiFileContext ctx(
            final String name, final String path, final long size, final int lines, final long mtime) {
        return new AiFileContext(name, path, size, lines, mtime);
    }

    private static AiFileContext javaCtx() {
        return ctx("Foo.java", "src/Foo.java", 500L, 10, 1000L);
    }

    private static AiCondition ext(final String... e) {
        final AiCondition c = new AiCondition();
        c.setExtensions(Arrays.asList(e));
        return c;
    }

    private static AiCondition size(final long min, final long max) {
        final AiCondition c = new AiCondition();
        final AiRangeCondition r = new AiRangeCondition();
        r.setMin(min);
        r.setMax(max);
        c.setSize(r);
        return c;
    }

    private static AiCondition lines(final long min, final long max) {
        final AiCondition c = new AiCondition();
        final AiRangeCondition r = new AiRangeCondition();
        r.setMin(min);
        r.setMax(max);
        c.setLines(r);
        return c;
    }

    private static AiConditionGroup group(final AiCondition... kids) {
        final AiConditionGroup g = new AiConditionGroup();
        g.setConditions(Arrays.asList(kids));
        return g;
    }

    private static AiCondition and(final AiCondition... kids) {
        final AiCondition c = new AiCondition();
        c.setAnd(group(kids));
        return c;
    }

    private static AiCondition or(final AiCondition... kids) {
        final AiCondition c = new AiCondition();
        c.setOr(group(kids));
        return c;
    }

    private static AiCondition not(final AiCondition kid) {
        final AiCondition c = new AiCondition();
        c.setNot(kid);
        return c;
    }

    // <editor-fold defaultstate="collapsed" desc="matches: leaves">
    @Test
    public void matches_extension() {
        assertThat(evaluator.matches(ext(".java", ".kt"), javaCtx()), is(true));
        assertThat(evaluator.matches(ext(".sql"), javaCtx()), is(false));
    }

    @Test
    public void matches_size() {
        assertThat(evaluator.matches(size(0L, 1000L), javaCtx()), is(true)); // 500 <= 1000
        assertThat(evaluator.matches(size(1000L, 0L), javaCtx()), is(false)); // 500 not > 1000
    }

    @Test
    public void matches_lines() {
        assertThat(evaluator.matches(lines(0L, 10L), javaCtx()), is(true)); // 10 <= 10
        assertThat(evaluator.matches(lines(10L, 0L), javaCtx()), is(false)); // 10 not > 10
    }

    @Test
    public void matches_modifiedAfter() {
        // instant 1970-01-01T00:00:01Z = 1000ms
        final AiCondition after = new AiCondition();
        after.setModifiedAfter("1970-01-01T00:00:01Z");
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 2000L)), is(true));
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 500L)), is(false));
        // exactly equal -> strictly-after is false (pins the '>' boundary)
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 1000L)), is(false));
    }

    @Test
    public void matches_modifiedBefore() {
        final AiCondition before = new AiCondition();
        before.setModifiedBefore("1970-01-01T00:00:01Z"); // 1000ms
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 500L)), is(true));
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 2000L)), is(false));
        // exactly equal -> strictly-before is false (pins the '<' boundary)
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 1000L)), is(false));
    }

    @Test
    public void matches_pathGlob() {
        final AiCondition glob = new AiCondition();
        glob.setPathGlob("**/generated/**");
        assertThat(evaluator.matches(glob, ctx("X.java", "src/generated/X.java", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("X.java", "src/main/X.java", 1L, 0, 0L)), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: and / or / not / nested">
    @Test
    public void matches_and_allTrue_oneFalse() {
        assertThat(evaluator.matches(and(ext(".java"), size(0L, 1000L)), javaCtx()), is(true));
        assertThat(evaluator.matches(and(ext(".java"), size(1000L, 0L)), javaCtx()), is(false));
    }

    @Test
    public void matches_or_oneTrue_allFalse() {
        assertThat(evaluator.matches(or(ext(".sql"), size(0L, 1000L)), javaCtx()), is(true));
        assertThat(evaluator.matches(or(ext(".sql"), size(1000L, 0L)), javaCtx()), is(false));
    }

    @Test
    public void matches_not() {
        assertThat(evaluator.matches(not(ext(".sql")), javaCtx()), is(true));
        assertThat(evaluator.matches(not(ext(".java")), javaCtx()), is(false));
    }

    @Test
    public void matches_nested() {
        // .java AND (>=big OR <=small) AND NOT generated
        final AiCondition glob = new AiCondition();
        glob.setPathGlob("**/generated/**");
        final AiCondition nested = and(ext(".java"), or(size(0L, 1000L), size(9999L, 0L)), not(glob));
        assertThat(evaluator.matches(nested, javaCtx()), is(true));
        assertThat(evaluator.matches(nested, ctx("X.java", "src/generated/X.java", 500L, 0, 0L)), is(false));
    }

    @Test
    public void matches_groupWithNullConditions_isVacuous() {
        // a group whose <conditions> list is null: AND is vacuously true, OR vacuously false
        final AiCondition andNull = new AiCondition();
        andNull.setAnd(new AiConditionGroup());
        assertThat(evaluator.matches(andNull, javaCtx()), is(true));
        final AiCondition orNull = new AiCondition();
        orNull.setOr(new AiConditionGroup());
        assertThat(evaluator.matches(orNull, javaCtx()), is(false));
        // and usesLines tolerates a null-conditions group
        assertThat(evaluator.usesLines(andNull), is(false));
        assertThat(evaluator.usesLines(orNull), is(false));
    }

    @Test
    public void matches_emptyNode_throws() {
        assertThrows(IllegalStateException.class, () -> evaluator.matches(new AiCondition(), javaCtx()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    @Test
    public void validate_validLeavesAndTree() {
        // every leaf/combinator must pass validation as the single branch of a node — this also pins the
        // branchCount() count++ for each kind (or / lines / modifiedBefore included).
        assertDoesNotThrow(() -> evaluator.validate(ext(".java")));
        assertDoesNotThrow(() -> evaluator.validate(size(0L, 1000L)));
        assertDoesNotThrow(() -> evaluator.validate(lines(0L, 100L)));
        assertDoesNotThrow(() -> evaluator.validate(and(ext(".java"), not(size(1000L, 0L)))));
        assertDoesNotThrow(() -> evaluator.validate(or(ext(".java"), size(0L, 100L))));
        final AiCondition after = new AiCondition();
        after.setModifiedAfter("2026-01-01T00:00:00Z");
        assertDoesNotThrow(() -> evaluator.validate(after));
        final AiCondition before = new AiCondition();
        before.setModifiedBefore("2026-01-01T00:00:00Z");
        assertDoesNotThrow(() -> evaluator.validate(before));
        final AiCondition glob = new AiCondition();
        glob.setPathGlob("**/x/**");
        assertDoesNotThrow(() -> evaluator.validate(glob));
    }

    @Test
    public void validate_zeroBranches_throws() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(new AiCondition()));
    }

    @Test
    public void validate_twoBranches_throws() {
        final AiCondition c = ext(".java");
        c.setPathGlob("**/x/**"); // now two branches set
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    @Test
    public void validate_emptyAnd_throws() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(and()));
    }

    @Test
    public void validate_emptyOr_throws() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(or()));
    }

    @Test
    public void validate_recursesIntoChildren() {
        final AiCondition badChild = ext(".java");
        badChild.setSize(new AiRangeCondition()); // two branches in the child
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(and(badChild)));
        // recursion must reach OR children and NOT children too
        final AiCondition badOrChild = ext(".java");
        badOrChild.setSize(new AiRangeCondition());
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(or(badOrChild)));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(not(new AiCondition())));
    }

    @Test
    public void validate_emptyExtensions_throws() {
        final AiCondition c = new AiCondition();
        c.setExtensions(Arrays.<String>asList());
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    @Test
    public void validate_unboundedRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(size(0L, 0L)));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(lines(0L, 0L)));
    }

    @Test
    public void validate_invalidDate_throws() {
        final AiCondition after = new AiCondition();
        after.setModifiedAfter("not-a-date");
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(after));
        final AiCondition before = new AiCondition();
        before.setModifiedBefore("2026/01/01");
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(before));
    }

    @Test
    public void validate_blankGlob_throws() {
        final AiCondition c = new AiCondition();
        c.setPathGlob("   ");
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="usesLines">
    @Test
    public void usesLines_directAndNested() {
        assertThat(evaluator.usesLines(lines(0L, 100L)), is(true));
        assertThat(evaluator.usesLines(and(ext(".java"), lines(0L, 100L))), is(true));
        assertThat(evaluator.usesLines(or(ext(".java"), lines(0L, 100L))), is(true));
        assertThat(evaluator.usesLines(not(lines(0L, 100L))), is(true));
    }

    @Test
    public void usesLines_absent() {
        assertThat(evaluator.usesLines(ext(".java")), is(false));
        assertThat(evaluator.usesLines(and(ext(".java"), size(0L, 100L))), is(false));
        assertThat(evaluator.usesLines(or(ext(".java"), size(0L, 100L))), is(false));
        assertThat(evaluator.usesLines(not(ext(".java"))), is(false));
    }
    // </editor-fold>
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import net.ladenthin.srcmorph.config.AiCondition;
import net.ladenthin.srcmorph.config.AiConditionGroup;
import net.ladenthin.srcmorph.config.AiRangeCondition;
import net.ladenthin.srcmorph.support.ReadBudget;
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
        assertThat(evaluator.usesLines(content("x")), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileNames">
    private static AiCondition names(final String... names) {
        final AiCondition c = new AiCondition();
        c.setFileNames(Arrays.asList(names));
        return c;
    }

    @Test
    public void matches_fileNames_exactNameInAnyCase() {
        final AiCondition docker = names("Dockerfile", "Jenkinsfile");
        assertThat(evaluator.matches(docker, ctx("Dockerfile", "app/Dockerfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("dockerfile", "app/dockerfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Jenkinsfile", "Jenkinsfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Dockerfile.dev", "Dockerfile.dev", 1L, 0, 0L)), is(false));
        assertThat(evaluator.matches(docker, ctx("MyDockerfile", "MyDockerfile", 1L, 0, 0L)), is(false));
    }

    @Test
    public void matches_fileNames_globsAndAlternatives() {
        final AiCondition docker = names("Dockerfile.*", "*.{gradle,gradle.kts}");
        assertThat(evaluator.matches(docker, ctx("Dockerfile.dev", "Dockerfile.dev", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("build.gradle", "build.gradle", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("build.gradle.kts", "a/build.gradle.kts", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Dockerfile", "Dockerfile", 1L, 0, 0L)), is(false));
    }

    @Test
    public void matches_fileNames_nullEntryIsSkippedAndBlanksAroundANameAreNot() {
        assertThat(evaluator.matches(names(null, " Makefile "), ctx("Makefile", "Makefile", 1L, 0, 0L)), is(true));
    }

    @Test
    public void validate_fileNames_validAndInvalid() {
        assertDoesNotThrow(() -> evaluator.validate(names("Dockerfile", "*.{yml,yaml}")));
        final IllegalArgumentException empty =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names()));
        assertThat(empty.getMessage(), is("a fileNames condition must list at least one name"));
        final IllegalArgumentException blank =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names("Dockerfile", " ")));
        assertThat(blank.getMessage(), is("a fileNames condition must not list a blank name"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names((String) null)));
        final IllegalArgumentException directory =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names("app/Dockerfile")));
        assertThat(
                directory.getMessage(),
                is("fileNames match a file's name without its directory, not 'app/Dockerfile'; use pathGlob"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names("/Dockerfile")));
        final IllegalArgumentException unclosed =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(names("*.{yml")));
        assertThat(unclosed.getMessage(), is("the glob '*.{yml' opens a '{' it does not close"));
    }

    @Test
    public void validate_fileNamesNextToAnotherLeaf_throws() {
        final AiCondition c = names("Dockerfile");
        c.setExtensions(Arrays.asList(".java"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="contentMatches">
    private static AiCondition content(final String regex) {
        final AiCondition c = new AiCondition();
        c.setContentMatches(regex);
        return c;
    }

    private static AiFileContext headCtx(final String head) {
        return new AiFileContext("deploy.yaml", "k8s/deploy.yaml", 100L, 0, 0L, head);
    }

    @Test
    public void matches_contentMatches_findsThePatternInTheHead() {
        final AiCondition kubernetes = content("(?m)^apiVersion:[ \\t]*apps/v1");
        assertThat(
                evaluator.matches(kubernetes, headCtx("# deployment\napiVersion: apps/v1\nkind: Deployment\n")),
                is(true));
        assertThat(evaluator.matches(kubernetes, headCtx("openapi: 3.0.0\n")), is(false));
        assertThat(evaluator.matches(kubernetes, headCtx("")), is(false));
    }

    @Test
    public void matches_contentMatches_aPatternPastItsReadBudget_matchesNothingAndWarns() {
        // arrange: a lazy comment pattern over unclosed comments reads to the end from every start
        final String head = "<!-- x\n".repeat(6_000);
        final ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(AiConditionEvaluator.class);
        final ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender =
                new ch.qos.logback.core.read.ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        final boolean matched;
        try {
            // act
            matched = evaluator.matches(content("(?s)<!--.*?-->"), headCtx(head));
        } finally {
            logger.detachAppender(appender);
        }

        // assert
        assertThat(matched, is(false));
        assertThat(appender.list.size(), is(1));
        assertThat(
                appender.list.get(0).getFormattedMessage(),
                is("contentMatches '(?s)<!--.*?-->' stopped after reading "
                        + ReadBudget.limit(head.length())
                        + " characters of the 42000-character head of k8s/deploy.yaml: the pattern backtracks"
                        + " too far, so the condition does not match"));
        assertThat(evaluator.matches(content("(?s)<!--.*?x"), headCtx(head)), is(true));
    }

    @Test
    public void matches_contentMatches_withoutAHead_matchesNothing() {
        assertThat(evaluator.matches(content("x"), javaCtx()), is(false));
        assertThat(evaluator.matches(content("^$"), javaCtx()), is(true));
    }

    @Test
    public void validate_contentMatches_validAndInvalid() {
        assertDoesNotThrow(() -> evaluator.validate(content("(?m)^openapi:")));
        final IllegalArgumentException empty =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(content("")));
        assertThat(empty.getMessage(), is("a contentMatches condition must not be empty"));
        final IllegalArgumentException invalid =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(content("(unclosed")));
        assertThat(
                invalid.getMessage().startsWith("a contentMatches condition is no Java regular expression: "),
                is(true));
    }

    @Test
    public void validate_contentMatchesNextToAnotherLeaf_throws() {
        final AiCondition c = content("x");
        c.setPathGlob("**/x");
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    @Test
    public void usesContent_directNestedAndAbsent() {
        assertThat(evaluator.usesContent(content("x")), is(true));
        assertThat(evaluator.usesContent(and(ext(".yaml"), content("x"))), is(true));
        assertThat(evaluator.usesContent(or(ext(".yaml"), content("x"))), is(true));
        assertThat(evaluator.usesContent(not(content("x"))), is(true));
        assertThat(evaluator.usesContent(ext(".yaml")), is(false));
        assertThat(evaluator.usesContent(and(ext(".yaml"), lines(0L, 10L))), is(false));
        assertThat(evaluator.usesContent(or(ext(".yaml"), size(0L, 10L))), is(false));
        assertThat(evaluator.usesContent(not(ext(".yaml"))), is(false));
        final AiCondition andNull = new AiCondition();
        andNull.setAnd(new AiConditionGroup());
        assertThat(evaluator.usesContent(andNull), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="interpreters">
    private static AiCondition interpreters(final String... names) {
        final AiCondition c = new AiCondition();
        c.setInterpreters(Arrays.asList(names));
        return c;
    }

    @Test
    public void matches_interpreters_readsTheShebangLineOfTheHead() {
        final AiCondition shell = interpreters("bash", "sh");
        assertThat(evaluator.matches(shell, headCtx("#!/usr/bin/env bash\nset -e\n")), is(true));
        assertThat(evaluator.matches(shell, headCtx("#!/bin/sh\n")), is(true));
        assertThat(evaluator.matches(shell, headCtx("#!/usr/bin/perl\n")), is(false));
        assertThat(evaluator.matches(shell, headCtx("echo '#!/bin/bash'\n")), is(false));
        assertThat(evaluator.matches(shell, headCtx("")), is(false));
        assertThat(evaluator.matches(interpreters("python"), headCtx("#!/usr/bin/python3.12\n")), is(true));
    }

    @Test
    public void validate_interpreters_emptyBlankOrAPath_isRefused() {
        assertDoesNotThrow(() -> evaluator.validate(interpreters("bash", "python3")));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(interpreters()))
                        .getMessage(),
                is("an interpreters condition must list at least one interpreter"));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(interpreters("bash", " ")))
                        .getMessage(),
                is("an interpreters condition must not list a blank interpreter"));
        final AiCondition withNull = new AiCondition();
        withNull.setInterpreters(Arrays.asList("bash", null));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(withNull))
                        .getMessage(),
                is("an interpreters condition must not list a blank interpreter"));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(interpreters("/bin/sh")))
                        .getMessage(),
                is("an interpreter is named without its directory (bash, not /bin/bash): '/bin/sh'"));
    }

    @Test
    public void validate_interpretersNextToAnotherLeaf_throws() {
        final AiCondition c = interpreters("bash");
        c.setExtensions(Arrays.asList(".sh"));
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
        assertThat(thrown.getMessage().contains("/contentMatches/interpreters: "), is(true));
    }

    @Test
    public void usesContent_interpretersReadTheHead() {
        assertThat(evaluator.usesContent(interpreters("bash")), is(true));
        assertThat(evaluator.usesContent(or(ext(".sh"), interpreters("bash"))), is(true));
    }

    @Test
    public void setInterpreters_copiesTheListAndKeepsNull() {
        final java.util.List<String> names = new java.util.ArrayList<>(Arrays.asList("bash"));
        final AiCondition c = new AiCondition();
        c.setInterpreters(names);
        names.add("perl");
        assertThat(c.getInterpreters(), is(Arrays.asList("bash")));
        c.setInterpreters(null);
        assertThat(c.getInterpreters() == null, is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="pathGlob alternatives">
    @Test
    public void matches_pathGlob_alternatives() {
        final AiCondition glob = new AiCondition();
        glob.setPathGlob("**/{k8s,helm}/**/*.{yml,yaml}");
        assertThat(evaluator.matches(glob, ctx("d.yaml", "deploy/k8s/base/d.yaml", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("v.yml", "helm/v.yml", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("d.json", "k8s/d.json", 1L, 0, 0L)), is(false));
        assertThat(evaluator.matches(glob, ctx("d.yaml", "docs/d.yaml", 1L, 0, 0L)), is(false));
    }

    @Test
    public void validate_pathGlobWithAnUnclosedAlternative_throws() {
        final AiCondition glob = new AiCondition();
        glob.setPathGlob("**/*.{yml");
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(glob));
        assertThat(thrown.getMessage(), is("the glob '**/*.{yml' opens a '{' it does not close"));
    }
    // </editor-fold>
}

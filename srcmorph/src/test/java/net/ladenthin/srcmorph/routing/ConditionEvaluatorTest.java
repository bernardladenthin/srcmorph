// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ladenthin.srcmorph.config.ConditionGroup;
import net.ladenthin.srcmorph.config.RangeCondition;
import net.ladenthin.srcmorph.config.RuleCondition;
import net.ladenthin.srcmorph.support.ReadBudget;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class ConditionEvaluatorTest {

    private final ConditionEvaluator evaluator = new ConditionEvaluator();

    private static FileContext ctx(
            final String name, final String path, final long size, final int lines, final long mtime) {
        return new FileContext(name, path, size, lines, mtime);
    }

    private static FileContext javaCtx() {
        return ctx("Foo.java", "src/Foo.java", 500L, 10, 1000L);
    }

    private static RuleCondition ext(final String... e) {
        final RuleCondition c = new RuleCondition();
        c.setExtensions(Arrays.asList(e));
        return c;
    }

    private static RuleCondition size(final long min, final long max) {
        final RuleCondition c = new RuleCondition();
        final RangeCondition r = new RangeCondition();
        r.setMin(min);
        r.setMax(max);
        c.setSize(r);
        return c;
    }

    private static RuleCondition lines(final long min, final long max) {
        final RuleCondition c = new RuleCondition();
        final RangeCondition r = new RangeCondition();
        r.setMin(min);
        r.setMax(max);
        c.setLines(r);
        return c;
    }

    private static ConditionGroup group(final RuleCondition... kids) {
        final ConditionGroup g = new ConditionGroup();
        g.setConditions(Arrays.asList(kids));
        return g;
    }

    private static RuleCondition and(final RuleCondition... kids) {
        final RuleCondition c = new RuleCondition();
        c.setAnd(group(kids));
        return c;
    }

    private static RuleCondition or(final RuleCondition... kids) {
        final RuleCondition c = new RuleCondition();
        c.setOr(group(kids));
        return c;
    }

    private static RuleCondition not(final RuleCondition kid) {
        final RuleCondition c = new RuleCondition();
        c.setNot(kid);
        return c;
    }

    // <editor-fold defaultstate="collapsed" desc="matches: leaves">
    @Test
    public void matches_extensionCondition_matchesOnlyAListedExtension() {
        // act, assert
        assertThat(evaluator.matches(ext(".java", ".kt"), javaCtx()), is(true));
        assertThat(evaluator.matches(ext(".sql"), javaCtx()), is(false));
    }

    @Test
    public void matches_sizeRange_matchesOnlyASizeInTheRange() {
        // act, assert
        assertThat(evaluator.matches(size(0L, 1000L), javaCtx()), is(true)); // 500 <= 1000
        assertThat(evaluator.matches(size(1000L, 0L), javaCtx()), is(false)); // 500 not > 1000
    }

    @Test
    public void matches_linesRange_matchesOnlyALineCountInTheRange() {
        // act, assert
        assertThat(evaluator.matches(lines(0L, 10L), javaCtx()), is(true)); // 10 <= 10
        assertThat(evaluator.matches(lines(10L, 0L), javaCtx()), is(false)); // 10 not > 10
    }

    @Test
    public void matches_modifiedAfter_matchesOnlyAStrictlyLaterTime() {
        // arrange
        // instant 1970-01-01T00:00:01Z = 1000ms
        final RuleCondition after = new RuleCondition();
        after.setModifiedAfter("1970-01-01T00:00:01Z");

        // act, assert
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 2000L)), is(true));
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 500L)), is(false));
        // exactly equal -> strictly-after is false (pins the '>' boundary)
        assertThat(evaluator.matches(after, ctx("F.java", "F.java", 1L, 0, 1000L)), is(false));
    }

    @Test
    public void matches_modifiedBefore_matchesOnlyAStrictlyEarlierTime() {
        // arrange
        final RuleCondition before = new RuleCondition();
        before.setModifiedBefore("1970-01-01T00:00:01Z"); // 1000ms

        // act, assert
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 500L)), is(true));
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 2000L)), is(false));
        // exactly equal -> strictly-before is false (pins the '<' boundary)
        assertThat(evaluator.matches(before, ctx("F.java", "F.java", 1L, 0, 1000L)), is(false));
    }

    @Test
    public void matches_pathGlob_matchesOnlyAMatchingPath() {
        // arrange
        final RuleCondition glob = new RuleCondition();
        glob.setPathGlob("**/generated/**");

        // act, assert
        assertThat(evaluator.matches(glob, ctx("X.java", "src/generated/X.java", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("X.java", "src/main/X.java", 1L, 0, 0L)), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: and / or / not / nested">
    @Test
    public void matches_andWithAllTrueOrOneFalse_matchesOnlyWhenAllAreTrue() {
        // act, assert
        assertThat(evaluator.matches(and(ext(".java"), size(0L, 1000L)), javaCtx()), is(true));
        assertThat(evaluator.matches(and(ext(".java"), size(1000L, 0L)), javaCtx()), is(false));
    }

    @Test
    public void matches_orWithOneTrueOrAllFalse_matchesWhenOneIsTrue() {
        // act, assert
        assertThat(evaluator.matches(or(ext(".sql"), size(0L, 1000L)), javaCtx()), is(true));
        assertThat(evaluator.matches(or(ext(".sql"), size(1000L, 0L)), javaCtx()), is(false));
    }

    @Test
    public void matches_not_invertsItsCondition() {
        // act, assert
        assertThat(evaluator.matches(not(ext(".sql")), javaCtx()), is(true));
        assertThat(evaluator.matches(not(ext(".java")), javaCtx()), is(false));
    }

    @Test
    public void matches_nestedTree_evaluatesEveryLevel() {
        // arrange
        // .java AND (>=big OR <=small) AND NOT generated
        final RuleCondition glob = new RuleCondition();
        glob.setPathGlob("**/generated/**");
        final RuleCondition nested = and(ext(".java"), or(size(0L, 1000L), size(9999L, 0L)), not(glob));

        // act, assert
        assertThat(evaluator.matches(nested, javaCtx()), is(true));
        assertThat(evaluator.matches(nested, ctx("X.java", "src/generated/X.java", 500L, 0, 0L)), is(false));
    }

    @Test
    public void matches_groupWithNullConditions_isVacuous() {
        // arrange
        // a group whose <conditions> list is null: AND is vacuously true, OR vacuously false
        final RuleCondition andNull = new RuleCondition();
        andNull.setAnd(new ConditionGroup());

        // act, assert
        assertThat(evaluator.matches(andNull, javaCtx()), is(true));

        // arrange
        final RuleCondition orNull = new RuleCondition();
        orNull.setOr(new ConditionGroup());

        // act, assert
        assertThat(evaluator.matches(orNull, javaCtx()), is(false));
        // and usesLines tolerates a null-conditions group
        assertThat(evaluator.usesLines(andNull), is(false));
        assertThat(evaluator.usesLines(orNull), is(false));
    }

    @Test
    public void matches_emptyNode_throws() {
        // arrange
        final RuleCondition empty = new RuleCondition();
        final FileContext context = javaCtx();

        // act, assert
        assertThrows(IllegalStateException.class, () -> evaluator.matches(empty, context));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate">
    @Test
    public void validate_validLeavesAndTree_noExceptionThrown() {
        // act, assert
        // every leaf/combinator must pass validation as the single branch of a node — this also pins the
        // branchCount() count++ for each kind (or / lines / modifiedBefore included).
        assertDoesNotThrow(() -> evaluator.validate(ext(".java")));
        assertDoesNotThrow(() -> evaluator.validate(size(0L, 1000L)));
        assertDoesNotThrow(() -> evaluator.validate(lines(0L, 100L)));
        assertDoesNotThrow(() -> evaluator.validate(and(ext(".java"), not(size(1000L, 0L)))));
        assertDoesNotThrow(() -> evaluator.validate(or(ext(".java"), size(0L, 100L))));

        // arrange
        final RuleCondition after = new RuleCondition();
        after.setModifiedAfter("2026-01-01T00:00:00Z");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(after));

        // arrange
        final RuleCondition before = new RuleCondition();
        before.setModifiedBefore("2026-01-01T00:00:00Z");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(before));

        // arrange
        final RuleCondition glob = new RuleCondition();
        glob.setPathGlob("**/x/**");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(glob));
    }

    @Test
    public void validate_zeroBranches_throws() {
        // arrange
        final RuleCondition empty = new RuleCondition();

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(empty));
    }

    @Test
    public void validate_twoBranches_throws() {
        // arrange
        final RuleCondition c = ext(".java");
        c.setPathGlob("**/x/**"); // now two branches set

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    @Test
    public void validate_emptyAnd_throws() {
        // arrange
        final RuleCondition emptyAnd = and();

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(emptyAnd));
    }

    @Test
    public void validate_emptyOr_throws() {
        // arrange
        final RuleCondition emptyOr = or();

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(emptyOr));
    }

    @Test
    public void validate_invalidChildOfAndOrNot_throws() {
        // arrange
        final RuleCondition badChild = ext(".java");
        badChild.setSize(new RangeCondition()); // two branches in the child
        final RuleCondition andWithBadChild = and(badChild);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(andWithBadChild));

        // arrange
        // recursion must reach OR children and NOT children too
        final RuleCondition badOrChild = ext(".java");
        badOrChild.setSize(new RangeCondition());
        final RuleCondition orWithBadChild = or(badOrChild);
        final RuleCondition notWithEmptyChild = not(new RuleCondition());

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(orWithBadChild));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(notWithEmptyChild));
    }

    @Test
    public void validate_emptyExtensions_throws() {
        // arrange
        final RuleCondition c = new RuleCondition();
        c.setExtensions(Arrays.<String>asList());

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    @Test
    public void validate_unboundedRange_throws() {
        // arrange
        final RuleCondition unboundedSize = size(0L, 0L);
        final RuleCondition unboundedLines = lines(0L, 0L);

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(unboundedSize));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(unboundedLines));
    }

    @Test
    public void validate_invalidDate_throws() {
        // arrange
        final RuleCondition after = new RuleCondition();
        after.setModifiedAfter("not-a-date");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(after));

        // arrange
        final RuleCondition before = new RuleCondition();
        before.setModifiedBefore("2026/01/01");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(before));
    }

    @Test
    public void validate_blankGlob_throws() {
        // arrange
        final RuleCondition c = new RuleCondition();
        c.setPathGlob("   ");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="usesLines">
    @Test
    public void usesLines_directAndNested_returnsTrue() {
        // act, assert
        assertThat(evaluator.usesLines(lines(0L, 100L)), is(true));
        assertThat(evaluator.usesLines(and(ext(".java"), lines(0L, 100L))), is(true));
        assertThat(evaluator.usesLines(or(ext(".java"), lines(0L, 100L))), is(true));
        assertThat(evaluator.usesLines(not(lines(0L, 100L))), is(true));
    }

    @Test
    public void usesLines_absent_returnsFalse() {
        // act, assert
        assertThat(evaluator.usesLines(ext(".java")), is(false));
        assertThat(evaluator.usesLines(and(ext(".java"), size(0L, 100L))), is(false));
        assertThat(evaluator.usesLines(or(ext(".java"), size(0L, 100L))), is(false));
        assertThat(evaluator.usesLines(not(ext(".java"))), is(false));
        assertThat(evaluator.usesLines(content("x")), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: fileNames">
    private static RuleCondition names(final String... names) {
        final RuleCondition c = new RuleCondition();
        c.setFileNames(Arrays.asList(names));
        return c;
    }

    @Test
    public void matches_fileNames_exactNameInAnyCase() {
        // arrange
        final RuleCondition docker = names("Dockerfile", "Jenkinsfile");

        // act, assert
        assertThat(evaluator.matches(docker, ctx("Dockerfile", "app/Dockerfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("dockerfile", "app/dockerfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Jenkinsfile", "Jenkinsfile", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Dockerfile.dev", "Dockerfile.dev", 1L, 0, 0L)), is(false));
        assertThat(evaluator.matches(docker, ctx("MyDockerfile", "MyDockerfile", 1L, 0, 0L)), is(false));
    }

    @Test
    public void matches_fileNames_globsAndAlternatives() {
        // arrange
        final RuleCondition docker = names("Dockerfile.*", "*.{gradle,gradle.kts}");

        // act, assert
        assertThat(evaluator.matches(docker, ctx("Dockerfile.dev", "Dockerfile.dev", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("build.gradle", "build.gradle", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("build.gradle.kts", "a/build.gradle.kts", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(docker, ctx("Dockerfile", "Dockerfile", 1L, 0, 0L)), is(false));
    }

    @Test
    public void matches_fileNames_nullEntryIsSkippedAndBlanksAroundANameAreNot() {
        // act, assert
        assertThat(evaluator.matches(names(null, " Makefile "), ctx("Makefile", "Makefile", 1L, 0, 0L)), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate: fileNames">
    @Test
    public void validate_fileNames_validAndInvalid() {
        // arrange
        final RuleCondition noNames = names();
        final RuleCondition blankName = names("Dockerfile", " ");
        final RuleCondition nullName = names((String) null);
        final RuleCondition withDirectory = names("app/Dockerfile");
        final RuleCondition rootedName = names("/Dockerfile");
        final RuleCondition unclosedGlob = names("*.{yml");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(names("Dockerfile", "*.{yml,yaml}")));
        final IllegalArgumentException empty =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(noNames));
        assertThat(empty.getMessage(), is("a fileNames condition must list at least one name"));
        final IllegalArgumentException blank =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(blankName));
        assertThat(blank.getMessage(), is("a fileNames condition must not list a blank name"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(nullName));
        final IllegalArgumentException directory =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(withDirectory));
        assertThat(
                directory.getMessage(),
                is("fileNames match a file's name without its directory, not 'app/Dockerfile'; use pathGlob"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(rootedName));
        final IllegalArgumentException unclosed =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(unclosedGlob));
        assertThat(unclosed.getMessage(), is("the glob '*.{yml' opens a '{' it does not close"));
    }

    @Test
    public void validate_fileNamesNextToAnotherLeaf_throws() {
        // arrange
        final RuleCondition c = names("Dockerfile");
        c.setExtensions(Arrays.asList(".java"));

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: contentMatches">
    private static RuleCondition content(final String regex) {
        final RuleCondition c = new RuleCondition();
        c.setContentMatches(regex);
        return c;
    }

    private static FileContext headCtx(final String head) {
        return new FileContext("deploy.yaml", "k8s/deploy.yaml", 100L, 0, 0L, head);
    }

    @Test
    public void matches_contentMatches_findsThePatternInTheHead() {
        // arrange
        final RuleCondition kubernetes = content("(?m)^apiVersion:[ \\t]*apps/v1");

        // act, assert
        assertThat(
                evaluator.matches(kubernetes, headCtx("# deployment\napiVersion: apps/v1\nkind: Deployment\n")),
                is(true));
        assertThat(evaluator.matches(kubernetes, headCtx("openapi: 3.0.0\n")), is(false));
        assertThat(evaluator.matches(kubernetes, headCtx("")), is(false));
    }

    @Test
    public void matches_contentMatchesPastItsReadBudget_matchesNothingAndWarns() {
        // arrange: a lazy comment pattern over unclosed comments reads to the end from every start
        final String head = "<!-- x\n".repeat(6_000);
        final Logger logger = (Logger) LoggerFactory.getLogger(ConditionEvaluator.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
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
    public void matches_contentMatchesThatOverflowsTheStack_matchesNothingAndWarns() {
        // arrange: a repeated group with an alternation recurses once per repetition, over a head of a
        // million characters (contentHeadChars can be set that high)
        final String head = "ab".repeat(500_000) + "c";
        final Logger logger = (Logger) LoggerFactory.getLogger(ConditionEvaluator.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        final boolean matched;
        try {
            // act
            matched = evaluator.matches(content("(?:a|b)*c"), headCtx(head));
        } finally {
            logger.detachAppender(appender);
        }

        // assert
        assertThat(matched, is(false));
        assertThat(appender.list.size(), is(1));
        assertThat(
                appender.list.get(0).getFormattedMessage(),
                is("contentMatches '(?:a|b)*c' overflowed the stack over the 1000001-character head of"
                        + " k8s/deploy.yaml: the pattern repeats a group that recurses once per repetition, so the"
                        + " condition does not match"));
    }

    @Test
    public void matches_contentMatchesWithoutAHead_matchesNothing() {
        // act, assert
        assertThat(evaluator.matches(content("x"), javaCtx()), is(false));
        assertThat(evaluator.matches(content("^$"), javaCtx()), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate: contentMatches">
    @Test
    public void validate_contentMatches_validAndInvalid() {
        // arrange
        final RuleCondition emptyPattern = content("");
        final RuleCondition invalidPattern = content("(unclosed");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(content("(?m)^openapi:")));
        final IllegalArgumentException empty =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(emptyPattern));
        assertThat(empty.getMessage(), is("a contentMatches condition must not be empty"));
        final IllegalArgumentException invalid =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(invalidPattern));
        assertThat(
                invalid.getMessage().startsWith("a contentMatches condition is no Java regular expression: "),
                is(true));
    }

    @Test
    public void validate_contentMatchesNextToAnotherLeaf_throws() {
        // arrange
        final RuleCondition c = content("x");
        c.setPathGlob("**/x");

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="usesContent">
    @Test
    public void usesContent_directNestedAndAbsent_returnsTrueOnlyWithContent() {
        // act, assert
        assertThat(evaluator.usesContent(content("x")), is(true));
        assertThat(evaluator.usesContent(and(ext(".yaml"), content("x"))), is(true));
        assertThat(evaluator.usesContent(or(ext(".yaml"), content("x"))), is(true));
        assertThat(evaluator.usesContent(not(content("x"))), is(true));
        assertThat(evaluator.usesContent(ext(".yaml")), is(false));
        assertThat(evaluator.usesContent(and(ext(".yaml"), lines(0L, 10L))), is(false));
        assertThat(evaluator.usesContent(or(ext(".yaml"), size(0L, 10L))), is(false));
        assertThat(evaluator.usesContent(not(ext(".yaml"))), is(false));

        // arrange
        final RuleCondition andNull = new RuleCondition();
        andNull.setAnd(new ConditionGroup());

        // act, assert
        assertThat(evaluator.usesContent(andNull), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: interpreters">
    private static RuleCondition interpreters(final String... names) {
        final RuleCondition c = new RuleCondition();
        c.setInterpreters(Arrays.asList(names));
        return c;
    }

    @Test
    public void matches_interpreters_readsTheShebangLineOfTheHead() {
        // arrange
        final RuleCondition shell = interpreters("bash", "sh");

        // act, assert
        assertThat(evaluator.matches(shell, headCtx("#!/usr/bin/env bash\nset -e\n")), is(true));
        assertThat(evaluator.matches(shell, headCtx("#!/bin/sh\n")), is(true));
        assertThat(evaluator.matches(shell, headCtx("#!/usr/bin/perl\n")), is(false));
        assertThat(evaluator.matches(shell, headCtx("echo '#!/bin/bash'\n")), is(false));
        assertThat(evaluator.matches(shell, headCtx("")), is(false));
        assertThat(evaluator.matches(interpreters("python"), headCtx("#!/usr/bin/python3.12\n")), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate: interpreters">
    @Test
    public void validate_interpretersEmptyBlankOrAPath_isRefused() {
        // arrange
        final RuleCondition noInterpreters = interpreters();
        final RuleCondition blankInterpreter = interpreters("bash", " ");

        // act, assert
        assertDoesNotThrow(() -> evaluator.validate(interpreters("bash", "python3")));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(noInterpreters))
                        .getMessage(),
                is("an interpreters condition must list at least one interpreter"));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(blankInterpreter))
                        .getMessage(),
                is("an interpreters condition must not list a blank interpreter"));

        // arrange
        final RuleCondition withNull = new RuleCondition();
        withNull.setInterpreters(Arrays.asList("bash", null));
        final RuleCondition withPath = interpreters("/bin/sh");

        // act, assert
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(withNull))
                        .getMessage(),
                is("an interpreters condition must not list a blank interpreter"));
        assertThat(
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(withPath))
                        .getMessage(),
                is("an interpreter is named without its directory (bash, not /bin/bash): '/bin/sh'"));
    }

    @Test
    public void validate_interpretersNextToAnotherLeaf_throws() {
        // arrange
        final RuleCondition c = interpreters("bash");
        c.setExtensions(Arrays.asList(".sh"));

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(c));

        // assert
        assertThat(thrown.getMessage().contains("/contentMatches/interpreters: "), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="usesContent: interpreters">
    @Test
    public void usesContent_interpreters_readTheHead() {
        // act, assert
        assertThat(evaluator.usesContent(interpreters("bash")), is(true));
        assertThat(evaluator.usesContent(or(ext(".sh"), interpreters("bash"))), is(true));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="setInterpreters">
    @Test
    public void setInterpreters_listOrNull_copiesTheListAndKeepsNull() {
        // arrange
        final List<String> names = new ArrayList<>(Arrays.asList("bash"));
        final RuleCondition c = new RuleCondition();

        // act
        c.setInterpreters(names);
        names.add("perl");

        // assert
        assertThat(c.getInterpreters(), is(Arrays.asList("bash")));

        // act
        c.setInterpreters(null);

        // assert
        assertThat(c.getInterpreters() == null, is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches: pathGlob alternatives">
    @Test
    public void matches_pathGlob_alternatives() {
        // arrange
        final RuleCondition glob = new RuleCondition();
        glob.setPathGlob("**/{k8s,helm}/**/*.{yml,yaml}");

        // act, assert
        assertThat(evaluator.matches(glob, ctx("d.yaml", "deploy/k8s/base/d.yaml", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("v.yml", "helm/v.yml", 1L, 0, 0L)), is(true));
        assertThat(evaluator.matches(glob, ctx("d.json", "k8s/d.json", 1L, 0, 0L)), is(false));
        assertThat(evaluator.matches(glob, ctx("d.yaml", "docs/d.yaml", 1L, 0, 0L)), is(false));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="validate: pathGlob alternatives">
    @Test
    public void validate_pathGlobWithAnUnclosedAlternative_throws() {
        // arrange
        final RuleCondition glob = new RuleCondition();
        glob.setPathGlob("**/*.{yml");

        // act
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> evaluator.validate(glob));

        // assert
        assertThat(thrown.getMessage(), is("the glob '**/*.{yml' opens a '{' it does not close"));
    }
    // </editor-fold>
}

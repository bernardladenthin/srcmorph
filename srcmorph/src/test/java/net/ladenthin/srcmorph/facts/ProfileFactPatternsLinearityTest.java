// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Measures that every pattern of every facts file of the current profile ({@code profiles/v3/*.tsv},
 * the {@code [ignore]} lines included) reads its input in linear time, over inputs that repeat one
 * adversarial line: an opener that is never closed, a tag without its {@code >}, a heredoc marker, an
 * unbalanced parenthesis, in the languages of every facts file. The measure is
 * {@link FactPatternLinearity}'s, which the {@code linearity-jdk8} job of {@code publish.yml} also runs on
 * Java 8. A facts file added to the profile is measured without a change here.
 */
public class ProfileFactPatternsLinearityTest {

    /** A bound for a pattern far worse than quadratic, so a regression fails instead of hanging. */
    private static final Duration PATTERN_LIMIT = Duration.ofSeconds(120);

    /**
     * One case per pattern of every facts file of the profile.
     *
     * @return the file, the label and the pattern of each counter
     * @throws IOException if the profile cannot be read
     */
    static Stream<Arguments> patterns() throws IOException {
        final List<Arguments> cases = new ArrayList<>();
        for (final String key : FactPatternLinearity.keys(ProfileFactPatterns.PROFILE_DIRECTORY)) {
            for (final FactCounter counter : ProfileFactPatterns.counters(key)) {
                cases.add(Arguments.of(key, counter.getLabel(), counter.getPattern()));
            }
        }
        return cases.stream();
    }

    // <editor-fold defaultstate="collapsed" desc="measure">
    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("patterns")
    public void measure_everyPatternOfTheProfile_readsLinearlyOverEveryRepeatedLine(
            final String key, final String label, final String pattern) {
        // act
        final List<String> failures =
                assertTimeoutPreemptively(PATTERN_LIMIT, () -> FactPatternLinearity.failures(key, label, pattern));

        // assert
        assertThat(failures, is(empty()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("quadraticPatterns")
    public void measure_quadraticPattern_isCaught(final String pattern) {
        // arrange
        final Pattern compiled = Pattern.compile(pattern);
        final String line = "<!-- x\n";

        // act
        final long shortReads = FactPatternLinearity.reads(
                compiled, FactPatternLinearity.repeat(line, FactPatternLinearity.SHORT_REPETITIONS));
        final long longReads = FactPatternLinearity.reads(
                compiled,
                FactPatternLinearity.repeat(
                        line, FactPatternLinearity.SHORT_REPETITIONS * FactPatternLinearity.GROWTH));

        // assert
        assertThat(FactPatternLinearity.MAX_GROWTH * shortReads, lessThanOrEqualTo(longReads));
    }

    @ParameterizedTest(name = "{0} over {1}")
    @MethodSource("quadraticWithinOneLinePatterns")
    public void measure_patternQuadraticWithinOneLine_isCaughtOnlyOverOneLongLine(
            final String pattern, final String line) {
        // act
        final List<String> overLines = FactPatternLinearity.failures("k", "l", pattern, List.of(line));
        final List<String> withinOneLine =
                FactPatternLinearity.failures("k", "l", pattern, FactPatternLinearity.units(List.of(line)));

        // assert
        assertThat(overLines, is(empty()));
        assertThat(withinOneLine, is(not(empty())));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("stackOverflowingPatterns")
    public void measure_patternThatOverflowsTheStack_isCaught(final String pattern) {
        // act
        final List<String> failures = FactPatternLinearity.failures("k", "l", pattern, List.of("@a @b "));

        // assert
        assertThat(failures, hasItem(containsString("overflowed the stack")));
    }

    @Test
    public void units_linesWithALineBreak_addTheLineWithoutIt() {
        // act
        final List<String> units = FactPatternLinearity.units(List.of("a\n", "b", "\n"));

        // assert
        assertThat(units, is(List.of("a\n", "b", "\n", "a")));
    }
    // </editor-fold>

    /**
     * Patterns known to be quadratic over unclosed comments, so the measure itself is checked: the lazy
     * comment pattern xml-facts had up to 1.4.0, and a tempered one that rescans the same way.
     *
     * @return one case per pattern
     */
    static Stream<String> quadraticPatterns() {
        return Stream.of("(?s)<!--.*?-->", "(?s)<!--(?:(?!-->).)*-->");
    }

    /**
     * Patterns that rescan to the end of their line from every opener, which repeated lines cannot show:
     * the secret keys of properties-facts, the substitutions of mybatis-facts and the character literals
     * of java-facts up to 1.4.0, each with the line it was measured over.
     *
     * @return one case per pattern and line
     */
    static Stream<Arguments> quadraticWithinOneLinePatterns() {
        return Stream.of(
                Arguments.of(
                        "(?im)^[ \\t]*+[^\\s#!=:][^=:\\n]*?(?:password|passwd|secret|token|credential)[^=:\\n]*?[ \\t]*+[=:]",
                        "password\n"),
                Arguments.of("\\$\\{[^}\\r\\n]*+\\}", "${\n"),
                Arguments.of("'(?:[^'\\\\\\n]++|\\\\.)*+'", "\\'\n"));
    }

    /**
     * A modifier list repeated without a possessive quantifier, as java-facts' type declarations had it up
     * to 1.4.0: {@code java.util.regex} recurses once per repetition, so a line of thousands of annotations
     * overflows the stack.
     *
     * @return one case per pattern
     */
    static Stream<String> stackOverflowingPatterns() {
        return Stream.of("(?m)^[ \\t]*(?:(?:public|@[A-Za-z_$][\\w$.]*+)[ \\t]+)*class[ \\t]+[A-Za-z_$]");
    }
}

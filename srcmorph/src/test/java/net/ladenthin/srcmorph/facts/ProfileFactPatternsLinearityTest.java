// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.config.FactCounter;
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
}

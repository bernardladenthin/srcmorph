// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.config.AiFactCounter;
import net.ladenthin.srcmorph.support.ReadBudget;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Measures that every pattern of every facts file of the current profile ({@code profiles/v3/*.tsv},
 * the {@code [ignore]} lines included) reads its input in linear time, over inputs that repeat one
 * adversarial line: an opener that is never closed, a tag without its {@code >}, a heredoc marker, an
 * unbalanced parenthesis, in the languages of every facts file.
 *
 * <p>The measure is not a stopwatch: the input is a {@link CharSequence} that counts the characters
 * the matcher reads, and a pattern must read at most {@value #MAX_GROWTH} times as many for an input
 * {@value #GROWTH} times as long. A linear pattern reads about {@value #GROWTH} times as many, a
 * quadratic one {@value #GROWTH} squared, so the test is exact on any machine and under any load. A
 * facts file added to the profile is measured without a change here.
 */
public class ProfileFactPatternsLinearityTest {

    /** Repetitions of the line in the shorter input. */
    private static final int SHORT_REPETITIONS = 2000;

    /** How much longer the longer input is. */
    private static final int GROWTH = 4;

    /** The most the reads may grow from the shorter input to the longer one; linear is {@value #GROWTH}. */
    private static final int MAX_GROWTH = 6;

    /** A bound for a pattern far worse than quadratic, so a regression fails instead of hanging. */
    private static final Duration PATTERN_LIMIT = Duration.ofSeconds(60);

    /**
     * The repeated lines: the openers, tags and markers of every language the profile counts, and a few
     * runs of single characters.
     */
    private static final List<String> LINES = Arrays.asList(
            // XML and POM
            "<!-- x\n",
            "<![CDATA[ x\n",
            "<? x\n",
            "<!DOCTYPE r [ x\n",
            "<!DOCTYPE r [ ] x\n",
            "<a b=\"c\" \n",
            "<xs:complexType a=\"b\"\n",
            "<plugin>\n",
            "<plugin><plugin>x\n",
            "<dependency\n",
            " xmlns:a = \n",
            " id = \n",
            // SQL
            "'abc\n",
            "/* x\n",
            "-- x\n",
            "CREATE TABLE t (\n",
            "INSERT INTO t VALUES (1,\n",
            "$$ x\n",
            "\"x\n",
            // shell
            "cat <<EOF\n",
            "x=$((1 << 4))\n",
            "echo \"cat <<EOF\"\n",
            "f() {\n",
            "while getopts ab opt\n",
            "# x\n",
            "${x\n",
            "$(\n",
            "`x\n",
            // Perl
            "=pod\n",
            "print <<EOF;\n",
            "sub f {\n",
            "q{\n",
            "s/x/\n",
            // Java
            "public void f(int a, \n",
            "Foo<Bar<Baz\n",
            "private static final Map<String, \n",
            "Foo(int a, (b\n",
            "@Override\n",
            "class A {\n",
            "/** x\n",
            "\"\"\"\n",
            "public x y = \n",
            "@A @B @C(x) \n",
            // Kotlin and Groovy
            "val s = \"\"\"\n",
            "/* /* x\n",
            "\"${a\n",
            "suspend suspend \n",
            "fun a.b.c\n",
            "(val x,\n",
            "def x = /a\n",
            "$/ x\n",
            "'''\n",
            "{ a, b \n",
            // JSP and Facelets
            "<%-- x\n",
            "<% x\n",
            "${a\n",
            "#{a \n",
            // YAML: OpenAPI, Kubernetes and Helm, GitHub Actions
            "  key: |\n",
            "  - key: >-\n",
            "{{/* x\n",
            "${{ secrets.\n",
            "kind: \n",
            "    get:\n",
            "  /a/b\n",
            // integration, BPMN, DMN, Dockerfile
            "<bpmndi:BPMNDiagram x\n",
            "<dmndi:DMNDI x\n",
            "<decisionTable a=\"b\" \n",
            "<endpoint a=\"b\" \n",
            "FROM \n",
            // Scala, Protobuf, Avro, GraphQL, AsyncAPI
            "object A extends B {\n",
            "def f[A](x: \n",
            "'a\n",
            "message A {\n",
            "rpc A(stream \n",
            "rpc A(B) returns (\n",
            "\"type\": \n",
            "type A {\n",
            "  f(a: \n",
            "@a @b \n",
            "  publish:\n",
            // Terraform, Ansible, Compose, Camel YAML, Gradle, properties
            "resource \"a\" \"b\" {\n",
            "x = <<EOF\n",
            "x = <<-EOF\n",
            "- name: x\n",
            "  image: \n",
            "- route:\n",
            "implementation(\"\n",
            "id 'a\n",
            "a.b.c = \n",
            "${a.b\n",
            // PowerShell and batch
            "<# x\n",
            "$x = @\"\n",
            "@'\n",
            "function Get-X {\n",
            "rem x\n",
            ":: x\n",
            "set /a x=\n",
            "for /f \"x\n",
            // single characters, without a line break
            "x",
            " ",
            "<",
            "(",
            ")",
            "{",
            "\"",
            "'",
            "\\",
            "#",
            "=",
            "\n");

    /**
     * One case per pattern of every facts file of the profile.
     *
     * @return the file, the label and the pattern of each counter
     */
    static Stream<Arguments> patterns() {
        final List<Path> files;
        try (Stream<Path> listing = Files.list(ProfileFactPatterns.PROFILE_DIRECTORY)) {
            files = listing.filter(file -> file.getFileName().toString().endsWith(".tsv"))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        final List<Arguments> cases = new ArrayList<>();
        for (final Path file : files) {
            final String name = file.getFileName().toString();
            final String key = name.substring(0, name.length() - ".tsv".length());
            for (final AiFactCounter counter : ProfileFactPatterns.counters(key)) {
                cases.add(Arguments.of(key, counter.getLabel(), counter.getPattern()));
            }
        }
        return cases.stream();
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("patterns")
    public void everyPattern_readsLinearlyOverEveryRepeatedLine(
            final String key, final String label, final String pattern) {
        final Pattern compiled = Pattern.compile(pattern);
        for (final String line : LINES) {
            // act
            final long shortReads =
                    assertTimeoutPreemptively(PATTERN_LIMIT, () -> reads(compiled, repeat(line, SHORT_REPETITIONS)));
            final long longReads = assertTimeoutPreemptively(
                    PATTERN_LIMIT, () -> reads(compiled, repeat(line, SHORT_REPETITIONS * GROWTH)));

            // assert
            assertThat(
                    key + " '" + label + "' over the line " + quoted(line) + ": " + shortReads + " reads, then "
                            + longReads + " for " + GROWTH + " times the input",
                    longReads,
                    lessThanOrEqualTo(MAX_GROWTH * Math.max(shortReads, 1L)));
            // the time limit of the facts line never stops a pattern of the profile
            final int longLength = line.length() * SHORT_REPETITIONS * GROWTH;
            assertThat(
                    key + " '" + label + "' over the line " + quoted(line) + ": " + longReads + " reads of "
                            + longLength + " characters",
                    longReads,
                    lessThanOrEqualTo(ReadBudget.limit(longLength)));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("quadraticPatterns")
    public void measure_quadraticPattern_isCaught(final String pattern) {
        final Pattern compiled = Pattern.compile(pattern);
        final long shortReads = reads(compiled, repeat("<!-- x\n", SHORT_REPETITIONS));
        final long longReads = reads(compiled, repeat("<!-- x\n", SHORT_REPETITIONS * GROWTH));

        assertThat(MAX_GROWTH * shortReads, lessThanOrEqualTo(longReads));
    }

    /**
     * Patterns known to be quadratic over unclosed comments, so the measure itself is checked: the lazy
     * comment pattern xml-facts had up to 1.4.0, and a tempered one that rescans the same way.
     *
     * @return one case per pattern
     */
    static Stream<String> quadraticPatterns() {
        return Stream.of("(?s)<!--.*?-->", "(?s)<!--(?:(?!-->).)*-->");
    }

    private static String repeat(final String line, final int times) {
        final StringBuilder text = new StringBuilder(line.length() * times);
        for (int i = 0; i < times; i++) {
            text.append(line);
        }
        return text.toString();
    }

    /** Runs {@code pattern} over {@code text} as the facts line does and returns the characters read. */
    private static long reads(final Pattern pattern, final String text) {
        final CountingSequence sequence = new CountingSequence(text);
        final Matcher matcher = pattern.matcher(sequence);
        // find every match, as AiFactExtractor counts them
        boolean found = matcher.find();
        while (found) {
            found = matcher.find();
        }
        return sequence.reads;
    }

    private static String quoted(final String line) {
        return "\"" + line.replace("\n", "\\n") + "\"";
    }

    /** A text that counts the characters a matcher reads from it. */
    private static final class CountingSequence implements CharSequence {

        private final String text;

        private long reads;

        private CountingSequence(final String text) {
            this.text = text;
        }

        @Override
        public int length() {
            return text.length();
        }

        @Override
        public char charAt(final int index) {
            reads++;
            return text.charAt(index);
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return text.subSequence(start, end);
        }

        @Override
        public String toString() {
            return text;
        }
    }
}

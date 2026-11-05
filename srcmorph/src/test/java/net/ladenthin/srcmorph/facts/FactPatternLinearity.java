// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ladenthin.srcmorph.config.AiFactCounter;
import net.ladenthin.srcmorph.support.ReadBudget;

/**
 * The measure of {@link ProfileFactPatternsLinearityTest}, written in Java 8 and without JUnit, so the
 * {@code linearity-jdk8} job of {@code publish.yml} runs it on the oldest JDK srcmorph supports: the
 * {@link ReadBudget} counts the {@code charAt} calls of {@code java.util.regex}, whose implementation
 * differs between JDKs, so a pattern linear on the JDK of the build is measured on Java 8 as well.
 *
 * <p>Every pattern of every facts file of a profile ({@code [ignore]} lines included) runs over inputs
 * that repeat one adversarial line. The input is a {@link CharSequence} that counts the characters the
 * matcher reads, and a pattern must read at most {@value #MAX_GROWTH} times as many for an input
 * {@value #GROWTH} times as long. A linear pattern reads about {@value #GROWTH} times as many, a quadratic
 * one {@value #GROWTH} squared, so the measure is exact on any machine and under any load.</p>
 */
public final class FactPatternLinearity {

    /** Repetitions of the line in the shorter input. */
    static final int SHORT_REPETITIONS = 2000;

    /** How much longer the longer input is. */
    static final int GROWTH = 4;

    /** The most the reads may grow from the shorter input to the longer one; linear is {@value #GROWTH}. */
    static final int MAX_GROWTH = 6;

    /**
     * The repeated lines: the openers, tags and markers of every language the profile counts, and a few
     * runs of single characters.
     */
    static final List<String> LINES = Arrays.asList(
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

    private FactPatternLinearity() {
        // static helpers and the entry point only
    }

    /**
     * Measures every pattern of every facts file of a profile directory and fails naming each pattern that
     * does not read linearly.
     *
     * @param args the profile directory, e.g. {@code profiles/v3}
     * @throws IOException    if a facts file cannot be read
     * @throws AssertionError naming every pattern that is not linear or reaches the time limit
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: FactPatternLinearity <profile directory>");
        }
        final Path directory = Paths.get(args[0]);
        final List<String> failures = new ArrayList<>();
        int patterns = 0;
        for (final String key : keys(directory)) {
            for (final AiFactCounter counter : counters(directory, key)) {
                patterns++;
                failures.addAll(
                        failures(key, String.valueOf(counter.getLabel()), String.valueOf(counter.getPattern())));
            }
        }
        if (!failures.isEmpty()) {
            throw new AssertionError(failures.size() + " measurements failed:\n" + String.join("\n", failures));
        }
        System.out.println(patterns + " patterns of " + directory + " read linearly on Java "
                + System.getProperty("java.version"));
    }

    /**
     * Returns the facts keys of a profile directory: its {@code .tsv} files without the extension, sorted.
     *
     * @param directory the profile directory
     * @return the keys
     * @throws IOException if the directory cannot be listed
     */
    static List<String> keys(final Path directory) throws IOException {
        try (Stream<Path> listing = Files.list(directory)) {
            return listing.map(file -> file.getFileName().toString())
                    .filter(name -> name.endsWith(".tsv"))
                    .map(name -> name.substring(0, name.length() - ".tsv".length()))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Reads one facts file as the production loader does, ignore counters and included files included.
     *
     * @param directory the profile directory
     * @param key       the facts key
     * @return the counters in file order
     * @throws IOException if the file cannot be read
     */
    static List<AiFactCounter> counters(final Path directory, final String key) throws IOException {
        final List<AiFactCounter> facts = new AiFactDefinitionLoader(directory.toFile())
                .load(null, Collections.singletonList(key))
                .get(0)
                .getFacts();
        if (facts == null) {
            throw new IllegalStateException("no counters in " + key);
        }
        return facts;
    }

    /**
     * Measures one pattern over every line of {@link #LINES}.
     *
     * @param key     the facts key, for the message
     * @param label   the counter's label, for the message
     * @param pattern the pattern
     * @return one message per line it does not read linearly over, or reads past the time limit of the
     *     facts line; empty when it passes
     */
    static List<String> failures(final String key, final String label, final String pattern) {
        final Pattern compiled = Pattern.compile(pattern);
        final List<String> failures = new ArrayList<>();
        for (final String line : LINES) {
            final long shortReads = reads(compiled, repeat(line, SHORT_REPETITIONS));
            final long longReads = reads(compiled, repeat(line, SHORT_REPETITIONS * GROWTH));
            if (longReads > MAX_GROWTH * Math.max(shortReads, 1L)) {
                failures.add(key + " '" + label + "' over the line " + quoted(line) + ": " + shortReads
                        + " reads, then " + longReads + " for " + GROWTH + " times the input");
            }
            // the time limit of the facts line never stops a pattern of the profile
            final int longLength = line.length() * SHORT_REPETITIONS * GROWTH;
            if (longReads > ReadBudget.limit(longLength)) {
                failures.add(key + " '" + label + "' over the line " + quoted(line) + ": " + longReads + " reads of "
                        + longLength + " characters");
            }
        }
        return failures;
    }

    /**
     * Returns a line repeated.
     *
     * @param line  the line
     * @param times how often
     * @return the text
     */
    static String repeat(final String line, final int times) {
        final StringBuilder text = new StringBuilder(line.length() * times);
        for (int i = 0; i < times; i++) {
            text.append(line);
        }
        return text.toString();
    }

    /**
     * Runs {@code pattern} over {@code text} as the facts line does and returns the characters read.
     *
     * @param pattern the pattern
     * @param text    the text
     * @return the characters the matcher read
     */
    static long reads(final Pattern pattern, final String text) {
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

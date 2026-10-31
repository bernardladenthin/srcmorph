// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the Java structural fact counters of the current profile ({@code profiles/v2/java-facts.tsv},
 * read through {@link ProfileFactPatterns}, so the shipped file is what is tested), in the real
 * {@link java.util.regex} engine {@code AiFactExtractor} uses, against the tricky-case self-tests. These
 * are deliberately approximate ("methods (approx)", "field declarations") - the tests pin the accepted
 * false-positive/negative boundary so the patterns cannot silently drift.
 */
public class JavaStructureFactPatternsTest {

    private static final Map<String, String> PATTERNS = ProfileFactPatterns.load("java-facts");

    /** Methods: modifiers, type params, generic/array return, multi-line params, throws, {@code {} or ;}. */
    private static final String METHOD_PATTERN = ProfileFactPatterns.pattern(PATTERNS, "methods (approx)");

    /** Field declarations that carry a STRONG modifier (final-only excluded, to skip final locals). */
    private static final String FIELD_PATTERN =
            ProfileFactPatterns.pattern(PATTERNS, "field declarations (w/ modifier)");

    /** Constructors: capitalized name directly followed by {@code (}, ending in {@code {}. */
    private static final String CTOR_PATTERN = ProfileFactPatterns.pattern(PATTERNS, "constructors");

    private static int count(final String pattern, final String source) {
        return ProfileFactPatterns.count(pattern, source);
    }

    // <editor-fold defaultstate="collapsed" desc="methods">
    @Test
    public void method_annotatedOnOwnLine_counts() {
        assertThat(count(METHOD_PATTERN, "@Override\npublic int foo() {\n}"), is(1));
    }

    @Test
    public void method_genericMultilineThrows_counts() {
        assertThat(count(METHOD_PATTERN, "public <T> T bar(List<T> x,\n int y) throws IOException {\n}"), is(1));
    }

    @Test
    public void method_arrayReturn_counts() {
        assertThat(count(METHOD_PATTERN, "int[] baz() {\n}"), is(1));
    }

    @Test
    public void method_abstractSemicolon_counts() {
        assertThat(count(METHOD_PATTERN, "abstract void run();"), is(1));
    }

    @Test
    public void method_doesNotCountControlStructures() {
        assertThat(count(METHOD_PATTERN, "if (foo()) { bar(); }"), is(0));
        assertThat(count(METHOD_PATTERN, "        for (int i = 0; i < n; i++) {\n}"), is(0));
    }

    @Test
    public void method_doesNotCountCalls() {
        assertThat(count(METHOD_PATTERN, "        obj.doThing(x);\n        foo(y);"), is(0));
        assertThat(count(METHOD_PATTERN, "        return compute(x);"), is(0));
    }

    @Test
    public void method_doesNotCountLambda() {
        assertThat(count(METHOD_PATTERN, "        Runnable r = () -> doWork();"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fields">
    @Test
    public void field_multipleDeclarators_countAsOneStatement() {
        // AiFactExtractor counts matches (statements), so "int a, b, c;" is ONE field declaration.
        assertThat(count(FIELD_PATTERN, "    private int a, b, c;"), is(1));
    }

    @Test
    public void field_staticFinalWithInit_counts() {
        assertThat(count(FIELD_PATTERN, "    public static final String NAME = \"x\";"), is(1));
    }

    @Test
    public void field_genericType_counts() {
        assertThat(count(FIELD_PATTERN, "    private Map<String, List<X>> m;"), is(1));
    }

    @Test
    public void field_transientArray_counts() {
        assertThat(count(FIELD_PATTERN, "    transient Object[] elementData;"), is(1));
    }

    @Test
    public void field_doesNotCountLocalsOrFinalLocals() {
        assertThat(count(FIELD_PATTERN, "        int local = 5;"), is(0));
        assertThat(count(FIELD_PATTERN, "        final int s;"), is(0));
        assertThat(count(FIELD_PATTERN, "        final Object[] es = elementData;"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructors">
    @Test
    public void ctor_counts() {
        assertThat(count(CTOR_PATTERN, "    public Foo(int x) {\n}"), is(1));
    }

    @Test
    public void ctor_doesNotCountLowercaseMethod() {
        assertThat(count(CTOR_PATTERN, "    public void foo(int x) {\n}"), is(0));
    }
    // </editor-fold>
}

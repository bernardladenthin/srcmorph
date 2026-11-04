// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the Java structural fact counters of the current profile ({@code profiles/v3/java-facts.tsv},
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

    /** Type declarations: classes, interfaces, annotation types, enums and records, nested ones too. */
    private static final String TYPE_PATTERN = ProfileFactPatterns.pattern(PATTERNS, "type declarations");

    /** Constants of the long enum in the run-time tests, as many as the large corpus's enum has. */
    private static final int LONG_ENUM_CONSTANTS = 6_000;

    /** Blank lines in front of a declaration in the run-time test of the type declarations. */
    private static final int LONG_BLANK_LINES = 40_000;

    /** What a long input may take; the quadratic parameter list took about 8 s on the 6 000-constant enum. */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    private static int count(final String pattern, final String source) {
        return ProfileFactPatterns.count(pattern, source);
    }

    /**
     * Writes an enum the way the large corpus's {@code PlatformErrorCode} is written: many constants with
     * arguments, then a constructor, two accessors and a static lookup.
     */
    private static String longEnum() {
        final StringBuilder source = new StringBuilder("package example.errors;\n\npublic enum PlatformErrorCode {\n");
        for (int i = 1; i <= LONG_ENUM_CONSTANTS; i++) {
            source.append("    AUTH_")
                    .append(i)
                    .append('(')
                    .append(i)
                    .append(", \"auth failure ")
                    .append(i)
                    .append("\")")
                    .append(i < LONG_ENUM_CONSTANTS ? ",\n" : ";\n");
        }
        return source.append("\n    private final int number;\n    private final String message;\n\n")
                .append("    PlatformErrorCode(int number, String message) {\n        this.number = number;\n")
                .append("        this.message = message;\n    }\n\n")
                .append("    public int getNumber() {\n        return number;\n    }\n\n")
                .append("    public String getMessage() {\n        return message;\n    }\n\n")
                .append("    public static PlatformErrorCode byNumber(int number) {\n        return null;\n    }\n}\n")
                .toString();
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

    @Test
    public void method_annotatedParametersWithParentheses_countOnce() {
        assertThat(
                count(
                        METHOD_PATTERN,
                        "    public void handle(@Qualifier(\"in\") Callback cb,\n"
                                + "            @Size(max = 3) List<String> names) throws IOException {\n    }\n"
                                + "    abstract int size(@Min(0) int from);\n"),
                is(2));
    }

    @Test
    public void method_longEnum_staysLinear() {
        final String source = longEnum();
        // getNumber, getMessage and byNumber; the constructor has no modifier and no return type
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(METHOD_PATTERN, source)), is(3));
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
    public void field_initializerOverManyLines_counts() {
        assertThat(
                count(FIELD_PATTERN, "    private static final int[] A = {\n        1,\n        2,\n    };\n"), is(1));
        assertThat(
                count(
                        FIELD_PATTERN,
                        "    private final Runnable r = new Runnable() {\n        public void run() {\n"
                                + "            go();\n        }\n    };\n"),
                is(1));
    }

    @Test
    public void field_initializerLongerThanTenThousandCharacters_doesNotCount() {
        // the initializer is read up to 10,000 characters, which keeps the pattern linear over a file
        // of declarations without their semicolons
        final String table = "    static final String T = \"" + "x".repeat(10_000) + "\";\n";
        assertThat(count(FIELD_PATTERN, table), is(0));
        assertThat(count(FIELD_PATTERN, "    static final String T = \"" + "x".repeat(9_000) + "\";\n"), is(1));
    }

    @Test
    public void field_genericTypeOverTwoLines_doesNotCount() {
        // type arguments are read within one line, so a declaration without its ">" cannot rescan the file
        assertThat(count(FIELD_PATTERN, "    private Map<String,\n        Integer> m;\n"), is(0));
    }

    @Test
    public void method_typeParametersOverTwoLines_doNotCount() {
        assertThat(count(METHOD_PATTERN, "    public <K,\n        V> void put(K k, V v) {\n"), is(0));
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

    @Test
    public void ctor_annotatedParametersWithParentheses_countOnce() {
        assertThat(
                count(
                        CTOR_PATTERN,
                        "    public Foo(@Named(\"a\") String a,\n            @Size(max = 3) List<String> b) {\n    }\n"),
                is(1));
    }

    @Test
    public void ctor_enumConstantsWithArguments_doNotCount() {
        assertThat(
                count(
                        CTOR_PATTERN,
                        "enum E {\n    A(1, \"a\"),\n    B(2, \"b\");\n    E(int n, String s) {\n    }\n}\n"),
                is(1));
    }

    @Test
    public void ctor_longEnum_staysLinear() {
        final String source = longEnum();
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(CTOR_PATTERN, source)), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="type declarations">
    @Test
    public void typeDeclarations_topLevelNestedAndAnnotationTypes_count() {
        assertThat(
                count(
                        TYPE_PATTERN,
                        "public class A {\n    private static final class B {\n    }\n    protected interface C {\n    }\n"
                                + "    enum D { X }\n    record E(int x) {\n    }\n    public @interface F {\n    }\n"
                                + "    sealed interface G permits H {\n    }\n    non-sealed class H implements G {\n    }\n}\n"),
                is(8));
    }

    @Test
    public void typeDeclarations_classLiteralsCommentsAndAnonymousClasses_doNotCount() {
        assertThat(
                count(
                        TYPE_PATTERN,
                        "Class<?> c = Foo.class;\n// class Old {\n * the class Foo reads\nRunnable r = new Runnable() {\n};\n"),
                is(0));
    }

    @Test
    public void typeDeclarations_afterManyBlankLines_stayLinear() {
        final String source = "\n".repeat(LONG_BLANK_LINES) + "int x;\nclass A {\n}\n";
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(TYPE_PATTERN, source)), is(1));
    }
    // </editor-fold>
}

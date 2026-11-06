// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the Java structural fact counters of the current profile ({@code profiles/v3/java-facts.tsv},
 * read through {@link ProfileFactPatterns}, so the shipped file is what is tested), counted through
 * {@link FactExtractor#counts}, so the [ignore] line blanks comments and literals first as in the facts
 * line, against the tricky-case self-tests. These are deliberately approximate ("methods (approx)", "field declarations") - the tests pin the accepted
 * false-positive/negative boundary so the patterns cannot silently drift.
 */
public class JavaStructureFactPatternsTest {

    private static final List<FactCounter> COUNTERS = ProfileFactPatterns.counters("java-facts");

    /** Methods: modifiers, type params, generic/array return, multi-line params, throws, {@code {} or ;}. */
    private static final String METHODS = "methods (approx)";

    /** Field declarations that carry a STRONG modifier (final-only excluded, to skip final locals). */
    private static final String FIELDS = "field declarations (w/ modifier)";

    /** Constructors: capitalized name directly followed by {@code (}, ending in {@code {}. */
    private static final String CONSTRUCTORS = "constructors";

    /** Type declarations: classes, interfaces, annotation types, enums and records, nested ones too. */
    private static final String TYPES = "type declarations";

    /** Constants of the long enum in the run-time tests, as many as the large corpus's enum has. */
    private static final int LONG_ENUM_CONSTANTS = 6_000;

    /** Blank lines in front of a declaration in the run-time test of the type declarations. */
    private static final int LONG_BLANK_LINES = 40_000;

    /** What a long input may take; the quadratic parameter list took about 8 s on the 6 000-constant enum. */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    /** Counts one counter of the file over a source as the facts line does: the [ignore] line blanks first. */
    private static int count(final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(COUNTERS, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
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
        // act, assert
        assertThat(count(METHODS, "@Override\npublic int foo() {\n}"), is(1));
    }

    @Test
    public void method_annotatedOnTheSameLine_counts() {
        // act, assert
        assertThat(
                count(
                        METHODS,
                        "    @Override public void run() {\n    }\n"
                                + "    @SuppressWarnings(\"x\") @Deprecated private static int f(int a) {\n    }\n"),
                is(2));
    }

    @Test
    public void method_nestedGenericBounds_count() {
        // act, assert
        assertThat(
                count(
                        METHODS,
                        "    public static <T extends Comparable<T>> T max(List<T> xs) {\n    }\n"
                                + "    <K, V extends Map<K, List<V>>> void put(K k, V v);\n"),
                is(2));
    }

    /** A switch expression's yield and a record header have a method's shape, but are none. */
    @Test
    public void method_yieldAndARecordHeader_doNotCount() {
        // act, assert
        assertThat(
                count(
                        METHODS,
                        "int x = switch (k) {\n    case 1 -> {\n        yield f(x);\n    }\n    default -> 0;\n};\n"
                                + "record Point(int x, int y) {\n}\npublic record Pair(String a, String b) {}\n"),
                is(0));
    }

    @Test
    public void method_genericMultilineThrows_counts() {
        // act, assert
        assertThat(count(METHODS, "public <T> T bar(List<T> x,\n int y) throws IOException {\n}"), is(1));
    }

    @Test
    public void method_arrayReturn_counts() {
        // act, assert
        assertThat(count(METHODS, "int[] baz() {\n}"), is(1));
    }

    @Test
    public void method_abstractSemicolon_counts() {
        // act, assert
        assertThat(count(METHODS, "abstract void run();"), is(1));
    }

    @Test
    public void method_annotationElementsWithDefaults_count() {
        // arrange
        final String annotation = "public @interface Check {\n"
                + "    String value() default \"\";\n"
                + "    int max() default 10;\n"
                + "    Class<?>[] groups() default {};\n"
                + "    String[] names() default {\"a\", \"b\"};\n"
                + "    Size size() default @Size(max = 3);\n"
                + "    Kind kind();\n"
                + "}\n";

        // act, assert
        assertThat(count(METHODS, annotation), is(6));
    }

    @Test
    public void method_controlStructures_doNotCount() {
        // act, assert
        assertThat(count(METHODS, "if (foo()) { bar(); }"), is(0));
        assertThat(count(METHODS, "        for (int i = 0; i < n; i++) {\n}"), is(0));
    }

    @Test
    public void method_calls_doNotCount() {
        // act, assert
        assertThat(count(METHODS, "        obj.doThing(x);\n        foo(y);"), is(0));
        assertThat(count(METHODS, "        return compute(x);"), is(0));
    }

    @Test
    public void method_lambda_doesNotCount() {
        // act, assert
        assertThat(count(METHODS, "        Runnable r = () -> doWork();"), is(0));
    }

    @Test
    public void method_annotatedParametersWithParentheses_countOnce() {
        // act, assert
        assertThat(
                count(
                        METHODS,
                        "    public void handle(@Qualifier(\"in\") Callback cb,\n"
                                + "            @Size(max = 3) List<String> names) throws IOException {\n    }\n"
                                + "    abstract int size(@Min(0) int from);\n"),
                is(2));
    }

    @Test
    public void method_longEnum_staysLinear() {
        // arrange
        final String source = longEnum();

        // act, assert
        // getNumber, getMessage and byNumber; the constructor has no modifier and no return type
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(METHODS, source)), is(3));
    }

    @Test
    public void method_typeParametersOverTwoLines_doNotCount() {
        // act, assert
        assertThat(count(METHODS, "    public <K,\n        V> void put(K k, V v) {\n"), is(0));
    }

    @Test
    public void method_throwsOnTheNextLine_counts() {
        // arrange
        final String source = "    public static void main(final String[] args)\n"
                + "            throws IOException {\n    }\n"
                + "    void read(int a)\r\n        throws IOException,\r\n            TimeoutException;\r\n";

        // act, assert
        assertThat(count(METHODS, source), is(2));
    }

    @Test
    public void method_braceOnTheNextLine_counts() {
        // arrange
        final String source = "    public int size()\n    {\n        return 0;\n    }\n"
                + "    private void f(int a) throws IOException\n    {\n    }\n";

        // act, assert
        assertThat(count(METHODS, source), is(2));
    }

    @Test
    public void method_controlStructuresWithTheBraceOnTheNextLine_doNotCount() {
        // act, assert
        assertThat(count(METHODS, "    if (ready())\n    {\n    }\n    while (more())\n    {\n    }\n"), is(0));
    }

    @Test
    public void method_thousandsOfAnnotationsOnOneLine_doNotOverflowTheStack() {
        // arrange
        final String some = FactPatternLinearity.repeat("@A @B(x) ", 30);
        final String thousands = FactPatternLinearity.repeat("@A @B(x) ", 20_000);

        // act, assert
        // up to 64 modifiers and annotations count with their declaration
        assertThat(count(METHODS, some + "void run() {\n}\n"), is(1));
        assertThat(count(FIELDS, some + "private int x;\n"), is(1));
        assertThat(count(CONSTRUCTORS, some + "public A(int a) {\n}\n"), is(1));
        // more are read without a stack overflow: a type declaration still counts, the others do not
        assertThat(count(TYPES, thousands + "class A {\n}\n"), is(1));
        assertThat(count(METHODS, thousands + "void run() {\n}\n"), is(0));
        assertThat(count(FIELDS, thousands + "private int x;\n"), is(0));
        assertThat(count(CONSTRUCTORS, thousands + "public A(int a) {\n}\n"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fields">
    @Test
    public void field_multipleDeclarators_countAsOneStatement() {
        // act, assert
        // FactExtractor counts matches (statements), so "int a, b, c;" is ONE field declaration.
        assertThat(count(FIELDS, "    private int a, b, c;"), is(1));
    }

    @Test
    public void field_staticFinalWithInit_counts() {
        // act, assert
        assertThat(count(FIELDS, "    public static final String NAME = \"x\";"), is(1));
    }

    @Test
    public void field_genericType_counts() {
        // act, assert
        assertThat(count(FIELDS, "    private Map<String, List<X>> m;"), is(1));
    }

    @Test
    public void field_initializerOverManyLines_counts() {
        // act, assert
        assertThat(count(FIELDS, "    private static final int[] A = {\n        1,\n        2,\n    };\n"), is(1));
        assertThat(
                count(
                        FIELDS,
                        "    private final Runnable r = new Runnable() {\n        public void run() {\n"
                                + "            go();\n        }\n    };\n"),
                is(1));
    }

    @Test
    public void field_initializerLongerThanTenThousandCharacters_doesNotCount() {
        // arrange
        // the initializer is read up to 10,000 characters, which keeps the pattern linear over a file
        // of declarations without their semicolons; an array, since the [ignore] line blanks a string
        final String table = "    static final int[] T = {" + "1,".repeat(5_000) + "};\n";
        final String shorter = "    static final int[] T = {" + "1,".repeat(4_500) + "};\n";

        // act, assert
        assertThat(count(FIELDS, table), is(0));
        assertThat(count(FIELDS, shorter), is(1));
    }

    @Test
    public void field_longStringInitializer_countsSinceTheStringIsBlanked() {
        // act, assert
        assertThat(count(FIELDS, "    static final String T = \"" + "x".repeat(20_000) + "\";\n"), is(1));
    }

    @Test
    public void field_genericTypeOverTwoLines_doesNotCount() {
        // act, assert
        // type arguments are read within one line, so a declaration without its ">" cannot rescan the file
        assertThat(count(FIELDS, "    private Map<String,\n        Integer> m;\n"), is(0));
    }

    @Test
    public void field_transientArray_counts() {
        // act, assert
        assertThat(count(FIELDS, "    transient Object[] elementData;"), is(1));
    }

    @Test
    public void field_localsOrFinalLocals_doNotCount() {
        // act, assert
        assertThat(count(FIELDS, "        int local = 5;"), is(0));
        assertThat(count(FIELDS, "        final int s;"), is(0));
        assertThat(count(FIELDS, "        final Object[] es = elementData;"), is(0));
    }

    @Test
    public void field_annotatedOnTheSameLine_counts() {
        // act, assert
        assertThat(
                count(FIELDS, "    @Inject private Clock clock;\n    @Column(name = \"x\") protected int x = 1;\n"),
                is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructors">
    @Test
    public void ctor_publicConstructor_counts() {
        // act, assert
        assertThat(count(CONSTRUCTORS, "    public Foo(int x) {\n}"), is(1));
    }

    @Test
    public void ctor_lowercaseMethod_doesNotCount() {
        // act, assert
        assertThat(count(CONSTRUCTORS, "    public void foo(int x) {\n}"), is(0));
    }

    @Test
    public void ctor_annotatedParametersWithParentheses_countOnce() {
        // act, assert
        assertThat(
                count(
                        CONSTRUCTORS,
                        "    public Foo(@Named(\"a\") String a,\n            @Size(max = 3) List<String> b) {\n    }\n"),
                is(1));
    }

    /**
     * An enum constant with a body has a constructor's shape; its arguments are values, not parameters,
     * and the [ignore] line turns {@code PLUS("+")} into {@code PLUS( )}, which reads as no arguments.
     */
    @Test
    public void ctor_enumConstantsWithABody_doNotCount() {
        // act, assert
        assertThat(
                count(
                        CONSTRUCTORS,
                        "enum Op {\n    PLUS(\"+\") {\n        int apply(int a) { return a; }\n    },\n"
                                + "    MINUS(1) {\n    },\n    TIMES(new Sym('*')) {\n    },\n    DIV(Sym.SLASH) {\n    },\n"
                                + "    NEG() {\n    },\n    @Deprecated ZERO( ) {\n    },\n    QUOTE('\"', \"x\") {\n    };\n"
                                + "    Op(String s) {\n    }\n    Op() {\n    }\n}\n"),
                is(2));
    }

    @Test
    public void ctor_noParametersWithAnAccessModifierOrALowerCaseLetter_counts() {
        // act, assert
        assertThat(
                count(
                        CONSTRUCTORS,
                        "    public URL() {\n    }\n    @Inject protected DAO() {\n    }\n    Op() {\n    }\n"),
                is(3));
    }

    /** The accepted false negative: a package-private constructor without parameters reads as a constant. */
    @Test
    public void ctor_noParametersNoAccessModifierAndAllCapitals_doesNotCount() {
        // act, assert
        assertThat(count(CONSTRUCTORS, "class DAO {\n    DAO() {\n    }\n}\n"), is(0));
    }

    @Test
    public void ctor_parametersOnTheNextLine_count() {
        // act, assert
        assertThat(
                count(CONSTRUCTORS, "    Service(\n            final Repo r,\n            int n) {\n    }\n"), is(1));
    }

    @Test
    public void ctor_annotatedOnTheSameLine_counts() {
        // act, assert
        assertThat(count(CONSTRUCTORS, "    @Inject public Service(final Repo r) {\n    }\n"), is(1));
    }

    @Test
    public void ctor_enumConstantsWithArguments_doNotCount() {
        // act, assert
        assertThat(
                count(
                        CONSTRUCTORS,
                        "enum E {\n    A(1, \"a\"),\n    B(2, \"b\");\n    E(int n, String s) {\n    }\n}\n"),
                is(1));
    }

    @Test
    public void ctor_longEnum_staysLinear() {
        // arrange
        final String source = longEnum();

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(CONSTRUCTORS, source)), is(1));
    }

    @Test
    public void ctor_throwsAndBraceOnTheNextLines_count() {
        // arrange
        final String source = "    public Service(final Repo r)\n            throws IOException {\n    }\n"
                + "    Service(int n)\n    {\n    }\n"
                + "    protected Service(String s)\r\n        throws IOException\r\n    {\r\n    }\r\n";

        // act, assert
        assertThat(count(CONSTRUCTORS, source), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="type declarations">
    @Test
    public void typeDeclarations_topLevelNestedAndAnnotationTypes_count() {
        // act, assert
        assertThat(
                count(
                        TYPES,
                        "public class A {\n    private static final class B {\n    }\n    protected interface C {\n    }\n"
                                + "    enum D { X }\n    record E(int x) {\n    }\n    public @interface F {\n    }\n"
                                + "    sealed interface G permits H {\n    }\n    non-sealed class H implements G {\n    }\n}\n"),
                is(8));
    }

    @Test
    public void typeDeclarations_annotatedOnTheSameLine_count() {
        // act, assert
        assertThat(
                count(
                        TYPES,
                        "@Entity public class Order {\n}\n@Table(name = \"lines\") @Deprecated final class Line {\n}\n"
                                + "@Retention(RUNTIME) public @interface Marker {\n}\n"
                                + "@JsonSubTypes({@Type(A.class), @Type(B.class)}) sealed interface Shape {\n}\n"),
                is(4));
    }

    @Test
    public void typeDeclarations_classLiteralsCommentsAndAnonymousClasses_doNotCount() {
        // act, assert
        assertThat(
                count(
                        TYPES,
                        "Class<?> c = Foo.class;\n// class Old {\n * the class Foo reads\nRunnable r = new Runnable() {\n};\n"),
                is(0));
    }

    @Test
    public void typeDeclarations_afterManyBlankLines_stayLinear() {
        // arrange
        final String source = "\n".repeat(LONG_BLANK_LINES) + "int x;\nclass A {\n}\n";

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(TYPES, source)), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the whole file: [ignore] and [raw]">
    /** A class whose comments, Javadoc, strings, text block and char literal name declarations. */
    private static final String COMMENTED_CLASS = "/** A public class Example: {@code public void run()} */\n"
            + "public class A {\n"
            + "    // TODO public later\n"
            + "    private static final String S = \"public class X // not a comment\";\n"
            + "    private static final String T = \"\"\"\n"
            + "        public class Y \" \\\"\"\" still the block\n"
            + "        \"\"\";\n"
            + "    /* FIXME */ public void run() {\n"
            + "    }\n"
            + "    char c = '\"';\n"
            + "    char d = '\\'';\n"
            + "    public int x = 1;\n"
            + "}\n";

    @Test
    public void wholeFile_commentsStringsAndTextBlocks_doNotCount() {
        // act
        final Map<String, Integer> counts =
                ProfileFactPatterns.counts(ProfileFactPatterns.counters("java-facts"), COMMENTED_CLASS);

        // assert
        assertThat(counts.get("type declarations"), is(1));
        assertThat(counts.get("public declarations"), is(3));
        assertThat(counts.get("methods (approx)"), is(1));
        assertThat(counts.get("field declarations (w/ modifier)"), is(3));
    }

    @Test
    public void wholeFile_todosInComments_count() {
        // act
        final Map<String, Integer> counts =
                ProfileFactPatterns.counts(ProfileFactPatterns.counters("java-facts"), COMMENTED_CLASS);

        // assert
        assertThat(counts.get("TODO/FIXME"), is(2));
    }

    @Test
    public void wholeFile_unclosedBlockCommentAndTextBlock_runToTheEnd() {
        // act
        final Map<String, Integer> comment =
                ProfileFactPatterns.counts(ProfileFactPatterns.counters("java-facts"), "public class A {\n/* public\n");
        final Map<String, Integer> textBlock = ProfileFactPatterns.counts(
                ProfileFactPatterns.counters("java-facts"), "public class A {\nString s = \"\"\"\npublic\n");

        // assert
        assertThat(comment.get("public declarations"), is(1));
        assertThat(textBlock.get("public declarations"), is(1));
    }

    @Test
    public void wholeFile_stringNotClosedOnItsLine_isBlankedToTheLineEnd() {
        // act
        final Map<String, Integer> counts = ProfileFactPatterns.counts(
                ProfileFactPatterns.counters("java-facts"),
                "String s = \"public\npublic class A {\nchar c = 'public\n");

        // assert
        // only the class line: each unclosed literal ends at its line end, so a line of escaped quotes stays linear
        assertThat(counts.get("public declarations"), is(1));
    }

    @Test
    public void wholeFile_overrideAnnotations_countWithOrWithoutThePackage() {
        // arrange
        final String source = "class A {\n"
                + "    @Override\n    public String toString() {\n        return \"@Override\";\n    }\n"
                + "    @java.lang.Override public int hashCode() {\n        return 0;\n    }\n"
                + "    // @Override\n"
                + "    @Overrides void f() {\n    }\n"
                + "}\n";

        // act
        final Map<String, Integer> counts =
                ProfileFactPatterns.counts(ProfileFactPatterns.counters("java-facts"), source);

        // assert
        // not the one in a string, the commented-out one nor @Overrides
        assertThat(counts.get("@Override"), is(2));
    }
    // </editor-fold>
}

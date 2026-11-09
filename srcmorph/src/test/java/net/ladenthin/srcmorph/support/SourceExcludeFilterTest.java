// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class SourceExcludeFilterTest {

    // <editor-fold defaultstate="collapsed" desc="empty / null / blank">
    @Test
    public void isExcluded_emptyPatternList_excludesNothing() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.<String>emptyList());

        // act, assert
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }

    @Test
    public void isExcluded_nullPatternList_excludesNothing() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(null);

        // act, assert
        assertThat(filter.isExcluded("anything.java"), is(false));
    }

    @Test
    public void isExcluded_nullAndBlankEntries_areIgnored() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Arrays.asList(null, "", "   ", "\t"));

        // act, assert
        assertThat(filter.isExcluded("anything.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="** / matches at root and nested">
    @Test
    public void isExcluded_doubleStarSlash_matchesAtRoot() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));

        // act, assert
        assertThat(filter.isExcluded("package-info.java"), is(true));
    }

    @Test
    public void isExcluded_doubleStarSlash_matchesNested() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));

        // act, assert
        assertThat(filter.isExcluded("src/main/java/com/example/package-info.java"), is(true));
    }

    @Test
    public void isExcluded_doubleStarSlash_doesNotMatchDifferentFile() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));

        // act, assert
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="* stays within a segment, ** spans segments">
    @Test
    public void isExcluded_singleStar_matchesWithinSegment() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("*.java"));

        // act, assert
        assertThat(filter.isExcluded("Foo.java"), is(true));
    }

    @Test
    public void isExcluded_singleStar_doesNotCrossSeparator() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("*.java"));

        // act, assert
        assertThat(filter.isExcluded("com/Foo.java"), is(false));
    }

    @Test
    public void isExcluded_trailingSingleStar_matchesRestOfSegmentOnly() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("Bar*"));

        // act, assert
        assertThat(filter.isExcluded("Bar123"), is(true));
        assertThat(filter.isExcluded("Bar/123"), is(false));
    }

    @Test
    public void isExcluded_trailingDoubleStar_spansSegments() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("target/**"));

        // act, assert
        assertThat(filter.isExcluded("target/generated-sources/com/Foo.java"), is(true));
    }

    @Test
    public void isExcluded_doubleStarInTheMiddle_spansSegments() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/generated/**"));

        // act, assert
        assertThat(filter.isExcluded("src/main/java/com/generated/Foo.java"), is(true));
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="? single char">
    @Test
    public void isExcluded_questionMark_matchesExactlyOneNonSeparatorChar() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("A?.java"));

        // act, assert
        assertThat(filter.isExcluded("Ab.java"), is(true));
        assertThat(filter.isExcluded("Abc.java"), is(false));
    }

    @Test
    public void isExcluded_questionMark_doesNotMatchSeparator() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("a?b"));

        // act, assert
        assertThat(filter.isExcluded("a/b"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="literal escaping">
    @Test
    public void isExcluded_dot_isLiteralNotAnyChar() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("Foo.java"));

        // act, assert
        assertThat(filter.isExcluded("Foo.java"), is(true));
        // The '.' must be escaped, so it does NOT match an arbitrary character.
        assertThat(filter.isExcluded("FooXjava"), is(false));
    }

    @Test
    public void isExcluded_regexMetacharacters_areTreatedLiterally() {
        // arrange
        // Every metacharacter the translator escapes, in one literal pattern. If any were left
        // unescaped the regex would either fail to compile or fail to match itself.
        final String literal = "a(b)[c]}d+e^f$g|h.txt";
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList(literal));

        // act, assert
        assertThat(filter.isExcluded(literal), is(true));
    }

    @Test
    public void isExcluded_braces_areAlternatives() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/{target,build}/**"));

        // act, assert
        assertThat(filter.isExcluded("target/a.java"), is(true));
        assertThat(filter.isExcluded("m/build/a.java"), is(true));
        assertThat(filter.isExcluded("m/src/a.java"), is(false));
        assertThat(filter.isExcluded("{target,build}/a.java"), is(false));
    }

    @Test
    public void isExcluded_backslash_isTreatedLiterally() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("a\\b.txt"));

        // act, assert
        assertThat(filter.isExcluded("a\\b.txt"), is(true));
        assertThat(filter.isExcluded("ab.txt"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="multiple patterns / no match">
    @Test
    public void isExcluded_multiplePatterns_anyMatchExcludes() {
        // arrange
        final SourceExcludeFilter filter =
                new SourceExcludeFilter(Arrays.asList("**/package-info.java", "**/module-info.java"));

        // act, assert
        assertThat(filter.isExcluded("com/module-info.java"), is(true));
    }

    @Test
    public void isExcluded_noPatternMatches_returnsFalse() {
        // arrange
        final SourceExcludeFilter filter =
                new SourceExcludeFilter(Arrays.asList("**/package-info.java", "**/module-info.java"));

        // act, assert
        assertThat(filter.isExcluded("com/example/Service.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isExcludedDirectory">
    @Test
    public void isExcludedDirectory_trailingDoubleStar_takesTheDirectoryAndEveryOneBelowIt() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList(" target/** "));

        // act, assert
        assertThat(filter.isExcludedDirectory("target"), is(true));
        assertThat(filter.isExcluded("target/a/b.java"), is(true));
        assertThat(filter.isExcludedDirectory("src"), is(false));
        assertThat(filter.isExcludedDirectory("src/target"), is(false));
        assertThat(filter.isExcludedDirectory("targets"), is(false));
    }

    @Test
    public void isExcludedDirectory_doubleStarOnBothSidesAndAlternatives_takesEveryDirectoryOfThatName() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/{target,build}/**"));

        // act, assert
        assertThat(filter.isExcludedDirectory("target"), is(true));
        assertThat(filter.isExcludedDirectory("m/build"), is(true));
        assertThat(filter.isExcludedDirectory("m/src"), is(false));
    }

    @Test
    public void isExcludedDirectory_excludesNotEndingInSlashDoubleStar_takeNoDirectory() {
        // arrange: each excludes files in the directory, none every file below it
        final SourceExcludeFilter filter =
                new SourceExcludeFilter(Arrays.asList("target/*", "target**", "target/**/*.java", "**/", "target"));

        // act, assert
        assertThat(filter.isExcludedDirectory("target"), is(false));
        assertThat(filter.isExcludedDirectory("target/a"), is(false));
    }

    @Test
    public void isExcludedDirectory_theBaseDirectory_isNeverTaken() {
        // arrange: */** excludes no file at the base root itself, and /** none at all
        final SourceExcludeFilter filter = new SourceExcludeFilter(Arrays.asList("*/**", "/**"));

        // act, assert
        assertThat(filter.isExcluded("Foo.java"), is(false));
        assertThat(filter.isExcludedDirectory(""), is(false));
        assertThat(filter.isExcludedDirectory("a"), is(true));
    }

    @Test
    public void isExcludedDirectory_noExcludes_takesNoDirectory() {
        // arrange
        final SourceExcludeFilter filter = new SourceExcludeFilter(null);

        // act, assert
        assertThat(filter.isExcludedDirectory("target"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_unclosedBrace_throwsException() {
        // act, assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new SourceExcludeFilter(Collections.singletonList("**/{target/**")));
    }
    // </editor-fold>
}

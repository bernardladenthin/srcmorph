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
    public void emptyPatternList_excludesNothing() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.<String>emptyList());
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }

    @Test
    public void nullPatternList_excludesNothing() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(null);
        assertThat(filter.isExcluded("anything.java"), is(false));
    }

    @Test
    public void nullAndBlankEntriesAreIgnored() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Arrays.asList(null, "", "   ", "\t"));
        assertThat(filter.isExcluded("anything.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="** / matches at root and nested">
    @Test
    public void doubleStarSlash_matchesAtRoot() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));
        assertThat(filter.isExcluded("package-info.java"), is(true));
    }

    @Test
    public void doubleStarSlash_matchesNested() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));
        assertThat(filter.isExcluded("src/main/java/com/example/package-info.java"), is(true));
    }

    @Test
    public void doubleStarSlash_doesNotMatchDifferentFile() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/package-info.java"));
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="* stays within a segment, ** spans segments">
    @Test
    public void singleStar_matchesWithinSegment() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("*.java"));
        assertThat(filter.isExcluded("Foo.java"), is(true));
    }

    @Test
    public void singleStar_doesNotCrossSeparator() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("*.java"));
        assertThat(filter.isExcluded("com/Foo.java"), is(false));
    }

    @Test
    public void trailingSingleStar_matchesRestOfSegmentOnly() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("Bar*"));
        assertThat(filter.isExcluded("Bar123"), is(true));
        assertThat(filter.isExcluded("Bar/123"), is(false));
    }

    @Test
    public void trailingDoubleStar_spansSegments() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("target/**"));
        assertThat(filter.isExcluded("target/generated-sources/com/Foo.java"), is(true));
    }

    @Test
    public void doubleStarInTheMiddle_spansSegments() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/generated/**"));
        assertThat(filter.isExcluded("src/main/java/com/generated/Foo.java"), is(true));
        assertThat(filter.isExcluded("src/main/java/com/example/Foo.java"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="? single char">
    @Test
    public void questionMark_matchesExactlyOneNonSeparatorChar() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("A?.java"));
        assertThat(filter.isExcluded("Ab.java"), is(true));
        assertThat(filter.isExcluded("Abc.java"), is(false));
    }

    @Test
    public void questionMark_doesNotMatchSeparator() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("a?b"));
        assertThat(filter.isExcluded("a/b"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="literal escaping">
    @Test
    public void dotIsLiteral_notAnyChar() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("Foo.java"));
        assertThat(filter.isExcluded("Foo.java"), is(true));
        // The '.' must be escaped, so it does NOT match an arbitrary character.
        assertThat(filter.isExcluded("FooXjava"), is(false));
    }

    @Test
    public void regexMetacharactersAreTreatedLiterally() {
        // Every metacharacter the translator escapes, in one literal pattern. If any were left
        // unescaped the regex would either fail to compile or fail to match itself.
        final String literal = "a(b)[c]}d+e^f$g|h.txt";
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList(literal));
        assertThat(filter.isExcluded(literal), is(true));
    }

    @Test
    public void bracesAreAlternatives() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("**/{target,build}/**"));
        assertThat(filter.isExcluded("target/a.java"), is(true));
        assertThat(filter.isExcluded("m/build/a.java"), is(true));
        assertThat(filter.isExcluded("m/src/a.java"), is(false));
        assertThat(filter.isExcluded("{target,build}/a.java"), is(false));
    }

    @Test
    public void unclosedBrace_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SourceExcludeFilter(Collections.singletonList("**/{target/**")));
    }

    @Test
    public void backslashIsTreatedLiterally() {
        final SourceExcludeFilter filter = new SourceExcludeFilter(Collections.singletonList("a\\b.txt"));
        assertThat(filter.isExcluded("a\\b.txt"), is(true));
        assertThat(filter.isExcluded("ab.txt"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="multiple patterns / no match">
    @Test
    public void multiplePatterns_anyMatchExcludes() {
        final SourceExcludeFilter filter =
                new SourceExcludeFilter(Arrays.asList("**/package-info.java", "**/module-info.java"));
        assertThat(filter.isExcluded("com/module-info.java"), is(true));
    }

    @Test
    public void noPatternMatches_returnsFalse() {
        final SourceExcludeFilter filter =
                new SourceExcludeFilter(Arrays.asList("**/package-info.java", "**/module-info.java"));
        assertThat(filter.isExcluded("com/example/Service.java"), is(false));
    }
    // </editor-fold>
}

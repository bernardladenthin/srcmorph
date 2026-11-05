// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class GlobTest {

    private static boolean matches(final String glob, final String path) {
        return Glob.compile(glob, false).matcher(path).matches();
    }

    // <editor-fold defaultstate="collapsed" desc="stars and question marks">
    @Test
    public void star_matchesWithinOneSegment() {
        assertThat(matches("*.java", "Foo.java"), is(true));
        assertThat(matches("*.java", "a/Foo.java"), is(false));
    }

    @Test
    public void doubleStarSlash_matchesZeroOrMoreDirectories() {
        assertThat(matches("**/Foo.java", "Foo.java"), is(true));
        assertThat(matches("**/Foo.java", "a/b/Foo.java"), is(true));
        assertThat(matches("a/**", "a/b/c"), is(true));
        assertThat(matches("a/**", "b/c"), is(false));
    }

    @Test
    public void questionMark_matchesOneCharacterExceptASlash() {
        assertThat(matches("?.sql", "a.sql"), is(true));
        assertThat(matches("?.sql", "ab.sql"), is(false));
        assertThat(matches("a?b", "a/b"), is(false));
    }

    @Test
    public void regexCharacters_areLiterals() {
        assertThat(matches("a.b+(c)[d]^$|\\e", "a.b+(c)[d]^$|\\e"), is(true));
        assertThat(matches("a.b", "axb"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="alternatives">
    @Test
    public void alternatives_matchAnyOne() {
        assertThat(matches("*.{yml,yaml}", "a.yml"), is(true));
        assertThat(matches("*.{yml,yaml}", "a.yaml"), is(true));
        assertThat(matches("*.{yml,yaml}", "a.json"), is(false));
        assertThat(matches("*.{yml,yaml}", "a.yml,yaml"), is(false));
    }

    @Test
    public void alternatives_holdGlobsAndNest() {
        assertThat(matches("{src/**/*.java,**/{k8s,helm}/*.yaml}", "src/a/B.java"), is(true));
        assertThat(matches("{src/**/*.java,**/{k8s,helm}/*.yaml}", "deploy/helm/v.yaml"), is(true));
        assertThat(matches("{src/**/*.java,**/{k8s,helm}/*.yaml}", "deploy/docs/v.yaml"), is(false));
    }

    @Test
    public void alternatives_anEmptyOneMatchesNothingThere() {
        assertThat(matches("pom{,-parent}.xml", "pom.xml"), is(true));
        assertThat(matches("pom{,-parent}.xml", "pom-parent.xml"), is(true));
    }

    @Test
    public void commaAndClosingBraceOutsideAnAlternative_areLiterals() {
        assertThat(matches("a,b", "a,b"), is(true));
        assertThat(matches("a}b", "a}b"), is(true));
        assertThat(matches("{a,b}}", "a}"), is(true));
    }

    @Test
    public void unclosedAlternative_throws() {
        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> Glob.compile("*.{yml,yaml", false));
        assertThat(thrown.getMessage(), is("the glob '*.{yml,yaml' opens a '{' it does not close"));
        assertThrows(IllegalArgumentException.class, () -> Glob.compile("{a,{b}", false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="case">
    @Test
    public void ignoreCase_matchesLettersInAnyCase() {
        assertThat(Glob.compile("Dockerfile", true).matcher("DOCKERFILE").matches(), is(true));
        assertThat(Glob.compile("Dockerfile", false).matcher("DOCKERFILE").matches(), is(false));
        assertThat(Glob.compile("ärger", true).matcher("ÄRGER").matches(), is(true));
    }

    @Test
    public void toRegex_isAnchored() {
        assertThat(Glob.toRegex("a"), is("^a$"));
        assertThat(Glob.toRegex("**/x.{a,b}"), is("^(?:.*/)?x\\.(?:a|b)$"));
    }
    // </editor-fold>
}

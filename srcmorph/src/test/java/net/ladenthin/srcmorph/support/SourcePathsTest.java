// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SourcePathsTest {

    private final SourcePaths pathSupport = new SourcePaths();

    // <editor-fold defaultstate="collapsed" desc="relativizeFromSrc">
    /** A leading {@code src} segment goes; none, or one further down, leaves the path relative to the base. */
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
        "/project/src/main/java/Foo.java, main/java/Foo.java",
        "/project/main/Foo.java, main/Foo.java",
        "/project/main/src/Foo.java, main/src/Foo.java"
    })
    public void relativizeFromSrc_aPath_dropsOnlyALeadingSrcSegment(final String source, final String expected) {
        // arrange
        final Path base = Paths.get("/project");
        final Path path = Paths.get(source);

        // act, assert
        // The subpath(1, n) return is asserted, killing the null-return mutant on that branch.
        assertThat(pathSupport.relativizeFromSrc(base, path), is(Paths.get(expected)));
    }

    @Test
    public void relativizeFromSrc_leadingSrcInAnotherCase_keepsItOnEveryPlatform() {
        // arrange
        Path base = Paths.get("/project");
        Path path = Paths.get("/project/Src/main/Foo.java");

        // act
        final Path relative = pathSupport.relativizeFromSrc(base, path);

        // assert: as a string, since a Windows path compares without case; only a file system that
        // ignores the case (Windows) can tell this from the old Path#startsWith, so CI on Linux does not
        assertThat(relative.toString().replace('\\', '/'), is("Src/main/Foo.java"));
    }

    @Test
    public void relativizeFromSrc_srcItself_isTheEmptyPath() {
        // arrange: a subtree "src", the package phase's root
        Path base = Paths.get("/project");

        // act, assert
        assertThat(pathSupport.relativizeFromSrc(base, base.resolve("src")).toString(), is(""));
    }

    @Test
    public void relativizeFromSrc_theBaseItself_isTheEmptyPath() {
        // arrange
        Path base = Paths.get("/project");

        // act, assert
        assertThat(pathSupport.relativizeFromSrc(base, base).toString(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="resolveAgainst">
    @Test
    public void resolveAgainst_relativePath_isTakenInsideTheDirectory() {
        // arrange
        final File directory = new File("prompts");

        // act, assert
        assertThat(
                SourcePaths.resolveAgainst(directory, "v1/file-body-java.md"),
                is(new File(directory, "v1/file-body-java.md").getPath()));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_absolutePath_staysUnchanged() {
        // arrange
        final String absolute = new File("elsewhere.md").getAbsolutePath();

        // act, assert
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), absolute), is(absolute));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_rootedPath_staysUnchanged() {
        // act, assert
        // absolute on every platform; on Windows a root-relative path (no drive)
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), "/profiles/x.md"), is("/profiles/x.md"));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_absolutePathWithAnAbsoluteDirectory_staysUnchanged() {
        // arrange
        final String absolute = new File("elsewhere/x.md").getAbsolutePath();

        // act, assert
        assertThat(SourcePaths.resolveAgainst(new File("prompts").getAbsoluteFile(), absolute), is(absolute));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_relativePathBelowAnAbsoluteDirectory_becomesAbsolute() {
        // arrange
        final File directory = new File("prompts").getAbsoluteFile();

        // act
        final String resolved = SourcePaths.resolveAgainst(directory, "v1/x.md");

        // assert
        assertThat(resolved, is(new File(directory, "v1/x.md").getPath()));
        assertThat(new File(String.valueOf(resolved)).isAbsolute(), is(true));
    }

    @Test
    @AbsolutePathRequirement
    @EnabledOnOs(OS.WINDOWS)
    public void resolveAgainst_windowsRootOrDriveRelativePath_staysUnchanged() {
        // act, assert
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), "M:x.md"), is("M:x.md"));
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), "\\x.md"), is("\\x.md"));
    }

    @Test
    public void resolveAgainst_unparsablePath_staysUnchanged() {
        // act, assert
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), "bad\u0000.md"), is("bad\u0000.md"));
    }

    @Test
    public void resolveAgainst_withoutDirectory_staysRelativeToTheWorkingDirectory() {
        // act, assert
        assertThat(SourcePaths.resolveAgainst(null, "v1/x.md"), is("v1/x.md"));
    }

    @Test
    public void resolveAgainst_nullPath_staysNull() {
        // act, assert
        assertThat(SourcePaths.resolveAgainst(new File("prompts"), null), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasExtension">
    @Test
    public void hasExtension_withOrWithoutTheDotAndInAnyCase_isTrue() {
        // act, assert
        assertThat(SourcePaths.hasExtension("Foo.java", ".java"), is(true));
        assertThat(SourcePaths.hasExtension("Foo.java", "java"), is(true));
        assertThat(SourcePaths.hasExtension("Foo.JAVA", ".java"), is(true));
        assertThat(SourcePaths.hasExtension("Foo.java", " .Java "), is(true));
    }

    @Test
    public void hasExtension_nameEndingInTheExtension_theDotIsPartOfIt() {
        // act, assert
        assertThat(SourcePaths.hasExtension("README.cmd", "md"), is(false));
        assertThat(SourcePaths.hasExtension("README.md", "md"), is(true));
        assertThat(SourcePaths.hasExtension("md", "md"), is(false));
        assertThat(SourcePaths.hasExtension(".md", "md"), is(true));
    }

    @Test
    public void hasExtension_blankExtension_matchesNothing() {
        // act, assert
        assertThat(SourcePaths.hasExtension("Foo.java", ""), is(false));
        assertThat(SourcePaths.hasExtension("Foo.java", "  "), is(false));
    }
    // </editor-fold>
}

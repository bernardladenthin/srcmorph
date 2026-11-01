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

public class AiPathSupportTest {

    private final AiPathSupport pathSupport = new AiPathSupport();

    @Test
    public void stripsLeadingSrcSegment() {
        Path base = Paths.get("/project");
        Path path = Paths.get("/project/src/main/java/Foo.java");
        // The subpath(1, n) return is asserted, killing the null-return mutant on that branch.
        assertThat(pathSupport.relativizeFromSrc(base, path), is(Paths.get("main/java/Foo.java")));
    }

    @Test
    public void keepsPathWithoutLeadingSrc() {
        Path base = Paths.get("/project");
        Path path = Paths.get("/project/main/Foo.java");
        assertThat(pathSupport.relativizeFromSrc(base, path), is(Paths.get("main/Foo.java")));
    }

    @Test
    public void doesNotStripSrcWhenNotFirstSegment() {
        Path base = Paths.get("/project");
        Path path = Paths.get("/project/main/src/Foo.java");
        assertThat(pathSupport.relativizeFromSrc(base, path), is(Paths.get("main/src/Foo.java")));
    }

    // <editor-fold defaultstate="collapsed" desc="resolveAgainst">
    @Test
    public void resolveAgainst_relativePath_isTakenInsideTheDirectory() {
        final File directory = new File("prompts");

        assertThat(
                AiPathSupport.resolveAgainst(directory, "v1/file-body-java.md"),
                is(new File(directory, "v1/file-body-java.md").getPath()));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_absolutePath_staysUnchanged() {
        final String absolute = new File("elsewhere.md").getAbsolutePath();

        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), absolute), is(absolute));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_rootedPath_staysUnchanged() {
        // absolute on every platform; on Windows a root-relative path (no drive)
        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), "/profiles/x.md"), is("/profiles/x.md"));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_absolutePathWithAnAbsoluteDirectory_staysUnchanged() {
        final String absolute = new File("elsewhere/x.md").getAbsolutePath();

        assertThat(AiPathSupport.resolveAgainst(new File("prompts").getAbsoluteFile(), absolute), is(absolute));
    }

    @Test
    @AbsolutePathRequirement
    public void resolveAgainst_relativePathBelowAnAbsoluteDirectory_becomesAbsolute() {
        final File directory = new File("prompts").getAbsoluteFile();

        final String resolved = AiPathSupport.resolveAgainst(directory, "v1/x.md");

        assertThat(resolved, is(new File(directory, "v1/x.md").getPath()));
        assertThat(new File(String.valueOf(resolved)).isAbsolute(), is(true));
    }

    @Test
    @AbsolutePathRequirement
    @EnabledOnOs(OS.WINDOWS)
    public void resolveAgainst_windowsRootOrDriveRelativePath_staysUnchanged() {
        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), "M:x.md"), is("M:x.md"));
        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), "\\x.md"), is("\\x.md"));
    }

    @Test
    public void resolveAgainst_unparsablePath_staysUnchanged() {
        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), "bad\u0000.md"), is("bad\u0000.md"));
    }

    @Test
    public void resolveAgainst_withoutDirectory_staysRelativeToTheWorkingDirectory() {
        assertThat(AiPathSupport.resolveAgainst(null, "v1/x.md"), is("v1/x.md"));
    }

    @Test
    public void resolveAgainst_nullPath_staysNull() {
        assertThat(AiPathSupport.resolveAgainst(new File("prompts"), null), is(nullValue()));
    }
    // </editor-fold>
}

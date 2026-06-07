// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

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
}

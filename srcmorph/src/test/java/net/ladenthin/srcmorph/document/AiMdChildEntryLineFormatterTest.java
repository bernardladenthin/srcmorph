// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiMdChildEntryLineFormatterTest {

    private final AiMdChildEntryLineFormatter formatter = new AiMdChildEntryLineFormatter();

    // <editor-fold defaultstate="collapsed" desc="format">
    @Test
    public void format_headerGiven_returnsPipeSeparatedNewlineTerminatedLine() {
        // arrange
        AiMdHeader header = new AiMdHeader(
                "MyClass.java",
                "1.0",
                "A1B2C3D4",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "0.1.0",
                "1.0.0",
                "file");

        // act, assert
        // Pin the exact line so the empty-string return mutant is killed and field order is asserted.
        // the modification time is no part of it: a touched source does not change its package
        assertThat(formatter.format("MyClass.java", header), is("MyClass.java|A1B2C3D4|file\n"));
    }
}

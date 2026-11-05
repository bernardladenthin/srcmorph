// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class AiGeneratorVersionTest {

    private static InputStream stream(final String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1));
    }

    // <editor-fold defaultstate="collapsed" desc="version">
    /** The build filled the resource in: a version, not the placeholder. */
    @Test
    public void version_isTheBuildsVersion() {
        // act
        final String version = new AiGeneratorVersion().version();

        // assert
        assertThat(version.matches("\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?"), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="read">
    @Test
    public void read_theVersionKey_isTrimmed() {
        assertThat(AiGeneratorVersion.read(stream("# a comment\nversion = 1.5.0 \n")), is("1.5.0"));
    }

    @Test
    public void read_noStream_isUnknown() {
        assertThat(AiGeneratorVersion.read(null), is(""));
    }

    @Test
    public void read_noVersionKey_isUnknown() {
        assertThat(AiGeneratorVersion.read(stream("other=1\n")), is(""));
    }

    @Test
    public void read_anUnreadableStream_isUnknownAndClosed() {
        // arrange
        final boolean[] closed = {false};
        final InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("unreadable");
            }

            @Override
            public void close() {
                closed[0] = true;
            }
        };

        // act
        final String version = AiGeneratorVersion.read(failing);

        // assert
        assertThat(version, is(""));
        assertThat(closed[0], is(true));
    }
    // </editor-fold>
}

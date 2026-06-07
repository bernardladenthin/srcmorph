// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AiChecksumSupportTest {

    @TempDir
    public Path folder;

    private final AiChecksumSupport checksumSupport = new AiChecksumSupport();

    @Test
    public void crc32OfKnownStringMatchesReferenceValue() {
        // CRC32("hello") == 0x3610A686; pinning the exact value proves the bytes were fed to
        // the checksum (kills the "skip crc32.update(...)" void-call mutant, which would yield 00000000).
        assertThat(checksumSupport.calculateCrc32Hex("hello"), is("3610A686"));
    }

    @Test
    public void crc32IsEightCharUppercaseHex() {
        assertThat(checksumSupport.calculateCrc32Hex(""), is("00000000"));
    }

    @Test
    public void crc32OfFileMatchesStringForm() throws IOException {
        Path file = folder.resolve("data.txt");
        Files.write(file, "hello".getBytes(StandardCharsets.UTF_8));
        assertThat(checksumSupport.calculateCrc32Hex(file), is("3610A686"));
    }
}

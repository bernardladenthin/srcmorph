// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

/** Utility helpers for computing CRC32 checksums used in {@code .ai.md} headers. */
public class AiChecksumSupport {

    /** Creates a new {@link AiChecksumSupport}. */
    public AiChecksumSupport() {
        // no-op
    }

    /**
     * Computes the CRC32 checksum of the contents of {@code file} as an 8-character hexadecimal string.
     *
     * @param file file whose contents are checksummed
     * @return uppercase 8-character CRC32 hex string
     * @throws IOException if the file cannot be read
     */
    public String calculateCrc32Hex(final Path file) throws IOException {
        final byte[] bytes = Files.readAllBytes(file);
        return calculateCrc32Hex(bytes);
    }

    /**
     * Computes the CRC32 checksum of the UTF-8 encoding of {@code value} as an 8-character hexadecimal string.
     *
     * @param value string whose UTF-8 encoding is checksummed
     * @return uppercase 8-character CRC32 hex string
     */
    public String calculateCrc32Hex(final String value) {
        return calculateCrc32Hex(value.getBytes(StandardCharsets.UTF_8));
    }

    private String calculateCrc32Hex(final byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return String.format("%08X", crc32.getValue());
    }
}

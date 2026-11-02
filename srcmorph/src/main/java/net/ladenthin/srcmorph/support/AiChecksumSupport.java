// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import lombok.ToString;

/** Utility helpers for computing CRC32 checksums used in {@code .ai.md} headers. */
@ToString
public class AiChecksumSupport {

    /** Creates a new {@link AiChecksumSupport}. */
    public AiChecksumSupport() {
        // no-op
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

    /**
     * Calculates the CRC32 of bytes as eight upper-case hexadecimal digits.
     *
     * @param bytes the bytes
     * @return the checksum
     */
    public String calculateCrc32Hex(final byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return String.format("%08X", crc32.getValue());
    }
}

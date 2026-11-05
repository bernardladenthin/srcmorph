// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ChecksumSupportTest {

    private final ChecksumSupport checksumSupport = new ChecksumSupport();

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
}

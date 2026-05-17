// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

public class AiChecksumSupport {

    public String calculateCrc32Hex(final Path file) throws IOException {
        final byte[] bytes = Files.readAllBytes(file);
        return calculateCrc32Hex(bytes);
    }

    public String calculateCrc32Hex(final String value) {
        return calculateCrc32Hex(value.getBytes(StandardCharsets.UTF_8));
    }

    private String calculateCrc32Hex(final byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return String.format("%08X", crc32.getValue());
    }
}
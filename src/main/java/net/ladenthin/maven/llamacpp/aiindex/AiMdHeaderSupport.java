// @formatter:off

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AiMdHeaderSupport {

    /**
     * Separator character used between fields in a checksum line produced by
     * {@link #buildChecksumLine(String, AiMdHeader)}.
     */
    private static final char CHECKSUM_LINE_SEPARATOR = '|';

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    public boolean shouldWrite(
            final boolean force,
            final Path targetFile,
            final AiMdHeader expectedHeader
    ) throws IOException {
        if (force) {
            return true;
        }

        if (!Files.exists(targetFile)) {
            return true;
        }

        final AiMdDocument actualDocument = new AiMdDocumentCodec().read(targetFile);
        final AiMdHeader actualHeader = actualDocument.header();

        if (!AiMdHeaderCodec.HEADER_VERSION_1_0.equals(actualHeader.h())) {
            return true;
        }

        if (compatibilityHelper.isBlank(actualDocument.body())) {
            return true;
        }

        return !expectedHeader.h().equals(actualHeader.h())
                || !expectedHeader.x().equals(actualHeader.x())
                || !expectedHeader.title().equals(actualHeader.title())
                || !expectedHeader.c().equals(actualHeader.c())
                || !expectedHeader.d().equals(actualHeader.d())
                || !expectedHeader.g().equals(actualHeader.g())
                || !expectedHeader.a().equals(actualHeader.a());
    }

    public String buildChecksumLine(
            final String name,
            final AiMdHeader childHeader
    ) {
        return name
                + CHECKSUM_LINE_SEPARATOR
                + childHeader.c()
                + CHECKSUM_LINE_SEPARATOR
                + childHeader.d()
                + CHECKSUM_LINE_SEPARATOR
                + childHeader.x()
                + '\n';
    }
}
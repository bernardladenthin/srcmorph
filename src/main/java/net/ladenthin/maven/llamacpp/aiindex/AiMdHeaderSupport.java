// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Header comparison helpers that decide whether an {@code .ai.md} file needs to be rewritten. */
public class AiMdHeaderSupport {

    /** Creates a new {@link AiMdHeaderSupport}. */
    public AiMdHeaderSupport() {
        // no-op
    }

    /**
     * Separator character used between fields in a checksum line produced by
     * {@link #buildChecksumLine(String, AiMdHeader)}.
     */
    private static final char CHECKSUM_LINE_SEPARATOR = '|';

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Determines whether the target {@code .ai.md} file should be rewritten.
     *
     * @param force           when {@code true}, always rewrite
     * @param targetFile      path to the {@code .ai.md} file
     * @param expectedHeader  header that would be written
     * @return {@code true} when the file is missing, the body is blank, the header
     *         version differs, or any structural header field differs from
     *         {@code expectedHeader}
     * @throws IOException if the existing file cannot be read
     */
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

    /**
     * Builds a deterministic checksum line that captures a child entry's identity for
     * package-level aggregation.
     *
     * @param name        child name (file or package name)
     * @param childHeader child header to read {@code c}, {@code d}, and {@code x} from
     * @return checksum line terminated by a newline character
     */
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
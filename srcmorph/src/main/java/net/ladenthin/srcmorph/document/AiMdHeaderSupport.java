// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiFactExtractor;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;

/** Header comparison helpers that decide whether an {@code .ai.md} file needs to be rewritten. */
@ToString
public class AiMdHeaderSupport {

    /** Creates a new {@link AiMdHeaderSupport} that rewrites a summary of a changed prompt or fact file. */
    public AiMdHeaderSupport() {
        this(true);
    }

    /**
     * Creates a new {@link AiMdHeaderSupport}.
     *
     * @param regenerateOnProfileChange whether a differing prompt hash ({@code P}) or facts hash
     *                                  ({@code N}) rewrites the file; see
     *                                  {@code SrcMorphConfiguration#isRegenerateOnProfileChange()}
     */
    public AiMdHeaderSupport(final boolean regenerateOnProfileChange) {
        this.regenerateOnProfileChange = regenerateOnProfileChange;
    }

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    private final boolean regenerateOnProfileChange;

    /**
     * Determines whether the target {@code .ai.md} file should be rewritten.
     *
     * @param force           when {@code true}, always rewrite
     * @param targetFile      path to the {@code .ai.md} file
     * @param expectedHeader  header that would be written
     * @return {@code true} when the file is missing, the body is blank or holds the facts line alone (what
     *         1.3.0 wrote for an empty or cut-off answer), the header version differs, or
     *         the node type, title, content checksum or {@code aiVersion} differs from
     *         {@code expectedHeader}, or, when this support regenerates on a profile change, the prompt
     *         hash ({@code P}) or the facts hash ({@code N}) differs (a header without them, written before
     *         1.4.0, differs from one with them). The source's modification time and the plugin version are not
     *         compared: a fresh clone, a touched file or a new srcmorph release regenerate nothing whose
     *         content is the same.
     * @throws IOException if the existing file cannot be read
     */
    public boolean shouldWrite(final boolean force, final Path targetFile, final AiMdHeader expectedHeader)
            throws IOException {
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

        if (compatibilityHelper.isBlank(actualDocument.body()) || isFactsLineOnly(actualDocument.body())) {
            return true;
        }

        return !expectedHeader.h().equals(actualHeader.h())
                || !expectedHeader.x().equals(actualHeader.x())
                || !expectedHeader.title().equals(actualHeader.title())
                || !expectedHeader.c().equals(actualHeader.c())
                || !expectedHeader.a().equals(actualHeader.a())
                || (regenerateOnProfileChange
                        && (!expectedHeader.p().equals(actualHeader.p())
                                || !expectedHeader.n().equals(actualHeader.n())));
    }

    /**
     * Returns whether a body is srcmorph's facts line and nothing else: no summary. Since 1.4.0 an
     * incomplete answer is never written, but 1.3.0 wrote the facts line alone for an empty or cut-off one.
     *
     * @param body the body of the existing file
     * @return {@code true} if the body is one line that starts with {@link AiFactExtractor#FACTS_HEADER}
     */
    private static boolean isFactsLineOnly(final String body) {
        final String trimmed = body.trim();
        return trimmed.startsWith(AiFactExtractor.FACTS_HEADER) && !trimmed.contains("\n");
    }
}

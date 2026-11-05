// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import lombok.ToString;

/**
 * Formats one line of a package-level or project-level checksum manifest for a single child entry.
 *
 * <p>Each child of a {@code package.ai.md} (either a sibling source file or a
 * sub-package), and each package of the {@code project.ai.md}, contributes one line in the order
 * {@code name | c | x}, separated by a pipe and terminated by a newline. The aggregator concatenates
 * the lines in deterministic order and uses the concatenated string as the input to the parent's
 * CRC32 checksum, so a changed checksum below changes every one above.</p>
 *
 * <p>Kept separate from {@link AiMdRewritePolicy} because the use case is
 * narrow (only {@link net.ladenthin.srcmorph.indexer.PackageIndexer} and
 * {@link net.ladenthin.srcmorph.indexer.ProjectIndexer} call this) and unrelated to the
 * rewrite-decision logic that lives on {@code AiMdRewritePolicy}.</p>
 *
 * @see net.ladenthin.srcmorph.indexer.PackageIndexer
 * @see net.ladenthin.srcmorph.indexer.ProjectIndexer
 */
@ToString
public class AiMdChildEntryLineFormatter {

    /** Creates a new {@link AiMdChildEntryLineFormatter}. */
    public AiMdChildEntryLineFormatter() {
        // no-op
    }

    /**
     * Separator character used between fields in a checksum line produced by
     * {@link #format(String, AiMdHeader)}.
     */
    private static final char CHECKSUM_LINE_SEPARATOR = '|';

    /**
     * Builds a deterministic checksum line that captures a child entry's identity
     * for package-level aggregation.
     *
     * @param name        child name (file or package name)
     * @param childHeader child header to read {@code c} and {@code x} from
     * @return checksum line terminated by a newline character, in the form {@code <name>|<c>|<x>\n}; the
     *         child's modification time is left out, so a touched source does not change its package
     */
    public String format(final String name, final AiMdHeader childHeader) {
        return name
                + CHECKSUM_LINE_SEPARATOR
                + childHeader.checksum()
                + CHECKSUM_LINE_SEPARATOR
                + childHeader.nodeType()
                + '\n';
    }
}

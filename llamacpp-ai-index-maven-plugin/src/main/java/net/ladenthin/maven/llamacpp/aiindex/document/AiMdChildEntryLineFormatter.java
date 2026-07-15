// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.document;

import lombok.ToString;

/**
 * Formats one line of a package-level checksum manifest for a single child entry.
 *
 * <p>Each child of a {@code package.ai.md} (either a sibling source file or a
 * sub-package) contributes one line in the order {@code name | c | d | x},
 * separated by a pipe and terminated by a newline. The aggregator concatenates
 * the lines in deterministic order (ascending child name) and uses the
 * concatenated string as the input to the parent package's CRC32 checksum.</p>
 *
 * <p>Kept separate from {@link AiMdHeaderSupport} because the use case is
 * narrow (only {@link net.ladenthin.maven.llamacpp.aiindex.indexer.PackageIndexer} calls this) and unrelated to the
 * rewrite-decision logic that lives on {@code AiMdHeaderSupport}.</p>
 *
 * @see net.ladenthin.maven.llamacpp.aiindex.indexer.PackageIndexer
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
     * @param childHeader child header to read {@code c}, {@code d}, and {@code x} from
     * @return checksum line terminated by a newline character, in the form
     *         {@code <name>|<c>|<d>|<x>\n}
     */
    public String format(final String name, final AiMdHeader childHeader) {
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

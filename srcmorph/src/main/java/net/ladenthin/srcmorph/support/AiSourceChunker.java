// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.support;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Splits a source string into chunks for map-reduce summarization of very large files.
 *
 * <p>Each chunk is at most {@code maxChars} long and ends at a <strong>line boundary</strong> (the last
 * {@code \n} within the window) so records/lines are never torn in the middle; if a single line is longer
 * than {@code maxChars} the chunk is hard-cut. Consecutive chunks may {@code overlap} by a number of
 * characters so context is not lost at the seam. When the file would produce more than {@code maxChunks}
 * chunks (and {@code maxChunks > 0}), a representative subset is selected — always including the first and
 * last chunk and evenly spacing the rest — so the summary samples the <em>whole</em> file, not just its
 * head. Pure; no I/O.</p>
 */
public final class AiSourceChunker {

    /** Utility class; not instantiable. */
    private AiSourceChunker() {
        // no-op
    }

    /**
     * Chunks {@code source} per the class contract.
     *
     * @param source       the full source text
     * @param maxChars     maximum characters per chunk (must be {@code >= 1})
     * @param overlapChars characters of overlap between consecutive chunks (clamped to {@code [0, maxChars - 1]})
     * @param maxChunks    maximum number of chunks to return; {@code <= 0} means unbounded (return all)
     * @return the chunks in document order (a representative subset when capped); empty if {@code source} is empty
     * @throws IllegalArgumentException if {@code maxChars < 1}
     */
    public static List<String> chunk(
            final String source, final int maxChars, final int overlapChars, final int maxChunks) {
        if (maxChars < 1) {
            throw new IllegalArgumentException("maxChars must be >= 1 but was " + maxChars);
        }
        final int length = source.length();
        final List<String> chunks = new ArrayList<>(length / maxChars + 1);
        if (length == 0) {
            return chunks;
        }
        final int overlap = Math.max(0, Math.min(overlapChars, maxChars - 1));

        int pos = 0;
        while (pos < length) {
            int end = Math.min(pos + maxChars, length);
            if (end < length) {
                final int lastNewline = source.lastIndexOf('\n', end - 1);
                if (lastNewline > pos) {
                    end = lastNewline + 1;
                }
            }
            chunks.add(source.substring(pos, end));
            if (end >= length) {
                break;
            }
            pos = Math.max(pos + 1, end - overlap);
        }
        return select(chunks, maxChunks);
    }

    /**
     * Returns all chunks, or a representative subset of exactly the distinct indices spanning first..last
     * when there are more than {@code maxChunks} and {@code maxChunks > 0}.
     *
     * @param chunks    the natural chunk list
     * @param maxChunks the cap ({@code <= 0} = unbounded)
     * @return the (possibly reduced) chunk list
     */
    private static List<String> select(final List<String> chunks, final int maxChunks) {
        final int total = chunks.size();
        if (maxChunks <= 0 || total <= maxChunks) {
            return chunks;
        }
        final Set<Integer> indices = new LinkedHashSet<>();
        if (maxChunks == 1) {
            indices.add(0);
        } else {
            for (int i = 0; i < maxChunks; i++) {
                indices.add((int) Math.round((double) i * (total - 1) / (maxChunks - 1)));
            }
        }
        final List<String> selected = new ArrayList<>(indices.size());
        for (final int index : indices) {
            selected.add(chunks.get(index));
        }
        return selected;
    }
}

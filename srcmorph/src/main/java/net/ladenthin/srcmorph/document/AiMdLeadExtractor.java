// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import lombok.ToString;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;

/**
 * Extracts the one-line lead from an {@code .ai.md} document body.
 *
 * <p>Every file and package summary is generated to begin with a one-sentence
 * <em>blockquote lead</em> (a line starting with {@value #BLOCKQUOTE_MARKER}) that captures the
 * essence of the node. The project-level index harvests exactly this line per package, so it can
 * present one navigable sentence per package without re-running the model.</p>
 *
 * <p>Extraction is deterministic and tolerant: leading blank lines are skipped and the first
 * non-blank line is the lead. If that line is a blockquote, the {@value #BLOCKQUOTE_MARKER} marker
 * is stripped and the remainder trimmed; otherwise the trimmed line is returned verbatim (a fallback
 * for a body whose model omitted the blockquote). A blank body yields an empty lead. Only the first
 * non-blank line is considered — a blockquote further down is never promoted to the lead.</p>
 */
@ToString
public class AiMdLeadExtractor {

    /** Creates a new {@link AiMdLeadExtractor}. */
    public AiMdLeadExtractor() {
        // no-op
    }

    /**
     * Markdown blockquote marker that prefixes a lead line. When the first non-blank body line
     * starts with this marker, the marker is stripped and the remainder is the lead.
     */
    public static final String BLOCKQUOTE_MARKER = ">";

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Returns the one-line lead of {@code body}: the first non-blank line, with a leading
     * {@value #BLOCKQUOTE_MARKER} blockquote marker stripped when present.
     *
     * @param body the markdown body of an {@code .ai.md} document; must not be {@code null}
     * @return the trimmed lead line, or the empty string when {@code body} has no non-blank line
     */
    public String extractLead(final String body) {
        for (final String rawLine : body.split("\n", -1)) {
            if (compatibilityHelper.isBlank(rawLine)) {
                continue;
            }
            final String line = rawLine.trim();
            if (line.startsWith(BLOCKQUOTE_MARKER)) {
                return line.substring(BLOCKQUOTE_MARKER.length()).trim();
            }
            return line;
        }
        return "";
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import lombok.ToString;

/**
 * The answers of one map-reduce summary, in the order they came: each chunk's map answer under its label,
 * and each reduce batch's answer under its round and batch. Kept on request
 * ({@link net.ladenthin.srcmorph.config.SrcMorphConfiguration#getPartsDirectory()}) to see where a summary
 * loses what a part said.
 */
@ToString
final class SummaryParts {

    /** The end of a chunk label line ({@link MapReduceMath#chunkLabel}), left out of the heading. */
    private static final String LABEL_END = ":\n";

    private final StringBuilder text = new StringBuilder();

    /**
     * Adds a chunk's map answer.
     *
     * @param label  the chunk's label line, e.g. {@code Chunk 2 of 7:\n}
     * @param answer the answer
     */
    void map(final String label, final String answer) {
        final String heading =
                label.endsWith(LABEL_END) ? label.substring(0, label.length() - LABEL_END.length()) : label;
        add(heading, answer);
    }

    /**
     * Adds a reduce batch's answer.
     *
     * @param round   the reduce round, from 1
     * @param batch   the batch of the round, from 1
     * @param batches the batches of the round
     * @param parts   the parts the batch combined
     * @param answer  the answer
     */
    void reduce(final int round, final int batch, final int batches, final int parts, final String answer) {
        add("Reduce round " + round + ", batch " + batch + " of " + batches + " (" + parts + " parts)", answer);
    }

    private void add(final String heading, final String answer) {
        text.append("## ").append(heading).append("\n\n").append(answer).append("\n\n");
    }

    /**
     * Returns the answers as Markdown, one {@code ##} section each.
     *
     * @return the text, {@code ""} when no answer was added
     */
    String text() {
        return text.toString();
    }
}

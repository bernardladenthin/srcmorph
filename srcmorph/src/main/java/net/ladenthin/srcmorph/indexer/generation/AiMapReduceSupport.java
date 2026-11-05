// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import java.util.ArrayList;
import java.util.List;

/**
 * The arithmetic and the text of a map-reduce summary, without a model: how large a chunk is, how much
 * consecutive chunks overlap, and how the partial summaries are packed into the batches of one reduce
 * round. {@link SummaryGenerator} runs the generations.
 */
public final class AiMapReduceSupport {

    /** Upper bound on the character overlap between map-reduce chunks. */
    public static final int MAX_OVERLAP_CHARS = 2000;

    /** Fraction (1/n) of the chunk size used as overlap when smaller than {@link #MAX_OVERLAP_CHARS}. */
    public static final int OVERLAP_DIVISOR = 20;

    /** Minimum per-chunk source budget, so a tiny window never yields zero-length chunks. */
    public static final int MIN_CHUNK_CHARS = 1000;

    /**
     * Safety headroom (percent) subtracted from the window before sizing chunks and reduce batches.
     * {@code charsPerToken} is only an <em>estimate</em>; token-dense content (e.g. SQL with many
     * digits and punctuation, about 2.8 chars per token) tokenizes to more tokens than a budget computed
     * from a higher {@code charsPerToken} assumes, which would otherwise let a chunk overflow the model's
     * context at run time. The margin keeps chunks inside the window while the real density is up to this
     * percent below the configured one.
     */
    public static final int TOKEN_HEADROOM_PERCENT = 20;

    /**
     * Maximum number of partial summaries combined into a single reduce call. Bounds the reduce prompt
     * and makes every round shrink the list (a fan-in of at least 2), so the reduce terminates.
     */
    public static final int MAX_REDUCE_FANIN = 16;

    /** The fewest partials a reduce batch combines: every round shrinks the list, so the reduce ends. */
    public static final int MIN_REDUCE_FANIN = 2;

    /** Label prefix of one partial summary inside a reduce prompt. */
    public static final String PART_LABEL = "Part ";

    /** The end of a partial's label line. */
    private static final String PART_LABEL_END = ":\n";

    /** The blank line after a partial. */
    private static final String PART_SEPARATOR = "\n\n";

    /**
     * The characters reserved for a chunk's label ({@link #chunkLabel(int, int, int)}) in the budget of a
     * map call: {@code Chunk 99999 of 99999, sampled from 99999 chunks:} and its line end, with room.
     */
    public static final int CHUNK_LABEL_RESERVE = 64;

    /** The line that ends a partial {@link #shortened shortened} to fit a reduce batch. */
    public static final String SHORTENED_MARKER = "\n...";

    /** The separator between two counters of a facts line. */
    private static final String FACTS_SEPARATOR = "; ";

    /** The end of a facts line {@link #reduceFacts cut} for a reduce call. */
    private static final String FACTS_ELLIPSIS = "; ...\n\n";

    /** Approximate per-partial label and separator overhead when packing a reduce batch. */
    public static final int LABEL_OVERHEAD_CHARS = 16;

    /** Percent divisor. */
    private static final int PERCENT = 100;

    /** The start of the coverage line in front of the summary of an over-window source. */
    public static final String COVERAGE_HEADER = "**Coverage:** ";

    /** The end of the coverage line: a blank line before what follows. */
    public static final String COVERAGE_SUFFIX = "\n\n";

    /** Utility class; not instantiable. */
    private AiMapReduceSupport() {
        // no-op
    }

    /**
     * Returns the window of every map and reduce call: the model's window less the
     * {@link #TOKEN_HEADROOM_PERCENT token headroom}.
     *
     * @param maxInputChars the model's per-call window in characters
     * @return the window a chunk or a batch is sized against
     */
    public static int safeWindow(final int maxInputChars) {
        // in long, so a window above Integer.MAX_VALUE / 20 does not overflow
        return (int) (maxInputChars - (long) maxInputChars * TOKEN_HEADROOM_PERCENT / PERCENT);
    }

    /**
     * Returns the source budget of one chunk and one reduce batch: the safe window less the prompt
     * template, at least {@link #MIN_CHUNK_CHARS}.
     *
     * @param safeWindow       the {@link #safeWindow(int) safe window}
     * @param basePromptLength the length of the prompt template with an empty source
     * @return the budget in characters
     */
    public static int sourceBudget(final int safeWindow, final int basePromptLength) {
        return Math.max(MIN_CHUNK_CHARS, safeWindow - basePromptLength);
    }

    /**
     * Returns the overlap between consecutive chunks: a twentieth of the budget, at most
     * {@link #MAX_OVERLAP_CHARS}.
     *
     * @param sourceBudget the {@link #sourceBudget(int, int) chunk budget}
     * @return the overlap in characters
     */
    public static int overlap(final int sourceBudget) {
        return Math.min(MAX_OVERLAP_CHARS, sourceBudget / OVERLAP_DIVISOR);
    }

    /**
     * Packs one reduce round: consecutive partials go into one batch while it holds fewer than
     * {@link #MAX_REDUCE_FANIN} and the next one still fits {@code sourceBudget}; a batch always takes
     * at least one partial, however long. Every partial is labelled {@code Part n:} with its number in
     * the batch.
     *
     * @param partials     the partial summaries of the round
     * @param sourceBudget the characters one batch may hold
     * @return the batch texts, in order
     */
    public static List<String> reduceBatches(final List<String> partials, final int sourceBudget) {
        final List<List<String>> groups = reduceGroups(partials, sourceBudget);
        final List<String> batches = new ArrayList<>(groups.size());
        for (final List<String> group : groups) {
            batches.add(combineAll(group));
        }
        return batches;
    }

    /**
     * Groups one reduce round's partials as {@link #reduceBatches(List, int)} packs them, without the
     * labels: the caller sees which batch holds a single partial.
     *
     * @param partials     the partial summaries of the round
     * @param sourceBudget the characters one batch may hold
     * @return the groups, in order, each with at least one partial
     */
    public static List<List<String>> reduceGroups(final List<String> partials, final int sourceBudget) {
        final List<List<String>> groups = new ArrayList<>(partials.size());
        int i = 0;
        while (i < partials.size()) {
            final List<String> group = new ArrayList<>(MAX_REDUCE_FANIN);
            // the length the batch text has so far: every part with its label and separators
            int length = 0;
            while (group.size() < MAX_REDUCE_FANIN
                    && i < partials.size()
                    && (group.isEmpty() || length + partials.get(i).length() + LABEL_OVERHEAD_CHARS <= sourceBudget)) {
                length += partLength(group.size() + 1, partials.get(i));
                group.add(partials.get(i));
                i++;
            }
            groups.add(group);
        }
        return groups;
    }

    /**
     * Returns the longest a partial may be so that any two of them fit one batch of {@code sourceBudget}:
     * the budget less two parts' label overhead, halved.
     *
     * @param sourceBudget the characters one batch may hold
     * @return the length, at least {@code 1}
     */
    public static int pairablePartChars(final int sourceBudget) {
        return Math.max(1, (sourceBudget - 2 * LABEL_OVERHEAD_CHARS) / 2);
    }

    /**
     * Returns {@code partial} shortened to {@code maxChars} at its last line end before them, with
     * {@link #SHORTENED_MARKER}, or the partial as it is when it fits.
     *
     * @param partial  the partial summary
     * @param maxChars the characters it may take, the marker included
     * @return the partial or its head
     */
    public static String shortened(final String partial, final int maxChars) {
        if (partial.length() <= maxChars) {
            return partial;
        }
        final int room = Math.max(0, maxChars - SHORTENED_MARKER.length());
        final int lineEnd = partial.lastIndexOf('\n', room);
        return partial.substring(0, lineEnd > 0 ? lineEnd : room) + SHORTENED_MARKER;
    }

    /**
     * Returns a part summary cut off at the output budget as the reduce reads it: up to its last line end,
     * which drops the entry the cut ended in the middle of, then {@link #SHORTENED_MARKER}, so the reduce
     * sees that the part stops early; a part of one line stays whole, with the marker.
     *
     * @param partial the text the model wrote before the cut
     * @return the part for the reduce
     */
    public static String cutPart(final String partial) {
        final int lineEnd = partial.lastIndexOf('\n');
        return partial.substring(0, lineEnd > 0 ? lineEnd : partial.length()) + SHORTENED_MARKER;
    }

    /**
     * Returns the facts line a reduce call reads in front of its parts: the whole line when it takes at
     * most {@code maxChars}, otherwise its counters up to the last {@code "; "} that fits, then
     * {@code "; ..."}, so a long line never leaves the batch without room for the parts.
     *
     * @param facts    the facts line with its blank line, or {@code ""}
     * @param maxChars the characters it may take
     * @return the line for the reduce
     */
    public static String reduceFacts(final String facts, final int maxChars) {
        if (facts.length() <= maxChars) {
            return facts;
        }
        final int cut = facts.lastIndexOf(FACTS_SEPARATOR, Math.max(0, maxChars - FACTS_ELLIPSIS.length()));
        // no counter that fits: the ellipsis alone
        return facts.substring(0, Math.max(0, cut)) + FACTS_ELLIPSIS;
    }

    /**
     * Returns where to split a chunk in two at a line end near its middle: after the line break nearest
     * the middle on either side (the one before it on a tie) that leaves each half at least a quarter of
     * the chunk, or the middle itself when there is none, so no half is left almost empty.
     *
     * @param chunk the chunk, at least two characters
     * @return the length of the first half, between {@code 1} and {@code chunk.length() - 1}
     */
    public static int halfSplit(final String chunk) {
        final int length = chunk.length();
        final int middle = length / 2;
        final int least = Math.max(1, length / 4);
        // the split after the nearest line end before the middle and at or after it; 0 when there is none
        final int before = chunk.lastIndexOf('\n', middle - 1) + 1;
        final int after = chunk.indexOf('\n', middle) + 1;
        final boolean beforeFits = before >= least;
        final boolean afterFits = after > 0 && after <= length - least;
        if (beforeFits && (!afterFits || middle - before <= after - middle)) {
            return before;
        }
        return afterFits ? after : middle;
    }

    /**
     * Returns the line in front of a chunk in its map call, so the model sees which part of the source it
     * reads: {@code Chunk 3 of 44:}, or for a sample {@code Chunk 3 of 6, sampled from 44 chunks:}.
     *
     * @param number the chunk's number among the chunks read, from 1
     * @param read   the chunks read
     * @param total  the chunks of the source
     * @return the label and its line end
     */
    public static String chunkLabel(final int number, final int read, final int total) {
        return "Chunk " + number + " of " + read + (read < total ? ", sampled from " + total + " chunks" : "") + ":\n";
    }

    /**
     * Returns the note in front of a reduce's parts when they summarize a sample of the chunks, so the merge
     * states the sample's counts as the sample's: {@code **Sample:** the parts summarize 6 of 44 chunks; the
     * rest of the input was not read.}
     *
     * @param read  the chunks read
     * @param total the chunks of the source
     * @return the note and its blank line, or {@code ""} when every chunk was read
     */
    public static String sampleNote(final int read, final int total) {
        if (read >= total) {
            return "";
        }
        return "**Sample:** the parts summarize " + read + " of " + total
                + " chunks; the rest of the input was not read." + COVERAGE_SUFFIX;
    }

    /**
     * Returns the label of one half of a chunk read in two: {@code Chunk 3 of 44, half 1:}.
     *
     * @param label the chunk's label ({@link #chunkLabel(int, int, int)}, or a half's)
     * @param half  {@code 1} or {@code 2}
     * @return the half's label and its line end
     */
    public static String halfLabel(final String label, final int half) {
        final String line =
                label.endsWith(PART_LABEL_END) ? label.substring(0, label.length() - PART_LABEL_END.length()) : label;
        return line + ", half " + half + PART_LABEL_END;
    }

    /**
     * Returns every partial in one labelled text: the last resort when the partials are too long to
     * combine even two at a time, so the reduce ends with one (trimmed) call instead of not shrinking.
     *
     * @param partials the partial summaries
     * @return the labelled text of all of them
     */
    public static String combineAll(final Iterable<String> partials) {
        final StringBuilder all = new StringBuilder();
        int number = 0;
        for (final String partial : partials) {
            appendPart(all, ++number, partial);
        }
        return all.toString();
    }

    /**
     * Returns the coverage line of a map-reduce: how many of the source's chunks the summary read, how
     * many of its calls a context overflow made cut to a smaller window, how many part summaries the
     * output budget cut off (kept, the reduce read them), and how many the reduce shortened because no two
     * of them fitted one batch.
     *
     * @param summarized the chunks summarized
     * @param total      the chunks of the source
     * @param cutCalls   the map and reduce calls whose input was cut to fit the window
     * @param cutParts   the part summaries cut off at the output budget
     * @param shortened  the part summaries shortened to fit a reduce batch
     * @return {@code **Coverage:** 6 of 9 chunks summarized (sampled; 2 calls cut to fit the window; 1 part
     *         summary cut at the output budget; 3 part summaries shortened to fit the reduce)}, each note only
     *         when it applies
     */
    public static String chunkCoverage(
            final int summarized, final int total, final int cutCalls, final int cutParts, final int shortened) {
        final List<String> notes = new ArrayList<>(4);
        if (summarized < total) {
            notes.add("sampled");
        }
        if (cutCalls > 0) {
            notes.add(cutCalls + (cutCalls == 1 ? " call" : " calls") + " cut to fit the window");
        }
        if (cutParts > 0) {
            notes.add(cutParts + (cutParts == 1 ? " part summary" : " part summaries") + " cut at the output budget");
        }
        if (shortened > 0) {
            notes.add(shortened + (shortened == 1 ? " part summary" : " part summaries")
                    + " shortened to fit the reduce");
        }
        return COVERAGE_HEADER + summarized + " of " + total + " chunks summarized"
                + (notes.isEmpty() ? "" : " (" + String.join("; ", notes) + ")") + COVERAGE_SUFFIX;
    }

    /**
     * Returns the coverage line of a sampled head: how much of the source the one call read.
     *
     * @param read  the characters read
     * @param total the characters of the source
     * @return {@code **Coverage:** the first 12000 of 90000 characters (sampled head)}
     */
    public static String headCoverage(final int read, final int total) {
        return COVERAGE_HEADER + "the first " + read + " of " + total + " characters (sampled head)" + COVERAGE_SUFFIX;
    }

    /**
     * Whether a chunk can be read again as two halves, each at least {@link #MIN_CHUNK_CHARS} long: one
     * that came back without an answer, or that does not fit the counted window. A shorter one is cut
     * to the window instead.
     *
     * @param chunkLength the chunk's characters
     * @return {@code true} from twice {@link #MIN_CHUNK_CHARS} on
     */
    public static boolean splittable(final int chunkLength) {
        return chunkLength >= 2 * MIN_CHUNK_CHARS;
    }

    /**
     * Returns how many reduce calls a map-reduce of {@code chunks} partials makes, when every batch holds
     * {@link #MAX_REDUCE_FANIN} partials (fewer when they are long): one per batch of every round, until
     * one summary is left; none for a single chunk.
     *
     * @param chunks the partials of the first round
     * @return the reduce calls
     */
    public static long reduceCalls(final long chunks) {
        return reduceCalls(chunks, MAX_REDUCE_FANIN);
    }

    /**
     * Returns how many reduce calls a map-reduce of {@code chunks} partials makes when every batch holds
     * {@code fanIn} of them (2 to {@link #MAX_REDUCE_FANIN}): one per batch of every round, until one
     * summary is left; none for a single chunk.
     *
     * @param chunks the partials of the first round
     * @param fanIn  the partials of a batch, held to 2 to {@link #MAX_REDUCE_FANIN}
     * @return the reduce calls
     */
    public static long reduceCalls(final long chunks, final int fanIn) {
        final int perBatch = Math.max(MIN_REDUCE_FANIN, Math.min(MAX_REDUCE_FANIN, fanIn));
        long calls = 0;
        for (long level = chunks; level > 1; level = (level + perBatch - 1) / perBatch) {
            calls += (level + perBatch - 1) / perBatch;
        }
        return calls;
    }

    /**
     * Returns how many partials a reduce batch is expected to hold before they are written: as many part
     * summaries of {@code expectedOutputTokens} as fit the source budget (the run packs them so), 2 to
     * {@link #MAX_REDUCE_FANIN}.
     *
     * @param sourceBudget         the characters a call's source may take
     * @param charsPerToken        the model's characters per token, above 0
     * @param expectedOutputTokens the tokens a part summary is expected to take
     * @return the expected fan-in
     */
    public static int expectedFanIn(
            final long sourceBudget, final double charsPerToken, final int expectedOutputTokens) {
        final double partChars = expectedOutputTokens * charsPerToken + CHUNK_LABEL_RESERVE;
        final long fits = (long) (sourceBudget / partChars);
        return (int) Math.max(MIN_REDUCE_FANIN, Math.min(MAX_REDUCE_FANIN, fits));
    }

    /**
     * Returns how many chunks {@link net.ladenthin.srcmorph.indexer.generation.AiSourceChunker} cuts a source of
     * {@code sourceChars} into with the given budget and overlap, ignoring the line-boundary rounding
     * (which can add a chunk): a chunk advances {@code budget - overlap} characters.
     *
     * @param sourceChars  the source length
     * @param sourceBudget the characters of one chunk
     * @param overlap      the overlap of consecutive chunks
     * @return the chunks, at least one
     */
    public static long chunkCount(final long sourceChars, final int sourceBudget, final int overlap) {
        // the first chunk takes the budget, every further one advances by a step
        final long step = Math.max(1, sourceBudget - overlap);
        return 1 + (Math.max(0, sourceChars - sourceBudget) + step - 1) / step;
    }

    private static void appendPart(final StringBuilder target, final int number, final String partial) {
        target.append(PART_LABEL)
                .append(number)
                .append(PART_LABEL_END)
                .append(partial)
                .append(PART_SEPARATOR);
    }

    private static int partLength(final int number, final String partial) {
        return PART_LABEL.length()
                + Integer.toString(number).length()
                + PART_LABEL_END.length()
                + partial.length()
                + PART_SEPARATOR.length();
    }
}

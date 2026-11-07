// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

/**
 * A text that lets a regex matcher read a fixed number of characters and then stops it: the time limit
 * of a fact counter's pattern and of a {@code contentMatches} condition, counted in characters rather
 * than seconds so it is the same on every machine.
 *
 * <p>The limit of a text is {@value #READS_PER_CHAR} characters per character plus
 * {@value #READ_ALLOWANCE} ({@link #limit(int)}); a pattern that backtracks past it (a nested quantifier
 * over a long line, say) makes {@link #charAt(int)} throw {@link Exhausted}.</p>
 */
public final class ReadBudget implements CharSequence {

    /** Characters a pattern may read per character of the text before it is stopped. */
    public static final long READS_PER_CHAR = 2_000L;

    /** Characters a pattern may read on top of {@value #READS_PER_CHAR} per character of the text. */
    public static final long READ_ALLOWANCE = 1_000_000L;

    private final CharSequence text;

    private final long budget;

    private long reads;

    /**
     * Creates a budget over a text.
     *
     * @param text   the text
     * @param budget the characters the matcher may read
     */
    ReadBudget(final CharSequence text, final long budget) {
        this.text = text;
        this.budget = budget;
    }

    /**
     * Creates a budget over a text with the {@link #limit(int) limit} of its length.
     *
     * @param text the text
     * @return the budget
     */
    public static ReadBudget over(final CharSequence text) {
        return new ReadBudget(text, limit(text.length()));
    }

    /**
     * Returns how many characters a pattern may read of a text of the given length.
     *
     * @param length the text's length
     * @return {@value #READS_PER_CHAR} per character plus {@value #READ_ALLOWANCE}
     */
    public static long limit(final int length) {
        return READS_PER_CHAR * length + READ_ALLOWANCE;
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(final int index) {
        reads++;
        if (reads > budget) {
            throw new Exhausted();
        }
        return text.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return text.subSequence(start, end);
    }

    @Override
    public String toString() {
        return text.toString();
    }

    /** Thrown by {@link #charAt(int)} once the budget is read. */
    public static final class Exhausted extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /** Creates the exception without a message, a cause or a stack trace. */
        Exhausted() {
            super(null, null, false, false);
        }
    }
}

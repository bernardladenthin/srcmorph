// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Counts the {@link IndexOutcome}s of one pass over many files or packages. */
@ToString
@EqualsAndHashCode
public final class IndexTally {

    private int written;

    private int unchanged;

    private int failed;

    /** Creates an empty tally. */
    public IndexTally() {
        // no-op
    }

    /**
     * Counts one outcome.
     *
     * @param outcome the outcome
     */
    public void add(final IndexOutcome outcome) {
        if (outcome == IndexOutcome.WRITTEN) {
            written++;
        } else if (outcome == IndexOutcome.UNCHANGED) {
            unchanged++;
        } else {
            failed++;
        }
    }

    /**
     * Returns how many were written or refreshed.
     *
     * @return the count
     */
    public int written() {
        return written;
    }

    /**
     * Returns how many were up to date.
     *
     * @return the count
     */
    public int unchanged() {
        return unchanged;
    }

    /**
     * Returns how many got an incomplete answer and were not written.
     *
     * @return the count
     */
    public int failed() {
        return failed;
    }
}

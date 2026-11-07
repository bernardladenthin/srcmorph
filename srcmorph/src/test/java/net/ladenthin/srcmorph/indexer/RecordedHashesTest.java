// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class RecordedHashesTest {

    // <editor-fold defaultstate="collapsed" desc="accessors">
    @Test
    public void accessors_twoHashes_returnEachHash() {
        // arrange
        final RecordedHashes hashes = new RecordedHashes("1a2b3c4d", "5e6f7a8b");

        // act
        final String promptHash = hashes.promptHash();
        final String factsHash = hashes.factsHash();

        // assert
        assertThat(promptHash, is("1a2b3c4d"));
        assertThat(factsHash, is("5e6f7a8b"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="equals">
    @Test
    public void equals_sameHashes_isEqual() {
        // arrange
        final RecordedHashes first = new RecordedHashes("1a2b3c4d", "");

        // act
        final RecordedHashes second = new RecordedHashes("1a2b3c4d", "");

        // assert
        assertThat(first, is(second));
    }

    @Test
    public void equals_otherFactsHash_isNotEqual() {
        // arrange
        final RecordedHashes first = new RecordedHashes("1a2b3c4d", "");

        // act
        final RecordedHashes second = new RecordedHashes("1a2b3c4d", "5e6f7a8b");

        // assert
        assertThat(first, is(not(second)));
    }
    // </editor-fold>
}

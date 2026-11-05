// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.support.ConvertToRecord;

/**
 * The hashes of the prompt and the fact counters a summary was written with, as its header records them
 * ({@code ""} each when it does not).
 */
@ConvertToRecord
@ToString
@EqualsAndHashCode
public final class RecordedHashes {

    private final String promptHash;
    private final String factsHash;

    /**
     * Creates the pair.
     *
     * @param promptHash the CRC32 of the prompt text, or {@code ""}
     * @param factsHash  the CRC32 of the fact counters, or {@code ""}
     */
    public RecordedHashes(final String promptHash, final String factsHash) {
        this.promptHash = promptHash;
        this.factsHash = factsHash;
    }

    /**
     * Returns the CRC32 of the prompt text.
     *
     * @return the prompt hash, or {@code ""}
     */
    public String promptHash() {
        return promptHash;
    }

    /**
     * Returns the CRC32 of the fact counters.
     *
     * @return the facts hash, or {@code ""}
     */
    public String factsHash() {
        return factsHash;
    }
}

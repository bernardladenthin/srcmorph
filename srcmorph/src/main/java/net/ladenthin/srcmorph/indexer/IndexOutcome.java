// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

/** What indexing one file, package or project did. */
public enum IndexOutcome {

    /** The {@code .ai.md} was written or refreshed. */
    WRITTEN,

    /** The {@code .ai.md} was up to date and left as it is. */
    UNCHANGED,

    /**
     * The model's answer was incomplete, so nothing was written: an older {@code .ai.md} stays, and the
     * next run tries again.
     */
    FAILED
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import net.ladenthin.srcmorph.config.IncompleteStrategy;
import net.ladenthin.srcmorph.provider.IncompleteAnswerException;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;

/** What a call does with an incomplete answer (empty, cut off, aborted or missing). */
enum IncompleteAnswerAction {

    /**
     * A part summary cut off at the output budget with text: kept at once, without asking again, since the
     * reduce reads it as it is. A model that lists a long run of entries one by one would otherwise fail
     * the whole file.
     */
    KEEP_PART,

    /** A final answer cut off at the output budget with text, under {@code onIncomplete: keep}: kept. */
    KEEP,

    /** Asked for once more: the rule's {@code onIncomplete} leaves an attempt. */
    ASK_AGAIN,

    /** No attempt left: the answer stays incomplete. */
    FAIL;

    private static final Java8CompatibilityHelper COMPATIBILITY = new Java8CompatibilityHelper();

    /**
     * Decides what to do with an incomplete answer.
     *
     * @param answer   the incomplete answer
     * @param part     whether the answer is a part summary the reduce reads, not the final answer
     * @param strategy the rule's {@code onIncomplete}
     * @param attempt  the attempt that answered, from 1
     * @return the action
     */
    static IncompleteAnswerAction of(
            final IncompleteAnswerException answer,
            final boolean part,
            final IncompleteStrategy strategy,
            final int attempt) {
        final boolean cutWithText = answer.reason() == IncompleteAnswerException.Reason.TRUNCATED
                && !COMPATIBILITY.isBlank(answer.partialText());
        if (cutWithText && part) {
            return KEEP_PART;
        }
        if (cutWithText && strategy == IncompleteStrategy.KEEP) {
            return KEEP;
        }
        return attempt >= strategy.attempts() ? FAIL : ASK_AGAIN;
    }
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ReadBudgetTest {

    // <editor-fold defaultstate="collapsed" desc="limit">
    @Test
    public void limit_textLength_isTwoThousandCharactersPerCharacterPlusAMillion() {
        // act, assert
        assertThat(ReadBudget.limit(0), is(1_000_000L));
        assertThat(ReadBudget.limit(10), is(1_020_000L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="charAt">
    @Test
    public void charAt_budgetOfThree_readsItsBudgetAndStopsTheReadAfter() {
        // arrange
        final ReadBudget text = new ReadBudget("abcd", 3);

        // act, assert
        assertThat(text.charAt(0), is('a'));
        assertThat(text.charAt(1), is('b'));
        assertThat(text.charAt(3), is('d'));
        assertThrows(ReadBudget.Exhausted.class, () -> text.charAt(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="length, subSequence, toString">
    @Test
    public void lengthSubSequenceToString_zeroBudget_areThoseOfTheWrappedText() {
        // arrange
        final ReadBudget text = new ReadBudget("abcd", 0);

        // act, assert
        assertThat(text.length(), is(4));
        assertThat(text.subSequence(1, 3).toString(), is("bc"));
        assertThat(text.toString(), is("abcd"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="over">
    @Test
    public void over_aText_hasTheLimitOfItsLength() {
        // arrange
        final ReadBudget text = ReadBudget.over("ab");

        // act
        for (long i = 0; i < ReadBudget.limit(2); i++) {
            text.charAt(0);
        }

        // assert
        assertThrows(ReadBudget.Exhausted.class, () -> text.charAt(0));
    }
    // </editor-fold>
}

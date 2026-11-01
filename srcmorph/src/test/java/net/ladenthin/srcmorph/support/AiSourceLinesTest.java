// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiSourceLinesTest {

    // <editor-fold defaultstate="collapsed" desc="normalize">
    @Test
    public void normalize_everyLineEnding_becomesALineFeed() {
        assertThat(AiSourceLines.normalize("a\r\nb\rc\nd"), is(equalTo("a\nb\nc\nd")));
    }

    @Test
    public void normalize_crCrLf_isTwoLineEndings() {
        // a lone \r, then a \r\n: two line endings, not one and not three
        assertThat(AiSourceLines.normalize("a\r\r\nb"), is(equalTo("a\n\nb")));
    }

    @Test
    public void normalize_withoutACarriageReturn_returnsTheSameText() {
        final String text = "a\nb\n";

        assertThat(AiSourceLines.normalize(text), is(sameInstance(text)));
    }

    @Test
    public void normalize_aCarriageReturnAtTheStart_isSeen() {
        // indexOf('\r') == 0 must not count as "none"
        assertThat(AiSourceLines.normalize("\rx"), is(equalTo("\nx")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="count">
    @Test
    public void count_emptyText_isZero() {
        assertThat(AiSourceLines.count(""), is(0));
    }

    @Test
    public void count_aFinalLineEnding_addsNoLine() {
        assertThat(AiSourceLines.count("a\nb\n"), is(2));
        assertThat(AiSourceLines.count("a\nb"), is(2));
        assertThat(AiSourceLines.count("a"), is(1));
        assertThat(AiSourceLines.count("\n"), is(1));
    }

    @Test
    public void count_crLfAndCr_countLikeLf() {
        assertThat(AiSourceLines.count("a\r\nb\r\n"), is(2));
        assertThat(AiSourceLines.count("a\rb\rc"), is(3));
        assertThat(AiSourceLines.count("a\r\n\r\nb"), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="split">
    @Test
    public void split_atEveryLineEnding_keepingTheTrailingEmptyLine() {
        assertThat(AiSourceLines.split("a\r\nb\rc\nd\n"), is(equalTo(new String[] {"a", "b", "c", "d", ""})));
    }

    @Test
    public void split_emptyText_isOneEmptyLine() {
        assertThat(AiSourceLines.split(""), is(equalTo(new String[] {""})));
    }
    // </editor-fold>
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiDeterministicSummaryTest {

    @Test
    public void emptySource_metadataOnly_zeroCounts() {
        final String body = AiDeterministicSummary.body("", "Empty.java", 5);
        assertThat(body, containsString("`Empty.java`"));
        assertThat(body, containsString("0 chars, 0 lines"));
        assertThat(body, not(containsString("```")));
    }

    @Test
    public void sampleLinesZero_metadataOnly() {
        final String body = AiDeterministicSummary.body("a\nb\n", "F.java", 0);
        assertThat(body, containsString("4 chars, 2 lines"));
        assertThat(body, not(containsString("```")));
    }

    @Test
    public void smallFile_showsAllContentOnce() {
        // 3 lines <= 2*5 -> a single "Content" block with every line, no First/Last split.
        final String body = AiDeterministicSummary.body("L1\nL2\nL3", "S.java", 5);
        assertThat(body, containsString("8 chars, 3 lines"));
        assertThat(body, containsString("Content:"));
        assertThat(body, containsString("L1"));
        assertThat(body, containsString("L3"));
        assertThat(body, not(containsString("First")));
    }

    @Test
    public void largeFile_showsHeadAndTailOnly() {
        // 10 lines > 2*2 -> First 2 + Last 2; the middle (ln5) is omitted.
        final String body =
                AiDeterministicSummary.body("ln0\nln1\nln2\nln3\nln4\nln5\nln6\nln7\nln8\nln9", "Big.java", 2);
        assertThat(body, containsString("First 2 lines:"));
        assertThat(body, containsString("Last 2 lines:"));
        assertThat(body, containsString("ln0"));
        assertThat(body, containsString("ln1"));
        assertThat(body, containsString("ln8"));
        assertThat(body, containsString("ln9"));
        assertThat(body, not(containsString("ln5")));
    }

    @Test
    public void exactlyDoubleSampleLines_stillShowsContentNotHeadTail() {
        // lines == 2*sampleLines is the boundary: <= keeps one "Content" block (not First/Last).
        final String body = AiDeterministicSummary.body("a\nb\nc\nd", "B.java", 2);
        assertThat(body, containsString("Content:"));
        assertThat(body, not(containsString("First")));
    }

    @Test
    public void lineCount_handlesTrailingNewlineAndNoTrailing() {
        assertThat(AiDeterministicSummary.body("a", "f", 0), containsString("1 lines"));
        assertThat(AiDeterministicSummary.body("a\n", "f", 0), containsString("1 lines"));
        assertThat(AiDeterministicSummary.body("a\nb", "f", 0), containsString("2 lines"));
    }

    @Test
    public void charCountReflectsSourceLength() {
        assertThat(AiDeterministicSummary.body("abcde", "f", 0), containsString("5 chars"));
    }
}

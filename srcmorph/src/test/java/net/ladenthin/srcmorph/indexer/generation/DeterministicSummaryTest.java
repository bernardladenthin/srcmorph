// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class DeterministicSummaryTest {

    // <editor-fold defaultstate="collapsed" desc="body">
    @Test
    public void body_emptySource_metadataOnlyWithZeroCounts() {
        // act
        final String body = DeterministicSummary.body("", "Empty.java", 5);

        // assert
        assertThat(body, containsString("`Empty.java`"));
        assertThat(body, containsString("0 chars, 0 lines"));
        assertThat(body, not(containsString("```")));
    }

    @Test
    public void body_sampleLinesZero_metadataOnly() {
        // act
        final String body = DeterministicSummary.body("a\nb\n", "F.java", 0);

        // assert
        assertThat(body, containsString("4 chars, 2 lines"));
        assertThat(body, not(containsString("```")));
    }

    @Test
    public void body_smallFile_showsAllContentOnce() {
        // act
        // 3 lines <= 2*5 -> a single "Content" block with every line, no First/Last split.
        final String body = DeterministicSummary.body("L1\nL2\nL3", "S.java", 5);

        // assert
        assertThat(body, containsString("8 chars, 3 lines"));
        assertThat(body, containsString("Content:"));
        assertThat(body, containsString("L1"));
        assertThat(body, containsString("L3"));
        assertThat(body, not(containsString("First")));
    }

    @Test
    public void body_largeFile_showsHeadAndTailOnly() {
        // act
        // 10 lines > 2*2 -> First 2 + Last 2; the middle (ln5) is omitted.
        final String body =
                DeterministicSummary.body("ln0\nln1\nln2\nln3\nln4\nln5\nln6\nln7\nln8\nln9", "Big.java", 2);

        // assert
        assertThat(body, containsString("First 2 lines:"));
        assertThat(body, containsString("Last 2 lines:"));
        assertThat(body, containsString("ln0"));
        assertThat(body, containsString("ln1"));
        assertThat(body, containsString("ln8"));
        assertThat(body, containsString("ln9"));
        assertThat(body, not(containsString("ln5")));
    }

    @Test
    public void body_largeFileEndingWithALineBreak_theLastLinesAreTheLastLines() {
        // act
        // the trailing "\n" splits into one more, empty element, which must not take a line's place
        final String body = DeterministicSummary.body("ln0\nln1\nln2\nln3\nln4\nln5\n", "Big.java", 2);

        // assert
        assertThat(body, containsString("Last 2 lines:\n```\nln4\nln5\n```\n"));
    }

    @Test
    public void body_smallFileEndingWithALineBreak_hasNoEmptyLastLine() {
        // act
        final String body = DeterministicSummary.body("L1\nL2\n", "S.java", 5);

        // assert
        assertThat(body, containsString("Content:\n```\nL1\nL2\n```\n"));
    }

    @Test
    public void body_exactlyDoubleSampleLines_stillShowsContentNotHeadTail() {
        // act
        // lines == 2*sampleLines is the boundary: <= keeps one "Content" block (not First/Last).
        final String body = DeterministicSummary.body("a\nb\nc\nd", "B.java", 2);

        // assert
        assertThat(body, containsString("Content:"));
        assertThat(body, not(containsString("First")));
    }

    @Test
    public void body_trailingNewlineOrNone_countsTheLines() {
        // act, assert
        assertThat(DeterministicSummary.body("a", "f", 0), containsString("1 lines"));
        assertThat(DeterministicSummary.body("a\n", "f", 0), containsString("1 lines"));
        assertThat(DeterministicSummary.body("a\nb", "f", 0), containsString("2 lines"));
    }

    @Test
    public void body_anySource_charCountReflectsSourceLength() {
        // act, assert
        assertThat(DeterministicSummary.body("abcde", "f", 0), containsString("5 chars"));
    }

    @Test
    public void body_aSourceWithoutBackticks_isFencedWithThree() {
        // act, assert
        assertThat(
                DeterministicSummary.body("a\nb", "f", 1),
                is(equalTo("> Large file summarized deterministically (no AI): `f` - 3 chars, 2 lines.\n"
                        + "\nContent:\n```\na\nb\n```\n")));
    }

    @Test
    public void body_aMarkdownSourceWithAFence_isFencedLongerThanItsLongestBacktickRun() {
        // arrange: a Markdown file whose own fence would close a fence of three
        final String source = "# Title\n```java\ncode\n```\n";

        // act
        final String body = DeterministicSummary.body(source, "README.md", 10);

        // assert
        assertThat(body, containsString("\nContent:\n````\n# Title\n```java\ncode\n```\n````\n"));
    }

    @Test
    public void body_backtickRuns_theFenceOutgrowsTheLongestRunOfTheLinesItShows() {
        // act, assert
        // the longest run (five) is not the first one, and a line may end in it
        assertThat(DeterministicSummary.body("``a\nb`````", "f", 1), containsString("\n``````\n``a\nb`````\n``````\n"));
        // two runs of two in one line are no run of four
        assertThat(DeterministicSummary.body("``a``", "f", 1), containsString("\n```\n``a``\n```\n"));
        // the head and the tail block each count the lines they show
        assertThat(
                DeterministicSummary.body("````\nb\nc\nd\ne", "f", 2),
                containsString("First 2 lines:\n`````\n````\nb\n`````\n\nLast 2 lines:\n```\nd\ne\n```\n"));
    }
    // </editor-fold>
}

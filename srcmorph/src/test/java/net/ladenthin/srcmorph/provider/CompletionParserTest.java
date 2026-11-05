// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CompletionParserTest {

    private final CompletionParser parser = new CompletionParser();

    // <editor-fold defaultstate="collapsed" desc="parseCompletion">
    @Test
    public void parseCompletion_completeThinkingBlock_returnsBodyOnly() throws Exception {
        // arrange
        final String response = CompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + CompletionParser.THINKING_BLOCK_END_MARKER + "\nactual answer";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_truncatedThinkingBlock_throwsIOException() {
        // arrange
        final String response = CompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning only, no end";

        try {
            // act
            parser.parseCompletion(response);
            fail("Expected IOException");
        } catch (final IOException e) {
            // assert
            assertThat(e.getMessage(), containsString(CompletionParser.THINKING_BLOCK_START_MARKER));
            assertThat(e.getMessage(), containsString(CompletionParser.THINKING_BLOCK_END_MARKER));
        }
    }

    @Test
    public void parseCompletion_noThinkingBlock_returnsTrimmedResponse() throws Exception {
        // arrange
        final String response = "  plain answer  ";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("plain answer")));
    }

    @Test
    public void parseCompletion_nullResponse_returnsEmptyString() throws Exception {
        // act
        final String result = parser.parseCompletion(null);

        // assert
        assertThat(result, is(equalTo("")));
    }

    @Test
    public void parseCompletion_emptyThinkingBlock_returnsAnswerDirectlyAfterIt() throws Exception {
        // The end marker follows the start marker directly, at index 0 of the block's text.
        // Pins the < 0 check: a <= 0 mutation would report this complete block as truncated.
        // arrange
        final String response = CompletionParser.THINKING_BLOCK_START_MARKER
                + CompletionParser.THINKING_BLOCK_END_MARKER + "actual answer";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_thinkingBlockAfterLeadingWhitespace_returnsBodyOnly() throws Exception {
        // arrange
        final String response = "\n  " + CompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + CompletionParser.THINKING_BLOCK_END_MARKER + "\nactual answer";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    /**
     * A summary of this very parser quotes both markers. Only a block at the start is reasoning;
     * stripping up to the last end marker anywhere cut such a summary down to its tail.
     */
    @Test
    public void parseCompletion_markersQuotedInAnswer_returnsWholeAnswer() throws Exception {
        // arrange
        final String response = "### CompletionParser.java\nStrips a thinking block ("
                + CompletionParser.THINKING_BLOCK_START_MARKER + " ... "
                + CompletionParser.THINKING_BLOCK_END_MARKER + ").\n\n#### Exceptions\n- IOException";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(response)));
    }

    @Test
    public void parseCompletion_startMarkerQuotedInAnswerWithoutEndMarker_returnsWholeAnswer() throws Exception {
        // arrange
        final String response = "Opens with " + CompletionParser.THINKING_BLOCK_START_MARKER + " only.";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(response)));
    }

    @Test
    public void parseCompletion_answerAfterThinkingBlockQuotesEndMarker_keepsTheQuote() throws Exception {
        // arrange
        final String answer = "The block closes at " + CompletionParser.THINKING_BLOCK_END_MARKER + ".";
        final String response = CompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + CompletionParser.THINKING_BLOCK_END_MARKER + "\n" + answer;

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(answer)));
    }
    // </editor-fold>

    // <think> blocks, as DeepSeek-R1 and Qwen3 write them
    @Test
    public void parseCompletion_leadingThinkTag_isStripped() throws Exception {
        final String result =
                new CompletionParser().parseCompletion("  <think>\nweighing it up\n</think>\n\nactual answer  ");

        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_unclosedThinkTag_isATruncatedAnswer() {
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> new CompletionParser().parseCompletion("<think>still weighing it up"));

        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
        assertThat(thrown.getMessage(), containsString(CompletionParser.THINK_TAG_START_MARKER));
        assertThat(thrown.getMessage(), containsString(CompletionParser.THINK_TAG_END_MARKER));
    }

    @Test
    public void parseCompletion_thinkTagQuotedInTheAnswer_isKept() throws Exception {
        final String response = "The parser strips a leading <think> ... </think> block.";

        assertThat(new CompletionParser().parseCompletion(response), is(equalTo(response)));
    }

    /**
     * A chat template that opens the thinking block itself (Qwen3, DeepSeek-R1) leaves the answer with only
     * the closing tag: the reasoning before it is no part of the answer.
     */
    @Test
    public void parseCompletion_reasoningClosedByATagTheTemplateOpened_isStripped() throws Exception {
        assertThat(
                parser.parseCompletion("Let me read the file first.\n</think>\n\n> The lead.\nThe body."),
                is(equalTo("> The lead.\nThe body.")));
    }

    /** No reasoning at all before the template's block was closed: the answer is what follows. */
    @Test
    public void parseCompletion_aResponseStartingWithTheClosingTag_isTheTextAfterIt() throws Exception {
        assertThat(parser.parseCompletion("</think>\n> The lead."), is(equalTo("> The lead.")));
    }

    /** A closing tag quoted in backticks is the answer writing about it, so nothing is stripped. */
    @Test
    public void parseCompletion_aClosingTagQuotedInBackticks_isKept() throws Exception {
        final String response = "> The lead.\nThe block ends at `</think>`, the answer follows.";

        assertThat(parser.parseCompletion(response), is(equalTo(response)));
    }
}

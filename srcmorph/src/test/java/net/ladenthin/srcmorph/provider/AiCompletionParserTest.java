// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class AiCompletionParserTest {

    private final AiCompletionParser parser = new AiCompletionParser();

    // <editor-fold defaultstate="collapsed" desc="parseCompletion">
    @Test
    public void parseCompletion_completeThinkingBlock_returnsBodyOnly() throws Exception {
        // arrange
        final String response = AiCompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + AiCompletionParser.THINKING_BLOCK_END_MARKER + "\nactual answer";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_truncatedThinkingBlock_throwsIOException() {
        // arrange
        final String response = AiCompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning only, no end";

        try {
            // act
            parser.parseCompletion(response);
            fail("Expected IOException");
        } catch (final IOException e) {
            // assert
            assertThat(e.getMessage(), containsString(AiCompletionParser.THINKING_BLOCK_START_MARKER));
            assertThat(e.getMessage(), containsString(AiCompletionParser.THINKING_BLOCK_END_MARKER));
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
        final String response = AiCompletionParser.THINKING_BLOCK_START_MARKER
                + AiCompletionParser.THINKING_BLOCK_END_MARKER + "actual answer";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_thinkingBlockAfterLeadingWhitespace_returnsBodyOnly() throws Exception {
        // arrange
        final String response = "\n  " + AiCompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + AiCompletionParser.THINKING_BLOCK_END_MARKER + "\nactual answer";

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
        final String response = "### AiCompletionParser.java\nStrips a thinking block ("
                + AiCompletionParser.THINKING_BLOCK_START_MARKER + " ... "
                + AiCompletionParser.THINKING_BLOCK_END_MARKER + ").\n\n#### Exceptions\n- IOException";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(response)));
    }

    @Test
    public void parseCompletion_startMarkerQuotedInAnswerWithoutEndMarker_returnsWholeAnswer() throws Exception {
        // arrange
        final String response = "Opens with " + AiCompletionParser.THINKING_BLOCK_START_MARKER + " only.";

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(response)));
    }

    @Test
    public void parseCompletion_answerAfterThinkingBlockQuotesEndMarker_keepsTheQuote() throws Exception {
        // arrange
        final String answer = "The block closes at " + AiCompletionParser.THINKING_BLOCK_END_MARKER + ".";
        final String response = AiCompletionParser.THINKING_BLOCK_START_MARKER + "\nreasoning\n"
                + AiCompletionParser.THINKING_BLOCK_END_MARKER + "\n" + answer;

        // act
        final String result = parser.parseCompletion(response);

        // assert
        assertThat(result, is(equalTo(answer)));
    }
    // </editor-fold>
}

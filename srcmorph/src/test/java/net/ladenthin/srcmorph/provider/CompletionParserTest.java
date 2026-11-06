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

    // <think> blocks, as DeepSeek-R1 and Qwen3 write them
    @Test
    public void parseCompletion_leadingThinkTag_isStripped() throws Exception {
        // act
        final String result =
                new CompletionParser().parseCompletion("  <think>\nweighing it up\n</think>\n\nactual answer  ");

        // assert
        assertThat(result, is(equalTo("actual answer")));
    }

    @Test
    public void parseCompletion_unclosedThinkTag_isATruncatedAnswer() {
        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> new CompletionParser().parseCompletion("<think>still weighing it up"));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
        assertThat(thrown.getMessage(), containsString(CompletionParser.THINK_TAG_START_MARKER));
        assertThat(thrown.getMessage(), containsString(CompletionParser.THINK_TAG_END_MARKER));
    }

    @Test
    public void parseCompletion_thinkTagQuotedInTheAnswer_isKept() throws Exception {
        // arrange
        final String response = "The parser strips a leading <think> ... </think> block.";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(response), is(equalTo(response)));
    }

    /**
     * A chat template that opens the thinking block itself (Qwen3, DeepSeek-R1) leaves the answer with only
     * the closing tag: the reasoning before it is no part of the answer.
     */
    @Test
    public void parseCompletion_reasoningClosedByATagTheTemplateOpened_isStripped() throws Exception {
        // act, assert
        assertThat(
                parser.parseCompletion("Let me read the file first.\n</think>\n\n> The lead.\nThe body."),
                is(equalTo("> The lead.\nThe body.")));
    }

    /** No reasoning at all before the template's block was closed: the answer is what follows. */
    @Test
    public void parseCompletion_aResponseStartingWithTheClosingTag_isTheTextAfterIt() throws Exception {
        // act, assert
        assertThat(parser.parseCompletion("</think>\n> The lead."), is(equalTo("> The lead.")));
    }

    /** A closing tag quoted in backticks is the answer writing about it, so nothing is stripped. */
    @Test
    public void parseCompletion_aClosingTagQuotedInBackticks_isKept() throws Exception {
        // arrange
        final String response = "> The lead.\nThe block ends at `</think>`, the answer follows.";

        // act, assert
        assertThat(parser.parseCompletion(response), is(equalTo(response)));
    }

    /** A closing tag in double quotes, at the end of its line, is the answer writing about it too. */
    @Test
    public void parseCompletion_aClosingTagInDoubleQuotesAtALineEnd_isKept() throws Exception {
        // arrange
        final String response = "> Strips the reasoning up to \"</think>\nThe body.";

        // act, assert
        assertThat(parser.parseCompletion(response), is(equalTo(response)));
    }

    /** A closing tag inside a sentence names the tag; a template's tag ends its line. */
    @Test
    public void parseCompletion_aClosingTagInsideASentence_isKept() throws Exception {
        // arrange
        final String response = "> Strips the reasoning up to </think> and keeps the rest.\nThe body.";

        // act, assert
        assertThat(parser.parseCompletion(response), is(equalTo(response)));
    }

    /** The template's tag at the very end: the reasoning is all there is, the answer is empty. */
    @Test
    public void parseCompletion_reasoningEndingInTheClosingTag_isEmpty() throws Exception {
        // act, assert
        assertThat(parser.parseCompletion("Thinking it over.\n</think>"), is(equalTo("")));
    }

    /** A tag at the end of a list item's line is the answer naming it: the lead before it stays. */
    @Test
    public void parseCompletion_aClosingTagEndingAListItem_isKept() throws Exception {
        // arrange
        final String response = "> Parses model completions.\n\n- Closing marker: </think>\n- Gemma: <|channel>thought";

        // act, assert
        assertThat(parser.parseCompletion(response), is(equalTo(response)));
    }

    /** Reasoning that names the tag before its real end: the tag alone on its line ends it. */
    @Test
    public void parseCompletion_reasoningNamingTheTagBeforeItsEnd_isStrippedAtTheTagAloneOnItsLine() throws Exception {
        // arrange
        final String response = "It strips up to `</think>` first.\nSo it goes.\n</think>\n\n> Lead.";

        // act, assert
        assertThat(parser.parseCompletion(response), is(equalTo("> Lead.")));
    }

    /** Spaces around the tag on its line do not make it text. */
    @Test
    public void parseCompletion_reasoningSentenceEndingInTheTag_isStripped() throws Exception {
        // act
        final String answer = new CompletionParser().parseCompletion("I will now write it.</think>\n\n> The lead.");

        // assert
        assertThat(answer, is("> The lead."));
    }

    @Test
    public void parseCompletion_tagAfterASpaceAtALineEndWithoutALead_isKept() throws Exception {
        // arrange: no lead before, so only the character before the tag tells it from reasoning
        final String response = "Closing marker: </think>\nThe rest.";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(response), is(response));
    }

    @Test
    public void parseCompletion_tagAfterAQuoteAtALineEndWithoutALead_isKept() throws Exception {
        // arrange: the first of the characters that mark a named tag, with no lead before
        final String response = "It strips \"</think>\nThe rest.";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(response), is(response));
    }

    @Test
    public void parseCompletion_gluedTagFollowedByTextWithoutALead_isKept() throws Exception {
        // arrange: glued to a word, but the line goes on
        final String response = "It strips x</think> tags.\nThe rest.";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(response), is(response));
    }

    @Test
    public void parseCompletion_gluedTagAfterTheLead_isKept() throws Exception {
        // arrange: an answer that names the tag glued to a list item's label
        final String response = "> Strips a template-opened reasoning block.\n\n- End marker:</think>\n- The rest.";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(response), is(response));
    }

    @Test
    public void parseCompletion_gluedTagInAFence_isKept() throws Exception {
        // arrange: a template quoted in a fence, with no lead before it, backtick and tilde
        final String ticks = "```jinja\n{{ reasoning }}</think>\n{{ content }}\n```";
        final String tildes = "~~~jinja\n{{ reasoning }}</think>\n{{ content }}\n~~~";

        // act, assert
        assertThat(new CompletionParser().parseCompletion(ticks), is(ticks));
        assertThat(new CompletionParser().parseCompletion(tildes), is(tildes));
    }

    @Test
    public void parseCompletion_tagAloneOnALaterLine_winsOverAGluedOneBefore() throws Exception {
        // act: the reasoning names the tag glued to a word first, then ends at the tag alone on its line
        final String answer =
                new CompletionParser().parseCompletion("It ends with x</think>\nso here:\n</think>\n> The lead.");

        // assert
        assertThat(answer, is("> The lead."));
    }

    @Test
    public void parseCompletion_tagAfterAQuoteAtALineEnd_isKept() throws Exception {
        // act: a quote before the tag, and a backtick, mark a tag the text names
        final String quoted = new CompletionParser().parseCompletion("> A parser.\nIt strips \"</think>\n");
        final String ticked = new CompletionParser().parseCompletion("> A parser.\nIt strips `</think>\n");

        // assert
        assertThat(quoted, is("> A parser.\nIt strips \"</think>"));
        assertThat(ticked, is("> A parser.\nIt strips `</think>"));
    }

    @Test
    public void parseCompletion_gluedTagFollowedByText_isKept() throws Exception {
        // act: the tag is glued to a word but the line goes on
        final String answer = new CompletionParser().parseCompletion("> A parser.\nIt strips x</think> tags.");

        // assert
        assertThat(answer, is("> A parser.\nIt strips x</think> tags."));
    }

    @Test
    public void parseCompletion_onlyTheTag_isEmpty() throws Exception {
        // act, assert: a tag at the very start is alone on its line
        assertThat(new CompletionParser().parseCompletion("</think>"), is(""));
    }

    @Test
    public void parseCompletion_closingTagWithSpacesOnItsLine_isStripped() throws Exception {
        // act, assert
        assertThat(parser.parseCompletion("Thinking.\n  </think>  \n> The lead."), is(equalTo("> The lead.")));
    }

    /** A carriage return after the template's tag ends its line as well. */
    @Test
    public void parseCompletion_closingTagBeforeACarriageReturn_isStripped() throws Exception {
        // act, assert
        assertThat(parser.parseCompletion("Thinking.\n</think>\r\n> The lead."), is(equalTo("> The lead.")));
    }
    // </editor-fold>
}

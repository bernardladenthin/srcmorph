// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

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
    // </editor-fold>
}

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatMessage;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import org.junit.jupiter.api.Test;

public class GenerationUsageTest {

    private static ChatResponse response(final String finishReason) {
        return new ChatResponse(
                "id",
                Collections.singletonList(new ChatChoice(0, new ChatMessage("assistant", "text"), finishReason)),
                new Usage(120, 30, 100),
                null,
                "{}");
    }

    @Test
    public void of_readsTheUsageAndTheFinishReason() {
        final GenerationUsage usage = GenerationUsage.of(response("length"));

        assertThat(usage.calls(), is(1));
        assertThat(usage.promptTokens(), is(120L));
        assertThat(usage.completionTokens(), is(30L));
        assertThat(usage.cachedPromptTokens(), is(100L));
        assertThat(usage.finishReason(), is(equalTo("length")));
    }

    @Test
    public void of_noChoiceOrNoReason_isAnEmptyReason() {
        final ChatResponse noChoice =
                new ChatResponse("id", Collections.<ChatChoice>emptyList(), new Usage(1, 2), null, "{}");

        assertThat(GenerationUsage.of(noChoice).finishReason(), is(equalTo("")));
        assertThat(GenerationUsage.of(response(null)).finishReason(), is(equalTo("")));
    }

    @Test
    public void withCall_sumsTheTokens_countsTheCall_keepsTheLastReason() {
        final GenerationUsage sum = GenerationUsage.NONE
                .withCall(new GenerationUsage(1, 10, 2, 5, "length"))
                .withCall(new GenerationUsage(1, 20, 3, 0, "stop"))
                .withCall(GenerationUsage.NONE);

        // the third call's provider knew no usage: the call is counted, its tokens are none
        assertThat(sum, is(equalTo(new GenerationUsage(3, 30, 5, 5, ""))));
        assertThat(
                GenerationUsage.NONE
                        .withCall(new GenerationUsage(1, 1, 1, 1, "stop"))
                        .finishReason(),
                is(equalTo("stop")));
    }

    /** A sum of two calls would be counted as one, so it is refused. */
    @Test
    public void withCall_theSumOfSeveralCalls_isRefused() {
        final GenerationUsage two = new GenerationUsage(2, 10, 2, 0, "stop");

        final IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> GenerationUsage.NONE.withCall(two));

        assertThat(thrown.getMessage(), is(equalTo("withCall takes the usage of one call, not of 2")));
    }
}

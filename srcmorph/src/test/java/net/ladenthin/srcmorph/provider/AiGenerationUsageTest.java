// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatMessage;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import org.junit.jupiter.api.Test;

public class AiGenerationUsageTest {

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
        final AiGenerationUsage usage = AiGenerationUsage.of(response("length"));

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

        assertThat(AiGenerationUsage.of(noChoice).finishReason(), is(equalTo("")));
        assertThat(AiGenerationUsage.of(response(null)).finishReason(), is(equalTo("")));
    }

    @Test
    public void add_sumsTheTokens_countsTheCall_keepsTheLastReason() {
        final AiGenerationUsage sum = AiGenerationUsage.NONE
                .add(new AiGenerationUsage(1, 10, 2, 5, "length"))
                .add(new AiGenerationUsage(1, 20, 3, 0, "stop"))
                .add(AiGenerationUsage.NONE);

        assertThat(sum, is(equalTo(new AiGenerationUsage(3, 30, 5, 5, ""))));
        assertThat(
                AiGenerationUsage.NONE
                        .add(new AiGenerationUsage(1, 1, 1, 1, "stop"))
                        .finishReason(),
                is(equalTo("stop")));
    }
}

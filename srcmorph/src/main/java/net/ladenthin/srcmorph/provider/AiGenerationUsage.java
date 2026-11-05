// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;

/**
 * The tokens of generations and how the last one ended: one call as a provider reports it
 * ({@link AiGenerationProvider#lastUsage()}), or the sum of the calls of one file
 * ({@link #withCall(AiGenerationUsage)}). Immutable.
 */
@ToString
@EqualsAndHashCode
public final class AiGenerationUsage {

    /** No call, nothing known. */
    public static final AiGenerationUsage NONE = new AiGenerationUsage(0, 0L, 0L, 0L, "");

    private final int calls;

    private final long promptTokens;

    private final long completionTokens;

    private final long cachedPromptTokens;

    private final String finishReason;

    /**
     * Creates a usage.
     *
     * @param calls              the generations counted
     * @param promptTokens       the prompt tokens, the cached ones included
     * @param completionTokens   the generated tokens, reasoning included
     * @param cachedPromptTokens the prompt tokens served from a cache
     * @param finishReason       how the last generation ended ({@code stop}, {@code length}, ...), empty
     *                           when unknown
     */
    public AiGenerationUsage(
            final int calls,
            final long promptTokens,
            final long completionTokens,
            final long cachedPromptTokens,
            final String finishReason) {
        this.calls = calls;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.cachedPromptTokens = cachedPromptTokens;
        this.finishReason = finishReason;
    }

    /**
     * Returns the usage of one chat response: its usage block and the finish reason of its first choice.
     *
     * @param response the parsed response
     * @return one call's usage
     */
    public static AiGenerationUsage of(final ChatResponse response) {
        final Usage usage = response.getUsage();
        final List<ChatChoice> choices = response.getChoices();
        final String reason = choices.isEmpty() ? null : choices.get(0).getFinishReason();
        return new AiGenerationUsage(
                1,
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getCachedTokens(),
                reason == null ? "" : reason);
    }

    /**
     * Returns this usage with one more call counted: its tokens added, its finish reason the last one.
     * A call whose provider reports nothing ({@link #NONE}) still counts as a call: it was made.
     *
     * @param call the usage of that one call, {@link #NONE} when the provider knows none
     * @return the sum
     * @throws IllegalArgumentException if {@code call} is the sum of several calls, which this would count
     *                                  as one
     */
    public AiGenerationUsage withCall(final AiGenerationUsage call) {
        if (call.calls > 1) {
            throw new IllegalArgumentException("withCall takes the usage of one call, not of " + call.calls);
        }
        return new AiGenerationUsage(
                calls + 1,
                promptTokens + call.promptTokens,
                completionTokens + call.completionTokens,
                cachedPromptTokens + call.cachedPromptTokens,
                call.finishReason);
    }

    /**
     * Returns the generations counted.
     *
     * @return the calls
     */
    public int calls() {
        return calls;
    }

    /**
     * Returns the prompt tokens, the cached ones included.
     *
     * @return the tokens
     */
    public long promptTokens() {
        return promptTokens;
    }

    /**
     * Returns the generated tokens, reasoning included.
     *
     * @return the tokens
     */
    public long completionTokens() {
        return completionTokens;
    }

    /**
     * Returns the prompt tokens served from a cache.
     *
     * @return the tokens
     */
    public long cachedPromptTokens() {
        return cachedPromptTokens;
    }

    /**
     * Returns how the last generation ended.
     *
     * @return {@code stop}, {@code length}, ..., empty when unknown
     */
    public String finishReason() {
        return finishReason;
    }
}

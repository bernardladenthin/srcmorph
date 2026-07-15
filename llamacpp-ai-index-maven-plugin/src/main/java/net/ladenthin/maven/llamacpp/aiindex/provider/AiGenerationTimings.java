// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.provider;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord;

/**
 * A generated text plus the model's measured timing for that generation, used by the
 * {@code ai-index:calibrate} goal to derive per-machine throughput. Prefill = prompt processing; decode =
 * answer generation. Rates of {@code 0} mean the provider did not report timings (e.g. the mock provider
 * or a binding that omits them).
 *
 * <p>Record-shaped value type marked {@link ConvertToRecord} for the future Java&nbsp;17+ migration; the
 * accessors follow record style ({@code text()}, not {@code getText()}).</p>
 */
@ConvertToRecord
@ToString
@EqualsAndHashCode
public final class AiGenerationTimings {

    private final String text;
    private final int promptTokens;
    private final double prefillTokensPerSecond;
    private final int predictedTokens;
    private final double decodeTokensPerSecond;

    /**
     * Creates a new {@link AiGenerationTimings}.
     *
     * @param text                   the generated (parsed) text
     * @param promptTokens           number of prompt tokens the model processed (prefill)
     * @param prefillTokensPerSecond measured prefill throughput (tokens/second)
     * @param predictedTokens        number of tokens the model generated (decode)
     * @param decodeTokensPerSecond  measured decode throughput (tokens/second)
     */
    public AiGenerationTimings(
            final String text,
            final int promptTokens,
            final double prefillTokensPerSecond,
            final int predictedTokens,
            final double decodeTokensPerSecond) {
        this.text = text;
        this.promptTokens = promptTokens;
        this.prefillTokensPerSecond = prefillTokensPerSecond;
        this.predictedTokens = predictedTokens;
        this.decodeTokensPerSecond = decodeTokensPerSecond;
    }

    /**
     * Returns the generated (parsed) text.
     *
     * @return the text
     */
    public String text() {
        return text;
    }

    /**
     * Returns the number of prompt tokens the model processed.
     *
     * @return the prompt token count
     */
    public int promptTokens() {
        return promptTokens;
    }

    /**
     * Returns the measured prefill throughput (tokens/second).
     *
     * @return the prefill tokens per second
     */
    public double prefillTokensPerSecond() {
        return prefillTokensPerSecond;
    }

    /**
     * Returns the number of tokens the model generated.
     *
     * @return the predicted token count
     */
    public int predictedTokens() {
        return predictedTokens;
    }

    /**
     * Returns the measured decode throughput (tokens/second).
     *
     * @return the decode tokens per second
     */
    public double decodeTokensPerSecond() {
        return decodeTokensPerSecond;
    }
}

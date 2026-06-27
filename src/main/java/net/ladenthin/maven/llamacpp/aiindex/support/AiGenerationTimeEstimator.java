// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import lombok.ToString;

/**
 * Rough, reference-CPU duration estimate for a single AI generation, used to warn the
 * user before a large file blocks the build for minutes.
 *
 * <p><b>Why this is non-trivial.</b> On CPU, generation time is <em>not</em> a constant
 * tokens/second: it is dominated by <em>prefill</em> (processing the prompt), whose
 * per-token cost grows linearly with the prompt length {@code n} because attention is
 * {@code O(n)} per token — so total prefill is {@code O(n^2)}. Decode (generating the
 * answer) is comparatively small but also slows as the context grows. The model below
 * was fitted to three end-to-end measurements and reproduces them to within ~0.5 %
 * (see {@code AiGenerationTimeEstimatorTest}):</p>
 *
 * <pre>
 *   prompt tokens n   measured prefill   model prefill
 *   3 309             88.3 s             88.1 s
 *   24 081            978.0 s            978.5 s
 *   61 484            4053 s             4049 s
 * </pre>
 *
 * <p><b>Calibration.</b> The constants are measured on an AMD Ryzen 7 5800H (CPU-only,
 * 8 threads) running gpt-oss-20b UD-Q4_K_XL via llama.cpp. They are intentionally a
 * <em>rough</em> estimate: a different CPU, thread count, model, or quantisation shifts
 * the absolute numbers (a GPU would make them far smaller). Treat the output as an
 * order-of-magnitude hint, not a guarantee. The <em>shape</em> (quadratic prefill) holds
 * across hardware; only the coefficients scale.</p>
 */
@ToString
public class AiGenerationTimeEstimator {

    /**
     * Linear prefill coefficient in milliseconds per prompt token. Dominates for small
     * prompts; fitted on the reference CPU (see class Javadoc).
     */
    public static final double PREFILL_LINEAR_MS_PER_TOKEN = 24.4d;

    /**
     * Quadratic prefill coefficient in milliseconds per prompt token squared. Dominates
     * for large prompts and is the reason throughput "shrinks" as files grow.
     */
    public static final double PREFILL_QUADRATIC_MS_PER_TOKEN_SQUARED = 0.000674d;

    /**
     * Linear decode coefficient in milliseconds per generated token at (near) zero
     * context length.
     */
    public static final double DECODE_LINEAR_MS_PER_TOKEN = 56.8d;

    /**
     * Additional decode milliseconds per generated token for each token already in the
     * context — captures the decode slowdown on long prompts.
     */
    public static final double DECODE_MS_PER_TOKEN_PER_CONTEXT_TOKEN = 0.01568d;

    /**
     * Measured characters per token for dense Java source (~4.2). Used only to estimate
     * the token count from a character count; this is deliberately distinct from the
     * conservative {@code charsPerToken} used for input trimming.
     */
    public static final double ESTIMATION_CHARS_PER_TOKEN = 4.2d;

    /**
     * Approximate fixed prompt-template token overhead added on top of the source tokens
     * (system instructions plus chat-template scaffolding).
     */
    public static final int PROMPT_TEMPLATE_TOKEN_OVERHEAD = 400;

    /**
     * Typical number of generated output tokens assumed for the decode part of the
     * estimate when the caller does not specify one. Observed summaries ranged ~450–1200
     * tokens; this is a representative middle value.
     */
    public static final int DEFAULT_EXPECTED_OUTPUT_TOKENS = 800;

    /** Milliseconds per second, for converting the internal millisecond model to seconds. */
    public static final double MILLIS_PER_SECOND = 1000.0d;

    /** Seconds per minute, for the human-readable duration formatting. */
    public static final long SECONDS_PER_MINUTE = 60L;

    /**
     * Threshold (seconds) at or above which {@link #formatDuration(long)} switches from a
     * "{@code ~N s}" rendering to a "{@code ~N min}" rendering.
     */
    public static final long MINUTE_FORMAT_THRESHOLD_SECONDS = 90L;

    /** Creates a new {@link AiGenerationTimeEstimator}. */
    public AiGenerationTimeEstimator() {
        // no-op
    }

    /**
     * Estimates the prompt token count for a source of the given character length,
     * including the fixed template overhead.
     *
     * @param sourceChars number of source characters that will be sent to the model
     * @return estimated prompt token count (source tokens + template overhead)
     */
    public int estimatePromptTokens(final int sourceChars) {
        return (int) Math.round(sourceChars / ESTIMATION_CHARS_PER_TOKEN) + PROMPT_TEMPLATE_TOKEN_OVERHEAD;
    }

    /**
     * Estimates prefill (prompt-processing) time for a prompt of {@code promptTokens}
     * tokens using the quadratic model.
     *
     * @param promptTokens prompt length in tokens
     * @return estimated prefill time in milliseconds
     */
    public double estimatePrefillMillis(final int promptTokens) {
        final double n = promptTokens;
        return PREFILL_LINEAR_MS_PER_TOKEN * n + PREFILL_QUADRATIC_MS_PER_TOKEN_SQUARED * n * n;
    }

    /**
     * Estimates decode (answer-generation) time for the given context length and expected
     * output token count.
     *
     * @param promptTokens         context length in tokens (the prompt already prefilled)
     * @param expectedOutputTokens number of tokens the model is expected to generate
     * @return estimated decode time in milliseconds
     */
    public double estimateDecodeMillis(final int promptTokens, final int expectedOutputTokens) {
        return expectedOutputTokens
                * (DECODE_LINEAR_MS_PER_TOKEN + DECODE_MS_PER_TOKEN_PER_CONTEXT_TOKEN * promptTokens);
    }

    /**
     * Estimates total generation time (prefill + decode) in seconds for a source of the
     * given character length, assuming {@link #DEFAULT_EXPECTED_OUTPUT_TOKENS} of output.
     *
     * @param sourceChars number of source characters that will be sent to the model
     * @return estimated total time in seconds, rounded to the nearest second
     */
    public long estimateSeconds(final int sourceChars) {
        return estimateSeconds(sourceChars, DEFAULT_EXPECTED_OUTPUT_TOKENS);
    }

    /**
     * Estimates total generation time (prefill + decode) in seconds for a source of the
     * given character length and expected output token count.
     *
     * @param sourceChars          number of source characters that will be sent to the model
     * @param expectedOutputTokens number of tokens the model is expected to generate
     * @return estimated total time in seconds, rounded to the nearest second
     */
    public long estimateSeconds(final int sourceChars, final int expectedOutputTokens) {
        final int promptTokens = estimatePromptTokens(sourceChars);
        final double millis =
                estimatePrefillMillis(promptTokens) + estimateDecodeMillis(promptTokens, expectedOutputTokens);
        return Math.round(millis / MILLIS_PER_SECOND);
    }

    /**
     * Formats a second count as a short human-readable string: "{@code ~N s}" below
     * {@link #MINUTE_FORMAT_THRESHOLD_SECONDS}, otherwise "{@code ~N min}".
     *
     * @param seconds duration in seconds
     * @return short, prefixed duration string
     */
    public String formatDuration(final long seconds) {
        if (seconds >= MINUTE_FORMAT_THRESHOLD_SECONDS) {
            final long minutes = Math.round((double) seconds / (double) SECONDS_PER_MINUTE);
            return "~" + minutes + " min";
        }
        return "~" + seconds + " s";
    }
}

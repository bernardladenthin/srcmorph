// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import lombok.ToString;

/**
 * Rough duration estimate for a single AI generation, based on GPT-6 Luna, the model the benchmark
 * measures remotely: the plan's time, the progress bar and the per-file log line use it for every model
 * without a {@code calibration} of its own.
 *
 * <p><b>The model.</b> A call costs a fixed latency, a little per prompt token and most per generated
 * token, all linear: {@code 2.1 s + 0.105 ms * prompt tokens + 6.45 ms * output tokens} (about 155
 * tokens a second). Fitted by least squares over 1423 single-call files of the 2026-11 measurements
 * (Java, the language and the large corpus, a TypeScript repository; prompts up to 98K tokens); its sum
 * is the measured sum, and per prompt size the measured median time and the model's agree:</p>
 *
 * <pre>
 *   prompt tokens   files   median prompt   median output   measured   model
 *   below 2K        626     1 408           618             6.0 s      6.2 s
 *   2K to 8K        587     3 504           1 002           8.9 s      8.9 s
 *   8K to 20K       152     11 943          1 707           14.2 s     14.4 s
 *   20K to 60K      43      21 315          1 320           12.1 s     12.9 s
 *   60K and more    15      97 616          456             16.8 s     15.3 s
 * </pre>
 *
 * <p>So a big prompt stays cheap: 100K tokens add about 10 s. A local model is another matter: on a
 * CPU, prefill grows with the square of the prompt (gpt-oss-20b on a Ryzen 7 5800H took 978 s for
 * 24K tokens) and decode is far slower. {@code Calibrate} measures a model's own throughput, and its
 * {@code calibration} then replaces this estimate (see {@link #estimateSecondsCalibrated}).</p>
 */
@ToString
public class GenerationTimeEstimator {

    /** Fixed milliseconds of one call, whatever its size: the latency until the answer starts. */
    public static final double REQUEST_OVERHEAD_MS = 2100.0d;

    /** Milliseconds per prompt token (see class Javadoc). */
    public static final double PREFILL_MS_PER_TOKEN = 0.105d;

    /** Milliseconds per generated token (see class Javadoc). */
    public static final double DECODE_MS_PER_TOKEN = 6.45d;

    /**
     * Characters per prompt token of GPT-6 Luna's count over the measured sources (4.0 for TypeScript,
     * 3.4 to 5.0 for the Java and language corpora). Used only to estimate the token count from a character
     * count; this is deliberately distinct from the conservative {@code charsPerToken} used for input
     * trimming.
     */
    public static final double ESTIMATION_CHARS_PER_TOKEN = 4.0d;

    /**
     * Prompt tokens added on top of the source tokens: the profile's prompt, the path and the facts line
     * (about 1000 by the same regression; 500 to 1100 depending on the prompt).
     */
    public static final int PROMPT_TEMPLATE_TOKEN_OVERHEAD = 1000;

    /**
     * Display safety margin applied to the final estimate. The chars/token and template constants are
     * accurate central values (not conservative), so a +15 % margin keeps the logged ETA from
     * under-promising on the per-file variance observed in the benchmark.
     */
    public static final double ETA_SAFETY_MARGIN = 1.15d;

    /**
     * Number of generated output tokens assumed for the decode part of the estimate when the caller does
     * not specify one: the mean of the measured summaries (median 847, mean 959).
     */
    public static final int DEFAULT_EXPECTED_OUTPUT_TOKENS = 950;

    /** Milliseconds per second, for converting the internal millisecond model to seconds. */
    public static final double MILLIS_PER_SECOND = 1000.0d;

    /** Seconds per minute, for the human-readable duration formatting. */
    public static final long SECONDS_PER_MINUTE = 60L;

    /**
     * Threshold (seconds) at or above which {@link #formatDuration(long)} switches from a
     * "{@code ~N s}" rendering to a "{@code ~N min}" rendering.
     */
    public static final long MINUTE_FORMAT_THRESHOLD_SECONDS = 90L;

    /** Creates a new {@link GenerationTimeEstimator}. */
    public GenerationTimeEstimator() {
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
     * Estimates the time until the answer starts for a prompt of {@code promptTokens} tokens: the
     * request's fixed latency plus the prompt.
     *
     * @param promptTokens prompt length in tokens
     * @return estimated prefill time in milliseconds
     */
    public double estimatePrefillMillis(final int promptTokens) {
        return REQUEST_OVERHEAD_MS + PREFILL_MS_PER_TOKEN * promptTokens;
    }

    /**
     * Estimates decode (answer-generation) time for the expected output token count.
     *
     * @param expectedOutputTokens number of tokens the model is expected to generate
     * @return estimated decode time in milliseconds
     */
    public double estimateDecodeMillis(final int expectedOutputTokens) {
        return DECODE_MS_PER_TOKEN * expectedOutputTokens;
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
        final double millis = estimatePrefillMillis(promptTokens) + estimateDecodeMillis(expectedOutputTokens);
        return Math.round(millis / MILLIS_PER_SECOND * ETA_SAFETY_MARGIN);
    }

    /**
     * Calibrated estimate assuming {@link #DEFAULT_EXPECTED_OUTPUT_TOKENS} of output. Delegates to
     * {@link #estimateSecondsCalibrated(int, int, double, double, double)}.
     *
     * @param sourceChars             number of source characters that will be sent to the model
     * @param prefillTokensPerSecond  measured prefill throughput, or {@code <= 0} to fall back
     * @param decodeTokensPerSecond   measured decode throughput, or {@code <= 0} to fall back
     * @param calibratedCharsPerToken measured characters per token, or {@code <= 0} to use the default
     * @return estimated total time in seconds, rounded to the nearest second
     */
    public long estimateSecondsCalibrated(
            final int sourceChars,
            final double prefillTokensPerSecond,
            final double decodeTokensPerSecond,
            final double calibratedCharsPerToken) {
        return estimateSecondsCalibrated(
                sourceChars,
                DEFAULT_EXPECTED_OUTPUT_TOKENS,
                prefillTokensPerSecond,
                decodeTokensPerSecond,
                calibratedCharsPerToken);
    }

    /**
     * Estimates total generation time (seconds) using measured per-machine calibration when available.
     *
     * <p>When both {@code prefillTokensPerSecond} and {@code decodeTokensPerSecond} are {@code > 0}, uses
     * the linear model {@code promptTokens / prefillTps + outputTokens / decodeTps} (with
     * {@code calibratedCharsPerToken} for the token estimate when {@code > 0}); otherwise falls back to the
     * built-in GPT-6 Luna model in {@link #estimateSeconds(int, int)}.</p>
     *
     * @param sourceChars              number of source characters that will be sent to the model
     * @param expectedOutputTokens     number of tokens the model is expected to generate
     * @param prefillTokensPerSecond   measured prefill throughput, or {@code <= 0} to fall back
     * @param decodeTokensPerSecond    measured decode throughput, or {@code <= 0} to fall back
     * @param calibratedCharsPerToken  measured characters per token, or {@code <= 0} to use the default
     * @return estimated total time in seconds, rounded to the nearest second
     */
    public long estimateSecondsCalibrated(
            final int sourceChars,
            final int expectedOutputTokens,
            final double prefillTokensPerSecond,
            final double decodeTokensPerSecond,
            final double calibratedCharsPerToken) {
        if (prefillTokensPerSecond <= 0.0d || decodeTokensPerSecond <= 0.0d) {
            return estimateSeconds(sourceChars, expectedOutputTokens);
        }
        final double charsPerToken =
                calibratedCharsPerToken > 0.0d ? calibratedCharsPerToken : ESTIMATION_CHARS_PER_TOKEN;
        final int promptTokens = (int) Math.round(sourceChars / charsPerToken) + PROMPT_TEMPLATE_TOKEN_OVERHEAD;
        final double seconds = promptTokens / prefillTokensPerSecond + expectedOutputTokens / decodeTokensPerSecond;
        return Math.round(seconds * ETA_SAFETY_MARGIN);
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

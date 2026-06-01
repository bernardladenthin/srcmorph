// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Immutable configuration for the llama.cpp JNI provider. */
@ConvertToRecord
public class LlamaCppJniConfig {
    private final String libraryPath;
    private final String modelPath;
    private final int contextSize;
    private final int maxOutputTokens;
    private final float temperature;
    private final int threads;
    private final float topP;
    private final int topK;
    private final float repeatPenalty;
    private final boolean chatTemplateEnableThinking;
    private final List<String> stopStrings;

    /**
     * Creates a new {@link LlamaCppJniConfig}.
     *
     * @param libraryPath                 native library path; may be {@code null}
     * @param modelPath                   path to the GGUF model file
     * @param contextSize                 context window size in tokens
     * @param maxOutputTokens             maximum number of output tokens per call
     * @param temperature                 sampling temperature
     * @param threads                     number of CPU threads
     * @param topP                        nucleus-sampling probability threshold
     * @param topK                        top-k sampling limit
     * @param repeatPenalty               repetition penalty
     * @param chatTemplateEnableThinking  whether chat-template thinking mode is enabled
     * @param stopStrings                 stop strings; may be {@code null} (treated as empty)
     */
    public LlamaCppJniConfig(
            String libraryPath,
            String modelPath,
            int contextSize,
            int maxOutputTokens,
            float temperature,
            int threads,
            float topP,
            int topK,
            float repeatPenalty,
            boolean chatTemplateEnableThinking,
            List<String> stopStrings) {
        Objects.requireNonNull(modelPath, "modelPath");
        this.libraryPath = libraryPath;
        this.modelPath = modelPath;
        this.contextSize = contextSize;
        this.maxOutputTokens = maxOutputTokens;
        this.temperature = temperature;
        this.threads = threads;
        this.topP = topP;
        this.topK = topK;
        this.repeatPenalty = repeatPenalty;
        this.chatTemplateEnableThinking = chatTemplateEnableThinking;
        this.stopStrings = stopStrings != null ? stopStrings : Collections.emptyList();
    }

    /**
     * Returns the native library path.
     *
     * @return native library path, or {@code null} to use the bundled library
     */
    public String libraryPath() {
        return libraryPath;
    }

    /**
     * Returns the GGUF model file path.
     *
     * @return model file path
     */
    public String modelPath() {
        return modelPath;
    }

    /**
     * Returns the context window size in tokens.
     *
     * @return context window size
     */
    public int contextSize() {
        return contextSize;
    }

    /**
     * Returns the maximum number of output tokens per call.
     *
     * @return maximum output tokens
     */
    public int maxOutputTokens() {
        return maxOutputTokens;
    }

    /**
     * Returns the sampling temperature.
     *
     * @return sampling temperature
     */
    public float temperature() {
        return temperature;
    }

    /**
     * Returns the number of CPU threads.
     *
     * @return number of CPU threads
     */
    public int threads() {
        return threads;
    }

    /**
     * Returns the nucleus-sampling probability threshold.
     *
     * @return top-p value
     */
    public float topP() {
        return topP;
    }

    /**
     * Returns the top-k sampling limit.
     *
     * @return top-k value
     */
    public int topK() {
        return topK;
    }

    /**
     * Returns the repetition penalty.
     *
     * @return repeat penalty
     */
    public float repeatPenalty() {
        return repeatPenalty;
    }

    /**
     * Returns whether chat-template thinking mode is enabled.
     *
     * @return {@code true} when chat-template thinking mode is enabled
     */
    public boolean chatTemplateEnableThinking() {
        return chatTemplateEnableThinking;
    }

    /**
     * Returns an unmodifiable view of the configured stop strings.
     *
     * @return unmodifiable list of stop strings
     */
    public List<String> stopStrings() {
        return Collections.unmodifiableList(stopStrings);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        LlamaCppJniConfig that = (LlamaCppJniConfig) obj;
        return Objects.equals(this.libraryPath, that.libraryPath)
                && Objects.equals(this.modelPath, that.modelPath)
                && this.contextSize == that.contextSize
                && this.maxOutputTokens == that.maxOutputTokens
                && Float.floatToIntBits(this.temperature) == Float.floatToIntBits(that.temperature)
                && this.threads == that.threads
                && Float.floatToIntBits(this.topP) == Float.floatToIntBits(that.topP)
                && this.topK == that.topK
                && Float.floatToIntBits(this.repeatPenalty) == Float.floatToIntBits(that.repeatPenalty)
                && this.chatTemplateEnableThinking == that.chatTemplateEnableThinking
                && Objects.equals(this.stopStrings, that.stopStrings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                libraryPath,
                modelPath,
                contextSize,
                maxOutputTokens,
                temperature,
                threads,
                topP,
                topK,
                repeatPenalty,
                chatTemplateEnableThinking,
                stopStrings);
    }

    @Override
    public String toString() {
        return "LlamaCppJniConfig[" + "libraryPath="
                + libraryPath + ", " + "modelPath="
                + modelPath + ", " + "contextSize="
                + contextSize + ", " + "maxOutputTokens="
                + maxOutputTokens + ", " + "temperature="
                + temperature + ", " + "threads="
                + threads + ", " + "topP="
                + topP + ", " + "topK="
                + topK + ", " + "repeatPenalty="
                + repeatPenalty + ", " + "chatTemplateEnableThinking="
                + chatTemplateEnableThinking + ", " + "stopStrings="
                + stopStrings + ']';
    }
}

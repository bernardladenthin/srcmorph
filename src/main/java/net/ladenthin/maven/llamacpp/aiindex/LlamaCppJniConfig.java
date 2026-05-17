// @formatter:off

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public LlamaCppJniConfig(String libraryPath, String modelPath, int contextSize, int maxOutputTokens,
            float temperature, int threads, float topP, int topK, float repeatPenalty,
            boolean chatTemplateEnableThinking, List<String> stopStrings) {
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

    public String libraryPath() {
        return libraryPath;
    }

    public String modelPath() {
        return modelPath;
    }

    public int contextSize() {
        return contextSize;
    }

    public int maxOutputTokens() {
        return maxOutputTokens;
    }

    public float temperature() {
        return temperature;
    }

    public int threads() {
        return threads;
    }

    public float topP() {
        return topP;
    }

    public int topK() {
        return topK;
    }

    public float repeatPenalty() {
        return repeatPenalty;
    }

    public boolean chatTemplateEnableThinking() {
        return chatTemplateEnableThinking;
    }

    public List<String> stopStrings() {
        return stopStrings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        LlamaCppJniConfig that = (LlamaCppJniConfig) obj;
        return Objects.equals(this.libraryPath, that.libraryPath) &&
                Objects.equals(this.modelPath, that.modelPath) &&
                this.contextSize == that.contextSize &&
                this.maxOutputTokens == that.maxOutputTokens &&
                Float.floatToIntBits(this.temperature) == Float.floatToIntBits(that.temperature) &&
                this.threads == that.threads &&
                Float.floatToIntBits(this.topP) == Float.floatToIntBits(that.topP) &&
                this.topK == that.topK &&
                Float.floatToIntBits(this.repeatPenalty) == Float.floatToIntBits(that.repeatPenalty) &&
                this.chatTemplateEnableThinking == that.chatTemplateEnableThinking &&
                Objects.equals(this.stopStrings, that.stopStrings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads,
                topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings);
    }

    @Override
    public String toString() {
        return "LlamaCppJniConfig[" +
                "libraryPath=" + libraryPath + ", " +
                "modelPath=" + modelPath + ", " +
                "contextSize=" + contextSize + ", " +
                "maxOutputTokens=" + maxOutputTokens + ", " +
                "temperature=" + temperature + ", " +
                "threads=" + threads + ", " +
                "topP=" + topP + ", " +
                "topK=" + topK + ", " +
                "repeatPenalty=" + repeatPenalty + ", " +
                "chatTemplateEnableThinking=" + chatTemplateEnableThinking + ", " +
                "stopStrings=" + stopStrings + ']';
    }

}

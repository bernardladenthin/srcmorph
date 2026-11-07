// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.ModelDefinition;
import org.jspecify.annotations.Nullable;

/**
 * Pure mapping from a resolved {@link ModelDefinition} to an immutable {@link ProviderConfig}.
 *
 * <p>Extracted from what was {@code AbstractAiIndexMojo.buildLlamaCppJniConfig} in the
 * {@code llamacpp-ai-index-maven-plugin} module: the model lookup lives in the {@code engine}
 * package's {@code EngineSupport}, which calls the method here to do the field-by-field translation.
 * It is pure (no I/O, no lookups, no thrown exceptions), so it is fully unit-testable and the
 * project's PIT mutation-coverage target for this translation.</p>
 */
public final class ProviderConfigFactory {

    private ProviderConfigFactory() {
        // utility class — not instantiable
    }

    /**
     * Builds a {@link ProviderConfig} by copying every field from a resolved
     * {@link ModelDefinition} (an {@link net.ladenthin.srcmorph.config.ModelDefinition} looked up by
     * key). An unset list of the definition is an empty one here: no stop strings, the binding's own
     * DRY sequence breakers.
     *
     * @param config      the resolved AI model generation config
     * @return the fully populated llama.cpp configuration
     */
    public static ProviderConfig from(final ModelDefinition config) {
        final @Nullable List<String> breakers = config.getDrySequenceBreakers();
        final @Nullable List<String> stopStrings = config.getStopStrings();
        // A remote model (openai-compatible) needs no model file, so its definition may name none.
        return ProviderConfig.builder()
                .modelPath(config.getModelPath())
                .contextSize(config.getContextSize())
                .maxOutputTokens(config.getMaxOutputTokens())
                .temperature(config.getTemperature())
                .threads(config.getThreads())
                .topP(config.getTopP())
                .topK(config.getTopK())
                .minP(config.getMinP())
                .topNSigma(config.getTopNSigma())
                .repeatPenalty(config.getRepeatPenalty())
                .chatTemplateEnableThinking(config.getChatTemplateEnableThinking())
                .cachePrompt(config.isCachePrompt())
                .swaFull(config.isSwaFull())
                .cacheReuse(config.getCacheReuse())
                .gpuLayers(config.getGpuLayers())
                .seed(config.getSeed())
                .cpuMoeLayers(config.getCpuMoeLayers())
                .cpuFfnLayers(config.getCpuFfnLayers())
                .kvUnifiedPerSlot(config.getKvUnifiedPerSlot())
                .lazyMode(config.getLazyMode())
                .repeatLastN(config.getRepeatLastN())
                .cacheTypeK(config.getCacheTypeK())
                .cacheTypeV(config.getCacheTypeV())
                .flashAttn(config.isFlashAttn())
                .batchSize(config.getBatchSize())
                .ubatchSize(config.getUbatchSize())
                .threadsBatch(config.getThreadsBatch())
                .mainGpu(config.getMainGpu())
                .devices(config.getDevices())
                .provider(config.getProvider())
                .remoteModel(config.getRemoteModel())
                .baseUrl(config.getBaseUrl())
                .apiKeyEnv(config.getApiKeyEnv())
                .stream(config.isStream())
                .requestsPerMinute(config.getRequestsPerMinute())
                .maxRetries(config.getMaxRetries())
                .reasoningEffort(config.getReasoningEffort())
                .reasoningBudgetTokens(config.getReasoningBudgetTokens())
                .dryMultiplier(config.getDryMultiplier())
                .dryBase(config.getDryBase())
                .dryAllowedLength(config.getDryAllowedLength())
                .dryPenaltyLastN(config.getDryPenaltyLastN())
                .drySequenceBreakers(breakers != null ? breakers : Collections.<String>emptyList())
                .stopStrings(stopStrings != null ? stopStrings : Collections.<String>emptyList())
                .build();
    }
}

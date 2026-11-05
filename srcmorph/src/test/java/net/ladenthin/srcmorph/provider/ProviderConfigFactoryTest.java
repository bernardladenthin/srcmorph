// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import org.junit.jupiter.api.Test;

public class ProviderConfigFactoryTest {

    /**
     * Every field set to a distinct, distinguishable value so a mutant that swaps two getters (or drops
     * an argument) in {@link ProviderConfigFactory#fromGenerationConfig} is killed.
     */
    private static AiGenerationConfig fullConfig() {
        final AiGenerationConfig config = new AiGenerationConfig();
        config.setModelPath("model.gguf");
        config.setContextSize(1111);
        config.setMaxOutputTokens(222);
        config.setTemperature(0.33f);
        config.setThreads(7);
        config.setTopP(0.55f);
        config.setTopK(66);
        config.setMinP(0.07f);
        config.setTopNSigma(0.88f);
        config.setRepeatPenalty(1.09f);
        config.setChatTemplateEnableThinking(false);
        config.setCachePrompt(false);
        config.setSwaFull(false);
        config.setCacheReuse(101);
        config.setGpuLayers(12);
        config.setSeed(12345);
        config.setCpuMoeLayers(24);
        config.setCpuFfnLayers(16);
        config.setKvUnifiedPerSlot(4096);
        config.setLazyMode("on");
        config.setRepeatLastN(128);
        config.setCacheTypeK("q8_0");
        config.setCacheTypeV("q4_0");
        config.setFlashAttn(true);
        config.setBatchSize(512);
        config.setUbatchSize(256);
        config.setThreadsBatch(6);
        config.setMainGpu(3);
        config.setDevices("Vulkan1");
        config.setProvider("openai-compatible");
        config.setRemoteModel("openai/gpt-6-luna");
        config.setBaseUrl("http://127.0.0.1:8080/v1");
        config.setApiKeyEnv("MY_KEY");
        config.setStream(true);
        config.setRequestsPerMinute(25);
        config.setMaxRetries(5);
        config.setReasoningEffort("high");
        config.setReasoningBudgetTokens(512);
        config.setDryMultiplier(0.5f);
        config.setDryBase(1.3f);
        config.setDryAllowedLength(7);
        config.setDryPenaltyLastN(999);
        config.setDrySequenceBreakers(Arrays.asList("\n", "."));
        config.setStopStrings(Arrays.asList("<end>"));
        return config;
    }

    @Test
    public void fromGenerationConfig_threadsEveryFieldThrough() {
        final ProviderConfig result = ProviderConfigFactory.fromGenerationConfig(fullConfig());

        assertThat(result.modelPath(), is("model.gguf"));
        assertThat(result.contextSize(), is(1111));
        assertThat(result.maxOutputTokens(), is(222));
        assertThat(result.temperature(), is(0.33f));
        assertThat(result.threads(), is(7));
        assertThat(result.topP(), is(0.55f));
        assertThat(result.topK(), is(66));
        assertThat(result.minP(), is(0.07f));
        assertThat(result.topNSigma(), is(0.88f));
        assertThat(result.repeatPenalty(), is(1.09f));
        assertThat(result.chatTemplateEnableThinking(), is(false));
        assertThat(result.cachePrompt(), is(false));
        assertThat(result.swaFull(), is(false));
        assertThat(result.cacheReuse(), is(101));
        assertThat(result.gpuLayers(), is(12));
        assertThat(result.seed(), is(12345));
        assertThat(result.cpuMoeLayers(), is(24));
        assertThat(result.cpuFfnLayers(), is(16));
        assertThat(result.kvUnifiedPerSlot(), is(4096));
        assertThat(result.lazyMode(), is("on"));
        assertThat(result.repeatLastN(), is(128));
        assertThat(result.cacheTypeK(), is("q8_0"));
        assertThat(result.cacheTypeV(), is("q4_0"));
        // Distinct from swaFull above (false), so a swap of the two adjacent booleans is caught.
        assertThat(result.flashAttn(), is(true));
        assertThat(result.batchSize(), is(512));
        assertThat(result.ubatchSize(), is(256));
        assertThat(result.threadsBatch(), is(6));
        assertThat(result.mainGpu(), is(3));
        assertThat(result.devices(), is("Vulkan1"));
        assertThat(result.provider(), is("openai-compatible"));
        assertThat(result.remoteModel(), is("openai/gpt-6-luna"));
        assertThat(result.baseUrl(), is("http://127.0.0.1:8080/v1"));
        assertThat(result.apiKeyEnv(), is("MY_KEY"));
        assertThat(result.stream(), is(true));
        assertThat(result.requestsPerMinute(), is(25));
        assertThat(result.maxRetries(), is(5));
        assertThat(result.reasoningEffort(), is("high"));
        assertThat(result.reasoningBudgetTokens(), is(512));
        assertThat(result.dryMultiplier(), is(0.5f));
        assertThat(result.dryBase(), is(1.3f));
        assertThat(result.dryAllowedLength(), is(7));
        assertThat(result.dryPenaltyLastN(), is(999));
        assertThat(result.drySequenceBreakers(), hasItem("\n"));
        assertThat(result.drySequenceBreakers(), hasItem("."));
        assertThat(result.stopStrings(), hasItem("<end>"));
    }

    @Test
    public void fromGenerationConfig_listsNullifiedThroughTheSetters_arriveAsEmptyLists() {
        // Note what this does and does not pin: the setters normalise null to an empty list, so the
        // factory never sees a null here. It proves the empty list survives the mapping, NOT that the
        // factory itself guards against null -- that guard lives in ProviderConfig's constructor
        // and is pinned by ProviderConfigTest.
        final AiGenerationConfig config = fullConfig();
        config.setStopStrings(null);
        config.setDrySequenceBreakers(null);

        final ProviderConfig result = ProviderConfigFactory.fromGenerationConfig(config);

        assertThat(result.stopStrings(), is(Collections.<String>emptyList()));
        assertThat(result.drySequenceBreakers(), is(Collections.<String>emptyList()));
    }

    @Test
    public void fromGenerationConfig_noModelPath_hasNone() {
        // arrange: a remote (openai-compatible) definition names no model file
        final AiGenerationConfig config = new AiGenerationConfig();
        config.setModelPath(null);
        config.setProvider("openai-compatible");

        // act
        final ProviderConfig result = ProviderConfigFactory.fromGenerationConfig(config);

        // assert
        assertThat(result.modelPath(), is(nullValue()));
        assertThat(result.provider(), is("openai-compatible"));
    }
}

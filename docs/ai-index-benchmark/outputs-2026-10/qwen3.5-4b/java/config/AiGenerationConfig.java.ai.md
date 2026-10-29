### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:02:03Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for LLM inference including model paths, sampling strategies, KV cache optimization, and hardware offloading.

#### Purpose
- Stores mutable configuration for a single AI generation step including model paths, sampling parameters, retry policies, and input trimming limits.
- Bridges Maven configuration layer and AI provider implementations with default values for llama.cpp and OpenAI-compatible providers.

#### Type
- `public class AiGenerationConfig`
- Modifiers: `public`, `@ToString` (Lombok), `@SuppressWarnings`
- No extends/implements; no generics

#### Input
- Constructor: none (no-arg)
- Injected dependencies: none (self-contained)
- Consumed fields: 54 instance fields (modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings, calibration)
- Read resources: none

#### Output
- Return types: 54 getter methods returning primitive types, `@Nullable String`, `@Nullable Boolean`, or `List<String>`
- Produced state: 54 instance fields mutated via setters
- Written resources: none
- Side effects: none

#### Core logic
- Provides 54 static default constants (`DEFAULT_*`) matching instance field initializers for all AI generation parameters.
- Implements 54 getter/setter pairs for mutable configuration fields covering model paths, context windows, sampling (temperature, top-p, top-k, min-p, top-n-sigma, repeat penalty, dry), KV cache optimization (cache reuse, SWA full, quantization types, flash attention), hardware offloading (GPU layers, CPU MoE/FFN layers), batching, and device selection.
- Handles null safety for string fields (modelPath, lazyMode, cacheTypeK, cacheTypeV, devices, provider, remoteModel, baseUrl, apiKeyEnv) with fallback to defaults or empty collections.
- Manages tri-state boolean for chat template thinking mode (null = unset, true/false = forwarded).
- Supports dynamic defaults for threads (based on available processors), cache reuse (256 tokens), and GPU layer offloading (-1 = binding default).
- Provides unmodifiable list views for drySequenceBreakers and stopStrings via Collections.unmodifiableList.

#### Public API
- 54 public methods: 28 getters (getModelPath, getContextSize, getMaxOutputTokens, getTemperature, getThreads, getCharsPerToken, getMaxInputChars, isWarnOnTrim, getTopP, getTopK, getMinP, getTopNSigma, getChatTemplateEnableThinking, isCachePrompt, isSwaFull, getCacheReuse, getGpuLayers, getSeed, getCpuMoeLayers, getCpuFfnLayers, getKvUnifiedPerSlot, getLazyMode, getRepeatLastN, getCacheTypeK, getCacheTypeV, isFlashAttn, getBatchSize, getUbatchSize, getThreadsBatch, getMainGpu, getDevices, getProvider, getRemoteModel, getBaseUrl, getApiKeyEnv, getReasoningEffort, getReasoningBudgetTokens, getDryMultiplier, getDryBase, getDryAllowedLength, getDryPenaltyLastN, getDrySequenceBreakers, getStopStrings, getCalibration) and 26 setters (setModelPath, setContextSize, setMaxOutputTokens, setTemperature, setThreads, setCharsPerToken, setMaxInputChars, setWarnOnTrim, setTopP, setTopK, setMinP, setTopNSigma, setChatTemplateEnableThinking, setCachePrompt, setSwaFull, setCacheReuse, setGpuLayers, setSeed, setCpuMoeLayers, setCpuFfnLayers, setKvUnifiedPerSlot, setLazyMode, setRepeatLastN, setCacheTypeK, setCacheTypeV, setFlashAttn, setBatchSize, setUbatchSize, setThreadsBatch, setMainGpu, setDevices, setProvider, setRemoteModel, setBaseUrl, setApiKeyEnv, setReasoningEffort, setReasoningBudgetTokens, setDryMultiplier, setDryBase, setDryAllowedLength, setDryPenaltyLastN, setDrySequenceBreakers, setStopStrings, setCalibration).
- Total: 54 public methods (28 getters + 26 setters)

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`
- `java.lang.Runtime`
- `java.lang.Math`

#### Exceptions / Errors
- No exceptions declared or thrown.
- Null handling: setters accept `@Nullable String` and `@Nullable List<String>` with null-safe defaults; `@Nullable Boolean` for chat template thinking mode.
- Error conditions: none explicitly handled; validation occurs in binding layer (e.g., negative repeatLastN rejected by binding).

#### Concurrency
- Mutable state managed via setters; no thread-safety guarantees.
- No synchronization or immutability; instances are JavaBeans populated by Maven plugin framework.

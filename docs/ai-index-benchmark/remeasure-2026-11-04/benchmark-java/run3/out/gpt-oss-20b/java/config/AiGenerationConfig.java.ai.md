### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:44Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, from model path and context size to sampling, GPU, cache, and provider settings.

#### Purpose
- Stores configuration for a single AI generation step.
- Exposes getters/setters for Maven plugin and provider layers.

#### Type
`public class AiGenerationConfig` – mutable JavaBean, Lombok `@ToString`, no other annotations.

#### Input
- Constructor: no arguments, defaults applied.
- Setters for all fields (modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings, calibration).

#### Output
- Getters return current values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` (Lombok) prints all fields for diagnostics.

#### Core logic
- Default constants defined for every parameter.
- Setters validate nulls by resetting to defaults.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace null with default.
- List setters replace null with empty list.
- No additional computation; values are passed through to provider.

#### Public API
- `getModelPath() -> String?` – model file path.
- `setModelPath(String?)` – set model path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int)` – set context size.
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int)` – set max output tokens.
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float)` – set temperature.
- `getThreads() -> int` – CPU threads.
- `setThreads(int)` – set threads.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int)` – set chars per token.
- `getMaxInputChars() -> int` – max input chars.
- `setMaxInputChars(int)` – set max input chars.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean)` – set trim warning.
- `getTopP() -> float` – nucleus sampling threshold.
- `setTopP(float)` – set top‑p.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int)` – set top‑k.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float)` – set repeat penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float)` – set min‑p.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float)` – set top‑n‑sigma.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?)` – set thinking mode.
- `isCachePrompt() -> boolean` – prompt‑caching flag.
- `isSwaFull() -> boolean` – full‑SWA flag.
- `setSwaFull(boolean)` – set full‑SWA.
- `getCacheReuse() -> int` – cache‑reuse chunk size.
- `setCacheReuse(int)` – set cache‑reuse.
- `getGpuLayers() -> int` – GPU offload layers.
- `setGpuLayers(int)` – set GPU layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int)` – set seed.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int)` – set CPU MoE layers.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int)` – set CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV cap.
- `setKvUnifiedPerSlot(int)` – set KV cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?)` – set lazy mode.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int)` – set repeat‑penalty window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?)` – set cache type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?)` – set cache type V.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean)` – set Flash Attention.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int)` – set batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int)` – set physical batch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int)` – set batch threads.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int)` – set main GPU.
- `getDevices() -> String` – device list.
- `setDevices(String)` – set devices.
- `getProvider() -> String` – AI provider.
- `setProvider(String?)` – set provider.
- `getRemoteModel() -> String` – remote model id.
- `setRemoteModel(String?)` – set remote model.
- `getBaseUrl() -> String` – base URL.
- `setBaseUrl(String?)` – set base URL.
- `getApiKeyEnv() -> String` – API key env var name.
- `setApiKeyEnv(String?)` – set API key env.
- `getReasoningEffort() -> String` – reasoning effort level.
- `setReasoningEffort(String)` – set reasoning effort.
- `getReasoningBudgetTokens() -> int` – reasoning budget.
- `setReasoningBudgetTokens(int)` – set reasoning budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float)` – set DRY multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float)` – set DRY base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int)` – set DRY allowed length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int)` – set DRY penalty window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable breakers list.
- `setDrySequenceBreakers(List<String>?)` – set breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings.
- `setStopStrings(List<String>?)` – set stop strings.
- `getCalibration() -> AiCalibration?` – calibration object.
- `setCalibration(AiCalibration?)` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null arguments are replaced with defaults; no `NullPointerException`.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances are intended for single‑thread use by Maven plugin.

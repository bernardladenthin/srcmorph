### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:51Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider APIs.

#### Purpose
- Stores all AI generation settings for a single model run.
- Provides getters/setters for Maven plugin configuration.

#### Type
`public class AiGenerationConfig` – mutable JavaBean, Lombok `@ToString`, no other annotations.

#### Input
- Constructor: no arguments.
- Setters receive primitive values, `String`, `List<String>`, or `@Nullable` objects.
- `setCalibration(AiCalibration)` accepts a calibration object.

#### Output
- Getters return current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `setCachePrompt(boolean)` mutates internal flag.

#### Core logic
- Default constants define sensible AI generation defaults.
- Setters validate nulls by resetting to defaults when `null` is passed.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace `null` with empty defaults.
- `setDevices` and `setProvider` treat `null` as default empty string.
- `setCalibration` stores optional calibration.

#### Public API
- `getModelPath()`/`setModelPath(String)` – model file path.
- `getContextSize()`/`setContextSize(int)` – context window size.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` – max output tokens.
- `getTemperature()`/`setTemperature(float)` – sampling temperature.
- `getThreads()`/`setThreads(int)` – CPU thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` – chars‑per‑token ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` – max input characters.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` – trim warning flag.
- `getTopP()`/`setTopP(float)` – nucleus‑sampling threshold.
- `getTopK()`/`setTopK(int)` – top‑k limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` – repetition penalty.
- `getMinP()`/`setMinP(float)` – min‑p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` – top‑n‑sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` – chat‑template thinking mode.
- `isCachePrompt()` – prompt‑caching flag.
- `isSwaFull()`/`setSwaFull(boolean)` – full‑SWA KV cache flag.
- `getCacheReuse()`/`setCacheReuse(int)` – KV prefix‑reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` – GPU layer offload count.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` – CPU MoE layers.
- `getSeed()`/`setSeed(int)` – RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` – CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` – unified KV context cap.
- `getLazyMode()`/`setLazyMode(String)` – tensor‑read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` – repeat‑penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` – KV cache type K.
- `getCacheTypeV()`/`setCacheTypeV(String)` – KV cache type V.
- `isFlashAttn()`/`setFlashAttn(boolean)` – Flash Attention flag.
- `getBatchSize()`/`setBatchSize(int)` – logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` – physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` – batch thread count.
- `getMainGpu()`/`setMainGpu(int)` – primary GPU index.
- `getDevices()`/`setDevices(String)` – device list.
- `getProvider()`/`setProvider(String)` – AI provider name.
- `getRemoteModel()`/`setRemoteModel(String)` – remote model ID.
- `getBaseUrl()`/`setBaseUrl(String)` – base URL for OpenAI‑compatible API.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` – environment variable for API key.
- `setCachePrompt(boolean)` – enable/disable prompt caching.
- `getReasoningEffort()`/`setReasoningEffort(String)` – reasoning‑effort level.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` – reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` – DRY multiplier.
- `getDryBase()`/`setDryBase(float)` – DRY base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` – DRY allowed length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` – DRY penalty window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` – DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` – stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` – per‑machine timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null arguments are handled by resetting to default values.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

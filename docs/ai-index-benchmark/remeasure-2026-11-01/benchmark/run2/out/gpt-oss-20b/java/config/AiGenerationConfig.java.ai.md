### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:03Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider APIs.

#### Purpose
- Stores AI generation settings (model path, sampling, caching, GPU, API details).
- Provides getters/setters for Maven plugin configuration.

#### Type
`public class AiGenerationConfig` – class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments.
- Setters receive primitive or object values for each configuration field.
- `setDrySequenceBreakers`, `setStopStrings` accept `List<String>` (nullable).
- `setCalibration` accepts `AiCalibration` (nullable).

#### Output
- Getters return current field values.
- `getDrySequenceBreakers`, `getStopStrings` return unmodifiable lists.
- `setCachePrompt` modifies internal flag.

#### Core logic
- Default constants define standard AI generation parameters.
- Setters enforce non‑null defaults for optional string fields.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace null with empty defaults.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` reset to defaults when null.
- `setCachePrompt` directly updates flag.
- No additional computation; class is a plain data holder.

#### Public API
- `getModelPath()` / `setModelPath(String)` – model file path.
- `getContextSize()` / `setContextSize(int)` – context window size.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` – max output tokens.
- `getTemperature()` / `setTemperature(float)` – sampling temperature.
- `getThreads()` / `setThreads(int)` – CPU thread count.
- `getCharsPerToken()` / `setCharsPerToken(int)` – chars‑per‑token ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` – max input characters.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` – trim warning flag.
- `getTopP()` / `setTopP(float)` – nucleus‑sampling threshold.
- `getTopK()` / `setTopK(int)` – top‑k limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` – repetition penalty.
- `getMinP()` / `setMinP(float)` – min‑p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` – top‑n‑sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` – chat‑template thinking mode.
- `isCachePrompt()` – prompt‑caching flag.
- `isSwaFull()` / `setSwaFull(boolean)` – full‑SWA KV cache flag.
- `getCacheReuse()` / `setCacheReuse(int)` – KV prefix‑reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` – GPU layer offload count.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` – CPU MoE layers.
- `getSeed()` / `setSeed(int)` – RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` – CPU FFN layers.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` – unified KV context cap.
- `getLazyMode()` / `setLazyMode(String)` – tensor‑read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` – repeat‑penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` – KV cache type K.
- `getCacheTypeV()` / `setCacheTypeV(String)` – KV cache type V.
- `isFlashAttn()` / `setFlashAttn(boolean)` – Flash Attention flag.
- `getBatchSize()` / `setBatchSize(int)` – logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` – physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` – batch thread count.
- `getMainGpu()` / `setMainGpu(int)` – primary GPU index.
- `getDevices()` / `setDevices(String)` – device list.
- `getProvider()` / `setProvider(String)` – AI provider.
- `getRemoteModel()` / `setRemoteModel(String)` – remote model ID.
- `getBaseUrl()` / `setBaseUrl(String)` – API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` – API key environment variable.
- `setCachePrompt(boolean)` – enable/disable prompt caching.
- `getReasoningEffort()` / `setReasoningEffort(String)` – reasoning‑effort level.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` – reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` – DRY multiplier.
- `getDryBase()` / `setDryBase(float)` – DRY base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` – DRY allowed length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` – DRY penalty window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` – DRY sequence breakers.
- `getStopStrings()` / `setStopStrings(List<String>)` – stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` – per‑machine timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: setters replace null with defaults; getters may return null for optional fields.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances are intended for single‑threaded configuration building.

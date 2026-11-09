### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:01Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, exposing getters/setters for configuration values used by the Maven plugin and AI provider implementations.

#### Purpose
- Stores AI generation settings (model path, sampling, prompt trimming, cache, GPU, API, etc.).
- Provides defaults for each parameter.
- Supplies Lombok‑generated `toString` for diagnostics.

#### Type
- `public class AiGenerationConfig` (final, mutable JavaBean, Lombok `@ToString`).
- Extends `Object`; no interfaces or annotations beyond `@SuppressWarnings` and `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for:
  - `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`, `calibration`.

#### Output
- Getter methods return current field values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `setCachePrompt(boolean)` mutates `cachePrompt`.
- `setCalibration(AiCalibration)` stores calibration.

#### Core logic
- All fields initialized to static defaults.
- Setters perform minimal validation (e.g., non‑null defaults, empty string handling).
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace `null` with default constants.
- `setDevices` treats `null` as empty default.
- `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace `null` with respective defaults.
- `setDrySequenceBreakers` and `setStopStrings` replace `null` with empty list.

#### Public API
- `getModelPath() -> String?` – model file path
- `setModelPath(String?) -> void` – set model path
- `getContextSize() -> int` – context window size
- `setContextSize(int) -> void`
- `getMaxOutputTokens() -> int` – max output tokens
- `setMaxOutputTokens(int) -> void`
- `getTemperature() -> float` – sampling temperature
- `setTemperature(float) -> void`
- `getThreads() -> int` – CPU threads
- `setThreads(int) -> void`
- `getCharsPerToken() -> int` – chars per token
- `setCharsPerToken(int) -> void`
- `getMaxInputChars() -> int` – max input chars
- `setMaxInputChars(int) -> void`
- `isWarnOnTrim() -> boolean` – trim warning flag
- `setWarnOnTrim(boolean) -> void`
- `getTopP() -> float` – nucleus threshold
- `setTopP(float) -> void`
- `getTopK() -> int` – top‑k limit
- `setTopK(int) -> void`
- `getRepeatPenalty() -> float` – repetition penalty
- `setRepeatPenalty(float) -> void`
- `getMinP() -> float` – min‑p threshold
- `setMinP(float) -> void`
- `getTopNSigma() -> float` – top‑n‑sigma threshold
- `setTopNSigma(float) -> void`
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode
- `setChatTemplateEnableThinking(Boolean?) -> void`
- `isCachePrompt() -> boolean` – prompt‑caching flag
- `isSwaFull() -> boolean` – full‑SWA flag
- `setSwaFull(boolean) -> void`
- `getCacheReuse() -> int` – cache‑reuse chunk size
- `setCacheReuse(int) -> void`
- `getGpuLayers() -> int` – GPU layers offload
- `setGpuLayers(int) -> void`
- `getSeed() -> int` – RNG seed
- `setSeed(int) -> void`
- `getCpuMoeLayers() -> int` – CPU MoE layers
- `setCpuMoeLayers(int) -> void`
- `getCpuFfnLayers() -> int` – CPU FFN layers
- `setCpuFfnLayers(int) -> void`
- `getKvUnifiedPerSlot() -> int` – KV unified per‑slot cap
- `setKvUnifiedPerSlot(int) -> void`
- `getLazyMode() -> String` – tensor‑read laziness
- `setLazyMode(String?) -> void`
- `getRepeatLastN() -> int` – repeat‑penalty window
- `setRepeatLastN(int) -> void`
- `getCacheTypeK() -> String` – KV cache type K
- `setCacheTypeK(String?) -> void`
- `getCacheTypeV() -> String` – KV cache type V
- `setCacheTypeV(String?) -> void`
- `isFlashAttn() -> boolean` – Flash Attention flag
- `setFlashAttn(boolean) -> void`
- `getBatchSize() -> int` – logical batch size
- `setBatchSize(int) -> void`
- `getUbatchSize() -> int` – physical batch size
- `setUbatchSize(int) -> void`
- `getThreadsBatch() -> int` – batch thread count
- `setThreadsBatch(int) -> void`
- `getMainGpu() -> int` – primary GPU index
- `setMainGpu(int) -> void`
- `getDevices() -> String` – device list
- `setDevices(String) -> void`
- `getProvider() -> String` – AI provider
- `setProvider(String) -> void`
- `getRemoteModel() -> String` – remote model id
- `setRemoteModel(String) -> void`
- `getBaseUrl() -> String` – API base URL
- `setBaseUrl(String) -> void`
- `getApiKeyEnv() -> String` – API key env var
- `setApiKeyEnv(String) -> void`
- `setCachePrompt(boolean) -> void`
- `getReasoningEffort() -> String` – reasoning effort level
- `setReasoningEffort(String) -> void`
- `getReasoningBudgetTokens() -> int` – reasoning budget
- `setReasoningBudgetTokens(int) -> void`
- `getDryMultiplier() -> float` – DRY multiplier
- `setDryMultiplier(float) -> void`
- `getDryBase() -> float` – DRY base
- `setDryBase(float) -> void`
- `getDryAllowedLength() -> int` – DRY allowed length
- `setDryAllowedLength(int) -> void`
- `getDryPenaltyLastN() -> int` – DRY penalty window
- `setDryPenaltyLastN(int) -> void`
- `getDrySequenceBreakers() -> List<String>` – unmodifiable list
- `setDrySequenceBreakers(List<String>?) -> void`
- `getStopStrings() -> List<String>` – unmodifiable list
- `setStopStrings(List<String>?) -> void`
- `getCalibration() -> AiCalibration?` – calibration
- `setCalibration(AiCalibration?) -> void`

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept `null` and replace with defaults.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

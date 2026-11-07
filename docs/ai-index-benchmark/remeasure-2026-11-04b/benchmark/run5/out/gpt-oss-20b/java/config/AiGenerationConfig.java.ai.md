### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:37Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable JavaBean holding all AI generation parameters for a single model run, used by the Maven plugin to configure the AI provider.

#### Purpose
- Stores configuration for AI generation (model path, sampling, prompt trimming, cache, GPU, API, etc.).
- Exposes getters/setters for Maven plugin binding.

#### Type
- `public class AiGenerationConfig` (class, public, mutable, Lombok `@ToString`).

#### Input
- Constructor: no arguments.
- Fields set via setters or Maven plugin injection:
  - `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`, `calibration`.

#### Output
- Getters return current field values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- No state mutation beyond setters.

#### Core logic
- No complex algorithms; only field assignments and basic null‑coalescing in setters.
- Default constants provide initial values for all fields.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace `null` with default constants.
- `getDrySequenceBreakers()`/`getStopStrings()` wrap internal lists with `Collections.unmodifiableList`.

#### Public API
- `getModelPath() -> String?` – model file path.
- `setModelPath(String?) -> void` – set model path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int) -> void`
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int) -> void`
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float) -> void`
- `getThreads() -> int` – CPU threads.
- `setThreads(int) -> void`
- `getCharsPerToken() -> int` – chars per token.
- `setCharsPerToken(int) -> void`
- `getMaxInputChars() -> int` – max input chars.
- `setMaxInputChars(int) -> void`
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean) -> void`
- `getTopP() -> float` – nucleus sampling.
- `setTopP(float) -> void`
- `getTopK() -> int` – top‑k limit.
- `setTopK(int) -> void`
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float) -> void`
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float) -> void`
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float) -> void`
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?) -> void`
- `isCachePrompt() -> boolean` – prompt cache flag.
- `isSwaFull() -> boolean` – full SWA cache flag.
- `setSwaFull(boolean) -> void`
- `getCacheReuse() -> int` – cache‑reuse chunk size.
- `setCacheReuse(int) -> void`
- `getGpuLayers() -> int` – GPU layers offload.
- `setGpuLayers(int) -> void`
- `getSeed() -> int` – RNG seed.
- `setSeed(int) -> void`
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int) -> void`
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int) -> void`
- `getKvUnifiedPerSlot() -> int` – KV unified per‑slot cap.
- `setKvUnifiedPerSlot(int) -> void`
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?) -> void`
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int) -> void`
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?) -> void`
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?) -> void`
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean) -> void`
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int) -> void`
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int) -> void`
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int) -> void`
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int) -> void`
- `getDevices() -> String` – device list.
- `setDevices(String) -> void`
- `getProvider() -> String` – AI provider.
- `setProvider(String?) -> void`
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?) -> void`
- `getBaseUrl() -> String` – API base URL.
- `setBaseUrl(String?) -> void`
- `getApiKeyEnv() -> String` – API key env var name.
- `setApiKeyEnv(String?) -> void`
- `getReasoningEffort() -> String` – reasoning effort level.
- `setReasoningEffort(String) -> void`
- `getReasoningBudgetTokens() -> int` – reasoning budget.
- `setReasoningBudgetTokens(int) -> void`
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float) -> void`
- `getDryBase() -> float` – DRY base.
- `setDryBase(float) -> void`
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int) -> void`
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int) -> void`
- `getDrySequenceBreakers() -> List<String>` – unmodifiable list.
- `setDrySequenceBreakers(List<String>?) -> void`
- `getStopStrings() -> List<String>` – unmodifiable list.
- `setStopStrings(List<String>?) -> void`
- `getCalibration() -> AiCalibration?` – per‑machine calibration.
- `setCalibration(AiCalibration?) -> void`

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCalibration` (used in `calibration` field).

#### Exceptions / Errors
- No explicit exceptions thrown; setters perform null‑coalescing to defaults.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances are intended for single‑threaded configuration use.

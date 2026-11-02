### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:29Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider implementations.

#### Purpose
- Stores AI generation settings (model path, sampling, retry, trimming, stop strings).
- Exposes getters/setters for Maven plugin framework.

#### Type
`public class AiGenerationConfig` – class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments.
- Setter methods accept primitive values, `String`, `List<String>`, `@Nullable` types.
- Optional `AiCalibration` object.

#### Output
- Getter methods return current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` (Lombok) for diagnostics.

#### Core logic
- Default constants define sensible AI generation defaults.
- Setters validate nulls by resetting to defaults.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace null with empty defaults.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace null with empty defaults.
- `setCalibration` stores optional calibration.

#### Public API
- `getModelPath() -> String?` – model file path.
- `setModelPath(String?)` – set model path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int)` – set context size.
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int)` – set max output tokens.
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float)` – set temperature.
- `getThreads() -> int` – CPU thread count.
- `setThreads(int)` – set thread count.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int)` – set chars per token.
- `getMaxInputChars() -> int` – max input characters.
- `setMaxInputChars(int)` – set max input characters.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean)` – set trim warning flag.
- `getTopP() -> float` – nucleus sampling threshold.
- `setTopP(float)` – set nucleus threshold.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int)` – set top‑k limit.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float)` – set repetition penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float)` – set min‑p threshold.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float)` – set top‑n‑sigma threshold.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?)` – set chat‑template thinking mode.
- `isCachePrompt() -> boolean` – prompt‑caching flag.
- `isSwaFull() -> boolean` – full‑SWA flag.
- `setSwaFull(boolean)` – set full‑SWA flag.
- `getCacheReuse() -> int` – KV prefix‑reuse chunk size.
- `setCacheReuse(int)` – set KV prefix‑reuse chunk size.
- `getGpuLayers() -> int` – GPU layer offload count.
- `setGpuLayers(int)` – set GPU layer offload count.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int)` – set CPU MoE layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int)` – set RNG seed.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int)` – set CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int)` – set unified KV cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?)` – set tensor‑read laziness.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int)` – set repeat‑penalty window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?)` – set KV cache type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?)` – set KV cache type V.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean)` – set Flash Attention flag.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int)` – set logical batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int)` – set physical batch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int)` – set batch thread count.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int)` – set primary GPU index.
- `getDevices() -> String` – device list.
- `setDevices(String)` – set device list.
- `getProvider() -> String` – AI provider.
- `setProvider(String?)` – set AI provider.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?)` – set remote model ID.
- `getBaseUrl() -> String` – base URL.
- `setBaseUrl(String?)` – set base URL.
- `getApiKeyEnv() -> String` – API key environment variable.
- `setApiKeyEnv(String?)` – set API key env.
- `setCachePrompt(boolean)` – set prompt‑caching flag.
- `getReasoningEffort() -> String` – reasoning effort level.
- `setReasoningEffort(String)` – set reasoning effort.
- `getReasoningBudgetTokens() -> int` – reasoning token budget.
- `setReasoningBudgetTokens(int)` – set reasoning token budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float)` – set DRY multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float)` – set DRY base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int)` – set DRY allowed length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int)` – set DRY penalty window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable DRY sequence breakers.
- `setDrySequenceBreakers(List<String>?)` – set DRY sequence breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings.
- `setStopStrings(List<String>?)` – set stop strings.
- `getCalibration() -> AiCalibration?` – calibration object.
- `setCalibration(AiCalibration?)` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCalibration`.

#### Exceptions / Errors
- No checked exceptions thrown.
- Null arguments are handled by resetting to defaults; no `NullPointerException`.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

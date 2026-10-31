### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:49:03Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for a single model run, including model path, sampling, caching, GPU settings, and provider details.

#### Purpose
- Holds mutable AI generation configuration.
- Transports settings from Maven plugin to AI provider implementations.

#### Type
`public class AiGenerationConfig` – mutable JavaBean, Lombok `@ToString`, no `equals`/`hashCode`.

#### Input
- Constructor: no arguments.
- Setter methods for all configuration fields.
- `AiCalibration` object for per‑machine timing.

#### Output
- Getter methods expose current configuration.
- `Collections.unmodifiableList` for `drySequenceBreakers` and `stopStrings`.
- `toString()` via Lombok for diagnostics.

#### Core logic
- Default constants for all parameters.
- Setters enforce defaults when `null` or empty values are passed.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` reset to default constants on `null`.
- `setDrySequenceBreakers` and `setStopStrings` replace lists with empty `ArrayList` on `null`.
- No additional computation; class is a pure data holder.

#### Public API
- `getModelPath() -> String?` – model file path.
- `setModelPath(String?) -> void` – set model path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int) -> void` – set context size.
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int) -> void` – set max output tokens.
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float) -> void` – set temperature.
- `getThreads() -> int` – CPU threads.
- `setThreads(int) -> void` – set threads.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int) -> void` – set ratio.
- `getMaxInputChars() -> int` – max input characters.
- `setMaxInputChars(int) -> void` – set max input chars.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean) -> void` – set trim warning.
- `getTopP() -> float` – nucleus sampling threshold.
- `setTopP(float) -> void` – set top‑p.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int) -> void` – set top‑k.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float) -> void` – set repeat penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float) -> void` – set min‑p.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float) -> void` – set top‑n‑sigma.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?) -> void` – set thinking mode.
- `isCachePrompt() -> boolean` – prompt‑caching flag.
- `isSwaFull() -> boolean` – full SWA KV flag.
- `getCacheReuse() -> int` – KV prefix‑reuse chunk size.
- `setCacheReuse(int) -> void` – set cache reuse.
- `getGpuLayers() -> int` – GPU layer offload count.
- `setGpuLayers(int) -> void` – set GPU layers.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int) -> void` – set CPU MoE layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int) -> void` – set seed.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int) -> void` – set CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int) -> void` – set KV cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?) -> void` – set lazy mode.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int) -> void` – set repeat‑last‑n.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?) -> void` – set cache type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?) -> void` – set cache type V.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean) -> void` – set Flash Attention.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int) -> void` – set batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int) -> void` – set ubatch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int) -> void` – set batch threads.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int) -> void` – set main GPU.
- `getDevices() -> String` – device list.
- `setDevices(String) -> void` – set devices.
- `getProvider() -> String` – AI provider.
- `setProvider(String?) -> void` – set provider.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?) -> void` – set remote model.
- `getBaseUrl() -> String` – base URL.
- `setBaseUrl(String?) -> void` – set base URL.
- `getApiKeyEnv() -> String` – API key environment variable.
- `setApiKeyEnv(String?) -> void` – set API key env.
- `setCachePrompt(boolean) -> void` – enable prompt caching.
- `getReasoningEffort() -> String` – reasoning effort level.
- `setReasoningEffort(String) -> void` – set reasoning effort.
- `getReasoningBudgetTokens() -> int` – reasoning budget.
- `setReasoningBudgetTokens(int) -> void` – set reasoning budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float) -> void` – set DRY multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float) -> void` – set DRY base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int) -> void` – set DRY allowed length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int) -> void` – set DRY penalty window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable DRY sequence breakers.
- `setDrySequenceBreakers(List<String>?) -> void` – set DRY sequence breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings.
- `setStopStrings(List<String>?) -> void` – set stop strings.
- `getCalibration() -> AiCalibration?` – calibration object.
- `setCalibration(AiCalibration?) -> void` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: setters replace `null` with defaults or empty collections.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

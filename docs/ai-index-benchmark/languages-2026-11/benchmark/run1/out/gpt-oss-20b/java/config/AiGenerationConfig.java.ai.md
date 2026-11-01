### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:05Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider implementations.

#### Purpose
- Stores AI generation settings (model path, sampling, retry, trimming, stop strings).
- Exposes getters/setters for Maven plugin configuration.

#### Type
`public class AiGenerationConfig` – class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments.
- Setter methods receive primitive or object values for each configuration field.
- `setDrySequenceBreakers`/`setStopStrings` accept `List<String>` or `null`.
- `setCalibration` accepts `AiCalibration` or `null`.

#### Output
- Getter methods return current field values.
- `getDrySequenceBreakers`/`getStopStrings` return unmodifiable lists.
- `isCachePrompt`, `isSwaFull`, `isFlashAttn`, `isWarnOnTrim` return booleans.
- `getModelPath`, `getChatTemplateEnableThinking`, `getCalibration` return nullable types.

#### Core logic
- Default constants define standard AI generation parameters.
- Setters enforce non‑null defaults for optional string fields.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace `null` with empty defaults.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` reset to default when `null`.
- `getDrySequenceBreakers`/`getStopStrings` expose immutable views to prevent external mutation.

#### Public API
- `getModelPath() -> String?` – retrieve model file path.
- `setModelPath(String?) -> void` – set model file path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int) -> void` – set context size.
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int) -> void` – set max output tokens.
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float) -> void` – set temperature.
- `getThreads() -> int` – CPU thread count.
- `setThreads(int) -> void` – set thread count.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int) -> void` – set ratio.
- `getMaxInputChars() -> int` – max input characters.
- `setMaxInputChars(int) -> void` – set max input chars.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean) -> void` – set trim warning flag.
- `getTopP() -> float` – nucleus sampling threshold.
- `setTopP(float) -> void` – set top‑p.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int) -> void` – set top‑k.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float) -> void` – set penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float) -> void` – set min‑p.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float) -> void` – set top‑n‑sigma.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?) -> void` – set thinking mode.
- `isCachePrompt() -> boolean` – prompt caching flag.
- `isSwaFull() -> boolean` – full SWA KV flag.
- `setSwaFull(boolean) -> void` – set SWA flag.
- `getCacheReuse() -> int` – KV prefix reuse chunk size.
- `setCacheReuse(int) -> void` – set reuse size.
- `getGpuLayers() -> int` – GPU offload layers.
- `setGpuLayers(int) -> void` – set GPU layers.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int) -> void` – set MoE layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int) -> void` – set seed.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int) -> void` – set FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int) -> void` – set cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?) -> void` – set laziness.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int) -> void` – set window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?) -> void` – set type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?) -> void` – set type V.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean) -> void` – set flag.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int) -> void` – set batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int) -> void` – set physical batch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int) -> void` – set batch threads.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int) -> void` – set GPU index.
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
- `getReasoningBudgetTokens() -> int` – reasoning token budget.
- `setReasoningBudgetTokens(int) -> void` – set budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float) -> void` – set multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float) -> void` – set base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int) -> void` – set length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int) -> void` – set window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable breakers list.
- `setDrySequenceBreakers(List<String>?) -> void` – set breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings list.
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
- Setters accept `null` for optional fields, converting to defaults.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances are intended for single‑threaded configuration use.

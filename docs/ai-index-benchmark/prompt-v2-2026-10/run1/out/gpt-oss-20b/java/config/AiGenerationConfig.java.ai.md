### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:35:22Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a mutable configuration object for AI generation steps, exposing all model, sampling, and runtime parameters for Maven plugin integration.

#### Purpose
- Holds AI generation settings for a single model run.
- Supplies values to provider implementations and Maven configuration.

#### Type
`public class AiGenerationConfig` – class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each configuration field.
- `setDrySequenceBreakers`, `setStopStrings`, `setCalibration` accept lists or objects.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` handle `null` as reset to default.

#### Output
- Getter methods expose current values.
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.
- `getCalibration` returns optional calibration.

#### Core logic
- Default constants define sensible AI generation parameters.
- Setters enforce non‑null defaults for optional strings/lists.
- Boolean getters follow JavaBean naming (`isX`).
- No additional computation; configuration is a plain data holder.

#### Public API
- `getModelPath() -> String?` – model file path.
- `setModelPath(String?) -> void` – set model path.
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
- `setWarnOnTrim(boolean) -> void` – set trim warning.
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
- `setChatTemplateEnableThinking(Boolean?) -> void` – set mode.
- `isCachePrompt() -> boolean` – prompt caching flag.
- `isSwaFull() -> boolean` – full‑size SWA flag.
- `setSwaFull(boolean) -> void` – set SWA flag.
- `getCacheReuse() -> int` – KV prefix reuse chunk size.
- `setCacheReuse(int) -> void` – set reuse size.
- `getGpuLayers() -> int` – GPU layer offload count.
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
- `setDevices(String?) -> void` – set devices.
- `getProvider() -> String` – AI provider.
- `setProvider(String?) -> void` – set provider.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?) -> void` – set remote model.
- `getBaseUrl() -> String` – base API URL.
- `setBaseUrl(String?) -> void` – set base URL.
- `getApiKeyEnv() -> String` – API key environment variable.
- `setApiKeyEnv(String?) -> void` – set key env.
- `setCachePrompt(boolean) -> void` – enable prompt caching.
- `getReasoningEffort() -> String` – reasoning effort level.
- `setReasoningEffort(String) -> void` – set effort.
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
- `getCalibration() -> AiCalibration?` – optional calibration.
- `setCalibration(AiCalibration?) -> void` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters validate `null` by resetting to defaults; no illegal argument checks beyond that.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building. Instances are mutable and not thread‑safe.

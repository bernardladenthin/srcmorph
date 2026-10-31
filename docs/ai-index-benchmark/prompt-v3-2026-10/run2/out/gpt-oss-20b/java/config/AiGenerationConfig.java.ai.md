### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:16:10Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, exposing getters and setters for model, sampling, caching, and provider settings.

#### Purpose
- Stores AI generation configuration for a single model run.
- Supplies defaults and allows overrides via setters.

#### Type
`public class AiGenerationConfig` – class, public, mutable, Lombok `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for all configuration fields.
- Optional `AiCalibration` injected via `setCalibration`.

#### Output
- Getter methods expose current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` (Lombok) prints all fields for diagnostics.

#### Core logic
- Default constants define baseline values for context size, sampling, caching, GPU, etc.
- Setters assign values; some normalize nulls to defaults (e.g., `setLazyMode`, `setCacheTypeK`).
- Boolean getters use `isX` naming for readability.
- List getters return immutable views; setters replace lists or reset to empty.
- Calibration is optional; getters return nullable.

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
- `setThreads(int) -> void` – set threads.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int) -> void` – set chars per token.
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
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking flag.
- `setChatTemplateEnableThinking(Boolean?) -> void` – set flag.
- `isCachePrompt() -> boolean` – prompt caching enabled.
- `isSwaFull() -> boolean` – full SWA KV cache kept.
- `setSwaFull(boolean) -> void` – set SWA full flag.
- `getCacheReuse() -> int` – KV prefix reuse chunk size.
- `setCacheReuse(int) -> void` – set cache reuse.
- `getGpuLayers() -> int` – GPU layer offload count.
- `setGpuLayers(int) -> void` – set GPU layers.
- `getCpuMoeLayers() -> int` – CPU MoE layers kept.
- `setCpuMoeLayers(int) -> void` – set CPU MoE layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int) -> void` – set seed.
- `getCpuFfnLayers() -> int` – CPU FFN layers kept.
- `setCpuFfnLayers(int) -> void` – set CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int) -> void` – set KV cap.
- `getLazyMode() -> String` – tensor‑read laziness mode.
- `setLazyMode(String?) -> void` – set lazy mode.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int) -> void` – set repeat window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?) -> void` – set cache type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?) -> void` – set cache type V.
- `isFlashAttn() -> boolean` – Flash Attention enabled.
- `setFlashAttn(boolean) -> void` – set Flash Attn.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int) -> void` – set batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int) -> void` – set physical batch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int) -> void` – set batch threads.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int) -> void` – set main GPU.
- `getDevices() -> String` – device list.
- `setDevices(String?) -> void` – set devices.
- `getProvider() -> String` – AI provider name.
- `setProvider(String?) -> void` – set provider.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?) -> void` – set remote model.
- `getBaseUrl() -> String` – OpenAI‑compatible base URL.
- `setBaseUrl(String?) -> void` – set base URL.
- `getApiKeyEnv() -> String` – API key environment variable name.
- `setApiKeyEnv(String?) -> void` – set API key env.
- `setCachePrompt(boolean) -> void` – enable prompt caching.
- `getReasoningEffort() -> String` – reasoning‑effort level.
- `setReasoningEffort(String) -> void` – set reasoning effort.
- `getReasoningBudgetTokens() -> int` – reasoning token budget.
- `setReasoningBudgetTokens(int) -> void` – set reasoning budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float) -> void` – set DRY multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float) -> void` – set DRY base.
- `getDryAllowedLength() -> int` – DRY allowed n‑gram length.
- `setDryAllowedLength(int) -> void` – set DRY allowed length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int) -> void` – set DRY penalty window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable DRY sequence breakers.
- `setDrySequenceBreakers(List<String>?) -> void` – set DRY breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings.
- `setStopStrings(List<String>?) -> void` – set stop strings.
- `getCalibration() -> AiCalibration?` – per‑machine timing calibration.
- `setCalibration(AiCalibration?) -> void` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters accept nulls and normalize to defaults; no validation errors.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

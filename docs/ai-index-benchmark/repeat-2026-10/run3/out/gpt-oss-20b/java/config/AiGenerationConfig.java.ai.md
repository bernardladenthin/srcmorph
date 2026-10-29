### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:52:07Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a mutable configuration bean for AI generation steps, exposing all model, sampling, and runtime parameters for Maven plugin integration.

#### Purpose
- Holds AI generation settings for a single model run.
- Serves as a JavaBean populated by Maven plugin and passed to AI providers.

#### Type
- Class, public, mutable, Lombok `@ToString` annotated.

#### Input
- Constructor: no arguments.
- Setter methods for all configuration fields.
- Optional `AiCalibration` injected via `setCalibration`.

#### Output
- Getter methods expose current configuration values.
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.
- `toString` provides diagnostic representation.

#### Core logic
- Default constants define sensible AI generation parameters.
- Setters validate or normalize values (e.g., null → default, empty → default).
- No complex computation; configuration is stored and retrieved.

#### Public API
- `getModelPath() -> String?` – retrieve model file path.
- `setModelPath(String?)` – set model file path.
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
- `isCachePrompt() -> boolean` – prompt caching flag.
- `isSwaFull() -> boolean` – full SWA KV flag.
- `setSwaFull(boolean)` – set full SWA KV flag.
- `getCacheReuse() -> int` – KV prefix reuse chunk size.
- `setCacheReuse(int)` – set KV prefix reuse chunk size.
- `getGpuLayers() -> int` – GPU layer offload count.
- `setGpuLayers(int)` – set GPU layer offload count.
- `getCpuMoeLayers() -> int` – CPU MoE layer count.
- `setCpuMoeLayers(int)` – set CPU MoE layer count.
- `getSeed() -> int` – RNG seed.
- `setSeed(int)` – set RNG seed.
- `getCpuFfnLayers() -> int` – CPU FFN layer count.
- `setCpuFfnLayers(int)` – set CPU FFN layer count.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int)` – set unified KV context cap.
- `getLazyMode() -> String` – tensor‑read laziness mode.
- `setLazyMode(String?)` – set tensor‑read laziness mode.
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
- `getProvider() -> String` – AI provider name.
- `setProvider(String?)` – set AI provider name.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?)` – set remote model ID.
- `getBaseUrl() -> String` – OpenAI‑compatible base URL.
- `setBaseUrl(String?)` – set base URL.
- `getApiKeyEnv() -> String` – API key environment variable name.
- `setApiKeyEnv(String?)` – set API key env name.
- `setCachePrompt(boolean)` – enable prompt caching.
- `getReasoningEffort() -> String` – reasoning‑effort level.
- `setReasoningEffort(String)` – set reasoning‑effort level.
- `getReasoningBudgetTokens() -> int` – reasoning token budget.
- `setReasoningBudgetTokens(int)` – set reasoning token budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float)` – set DRY multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float)` – set DRY base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int)` – set DRY allowed length.
- `getDryPenaltyLastN() -> int` – DRY penalty look‑back window.
- `setDryPenaltyLastN(int)` – set DRY penalty look‑back window.
- `getDrySequenceBreakers() -> List<String>` – DRY sequence breakers.
- `setDrySequenceBreakers(List<String>?)` – set DRY sequence breakers.
- `getStopStrings() -> List<String>` – stop strings.
- `setStopStrings(List<String>?)` – set stop strings.
- `getCalibration() -> AiCalibration?` – per‑machine calibration.
- `setCalibration(AiCalibration?)` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters perform null‑to‑default normalization; no validation errors.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

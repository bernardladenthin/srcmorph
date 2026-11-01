### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:03:57Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven settings to AI provider APIs.

#### Purpose
- Stores AI generation settings (model path, sampling, caching, device, provider, etc.).
- Provides getters/setters for Maven plugin configuration.

#### Type
`public class AiGenerationConfig` – class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments.
- Setter methods accept primitive values, `String`, `List<String>`, `@Nullable` objects.
- Fields initialized with static defaults.

#### Output
- Getter methods expose current configuration values.
- `toString()` (Lombok) for diagnostics.
- No external side effects.

#### Core logic
- Default constants define sensible AI generation parameters.
- Setters validate nulls by resetting to defaults.
- List getters return unmodifiable views.
- Calibration field holds optional per‑machine timing data.

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
- `setCharsPerToken(int)` – set ratio.
- `getMaxInputChars() -> int` – max input characters.
- `setMaxInputChars(int)` – set max input chars.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean)` – set trim warning flag.
- `getTopP() -> float` – nucleus sampling threshold.
- `setTopP(float)` – set top‑p.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int)` – set top‑k.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float)` – set penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float)` – set min‑p.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float)` – set top‑n‑sigma.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?)` – set mode.
- `isCachePrompt() -> boolean` – prompt‑caching flag.
- `setCachePrompt(boolean)` – set prompt‑caching.
- `isSwaFull() -> boolean` – full‑SWA cache flag.
- `setSwaFull(boolean)` – set full‑SWA.
- `getCacheReuse() -> int` – KV prefix reuse chunk size.
- `setCacheReuse(int)` – set reuse size.
- `getGpuLayers() -> int` – GPU layer offload count.
- `setGpuLayers(int)` – set GPU layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int)` – set seed.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int)` – set MoE layers.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int)` – set FFN layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int)` – set cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?)` – set laziness.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int)` – set window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?)` – set type K.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?)` – set type V.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean)` – set flag.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int)` – set batch size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int)` – set physical batch size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int)` – set batch threads.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int)` – set GPU index.
- `getDevices() -> String` – device list.
- `setDevices(String)` – set device list.
- `getProvider() -> String` – AI provider name.
- `setProvider(String?)` – set provider.
- `getRemoteModel() -> String` – remote model ID.
- `setRemoteModel(String?)` – set remote model.
- `getBaseUrl() -> String` – base URL for OpenAI‑compatible API.
- `setBaseUrl(String?)` – set base URL.
- `getApiKeyEnv() -> String` – environment variable for API key.
- `setApiKeyEnv(String?)` – set API key env.
- `getReasoningEffort() -> String` – reasoning‑effort level.
- `setReasoningEffort(String)` – set reasoning effort.
- `getReasoningBudgetTokens() -> int` – reasoning token budget.
- `setReasoningBudgetTokens(int)` – set budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float)` – set multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float)` – set base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int)` – set length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int)` – set window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable DRY breakers list.
- `setDrySequenceBreakers(List<String>?)` – set breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings list.
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
- Null arguments reset to defaults; no `NullPointerException`.

#### Concurrency
- No synchronization; instance is not thread‑safe. Instances are intended for single‑threaded configuration usage.

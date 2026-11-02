### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:14Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures per-model AI generation, inference sampling, prompt limits, hardware use, provider routing, and output stopping behavior.

#### Purpose
- Carries mutable generation settings from Maven configuration to AI providers.
- Supplies defaults for local llama.cpp inference and OpenAI-compatible or mock providers.

#### Type
- `public class AiGenerationConfig` — mutable JavaBean class.
- Annotated `@ToString`; nullable fields use JSpecify `@Nullable`.
- No `equals` or `hashCode`; instances use identity semantics.

#### Input
- No-argument constructor; settings arrive through setters.
- Configuration values include model path, context and token limits, sampling and repetition controls, prompt trimming, hardware and KV-cache options, provider and API settings, reasoning controls, stop strings, DRY sequence breakers, and optional `AiCalibration`.
- Defaults are defined by public `DEFAULT_*` constants. Inference thread default is `min(4, availableProcessors())`.

#### Output
- Getter methods expose configured generation settings and optional calibration.
- `getStopStrings()` and `getDrySequenceBreakers()` return unmodifiable views of their lists.
- Setters mutate the configuration; null list arguments reset lists to empty, and several nullable string setters reset to their default.
- Lombok-generated `toString()` supports build-log diagnostics.

#### Core logic
- Initializes generation, sampling, prompt sizing, provider, and hardware settings from defaults.
- Supports automatic input-character budgeting via `charsPerToken`, with `maxInputChars` as the fallback limit.
- Represents chat-template thinking as tri-state: `null` omits the setting; `true` and `false` are explicit values.
- Uses sentinel values to leave upstream defaults unset, including `-1` for several llama.cpp options; empty strings omit optional string settings.
- Stores local/remote provider selection, remote model ID, base URL, and API-key environment-variable name; does not store the API key itself.
- Accepts per-machine timing calibration and lists of stop strings and DRY sequence breakers.

#### Public API
- `AiGenerationConfig()` — create configuration with defaults.
- `getModelPath()` / `setModelPath(String)` -> GGUF model path.
- `getContextSize()` / `setContextSize(int)` -> context window.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()` / `setTemperature(float)` -> sampling temperature.
- `getThreads()` / `setThreads(int)` -> inference thread count.
- `getCharsPerToken()` / `setCharsPerToken(int)` -> input budget ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` -> prompt character limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()` / `setTopP(float)` -> nucleus sampling threshold.
- `getTopK()` / `setTopK(int)` -> top-k limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()` / `setMinP(float)` -> min-p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` -> template thinking mode.
- `isCachePrompt()` / `setCachePrompt(boolean)` -> prompt prefix caching.
- `isSwaFull()` / `setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()` / `setCacheReuse(int)` -> KV prefix reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` -> CPU MoE layers.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` -> CPU FFN layers.
- `getSeed()` / `setSeed(int)` -> generation RNG seed.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()` / `setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()` / `setBatchSize(int)` -> logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()` / `setMainGpu(int)` -> primary GPU index.
- `getDevices()` / `setDevices(String)` -> backend device selection.
- `getProvider()` / `setProvider(String)` -> per-model provider.
- `getRemoteModel()` / `setRemoteModel(String)` -> remote model ID.
- `getBaseUrl()` / `setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()` / `setReasoningEffort(String)` -> reasoning effort level.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()` / `setDryMultiplier(float)` -> DRY repetition penalty multiplier.
- `getDryBase()` / `setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` -> DRY reset tokens.
- `getStopStrings()` / `setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` -> per-machine timing calibration.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- Lombok `ToString`
- JSpecify `Nullable`
- `Runtime.getRuntime().availableProcessors()`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Null handling: nullable model path and thinking mode are preserved; null list values become empty lists; null lazy mode, cache types, devices, provider, remote model, base URL, and API-key variable reset to defaults.
- Numeric and string ranges are generally not validated in this class.

#### Concurrency
- Mutable, unsynchronized configuration object; no thread-safety guarantees.
- List getters expose unmodifiable views, but setters retain the supplied list reference.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:22Z
- G: 
- A: 0.0.0
- X: file
---
> Configures per-model AI generation, including prompt limits, sampling, inference hardware, provider routing, and reasoning controls.

#### Purpose
- `AiGenerationConfig` carries one generation step’s settings between Maven configuration and AI providers.
- Supplies defaults for llama.cpp inference, OpenAI-compatible routing, prompt trimming, and generation behavior.

#### Type
- `public class AiGenerationConfig` — mutable class; no superclass or interfaces.
- `@ToString` generates a field-based diagnostic representation; `equals` and `hashCode` are not generated.
- `@Nullable` marks nullable model path, chat-template thinking mode, calibration, and selected setter inputs.

#### Input
- No-argument constructor; initializes settings through field defaults.
- Setters accept model path, context and token limits, sampling controls, hardware/cache options, provider/API settings, reasoning and DRY settings, stop strings, sequence breakers, and optional calibration.
- List setters accept `null` to reset lists to empty; several string setters normalize `null` to their defaults.

#### Output
- Getters expose configured generation settings; list getters return unmodifiable views.
- Setters mutate the configuration object.
- Public `DEFAULT_*` constants expose defaults for the configuration fields.

#### Core logic
- Provides defaults for model context, output size, temperature, thread count, input trimming, sampling, caching, and hardware offload.
- Supports local and OpenAI-compatible provider selection, remote model ID, base URL, and API-key environment-variable name.
- Models chat-template thinking as tri-state: `null` leaves the template kwarg unset; `true` or `false` is configurable.
- Carries reasoning-effort and token-budget controls, plus DRY repetition parameters and sequence breakers.
- Uses `charsPerToken` as the automatic input-limit calculation setting; `0` disables automatic calculation in favor of `maxInputChars`.
- Returns unmodifiable list views, but setters retain the supplied non-null list reference.

#### Public API
- `AiGenerationConfig()` — initialize defaults.
- `getModelPath()`/`setModelPath(String)` -> GGUF model path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> input-limit token estimate.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> source character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim-warning setting.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> template thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full-size SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix-reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition-penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model AI provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> per-machine timing calibration.

#### Dependencies
- `AiCalibration`
- `ArrayList`, `Collections`, `List`
- Lombok `ToString`
- JSpecify `Nullable`
- `Runtime` for available processor count

#### Exceptions / Errors
- No explicit exceptions or validation.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` replace `null` with defaults; list setters replace `null` with empty lists.
- Numeric ranges and string values are not validated by this class.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.
- List getters are unmodifiable views, not defensive copies; mutations through a retained setter-supplied list remain observable.

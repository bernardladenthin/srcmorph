### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:57:45Z
- G: 
- A: 0.0.0
- X: file
---
> Carries model-routing, inference, sampling, prompt-trimming, and calibration settings for a single AI generation step.

#### Purpose
- Stores mutable generation settings populated by the Maven configuration layer and consumed by AI providers.
- Supplies defaults for local and OpenAI-compatible providers, including sampling, runtime, and prompt controls.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString`; no superclass or implemented interfaces.
- Mutable JavaBean; `equals` and `hashCode` are not generated.
- Public static constants define defaults for context, output, sampling, runtime, provider, and related settings.

#### Input
- No-argument constructor; initializes settings from defaults.
- Setters accept model path, inference and sampling controls, provider configuration, stop strings, DRY sequence breakers, and optional `AiCalibration`.
- Reads available CPU core count to determine `DEFAULT_THREADS`.

#### Output
- Getters expose configuration values for generation providers.
- List getters return unmodifiable views of DRY sequence breakers and stop strings.
- Lombok-generated `toString()` provides field-based diagnostic output.

#### Core logic
- Initializes inference, prompt trimming, sampling, caching, hardware, provider, and reasoning settings from public defaults.
- Uses nullable thinking mode to distinguish unset from explicit `true` or `false`.
- Uses sentinel defaults such as `-1` and empty strings to represent provider/upstream defaults or omitted options.
- Resets nullable string inputs to their defaults in setters; resets nullable list inputs to empty lists.
- Stores optional calibration without calculating or applying it in this class.

#### Public API
- `AiGenerationConfig()` -> `void` — initializes default configuration.
- `getModelPath()`/`setModelPath(String)` -> model file path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> input-budget estimation ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt input character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p sampling threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> optional thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt caching setting.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV-cache setting.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload count.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layer count.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layer count.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing thread count.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model provider override.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort setting.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> optional machine calibration.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `java.lang.Runtime`

#### Exceptions / Errors
- No explicit exceptions, validation, or range checks.
- Nullable list setters replace `null` with empty lists; nullable string setters reset to configured defaults where implemented.
- Some settings are documented as requiring valid ranges; enforcement is left to consumers or provider bindings.

#### Concurrency
- Mutable and unsynchronized; no thread-safety guarantees.
- List getters prevent mutation through the returned view, but setters retain a supplied non-null list reference.

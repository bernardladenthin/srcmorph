### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:58:57Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, inference hardware, and repetition controls for each AI generation step.

#### Purpose
- Carries mutable per-model AI generation settings from Maven configuration to AI providers.
- Supplies defaults for generation, prompt trimming, caching, hardware, and remote API settings.

#### Type
- `public class AiGenerationConfig` — mutable class; no superclass or implemented interfaces.
- `@ToString`; Lombok generates diagnostic `toString()` only. Equality and hash code are not generated.
- Public no-argument constructor; 46 instance fields, including nullable model path and calibration.

#### Input
- Constructor accepts no parameters; fields are populated through setters.
- Inputs include model path, context and token limits, sampling and repetition settings, provider/API routing, hardware and cache controls, stop strings, DRY sequence breakers, and optional `AiCalibration`.
- List setters accept `null` to reset to empty lists; several string setters normalize `null` to their defaults.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate the configuration object. No files, streams, or external services are accessed.
- Lombok-generated `toString()` provides field-based diagnostic output.

#### Core logic
- Initializes generation and provider settings from public defaults, including `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`, `DEFAULT_PROVIDER`, and hardware and cache defaults.
- Stores prompt-trimming controls (`charsPerToken`, `maxInputChars`, `warnOnTrim`) for provider-side input handling.
- Stores sampling controls including top-p, top-k, min-p, top-n-sigma, repeat penalty, and DRY settings.
- Stores llama.cpp controls for prompt caching, KV reuse, SWA, GPU/CPU layer placement, batching, threads, devices, and cache types.
- Uses nullable `chatTemplateEnableThinking` to distinguish unset from explicitly enabled or disabled.
- Resets nullable list inputs to empty lists; exposes list contents through unmodifiable wrappers.

#### Public API
- `AiGenerationConfig()` — creates defaults.
- `getModelPath()`/`setModelPath(String)` -> GGUF model path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> prompt-size estimate ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warnings.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> tri-state thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt-prefix caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full-size SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix-reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layer count.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layer count.
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
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort level.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY exponential base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> optional timing calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.
- Referenced provider and binding concepts: `net.ladenthin.llama.parameters.ModelParameters`, llama.cpp, OpenAI-compatible APIs.

#### Exceptions / Errors
- No explicit exceptions are thrown or caught.
- No validation of numeric ranges or string values is performed by setters.
- Nullable model path, thinking mode, and calibration may remain unset; selected nullable strings reset to defaults, and nullable list inputs reset to empty lists.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.
- List getters prevent mutation through the returned view, but setters retain the supplied list reference.

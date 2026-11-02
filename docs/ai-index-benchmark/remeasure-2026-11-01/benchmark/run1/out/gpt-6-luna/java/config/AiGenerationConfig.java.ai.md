### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:03Z
- G: 
- A: 0.0.0
- X: file
---
> Carries per-model AI generation, sampling, inference, provider-routing, and input-trimming settings between Maven configuration and generation providers.

#### Purpose
- `AiGenerationConfig` stores mutable parameters for one AI generation step, with defaults for local inference and OpenAI-compatible routing.
- Exposes JavaBean accessors for Maven configuration; holds optional calibration, DRY sequence breakers, and stop strings.

#### Type
- `AiGenerationConfig` — public class; no superclass or implemented interfaces.
- `@ToString`; Lombok generates a field-based diagnostic string. `equals` and `hashCode` are not generated.
- Public static defaults cover context and output limits, sampling, trimming, caching, model execution, provider routing, and repetition controls.

#### Input
- No-argument constructor; fields initialize from `DEFAULT_*` constants.
- Setters accept model path, context and token limits, sampling and repetition parameters, thread and hardware settings, cache controls, provider/API settings, reasoning controls, string lists, and optional `AiCalibration`.
- Nullable model path, chat-template thinking mode, calibration, and selected string/list setters support unset or reset states.

#### Output
- Getters expose configured values; `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable views.
- Setters mutate configuration fields. Null list inputs reset to empty lists; selected nullable string inputs reset to their defaults.
- Lombok-generated `toString()` provides field-based diagnostics.

#### Core logic
- Initializes generation defaults, including context size `32768`, maximum output `128`, temperature `0.15`, top-p `0.9`, top-k `40`, and min-p `0.0`.
- Sets CPU thread default to the lesser of four and available processors.
- Stores source trimming settings: `charsPerToken = 4`, `maxInputChars = 120000`, and trim warnings enabled.
- Configures llama.cpp options such as prompt caching, SWA, cache reuse, GPU/CPU layer placement, KV cache types, batch sizes, and device selection.
- Supports tri-state chat-template thinking (`null` means omit the kwarg), reasoning effort/budget, and opt-in DRY sampling controls.
- Supports per-model provider selection, remote model ID, base URL, and API-key environment-variable name.
- Does not calculate input limits, invoke providers, validate values, or read/write resources.

#### Public API
- `AiGenerationConfig()` — initializes default configuration.
- `getModelPath()`/`setModelPath(String)` -> GGUF path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> automatic trim ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> input character cap.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warnings.
- `getTopP()`/`setTopP(float)` -> nucleus threshold.
- `getTopK()`/`setTopK(int)` -> top-k limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> thinking kwarg.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt KV caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix-reuse chunk.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU offload layers.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device list.
- `getProvider()`/`setProvider(String)` -> model AI provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model ID.
- `getBaseUrl()`/`setBaseUrl(String)` -> compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY sampling multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY reset tokens.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> machine timing calibration.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `Runtime.getRuntime().availableProcessors()`

#### Exceptions / Errors
- No explicit exceptions thrown or caught.
- Setters generally store supplied values without validation.
- Null list inputs reset to empty lists; nullable `lazyMode`, cache types, and other documented string settings reset to defaults where implemented.
- `getDrySequenceBreakers()` and `getStopStrings()` prevent list mutation through the returned view.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

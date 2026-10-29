### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:34Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, inference hardware, caching, and stop behavior for AI-generated project summaries.

#### Purpose
- `AiGenerationConfig` carries one generation step’s settings between Maven configuration and AI providers.
- Supplies defaults for local llama.cpp and OpenAI-compatible generation, including sampling, resource limits, and optional calibration.

#### Type
- Public mutable Java class; no superclass or implemented interfaces.
- `@ToString`; no generated equality or hash code.
- Nullable model path, tri-state chat-template thinking flag, and optional `AiCalibration`.

#### Input
- No-argument constructor; settings are supplied through JavaBean setters.
- Settings cover model path, context/output limits, input trimming, sampling, reasoning, stop strings, provider/API routing, llama.cpp caching and hardware options, and calibration.
- `setDrySequenceBreakers` and `setStopStrings` accept lists; null resets either list to empty.
- Several string setters normalize null to their configured empty/default value; `setLazyMode` does likewise.

#### Output
- Getter methods expose configured values; list getters return unmodifiable views.
- Mutable configuration state is retained in instance fields; Lombok-generated `toString()` supports diagnostics.
- Defaults include context size `32768`, output limit `128`, temperature `0.15`, top-p `0.9`, top-k `40`, provider unset, and prompt caching enabled.

#### Core logic
- Initializes settings from constants for generation, prompt trimming, sampling, reasoning, cache behavior, device selection, and provider configuration.
- Uses `Math.min(4, Runtime.getRuntime().availableProcessors())` for the default inference thread count.
- Represents chat-template thinking as tri-state: null means omit the kwarg; true/false are explicit values.
- Uses sentinel defaults to leave upstream decisions unchanged, including `-1` for several llama.cpp options and empty strings for optional settings.
- Normalizes nullable list/string inputs where specified; does not validate numeric ranges or string values.
- Stores calibration as nullable `AiCalibration`.

#### Public API
- `AiGenerationConfig()` → constructor; initialize defaults.
- `getModelPath()` → `@Nullable String`; read model path; `setModelPath(@Nullable String)` → `void`; configure model path.
- `getContextSize()` → `int`; read context limit; `setContextSize(int)` → `void`; configure context limit.
- `getMaxOutputTokens()` → `int`; read output-token limit; `setMaxOutputTokens(int)` → `void`; configure output-token limit.
- `getTemperature()` → `float`; read temperature; `setTemperature(float)` → `void`; configure temperature.
- `getThreads()` → `int`; read inference threads; `setThreads(int)` → `void`; configure inference threads.
- `getCharsPerToken()` → `int`; read token estimate; `setCharsPerToken(int)` → `void`; configure token estimate.
- `getMaxInputChars()` → `int`; read input limit; `setMaxInputChars(int)` → `void`; configure input limit.
- `isWarnOnTrim()` → `boolean`; read trim-warning flag; `setWarnOnTrim(boolean)` → `void`; configure trim warnings.
- `getTopP()` → `float`; read top-p; `setTopP(float)` → `void`; configure top-p.
- `getTopK()` → `int`; read top-k; `setTopK(int)` → `void`; configure top-k.
- `getRepeatPenalty()` → `float`; read repetition penalty; `setRepeatPenalty(float)` → `void`; configure repetition penalty.
- `getMinP()` → `float`; read min-p; `setMinP(float)` → `void`; configure min-p.
- `getTopNSigma()` → `float`; read top-n-sigma; `setTopNSigma(float)` → `void`; configure top-n-sigma.
- `getChatTemplateEnableThinking()` → `@Nullable Boolean`; read thinking mode; `setChatTemplateEnableThinking(@Nullable Boolean)` → `void`; configure thinking mode.
- `isCachePrompt()` → `boolean`; read prompt-cache flag; `setCachePrompt(boolean)` → `void`; configure prompt caching.
- `isSwaFull()` → `boolean`; read full-SWA flag; `setSwaFull(boolean)` → `void`; configure full-SWA cache.
- `getCacheReuse()` → `int`; read reuse chunk size; `setCacheReuse(int)` → `void`; configure reuse chunk size.
- `getGpuLayers()` → `int`; read GPU offload count; `setGpuLayers(int)` → `void`; configure GPU offload.
- `getCpuMoeLayers()` → `int`; read CPU MoE layers; `setCpuMoeLayers(int)` → `void`; configure CPU MoE layers.
- `getSeed()` → `int`; read RNG seed; `setSeed(int)` → `void`; configure RNG seed.
- `getCpuFfnLayers()` → `int`; read CPU FFN layers; `setCpuFfnLayers(int)` → `void`; configure CPU FFN layers.
- `getKvUnifiedPerSlot()` → `int`; read KV slot cap; `setKvUnifiedPerSlot(int)` → `void`; configure KV slot cap.
- `getLazyMode()` → `String`; read tensor-read mode; `setLazyMode(@Nullable String)` → `void`; configure tensor-read mode.
- `getRepeatLastN()` → `int`; read repetition window; `setRepeatLastN(int)` → `void`; configure repetition window.
- `getCacheTypeK()` → `String`; read K-cache type; `setCacheTypeK(@Nullable String)` → `void`; configure K-cache type.
- `getCacheTypeV()` → `String`; read V-cache type; `setCacheTypeV(@Nullable String)` → `void`; configure V-cache type.
- `isFlashAttn()` → `boolean`; read Flash Attention flag; `setFlashAttn(boolean)` → `void`; configure Flash Attention.
- `getBatchSize()` → `int`; read logical batch size; `setBatchSize(int)` → `void`; configure logical batch size.
- `getUbatchSize()` → `int`; read physical batch size; `setUbatchSize(int)` → `void`; configure physical batch size.
- `getThreadsBatch()` → `int`; read batch thread count; `setThreadsBatch(int)` → `void`; configure batch threads.
- `getMainGpu()` → `int`; read primary GPU; `setMainGpu(int)` → `void`; select primary GPU.
- `getDevices()` → `String`; read device list; `setDevices(String)` → `void`; configure device list.
- `getProvider()` → `String`; read model provider; `setProvider(String)` → `void`; configure model provider.
- `getRemoteModel()` → `String`; read remote model ID; `setRemoteModel(String)` → `void`; configure remote model ID.
- `getBaseUrl()` → `String`; read API base URL; `setBaseUrl(String)` → `void`; configure API base URL.
- `getApiKeyEnv()` → `String`; read API-key variable name; `setApiKeyEnv(String)` → `void`; configure API-key variable name.
- `getReasoningEffort()` → `String`; read reasoning effort; `setReasoningEffort(String)` → `void`; configure reasoning effort.
- `getReasoningBudgetTokens()` → `int`; read reasoning budget; `setReasoningBudgetTokens(int)` → `void`; configure reasoning budget.
- `getDryMultiplier()` → `float`; read DRY multiplier; `setDryMultiplier(float)` → `void`; configure DRY multiplier.
- `getDryBase()` → `float`; read DRY base; `setDryBase(float)` → `void`; configure DRY base.
- `getDryAllowedLength()` → `int`; read DRY allowed length; `setDryAllowedLength(int)` → `void`; configure DRY allowed length.
- `getDryPenaltyLastN()` → `int`; read DRY look-back window; `setDryPenaltyLastN(int)` → `void`; configure DRY look-back window.
- `getDrySequenceBreakers()` → `List<String>`; read unmodifiable breakers; `setDrySequenceBreakers(@Nullable List<String>)` → `void`; configure DRY breakers.
- `getStopStrings()` → `List<String>`; read unmodifiable stops; `setStopStrings(@Nullable List<String>)` → `void`; configure stop strings.
- `getCalibration()` → `@Nullable AiCalibration`; read calibration; `setCalibration(@Nullable AiCalibration)` → `void`; configure calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.
- Referenced integration concepts: llama.cpp, `ModelParameters`, OpenAI-compatible chat completions.

#### Exceptions / Errors
- No explicit exceptions thrown or caught.
- Numeric values and most non-null strings are assigned without validation.
- Null list inputs reset to empty; nullable `modelPath`, thinking mode, and calibration remain nullable.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.
- List getters provide unmodifiable views, not defensive copies.

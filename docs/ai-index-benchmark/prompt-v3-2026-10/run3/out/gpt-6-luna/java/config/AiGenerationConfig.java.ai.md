### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:13Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, reasoning, hardware, caching, and stopping behavior for an AI generation step.

#### Purpose
- Carries per-model generation settings between Maven configuration and AI providers.
- Provides defaults and JavaBean accessors for inference, remote-provider, and calibration options.

#### Type
- `public class AiGenerationConfig` — mutable class.
- Annotated `@ToString`; Lombok generates diagnostic `toString`.
- No `equals` or `hashCode`; instances use identity semantics.

#### Input
- No-argument constructor; initializes settings from `DEFAULT_*` constants.
- Setters accept model path, inference and prompt limits, sampling and reasoning settings, backend options, provider/API configuration, stop strings, DRY sequence breakers, and optional `AiCalibration`.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate instance fields; null list inputs reset to empty lists.
- Null inputs to selected string setters reset to their defaults.

#### Core logic
- Stores defaults for context and output limits, thread count, prompt trimming, and sampling.
- Represents chat-template thinking as tri-state `Boolean`: null omits the setting; true/false are explicit.
- Configures llama.cpp KV prompt reuse, SWA, CPU/GPU placement, batching, attention, cache types, and device selection.
- Supports OpenAI-compatible provider routing, remote model ID, base URL, and API-key environment-variable name.
- Carries reasoning effort/budget, DRY parameters and sequence breakers, stop strings, and optional machine calibration.
- Does not validate settings; downstream providers interpret sentinel values such as `-1` and empty strings.

#### Public API
- `AiGenerationConfig()` — initialize defaults.
- `getModelPath()` / `setModelPath(String)` → model file path.
- `getContextSize()` / `setContextSize(int)` → context token limit.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` → output token limit.
- `getTemperature()` / `setTemperature(float)` → sampling temperature.
- `getThreads()` / `setThreads(int)` → inference thread count.
- `getCharsPerToken()` / `setCharsPerToken(int)` → prompt sizing ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` → prompt character limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` → trim warnings.
- `getTopP()` / `setTopP(float)` → nucleus sampling threshold.
- `getTopK()` / `setTopK(int)` → top-k sampling limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` → repetition penalty.
- `getMinP()` / `setMinP(float)` → min-p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` → top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` → thinking-mode override.
- `isCachePrompt()` / `setCachePrompt(boolean)` → prompt caching.
- `isSwaFull()` / `setSwaFull(boolean)` → full SWA KV cache.
- `getCacheReuse()` / `setCacheReuse(int)` → KV prefix reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` → GPU layer offload.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` → CPU MoE layers.
- `getSeed()` / `setSeed(int)` → generation RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` → CPU FFN layers.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` → per-slot KV context cap.
- `getLazyMode()` / `setLazyMode(String)` → tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` → repetition-penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` → K cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` → V cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` → Flash Attention.
- `getBatchSize()` / `setBatchSize(int)` → logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` → physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` → prompt-processing thread count.
- `getMainGpu()` / `setMainGpu(int)` → primary GPU index.
- `getDevices()` / `setDevices(String)` → backend device selection.
- `getProvider()` / `setProvider(String)` → per-model AI provider.
- `getRemoteModel()` / `setRemoteModel(String)` → remote model ID.
- `getBaseUrl()` / `setBaseUrl(String)` → OpenAI-compatible API URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` → API-key environment variable name.
- `getReasoningEffort()` / `setReasoningEffort(String)` → reasoning-effort kwarg.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` → reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` → DRY repetition multiplier.
- `getDryBase()` / `setDryBase(float)` → DRY penalty base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` → DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` → DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` → DRY sequence breakers.
- `getStopStrings()` / `setStopStrings(List<String>)` → generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` → per-machine timing calibration.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- Referenced in documentation: `net.ladenthin.llama.parameters.ModelParameters`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable model path, thinking override, calibration, and selected setter inputs.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` normalize null to defaults; list setters normalize null to empty lists.
- List getters prevent mutation through the returned view; setters retain non-null list references.

#### Concurrency
- Mutable and unsynchronized; no thread-safety guarantees.

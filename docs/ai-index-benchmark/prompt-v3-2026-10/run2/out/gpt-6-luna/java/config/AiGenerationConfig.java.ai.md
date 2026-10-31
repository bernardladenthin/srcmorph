### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:10:57Z
- G: 
- A: 0.0.0
- X: file
---
> Carries model, sampling, prompt-budget, provider, and hardware settings for one AI generation step.

#### Purpose
- `AiGenerationConfig` transfers Maven-configured generation parameters to AI provider implementations.
- Supplies defaults and mutable JavaBean accessors for inference, prompt trimming, remote API routing, and optional calibration.

#### Type
- `AiGenerationConfig` — public class; Lombok `@ToString`; no equality or hash-code generation.
- Nullable fields use JSpecify `@Nullable`.

#### Input
- No-argument constructor; all settings are supplied through setters.
- Settings include model path, context/output limits, sampling and repetition controls, prompt/cache options, hardware tuning, provider/API details, stop strings, DRY controls, and optional `AiCalibration`.

#### Output
- Returns configured values through accessors; `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable views.
- Defaults include context size `32768`, output limit `128`, temperature `0.15`, top-p `0.9`, provider unset, and prompt caching enabled.
- Setters mutate instance fields; null list inputs reset lists to empty, and selected nullable string inputs reset to their defaults.
- Lombok-generated `toString()` includes instance fields.

#### Core logic
- Holds per-generation configuration; performs no inference or validation itself.
- Initializes settings from the declared defaults, including hardware-aware thread count `min(4, availableProcessors())`.
- Uses `charsPerToken` and `maxInputChars` settings to support provider-side prompt-budget handling; this class does not calculate the limit.
- Represents chat-template thinking as tri-state `Boolean`: `null` means unset.
- Stores stop strings and DRY sequence breakers in mutable lists; exposes unmodifiable getter views.
- Stores optional machine timing calibration; `null` means absent.

#### Public API
- `AiGenerationConfig()` → constructor; initializes defaults.
- `getModelPath()`/`setModelPath(String)` → model file path.
- `getContextSize()`/`setContextSize(int)` → context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` → output token limit.
- `getTemperature()`/`setTemperature(float)` → sampling temperature.
- `getThreads()`/`setThreads(int)` → inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` → automatic input-budget ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` → prompt source character cap.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` → trim warning setting.
- `getTopP()`/`setTopP(float)` → nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` → top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` → repetition penalty.
- `getMinP()`/`setMinP(float)` → min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` → top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` → nullable thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` → prompt caching setting.
- `isSwaFull()`/`setSwaFull(boolean)` → full SWA KV-cache setting.
- `getCacheReuse()`/`setCacheReuse(int)` → KV prefix reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` → GPU-offloaded layers.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` → CPU MoE layers.
- `getSeed()`/`setSeed(int)` → generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` → CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` → per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` → tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` → repetition penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` → K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` → V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` → Flash Attention setting.
- `getBatchSize()`/`setBatchSize(int)` → logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` → physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` → prompt-processing thread count.
- `getMainGpu()`/`setMainGpu(int)` → primary GPU index.
- `getDevices()`/`setDevices(String)` → backend device selection.
- `getProvider()`/`setProvider(String)` → per-model AI provider.
- `getRemoteModel()`/`setRemoteModel(String)` → remote API model ID.
- `getBaseUrl()`/`setBaseUrl(String)` → OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` → API key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` → reasoning-effort setting.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` → reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` → DRY repetition multiplier.
- `getDryBase()`/`setDryBase(float)` → DRY exponential base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` → DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` → DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` → DRY reset tokens.
- `getStopStrings()`/`setStopStrings(List<String>)` → generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` → optional timing calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.
- Referenced provider concepts: `ModelParameters`, llama.cpp, OpenAI-compatible APIs.

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable model path, thinking mode, and calibration are preserved; null list arguments reset to empty lists.
- Nullable lazy mode, cache types, and selected provider/API strings reset to configured defaults.

#### Concurrency
- Mutable, unsynchronized JavaBean; not thread-safe.
- List getters prevent mutation through the returned view, but setters retain the supplied list instance.

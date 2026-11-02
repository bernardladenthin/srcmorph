### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:11Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, inference hardware, and stopping behavior for one AI generation step.

#### Purpose
- Carries mutable generation settings from Maven configuration to AI providers.
- Supplies defaults for model inference, remote APIs, prompt trimming, and optional calibration.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString`; no superclass or implemented interfaces declared.
- Public no-argument constructor; 45 private instance fields, including nullable `modelPath`, `chatTemplateEnableThinking`, and `calibration`.
- Public `static final` defaults cover inference, sampling, provider routing, hardware, and caching; thread default is capped at four available processors.

#### Input
- Setters accept model path, context and token limits, sampling and repetition settings, prompt trimming options, provider/API details, hardware controls, and calibration.
- List setters accept DRY sequence breakers and stop strings; nullable list input resets each to empty.
- No files, streams, or external configuration resources are read directly.

#### Output
- Getters expose configured values; list getters return unmodifiable views.
- Setters mutate configuration fields; null handling resets selected strings to their defaults and nullable calibration/thinking values remain nullable.
- Lombok generates `toString()` for diagnostics.

#### Core logic
- Initializes generation settings from `DEFAULT_*` constants.
- Stores provider selection and OpenAI-compatible endpoint, model ID, and API-key environment-variable name.
- Represents chat-template thinking as tri-state: `null` means omit the kwarg; `true` and `false` are explicit values.
- Holds prompt-cache and SWA prefix-reuse controls, sampling thresholds, DRY repetition controls, and stop conditions.
- Provides GPU/CPU layer, device, batch, KV-cache, and thread settings for inference providers.
- Stores optional per-machine `AiCalibration`; this class does not calculate calibration or apply generation settings.

#### Public API
- `AiGenerationConfig()` → constructor; initialize defaults.
- `getModelPath()` / `setModelPath(String)` → `String`; model file path.
- `getContextSize()` / `setContextSize(int)` → `int`; context window.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` → `int`; output token limit.
- `getTemperature()` / `setTemperature(float)` → `float`; sampling temperature.
- `getThreads()` / `setThreads(int)` → `int`; inference threads.
- `getCharsPerToken()` / `setCharsPerToken(int)` → `int`; automatic trim ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` → `int`; prompt source limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` → `boolean`; trim warnings.
- `getTopP()` / `setTopP(float)` → `float`; nucleus threshold.
- `getTopK()` / `setTopK(int)` → `int`; top-k limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` → `float`; repetition penalty.
- `getMinP()` / `setMinP(float)` → `float`; min-p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` → `float`; top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` → `@Nullable Boolean`; thinking kwarg.
- `isCachePrompt()` / `setCachePrompt(boolean)` → `boolean`; prompt caching.
- `isSwaFull()` / `setSwaFull(boolean)` → `boolean`; full SWA cache.
- `getCacheReuse()` / `setCacheReuse(int)` → `int`; prefix-reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` → `int`; GPU layer offload.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` → `int`; CPU MoE layers.
- `getSeed()` / `setSeed(int)` → `int`; generation RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` → `int`; CPU FFN layers.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` → `int`; per-slot KV cap.
- `getLazyMode()` / `setLazyMode(String)` → `String`; tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` → `int`; repetition window.
- `getCacheTypeK()` / `setCacheTypeK(String)` → `String`; K-cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` → `String`; V-cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` → `boolean`; Flash Attention.
- `getBatchSize()` / `setBatchSize(int)` → `int`; logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` → `int`; physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` → `int`; batch-processing threads.
- `getMainGpu()` / `setMainGpu(int)` → `int`; primary GPU index.
- `getDevices()` / `setDevices(String)` → `String`; selected backend devices.
- `getProvider()` / `setProvider(String)` → `String`; per-model AI provider.
- `getRemoteModel()` / `setRemoteModel(String)` → `String`; remote model ID.
- `getBaseUrl()` / `setBaseUrl(String)` → `String`; compatible API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` → `String`; API-key environment variable.
- `getReasoningEffort()` / `setReasoningEffort(String)` → `String`; reasoning effort kwarg.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` → `int`; reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` → `float`; DRY penalty multiplier.
- `getDryBase()` / `setDryBase(float)` → `float`; DRY penalty base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` → `int`; DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` → `int`; DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` → `List<String>`; DRY sequence breakers.
- `getStopStrings()` / `setStopStrings(List<String>)` → `List<String>`; generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` → `@Nullable AiCalibration`; timing calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.
- Referenced provider concepts include `net.ladenthin.llama.parameters.ModelParameters` and llama.cpp inference settings.

#### Exceptions / Errors
- No exceptions are declared or caught.
- Nullable model path, thinking mode, and calibration are supported; selected nullable string setters replace null with defaults.
- Numeric ranges and string values are not validated in this class.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.
- List getters prevent modification through the returned view; list setters retain the supplied non-null list reference.

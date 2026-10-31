### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:35Z
- G: 
- A: 0.0.0
- X: file
---
> Configures per-model AI generation, sampling, prompt limits, provider routing, and inference-runtime behavior.

#### Purpose
- Carries mutable generation settings from Maven configuration to AI providers.
- Supplies defaults for sampling, prompt trimming, caching, hardware, and remote-provider options.

#### Type
- `public class AiGenerationConfig` — class; no explicit superclass or implemented interfaces.
- `@ToString`; Lombok generates a diagnostic string. Equality and hash code are not generated.
- Public static defaults cover context/output limits, sampling, provider routing, runtime tuning, and retry-independent generation options.

#### Input
- Public no-argument constructor; initializes instance fields from default constants.
- Setters accept model path, sampling and prompt limits, provider settings, runtime parameters, stop strings, DRY sequence breakers, and optional `AiCalibration`.
- `modelPath`, chat-template thinking mode, and calibration are nullable; several string/list setters reset null inputs to defaults or empty lists.

#### Output
- Getters expose configured generation parameters and calibration.
- List getters return unmodifiable views of DRY sequence breakers and stop strings.
- Setters mutate configuration state; no files or external resources are read or written.

#### Core logic
- Initializes context size, output-token cap, temperature, CPU threads, input trimming, and sampling settings from defaults.
- Stores llama.cpp options for prompt/SWA caching, KV reuse, GPU/CPU layer placement, batching, devices, cache types, and Flash Attention.
- Stores chat-template reasoning controls, seed, DRY repetition controls, and stop strings.
- Supports per-model provider selection and OpenAI-compatible endpoint, model ID, and API-key environment-variable name.
- Treats sentinel values such as `-1`, empty strings, and nullable thinking mode as provider-forwarding/default conventions; validation and provider behavior are outside this class.

#### Public API
- `AiGenerationConfig()` -> `void` — initialize defaults.
- `getModelPath()`/`setModelPath(String)` -> `String` — model file path.
- `getContextSize()`/`setContextSize(int)` -> `int` — context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> `int` — output token limit.
- `getTemperature()`/`setTemperature(float)` -> `float` — sampling temperature.
- `getThreads()`/`setThreads(int)` -> `int` — inference CPU threads.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> `int` — automatic trim estimate.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> `int` — source character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> `boolean` — trim warning toggle.
- `getTopP()`/`setTopP(float)` -> `float` — nucleus threshold.
- `getTopK()`/`setTopK(int)` -> `int` — top-k limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> `float` — repetition penalty.
- `getMinP()`/`setMinP(float)` -> `float` — min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> `float` — top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> `Boolean` — chat-template thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> `boolean` — prompt-cache toggle.
- `isSwaFull()`/`setSwaFull(boolean)` -> `boolean` — full SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> `int` — KV prefix-reuse chunk.
- `getGpuLayers()`/`setGpuLayers(int)` -> `int` — GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> `int` — CPU MoE layers.
- `getSeed()`/`setSeed(int)` -> `int` — generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> `int` — CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> `int` — per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> `String` — tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> `int` — repetition-penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> `String` — K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> `String` — V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> `boolean` — Flash Attention toggle.
- `getBatchSize()`/`setBatchSize(int)` -> `int` — logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> `int` — physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> `int` — batch-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> `int` — primary GPU index.
- `getDevices()`/`setDevices(String)` -> `String` — selected backend devices.
- `getProvider()`/`setProvider(String)` -> `String` — per-model provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> `String` — remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> `String` — compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> `String` — API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> `String` — reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> `int` — reasoning token cap.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> `float` — DRY penalty multiplier.
- `getDryBase()`/`setDryBase(float)` -> `float` — DRY exponential base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> `int` — unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> `int` — DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> `List<String>` — DRY reset tokens.
- `getStopStrings()`/`setStopStrings(List<String>)` -> `List<String>` — generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> `AiCalibration` — machine timing calibration.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.llama.parameters.ModelParameters` — referenced in documentation.

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable list setters replace null with empty lists; nullable string setters for lazy mode and cache types reset to defaults.
- Other nullable-annotated values may remain null.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.

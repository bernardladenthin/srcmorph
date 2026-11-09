### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:11Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures model selection, prompt limits, sampling, reasoning, hardware, caching, and stop behavior for AI-generated project summaries.

#### Purpose
- Carries one generation step’s settings between Maven configuration and AI provider implementations.
- Supplies defaults for llama.cpp and OpenAI-compatible generation, plus optional calibration.

#### Type
- `public class AiGenerationConfig` — mutable JavaBean class; Lombok `@ToString`; no `equals`/`hashCode`.
- Nullable fields: model path, chat-template thinking mode, calibration.

#### Input
- No-argument constructor; settings supplied through setters.
- Configuration covers model/provider endpoints, inference and sampling parameters, prompt trimming, hardware/cache options, reasoning, DRY repetition controls, stop strings, and calibration.
- Defaults include context size 32768, output limit 128, temperature 0.15, top-p 0.9, and API key environment-variable name empty.

#### Output
- Stores configuration state; getters expose values and unmodifiable views of DRY sequence breakers and stop strings.
- Lombok-generated `toString()` supports build-log diagnostics; no external resources are read or written.

#### Core logic
- Initializes instance settings from public defaults; CPU thread default is `min(4, availableProcessors())`.
- Uses sentinel defaults to defer decisions to the binding/native build: commonly `-1` for unset numeric options and empty strings for unset string options.
- Preserves tri-state thinking mode: `null` omits the chat-template kwarg; `true` and `false` are explicit settings.
- Resets nullable string/list setters to their configured empty defaults; list getters return unmodifiable views.
- Holds provider routing (`llamacpp-jni`, `openai-compatible`, `mock`), remote model, base URL, and API-key environment-variable name; stores no API key.
- Captures sampling controls (top-p, top-k, min-p, top-n-sigma, repetition penalty, DRY) and generation limits.
- Captures KV/prompt caching, GPU/CPU layer placement, device selection, batch/thread settings, and optional timing calibration.

#### Public API
- `AiGenerationConfig()` — initialize defaults.
- `getModelPath()`/`setModelPath(String)` -> GGUF model path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output-token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference CPU threads.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> automatic input sizing ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt input character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warnings.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> thinking kwarg.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt-prefix caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse chunk.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model provider routing.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY penalty multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY reset tokens.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> per-machine timing calibration.

#### Dependencies
- `AiCalibration` — optional per-machine timing data.
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`; `org.jspecify.annotations.Nullable`.
- `Runtime` — available-processor count for thread default.
- `net.ladenthin.llama.parameters.ModelParameters` — referenced in documentation for thinking kwargs.

#### Exceptions / Errors
- No explicit exceptions or validation.
- Null model path, thinking mode, and calibration are supported.
- Null lazy mode, cache types, devices, provider, remote model, base URL, API-key environment name, and list values reset to defaults/empty lists; other values are stored without validation.

#### Concurrency
- Mutable, unsynchronized configuration object; not thread-safe by implementation.
- List getters are unmodifiable views of mutable backing lists; setters retain a supplied non-null list reference.

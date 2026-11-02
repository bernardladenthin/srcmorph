### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:41:42Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, runtime resources, and stop behavior for one AI generation step.

#### Purpose
- `AiGenerationConfig` carries Maven-configured generation settings to AI providers.
- Supplies defaults for model inference, provider selection, input trimming, caching, and calibration.

#### Type
- `AiGenerationConfig` — mutable public class; no superclass or implemented interfaces.
- `@ToString`; Lombok generates a field-based `toString()`. Equality and hash code are not generated.
- Defines defaults for context, sampling, threading, providers, model execution, caching, and prompt handling.

#### Input
- Public no-argument constructor; fields initialize from `DEFAULT_*` constants.
- Setters accept model path, context and token limits, sampling settings, provider/API configuration, runtime options, lists, and optional `AiCalibration`.
- Nullable inputs: model path, chat-template thinking mode, calibration, and list/string settings documented to reset to defaults or empty values.
- No files, streams, or external configuration are read directly.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate instance fields; null list inputs reset to empty lists.
- No inference, resource writing, or other external side effects.

#### Core logic
- Initializes settings from constants, including context size `32768`, output limit `128`, temperature `0.15`, and thread count capped at four or available processors.
- Stores prompt sizing controls: `charsPerToken`, `maxInputChars`, and trim-warning flag; automatic sizing is left to consumers.
- Groups sampling controls, including top-p/top-k, min-p, top-n-sigma, repetition penalty, and DRY parameters.
- Supports tri-state chat-template thinking (`null` means omit the kwarg) and optional reasoning effort/budget.
- Carries llama.cpp runtime controls for prompt/KV caching, GPU and CPU layer placement, batch sizes, attention, devices, and cache types.
- Stores provider overrides and OpenAI-compatible endpoint settings; API key configuration stores an environment-variable name, not a key.
- Holds DRY sequence breakers, stop strings, and optional per-machine `AiCalibration`.

#### Public API
- `AiGenerationConfig()` -> `void` — initializes defaults.
- `getModelPath()`/`setModelPath(String)` -> `String`/`void` — model file path.
- `getContextSize()`/`setContextSize(int)` -> `int`/`void` — context token limit.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> `int`/`void` — output token limit.
- `getTemperature()`/`setTemperature(float)` -> `float`/`void` — sampling temperature.
- `getThreads()`/`setThreads(int)` -> `int`/`void` — inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> `int`/`void` — input sizing ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> `int`/`void` — prompt character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> `boolean`/`void` — trim warning control.
- `getTopP()`/`setTopP(float)` -> `float`/`void` — nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> `int`/`void` — top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> `float`/`void` — repetition penalty.
- `getMinP()`/`setMinP(float)` -> `float`/`void` — min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> `float`/`void` — top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> `Boolean`/`void` — thinking-mode override.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> `boolean`/`void` — prompt cache control.
- `isSwaFull()`/`setSwaFull(boolean)` -> `boolean`/`void` — full SWA cache control.
- `getCacheReuse()`/`setCacheReuse(int)` -> `int`/`void` — KV prefix reuse threshold.
- `getGpuLayers()`/`setGpuLayers(int)` -> `int`/`void` — GPU layer offload count.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> `int`/`void` — CPU MoE layer count.
- `getSeed()`/`setSeed(int)` -> `int`/`void` — generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> `int`/`void` — CPU FFN layer count.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> `int`/`void` — per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> `String`/`void` — tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> `int`/`void` — repetition penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> `String`/`void` — K cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> `String`/`void` — V cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> `boolean`/`void` — Flash Attention control.
- `getBatchSize()`/`setBatchSize(int)` -> `int`/`void` — logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> `int`/`void` — physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> `int`/`void` — prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> `int`/`void` — primary GPU index.
- `getDevices()`/`setDevices(String)` -> `String`/`void` — backend device selection.
- `getProvider()`/`setProvider(String)` -> `String`/`void` — model provider override.
- `getRemoteModel()`/`setRemoteModel(String)` -> `String`/`void` — remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> `String`/`void` — compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> `String`/`void` — API key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> `String`/`void` — reasoning effort setting.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> `int`/`void` — reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> `float`/`void` — DRY penalty multiplier.
- `getDryBase()`/`setDryBase(float)` -> `float`/`void` — DRY growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> `int`/`void` — unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> `int`/`void` — DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> `List<String>`/`void` — DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> `List<String>`/`void` — generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> `AiCalibration`/`void` — machine timing calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.

#### Exceptions / Errors
- No exceptions are explicitly thrown or caught.
- Nullable list setters replace `null` with empty lists; several nullable string setters replace `null` with their configured defaults.
- `getDrySequenceBreakers()` and `getStopStrings()` prevent list mutation through the returned view; setters retain a non-null supplied list.

#### Concurrency
- Mutable, unsynchronized JavaBean; not thread-safe.
- List views are unmodifiable but reflect changes to the backing list.

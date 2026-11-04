### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:28Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures llama.cpp and OpenAI-compatible text generation, including model loading, sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` carries provider and generation settings; `Builder` applies `AiGenerationConfig` defaults and constructs the configuration.

#### Type
- `LlamaCppJniConfig` — final class; annotated `@ConvertToRecord`, Lombok `@ToString`, and `@EqualsAndHashCode`; 41 private final configuration fields; nullable `Boolean chatTemplateEnableThinking`.
- `Builder` — public static final nested class; fluent mutable builder with a required `modelPath`.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- `Builder` setters accept sampling, token limits, threading, KV cache, GPU/device, provider/API, reasoning, DRY, and stop-string settings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.
- `apiKeyEnv` accepts the name of an environment variable, not an API key.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors return configured settings; `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.
- Stores 41 settings, including model path, generation parameters, provider configuration, and list-valued controls.

#### Core logic
- `builder(modelPath)` creates a builder and rejects a null model path.
- Builder fields initialize from `AiGenerationConfig.DEFAULT_*` (or empty lists); setter calls replace values fluently.
- String setters for lazy mode, cache types, devices, provider, remote model, URL, API-key environment name, and reasoning effort restore their defaults on `null`; list setters restore empty lists on `null`.
- `build()` copies builder field references and values into the configuration; constructor performs no normalization.
- Configuration fields are final, but list inputs are not copied; accessors prevent mutation through the returned view, not through an external list reference.

#### Public API
- `builder(String)` -> `Builder` — start configuration.
- `modelPath()` -> `String` — model file path.
- `contextSize()` -> `int` — context window.
- `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature.
- `threads()` -> `int` — CPU thread count.
- `topP()` -> `float` — nucleus threshold.
- `topK()` -> `int` — top-k limit.
- `minP()` -> `float` — min-p threshold.
- `topNSigma()` -> `float` — top-n-sigma threshold.
- `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — chat-template thinking mode.
- `cachePrompt()` -> `boolean` — prompt caching toggle.
- `swaFull()` -> `boolean` — full SWA KV cache toggle.
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — GPU offload layer count.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU MoE layer count.
- `cpuFfnLayers()` -> `int` — CPU FFN layer count.
- `kvUnifiedPerSlot()` -> `int` — per-slot unified KV cap.
- `lazyMode()` -> `String` — tensor-read laziness.
- `repeatLastN()` -> `int` — repetition penalty window.
- `cacheTypeK()` -> `String` — K cache data type.
- `cacheTypeV()` -> `String` — V cache data type.
- `flashAttn()` -> `boolean` — Flash Attention toggle.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch-processing thread count.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — selected devices.
- `provider()` -> `String` — AI provider selection.
- `remoteModel()` -> `String` — remote provider model ID.
- `baseUrl()` -> `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning-effort setting.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY sampling multiplier.
- `dryBase()` -> `float` — DRY base.
- `dryAllowedLength()` -> `int` — DRY allowed length.
- `dryPenaltyLastN()` -> `int` — DRY penalty look-back.
- `drySequenceBreakers()` -> `List<String>` — DRY sequence breakers.
- `stopStrings()` -> `List<String>` — generation stop strings.
- `Builder.contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)` -> `Builder` — set corresponding option.
- `Builder.build()` -> `LlamaCppJniConfig` — create configuration.

#### Dependencies
- `AiGenerationConfig`
- `ConvertToRecord`
- `Collections`, `List`, `Objects`
- Lombok `EqualsAndHashCode`, `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` for a null model path.
- Nullable thinking mode remains unset when null; several string and list setters map null to their configured defaults.

#### Concurrency
- `Builder` is explicitly not thread-safe.
- `LlamaCppJniConfig` fields are final; list references are not defensively copied.

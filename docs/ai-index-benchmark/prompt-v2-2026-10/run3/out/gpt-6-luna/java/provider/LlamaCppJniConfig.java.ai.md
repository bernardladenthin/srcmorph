### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:31Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI and OpenAI-compatible generation, including sampling, hardware, caching, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` holds immutable per-model generation settings.
- Its builder applies `AiGenerationConfig` defaults and allows named overrides.

#### Type
- `public final class LlamaCppJniConfig` — class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder` — fluent mutable builder.
- 41 private final configuration fields; `chatTemplateEnableThinking` is nullable.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept generation, sampling, caching, hardware, provider, reasoning, DRY, and stop-string settings.
- Default values come from `AiGenerationConfig`; list defaults are empty lists.

#### Output
- `build()` produces a `LlamaCppJniConfig` containing the model path and configured values.
- Record-style accessors expose values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over all fields.

#### Core logic
- Builder initializes settings from `AiGenerationConfig.DEFAULT_*`; model path is required at builder creation.
- Setters update one setting and return the same builder.
- Nullable string setters restore their matching default on `null`; nullable list setters substitute an empty list.
- `build()` copies all builder values into the immutable config without further normalization.
- `chatTemplateEnableThinking` preserves `null` to represent an unset option.

#### Public API
- `builder(String)` -> `Builder` — starts configuration with defaults.
- `modelPath()` -> `String` — returns GGUF model path.
- `contextSize()` -> `int` — returns context window.
- `maxOutputTokens()` -> `int` — returns output token limit.
- `temperature()` -> `float` — returns sampling temperature.
- `threads()` -> `int` — returns CPU thread count.
- `topP()` -> `float` — returns top-p threshold.
- `topK()` -> `int` — returns top-k limit.
- `minP()` -> `float` — returns min-p threshold.
- `topNSigma()` -> `float` — returns top-n-sigma threshold.
- `repeatPenalty()` -> `float` — returns repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — returns thinking mode.
- `cachePrompt()` -> `boolean` — returns prompt-caching setting.
- `swaFull()` -> `boolean` — returns full-size SWA KV setting.
- `cacheReuse()` -> `int` — returns KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — returns GPU offload layers.
- `seed()` -> `int` — returns generation RNG seed.
- `cpuMoeLayers()` -> `int` — returns CPU MoE layer count.
- `cpuFfnLayers()` -> `int` — returns CPU FFN layer count.
- `kvUnifiedPerSlot()` -> `int` — returns per-slot KV cap.
- `lazyMode()` -> `String` — returns tensor-read laziness mode.
- `repeatLastN()` -> `int` — returns repetition penalty window.
- `cacheTypeK()` -> `String` — returns K-cache data type.
- `cacheTypeV()` -> `String` — returns V-cache data type.
- `flashAttn()` -> `boolean` — returns Flash Attention setting.
- `batchSize()` -> `int` — returns logical batch size.
- `ubatchSize()` -> `int` — returns physical batch size.
- `threadsBatch()` -> `int` — returns batch-processing thread count.
- `mainGpu()` -> `int` — returns primary GPU index.
- `devices()` -> `String` — returns device selection.
- `provider()` -> `String` — returns provider selection.
- `remoteModel()` -> `String` — returns remote model ID.
- `baseUrl()` -> `String` — returns compatible API base URL.
- `apiKeyEnv()` -> `String` — returns API-key environment variable name.
- `reasoningEffort()` -> `String` — returns reasoning effort.
- `reasoningBudgetTokens()` -> `int` — returns reasoning token budget.
- `dryMultiplier()` -> `float` — returns DRY multiplier.
- `dryBase()` -> `float` — returns DRY base.
- `dryAllowedLength()` -> `int` — returns DRY allowed length.
- `dryPenaltyLastN()` -> `int` — returns DRY penalty window.
- `drySequenceBreakers()` -> `List<String>` — returns unmodifiable DRY breakers.
- `stopStrings()` -> `List<String>` — returns unmodifiable stop strings.
- `Builder.contextSize(int)` -> `Builder` — sets context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — sets output token limit.
- `Builder.temperature(float)` -> `Builder` — sets sampling temperature.
- `Builder.threads(int)` -> `Builder` — sets CPU thread count.
- `Builder.topP(float)` -> `Builder` — sets top-p threshold.
- `Builder.topK(int)` -> `Builder` — sets top-k limit.
- `Builder.minP(float)` -> `Builder` — sets min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — sets top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — sets repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — sets thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — sets prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — sets full-size SWA KV.
- `Builder.cacheReuse(int)` -> `Builder` — sets KV prefix-reuse size.
- `Builder.gpuLayers(int)` -> `Builder` — sets GPU offload layers.
- `Builder.seed(int)` -> `Builder` — sets generation seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — sets CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — sets CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — sets per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — sets tensor-read mode.
- `Builder.repeatLastN(int)` -> `Builder` — sets repetition window.
- `Builder.cacheTypeK(String)` -> `Builder` — sets K-cache data type.
- `Builder.cacheTypeV(String)` -> `Builder` — sets V-cache data type.
- `Builder.flashAttn(boolean)` -> `Builder` — sets Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — sets logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — sets physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — sets batch-processing threads.
- `Builder.mainGpu(int)` -> `Builder` — sets primary GPU index.
- `Builder.devices(String)` -> `Builder` — sets device selection.
- `Builder.provider(String)` -> `Builder` — sets provider.
- `Builder.remoteModel(String)` -> `Builder` — sets remote model ID.
- `Builder.baseUrl(String)` -> `Builder` — sets compatible API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — sets API-key environment variable.
- `Builder.reasoningEffort(String)` -> `Builder` — sets reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — sets reasoning token budget.
- `Builder.dryMultiplier(float)` -> `Builder` — sets DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — sets DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — sets DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — sets DRY penalty window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — sets DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — sets stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — creates immutable config.

#### Dependencies
- `AiGenerationConfig` — defaults for generation settings.
- `ConvertToRecord` — record-migration marker.
- Lombok `EqualsAndHashCode`, `ToString`.
- JSpecify `Nullable`.
- `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `"modelPath"`.
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, and `reasoningEffort` replace `null` with corresponding defaults.
- Null DRY breaker and stop-string lists become empty lists.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors expose unmodifiable views.
- `Builder` is explicitly not thread-safe; intended for single-threaded construction.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:36Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- Defines immutable generation settings initialized from `AiGenerationConfig` defaults and customized through a fluent builder.
- Exposes record-style accessors for model, sampling, runtime, provider, and stop-sequence settings.

#### Type
- `public final class LlamaCppJniConfig` — class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`; private constructor accepts `Builder`.
- `public static final class Builder` — nested class; fluent mutable builder for `LlamaCppJniConfig`.

#### Input
- `builder(String modelPath)` requires the GGUF model path; builder constructor rejects null.
- Builder setters accept generation limits, sampling controls, llama.cpp runtime and hardware options, provider/API settings, reasoning controls, and DRY/stop lists.
- Builder fields default to corresponding `AiGenerationConfig.DEFAULT_*` constants; list defaults are empty lists.
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, provider/API strings, and list setters replace null with defaults; `chatTemplateEnableThinking` remains nullable.

#### Output
- `build()` produces an immutable `LlamaCppJniConfig` containing 41 configuration values.
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable views of their lists.
- Lombok generates `equals`, `hashCode`, and `toString` over all fields.

#### Core logic
- `builder(modelPath)` constructs a `Builder`; its constructor applies `Objects.requireNonNull(modelPath, "modelPath")`.
- `Builder` initializes 39 non-list options from `AiGenerationConfig.DEFAULT_*`; `drySequenceBreakers` and `stopStrings` start as empty lists.
- Each setter updates its corresponding builder field and returns `this`; nullable string/list setters restore defaults when passed null.
- `build()` calls `new LlamaCppJniConfig(this)`; the private constructor copies all 41 builder values without additional normalization.
- Configuration accessors return stored values; list accessors wrap stored lists with `Collections.unmodifiableList`.

#### Public API
- `builder(String)` -> `Builder` — starts configuration with model path.
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
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — optional thinking mode.
- `cachePrompt()` -> `boolean` — prompt caching setting.
- `swaFull()` -> `boolean` — full SWA KV cache setting.
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — GPU offload layers.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU MoE layers.
- `cpuFfnLayers()` -> `int` — CPU FFN layers.
- `kvUnifiedPerSlot()` -> `int` — per-slot KV context cap.
- `lazyMode()` -> `String` — tensor-read laziness.
- `repeatLastN()` -> `int` — repetition penalty window.
- `cacheTypeK()` -> `String` — K cache data type.
- `cacheTypeV()` -> `String` — V cache data type.
- `flashAttn()` -> `boolean` — Flash Attention setting.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch processing threads.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — selected devices.
- `provider()` -> `String` — AI provider identifier.
- `remoteModel()` -> `String` — remote model identifier.
- `baseUrl()` -> `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning effort setting.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY multiplier.
- `dryBase()` -> `float` — DRY base.
- `dryAllowedLength()` -> `int` — DRY allowed length.
- `dryPenaltyLastN()` -> `int` — DRY penalty look-back.
- `drySequenceBreakers()` -> `List<String>` — unmodifiable DRY breaker view.
- `stopStrings()` -> `List<String>` — unmodifiable stop-string view.
- `Builder.contextSize(int)` -> `Builder` — sets context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — sets output token limit.
- `Builder.temperature(float)` -> `Builder` — sets sampling temperature.
- `Builder.threads(int)` -> `Builder` — sets CPU threads.
- `Builder.topP(float)` -> `Builder` — sets nucleus threshold.
- `Builder.topK(int)` -> `Builder` — sets top-k limit.
- `Builder.minP(float)` -> `Builder` — sets min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — sets top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — sets repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — sets optional thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — sets prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — sets full SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — sets KV reuse chunk size.
- `Builder.gpuLayers(int)` -> `Builder` — sets GPU offload layers.
- `Builder.seed(int)` -> `Builder` — sets RNG seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — sets CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — sets CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — sets per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — sets tensor-read laziness.
- `Builder.repeatLastN(int)` -> `Builder` — sets repetition window.
- `Builder.cacheTypeK(String)` -> `Builder` — sets K cache type.
- `Builder.cacheTypeV(String)` -> `Builder` — sets V cache type.
- `Builder.flashAttn(boolean)` -> `Builder` — sets Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — sets logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — sets physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — sets batch thread count.
- `Builder.mainGpu(int)` -> `Builder` — sets primary GPU.
- `Builder.devices(String)` -> `Builder` — sets device selection.
- `Builder.provider(String)` -> `Builder` — sets provider identifier.
- `Builder.remoteModel(String)` -> `Builder` — sets remote model.
- `Builder.baseUrl(String)` -> `Builder` — sets API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — sets API-key environment variable.
- `Builder.reasoningEffort(String)` -> `Builder` — sets reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — sets reasoning budget.
- `Builder.dryMultiplier(float)` -> `Builder` — sets DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — sets DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — sets DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — sets DRY look-back.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — sets DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — sets stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — creates immutable configuration.

#### Dependencies
- `AiGenerationConfig`; `ConvertToRecord`; `Nullable`.
- `Collections`, `List`, `Objects`.
- Lombok `EqualsAndHashCode`, `ToString`.

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` for null model path.
- Nullable `chatTemplateEnableThinking` may remain null; string and list setters restore defaults on null.

#### Concurrency
- `LlamaCppJniConfig` is immutable; builder is explicitly not thread-safe.

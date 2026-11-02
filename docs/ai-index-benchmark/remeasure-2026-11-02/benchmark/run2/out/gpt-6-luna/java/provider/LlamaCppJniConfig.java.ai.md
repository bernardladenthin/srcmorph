### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:55Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures llama.cpp and compatible AI model generation, including sampling, KV caching, hardware placement, remote API settings, reasoning, and stop sequences.

#### Purpose
- `LlamaCppJniConfig` holds immutable per-model generation settings, defaulting optional values from `AiGenerationConfig`.
- `Builder` provides named fluent configuration; `modelPath` is required.

#### Type
- `LlamaCppJniConfig` — `public final` class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- `Builder` — `public static final` nested class.
- `@Nullable Boolean chatTemplateEnableThinking`; 41 configuration fields total, all final in the config.

#### Input
- `builder(String modelPath)` requires a GGUF model path.
- Builder setters accept generation, sampling, caching, hardware, provider/API, reasoning, DRY, and stop-sequence options.
- `AiGenerationConfig.DEFAULT_*` constants supply scalar and string defaults; lists default to empty lists.

#### Output
- `build()` creates an immutable `LlamaCppJniConfig` by copying all builder values.
- Record-style accessors expose each setting; list accessors return unmodifiable views.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Core logic
- `builder(modelPath)` constructs a builder and rejects a null path.
- Builder fields initialize to corresponding `AiGenerationConfig` defaults; list settings initialize empty.
- Fluent setters replace values; nullable string and list inputs restore their defaults when null.
- `build()` copies the populated builder into the config without additional normalization.
- Null thinking mode remains distinct from `false`, allowing the provider to omit the chat-template kwarg.
- Configuration is immutable after construction; builder is intended for single-threaded use.

#### Public API
- `builder(String)` -> `Builder` — create defaulted builder.
- `modelPath()` -> `String` — GGUF model path.
- `contextSize()` -> `int` — context window.
- `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature.
- `threads()` -> `int` — CPU thread count.
- `topP()` -> `float` — nucleus threshold.
- `topK()` -> `int` — top-k limit.
- `minP()` -> `float` — min-p threshold.
- `topNSigma()` -> `float` — top-n-sigma threshold.
- `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — thinking mode.
- `cachePrompt()` -> `boolean` — prompt caching flag.
- `swaFull()` -> `boolean` — full SWA cache flag.
- `cacheReuse()` -> `int` — KV prefix reuse chunk.
- `gpuLayers()` -> `int` — GPU offload layers.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU MoE layers.
- `cpuFfnLayers()` -> `int` — CPU FFN layers.
- `kvUnifiedPerSlot()` -> `int` — per-slot KV cap.
- `lazyMode()` -> `String` — tensor-read laziness.
- `repeatLastN()` -> `int` — repeat penalty window.
- `cacheTypeK()` -> `String` — K cache type.
- `cacheTypeV()` -> `String` — V cache type.
- `flashAttn()` -> `boolean` — Flash Attention flag.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch-processing threads.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — device selection.
- `provider()` -> `String` — AI provider.
- `remoteModel()` -> `String` — remote model identifier.
- `baseUrl()` -> `String` — compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning effort setting.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY multiplier.
- `dryBase()` -> `float` — DRY base.
- `dryAllowedLength()` -> `int` — DRY allowed length.
- `dryPenaltyLastN()` -> `int` — DRY look-back window.
- `drySequenceBreakers()` -> `List<String>` — DRY sequence breakers.
- `stopStrings()` -> `List<String>` — generation stop strings.
- `Builder.contextSize(int)` -> `Builder` — set context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — set output token limit.
- `Builder.temperature(float)` -> `Builder` — set sampling temperature.
- `Builder.threads(int)` -> `Builder` — set CPU thread count.
- `Builder.topP(float)` -> `Builder` — set nucleus threshold.
- `Builder.topK(int)` -> `Builder` — set top-k limit.
- `Builder.minP(float)` -> `Builder` — set min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — set full SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload layers.
- `Builder.seed(int)` -> `Builder` — set RNG seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — set per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int)` -> `Builder` — set repetition window.
- `Builder.cacheTypeK(String)` -> `Builder` — set K cache type.
- `Builder.cacheTypeV(String)` -> `Builder` — set V cache type.
- `Builder.flashAttn(boolean)` -> `Builder` — set Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — set logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — set physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — set batch thread count.
- `Builder.mainGpu(int)` -> `Builder` — set primary GPU.
- `Builder.devices(String)` -> `Builder` — set device selection.
- `Builder.provider(String)` -> `Builder` — set AI provider.
- `Builder.remoteModel(String)` -> `Builder` — set remote model.
- `Builder.baseUrl(String)` -> `Builder` — set API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — set API-key environment variable.
- `Builder.reasoningEffort(String)` -> `Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — set reasoning budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — set stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — create immutable config.

#### Dependencies
- Java: `Collections`, `List`, `Objects`.
- Project: `AiGenerationConfig`, `ConvertToRecord`.
- Annotations: Lombok `EqualsAndHashCode`, `ToString`; JSpecify `Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Null `lazyMode`, cache types, devices, provider, remote model, base URL, API-key environment name, and reasoning effort restore their `AiGenerationConfig` defaults.
- Null DRY sequence breakers or stop strings become empty lists.

#### Concurrency
- `LlamaCppJniConfig` is immutable; list accessors prevent mutation through returned views.
- `Builder` is explicitly not thread-safe.

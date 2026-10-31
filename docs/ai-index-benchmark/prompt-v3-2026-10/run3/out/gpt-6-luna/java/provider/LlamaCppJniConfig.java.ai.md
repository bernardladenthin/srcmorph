### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:36:11Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI and OpenAI-compatible model generation, including sampling, compute, caching, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` holds model-generation settings, initialized from `AiGenerationConfig` defaults and selectively overridden through a fluent builder.
- Provides record-style accessors and Lombok-generated value semantics.

#### Type
- `public final class LlamaCppJniConfig` — final class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder` — fluent builder for `LlamaCppJniConfig`.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- `Builder` setter parameters configure sampling, CPU/GPU execution, KV cache, batching, provider/API details, reasoning, DRY sampling, and stop strings.
- Builder initializes options from `AiGenerationConfig.DEFAULT_*`; list options default to empty lists.

#### Output
- `build()` produces a `LlamaCppJniConfig` containing 41 final fields.
- Accessors expose configured values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over all fields.

#### Core logic
- Builder requires `modelPath` at construction and initializes other options to defaults.
- Fluent setters assign configuration values; string and list setters with `null` restore their defaults. Numeric and boolean setters assign directly.
- Private config constructor copies every builder field without further normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`; input lists are not copied.
- `chatTemplateEnableThinking` preserves nullable `Boolean` semantics so unset can differ from `false`.

#### Public API
- `builder(String)` -> `Builder` — start with defaults.
- `modelPath()` -> `String` — model file path.
- `contextSize()` -> `int` — context window.
- `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature.
- `threads()` -> `int` — CPU thread count.
- `topP()` -> `float` — nucleus sampling threshold.
- `topK()` -> `int` — top-k limit.
- `minP()` -> `float` — min-p threshold.
- `topNSigma()` -> `float` — top-n-sigma threshold.
- `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — optional template thinking mode.
- `cachePrompt()` -> `boolean` — prompt caching flag.
- `swaFull()` -> `boolean` — full SWA KV cache flag.
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — GPU layer offload count.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU MoE layer count.
- `cpuFfnLayers()` -> `int` — CPU FFN layer count.
- `kvUnifiedPerSlot()` -> `int` — per-slot unified KV cap.
- `lazyMode()` -> `String` — tensor-read laziness.
- `repeatLastN()` -> `int` — repetition-penalty window.
- `cacheTypeK()` -> `String` — K-cache data type.
- `cacheTypeV()` -> `String` — V-cache data type.
- `flashAttn()` -> `boolean` — Flash Attention flag.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch-processing thread count.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — device selection.
- `provider()` -> `String` — AI provider.
- `remoteModel()` -> `String` — remote model identifier.
- `baseUrl()` -> `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning-effort setting.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY sampling multiplier.
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
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set optional thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — set full SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload count.
- `Builder.seed(int)` -> `Builder` — set generation seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — set KV context cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int)` -> `Builder` — set repetition window.
- `Builder.cacheTypeK(String)` -> `Builder` — set K-cache data type.
- `Builder.cacheTypeV(String)` -> `Builder` — set V-cache data type.
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
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — set stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — create immutable config.

#### Dependencies
- `AiGenerationConfig` — default values for configuration options.
- `ConvertToRecord`, Lombok `EqualsAndHashCode`, Lombok `ToString`.
- `Collections`, `List`, `Objects`; JSpecify `Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `"modelPath"`.
- Null string and list setter inputs restore defaults; nullable thinking mode remains nullable.

#### Concurrency
- `LlamaCppJniConfig` fields are final; builder documentation specifies it is not thread-safe.
- List accessors prevent mutation through returned views, but builder-supplied lists are not defensively copied.

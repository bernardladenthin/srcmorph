### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:58:27Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI model loading, sampling, caching, hardware, remote-provider, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` holds generation and provider settings for one model path.
- `Builder` applies `AiGenerationConfig` defaults and creates the immutable configuration.

#### Type
- `LlamaCppJniConfig` — `final` class; annotated `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.
- `Builder` — `public static final` nested class.
- Configuration contains 41 fields; Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Optional fluent builder parameters configure sampling, llama.cpp runtime, providers, reasoning, DRY sampling, and stop strings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose configured values; list accessors return unmodifiable views.
- No resources are read or written.

#### Core logic
- `Builder` initializes all options to their corresponding defaults.
- Fluent methods replace individual values; selected nullable strings and lists restore their defaults when passed `null`.
- The private configuration constructor copies all builder fields without further normalization.
- Thinking mode remains nullable so callers can distinguish unset from explicitly enabled or disabled.
- List values are retained from the builder; accessors wrap them as unmodifiable views.

#### Public API
- `builder(String)` → `Builder` — start with model path.
- `modelPath()` → `String` — return model path.
- `contextSize()`/`contextSize(int)` → `int`/`Builder` — context window.
- `maxOutputTokens()`/`maxOutputTokens(int)` → `int`/`Builder` — output token limit.
- `temperature()`/`temperature(float)` → `float`/`Builder` — sampling temperature.
- `threads()`/`threads(int)` → `int`/`Builder` — CPU thread count.
- `topP()`/`topP(float)` → `float`/`Builder` — nucleus-sampling threshold.
- `topK()`/`topK(int)` → `int`/`Builder` — top-k limit.
- `minP()`/`minP(float)` → `float`/`Builder` — min-p threshold.
- `topNSigma()`/`topNSigma(float)` → `float`/`Builder` — top-n-sigma threshold.
- `repeatPenalty()`/`repeatPenalty(float)` → `float`/`Builder` — repetition penalty.
- `chatTemplateEnableThinking()`/`chatTemplateEnableThinking(@Nullable Boolean)` → `@Nullable Boolean`/`Builder` — thinking mode.
- `cachePrompt()`/`cachePrompt(boolean)` → `boolean`/`Builder` — prompt-prefix caching.
- `swaFull()`/`swaFull(boolean)` → `boolean`/`Builder` — retain full SWA KV cache.
- `cacheReuse()`/`cacheReuse(int)` → `int`/`Builder` — KV prefix-reuse chunk.
- `gpuLayers()`/`gpuLayers(int)` → `int`/`Builder` — GPU layer offload.
- `seed()`/`seed(int)` → `int`/`Builder` — generation RNG seed.
- `cpuMoeLayers()`/`cpuMoeLayers(int)` → `int`/`Builder` — CPU MoE layers.
- `cpuFfnLayers()`/`cpuFfnLayers(int)` → `int`/`Builder` — CPU FFN layers.
- `kvUnifiedPerSlot()`/`kvUnifiedPerSlot(int)` → `int`/`Builder` — per-slot KV cap.
- `lazyMode()`/`lazyMode(String)` → `String`/`Builder` — tensor-read laziness.
- `repeatLastN()`/`repeatLastN(int)` → `int`/`Builder` — repetition-penalty window.
- `cacheTypeK()`/`cacheTypeK(String)` → `String`/`Builder` — K cache data type.
- `cacheTypeV()`/`cacheTypeV(String)` → `String`/`Builder` — V cache data type.
- `flashAttn()`/`flashAttn(boolean)` → `boolean`/`Builder` — Flash Attention setting.
- `batchSize()`/`batchSize(int)` → `int`/`Builder` — logical batch size.
- `ubatchSize()`/`ubatchSize(int)` → `int`/`Builder` — physical batch size.
- `threadsBatch()`/`threadsBatch(int)` → `int`/`Builder` — batch-processing thread count.
- `mainGpu()`/`mainGpu(int)` → `int`/`Builder` — primary GPU index.
- `devices()`/`devices(String)` → `String`/`Builder` — device selection.
- `provider()`/`provider(String)` → `String`/`Builder` — AI provider selection.
- `remoteModel()`/`remoteModel(String)` → `String`/`Builder` — remote model identifier.
- `baseUrl()`/`baseUrl(String)` → `String`/`Builder` — OpenAI-compatible API base URL.
- `apiKeyEnv()`/`apiKeyEnv(String)` → `String`/`Builder` — API-key environment variable name.
- `reasoningEffort()`/`reasoningEffort(String)` → `String`/`Builder` — reasoning effort setting.
- `reasoningBudgetTokens()`/`reasoningBudgetTokens(int)` → `int`/`Builder` — reasoning token budget.
- `dryMultiplier()`/`dryMultiplier(float)` → `float`/`Builder` — DRY sampling multiplier.
- `dryBase()`/`dryBase(float)` → `float`/`Builder` — DRY base.
- `dryAllowedLength()`/`dryAllowedLength(int)` → `int`/`Builder` — DRY allowed length.
- `dryPenaltyLastN()`/`dryPenaltyLastN(int)` → `int`/`Builder` — DRY look-back window.
- `drySequenceBreakers()`/`drySequenceBreakers(List<String>)` → `List<String>`/`Builder` — DRY sequence breakers.
- `stopStrings()`/`stopStrings(List<String>)` → `List<String>`/`Builder` — generation stop strings.
- `build()` → `LlamaCppJniConfig` — create immutable configuration.

#### Dependencies
- `AiGenerationConfig` — configuration defaults.
- `ConvertToRecord` — record-migration marker.
- Lombok `@EqualsAndHashCode`, `@ToString`.
- JSpecify `@Nullable`.
- Java `Collections`, `List`, and `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `"modelPath"`.
- Nullable builder inputs for selected strings and lists restore defaults; no other validation is performed.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors expose unmodifiable wrappers.
- `Builder` is explicitly not thread-safe; intended for single-threaded construction.

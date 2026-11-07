### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:11:17Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures GGUF model generation, sampling, llama.cpp runtime resources, remote-provider settings, reasoning controls, and stop behavior.

#### Purpose
- `LlamaCppJniConfig` carries model and generation options from named builder inputs to provider code.
- Supplies `AiGenerationConfig` defaults and immutable access to the resulting configuration.

#### Type
- `public final class LlamaCppJniConfig`; annotated `@ConvertToRecord`, Lombok `@ToString` and `@EqualsAndHashCode`.
- Nested `public static final class Builder`; builder pattern; no inheritance or implemented interfaces.
- Nullable `Boolean chatTemplateEnableThinking`; list-valued DRY sequence breakers and stop strings.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Optional fluent `Builder` setters configure sampling, threading, cache and GPU settings, provider/API fields, reasoning, DRY sampling, and stop strings.
- Builder fields initialize from corresponding `AiGenerationConfig.DEFAULT_*` constants; list defaults are empty lists.

#### Output
- `build()` creates an immutable `LlamaCppJniConfig` containing 41 configuration values.
- Record-style accessors expose configuration; list accessors return unmodifiable views.
- Lombok generates equality, hash code, and string representation over all fields.

#### Core logic
- `Builder` stores the required model path and initializes all other options with defaults.
- Fluent setters replace option values; nullable string and list inputs restore defaults when null. `chatTemplateEnableThinking` preserves null as unset.
- Private constructor copies every builder value into final fields; it performs no validation or normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`.

#### Public API
- `builder(String)` → `Builder` — start with model path and defaults.
- `modelPath()` → `String` — GGUF model path.
- `contextSize()` → `int` — context window.
- `maxOutputTokens()` → `int` — output token limit.
- `temperature()` → `float` — sampling temperature.
- `threads()` → `int` — CPU thread count.
- `topP()` → `float` — nucleus sampling threshold.
- `topK()` → `int` — top-k sampling limit.
- `minP()` → `float` — min-p threshold.
- `topNSigma()` → `float` — top-n-sigma threshold.
- `repeatPenalty()` → `float` — repetition penalty.
- `chatTemplateEnableThinking()` → `@Nullable Boolean` — chat-template thinking mode.
- `cachePrompt()` → `boolean` — prompt caching flag.
- `swaFull()` → `boolean` — full-size SWA KV cache flag.
- `cacheReuse()` → `int` — KV prefix-reuse chunk size.
- `gpuLayers()` → `int` — GPU layer offload count.
- `seed()` → `int` — generation RNG seed.
- `cpuMoeLayers()` → `int` — CPU MoE layer count.
- `cpuFfnLayers()` → `int` — CPU FFN layer count.
- `kvUnifiedPerSlot()` → `int` — per-slot unified KV cap.
- `lazyMode()` → `String` — tensor-read laziness mode.
- `repeatLastN()` → `int` — repetition penalty window.
- `cacheTypeK()` / `cacheTypeV()` → `String` — KV-cache data types.
- `flashAttn()` → `boolean` — Flash Attention flag.
- `batchSize()` / `ubatchSize()` → `int` — logical and physical batch sizes.
- `threadsBatch()` → `int` — batch-processing thread count.
- `mainGpu()` → `int` — primary GPU index.
- `devices()` → `String` — device selection.
- `provider()` → `String` — provider selection.
- `remoteModel()` → `String` — remote model identifier.
- `baseUrl()` → `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` → `String` — environment variable name for API key.
- `reasoningEffort()` → `String` — reasoning effort setting.
- `reasoningBudgetTokens()` → `int` — reasoning token budget.
- `dryMultiplier()` / `dryBase()` → `float` — DRY sampling parameters.
- `dryAllowedLength()` / `dryPenaltyLastN()` → `int` — DRY length and penalty window.
- `drySequenceBreakers()` / `stopStrings()` → `List<String>` — generation stop controls.
- `Builder` has 40 fluent setters: `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)` — each returns `Builder` and sets its option.
- `Builder.build()` → `LlamaCppJniConfig` — construct immutable configuration.

#### Dependencies
- `AiGenerationConfig` — option defaults.
- `ConvertToRecord` — record-migration marker.
- Lombok `@EqualsAndHashCode`, `@ToString`.
- JSpecify `@Nullable`; Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, message `modelPath`.
- No other validation or explicit exception handling; null string/list setter inputs restore defaults, except nullable thinking mode.

#### Concurrency
- `LlamaCppJniConfig` fields are final; intended immutable configuration.
- `Builder` is mutable and explicitly not thread-safe.

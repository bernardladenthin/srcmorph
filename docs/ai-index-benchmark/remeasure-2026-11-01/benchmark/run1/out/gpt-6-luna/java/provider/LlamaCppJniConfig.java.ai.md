### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:21:03Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI model loading, generation, sampling, caching, hardware, remote-provider, reasoning, and stop-string options.

#### Purpose
- `LlamaCppJniConfig` holds immutable, fully defaulted settings for a llama.cpp JNI model and related generation providers.
- Nested `Builder` allows named configuration of model and generation options.

#### Type
- `LlamaCppJniConfig` — `public final` class; implements no interfaces.
- Nested `public static final class Builder`.
- `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`; value semantics cover all fields.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling, cache, hardware, provider, reasoning, DRY, and stop-string settings.
- Other settings initialize from `AiGenerationConfig.DEFAULT_*`; DRY sequence breakers and stop strings default to empty lists.

#### Output
- Immutable `LlamaCppJniConfig` instance returned by `Builder.build()`.
- Record-style accessors expose configuration values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString`.

#### Core logic
- `LlamaCppJniConfig.Builder` stores the required model path and defaults for all other options.
- Setters mutate builder state and return the same builder for chaining.
- `null` resets selected string settings to their configured defaults; null list values become empty lists.
- Private configuration constructor copies all 41 builder values without additional normalization.
- Sampling options include temperature, top-p/top-k, min-p, top-n-sigma, repeat penalty, and DRY controls.
- Runtime options include prompt/KV caching, Flash Attention, batch sizes, CPU/GPU placement, devices, and thread counts.
- Provider options cover local/OpenAI-compatible/mock selection, remote model, API URL/key environment-variable name, and reasoning controls.

#### Public API
- `builder(String)` -> `Builder` — start with model path and defaults
- `modelPath()` -> `String` — GGUF model path
- `contextSize()` -> `int` — context window
- `maxOutputTokens()` -> `int` — output token limit
- `temperature()` -> `float` — sampling temperature
- `threads()` -> `int` — CPU thread count
- `topP()` -> `float` — nucleus sampling threshold
- `topK()` -> `int` — top-k sampling limit
- `minP()` -> `float` — min-p threshold
- `topNSigma()` -> `float` — top-n-sigma threshold
- `repeatPenalty()` -> `float` — repetition penalty
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — template thinking mode
- `cachePrompt()` -> `boolean` — prompt caching flag
- `swaFull()` -> `boolean` — full-size SWA KV flag
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size
- `gpuLayers()` -> `int` — GPU offload layer count
- `seed()` -> `int` — generation RNG seed
- `cpuMoeLayers()` -> `int` — CPU MoE layer count
- `cpuFfnLayers()` -> `int` — CPU FFN layer count
- `kvUnifiedPerSlot()` -> `int` — per-slot unified KV cap
- `lazyMode()` -> `String` — tensor-read laziness
- `repeatLastN()` -> `int` — repetition penalty window
- `cacheTypeK()` -> `String` — K cache data type
- `cacheTypeV()` -> `String` — V cache data type
- `flashAttn()` -> `boolean` — Flash Attention flag
- `batchSize()` -> `int` — logical batch size
- `ubatchSize()` -> `int` — physical batch size
- `threadsBatch()` -> `int` — batch-processing thread count
- `mainGpu()` -> `int` — primary GPU index
- `devices()` -> `String` — device selection
- `provider()` -> `String` — AI provider
- `remoteModel()` -> `String` — remote provider model ID
- `baseUrl()` -> `String` — OpenAI-compatible API base URL
- `apiKeyEnv()` -> `String` — API-key environment variable name
- `reasoningEffort()` -> `String` — reasoning-effort value
- `reasoningBudgetTokens()` -> `int` — reasoning token budget
- `dryMultiplier()` -> `float` — DRY sampling multiplier
- `dryBase()` -> `float` — DRY base
- `dryAllowedLength()` -> `int` — DRY allowed length
- `dryPenaltyLastN()` -> `int` — DRY look-back window
- `drySequenceBreakers()` -> `List<String>` — DRY sequence breakers
- `stopStrings()` -> `List<String>` — stop strings
- `contextSize(int)` -> `Builder` — set context window
- `maxOutputTokens(int)` -> `Builder` — set output token limit
- `temperature(float)` -> `Builder` — set sampling temperature
- `threads(int)` -> `Builder` — set CPU thread count
- `topP(float)` -> `Builder` — set nucleus threshold
- `topK(int)` -> `Builder` — set top-k limit
- `minP(float)` -> `Builder` — set min-p threshold
- `topNSigma(float)` -> `Builder` — set top-n-sigma threshold
- `repeatPenalty(float)` -> `Builder` — set repetition penalty
- `chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set template thinking mode
- `cachePrompt(boolean)` -> `Builder` — set prompt caching
- `swaFull(boolean)` -> `Builder` — set full-size SWA KV
- `cacheReuse(int)` -> `Builder` — set KV prefix reuse
- `gpuLayers(int)` -> `Builder` — set GPU offload layers
- `seed(int)` -> `Builder` — set generation RNG seed
- `cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers
- `cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers
- `kvUnifiedPerSlot(int)` -> `Builder` — set per-slot KV cap
- `lazyMode(String)` -> `Builder` — set tensor-read laziness
- `repeatLastN(int)` -> `Builder` — set repetition window
- `cacheTypeK(String)` -> `Builder` — set K cache type
- `cacheTypeV(String)` -> `Builder` — set V cache type
- `flashAttn(boolean)` -> `Builder` — set Flash Attention
- `batchSize(int)` -> `Builder` — set logical batch size
- `ubatchSize(int)` -> `Builder` — set physical batch size
- `threadsBatch(int)` -> `Builder` — set batch thread count
- `mainGpu(int)` -> `Builder` — set primary GPU
- `devices(String)` -> `Builder` — set device selection
- `provider(String)` -> `Builder` — set AI provider
- `remoteModel(String)` -> `Builder` — set remote model ID
- `baseUrl(String)` -> `Builder` — set API base URL
- `apiKeyEnv(String)` -> `Builder` — set API-key environment variable
- `reasoningEffort(String)` -> `Builder` — set reasoning effort
- `reasoningBudgetTokens(int)` -> `Builder` — set reasoning token budget
- `dryMultiplier(float)` -> `Builder` — set DRY multiplier
- `dryBase(float)` -> `Builder` — set DRY base
- `dryAllowedLength(int)` -> `Builder` — set DRY allowed length
- `dryPenaltyLastN(int)` -> `Builder` — set DRY look-back window
- `drySequenceBreakers(List<String>)` -> `Builder` — set DRY sequence breakers
- `stopStrings(List<String>)` -> `Builder` — set stop strings
- `build()` -> `LlamaCppJniConfig` — create immutable configuration

#### Dependencies
- `AiGenerationConfig` — default values for generation settings.
- `ConvertToRecord` — record-migration marker.
- `Collections`, `List`, `Objects` — list views, list defaults, and null validation.
- Lombok `@EqualsAndHashCode`, `@ToString`; JSpecify `@Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, and `reasoningEffort` replace null with their `AiGenerationConfig` defaults.
- Null DRY sequence-breaker and stop-string lists become empty lists.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors expose unmodifiable views.
- `Builder` is explicitly not thread-safe; intended for single-threaded construction.

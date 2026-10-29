### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:49Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, built through a fluent builder with thirty-seven named fields.

#### Purpose
Value-type configuration for the llama.cpp JNI AI provider. Replaces a thirty-seven-argument positional constructor with a fluent builder; all defaults are sourced from `AiGenerationConfig.DEFAULT_*`. Immutable after build; produced via `LlamaCppJniConfig.builder(String)`.

#### Type
Class: `public final class LlamaCppJniConfig`. Implements value semantics via Lombok `@EqualsAndHashCode`, `@ToString`, and `@ConvertToRecord` (for Java 17+ migration). No extends / implements clauses. Key annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`, `@Nullable` (on `chatTemplateEnableThinking` field).

#### Input
Constructor: private `LlamaCppJniConfig(Builder)`. Builder: `public static Builder builder(String modelPath)` — `modelPath` is required; all other fields start at `AiGenerationConfig.DEFAULT_*`. Parameters for each setter method (e.g. `contextSize(int)`, `temperature(float)`, `dryPenaltyLastN(int)`, etc.). Injected dependency: `AiGenerationConfig` for default values.

#### Output
Returned type: `LlamaCppJniConfig` from `builder(String).build()`. Accessor methods return individual fields (e.g. `modelPath() -> String`, `contextSize() -> int`, `temperature() -> float`). `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList(...)`. Side effect: produces immutable config object with full value semantics (Lombok-generated `equals`, `hashCode`, `toString`).

#### Core logic
- Builder pattern: `builder(String)` creates a new `Builder(modelPath)`; setters mutate builder state and return `this` for chaining; `build()` constructs `new LlamaCppJniConfig(this)`.
- Private constructor copies all thirty-seven builder fields into the config instance; no normalization occurs (builder already normalized).
- Defaults are resolved from `AiGenerationConfig.DEFAULT_*` constants when not explicitly set by a setter.
- `drySequenceBreakers` and `stopStrings` default to empty list; setters accept `null` to restore empty default.
- Builder is not thread-safe; config is immutable after `build()`.

#### Public API
- `builder(String modelPath) -> Builder` — creates builder with required model path and defaults.
- `build() -> LlamaCppJniConfig` — produces immutable config.
- `modelPath() -> String` — model file path.
- `contextSize() -> int` — context window size in tokens.
- `maxOutputTokens() -> int` — maximum output tokens per call.
- `temperature() -> float` — sampling temperature.
- `threads() -> int` — number of CPU threads.
- `topP() -> float` — nucleus-sampling probability threshold.
- `topK() -> int` — top-k sampling limit.
- `minP() -> float` — min-p sampling threshold.
- `topNSigma() -> float` — top-n-sigma sampling threshold.
- `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — chat-template thinking mode.
- `cachePrompt() -> boolean` — prompt caching enabled.
- `swaFull() -> boolean` — full SWA KV cache kept.
- `cacheReuse() -> int` — KV prefix-reuse minimum chunk size.
- `gpuLayers() -> int` — GPU layers to offload.
- `seed() -> int` — RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot unified KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repeat-penalty window.
- `cacheTypeK() -> String` — KV-cache data type for K.
- `cacheTypeV() -> String` — KV-cache data type for V.
- `flashAttn() -> boolean` — Flash Attention enabled.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch/prompt-processing thread count.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — AI provider.
- `remoteModel() -> String` — model ID for openai-compatible provider.
- `baseUrl() -> String` — base URL for openai-compatible API.
- `apiKeyEnv() -> String` — API key environment variable name.
- `reasoningEffort() -> String` — reasoning effort kwarg.
- `reasoningBudgetTokens() -> int` — reasoning budget tokens.
- `dryMultiplier() -> float` — DRY sampling multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY penalty look-back window.
- `drySequenceBreakers() -> List<String>` — unmodifiable DRY sequence breakers.
- `stopStrings() -> List<String>` — unmodifiable stop strings.

Count: 37 accessor methods + `builder` + `build` = 39 public members total. Builder has 37 setters + constructor + `build` = 39 members. Config has 37 accessors + private constructor = 38 members.

#### Dependencies
Imports: `java.util.Collections`, `java.util.List`, `java.util.Objects`. Referenced types: `AiGenerationConfig` (for defaults), `ConvertToRecord` (annotation), `org.jspecify.annotations.Nullable`, Lombok (`@EqualsAndHashCode`, `@ToString`, `@ConvertToRecord`).

#### Exceptions / Errors
`NullPointerException` thrown from `builder(String)` if `modelPath` is `null` (via `Objects.requireNonNull`). No other checked exceptions. Null-handling: `chatTemplateEnableThinking` field and parameter are `@Nullable`; `null` means "omit the kwarg, model's chat-template default applies." `drySequenceBreakers`/`stopStrings` setters accept `null` to restore empty default.

#### Concurrency
Builder is explicitly not thread-safe: "filled and built on one thread." The resulting `LlamaCppJniConfig` is immutable after `build()`, with value semantics from Lombok. No synchronization or volatile fields.

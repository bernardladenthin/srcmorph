### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:52:27Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, managing model paths, sampling parameters, and hardware offloading settings.

#### Purpose
* Provides a type-safe, immutable value object for llama.cpp JNI settings.
* Centralizes default values from `AiGenerationConfig` via a fluent builder.

#### Type
Final class. Annotated with `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.

#### Input
* `modelPath` (String): Required via `builder(String)`.
* `Builder` instance: Used by the private constructor to populate 42 fields.
* `AiGenerationConfig` constants: Used as default values in the `Builder`.

#### Output
* Immutable `LlamaCppJniConfig` instance.
* Individual field values via record-style accessor methods.
* Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
* **Immutability**: All fields are final; state is set once during construction from a `Builder`.
* **Defaulting**: The `Builder` initializes 41 fields with `AiGenerationConfig.DEFAULT_*` constants, ensuring no Java zero-defaults are used unless explicitly intended.
* **Null Handling**: 
    * `modelPath` is validated as non-null via `Objects.requireNonNull`.
    * Specific builder setters (e.g., `lazyMode`, `provider`) restore defaults if `null` is passed.
    * `chatTemplateEnableThinking` uses a nullable `Boolean` to distinguish between "unset" (null) and "explicitly disabled" (false).
* **Value Semantics**: Uses Lombok to implement `equals`, `hashCode`, and `toString` across all fields.

#### Public API
`builder(String) -> Builder` initializes builder with required model path.
`modelPath() -> String` returns GGUF model file path.
`contextSize() -> int` returns context window size.
`maxOutputTokens() -> int` returns max output tokens.
`temperature() -> float` returns sampling temperature.
`threads() -> int` returns CPU thread count.
`topP() -> float` returns nucleus-sampling threshold.
`topK() -> int` returns top-k sampling limit.
`minP() -> float` returns min-p threshold.
`topNSigma() -> float` returns top-n-sigma threshold.
`repeatPenalty() -> float` returns repetition penalty.
`chatTemplateEnableThinking() -> Boolean` returns thinking mode or null.
`cachePrompt() -> boolean` returns prompt caching status.
`swaFull() -> boolean` returns SWA KV cache status.
`cacheReuse() -> int` returns cache-reuse chunk size.
`gpuLayers() -> int` returns GPU offload layer count.
`seed() -> int` returns RNG seed.
`cpuMoeLayers() -> int` returns CPU MoE layer count.
`cpuFfnLayers() -> int` returns CPU FFN layer count.
`kvUnifiedPerSlot() -> int` returns unified KV context cap.
`lazyMode() -> String` returns tensor-read laziness mode.
`repeatLastN() -> int` returns repeat-penalty window.
`cacheTypeK() -> String` returns KV-cache type for K.
`cacheTypeV() -> String` returns KV-cache type for V.
`flashAttn() -> boolean` returns Flash Attention status.
`batchSize() -> int` returns logical batch size.
`ubatchSize() -> int` returns physical batch size.
`threadsBatch() -> int` returns batch processing thread count.
`mainGpu() -> int` returns primary GPU index.
`devices() -> String` returns device selection list.
`provider() -> String` returns AI provider identifier.
`remoteModel() -> String` returns OpenAI-compatible model ID.
`baseUrl() -> String` returns OpenAI-compatible API base URL.
`apiKeyEnv() -> String` returns API key environment variable name.
`reasoningEffort() -> String` returns reasoning effort level.
`reasoningBudgetTokens() -> int` returns reasoning token budget.
`dryMultiplier() -> float` returns DRY sampling multiplier.
`dryBase() -> float` returns DRY base.
`dryAllowedLength() -> int` returns DRY allowed length.
`dryPenaltyLastN() -> int` returns DRY penalty look-back window.
`drySequenceBreakers() -> List<String>` returns unmodifiable DRY breakers.
`stopStrings() -> List<String>` returns unmodifiable stop strings.

**Builder API:**
`build() -> LlamaCppJniConfig` creates the immutable config.
`contextSize..stopStrings (41 total)` fluent setters for configuration fields.

#### Dependencies
* `net.ladenthin.srcmorph.config.AiGenerationConfig` (Defaults)
* `net.ladenthin.srcmorph.support.ConvertToRecord` (Annotation)
* `org.jspecify.annotations.Nullable` (Nullability)
* `lombok.EqualsAndHashCode`, `lombok.ToString` (Boilerplate)
* `java.util.Collections`, `java.util.List`, `java.util.Objects` (Utilities)

#### Exceptions / Errors
* `NullPointerException`: Thrown by `builder(String)` if `modelPath` is null.

#### Concurrency
* `LlamaCppJniConfig`: Thread-safe due to immutability.
* `Builder`: Not thread-safe; intended for single-threaded construction.

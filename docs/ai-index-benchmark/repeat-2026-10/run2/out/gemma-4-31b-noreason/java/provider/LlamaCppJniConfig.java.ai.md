### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:55:30Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, managing model paths, sampling parameters, and hardware offloading settings.

#### Purpose
* Provides a type-safe, immutable value object for LLM generation settings.
* Centralizes default values from `AiGenerationConfig` for the llama.cpp JNI provider.

#### Type
* Final class.
* Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
* `Builder` instance (via private constructor).
* `modelPath` (String, required via `builder()` method).
* Various primitive and String parameters via `Builder` setters.
* `List<String>` for `drySequenceBreakers` and `stopStrings`.

#### Output
* Immutable `LlamaCppJniConfig` instance.
* Accessor methods returning the configured values.
* Unmodifiable lists for sequence breakers and stop strings.

#### Core logic
* **Immutability**: Fields are final; state is set once during construction from a `Builder`.
* **Defaulting**: The `Builder` initializes 41 fields using constants from `AiGenerationConfig`.
* **Null Handling**: 
    * `modelPath` is validated as non-null via `Objects.requireNonNull`.
    * Specific String setters in `Builder` restore defaults if `null` is passed.
    * `chatTemplateEnableThinking` allows `null` to signal omission of the keyword argument.
* **Collection Safety**: `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList`.

#### Public API
* `builder(String) -> Builder` starts configuration with required model path.
* `modelPath() -> String` returns GGUF file path.
* `contextSize() -> int` returns context window size.
* `maxOutputTokens() -> int` returns max output tokens.
* `temperature() -> float` returns sampling temperature.
* `threads() -> int` returns CPU thread count.
* `topP() -> float` returns nucleus-sampling threshold.
* `topK() -> int` returns top-k limit.
* `minP() -> float` returns min-p threshold.
* `topNSigma() -> float` returns top-n-sigma threshold.
* `repeatPenalty() -> float` returns repetition penalty.
* `chatTemplateEnableThinking() -> Boolean` returns thinking mode or null.
* `cachePrompt() -> boolean` returns prompt caching status.
* `swaFull() -> boolean` returns SWA KV cache status.
* `cacheReuse() -> int` returns KV prefix-reuse chunk size.
* `gpuLayers() -> int` returns GPU offload layer count.
* `seed() -> int` returns RNG seed.
* `cpuMoeLayers() -> int` returns CPU MoE layer count.
* `cpuFfnLayers() -> int` returns CPU FFN layer count.
* `kvUnifiedPerSlot() -> int` returns unified KV context cap.
* `lazyMode() -> String` returns tensor-read laziness mode.
* `repeatLastN() -> int` returns repeat-penalty window.
* `cacheTypeK() -> String` returns KV-cache type for K.
* `cacheTypeV() -> String` returns KV-cache type for V.
* `flashAttn() -> boolean` returns Flash Attention status.
* `batchSize() -> int` returns logical batch size.
* `ubatchSize() -> int` returns physical batch size.
* `threadsBatch() -> int` returns batch processing thread count.
* `mainGpu() -> int` returns primary GPU index.
* `devices() -> String` returns device selection list.
* `provider() -> String` returns AI provider identifier.
* `remoteModel() -> String` returns remote model ID.
* `baseUrl() -> String` returns OpenAI-compatible API URL.
* `apiKeyEnv() -> String` returns API key environment variable name.
* `reasoningEffort() -> String` returns reasoning effort level.
* `reasoningBudgetTokens() -> int` returns reasoning token budget.
* `dryMultiplier() -> float` returns DRY sampling multiplier.
* `dryBase() -> float` returns DRY base.
* `dryAllowedLength() -> int` returns DRY allowed length.
* `dryPenaltyLastN() -> int` returns DRY penalty window.
* `drySequenceBreakers() -> List<String>` returns unmodifiable sequence breakers.
* `stopStrings() -> List<String>` returns unmodifiable stop strings.
* `Builder.build() -> LlamaCppJniConfig` creates the immutable config.
* `Builder.contextSize..Builder.stopStrings (42 total)` fluent setters for configuration.

#### Dependencies
* `net.ladenthin.srcmorph.config.AiGenerationConfig` (Defaults).
* `net.ladenthin.srcmorph.support.ConvertToRecord` (Marker).
* `java.util.Collections`, `java.util.List`, `java.util.Objects`.
* `org.jspecify.annotations.Nullable`.
* `lombok.EqualsAndHashCode`, `lombok.ToString`.

#### Exceptions / Errors
* `NullPointerException` thrown by `builder(String)` if `modelPath` is null.

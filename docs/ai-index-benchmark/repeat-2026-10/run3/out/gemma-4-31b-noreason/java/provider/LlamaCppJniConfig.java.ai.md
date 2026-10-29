### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:28:23Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an immutable configuration object for the llama.cpp JNI provider, managing model paths, sampling parameters, and hardware offloading settings.

#### Purpose
* Encapsulates all configuration parameters required to initialize and run a llama.cpp JNI instance.
* Ensures type-safe, immutable value semantics for AI generation settings.

#### Type
* Final class.
* Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
* `Builder` instance (via private constructor).
* `modelPath` (String, required via `builder()` method).
* Various primitive and String parameters via `Builder` setters.
* `AiGenerationConfig` constants used as default values in `Builder`.

#### Output
* Immutable `LlamaCppJniConfig` instance.
* Individual configuration values via record-style accessor methods.
* Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
* **Immutability**: All fields are `final`; state is set once during construction from a `Builder`.
* **Defaulting**: The `Builder` initializes 41 fields using constants from `AiGenerationConfig` to ensure consistent defaults.
* **Validation**: `modelPath` is enforced as non-null via `Objects.requireNonNull` at the start of the builder chain.
* **Null Handling**: Specific setters in `Builder` (e.g., `lazyMode`, `provider`) restore defaults if `null` is passed.
* **Collection Safety**: Returns `Collections.unmodifiableList` for list-based configurations to prevent mutation of the internal state.

#### Public API
* `builder(String) -> Builder`: Entry point for configuration creation.
* `modelPath() -> String`: Returns GGUF model path.
* `contextSize() -> int`: Returns context window size.
* `maxOutputTokens() -> int`: Returns max output tokens.
* `temperature() -> float`: Returns sampling temperature.
* `threads() -> int`: Returns CPU thread count.
* `topP() -> float`: Returns nucleus-sampling threshold.
* `topK() -> int`: Returns top-k limit.
* `minP() -> float`: Returns min-p threshold.
* `topNSigma() -> float`: Returns top-n-sigma threshold.
* `repeatPenalty() -> float`: Returns repetition penalty.
* `chatTemplateEnableThinking() -> Boolean`: Returns thinking mode toggle.
* `cachePrompt() -> boolean`: Returns prompt caching status.
* `swaFull() -> boolean`: Returns SWA KV cache status.
* `cacheReuse() -> int`: Returns cache-reuse chunk size.
* `gpuLayers() -> int`: Returns GPU offload layer count.
* `seed() -> int`: Returns RNG seed.
* `cpuMoeLayers() -> int`: Returns CPU MoE layer count.
* `cpuFfnLayers() -> int`: Returns CPU FFN layer count.
* `kvUnifiedPerSlot() -> int`: Returns unified KV context cap.
* `lazyMode() -> String`: Returns tensor-read laziness mode.
* `repeatLastN() -> int`: Returns repeat-penalty window.
* `cacheTypeK() -> String`: Returns KV-cache type for K.
* `cacheTypeV() -> String`: Returns KV-cache type for V.
* `flashAttn() -> boolean`: Returns Flash Attention status.
* `batchSize() -> int`: Returns logical batch size.
* `ubatchSize() -> int`: Returns physical batch size.
* `threadsBatch() -> int`: Returns batch processing thread count.
* `mainGpu() -> int`: Returns primary GPU index.
* `devices() -> String`: Returns device selection list.
* `provider() -> String`: Returns AI provider identifier.
* `remoteModel() -> String`: Returns remote model ID.
* `baseUrl() -> String`: Returns OpenAI-compatible API URL.
* `apiKeyEnv() -> String`: Returns API key environment variable name.
* `reasoningEffort() -> String`: Returns reasoning effort level.
* `reasoningBudgetTokens() -> int`: Returns reasoning token budget.
* `dryMultiplier() -> float`: Returns DRY sampling multiplier.
* `dryBase() -> float`: Returns DRY base.
* `dryAllowedLength() -> int`: Returns DRY allowed length.
* `dryPenaltyLastN() -> int`: Returns DRY penalty window.
* `drySequenceBreakers() -> List<String>`: Returns unmodifiable sequence breakers.
* `stopStrings() -> List<String>`: Returns unmodifiable stop strings.

**Builder API:**
* `build() -> LlamaCppJniConfig`: Finalizes the configuration object.
* `contextSize..stopStrings (41 total)`: Fluent setters for each configuration field.

#### Dependencies
* `java.util.Collections`
* `java.util.List`
* `java.util.Objects`
* `net.ladenthin.srcmorph.config.AiGenerationConfig`
* `net.ladenthin.srcmorph.support.ConvertToRecord`
* `org.jspecify.annotations.Nullable`
* `lombok.EqualsAndHashCode`
* `lombok.ToString`

#### Exceptions / Errors
* `NullPointerException`: Thrown by `builder(String)` if `modelPath` is null.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:29Z
- G: 
- A: 0.0.0
- X: file
---
> Configures and validates immutable parameters for the llama.cpp JNI AI generation provider.

#### Purpose
*   Defines an immutable value type for passing 37 specific configuration options to the llama.cpp native library.
*   Provides a Builder pattern to enforce required fields and apply default values from `AiGenerationConfig`.

#### Type
*   `public final class LlamaCppJniConfig`
*   Annotated with `@ToString`, `@EqualsAndHashCode`, and `@ConvertToRecord`.
*   Contains 37 private final fields (String, int, float, Boolean, List).
*   Inner class `public static final class Builder` manages construction.

#### Input
*   **Constructor**: Private `Builder` instance containing all 37 configuration values.
*   **Static Method**: `builder(String modelPath)` requires a non-null model path string.
*   **Builder Setters**: Optional parameters for context size, temperature, GPU layers, sampling thresholds (topP, topK, minP), caching, batching, and DRY (Don't Repeat Yourself) sampling settings.
*   **Defaults**: All optional fields initialize to `AiGenerationConfig.DEFAULT_*` values within the Builder.

#### Output
*   **Return**: Immutable `LlamaCppJniConfig` instance via `build()`.
*   **Accessors**: 37 public methods (e.g., `modelPath()`, `temperature()`, `gpuLayers()`) returning field values.
*   **Collections**: `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.
*   **Side Effects**: None; the class is stateless after construction.

#### Core logic
*   **Enforced Construction**: Prevents instantiation via positional constructor to ensure all 37 arguments are named and defaults are applied.
*   **Default Application**: `Builder` initializes all optional fields to `AiGenerationConfig` defaults before allowing overrides.
*   **Null Handling**: `modelPath` is strictly required; `null` values for optional String/List fields in setters restore defaults or empty lists.
*   **Immutability**: All fields are `final`; the resulting object cannot be modified after creation.
*   **Value Semantics**: Lombok generates `equals`, `hashCode`, and `toString` based on all 37 fields.

#### Public API
*   `builder(String modelPath) -> Builder`
    *   Creates a new builder with the required model path.
*   `modelPath() -> String`
    *   Returns the GGUF model file path.
*   `contextSize() -> int`
    *   Returns the context window size in tokens.
*   `maxOutputTokens() -> int`
    *   Returns the maximum output token limit.
*   `temperature() -> float`
    *   Returns the sampling temperature.
*   `threads() -> int`
    *   Returns the number of CPU threads.
*   `topP() -> float`
    *   Returns the nucleus-sampling probability threshold.
*   `topK() -> int`
    *   Returns the top-k sampling limit.
*   `minP() -> float`
    *   Returns the min-p sampling threshold.
*   `topNSigma() -> float`
    *   Returns the top-n-sigma sampling threshold.
*   `repeatPenalty() -> float`
    *   Returns the repetition penalty value.
*   `chatTemplateEnableThinking() -> Boolean`
    *   Returns the chat-template thinking mode setting.
*   `cachePrompt() -> boolean`
    *   Returns whether prompt caching is enabled.
*   `swaFull() -> boolean`
    *   Returns whether full-size SWA KV cache is kept.
*   `cacheReuse() -> int`
    *   Returns the KV prefix-reuse chunk size.
*   `gpuLayers() -> int`
    *   Returns the number of layers offloaded to GPU.
*   `seed() -> int`
    *   Returns the RNG seed for generation.
*   `cpuMoeLayers() -> int`
    *   Returns the number of MoE layers on CPU.
*   `cpuFfnLayers() -> int`
    *   Returns the number of FFN layers on CPU.
*   `kvUnifiedPerSlot() -> int`
    *   Returns the per-slot unified KV context cap.
*   `lazyMode() -> String`
    *   Returns the tensor-read laziness mode.
*   `repeatLastN() -> int`
    *   Returns the repeat-penalty window size.
*   `cacheTypeK() -> String`
    *   Returns the KV-cache data type for K.
*   `cacheTypeV() -> String`
    *   Returns the KV-cache data type for V.
*   `flashAttn() -> boolean`
    *   Returns whether Flash Attention is enabled.
*   `batchSize() -> int`
    *   Returns the logical batch size.
*   `ubatchSize() -> int`
    *   Returns the physical batch size.
*   `threadsBatch() -> int`
    *   Returns the batch processing thread count.
*   `mainGpu() -> int`
    *   Returns the primary GPU index.
*   `devices() -> String`
    *   Returns the device selection list.
*   `provider() -> String`
    *   Returns the AI provider identifier.
*   `remoteModel() -> String`
    *   Returns the remote model ID for OpenAI-compatible providers.
*   `baseUrl() -> String`
    *   Returns the base URL for the API.
*   `apiKeyEnv() -> String`
    *   Returns the environment variable name for the API key.
*   `reasoningEffort() -> String`
    *   Returns the reasoning effort level.
*   `reasoningBudgetTokens() -> int`
    *   Returns the reasoning token budget.
*   `dryMultiplier() -> float`
    *   Returns the DRY sampling multiplier.
*   `dryBase() -> float`
    *   Returns the DRY base value.
*   `dryAllowedLength() -> int`
    *   Returns the DRY allowed length.
*   `dryPenaltyLastN() -> int`
    *   Returns the DRY penalty look-back window.
*   `drySequenceBreakers() -> List<String>`
    *   Returns the list of DRY sequence breakers.
*   `stopStrings() -> List<String>`
    *   Returns the list of stop strings.
*   `contextSize(int) -> Builder`
    *   Sets the context window size.
*   `maxOutputTokens(int) -> Builder`
    *   Sets the maximum output tokens.
*   `temperature(float) -> Builder`
    *   Sets the sampling temperature.
*   `threads(int) -> Builder`
    *   Sets the CPU thread count.
*   `topP(float) -> Builder`
    *   Sets the top-p threshold.
*   `topK(int) -> Builder`
    *   Sets the top-k limit.
*   `minP(float) -> Builder`
    *   Sets the min-p threshold.
*   `topNSigma(float) -> Builder`
    *   Sets the top-n-sigma threshold.
*   `repeatPenalty(float) -> Builder`
    *   Sets the repetition penalty.
*   `chatTemplateEnableThinking(Boolean) -> Builder`
    *   Sets the chat-template thinking mode.
*   `cachePrompt(boolean) -> Builder`
    *   Enables or disables prompt caching.
*   `swaFull(boolean) -> Builder`
    *   Sets the full SWA KV cache flag.
*   `cacheReuse(int) -> Builder`
    *   Sets the cache reuse chunk size.
*   `gpuLayers(int) -> Builder`
    *   Sets the number of GPU layers.
*   `seed(int) -> Builder`
    *   Sets the RNG seed.
*   `cpuMoeLayers(int) -> Builder`
    *   Sets the CPU MoE layer count.
*   `cpuFfnLayers(int) -> Builder`
    *   Sets the CPU FFN layer count.
*   `kvUnifiedPerSlot(int) -> Builder`
    *   Sets the KV unified per slot cap.
*   `lazyMode(String) -> Builder`
    *   Sets the tensor-read laziness mode.
*   `repeatLastN(int) -> Builder`
    *   Sets the repeat-penalty window.
*   `cacheTypeK(String) -> Builder`
    *   Sets the KV cache type for K.
*   `cacheTypeV(String) -> Builder`
    *   Sets the KV cache type for V.
*   `flashAttn(boolean) -> Builder`
    *   Enables or disables Flash Attention.
*   `batchSize(int) -> Builder`
    *   Sets the logical batch size.
*   `ubatchSize(int) -> Builder`
    *   Sets the physical batch size.
*   `threadsBatch(int) -> Builder`
    *   Sets the batch thread count.
*   `mainGpu(int) -> Builder`
    *   Sets the main GPU index.
*   `devices(String) -> Builder`
    *   Sets the device selection string.
*   `provider(String) -> Builder`
    *   Sets the AI provider string.
*   `remoteModel(String) -> Builder`
    *   Sets the remote model ID.
*   `baseUrl(String) -> Builder`
    *   Sets the API base URL.
*   `apiKeyEnv(String) -> Builder`
    *   Sets the API key environment variable name.
*   `reasoningEffort(String) -> Builder`
    *   Sets the reasoning effort string.
*   `reasoningBudgetTokens(int) -> Builder`
    *   Sets the reasoning budget tokens.
*   `dryMultiplier(float) -> Builder`
    *   Sets the DRY multiplier.
*   `dryBase(float) -> Builder`
    *   Sets the DRY base.
*   `dryAllowedLength(int) -> Builder`
    *   Sets the DRY allowed length.
*   `dryPenaltyLastN(int) -> Builder`
    *   Sets the DRY penalty last N.
*   `drySequenceBreakers(List<String>) -> Builder`
    *   Sets the DRY sequence breakers list.
*   `stopStrings(List<String>) -> Builder`
    *   Sets the stop strings list.
*   `build() -> LlamaCppJniConfig`
    *   Constructs the immutable configuration object.

#### Dependencies
*   `net.ladenthin.srcmorph.config.AiGenerationConfig` (Default values)
*   `net.ladenthin.srcmorph.support.ConvertToRecord` (Annotation)
*   `org.jspecify.annotations.Nullable` (Annotation)
*   `lombok.EqualsAndHashCode`, `lombok.ToString` (Annotations)
*   `java.util.Collections`, `java.util.List`, `java.util.Objects` (Standard Library)

#### Exceptions / Errors
*   `NullPointerException`: Thrown by `builder(String)` if `modelPath` is null.
*   `NullPointerException`: Thrown by `Builder` constructor if `modelPath` is null.
*   Null handling: Optional String/List setters restore defaults if `null` is passed.

#### Concurrency
*   **Immutable**: The `LlamaCppJniConfig` instance is thread-safe after construction.
*   **Builder**: The `Builder` class is not thread-safe and intended for single-threaded use.

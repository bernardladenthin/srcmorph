### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:04:08Z
- G: 
- A: 0.0.0
- X: file
---
> Configures and validates parameters for the llama.cpp JNI provider to control AI model generation behavior, hardware offloading, and sampling strategies.

#### Purpose
*   Provides an immutable configuration value object for the `LlamaCppJni` AI provider.
*   Centralizes default values from `AiGenerationConfig` and enforces required fields via a Builder pattern.

#### Type
*   `public final class LlamaCppJniConfig`
*   Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok).
*   Implements `ConvertToRecord` interface marker.
*   Contains 37 private final fields (String, int, float, Boolean, List).
*   Inner static class `Builder` with 37 mutable fields initialized to `AiGenerationConfig.DEFAULT_*`.

#### Input
*   **Constructor**: Private `Builder` instance containing all configuration state.
*   **Static Method**: `builder(String modelPath)` requires a non-null model file path.
*   **Builder Setters**: 36 optional setters for generation parameters (e.g., `temperature`, `gpuLayers`, `stopStrings`).
*   **Dependencies**: `AiGenerationConfig` for default values; `Collections` for empty lists.

#### Output
*   **Return**: `LlamaCppJniConfig` instance containing immutable configuration state.
*   **Accessors**: 37 public methods (e.g., `modelPath()`, `temperature()`, `stopStrings()`) returning field values.
*   **Side Effects**: None; class is immutable.
*   **Lists**: `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.

#### Core logic
*   **Initialization**: `builder(String)` creates a `Builder` instance with `modelPath` and all other fields set to `AiGenerationConfig` defaults.
*   **Validation**: `modelPath` is validated for null in the `Builder` constructor; all other fields default to safe values.
*   **Null Handling**: String setters (e.g., `lazyMode`, `devices`) restore defaults if `null` is passed; List setters treat `null` as empty.
*   **Construction**: `build()` instantiates `LlamaCppJniConfig` by copying all `Builder` fields to final private fields.
*   **Immutability**: No setters exist on `LlamaCppJniConfig`; state is fixed upon construction.

#### Public API
*   `builder(String) -> Builder` Starts configuration with required model path.
*   `modelPath() -> String` Returns the GGUF model file path.
*   `contextSize() -> int` Returns the context window size in tokens.
*   `maxOutputTokens() -> int` Returns the maximum output tokens per call.
*   `temperature() -> float` Returns the sampling temperature.
*   `threads() -> int` Returns the number of CPU threads.
*   `topP() -> float` Returns the nucleus-sampling probability threshold.
*   `topK() -> int` Returns the top-k sampling limit.
*   `minP() -> float` Returns the min-p sampling threshold.
*   `topNSigma() -> float` Returns the top-n-sigma sampling threshold.
*   `repeatPenalty() -> float` Returns the repetition penalty.
*   `chatTemplateEnableThinking() -> Boolean` Returns the chat-template thinking mode.
*   `cachePrompt() -> boolean` Returns whether prompt caching is enabled.
*   `swaFull() -> boolean` Returns whether full-size SWA KV cache is kept.
*   `cacheReuse() -> int` Returns the KV prefix-reuse minimum chunk size.
*   `gpuLayers() -> int` Returns the number of model layers to offload to GPU.
*   `seed() -> int` Returns the RNG seed used for generation.
*   `cpuMoeLayers() -> int` Returns the number of MoE layers kept on CPU.
*   `cpuFfnLayers() -> int` Returns the number of FFN layers kept on CPU.
*   `kvUnifiedPerSlot() -> int` Returns the per-slot unified KV context cap.
*   `lazyMode() -> String` Returns the tensor-read laziness mode.
*   `repeatLastN() -> int` Returns the repeat-penalty window.
*   `cacheTypeK() -> String` Returns the KV-cache data type for K.
*   `cacheTypeV() -> String` Returns the KV-cache data type for V.
*   `flashAttn() -> boolean` Returns whether Flash Attention is enabled.
*   `batchSize() -> int` Returns the logical batch size.
*   `ubatchSize() -> int` Returns the physical batch size.
*   `threadsBatch() -> int` Returns the batch/prompt-processing thread count.
*   `mainGpu() -> int` Returns the primary GPU index.
*   `devices() -> String` Returns the device selection list.
*   `provider() -> String` Returns the AI provider type.
*   `remoteModel() -> String` Returns the model ID for remote providers.
*   `baseUrl() -> String` Returns the base URL of the OpenAI-compatible API.
*   `apiKeyEnv() -> String` Returns the environment variable name for the API key.
*   `reasoningEffort() -> String` Returns the reasoning-effort kwarg value.
*   `reasoningBudgetTokens() -> int` Returns the reasoning/think-token budget.
*   `dryMultiplier() -> float` Returns the DRY sampling multiplier.
*   `dryBase() -> float` Returns the DRY base value.
*   `dryAllowedLength() -> int` Returns the DRY allowed length.
*   `dryPenaltyLastN() -> int` Returns the DRY penalty look-back window.
*   `drySequenceBreakers() -> List<String>` Returns unmodifiable DRY sequence breakers.
*   `stopStrings() -> List<String>` Returns unmodifiable stop strings.
*   `contextSize(int) -> Builder` Sets context window size.
*   `maxOutputTokens(int) -> Builder` Sets maximum output tokens.
*   `temperature(float) -> Builder` Sets sampling temperature.
*   `threads(int) -> Builder` Sets number of CPU threads.
*   `topP(float) -> Builder` Sets nucleus-sampling threshold.
*   `topK(int) -> Builder` Sets top-k sampling limit.
*   `minP(float) -> Builder` Sets min-p sampling threshold.
*   `topNSigma(float) -> Builder` Sets top-n-sigma threshold.
*   `repeatPenalty(float) -> Builder` Sets repetition penalty.
*   `chatTemplateEnableThinking(Boolean) -> Builder` Sets chat-template thinking mode.
*   `cachePrompt(boolean) -> Builder` Enables/disables prompt caching.
*   `swaFull(boolean) -> Builder` Enables/disables full SWA KV cache.
*   `cacheReuse(int) -> Builder` Sets KV prefix-reuse chunk size.
*   `gpuLayers(int) -> Builder` Sets GPU offload layers.
*   `seed(int) -> Builder` Sets RNG seed.
*   `cpuMoeLayers(int) -> Builder` Sets CPU MoE layers.
*   `cpuFfnLayers(int) -> Builder` Sets CPU FFN layers.
*   `kvUnifiedPerSlot(int) -> Builder` Sets per-slot KV context cap.
*   `lazyMode(String) -> Builder` Sets tensor-read laziness.
*   `repeatLastN(int) -> Builder` Sets repeat-penalty window.
*   `cacheTypeK(String) -> Builder` Sets KV-cache type for K.
*   `cacheTypeV(String) -> Builder` Sets KV-cache type for V.
*   `flashAttn(boolean) -> Builder` Enables/disables Flash Attention.
*   `batchSize(int) -> Builder` Sets logical batch size.
*   `ubatchSize(int) -> Builder` Sets physical batch size.
*   `threadsBatch(int) -> Builder` Sets batch processing thread count.
*   `mainGpu(int) -> Builder` Sets primary GPU index.
*   `devices(String) -> Builder` Sets device selection.
*   `provider(String) -> Builder` Sets AI provider type.
*   `remoteModel(String) -> Builder` Sets remote model ID.
*   `baseUrl(String) -> Builder` Sets API base URL.
*   `apiKeyEnv(String) -> Builder` Sets API key environment variable.
*   `reasoningEffort(String) -> Builder` Sets reasoning effort.
*   `reasoningBudgetTokens(int) -> Builder` Sets reasoning token budget.
*   `dryMultiplier(float) -> Builder` Sets DRY multiplier.
*   `dryBase(float) -> Builder` Sets DRY base.
*   `dryAllowedLength(int) -> Builder` Sets DRY allowed length.
*   `dryPenaltyLastN(int) -> Builder` Sets DRY penalty window.
*   `drySequenceBreakers(List<String>) -> Builder` Sets DRY sequence breakers.
*   `stopStrings(List<String>) -> Builder` Sets stop strings.
*   `build() -> LlamaCppJniConfig` Constructs the immutable configuration.

#### Dependencies
*   `java.util.Collections`
*   `java.util.List`
*   `java.util.Objects`
*   `lombok.EqualsAndHashCode`
*   `lombok.ToString`
*   `net.ladenthin.srcmorph.config.AiGenerationConfig`
*   `net.ladenthin.srcmorph.support.ConvertToRecord`
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `NullPointerException`: Thrown by `builder(String)` if `modelPath` is null.
*   `null` handling: String setters restore defaults if `null` is passed; List setters treat `null` as empty.

#### Concurrency
*   **Immutable**: `LlamaCppJniConfig` is thread-safe after construction.
*   **Builder**: `Builder` is not thread-safe; intended for single-threaded use.

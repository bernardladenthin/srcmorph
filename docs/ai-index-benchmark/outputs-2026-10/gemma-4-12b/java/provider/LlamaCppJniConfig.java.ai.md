### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:47:03Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides an immutable configuration object for the llama.cpp JNI provider, including model paths, hardware offloading settings, and sampling parameters.

#### Purpose
*   Holds configuration parameters for the llama.cpp JNI integration.
*   Provides a fluent builder to construct the configuration with default values.

#### Type
final class; @ConvertToRecord, @ToString, @EqualsAndHashCode.

#### Input
*   `Builder` instance (via private constructor).
*   `modelPath` (String) via `Builder.builder(String)`.
*   Various primitive and object types (int, float, String, List, Boolean) via `Builder` setter methods.

#### Output
*   Immutable `LlamaCppJniConfig` instance.
*   Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
*   **Immutability**: Fields are final; the class is designed to be record-shaped for future migration.
*   **Builder Pattern**: Uses a nested `Builder` class to handle 37 parameters, ensuring all fields are initialized with `AiGenerationConfig` defaults.
*   **Null Handling**: The builder converts `null` inputs for several fields back to their respective `AiGenerationConfig` defaults.
*   **Validation**: `modelPath` is required and checked for nullity during builder instantiation.

#### Public API
*   `builder(String modelPath) -> Builder` Starts the fluent construction process.
*   `modelPath() -> String` Returns the GGUF model file path.
*   `contextSize() -> int` Returns the context window size.
*   `maxOutputTokens() -> int` Returns the maximum output tokens.
*   `temperature() -> float` Returns the sampling temperature.
*   `threads() -> int` Returns the CPU thread count.
*   `topP() -> float` Returns the nucleus-sampling threshold.
*   `topK() -> int` Returns the top-k sampling limit.
*   `minP() -> float` Returns the min-p sampling threshold.
*   `topNSigma() -> float` Returns the top-n-sigma threshold.
*   `repeatPenalty() -> float` Returns the repetition penalty.
*   `chatTemplateEnableThinking() -> Boolean` Returns the thinking mode flag.
*   `cachePrompt() -> boolean` Returns the prompt caching status.
*   `swaFull() -> boolean` Returns the SWA KV cache status.
*   `cacheReuse() -> int` Returns the KV prefix-reuse chunk size.
*   `gpuLayers() -> int` Returns the number of GPU offloaded layers.
*   `seed() -> int` Returns the RNG seed.
*   `cpuMoeLayers() -> int` Returns the CPU MoE layers.
*   `cpuFfnLayers() -> int` Returns the CPU FFN layers.
*   `kvUnifiedPerSlot() -> int` Returns the unified KV context cap.
*   `lazyMode() -> String` Returns the tensor-read laziness mode.
*   `repeatLastN() -> int` Returns the repeat-penalty window.
*   `cacheTypeK() -> String` Returns the KV-cache type for K.
*   `cacheTypeV() -> String` Returns the KV-cache type for V.
*   `flashAttn() -> boolean` Returns the Flash Attention status.
*   `batchSize() -> int` Returns the logical batch size.
*   `ubatchSize() -> int` Returns the physical batch size.
*   `threadsBatch() -> int` Returns the batch-processing thread count.
*   `mainGpu() -> int` Returns the primary GPU index.
*   `devices() -> String` Returns the device selection string.
*   `provider() -> String` Returns the AI provider type.
*   `remoteModel() -> String` Returns the remote model ID.
*   `baseUrl() -> String` Returns the OpenAI-compatible base URL.
*   `apiKeyEnv() -> String` Returns the API key environment variable name.
*   `reasoningEffort() -> String` Returns the reasoning effort level.
*   `reasoningBudgetTokens() -> int` Returns the reasoning token budget.
*   `dryMultiplier() -> float` Returns the DRY sampling multiplier.
*   `dryBase() -> float` Returns the DRY base value.
*   `dryAllowedLength() -> int` Returns the DRY allowed length.
*   `dryPenaltyLastN() -> int` Returns the DRY penalty look-back window.
*   `drySequenceBreakers() -> List<String>` Returns unmodifiable DRY breakers.
*   `stopStrings() -> List<String>` Returns unmodifiable stop strings.
*   `Builder.contextSize(int) -> Builder` Sets context size.
*   `Builder.maxOutputTokens(int) -> Builder` Sets max output tokens.
*   `Builder.temperature(float) -> Builder` Sets temperature.
*   `Builder.threads(int) -> Builder` Sets thread count.
*   `Builder.topP(float) -> Builder` Sets top-p.
*   `Builder.topK(int) -> Builder` Sets top-k.
*   `Builder.minP(float) -> Builder` Sets min-p.
*   `Builder.topNSigma(float) -> Builder` Sets top-n-sigma.
*   `Builder.repeatPenalty(float) -> Builder` Sets repeat penalty.
*   `Builder.chatTemplateEnableThinking(Boolean) -> Builder` Sets thinking mode.
*   `Builder.cachePrompt(boolean) -> Builder` Sets prompt caching.
*   `Builder.swaFull(boolean) -> Builder` Sets SWA full cache.
*   `Builder.cacheReuse(int) -> Builder` Sets cache reuse size.
*   `Builder.gpuLayers(int) -> Builder` Sets GPU layers.
*   `Builder.seed(int) -> Builder` Sets RNG seed.
*   `Builder.cpuMoeLayers(int) -> Builder` Sets CPU MoE layers.
*   `Builder.cpuFfnLayers(int) -> Builder` Sets CPU FFN layers.
*   `Builder.kvUnifiedPerSlot(int) -> Builder` Sets KV unified cap.
*   `Builder.lazyMode(String) -> Builder` Sets laziness mode.
*   `Builder.repeatLastN(int) -> Builder` Sets repeat-last-n window.
*   `Builder.cacheTypeK(String) -> Builder` Sets cache type K.
*   `Builder.cacheTypeV(String) -> Builder` Sets cache type V.
*   `Builder.flashAttn(boolean) -> Builder` Sets Flash Attention.
*   `Builder.batchSize(int) -> Builder` Sets logical batch size.
*   `Builder.ubatchSize(int) -> Builder` Sets physical batch size.
*   `Builder.threadsBatch(int) -> Builder` Sets batch threads.
*   `Builder.mainGpu(int) -> Builder` Sets main GPU index.
*   `Builder.devices(String) -> Builder` Sets device selection.
*   `Builder.provider(String) -> Builder` Sets provider type.
*   `Builder.remoteModel(String) -> Builder` Sets remote model ID.
*   `Builder.baseUrl(String) -> Builder` Sets base URL.
*   `Builder.apiKeyEnv(String) -> Builder` Sets API key env name.
*   `Builder.reasoningEffort(String) -> Builder` Sets reasoning effort.
*   `Builder.reasoningBudgetTokens(int) -> Builder` Sets reasoning budget.
*   `Builder.dryMultiplier(float) -> Builder` Sets DRY multiplier.
*   `Builder.dryBase(float) -> Builder` Sets DRY base.
*   `Builder.dryAllowedLength(int) -> Builder` Sets DRY allowed length.
*   `Builder.dryPenaltyLastN(int) -> Builder` Sets DRY penalty window.
*   `Builder.drySequenceBreakers(List<String>) -> Builder` Sets DRY breakers.
*   `Builder.stopStrings(List<String>) -> Builder` Sets stop strings.
*   `Builder.build() -> LlamaCppJniConfig` Finalizes the configuration.

#### Dependencies
*   `net.ladenthin.srcmorph.config.AiGenerationConfig`
*   `net.ladenthin.srcmorph.support.ConvertToRecord`
*   `java.util.Collections`
*   `java.util.List`
*   `java.util.Objects`
*   `lombok.EqualsAndHashCode`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `NullPointerException` thrown if `modelPath` is null in `Builder` constructor.

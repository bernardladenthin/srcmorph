### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:41:36Z
- G: 
- A: 0.0.0
- X: file
---
> <Provides immutable configuration settings for the llama.cpp JNI provider, including sampling, hardware offloading, and model parameters.>

#### Purpose
* Acts as a value object containing all parameters required to initialize and run a llama.cpp JNI-based AI model.
* Provides a fluent builder to manage 37 distinct configuration fields with default values from `AiGenerationConfig`.

#### Type
final class; implements none; annotated with `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.

#### Input
* `builder(String modelPath)`: Requires a non-null path to a GGUF model file.
* `Builder` setters: Various primitives, Strings, and Lists for fine-tuning model behavior.
* `LlamaCppJniConfig(Builder builder)`: Consumes a populated `Builder` instance.

#### Output
* `builder(String)` -> `Builder`: A builder initialized with defaults and the specified model path.
* `Builder.build()` -> `LlamaCppJniConfig`: An immutable configuration instance.
* Accessor methods: Return the configured values for all 41 fields (including lists and nullable booleans).

#### Core logic
* **Immutability**: The `LlamaCppJniConfig` class is final and all fields are private and final, ensuring thread-safety after construction.
* **Builder Pattern**: Uses a static inner `Builder` class to prevent the "telescoping constructor" problem associated with its 37 parameters.
* **Default Management**: The `Builder` initializes all fields using `AiGenerationConfig.DEFAULT_*` constants, ensuring consistent behavior across the application.
* **Null Handling**: The `Builder` handles `null` inputs for String and List fields by reverting to defaults or empty collections to prevent downstream `NullPointerException`.
* **Value Semantics**: Uses Lombok to implement `equals` and `hashCode` based on all fields, including exact-bit float comparisons.

#### Public API
* `builder(String) -> Builder` Creates a new builder with the required model path.
* `modelPath() -> String` Returns the GGUF model file path.
* `contextSize() -> int` Returns the context window size.
* `maxOutputTokens() -> int` Returns maximum output tokens.
* `temperature() -> float` Returns sampling temperature.
* `threads() -> int` Returns CPU thread count.
* `topP() -> float` Returns nucleus-sampling threshold.
* `topK() -> int` Returns top-k sampling limit.
* `minP() -> float` Returns min-p threshold.
* `topNSigma() -> float` Returns top-n-sigma threshold.
* `repeatPenalty() -> float` Returns repetition penalty.
* `chatTemplateEnableThinking() -> Boolean` Returns thinking mode status (nullable).
* `cachePrompt() -> boolean` Returns prompt caching status.
* `swaFull() -> boolean` Returns SWA KV cache status.
* `cacheReuse() -> int` Returns KV prefix-reuse chunk size.
* `gpuLayers() -> int` Returns number of GPU layers.
* `seed() -> int` Returns RNG seed.
* `cpuMoeLayers() -> int` Returns CPU MoE layers count.
* `cpuFfnLayers() -> int` Returns CPU FFN layers count.
* `kvUnifiedPerSlot() -> int` Returns per-slot unified KV cap.
* `lazyMode() -> String` Returns tensor-read laziness mode.
* `repeatLastN() -> int` Returns repeat-penalty window.
* `cacheTypeK() -> String` Returns KV-cache K data type.
* `cacheTypeV() -> String` Returns KV-cache V data type.
* `flashAttn() -> boolean` Returns Flash Attention status.
* `batchSize() -> int` Returns logical batch size.
* `ubatchSize() -> int` Returns physical batch size.
* `threadsBatch() -> int` Returns batch processing thread count.
* `mainGpu() -> int` Returns primary GPU index.
* `devices() -> String` Returns device selection string.
* `provider() -> String` Returns the AI provider type.
* `remoteModel() -> String` Returns the remote model ID.
* `baseUrl() -> String` Returns the OpenAI-compatible base URL.
* `apiKeyEnv() -> String` Returns the API key environment variable name.
* `reasoningEffort() -> String` Returns reasoning effort level.
* `reasoningBudgetTokens() -> int` Returns reasoning token budget.
* `dryMultiplier() -> float` Returns DRY sampling multiplier.
* `dryBase() -> float` Returns DRY base value.
* `dryAllowedLength() -> int` Returns DRY allowed length.
* `dryPenaltyLastN() -> int` Returns DRY penalty look-back window.
* `drySequenceBreakers() -> List<String>` Returns unmodifiable DRY sequence breakers.
* `stopStrings() -> List<String>` Returns unmodifiable stop strings.
* `Builder.contextSize(int) -> Builder` Sets context size.
* `Builder.maxOutputTokens(int) -> Builder` Sets max output tokens.
* `Builder.temperature(float) -> Builder` Sets temperature.
* `Builder.threads(int) -> Builder` Sets CPU threads.
* `Builder.topP(float) -> Builder` Sets top-p.
* `Builder.topK(int) -> Builder` Sets top-k.
* `Builder.minP(float) -> Builder` Sets min-p.
* `Builder.topNSigma(float) -> Builder` Sets top-n-sigma.
* `Builder.repeatPenalty(float) -> Builder` Sets repeat penalty.
* `Builder.chatTemplateEnableThinking(Boolean) -> Builder` Sets thinking mode.
* `Builder.cachePrompt(boolean) -> Builder` Sets prompt caching.
* `Builder.swaFull(boolean) -> Builder` Sets SWA full status.
* `Builder.cacheReuse(int) -> Builder` Sets cache reuse size.
* `Builder.gpuLayers(int) -> Builder` Sets GPU layer count.
* `Builder.seed(int) -> Builder` Sets RNG seed.
* `Builder.cpuMoeLayers(int) -> Builder` Sets CPU MoE layers.
* `Builder.cpuFfnLayers(int) -> Builder` Sets CPU FFN layers.
* `Builder.kvUnifiedPerSlot(int) -> Builder` Sets unified KV cap.
* `Builder.lazyMode(String) -> Builder` Sets tensor-read laziness.
* `Builder.repeatLastN(int) -> Builder` Sets repeat window.
* `Builder.cacheTypeK(String) -> Builder` Sets K cache type.
* `Builder.cacheTypeV(String) -> Builder` Sets V cache type.
* `Builder.flashAttn(boolean) -> Builder` Sets Flash Attention.
* `Builder.batchSize(int) -> Builder` Sets logical batch size.
* `Builder.ubatchSize(int) -> Builder` Sets physical batch size.
* `Builder.threadsBatch(int) -> Builder` Sets batch threads.
* `Builder.mainGpu(int) -> Builder` Sets main GPU index.
* `Builder.devices(String) -> Builder` Sets device list.
* `Builder.provider(String) -> Builder` Sets provider type.
* `Builder.remoteModel(String) -> Builder` Sets remote model ID.
* `Builder.baseUrl(String) -> Builder` Sets base URL.
* `Builder.apiKeyEnv(String) -> Builder` Sets API key env var.
* `Builder.reasoningEffort(String) -> Builder` Sets reasoning effort.
* `Builder.reasoningBudgetTokens(int) -> Builder` Sets reasoning budget.
* `Builder.dryMultiplier(float) -> Builder` Sets DRY multiplier.
* `Builder.dryBase(float) -> Builder` Sets DRY base.
* `Builder.dryAllowedLength(int) -> Builder` Sets DRY allowed length.
* `Builder.dryPenaltyLastN(int) -> Builder` Sets DRY penalty window.
* `Builder.drySequenceBreakers(List<String>) -> Builder` Sets DRY breakers.
* `Builder.stopStrings(List<String>) -> Builder` Sets stop strings.
* `Builder.build() -> LlamaCppJniConfig` Constructs the final config.

#### Dependencies
* `java.util.Collections`
* `java.util.List`
* `java.util.Objects`
* `lombok.EqualsAndHashCode`
* `lombok.ToString`
* `net.ladenthin.srcmorph.config.AiGenerationConfig`
* `net.ladenthin.srcmorph.support.ConvertToRecord`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `NullPointerException` thrown if `modelPath` is null during `builder()` initialization.

#### Concurrency
* `LlamaCppJniConfig` is immutable and thread-safe.
* `Builder` is not thread-safe.

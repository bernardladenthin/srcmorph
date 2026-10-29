### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:19Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Holds configuration settings for the llama.cpp JNI provider.
- Provides a fluent builder pattern for creating instances.

#### Type
- `final class` with `public` visibility.
- Extends `Object`.
- Implements `net.ladenthin.srcmorph.support.ConvertToRecord`.
- Annotated with `@ToString`, `@EqualsAndHashCode`, and `@ConvertToRecord`.

#### Input
- `modelPath` (String): Path to the GGUF model file.
- `Builder` (static nested class): Used to construct instances of `LlamaCppJniConfig`.

#### Output
- `LlamaCppJniConfig`: Immutable configuration object.

#### Core logic
- Immutable configuration object with 37 fields.
- Uses Lombok to generate `toString`, `equals`, and `hashCode` methods.
- Provides a private constructor to ensure immutability.
- Public `modelPath()` method returns the model file path.
- Public `contextSize()` method returns the context window size in tokens.
- Public `maxOutputTokens()` method returns the maximum number of output tokens per call.
- Public `temperature()` method returns the sampling temperature.
- Public `threads()` method returns the number of CPU threads.
- Public `topP()` method returns the nucleus-sampling probability threshold.
- Public `topK()` method returns the top-k sampling limit.
- Public `minP()` method returns the min-p sampling threshold.
- Public `topNSigma()` method returns the top-n-sigma sampling threshold.
- Public `repeatPenalty()` method returns the repetition penalty.
- Public `chatTemplateEnableThinking()` method returns the chat-template thinking mode.
- Public `cachePrompt()` method returns whether prompt caching is enabled.
- Public `swaFull()` method returns whether the full-size SWA KV cache is kept.
- Public `cacheReuse()` method returns the KV prefix-reuse minimum chunk size.
- Public `gpuLayers()` method returns the number of GPU layers to offload.
- Public `seed()` method returns the RNG seed for generation.
- Public `cpuMoeLayers()` method returns the number of MoE layers kept on the CPU.
- Public `cpuFfnLayers()` method returns the number of FFN layers kept on the CPU.
- Public `kvUnifiedPerSlot()` method returns the per-slot unified KV context cap.
- Public `lazyMode()` method returns the tensor-read laziness.
- Public `repeatLastN()` method returns the repeat-penalty window.
- Public `cacheTypeK()` method returns the KV-cache data type for K.
- Public `cacheTypeV()` method returns the KV-cache data type for V.
- Public `flashAttn()` method returns whether Flash Attention is enabled.
- Public `batchSize()` method returns the logical batch size.
- Public `ubatchSize()` method returns the physical batch size.
- Public `threadsBatch()` method returns the batch/prompt-processing thread count.
- Public `mainGpu()` method returns the primary GPU index.
- Public `devices()` method returns the device selection.
- Public `provider()` method returns the AI provider.
- Public `remoteModel()` method returns the model id the provider requests.
- Public `baseUrl()` method returns the base URL of the API.
- Public `apiKeyEnv()` method returns the environment variable name for the API key.
- Public `reasoningEffort()` method returns the reasoning-effort kwarg value.
- Public `reasoningBudgetTokens()` method returns the reasoning budget.
- Public `dryMultiplier()` method returns the DRY sampling multiplier.
- Public `dryBase()` method returns the DRY base.
- Public `dryAllowedLength()` method returns the DRY allowed length.
- Public `dryPenaltyLastN()` method returns the DRY penalty look-back window.
- Public `drySequenceBreakers()` method returns an unmodifiable list of DRY sequence breakers.
- Public `stopStrings()` method returns an unmodifiable list of stop strings.

#### Public API
- `modelPath() -> String`: Returns the model file path.
- `contextSize() -> int`: Returns the context window size in tokens.
- `maxOutputTokens() -> int`: Returns the maximum number of output tokens per call.
- `temperature() -> float`: Returns the sampling temperature.
- `threads() -> int`: Returns the number of CPU threads.
- `topP() -> float`: Returns the nucleus-sampling probability threshold.
- `topK() -> int`: Returns the top-k sampling limit.
- `minP() -> float`: Returns the min-p sampling threshold.
- `topNSigma() -> float`: Returns the top-n-sigma sampling threshold.
- `repeatPenalty() -> float`: Returns the repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean`: Returns the chat-template thinking mode.
- `cachePrompt() -> boolean`: Returns whether prompt caching is enabled.
- `swaFull() -> boolean`: Returns whether the full-size SWA KV cache is kept.
- `cacheReuse() -> int`: Returns the KV prefix-reuse minimum chunk size.
- `gpuLayers() -> int`: Returns the number of GPU layers to offload.
- `seed() -> int`: Returns the RNG seed for generation.
- `cpuMoeLayers() -> int`: Returns the number of MoE layers kept on the CPU.
- `cpuFfnLayers() -> int`: Returns the number of FFN layers kept on the CPU.
- `kvUnifiedPerSlot() -> int`: Returns the per-slot unified KV context cap.
- `lazyMode() -> String`: Returns the tensor-read laziness.
- `repeatLastN() -> int`: Returns the repeat-penalty window.
- `cacheTypeK() -> String`: Returns the KV-cache data type for K.
- `cacheTypeV() -> String`: Returns the KV-cache data type for V.
- `flashAttn() -> boolean`: Returns whether Flash Attention is enabled.
- `batchSize() -> int`: Returns the logical batch size.
- `ubatchSize() -> int`: Returns the physical batch size.
- `threadsBatch() -> int`: Returns the batch/prompt-processing thread count.
- `mainGpu() -> int`: Returns the primary GPU index.
- `devices() -> String`: Returns the device selection.
- `provider() -> String`: Returns the AI provider.
- `remoteModel() -> String`: Returns the model id the provider requests.
- `baseUrl() -> String`: Returns the base URL of the API.
- `apiKeyEnv() -> String`: Returns the environment variable name for the API key.
- `reasoningEffort() -> String`: Returns the reasoning-effort kwarg value.
- `reasoningBudgetTokens() -> int`: Returns the reasoning budget.
- `dryMultiplier() -> float`: Returns the DRY sampling multiplier.
- `dryBase() -> float`: Returns the DRY base.
- `dryAllowedLength() -> int`: Returns the DRY allowed length.
- `dryPenaltyLastN() -> int`: Returns the DRY penalty look-back window.
- `drySequenceBreakers() -> List<String>`: Returns an unmodifiable list of DRY sequence breakers.
- `stopStrings() -> List<String>`: Returns an unmodifiable list of stop strings.
- `builder(String modelPath) -> Builder`: Starts building a configuration for the given model.
- `build() -> LlamaCppJniConfig`: Builds the immutable configuration.

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `NullPointerException` if `modelPath` is `null` in the `builder` method.

#### Concurrency
- Not thread-safe, as the builder is intended to be used on one thread and produces immutable objects.

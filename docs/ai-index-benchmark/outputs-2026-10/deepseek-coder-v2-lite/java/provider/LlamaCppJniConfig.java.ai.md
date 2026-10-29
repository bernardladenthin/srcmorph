### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:41:30Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Defines a configuration for the llama.cpp JNI provider.
- Provides a builder pattern for constructing configurations.

#### Type
- Class (`final`).
- Extends: `None`.
- Implements: `None`.
- Key generics and type bounds: `None`.
- Notable annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `modelPath`: Path to the GGUF model file (required).
- `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`: Configured through builder methods.

#### Output
- An immutable `LlamaCppJniConfig` object.
- Accessors for all configuration parameters.

#### Core logic
- Configuration parameters are set through the builder.
- Parameters are initialized to default values from `AiGenerationConfig`.
- The configuration is immutable once built.

#### Public API
- `builder(String modelPath) -> Builder`: Starts building a configuration with default values.
- `modelPath() -> String`: Returns the GGUF model file path.
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
- `seed() -> int`: Returns the RNG seed used for generation.
- `cpuMoeLayers() -> int`: Returns the number of MoE layers kept on the CPU.
- `cpuFfnLayers() -> int`: Returns the number of FFN layers kept on the CPU.
- `kvUnifiedPerSlot() -> int`: Returns the per-slot unified KV context cap.
- `lazyMode() -> String`: Returns the tensor-read laziness mode.
- `repeatLastN() -> int`: Returns the repeat-penalty window.
- `cacheTypeK() -> String`: Returns the KV-cache data type for K.
- `cacheTypeV() -> String`: Returns the KV-cache data type for V.
- `flashAttn() -> boolean`: Returns whether Flash Attention is enabled.
- `batchSize() -> int`: Returns the logical batch size.
- `ubatchSize() -> int`: Returns the physical batch size.
- `threadsBatch() -> int`: Returns the batch/prompt-processing thread count.
- `mainGpu() -> int`: Returns the primary GPU index.
- `devices() -> String`: Returns the device selection.
- `provider() -> String`: Returns the AI provider of this model.
- `remoteModel() -> String`: Returns the model id for the OpenAI-compatible provider.
- `baseUrl() -> String`: Returns the base URL of the OpenAI-compatible API.
- `apiKeyEnv() -> String`: Returns the name of the environment variable holding the API key.
- `reasoningEffort() -> String`: Returns the gpt-oss reasoning-effort kwarg value.
- `reasoningBudgetTokens() -> int`: Returns the reasoning/think-token budget.
- `dryMultiplier() -> float`: Returns the DRY sampling multiplier.
- `dryBase() -> float`: Returns the DRY base.
- `dryAllowedLength() -> int`: Returns the DRY allowed length.
- `dryPenaltyLastN() -> int`: Returns the DRY penalty look-back window in tokens.
- `drySequenceBreakers() -> List<String>`: Returns an unmodifiable view of the configured DRY sequence breakers.
- `stopStrings() -> List<String>`: Returns an unmodifiable view of the configured stop strings.

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok` annotations (`@ToString`, `@EqualsAndHashCode`)
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`

#### Exceptions / Errors
- Throws `NullPointerException` if `modelPath` is `null` in the builder constructor.
- No other exceptions or error conditions are explicitly mentioned.

#### Concurrency
- The builder is not thread-safe and is not meant to be used concurrently.
- The immutable configuration produced by the builder is thread-safe and can be used concurrently.

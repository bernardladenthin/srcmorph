### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:58:51Z
- G: 
- A: 0.0.0
- X: file
---
> Configures the llama.cpp JNI provider for AI text generation with adjustable generation and model parameters.

#### Purpose
- Immutable configuration holder for the llama.cpp JNI provider used in srcmorph AI generation.
- Exposes named generation knobs (temperature, context size, top-p, etc.) and model path.

#### Type
- Final class `LlamaCppJniConfig`; immutable record-like configuration.
- Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`; Lombok generates equals, hashCode, toString.
- Contains 41 fields; all set via Builder.

#### Input
- `LlamaCppJniConfig.builder(String modelPath)` factory; modelPath must not be null.
- Builder defaults all 41 fields from `AiGenerationConfig.DEFAULT_*`.

#### Output
- Returns immutable `LlamaCppJniConfig` with all fields set.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Private constructor copies all Builder fields into the config.
- Builder factory enforces non-null modelPath (throws NullPointerException).
- Builder setters return this for chaining; no validation beyond defaults.
- `build()` constructs the final immutable instance.
- No normalization; builder already applied defaults.

#### Public API
- modelPath() -> String: returns the GGUF model file path.
- contextSize() -> int: returns the context window size in tokens.
- maxOutputTokens() -> int: returns the maximum output tokens per call.
- temperature() -> float: returns the sampling temperature.
- threads() -> int: returns the number of CPU threads.
- topP() -> float: returns the nucleus-sampling probability threshold.
- topK() -> int: returns the top-k sampling limit.
- minP() -> float: returns the min-p sampling threshold.
- topNSigma() -> float: returns the top-n-sigma sampling threshold.
- repeatPenalty() -> float: returns the repetition penalty.
- chatTemplateEnableThinking() -> @Nullable Boolean: returns chat template thinking mode.
- cachePrompt() -> boolean: returns whether prompt caching is enabled.
- swaFull() -> boolean: returns whether full SWA KV is kept.
- cacheReuse() -> int: returns the KV cache reuse chunk size.
- gpuLayers() -> int: returns the number of GPU layers to offload.
- seed() -> int: returns the RNG seed for generation.
- cpuMoeLayers() -> int: returns the number of CPU MoE layers.
- cpuFfnLayers() -> int: returns the number of CPU FFN layers.
- kvUnifiedPerSlot() -> int: returns the per-slot KV cap.
- lazyMode() -> String: returns the tensor-read laziness mode.
- repeatLastN() -> int: returns the repeat-penalty window.
- cacheTypeK() -> String: returns the KV cache type for K.
- cacheTypeV() -> String: returns the KV cache type for V.
- flashAttn() -> boolean: returns whether Flash Attention is enabled.
- batchSize() -> int: returns the logical batch size.
- ubatchSize() -> int: returns the physical batch size.
- threadsBatch() -> int: returns the batch/prompt thread count.
- mainGpu() -> int: returns the primary GPU index.
- devices() -> String: returns the device selection list.
- provider() -> String: returns the AI provider name.
- remoteModel() -> String: returns the remote model ID.
- baseUrl() -> String: returns the base API URL.
- apiKeyEnv() -> String: returns the environment variable name for the API key.
- reasoningEffort() -> String: returns the reasoning effort kwarg value.
- reasoningBudgetTokens() -> int: returns the reasoning token budget.
- dryMultiplier() -> float: returns the DRY sampling multiplier.
- dryBase() -> float: returns the DRY base.
- dryAllowedLength() -> int: returns the DRY allowed length.
- dryPenaltyLastN() -> int: returns the DRY penalty look-back window.
- drySequenceBreakers() -> List<String>: returns the unmodifiable sequence breakers.
- stopStrings() -> List<String>: returns the unmodifiable stop strings.
- LlamaCppJniConfig.builder(String) -> Builder: creates a builder with defaults.
- Builder setters: builderSetter1..builderSetter41 (41 total).

#### Dependencies
- java.util.Collections, java.util.List, java.util.Objects
- Lombok @EqualsAndHashCode, @ToString
- net.ladenthin.srcmorph.config.AiGenerationConfig
- net.ladenthin.srcmorph.support.ConvertToRecord
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- NullPointerException if modelPath is null in builder.
- Unset knobs return null (e.g., chatTemplateEnableThinking).
- No other checked exceptions.

#### Concurrency
- Not thread-safe; immutable after construction.
- Builder is single-threaded; no synchronization needed.

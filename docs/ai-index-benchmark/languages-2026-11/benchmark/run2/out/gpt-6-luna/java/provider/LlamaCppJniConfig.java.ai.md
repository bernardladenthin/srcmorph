### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:37:09Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI and OpenAI-compatible generation, including sampling, model execution, caching, and reasoning options.

#### Purpose
- `LlamaCppJniConfig` holds generation and provider settings for a GGUF model.
- Its fluent `Builder` applies `AiGenerationConfig` defaults and creates immutable configuration instances.

#### Type
- `LlamaCppJniConfig` — `final` class; annotated `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.
- Nested `public static final class Builder` — fluent configuration builder.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling, execution, provider, reasoning, DRY, stop-string, and sequence-breaker settings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list settings default to empty lists.

#### Output
- `build()` creates a `LlamaCppJniConfig` with final configuration fields.
- Accessors return configuration values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` across the configuration fields.

#### Core logic
- Builder initializes optional settings from `AiGenerationConfig` defaults.
- Setter calls update builder state and return the same builder for chaining.
- String setters for selected settings restore their `AiGenerationConfig` default when passed `null`; list setters use an empty list for `null`.
- The private constructor copies builder values into final fields without additional normalization.
- `chatTemplateEnableThinking` preserves `null` to represent an unset option.
- List accessors wrap the stored lists with `Collections.unmodifiableList`; the builder does not copy supplied lists.

#### Public API
- `builder(String)` -> `Builder` — start with model path and defaults.
- `modelPath()` -> `String` — return GGUF model path.
- `contextSize()` -> `int` — return context window size.
- `maxOutputTokens()` -> `int` — return output token limit.
- `temperature()` -> `float` — return sampling temperature.
- `threads()` -> `int` — return CPU thread count.
- `topP()` -> `float` — return nucleus sampling threshold.
- `topK()` -> `int` — return top-k limit.
- `minP()` -> `float` — return min-p threshold.
- `topNSigma()` -> `float` — return top-n-sigma threshold.
- `repeatPenalty()` -> `float` — return repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — return optional thinking mode.
- `cachePrompt()` -> `boolean` — return prompt-cache setting.
- `swaFull()` -> `boolean` — return full-size SWA cache setting.
- `cacheReuse()` -> `int` — return KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — return GPU offload layer count.
- `seed()` -> `int` — return generation RNG seed.
- `cpuMoeLayers()` -> `int` — return CPU MoE layer count.
- `cpuFfnLayers()` -> `int` — return CPU FFN layer count.
- `kvUnifiedPerSlot()` -> `int` — return per-slot KV context cap.
- `lazyMode()` -> `String` — return tensor-read laziness.
- `repeatLastN()` -> `int` — return repeat-penalty window.
- `cacheTypeK()` -> `String` — return K-cache data type.
- `cacheTypeV()` -> `String` — return V-cache data type.
- `flashAttn()` -> `boolean` — return Flash Attention setting.
- `batchSize()` -> `int` — return logical batch size.
- `ubatchSize()` -> `int` — return physical batch size.
- `threadsBatch()` -> `int` — return batch-processing thread count.
- `mainGpu()` -> `int` — return primary GPU index.
- `devices()` -> `String` — return device selection.
- `provider()` -> `String` — return provider selection.
- `remoteModel()` -> `String` — return remote model identifier.
- `baseUrl()` -> `String` — return compatible API base URL.
- `apiKeyEnv()` -> `String` — return API-key environment variable name.
- `reasoningEffort()` -> `String` — return reasoning-effort value.
- `reasoningBudgetTokens()` -> `int` — return reasoning token budget.
- `dryMultiplier()` -> `float` — return DRY sampling multiplier.
- `dryBase()` -> `float` — return DRY base.
- `dryAllowedLength()` -> `int` — return DRY allowed length.
- `dryPenaltyLastN()` -> `int` — return DRY look-back window.
- `drySequenceBreakers()` -> `List<String>` — return unmodifiable DRY breakers.
- `stopStrings()` -> `List<String>` — return unmodifiable stop strings.
- `Builder.contextSize(int)` -> `Builder` — set context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — set output token limit.
- `Builder.temperature(float)` -> `Builder` — set sampling temperature.
- `Builder.threads(int)` -> `Builder` — set CPU thread count.
- `Builder.topP(float)` -> `Builder` — set nucleus threshold.
- `Builder.topK(int)` -> `Builder` — set top-k limit.
- `Builder.minP(float)` -> `Builder` — set min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set optional thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — set full-size SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — set KV prefix-reuse chunk size.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload layers.
- `Builder.seed(int)` -> `Builder` — set RNG seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — set per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int)` -> `Builder` — set repeat-penalty window.
- `Builder.cacheTypeK(String)` -> `Builder` — set K-cache data type.
- `Builder.cacheTypeV(String)` -> `Builder` — set V-cache data type.
- `Builder.flashAttn(boolean)` -> `Builder` — set Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — set logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — set physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — set batch-processing threads.
- `Builder.mainGpu(int)` -> `Builder` — set primary GPU index.
- `Builder.devices(String)` -> `Builder` — set device selection.
- `Builder.provider(String)` -> `Builder` — set provider selection.
- `Builder.remoteModel(String)` -> `Builder` — set remote model identifier.
- `Builder.baseUrl(String)` -> `Builder` — set API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — set API-key environment variable name.
- `Builder.reasoningEffort(String)` -> `Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — set stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — build immutable configuration.

#### Dependencies
- `AiGenerationConfig` — defaults for generation and provider settings.
- `Collections`, `List`, `Objects` — list views, list values, and null validation.
- Lombok `@EqualsAndHashCode`, `@ToString`.
- `@ConvertToRecord`; JSpecify `@Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- Selected nullable string and list setter arguments restore defaults; `chatTemplateEnableThinking(null)` remains unset.

#### Concurrency
- `LlamaCppJniConfig` fields are final; builder documentation describes the produced configuration as immutable.
- `Builder` is explicitly not thread-safe.
- Supplied lists are not copied; unmodifiable accessor views do not prevent mutation through another reference.

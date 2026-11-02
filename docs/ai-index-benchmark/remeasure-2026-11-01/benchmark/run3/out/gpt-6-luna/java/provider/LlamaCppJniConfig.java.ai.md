### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:00:03Z
- G: 
- A: 0.0.0
- X: file
---
> Configures local llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop behavior.

#### Purpose
- Defines immutable per-model generation settings for the llama.cpp JNI provider and compatible remote providers.
- Provides a fluent builder initialized from `AiGenerationConfig` defaults.

#### Type
- `public final class LlamaCppJniConfig` — final class; no superclass or implemented interfaces.
- `public static final class Builder` — nested final builder.
- `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`; Lombok generates value-based `toString`, `equals`, and `hashCode`.
- Fields cover model path, sampling, prompt/KV caching, CPU/GPU execution, provider/API configuration, reasoning, DRY sampling, and stop strings.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- Builder setters accept sampling parameters, context/output limits, hardware and cache controls, provider/API settings, reasoning options, and DRY/stop lists.
- Builder defaults derive from `AiGenerationConfig.DEFAULT_*`; DRY sequence breakers and stop strings default to empty lists.

#### Output
- `build()` produces an immutable `LlamaCppJniConfig`.
- Accessors expose configuration values; list accessors return unmodifiable views.
- Configuration contains no file, network, or model-loading operations.

#### Core logic
- Builder requires a non-null model path and initializes other settings from defaults.
- Fluent setters assign values; nullable string settings restore their corresponding defaults, while nullable list settings become empty lists.
- `build()` passes all builder values to the private constructor, which copies them into final fields without further normalization.
- `chatTemplateEnableThinking` preserves null to represent an unset option.
- Lombok supplies value semantics across the fields.

#### Public API
- `builder(String)` -> `Builder` — start with model path and defaults.
- `modelPath()` -> `String` — GGUF model path.
- `contextSize()` -> `int` — context window.
- `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature.
- `threads()` -> `int` — CPU thread count.
- `topP()` -> `float` — nucleus-sampling threshold.
- `topK()` -> `int` — top-k limit.
- `minP()` -> `float` — min-p threshold.
- `topNSigma()` -> `float` — top-n-sigma threshold.
- `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — chat-template thinking mode.
- `cachePrompt()` -> `boolean` — prompt-prefix caching flag.
- `swaFull()` -> `boolean` — full-size SWA KV cache flag.
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — GPU offload layer count.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU-resident MoE layers.
- `cpuFfnLayers()` -> `int` — CPU-resident FFN layers.
- `kvUnifiedPerSlot()` -> `int` — per-slot unified KV cap.
- `lazyMode()` -> `String` — tensor-read laziness.
- `repeatLastN()` -> `int` — repeat-penalty window.
- `cacheTypeK()` -> `String` — K-cache data type.
- `cacheTypeV()` -> `String` — V-cache data type.
- `flashAttn()` -> `boolean` — Flash Attention flag.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch-processing thread count.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — selected devices.
- `provider()` -> `String` — configured AI provider.
- `remoteModel()` -> `String` — remote model identifier.
- `baseUrl()` -> `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning-effort setting.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY sampling multiplier.
- `dryBase()` -> `float` — DRY base.
- `dryAllowedLength()` -> `int` — DRY allowed length.
- `dryPenaltyLastN()` -> `int` — DRY penalty look-back window.
- `drySequenceBreakers()` -> `List<String>` — unmodifiable DRY breaker view.
- `stopStrings()` -> `List<String>` — unmodifiable stop-string view.
- `Builder.contextSize(int)` -> `Builder` — set context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — set output token limit.
- `Builder.temperature(float)` -> `Builder` — set temperature.
- `Builder.threads(int)` -> `Builder` — set CPU threads.
- `Builder.topP(float)` -> `Builder` — set top-p threshold.
- `Builder.topK(int)` -> `Builder` — set top-k limit.
- `Builder.minP(float)` -> `Builder` — set min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — set full SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload layers.
- `Builder.seed(int)` -> `Builder` — set generation seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — set unified KV cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read mode.
- `Builder.repeatLastN(int)` -> `Builder` — set repeat window.
- `Builder.cacheTypeK(String)` -> `Builder` — set K-cache type.
- `Builder.cacheTypeV(String)` -> `Builder` — set V-cache type.
- `Builder.flashAttn(boolean)` -> `Builder` — set Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — set logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — set physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — set batch thread count.
- `Builder.mainGpu(int)` -> `Builder` — set primary GPU.
- `Builder.devices(String)` -> `Builder` — set device selection.
- `Builder.provider(String)` -> `Builder` — set AI provider.
- `Builder.remoteModel(String)` -> `Builder` — set remote model.
- `Builder.baseUrl(String)` -> `Builder` — set API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — set API-key variable name.
- `Builder.reasoningEffort(String)` -> `Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — set reasoning budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — set DRY penalty window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — set stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — create immutable configuration.

#### Dependencies
- Java: `Collections`, `List`, `Objects`.
- Lombok: `EqualsAndHashCode`, `ToString`.
- JSpecify: `Nullable`.
- Project: `AiGenerationConfig`, `ConvertToRecord`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- Null string values for selected builder settings restore `AiGenerationConfig` defaults; null list values become empty lists.
- No validation of numeric ranges or other values is performed.

#### Concurrency
- `LlamaCppJniConfig` is immutable; list accessors expose unmodifiable views.
- `Builder` is explicitly not thread-safe.

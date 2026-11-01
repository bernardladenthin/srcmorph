### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:50Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp and OpenAI-compatible model generation, including sampling, caching, hardware, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` stores provider settings for a GGUF model as a value configuration.
- Its fluent `Builder` applies `AiGenerationConfig` defaults and creates configurations without a positional constructor call.

#### Type
- `LlamaCppJniConfig` — `final` class; annotated `@ConvertToRecord`, Lombok `@ToString`, and `@EqualsAndHashCode`.
- Nested `public static final class Builder` — mutable fluent builder.
- 41 private final configuration fields; `chatTemplateEnableThinking` is nullable. Configuration exposes record-style accessors.

#### Input
- `builder(String modelPath)` requires a model path.
- Builder setters accept generation, sampling, cache, hardware, provider/API, reasoning, DRY, and stop-string settings.
- Builder initializes scalar and string values from `AiGenerationConfig.DEFAULT_*`; sequence-breaker and stop-string lists default to empty lists.

#### Output
- `build()` returns a `LlamaCppJniConfig` populated from the builder.
- Accessors return configured values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` across all configuration fields.

#### Core logic
- `LlamaCppJniConfig.builder(modelPath)` creates a builder and rejects a null model path.
- Optional builder setters replace default values; selected string and list setters map null back to defaults.
- The private constructor copies all builder fields directly; it performs no normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`; the lists themselves are not copied.
- `chatTemplateEnableThinking` preserves null to represent an unset option.

#### Public API
- `builder(String)` -> `Builder` — start configuration with defaults.
- `modelPath()` -> `String` — GGUF model path.
- `contextSize()` -> `int` — context window size.
- `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature.
- `threads()` -> `int` — CPU thread count.
- `topP()` -> `float` — nucleus-sampling threshold.
- `topK()` -> `int` — top-k sampling limit.
- `minP()` -> `float` — min-p threshold.
- `topNSigma()` -> `float` — top-n-sigma threshold.
- `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — thinking mode or unset.
- `cachePrompt()` -> `boolean` — prompt caching flag.
- `swaFull()` -> `boolean` — full SWA KV cache flag.
- `cacheReuse()` -> `int` — KV prefix-reuse chunk size.
- `gpuLayers()` -> `int` — GPU layer offload count.
- `seed()` -> `int` — generation RNG seed.
- `cpuMoeLayers()` -> `int` — CPU MoE layer count.
- `cpuFfnLayers()` -> `int` — CPU FFN layer count.
- `kvUnifiedPerSlot()` -> `int` — per-slot unified KV cap.
- `lazyMode()` -> `String` — tensor-read laziness mode.
- `repeatLastN()` -> `int` — repetition penalty window.
- `cacheTypeK()` -> `String` — K cache data type.
- `cacheTypeV()` -> `String` — V cache data type.
- `flashAttn()` -> `boolean` — Flash Attention flag.
- `batchSize()` -> `int` — logical batch size.
- `ubatchSize()` -> `int` — physical batch size.
- `threadsBatch()` -> `int` — batch-processing thread count.
- `mainGpu()` -> `int` — primary GPU index.
- `devices()` -> `String` — device selection.
- `provider()` -> `String` — AI provider identifier.
- `remoteModel()` -> `String` — remote model identifier.
- `baseUrl()` -> `String` — OpenAI-compatible API base URL.
- `apiKeyEnv()` -> `String` — API-key environment variable name.
- `reasoningEffort()` -> `String` — reasoning-effort option.
- `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY sampling multiplier.
- `dryBase()` -> `float` — DRY base.
- `dryAllowedLength()` -> `int` — DRY allowed length.
- `dryPenaltyLastN()` -> `int` — DRY penalty look-back window.
- `drySequenceBreakers()` -> `List<String>` — unmodifiable DRY breaker view.
- `stopStrings()` -> `List<String>` — unmodifiable stop-string view.
- `Builder.contextSize(int)` -> `Builder` — set context window.
- `Builder.maxOutputTokens(int)` -> `Builder` — set output token limit.
- `Builder.temperature(float)` -> `Builder` — set sampling temperature.
- `Builder.threads(int)` -> `Builder` — set CPU thread count.
- `Builder.topP(float)` -> `Builder` — set nucleus threshold.
- `Builder.topK(int)` -> `Builder` — set top-k limit.
- `Builder.minP(float)` -> `Builder` — set min-p threshold.
- `Builder.topNSigma(float)` -> `Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float)` -> `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set thinking mode.
- `Builder.cachePrompt(boolean)` -> `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` -> `Builder` — set full SWA cache.
- `Builder.cacheReuse(int)` -> `Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload count.
- `Builder.seed(int)` -> `Builder` — set generation seed.
- `Builder.cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` -> `Builder` — set per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read mode.
- `Builder.repeatLastN(int)` -> `Builder` — set repetition window.
- `Builder.cacheTypeK(String)` -> `Builder` — set K cache type.
- `Builder.cacheTypeV(String)` -> `Builder` — set V cache type.
- `Builder.flashAttn(boolean)` -> `Builder` — set Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — set logical batch size.
- `Builder.ubatchSize(int)` -> `Builder` — set physical batch size.
- `Builder.threadsBatch(int)` -> `Builder` — set batch thread count.
- `Builder.mainGpu(int)` -> `Builder` — set primary GPU.
- `Builder.devices(String)` -> `Builder` — set device selection.
- `Builder.provider(String)` -> `Builder` — set provider identifier.
- `Builder.remoteModel(String)` -> `Builder` — set remote model.
- `Builder.baseUrl(String)` -> `Builder` — set API base URL.
- `Builder.apiKeyEnv(String)` -> `Builder` — set API-key environment variable.
- `Builder.reasoningEffort(String)` -> `Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int)` -> `Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` -> `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` -> `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` -> `Builder` — set DRY penalty window.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` -> `Builder` — set stop strings.
- `Builder.build()` -> `LlamaCppJniConfig` — create configuration.

#### Dependencies
- Java: `Collections`, `List`, `Objects`.
- Configuration defaults: `AiGenerationConfig`.
- Annotations: `ConvertToRecord`, `Nullable`, Lombok `EqualsAndHashCode`, `ToString`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, and `reasoningEffort` restore their configured defaults when passed null.
- Null sequence-breaker and stop-string lists become empty lists.

#### Concurrency
- `LlamaCppJniConfig` fields are final; builder is explicitly not thread-safe.
- List contents are not defensively copied, so external mutation of a supplied list can affect the configuration despite unmodifiable accessor views.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:56Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Captures model, sampling, hardware, caching, and API-provider settings for AI text generation.

#### Purpose
- Defines an immutable configuration for llama.cpp JNI generation and related OpenAI-compatible or mock providers.
- Provides a fluent builder initialized from `AiGenerationConfig` defaults.

#### Type
- `public final class LlamaCppJniConfig` — class; annotated `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`; no declared supertypes.
- `public static final class Builder` — class; nested in `LlamaCppJniConfig`; fluent mutable builder.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling and token limits, CPU/GPU and cache options, provider/API settings, reasoning and DRY parameters, and stop/sequence-breaker lists.
- Builder fields default to corresponding `AiGenerationConfig.DEFAULT_*` values; list defaults are empty lists.

#### Output
- `build()` creates a `LlamaCppJniConfig` containing 41 configuration fields.
- Record-style accessors expose configuration values; list accessors return unmodifiable views.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Core logic
- `builder(String)` constructs a `Builder`; its private constructor applies `Objects.requireNonNull(modelPath, "modelPath")`.
- Builder setters update individual fields and return `this`; string setters for `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, and `reasoningEffort` replace `null` with the relevant `AiGenerationConfig.DEFAULT_*`.
- `drySequenceBreakers(List<String>)` and `stopStrings(List<String>)` replace `null` with empty lists.
- `build()` calls the private `LlamaCppJniConfig(Builder)` constructor, which copies every builder field without further normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`; the builder does not copy supplied non-null lists.
- `chatTemplateEnableThinking` is nullable; `null` represents an unset chat-template option.

#### Public API
- `builder(String) -> Builder` — start configuration with required model path.
- `modelPath() -> String` — GGUF model path.
- `contextSize() -> int` — context window.
- `maxOutputTokens() -> int` — output token limit.
- `temperature() -> float` — sampling temperature.
- `threads() -> int` — CPU thread count.
- `topP() -> float` — nucleus threshold.
- `topK() -> int` — top-k limit.
- `minP() -> float` — min-p threshold.
- `topNSigma() -> float` — top-n-sigma threshold.
- `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — optional thinking mode.
- `cachePrompt() -> boolean` — prompt caching.
- `swaFull() -> boolean` — full-size SWA KV cache.
- `cacheReuse() -> int` — KV prefix reuse chunk size.
- `gpuLayers() -> int` — GPU offload layer count.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layer count.
- `cpuFfnLayers() -> int` — CPU FFN layer count.
- `kvUnifiedPerSlot() -> int` — per-slot KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repetition window.
- `cacheTypeK() -> String` — K-cache data type.
- `cacheTypeV() -> String` — V-cache data type.
- `flashAttn() -> boolean` — Flash Attention setting.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing thread count.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — provider identifier.
- `remoteModel() -> String` — remote model identifier.
- `baseUrl() -> String` — OpenAI-compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning-effort value.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY look-back window.
- `drySequenceBreakers() -> List<String>` — unmodifiable DRY sequence breakers.
- `stopStrings() -> List<String>` — unmodifiable stop strings.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output token limit.
- `Builder.temperature(float) -> Builder` — set temperature.
- `Builder.threads(int) -> Builder` — set CPU threads.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set optional thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt caching.
- `Builder.swaFull(boolean) -> Builder` — set full-size SWA cache.
- `Builder.cacheReuse(int) -> Builder` — set KV prefix reuse size.
- `Builder.gpuLayers(int) -> Builder` — set GPU offload layers.
- `Builder.seed(int) -> Builder` — set generation seed.
- `Builder.cpuMoeLayers(int) -> Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` — set per-slot KV cap.
- `Builder.lazyMode(String) -> Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int) -> Builder` — set repetition window.
- `Builder.cacheTypeK(String) -> Builder` — set K-cache type.
- `Builder.cacheTypeV(String) -> Builder` — set V-cache type.
- `Builder.flashAttn(boolean) -> Builder` — set Flash Attention.
- `Builder.batchSize(int) -> Builder` — set logical batch size.
- `Builder.ubatchSize(int) -> Builder` — set physical batch size.
- `Builder.threadsBatch(int) -> Builder` — set batch-processing threads.
- `Builder.mainGpu(int) -> Builder` — set primary GPU.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set provider identifier.
- `Builder.remoteModel(String) -> Builder` — set remote model identifier.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API-key environment variable name.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY sequence breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create immutable configuration.

#### Dependencies
- `AiGenerationConfig`
- `ConvertToRecord`
- Lombok `EqualsAndHashCode`, `ToString`
- `Nullable`
- `Collections`, `List`, `Objects`

#### Exceptions / Errors
- `Objects.requireNonNull` throws `NullPointerException` for a null `modelPath`.
- Nullable setters for specified strings restore defaults; nullable list setters use empty lists.
- No validation of numeric ranges or list contents.

#### Concurrency
- `Builder` is explicitly not thread-safe.
- `LlamaCppJniConfig` fields are final; returned lists are unmodifiable views, though supplied builder lists are not defensively copied.

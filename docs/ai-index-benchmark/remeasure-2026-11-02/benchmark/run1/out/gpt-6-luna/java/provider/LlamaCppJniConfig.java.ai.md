### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:28Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` holds generation and provider settings for a model; callers supply a required GGUF path and optionally override defaults.
- Its nested `Builder` applies `AiGenerationConfig` defaults and creates the immutable configuration.

#### Type
- `public final class LlamaCppJniConfig`; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder`.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- Builder setters accept generation sampling, cache, CPU/GPU, batching, provider/API, reasoning, DRY, and stop-string settings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig` containing 41 final fields.
- Accessors expose settings; list accessors return unmodifiable views.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Core logic
- `LlamaCppJniConfig.builder` creates a builder with `modelPath` and all other settings initialized to defaults.
- Builder setters assign values fluently; nullable string/list inputs restore their configured defaults when null.
- The private config constructor copies all builder values without further normalization.
- Nullable `chatTemplateEnableThinking` preserves unset state so providers can omit the chat-template kwarg.

#### Public API
- `builder(String) -> Builder` — start with model path.
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
- `chatTemplateEnableThinking() -> @Nullable Boolean` — template thinking mode.
- `cachePrompt() -> boolean` — prompt caching flag.
- `swaFull() -> boolean` — full SWA cache flag.
- `cacheReuse() -> int` — KV prefix-reuse chunk size.
- `gpuLayers() -> int` — GPU-offloaded layers.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repeat-penalty window.
- `cacheTypeK() -> String` — K cache data type.
- `cacheTypeV() -> String` — V cache data type.
- `flashAttn() -> boolean` — Flash Attention flag.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — generation provider.
- `remoteModel() -> String` — remote model identifier.
- `baseUrl() -> String` — OpenAI-compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning-effort value.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY sampling multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY penalty look-back window.
- `drySequenceBreakers() -> List<String>` — DRY sequence breakers.
- `stopStrings() -> List<String>` — stop strings.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output token limit.
- `Builder.temperature(float) -> Builder` — set sampling temperature.
- `Builder.threads(int) -> Builder` — set CPU thread count.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set template thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt caching.
- `Builder.swaFull(boolean) -> Builder` — set full SWA cache.
- `Builder.cacheReuse(int) -> Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int) -> Builder` — set GPU-offloaded layers.
- `Builder.seed(int) -> Builder` — set generation RNG seed.
- `Builder.cpuMoeLayers(int) -> Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` — set per-slot KV cap.
- `Builder.lazyMode(String) -> Builder` — set tensor-read mode.
- `Builder.repeatLastN(int) -> Builder` — set repeat window.
- `Builder.cacheTypeK(String) -> Builder` — set K cache type.
- `Builder.cacheTypeV(String) -> Builder` — set V cache type.
- `Builder.flashAttn(boolean) -> Builder` — set Flash Attention.
- `Builder.batchSize(int) -> Builder` — set logical batch size.
- `Builder.ubatchSize(int) -> Builder` — set physical batch size.
- `Builder.threadsBatch(int) -> Builder` — set batch thread count.
- `Builder.mainGpu(int) -> Builder` — set primary GPU.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set generation provider.
- `Builder.remoteModel(String) -> Builder` — set remote model identifier.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API-key environment variable.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create configuration.

#### Dependencies
- `AiGenerationConfig`, `ConvertToRecord`, `Nullable`.
- `Collections`, `List`, `Objects`.
- Lombok `EqualsAndHashCode`, `ToString`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `"modelPath"`.
- Null strings in selected setters restore the corresponding `AiGenerationConfig` default; null list inputs become empty lists.

#### Concurrency
- `LlamaCppJniConfig` is immutable after construction.
- `Builder` is mutable and explicitly not thread-safe.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:22Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures GGUF model loading and text generation, including sampling, hardware offload, caching, remote-provider settings, reasoning, and stop sequences.

#### Purpose
- Captures immutable llama.cpp JNI and OpenAI-compatible generation settings with defaults sourced from `AiGenerationConfig`.
- Provides a fluent builder requiring a model path and allowing optional overrides.

#### Type
- `LlamaCppJniConfig` — `final` class; annotated `@ConvertToRecord`, Lombok `@ToString` and `@EqualsAndHashCode`.
- Nested `Builder` — `public static final` class.
- 41 configuration fields; all final in the config. Nullable `Boolean chatTemplateEnableThinking` distinguishes unset from true/false.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling values, generation limits, CPU/GPU and KV-cache controls, provider/API metadata, reasoning and DRY settings, plus DRY sequence breakers and stop strings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; both lists default to empty lists.

#### Output
- `build()` produces an immutable `LlamaCppJniConfig` containing builder values.
- List accessors return unmodifiable views of configured DRY sequence breakers and stop strings.
- Lombok generates value-based `equals`, `hashCode`, and `toString` over all 41 fields.

#### Core logic
- Builder initializes optional settings from `AiGenerationConfig` defaults; callers override them through fluent setters.
- Constructor copies every builder field without further normalization.
- Null string setters restore their corresponding defaults; null list setters restore empty lists. Null thinking mode remains unset.
- `apiKeyEnv` stores the environment-variable name, not the API key; empty provider/model/reasoning values have documented omission or fallback semantics.
- Builder is mutable and not thread-safe; the produced config is immutable.

#### Public API
- `builder(String) -> Builder` — start configuration with model path.
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
- `cacheReuse() -> int` — KV prefix reuse chunk.
- `gpuLayers() -> int` — GPU offload layers.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot KV cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repetition window.
- `cacheTypeK() -> String` — K-cache data type.
- `cacheTypeV() -> String` — V-cache data type.
- `flashAttn() -> boolean` — Flash Attention flag.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — AI provider selection.
- `remoteModel() -> String` — remote model identifier.
- `baseUrl() -> String` — OpenAI-compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning-effort setting.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY sampling multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY penalty window.
- `drySequenceBreakers() -> List<String>` — unmodifiable DRY breakers.
- `stopStrings() -> List<String>` — unmodifiable stop strings.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output limit.
- `Builder.temperature(float) -> Builder` — set temperature.
- `Builder.threads(int) -> Builder` — set CPU threads.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set template thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt caching.
- `Builder.swaFull(boolean) -> Builder` — set full SWA cache.
- `Builder.cacheReuse(int) -> Builder` — set KV prefix reuse chunk.
- `Builder.gpuLayers(int) -> Builder` — set GPU offload layers.
- `Builder.seed(int) -> Builder` — set RNG seed.
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
- `Builder.threadsBatch(int) -> Builder` — set batch threads.
- `Builder.mainGpu(int) -> Builder` — set primary GPU.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set provider.
- `Builder.remoteModel(String) -> Builder` — set remote model.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API-key variable name.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY penalty window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create immutable config.

#### Dependencies
- `AiGenerationConfig`, `ConvertToRecord`, `Nullable`.
- `Collections`, `List`, `Objects`.
- Lombok `EqualsAndHashCode`, `ToString`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Null string builder inputs restore defaults for lazy mode, cache types, devices, provider, remote model, base URL, API-key variable, and reasoning effort.
- Null list inputs become empty lists; nullable thinking mode is preserved.

#### Concurrency
- `LlamaCppJniConfig` is immutable; builder is explicitly not thread-safe.

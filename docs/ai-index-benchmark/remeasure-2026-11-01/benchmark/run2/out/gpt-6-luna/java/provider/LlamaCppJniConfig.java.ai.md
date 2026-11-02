### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:42Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI model loading and text generation, including sampling, hardware, caching, remote-provider, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` is an immutable value configuration for a GGUF model and generation settings.
- Nested `Builder` applies `AiGenerationConfig` defaults and allows named overrides.

#### Type
- `LlamaCppJniConfig` — `final class`; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- `Builder` — nested `public static final class`.
- 41 private final configuration fields; `chatTemplateEnableThinking` is nullable.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- Builder setters accept generation, sampling, caching, CPU/GPU, provider, reasoning, DRY, and stop-string options.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose configuration values; list accessors return unmodifiable views.
- No resources are read or written.

#### Core logic
- `Builder` initializes every option to its corresponding default; model path is required and null-checked.
- Fluent setters update individual options; nullable strings restore their configured defaults, and nullable lists restore empty lists.
- The private config constructor copies all builder values without further normalization.
- `chatTemplateEnableThinking` preserves `null` to represent an unset option.
- DRY sequence breakers and stop strings are exposed through unmodifiable list wrappers.

#### Public API
- `builder(String) -> Builder` — starts defaulted configuration.
- `modelPath() -> String` — returns model path.
- `contextSize() -> int` — returns context window.
- `maxOutputTokens() -> int` — returns output token limit.
- `temperature() -> float` — returns sampling temperature.
- `threads() -> int` — returns CPU thread count.
- `topP() -> float` — returns nucleus threshold.
- `topK() -> int` — returns top-k limit.
- `minP() -> float` — returns min-p threshold.
- `topNSigma() -> float` — returns top-n-sigma threshold.
- `repeatPenalty() -> float` — returns repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — returns optional thinking mode.
- `cachePrompt() -> boolean` — returns prompt-cache setting.
- `swaFull() -> boolean` — returns full SWA cache setting.
- `cacheReuse() -> int` — returns KV prefix-reuse chunk size.
- `gpuLayers() -> int` — returns GPU offload layers.
- `seed() -> int` — returns generation RNG seed.
- `cpuMoeLayers() -> int` — returns CPU MoE layer count.
- `cpuFfnLayers() -> int` — returns CPU FFN layer count.
- `kvUnifiedPerSlot() -> int` — returns per-slot KV cap.
- `lazyMode() -> String` — returns tensor-read laziness mode.
- `repeatLastN() -> int` — returns repetition window.
- `cacheTypeK() -> String` — returns K-cache data type.
- `cacheTypeV() -> String` — returns V-cache data type.
- `flashAttn() -> boolean` — returns Flash Attention setting.
- `batchSize() -> int` — returns logical batch size.
- `ubatchSize() -> int` — returns physical batch size.
- `threadsBatch() -> int` — returns batch-processing thread count.
- `mainGpu() -> int` — returns primary GPU index.
- `devices() -> String` — returns device selection.
- `provider() -> String` — returns provider selection.
- `remoteModel() -> String` — returns remote model ID.
- `baseUrl() -> String` — returns API base URL.
- `apiKeyEnv() -> String` — returns API-key environment variable name.
- `reasoningEffort() -> String` — returns reasoning-effort setting.
- `reasoningBudgetTokens() -> int` — returns reasoning token budget.
- `dryMultiplier() -> float` — returns DRY multiplier.
- `dryBase() -> float` — returns DRY base.
- `dryAllowedLength() -> int` — returns DRY allowed length.
- `dryPenaltyLastN() -> int` — returns DRY look-back window.
- `drySequenceBreakers() -> List<String>` — returns unmodifiable DRY breakers.
- `stopStrings() -> List<String>` — returns unmodifiable stop strings.
- `Builder.contextSize(int) -> Builder` — sets context window.
- `Builder.maxOutputTokens(int) -> Builder` — sets output token limit.
- `Builder.temperature(float) -> Builder` — sets sampling temperature.
- `Builder.threads(int) -> Builder` — sets CPU thread count.
- `Builder.topP(float) -> Builder` — sets nucleus threshold.
- `Builder.topK(int) -> Builder` — sets top-k limit.
- `Builder.minP(float) -> Builder` — sets min-p threshold.
- `Builder.topNSigma(float) -> Builder` — sets top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — sets repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — sets optional thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — sets prompt caching.
- `Builder.swaFull(boolean) -> Builder` — sets full SWA cache.
- `Builder.cacheReuse(int) -> Builder` — sets KV prefix reuse.
- `Builder.gpuLayers(int) -> Builder` — sets GPU offload layers.
- `Builder.seed(int) -> Builder` — sets RNG seed.
- `Builder.cpuMoeLayers(int) -> Builder` — sets CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` — sets CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` — sets per-slot KV cap.
- `Builder.lazyMode(String) -> Builder` — sets tensor-read mode.
- `Builder.repeatLastN(int) -> Builder` — sets repetition window.
- `Builder.cacheTypeK(String) -> Builder` — sets K-cache type.
- `Builder.cacheTypeV(String) -> Builder` — sets V-cache type.
- `Builder.flashAttn(boolean) -> Builder` — sets Flash Attention.
- `Builder.batchSize(int) -> Builder` — sets logical batch size.
- `Builder.ubatchSize(int) -> Builder` — sets physical batch size.
- `Builder.threadsBatch(int) -> Builder` — sets batch thread count.
- `Builder.mainGpu(int) -> Builder` — sets primary GPU.
- `Builder.devices(String) -> Builder` — sets device selection.
- `Builder.provider(String) -> Builder` — sets provider.
- `Builder.remoteModel(String) -> Builder` — sets remote model ID.
- `Builder.baseUrl(String) -> Builder` — sets API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — sets API-key environment variable name.
- `Builder.reasoningEffort(String) -> Builder` — sets reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — sets reasoning budget.
- `Builder.dryMultiplier(float) -> Builder` — sets DRY multiplier.
- `Builder.dryBase(float) -> Builder` — sets DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — sets DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — sets DRY look-back window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — sets DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — sets stop strings.
- `Builder.build() -> LlamaCppJniConfig` — creates immutable configuration.

#### Dependencies
- `AiGenerationConfig` — default values for generation settings.
- `ConvertToRecord` — record-migration marker.
- `Nullable` — nullable thinking-mode annotation.
- `Collections`, `List`, `Objects` — list views, collection defaults, and null-checking.
- Lombok `EqualsAndHashCode`, `ToString` — generated value methods.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- Null `lazyMode`, cache types, devices, provider, remote-model, base URL, API-key environment, and reasoning-effort values restore their `AiGenerationConfig` defaults.
- Null breaker and stop-string lists become empty lists.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors prevent mutation through returned views.
- `Builder` is documented as not thread-safe.

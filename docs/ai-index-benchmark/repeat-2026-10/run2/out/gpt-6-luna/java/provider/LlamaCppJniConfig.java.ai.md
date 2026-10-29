### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:41Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` carries model-generation settings with defaults sourced from `AiGenerationConfig`.
- `Builder` provides named fluent configuration before creating the value object.

#### Type
- `public final class LlamaCppJniConfig`; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder`.
- 41 private final configuration fields; `chatTemplateEnableThinking` is nullable.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Input
- `builder(String modelPath)` requires a model path.
- Builder setters accept sampling, CPU/GPU, KV-cache, batch, provider/API, reasoning, DRY, and stop-string options.
- All builder options start at `AiGenerationConfig.DEFAULT_*`; sequence-breaker and stop-string lists start empty.

#### Output
- `build()` creates a `LlamaCppJniConfig` containing the builder’s values.
- Accessors expose configuration values; list accessors return unmodifiable views.
- No files, model resources, or network calls are performed.

#### Core logic
- `Builder` requires non-null `modelPath`, then initializes other values to defaults.
- Fluent setters replace values; selected nullable string/list setters restore their defaults on `null`.
- The private config constructor copies every builder field without further normalization.
- List fields are retained by reference; getters wrap them as unmodifiable views, not copies.
- Nullable thinking mode distinguishes unset from explicitly enabled or disabled.

#### Public API
- `builder(String) -> Builder` — start with model path and defaults.
- `modelPath() -> String` — return GGUF path.
- `contextSize() -> int` — return context window.
- `maxOutputTokens() -> int` — return output token limit.
- `temperature() -> float` — return sampling temperature.
- `threads() -> int` — return CPU thread count.
- `topP() -> float` — return nucleus threshold.
- `topK() -> int` — return top-k limit.
- `minP() -> float` — return min-p threshold.
- `topNSigma() -> float` — return top-n-sigma threshold.
- `repeatPenalty() -> float` — return repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — return thinking mode.
- `cachePrompt() -> boolean` — return prompt-cache setting.
- `swaFull() -> boolean` — return full SWA cache setting.
- `cacheReuse() -> int` — return cache-reuse chunk size.
- `gpuLayers() -> int` — return GPU offload layers.
- `seed() -> int` — return generation seed.
- `cpuMoeLayers() -> int` — return CPU MoE layers.
- `cpuFfnLayers() -> int` — return CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — return per-slot KV cap.
- `lazyMode() -> String` — return tensor-read mode.
- `repeatLastN() -> int` — return repetition window.
- `cacheTypeK() -> String` — return K-cache type.
- `cacheTypeV() -> String` — return V-cache type.
- `flashAttn() -> boolean` — return Flash Attention setting.
- `batchSize() -> int` — return logical batch size.
- `ubatchSize() -> int` — return physical batch size.
- `threadsBatch() -> int` — return batch thread count.
- `mainGpu() -> int` — return primary GPU index.
- `devices() -> String` — return device selection.
- `provider() -> String` — return provider identifier.
- `remoteModel() -> String` — return remote model ID.
- `baseUrl() -> String` — return API base URL.
- `apiKeyEnv() -> String` — return API-key environment variable name.
- `reasoningEffort() -> String` — return reasoning-effort setting.
- `reasoningBudgetTokens() -> int` — return reasoning token budget.
- `dryMultiplier() -> float` — return DRY multiplier.
- `dryBase() -> float` — return DRY base.
- `dryAllowedLength() -> int` — return DRY allowed length.
- `dryPenaltyLastN() -> int` — return DRY penalty window.
- `drySequenceBreakers() -> List<String>` — return unmodifiable breaker view.
- `stopStrings() -> List<String>` — return unmodifiable stop-string view.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output token limit.
- `Builder.temperature(float) -> Builder` — set sampling temperature.
- `Builder.threads(int) -> Builder` — set CPU thread count.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt caching.
- `Builder.swaFull(boolean) -> Builder` — set full SWA cache.
- `Builder.cacheReuse(int) -> Builder` — set cache-reuse chunk size.
- `Builder.gpuLayers(int) -> Builder` — set GPU offload layers.
- `Builder.seed(int) -> Builder` — set generation seed.
- `Builder.cpuMoeLayers(int) -> Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` — set per-slot KV cap.
- `Builder.lazyMode(String) -> Builder` — set tensor-read mode.
- `Builder.repeatLastN(int) -> Builder` — set repetition window.
- `Builder.cacheTypeK(String) -> Builder` — set K-cache type.
- `Builder.cacheTypeV(String) -> Builder` — set V-cache type.
- `Builder.flashAttn(boolean) -> Builder` — set Flash Attention.
- `Builder.batchSize(int) -> Builder` — set logical batch size.
- `Builder.ubatchSize(int) -> Builder` — set physical batch size.
- `Builder.threadsBatch(int) -> Builder` — set batch thread count.
- `Builder.mainGpu(int) -> Builder` — set primary GPU index.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set provider identifier.
- `Builder.remoteModel(String) -> Builder` — set remote model ID.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API-key variable name.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning token budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY penalty window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create configuration.

#### Dependencies
- `AiGenerationConfig` — configuration defaults.
- `ConvertToRecord` — migration marker.
- Lombok `EqualsAndHashCode`, `ToString`.
- `Collections`, `List`, `Objects`; JSpecify `Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Null `lazyMode`, cache types, devices, provider/API settings, and list arguments restore their respective defaults; nullable thinking mode remains unset when passed `null`.

#### Concurrency
- `LlamaCppJniConfig` fields are final; the builder is documented as not thread-safe.
- List values are not defensively copied; unmodifiable getter views do not prevent mutation through another reference.

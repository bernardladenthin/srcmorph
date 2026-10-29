### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:51Z
- G: 
- A: 0.0.0
- X: file
---
> Configures local llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` holds generation and provider settings for a model.
- `Builder` applies `AiGenerationConfig` defaults and constructs the immutable configuration.

#### Type
- `public final class LlamaCppJniConfig`; annotated `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.
- Nested `public static final class Builder`.
- 41 final configuration fields; `chatTemplateEnableThinking` is nullable. Lombok generates value-based equality, hash code, and string representation.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling, threading, caching, GPU/device, provider/API, reasoning, DRY, and stop-string options.
- Builder defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose all 41 configured values; list accessors return unmodifiable views.
- No files, network calls, or other external resources are read or written.

#### Core logic
- Builder initializes optional settings to configured defaults and requires `modelPath` at construction.
- Setters update builder state and return the builder; selected nullable string and list inputs restore defaults when null.
- Private config constructor copies builder fields without further normalization.
- `chatTemplateEnableThinking == null` represents an unset option; empty strings and sentinel values retain their documented provider meanings.

#### Public API
- `builder(String) -> Builder` — start with defaults.
- `modelPath() -> String` — return GGUF path.
- `contextSize() -> int` — return context window.
- `maxOutputTokens() -> int` — return output-token limit.
- `temperature() -> float` — return sampling temperature.
- `threads() -> int` — return CPU thread count.
- `topP() -> float` — return nucleus threshold.
- `topK() -> int` — return top-k limit.
- `minP() -> float` — return min-p threshold.
- `topNSigma() -> float` — return top-n-sigma threshold.
- `repeatPenalty() -> float` — return repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — return thinking-mode override.
- `cachePrompt() -> boolean` — return prompt-cache setting.
- `swaFull() -> boolean` — return full SWA-cache setting.
- `cacheReuse() -> int` — return prefix-reuse chunk size.
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
- `threadsBatch() -> int` — return batch-processing threads.
- `mainGpu() -> int` — return primary GPU index.
- `devices() -> String` — return device selection.
- `provider() -> String` — return provider identifier.
- `remoteModel() -> String` — return remote model ID.
- `baseUrl() -> String` — return API base URL.
- `apiKeyEnv() -> String` — return API-key environment-variable name.
- `reasoningEffort() -> String` — return reasoning-effort setting.
- `reasoningBudgetTokens() -> int` — return reasoning-token budget.
- `dryMultiplier() -> float` — return DRY multiplier.
- `dryBase() -> float` — return DRY base.
- `dryAllowedLength() -> int` — return DRY allowed length.
- `dryPenaltyLastN() -> int` — return DRY look-back window.
- `drySequenceBreakers() -> List<String>` — return unmodifiable breakers.
- `stopStrings() -> List<String>` — return unmodifiable stop strings.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output-token limit.
- `Builder.temperature(float) -> Builder` — set sampling temperature.
- `Builder.threads(int) -> Builder` — set CPU thread count.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set thinking override.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt caching.
- `Builder.swaFull(boolean) -> Builder` — set full SWA cache.
- `Builder.cacheReuse(int) -> Builder` — set prefix-reuse chunk size.
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
- `Builder.threadsBatch(int) -> Builder` — set batch-processing threads.
- `Builder.mainGpu(int) -> Builder` — set primary GPU index.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set provider identifier.
- `Builder.remoteModel(String) -> Builder` — set remote model ID.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API-key environment-variable name.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning-token budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — construct immutable config.

#### Dependencies
- `AiGenerationConfig` — default values.
- `ConvertToRecord` — record-migration marker.
- Lombok `EqualsAndHashCode`, `ToString`.
- JSpecify `Nullable`.
- Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- Null `lazyMode`, cache types, device/provider/API/reasoning strings restore corresponding defaults; null lists become empty lists.
- Other setter inputs are assigned without validation.

#### Concurrency
- Config fields are final; list accessors expose unmodifiable views.
- Builder is documented as not thread-safe.

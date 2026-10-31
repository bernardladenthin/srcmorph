### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:58Z
- G: 
- A: 0.0.0
- X: file
---
> Configures local and remote LLM text generation via llama.cpp JNI and OpenAI-compatible endpoints.

#### Purpose
- Immutable value object holding all 41 llama.cpp JNI / generation knobs for AI source transformation.
- Single-defaults builder entry point seeded from `AiGenerationConfig.DEFAULT_*`.

#### Type
- `public final class LlamaCppJniConfig`, nested `public static final class Builder`.
- Annotations: `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode` over all fields.

#### Input
- `builder(String modelPath)`: GGUF path, `Objects.requireNonNull`.
- `Builder` setters: ints, floats, booleans, `String`, `@Nullable Boolean chatTemplateEnableThinking`, `List<String> drySequenceBreakers/stopStrings`.
- Builder defaults read from `AiGenerationConfig.DEFAULT_*`; nullable String/List setters coerce `null` to default/empty.
- Private constructor consumes populated `Builder`.

#### Output
- 41 record-style accessors exposing model, sampling, KV-cache, GPU, DRY, remote-provider state.
- `drySequenceBreakers() -> List<String>` and `stopStrings() -> List<String>` return unmodifiable views.
- `Builder.build() -> LlamaCppJniConfig` produces immutable config.

#### Core logic
- `LlamaCppJniConfig` stores 41 final fields verbatim; no normalization/validation in constructor.
- `Builder` centralizes defaults so `LlamaCppJniConfigFactory` fallback avoids restating them.
- String setters `lazyMode, cacheTypeK/V, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort` restore `DEFAULT_*` on `null`.
- List setters `drySequenceBreakers, stopStrings` restore empty list on `null`.
- `chatTemplateEnableThinking` tri-state `Boolean`: `null` omits `enable_thinking` kwarg, defers to model template default.
- Sentinel conventions preserved: `-1` leave binding default, `0` disable, empty string omit kwarg.
- Lombok generates full value `equals/hashCode/toString`; float compare via `Float.floatToIntBits`.

#### Public API
- `builder(String modelPath) -> Builder` — start config with defaults.
- `modelPath() -> String` — return GGUF path.
- `contextSize() -> int` — return context window.
- `maxOutputTokens() -> int` — return output limit.
- `temperature() -> float` — return sampling temperature.
- `threads() -> int` — return CPU threads.
- `topP() -> float` — return nucleus threshold.
- `topK() -> int` — return top-k limit.
- `minP() -> float` — return min-p threshold.
- `topNSigma() -> float` — return top-n-sigma threshold.
- `repeatPenalty() -> float` — return repetition penalty.
- `chatTemplateEnableThinking() -> Boolean` — return thinking mode nullable.
- `cachePrompt() -> boolean` — return prompt-cache flag.
- `swaFull() -> boolean` — return full SWA flag.
- `cacheReuse() -> int` — return cache-reuse chunk.
- `gpuLayers() -> int` — return GPU offload layers.
- `seed() -> int` — return RNG seed.
- `cpuMoeLayers() -> int` — return CPU MoE layers.
- `cpuFfnLayers() -> int` — return CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — return unified KV cap.
- `lazyMode() -> String` — return tensor-read laziness.
- `repeatLastN() -> int` — return repeat window.
- `cacheTypeK() -> String` — return K cache dtype.
- `cacheTypeV() -> String` — return V cache dtype.
- `flashAttn() -> boolean` — return Flash Attention flag.
- `batchSize() -> int` — return logical batch size.
- `ubatchSize() -> int` — return physical batch size.
- `threadsBatch() -> int` — return batch thread count.
- `mainGpu() -> int` — return primary GPU index.
- `devices() -> String` — return device selection.
- `provider() -> String` — return AI provider id.
- `remoteModel() -> String` — return remote model id.
- `baseUrl() -> String` — return OpenAI-compatible base URL.
- `apiKeyEnv() -> String` — return API key env name.
- `reasoningEffort() -> String` — return reasoning effort.
- `reasoningBudgetTokens() -> int` — return reasoning token budget.
- `dryMultiplier() -> float` — return DRY multiplier.
- `dryBase() -> float` — return DRY base.
- `dryAllowedLength() -> int` — return DRY allowed length.
- `dryPenaltyLastN() -> int` — return DRY look-back window.
- `drySequenceBreakers() -> List<String>` — return DRY breakers view.
- `stopStrings() -> List<String>` — return stop strings view.
- `Builder.contextSize(int) -> Builder` — set context window.
- `Builder.maxOutputTokens(int) -> Builder` — set output limit.
- `Builder.temperature(float) -> Builder` — set sampling temperature.
- `Builder.threads(int) -> Builder` — set CPU threads.
- `Builder.topP(float) -> Builder` — set nucleus threshold.
- `Builder.topK(int) -> Builder` — set top-k limit.
- `Builder.minP(float) -> Builder` — set min-p threshold.
- `Builder.topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(Boolean) -> Builder` — set thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` — set prompt-cache flag.
- `Builder.swaFull(boolean) -> Builder` — set full SWA flag.
- `Builder.cacheReuse(int) -> Builder` — set cache-reuse chunk.
- `Builder.gpuLayers(int) -> Builder` — set GPU layers.
- `Builder.seed(int) -> Builder` — set RNG seed.
- `Builder.cpuMoeLayers(int) -> Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` — set unified KV cap.
- `Builder.lazyMode(String) -> Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int) -> Builder` — set repeat window.
- `Builder.cacheTypeK(String) -> Builder` — set K cache dtype.
- `Builder.cacheTypeV(String) -> Builder` — set V cache dtype.
- `Builder.flashAttn(boolean) -> Builder` — set Flash Attention flag.
- `Builder.batchSize(int) -> Builder` — set logical batch size.
- `Builder.ubatchSize(int) -> Builder` — set physical batch size.
- `Builder.threadsBatch(int) -> Builder` — set batch thread count.
- `Builder.mainGpu(int) -> Builder` — set primary GPU index.
- `Builder.devices(String) -> Builder` — set device selection.
- `Builder.provider(String) -> Builder` — set AI provider id.
- `Builder.remoteModel(String) -> Builder` — set remote model id.
- `Builder.baseUrl(String) -> Builder` — set API base URL.
- `Builder.apiKeyEnv(String) -> Builder` — set API key env name.
- `Builder.reasoningEffort(String) -> Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` — set reasoning budget.
- `Builder.dryMultiplier(float) -> Builder` — set DRY multiplier.
- `Builder.dryBase(float) -> Builder` — set DRY base.
- `Builder.dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — build immutable config.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.
- `lombok.EqualsAndHashCode`, `lombok.ToString`.
- `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.ConvertToRecord`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `NullPointerException` if `builder(null)` modelPath via `Objects.requireNonNull`.
- Null-tolerant setters fallback to defaults; no other validation thrown.

#### Concurrency
- `LlamaCppJniConfig` immutable, safely shareable; list getters wrap with unmodifiable view.
- `Builder` explicitly not thread-safe, single-thread fill-then-build.

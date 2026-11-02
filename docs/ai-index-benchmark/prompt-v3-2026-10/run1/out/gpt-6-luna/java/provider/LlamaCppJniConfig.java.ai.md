### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:49:12Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI model loading, generation, sampling, caching, hardware, provider, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` is an immutable value configuration for llama.cpp JNI and OpenAI-compatible generation.
- Its builder applies `AiGenerationConfig` defaults and allows named overrides.

#### Type
- `public final class LlamaCppJniConfig` — class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder` — fluent configuration builder.
- `LlamaCppJniConfig` has 41 `private final` fields; Lombok generates value-based equality, hashing, and string representation.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- `Builder` accepts optional overrides for generation and sampling, llama.cpp runtime and hardware, provider/API, reasoning, DRY sampling, and stop settings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list settings default to empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose configuration values; list accessors return unmodifiable views.
- No files or external resources are read or written.

#### Core logic
- `Builder` holds the required model path and initializes all other options to defaults.
- Fluent setters update individual options; nullable strings and list parameters restore their defaults when passed `null`.
- The private constructor copies all builder values into final fields without further normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`.
- `@EqualsAndHashCode` and `@ToString` provide value semantics over the fields.

#### Public API
- `builder(String) -> Builder` — start with defaults.
- `modelPath() -> String` — return GGUF model path.
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
- `cachePrompt() -> boolean` — return prompt caching flag.
- `swaFull() -> boolean` — return full SWA cache flag.
- `cacheReuse() -> int` — return KV prefix-reuse chunk size.
- `gpuLayers() -> int` — return GPU layer count.
- `seed() -> int` — return generation seed.
- `cpuMoeLayers() -> int` — return CPU MoE layer count.
- `cpuFfnLayers() -> int` — return CPU FFN layer count.
- `kvUnifiedPerSlot() -> int` — return per-slot KV cap.
- `lazyMode() -> String` — return tensor-read mode.
- `repeatLastN() -> int` — return repetition window.
- `cacheTypeK() -> String` — return K cache type.
- `cacheTypeV() -> String` — return V cache type.
- `flashAttn() -> boolean` — return Flash Attention flag.
- `batchSize() -> int` — return logical batch size.
- `ubatchSize() -> int` — return physical batch size.
- `threadsBatch() -> int` — return batch thread count.
- `mainGpu() -> int` — return primary GPU index.
- `devices() -> String` — return device selection.
- `provider() -> String` — return provider identifier.
- `remoteModel() -> String` — return remote model ID.
- `baseUrl() -> String` — return API base URL.
- `apiKeyEnv() -> String` — return API key environment variable name.
- `reasoningEffort() -> String` — return reasoning effort.
- `reasoningBudgetTokens() -> int` — return reasoning token budget.
- `dryMultiplier() -> float` — return DRY multiplier.
- `dryBase() -> float` — return DRY base.
- `dryAllowedLength() -> int` — return DRY allowed length.
- `dryPenaltyLastN() -> int` — return DRY penalty window.
- `drySequenceBreakers() -> List<String>` — return unmodifiable DRY breakers.
- `stopStrings() -> List<String>` — return unmodifiable stop strings.
- `Builder.contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>) -> Builder` — set option and return builder.
- `Builder.build() -> LlamaCppJniConfig` — build immutable configuration.

#### Dependencies
- `AiGenerationConfig` — configuration defaults.
- `ConvertToRecord` — migration marker.
- Lombok `@EqualsAndHashCode`, `@ToString`.
- JSpecify `@Nullable`.
- Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `modelPath`.
- Nullable string/list setters replace `null` with the matching default or empty list.
- Numeric and non-null string values receive no validation in this file.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors expose unmodifiable views.
- `Builder` is explicitly not thread-safe and is intended for single-threaded construction.

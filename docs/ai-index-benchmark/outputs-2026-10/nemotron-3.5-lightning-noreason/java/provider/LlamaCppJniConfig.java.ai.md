### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:41Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and configures parameters for the llama.cpp JNI provider, enabling model loading, generation settings, and provider-specific options.

#### Purpose
Configuration carrier for the llama.cpp JNI provider with thirty-seven value parameters controlling model loading, generation, and provider interaction.

#### Type
Class: `public final class LlamaCppJniConfig` with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`. Implements value semantics via Lombok over thirty-seven fields. No extends / implements.

#### Input
Constructed via `builder(String)` static factory. Builder sets all fields from `AiGenerationConfig.DEFAULT_*` defaults; `modelPath` is required, others are optional. Fields read during `new LlamaCppJniConfig(Builder)`.

#### Output
Immutable configuration object exposing thirty-seven read-only accessor methods (`modelPath()`, `contextSize()`, ..., `stopStrings()`). Returns unmodifiable lists for `drySequenceBreakers()` and `stopStrings()`. No side effects beyond value return.

#### Core logic
- Private constructor copies all thirty-seven fields from `Builder` instance.
- Builder initializes each field to matching `AiGenerationConfig.DEFAULT_*` constant; `modelPath` enforced non-null via `Objects.requireNonNull`.
- `drySequenceBreakers` and `stopStrings` default to empty list; setters accept `null` to restore empty default.
- Builder is mutable, not thread-safe; produced `LlamaCppJniConfig` is immutable value type.

#### Public API
Thirty-seven accessor methods: `modelPath() -> String`, `contextSize() -> int`, `maxOutputTokens() -> int`, `temperature() -> float`, `threads() -> int`, `topP() -> float`, `topK() -> int`, `minP() -> float`, `topNSigma() -> float`, `repeatPenalty() -> float`, `chatTemplateEnableThinking() -> @Nullable Boolean`, `cachePrompt() -> boolean`, `swaFull() -> boolean`, `cacheReuse() -> int`, `gpuLayers() -> int`, `seed() -> int`, `cpuMoeLayers() -> int`, `cpuFfnLayers() -> int`, `kvUnifiedPerSlot() -> int`, `lazyMode() -> String`, `repeatLastN() -> int`, `cacheTypeK() -> String`, `cacheTypeV() -> String`, `flashAttn() -> boolean`, `batchSize() -> int`, `ubatchSize() -> int`, `threadsBatch() -> int`, `mainGpu() -> int`, `devices() -> String`, `provider() -> String`, `remoteModel() -> String`, `baseUrl() -> String`, `apiKeyEnv() -> String`, `reasoningEffort() -> String`, `reasoningBudgetTokens() -> int`, `dryMultiplier() -> float`, `dryBase() -> float`, `dryAllowedLength() -> int`, `dryPenaltyLastN() -> int`, `drySequenceBreakers() -> List<String>`, `stopStrings() -> List<String>`.

#### Dependencies
`java.util.Collections`, `java.util.Objects`, `java.util.List`, `lombok.EqualsAndHashCode`, `lombok.ToString`, `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.ConvertToRecord`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
`NullPointerException` thrown if `modelPath` is `null` in `builder(String)`. Builder setter `apiKeyEnv(String)` accepts `null` to restore default; `drySequenceBreakers(List<String>)` and `stopStrings(List<String>)` accept `null` to restore empty default.

#### Concurrency
Builder not thread-safe; intended for single-threaded fill-then-build pattern. Produced `LlamaCppJniConfig` is immutable with value semantics.

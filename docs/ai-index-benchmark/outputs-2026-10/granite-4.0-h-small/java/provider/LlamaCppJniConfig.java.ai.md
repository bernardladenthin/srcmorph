### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:01:23Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, built through a fluent Builder with thirty-seven fields.

#### Purpose
- Holds all runtime parameters needed to configure the llama.cpp JNI provider.
- Provides a strongly-typed, immutable value object to avoid accidental misconfiguration.

#### Type
- `class` named `LlamaCppJniConfig`.
- `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` from Lombok.
- 37 private final fields covering model path, context size, sampling parameters, threading, GPU offload, cache behavior, and provider-specific options.
- Private positional constructor; public static `builder(String)` factory.

#### Input
- `Builder` populated by callers.
- Fields initialized from `AiGenerationConfig.DEFAULT_*` unless overridden.
- `modelPath` required; all other fields optional with sensible defaults.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Accessor methods for each field (`modelPath()`, `contextSize()`, etc.).
- `build()` method returns the configured instance.

#### Core logic
- Builder pattern with chained setters, each returning `this`.
- Validation only in the builder: required `modelPath` checked, no nulls allowed.
- No normalization performed; builder ensures values are already normalized.
- Private constructor copies all fields directly from the builder.

#### Public API
- `builder(String modelPath) -> Builder`
- `modelPath() -> String`
- `contextSize() -> int`
- `maxOutputTokens() -> int`
- `temperature() -> float`
- `threads() -> int`
- `topP() -> float`
- `topK() -> int`
- `minP() -> float`
- `topNSigma() -> float`
- `repeatPenalty() -> float`
- `chatTemplateEnableThinking() -> @Nullable Boolean`
- `cachePrompt() -> boolean`
- `swaFull() -> boolean`
- `cacheReuse() -> int`
- `gpuLayers() -> int`
- `seed() -> int`
- `cpuMoeLayers() -> int`
- `cpuFfnLayers() -> int`
- `kvUnifiedPerSlot() -> int`
- `lazyMode() -> String`
- `repeatLastN() -> int`
- `cacheTypeK() -> String`
- `cacheTypeV() -> String`
- `flashAttn() -> boolean`
- `batchSize() -> int`
- `ubatchSize() -> int`
- `threadsBatch() -> int`
- `mainGpu() -> int`
- `devices() -> String`
- `provider() -> String`
- `remoteModel() -> String`
- `baseUrl() -> String`
- `apiKeyEnv() -> String`
- `reasoningEffort() -> String`
- `reasoningBudgetTokens() -> int`
- `dryMultiplier() -> float`
- `dryBase() -> float`
- `dryAllowedLength() -> int`
- `dryPenaltyLastN() -> int`
- `drySequenceBreakers() -> List<String>`
- `stopStrings() -> List<String>`

#### Dependencies
- `java.util.Collections`, `java.util.List`.
- `java.util.Objects`.
- Lombok annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- `net.ladenthin.srcmorph.config.AiGenerationConfig` for default values.
- `org.jspecify.annotations.Nullable` for optional fields.

#### Exceptions / Errors
- `NullPointerException` if `modelPath` passed to `builder(String)` is null.
- No other explicit exception handling; relies on caller to provide valid numeric ranges.

#### Concurrency
- Immutable after construction; safe for concurrent use.
- Builder is not thread-safe; intended to be used on a single thread.

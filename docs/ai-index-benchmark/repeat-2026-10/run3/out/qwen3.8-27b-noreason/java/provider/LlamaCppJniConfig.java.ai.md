### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:42Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI and OpenAI-compatible AI generation parameters, built via a fluent Builder with centralized defaults.

#### Purpose
- Immutable value object encapsulating all 41 parameters for the `llama.cpp` JNI provider (sampling, KV-cache, GPU offload, DRY, stop strings) and OpenAI-compatible remote API settings.
- Centralizes default values from `AiGenerationConfig` to prevent drift between configuration sources.

#### Type
- `public final class LlamaCppJniConfig`
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok)
- Contains nested `public static final class Builder`

#### Input
- **Builder**: `modelPath` (required, non-null), 40 optional parameters initialized to `AiGenerationConfig.DEFAULT_*` constants.
- **Null Handling**: String setters (`lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`) and List setters (`drySequenceBreakers`, `stopStrings`) treat `null` as "restore default" or "empty list".

#### Output
- **Accessors**: 41 record-style public methods returning field values.
- **Immutability**: `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList` views.
- **Builder**: `build()` returns the immutable `LlamaCppJniConfig` instance.

#### Core logic
- **Builder Pattern**: Private constructor prevents direct instantiation; `builder(String modelPath)` is the sole entry point, enforcing non-null `modelPath` via `Objects.requireNonNull`.
- **Default Application**: Builder fields are initialized with `AiGenerationConfig.DEFAULT_*` constants, ensuring consistent fallbacks.
- **Null Safety**: Specific setters guard against `null` inputs by substituting the corresponding default constant or empty list.
- **Value Semantics**: Lombok generates `equals`, `hashCode`, and `toString` over all 41 fields, using `Float.floatToIntBits` for float comparisons.

#### Public API
- `builder(String modelPath) -> Builder` (static factory)
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
- **Builder Methods**: 41 fluent setters (one per field) + `build() -> LlamaCppJniConfig`

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (source of all default constants)
- `net.ladenthin.srcmorph.support.ConvertToRecord` (marker annotation)
- `org.jspecify.annotations.Nullable` (nullability annotation)
- `lombok.EqualsAndHashCode`, `lombok.ToString`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`

#### Exceptions / Errors
- `NullPointerException`: Thrown by `Builder` constructor if `modelPath` is `null`.

#### Concurrency
- **Immutable**: `LlamaCppJniConfig` is thread-safe after construction (all fields `final`, lists wrapped in unmodifiable views).
- **Builder**: Not thread-safe; intended for single-threaded use during construction.

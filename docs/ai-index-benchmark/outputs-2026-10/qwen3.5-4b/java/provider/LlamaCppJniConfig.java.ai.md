### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:03:20Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for llama.cpp JNI provider settings, enabling model path, sampling parameters, caching, and dry-run behavior.

#### Purpose
- Defines immutable configuration for llama.cpp JNI provider with 37 fields and a fluent builder pattern.
- Provides default values via `AiGenerationConfig` and supports optional overrides for inference tuning.

#### Type
- `public final class LlamaCppJniConfig` with nested static `Builder` class.
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Extends: none. Implements: none.

#### Input
- Constructor: `Builder` instance (37 fields).
- Builder setters: 36 optional parameters (all fields except `modelPath` which is required).
- Dependencies: `AiGenerationConfig` for defaults, `Collections` for unmodifiable lists.

#### Output
- Returns: 36 public accessor methods returning field values or unmodifiable views.
- Produces: Immutable `LlamaCppJniConfig` instance via `Builder.build()`.
- Side effects: None (pure getter/setter pattern).

#### Core logic
- Initializes 37 immutable fields from a `Builder` instance during construction.
- Applies default values from `AiGenerationConfig` for all fields except `modelPath`.
- Handles null inputs in builder setters by restoring defaults or using empty collections.
- Generates `equals`, `hashCode`, and `toString` via Lombok over all fields.

#### Public API
- `modelPath()` -> String
- `contextSize()` -> int
- `maxOutputTokens()` -> int
- `temperature()` -> float
- `threads()` -> int
- `topP()` -> float
- `topK()` -> int
- `minP()` -> float
- `topNSigma()` -> float
- `repeatPenalty()` -> float
- `chatTemplateEnableThinking()` -> Boolean
- `cachePrompt()` -> boolean
- `swaFull()` -> boolean
- `cacheReuse()` -> int
- `gpuLayers()` -> int
- `seed()` -> int
- `cpuMoeLayers()` -> int
- `cpuFfnLayers()` -> int
- `kvUnifiedPerSlot()` -> int
- `lazyMode()` -> String
- `repeatLastN()` -> int
- `cacheTypeK()` -> String
- `cacheTypeV()` -> String
- `flashAttn()` -> boolean
- `batchSize()` -> int
- `ubatchSize()` -> int
- `threadsBatch()` -> int
- `mainGpu()` -> int
- `devices()` -> String
- `provider()` -> String
- `remoteModel()` -> String
- `baseUrl()` -> String
- `apiKeyEnv()` -> String
- `reasoningEffort()` -> String
- `reasoningBudgetTokens()` -> int
- `dryMultiplier()` -> float
- `dryBase()` -> float
- `dryAllowedLength()` -> int
- `dryPenaltyLastN()` -> int
- `drySequenceBreakers()` -> List<String>
- `stopStrings()` -> List<String>
- Builder methods: 36 setters + 1 `build()` method (37 total)

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `NullPointerException` in `Builder` constructor if `modelPath` is null.
- No other exceptions; null handling via default restoration in setters.

#### Concurrency
- Thread-safe: All fields are final and immutable.
- Not thread-safe: `Builder` is mutable and single-threaded.
- No synchronization required for `LlamaCppJniConfig` usage.

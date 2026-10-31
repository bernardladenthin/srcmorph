### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:35Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration value object for the llama.cpp JNI AI generation provider, capturing all sampling, KV-cache, GPU, batching, DRY, and OpenAI-compatible model settings.

#### Purpose
- Record-shaped immutable configuration type holding every knob for the llama.cpp JNI provider (`LlamaCppJniConfig`).
- Centralizes all `AiGenerationConfig.DEFAULT_*` defaults through a fluent builder, so providers read one value object instead of restating defaults.

#### Type
- Final class : `public final class LlamaCppJniConfig`.
- Annotated `@ConvertToRecord` (future Java 17+ migration), Lombok `@ToString`, `@EqualsAndHashCode` (full value semantics, exact-bit float comparison).
- Nested `public static final class Builder` (fluent builder, not thread-safe).

#### Input
- Static factory `builder(String modelPath)` — `modelPath` is the only required value; null throws `NullPointerException` via `Objects.requireNonNull`.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` constants: 41 fields total (40 defaulted, 1 required).
- 40 optional builder setters, each taking one value; String setters (`lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`) restore default on `null`; List setters (`drySequenceBreakers`, `stopStrings`) treat `null` as empty list.
- Private constructor copies the 41 builder fields verbatim; no normalization occurs in the constructor (setters/normalization already handled in the builder).

#### Output
- 41 record-style accessors returning the immutable fields; `chatTemplateEnableThinking()` returns `@Nullable Boolean`.
- `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList(...)` views.
- `build()` returns a new immutable `LlamaCppJniConfig`; no other side effects.

#### Core logic
- The builder applies `AiGenerationConfig.DEFAULT_*` to all 40 optional fields at declaration time, making it the single source of defaults.
- `modelPath` is validated at builder construction (`Objects.requireNonNull(modelPath, "modelPath")`), so a half-formed builder cannot exist.
- Each setter returns `this` for chaining; String setters coalesce `null` back to the matching default; List setters coalesce `null` to an empty list.
- `build()` constructs the config via the private constructor, which simply assigns the 41 builder fields to final fields.
- The config is deeply immutable: final fields, unmodifiable list views, Lombok value semantics.

#### Public API
`LlamaCppJniConfig`:
- `builder(String modelPath) -> Builder` — starts builder with defaults, requires non-null path.
- Accessors (41 total): `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `minP()`, `topNSigma()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `cachePrompt()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `seed()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `repeatLastN()`, `cacheTypeK()`, `cacheTypeV()`, `flashAttn()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`, `provider()`, `remoteModel()`, `baseUrl()`, `apiKeyEnv()`, `reasoningEffort()`, `reasoningBudgetTokens()`, `dryMultiplier()`, `dryBase()`, `dryAllowedLength()`, `dryPenaltyLastN()`, `drySequenceBreakers()`, `stopStrings()` — each returns its immutable field value.

`Builder`:
- Setters (40 total), each `name(value) -> Builder`, chaining; grouped:
  - Sampling (10): `temperature`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`.
  - Context/batching (6): `contextSize`, `maxOutputTokens`, `batchSize`, `ubatchSize`, `threads`, `threadsBatch`.
  - KV-cache (6): `cachePrompt`, `swaFull`, `cacheReuse`, `cacheTypeK`, `cacheTypeV`, `kvUnifiedPerSlot`.
  - GPU (3): `gpuLayers`, `mainGpu`, `devices`.
  - CPU/execution (6): `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `lazyMode`, `repeatLastN`, `flashAttn`.
  - Remote/provider (7): `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `chatTemplateEnableThinking`.
  - Lists (2): `drySequenceBreakers`, `stopStrings`.
- `build() -> LlamaCppJniConfig` — constructs the immutable configuration.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.
- `lombok.EqualsAndHashCode`, `lombok.ToString`.
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (DEFAULT_* constants).
- `net.ladenthin.srcmorph.support.ConvertToRecord`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` and the Builder constructor when `modelPath` is null.
- Null-tolerant setters: String setters restore default on null; List setters treat null as empty.

#### Concurrency
- `LlamaCppJniConfig` is deeply immutable (final fields, unmodifiable list views) and safe for concurrent sharing.
- `Builder` is explicitly not thread-safe; designed to be filled and built on a single thread.

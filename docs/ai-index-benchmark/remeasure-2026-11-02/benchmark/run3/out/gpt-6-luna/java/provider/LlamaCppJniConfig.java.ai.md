### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:58:04Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures llama.cpp and OpenAI-compatible model generation, including sampling, compute, KV-cache, reasoning, and stop-sequence options.

#### Purpose
- `LlamaCppJniConfig` carries per-model generation and provider settings with defaults sourced from `AiGenerationConfig`.
- Its fluent `Builder` creates immutable configurations from a required GGUF model path.

#### Type
- `LlamaCppJniConfig` — `public final` class; annotated `@ConvertToRecord`, `@ToString`, and `@EqualsAndHashCode`.
- `Builder` — `public static final` nested class.
- `LlamaCppJniConfig` has 41 final fields; `chatTemplateEnableThinking` is nullable.

#### Input
- `builder(String modelPath)` requires a non-null model path.
- Builder setters accept sampling, thread, GPU, batching, cache, provider/API, reasoning, DRY, and stop-string settings.
- Unset values use `AiGenerationConfig.DEFAULT_*`; list settings default to empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose all 41 configuration values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over the configuration fields.

#### Core logic
- Builder initializes optional settings from `AiGenerationConfig` defaults and requires `modelPath` at construction.
- Setters assign supplied values; nullable string and list inputs restore their respective defaults when null.
- `build()` copies every builder value into the private configuration constructor without further normalization.
- `chatTemplateEnableThinking == null` represents an unset mode; empty provider-related strings and sentinel numeric values represent provider/binding defaults or disabled options.
- DRY settings cover multiplier, base, allowed length, penalty window, and sequence breakers; stop strings are configured separately.

#### Public API
- `builder(String) -> Builder` — start configuration for a model.
- Config accessors (41): `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `minP()`, `topNSigma()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `cachePrompt()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `seed()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `repeatLastN()`, `cacheTypeK()`, `cacheTypeV()`, `flashAttn()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`, `provider()`, `remoteModel()`, `baseUrl()`, `apiKeyEnv()`, `reasoningEffort()`, `reasoningBudgetTokens()`, `dryMultiplier()`, `dryBase()`, `dryAllowedLength()`, `dryPenaltyLastN()`, `drySequenceBreakers()`, `stopStrings()` — retrieve corresponding setting.
- Builder setters (40): `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)` — set corresponding option; return `Builder`.
- `build() -> LlamaCppJniConfig` — create immutable configuration.
- Lombok-generated `equals(Object)`, `hashCode()`, and `toString()` — value equality, hash, and representation.

#### Dependencies
- `AiGenerationConfig`, `ConvertToRecord`, `Nullable`
- `Collections`, `List`, `Objects`
- Lombok `EqualsAndHashCode`, `ToString`

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Nullable string setters restore defaults; nullable list setters replace null with an empty list.

#### Concurrency
- `LlamaCppJniConfig` is immutable; `Builder` is explicitly not thread-safe.

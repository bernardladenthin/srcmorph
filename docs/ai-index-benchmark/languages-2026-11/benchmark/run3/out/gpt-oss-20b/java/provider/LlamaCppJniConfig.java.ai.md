### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:04:33Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores llama.cpp runtime parameters.
- Provides immutable, record‑style accessors.

#### Type
`final class LlamaCppJniConfig` – immutable value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`. No inheritance.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller overrides.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all 37 builder fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Builder setters assign values; most accept `null` to restore defaults.
- `build()` returns new `LlamaCppJniConfig`.

#### Public API
- `static Builder builder(String)` – start builder with required model path.
- Getters (37 total):
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
- `Builder` (inner class) public methods:
  - `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`, `build() -> LlamaCppJniConfig`.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- Builder setters accept `null` to reset to defaults; no other validation.

#### Concurrency
- Immutable after construction; thread‑safe.
- Builder is not thread‑safe; intended single‑thread use.

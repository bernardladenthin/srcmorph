### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:53:54Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model and runtime options.

#### Purpose
- Stores llama.cpp JNI configuration values.
- Provides read‑only access to all settings.

#### Type
`final class LlamaCppJniConfig` – immutable value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`. Extends `Object`. No interfaces.

#### Input
- `Builder` instance supplied to private constructor.
- `Builder` fields initialized from `AiGenerationConfig.DEFAULT_*` or overridden by setters.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields to final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field; list getters wrap with `Collections.unmodifiableList`.
- `Builder` setters assign values and return `this` for chaining.
- `build()` returns a new `LlamaCppJniConfig`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.
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
- `Builder` methods (41 total):
  - `contextSize(int) -> Builder`
  - `maxOutputTokens(int) -> Builder`
  - `temperature(float) -> Builder`
  - `threads(int) -> Builder`
  - `topP(float) -> Builder`
  - `topK(int) -> Builder`
  - `minP(float) -> Builder`
  - `topNSigma(float) -> Builder`
  - `repeatPenalty(float) -> Builder`
  - `chatTemplateEnableThinking(@Nullable Boolean) -> Builder`
  - `cachePrompt(boolean) -> Builder`
  - `swaFull(boolean) -> Builder`
  - `cacheReuse(int) -> Builder`
  - `gpuLayers(int) -> Builder`
  - `seed(int) -> Builder`
  - `cpuMoeLayers(int) -> Builder`
  - `cpuFfnLayers(int) -> Builder`
  - `kvUnifiedPerSlot(int) -> Builder`
  - `lazyMode(String) -> Builder`
  - `repeatLastN(int) -> Builder`
  - `cacheTypeK(String) -> Builder`
  - `cacheTypeV(String) -> Builder`
  - `flashAttn(boolean) -> Builder`
  - `batchSize(int) -> Builder`
  - `ubatchSize(int) -> Builder`
  - `threadsBatch(int) -> Builder`
  - `mainGpu(int) -> Builder`
  - `devices(String) -> Builder`
  - `provider(String) -> Builder`
  - `remoteModel(String) -> Builder`
  - `baseUrl(String) -> Builder`
  - `apiKeyEnv(String) -> Builder`
  - `reasoningEffort(String) -> Builder`
  - `reasoningBudgetTokens(int) -> Builder`
  - `dryMultiplier(float) -> Builder`
  - `dryBase(float) -> Builder`
  - `dryAllowedLength(int) -> Builder`
  - `dryPenaltyLastN(int) -> Builder`
  - `drySequenceBreakers(List<String>) -> Builder`
  - `stopStrings(List<String>) -> Builder`
  - `build() -> LlamaCppJniConfig`

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- `lombok.EqualsAndHashCode`, `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` constructor throws `NullPointerException` for null `modelPath`.

#### Concurrency
- Immutable after construction; thread‑safe.
- `Builder` is not thread‑safe; intended for single‑thread use.

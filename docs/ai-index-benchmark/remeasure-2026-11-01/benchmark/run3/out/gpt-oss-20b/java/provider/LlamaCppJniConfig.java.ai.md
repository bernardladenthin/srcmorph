### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:57Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores llama.cpp runtime parameters.
- Provides read‑only access to configuration values.

#### Type
`final class LlamaCppJniConfig` – immutable, Lombok `@EqualsAndHashCode`, `@ToString`, annotated `@ConvertToRecord`.

#### Input
- `Builder` instance populated via fluent setters.
- `modelPath` mandatory string; all other fields default to `AiGenerationConfig.DEFAULT_*`.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Private constructor copies all builder fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- `Builder` initializes every field to its default; each setter assigns the value and returns `this`.
- `build()` returns a new `LlamaCppJniConfig` constructed from the builder.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.
- Getters (38 total):
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
- Builder methods (41 total):
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
- `AiGenerationConfig` for default constants.
- `ConvertToRecord`, `ToString`, `EqualsAndHashCode` (Lombok).
- `org.jspecify.annotations.Nullable`.
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` constructor enforces non‑null `modelPath`.

#### Concurrency
- Immutable after construction; thread‑safe.
- `Builder` is not thread‑safe; intended single‑thread usage.

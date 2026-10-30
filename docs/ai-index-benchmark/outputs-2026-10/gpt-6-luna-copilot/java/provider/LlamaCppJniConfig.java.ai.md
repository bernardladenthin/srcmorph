### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:35:22Z
- G: 
- A: 0.0.0
- X: file
---
> Configures local llama.cpp and OpenAI-compatible text generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` holds generation-provider settings with defaults sourced from `AiGenerationConfig`.
- Nested `Builder` provides named, fluent configuration before creating the value object.

#### Type
- `public final` class; nested `public static final` `Builder`.
- `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`; no superclass or implemented interfaces declared.
- 41 final configuration fields; `chatTemplateEnableThinking` is nullable.

#### Input
- `builder(String modelPath)` requires a GGUF model path; builder setters accept generation, backend, provider, reasoning, DRY, and stop-string settings.
- `AiGenerationConfig.DEFAULT_*` values initialize builder settings; list settings default to empty lists.

#### Output
- `build()` returns a `LlamaCppJniConfig`; accessors expose configured values.
- DRY sequence-breaker and stop-string accessors return unmodifiable list views.
- Lombok generates `equals`, `hashCode`, and `toString` over the fields.

#### Core logic
- Builder initializes optional settings from `AiGenerationConfig` defaults and retains the required model path.
- Fluent setters assign values; selected string setters restore their defaults for `null`, and list setters map `null` to empty lists.
- `build()` passes builder values to the private constructor, which copies them directly without further normalization.
- `chatTemplateEnableThinking` preserves `null` to represent an unset option.

#### Public API
- `builder(String) -> Builder` — start configuration for a model.
- `modelPath() -> String`; `contextSize() -> int`; `maxOutputTokens() -> int`; `temperature() -> float`; `threads() -> int`; `topP() -> float`; `topK() -> int`; `minP() -> float`; `topNSigma() -> float`; `repeatPenalty() -> float` — read model and sampling settings.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — read optional thinking mode.
- `cachePrompt() -> boolean`; `swaFull() -> boolean`; `cacheReuse() -> int` — read prompt and KV-cache settings.
- `gpuLayers() -> int`; `seed() -> int`; `cpuMoeLayers() -> int`; `cpuFfnLayers() -> int`; `kvUnifiedPerSlot() -> int`; `lazyMode() -> String` — read execution settings.
- `repeatLastN() -> int`; `cacheTypeK() -> String`; `cacheTypeV() -> String`; `flashAttn() -> boolean` — read sampling and attention settings.
- `batchSize() -> int`; `ubatchSize() -> int`; `threadsBatch() -> int`; `mainGpu() -> int`; `devices() -> String` — read batch and device settings.
- `provider() -> String`; `remoteModel() -> String`; `baseUrl() -> String`; `apiKeyEnv() -> String` — read provider connection settings.
- `reasoningEffort() -> String`; `reasoningBudgetTokens() -> int` — read reasoning controls.
- `dryMultiplier() -> float`; `dryBase() -> float`; `dryAllowedLength() -> int`; `dryPenaltyLastN() -> int`; `drySequenceBreakers() -> List<String>` — read DRY sampling settings.
- `stopStrings() -> List<String>` — read configured stop strings.
- `Builder.contextSize(int) -> Builder`; `maxOutputTokens(int) -> Builder`; `temperature(float) -> Builder`; `threads(int) -> Builder`; `topP(float) -> Builder`; `topK(int) -> Builder`; `minP(float) -> Builder`; `topNSigma(float) -> Builder`; `repeatPenalty(float) -> Builder` — set generation and sampling values.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set optional thinking mode.
- `Builder.cachePrompt(boolean) -> Builder`; `swaFull(boolean) -> Builder`; `cacheReuse(int) -> Builder` — set cache behavior.
- `Builder.gpuLayers(int) -> Builder`; `seed(int) -> Builder`; `cpuMoeLayers(int) -> Builder`; `cpuFfnLayers(int) -> Builder`; `kvUnifiedPerSlot(int) -> Builder`; `lazyMode(String) -> Builder` — set execution settings.
- `Builder.repeatLastN(int) -> Builder`; `cacheTypeK(String) -> Builder`; `cacheTypeV(String) -> Builder`; `flashAttn(boolean) -> Builder` — set sampling and attention settings.
- `Builder.batchSize(int) -> Builder`; `ubatchSize(int) -> Builder`; `threadsBatch(int) -> Builder`; `mainGpu(int) -> Builder`; `devices(String) -> Builder` — set batch and device settings.
- `Builder.provider(String) -> Builder`; `remoteModel(String) -> Builder`; `baseUrl(String) -> Builder`; `apiKeyEnv(String) -> Builder` — set provider connection values.
- `Builder.reasoningEffort(String) -> Builder`; `reasoningBudgetTokens(int) -> Builder` — set reasoning controls.
- `Builder.dryMultiplier(float) -> Builder`; `dryBase(float) -> Builder`; `dryAllowedLength(int) -> Builder`; `dryPenaltyLastN(int) -> Builder`; `drySequenceBreakers(List<String>) -> Builder` — set DRY sampling values.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create configuration.
- Lombok-generated: `equals(Object) -> boolean`, `hashCode() -> int`, `toString() -> String`.

#### Dependencies
- `AiGenerationConfig`; `ConvertToRecord`; Lombok `EqualsAndHashCode`, `ToString`; JSpecify `Nullable`.
- Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Nullable thinking mode is supported; selected string setters restore defaults on `null`, and list setters substitute empty lists.

#### Concurrency
- Config fields are final; builder is documented as not thread-safe.
- List accessors prevent mutation through returned views, but the lists are not defensively copied.

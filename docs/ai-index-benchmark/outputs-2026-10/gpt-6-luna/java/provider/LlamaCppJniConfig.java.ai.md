### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:38Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI model generation, sampling, caching, hardware, remote-provider, reasoning, and stop-sequence options with centralized defaults.

#### Purpose
- `LlamaCppJniConfig` holds immutable per-model generation settings for llama.cpp JNI and related providers.
- `Builder` applies `AiGenerationConfig` defaults and creates configurations through fluent setters.

#### Type
- `LlamaCppJniConfig`: `public final` class; `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`.
- `Builder`: `public static final` nested class.
- Configuration has 41 final fields, including nullable `chatTemplateEnableThinking` and two `List<String>` settings.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept sampling, token limits, CPU/GPU, KV cache, batching, provider, reasoning, DRY, and stop-sequence options.
- Builder defaults come from `AiGenerationConfig.DEFAULT_*`; sequence-breaker and stop-string defaults are empty lists.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors expose all 41 configuration values; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over all fields.

#### Core logic
- `LlamaCppJniConfig.builder(modelPath)` creates a builder after null-checking the required model path.
- Builder setters replace individual options and return the same builder; selected nullable string/list inputs restore their defaults when null.
- Private config constructor copies every builder value without further normalization.
- `chatTemplateEnableThinking` preserves null to represent an unset option; empty strings and sentinel values represent provider/binding defaults where documented.
- DRY sequence breakers and stop strings are stored from the builder and exposed through unmodifiable list wrappers.

#### Public API
- `builder(String) -> Builder` — start configuration with defaults.
- `modelPath() -> String`, `contextSize() -> int`, `maxOutputTokens() -> int`, `temperature() -> float`, `threads() -> int`, `topP() -> float`, `topK() -> int`, `minP() -> float`, `topNSigma() -> float`, `repeatPenalty() -> float`, `chatTemplateEnableThinking() -> @Nullable Boolean`, `cachePrompt() -> boolean`, `swaFull() -> boolean`, `cacheReuse() -> int`, `gpuLayers() -> int`, `seed() -> int`, `cpuMoeLayers() -> int`, `cpuFfnLayers() -> int`, `kvUnifiedPerSlot() -> int`, `lazyMode() -> String`, `repeatLastN() -> int`, `cacheTypeK() -> String`, `cacheTypeV() -> String`, `flashAttn() -> boolean`, `batchSize() -> int`, `ubatchSize() -> int`, `threadsBatch() -> int`, `mainGpu() -> int`, `devices() -> String`, `provider() -> String`, `remoteModel() -> String`, `baseUrl() -> String`, `apiKeyEnv() -> String`, `reasoningEffort() -> String`, `reasoningBudgetTokens() -> int`, `dryMultiplier() -> float`, `dryBase() -> float`, `dryAllowedLength() -> int`, `dryPenaltyLastN() -> int`, `drySequenceBreakers() -> List<String>`, `stopStrings() -> List<String>` — read configured values.
- `Builder` has 40 fluent option setters, one per configurable field other than `modelPath`; each returns `Builder` and accepts the corresponding field’s type.
- `Builder.build() -> LlamaCppJniConfig` — create immutable configuration.
- Total public methods: 83 (42 on `LlamaCppJniConfig`, 41 on `Builder`).

#### Dependencies
- `AiGenerationConfig` — centralized defaults.
- `ConvertToRecord` — record-migration marker.
- Lombok `EqualsAndHashCode`, `ToString`.
- JSpecify `Nullable`.
- `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `"modelPath"`.
- Null `lazyMode`, cache-type, device, provider, remote-provider, URL, API-key environment, reasoning-effort, and list arguments restore their configured defaults.
- No numeric range validation is performed.

#### Concurrency
- `LlamaCppJniConfig` fields are final; list accessors provide unmodifiable views.
- `Builder` is explicitly not thread-safe; intended for single-threaded construction.

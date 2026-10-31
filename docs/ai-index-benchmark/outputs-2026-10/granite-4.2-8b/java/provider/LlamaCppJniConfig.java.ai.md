### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:35:10Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
Immutable configuration class for the llama.cpp JNI provider built via a fluent builder, containing model path and generation parameters.

#### Purpose
- Provides a single immutable configuration object for llama.cpp JNI provider settings.
- Uses a builder pattern to set defaults from `AiGenerationConfig` and allow optional overrides.
- Exposes only getters (no setters) for all fields; builder handles mutation.

#### Type
- `public final class LlamaCppJniConfig` with inner static `Builder` class.
- Decorated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` from Lombok.
- No abstract, no sealed; `final` class.

#### Input
- `modelPath` (required string) passed to `builder(String)`.
- Builder setters receive: `int` for sizes/counts, `float` for probabilities/multipliers, `boolean`, `String`, `List<String>`, `@Nullable Boolean`.
- Defaults sourced from `AiGenerationConfig.DEFAULT_*` constants.
- `List<String>` defaults initialized to empty list via `Collections.emptyList()`.

#### Output
- `LlamaCppJniConfig` instance returned by `builder().build()`, immutable with all fields set.
- Builder methods return `this` for chaining.
- Getters return stored field values; `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList(...)`.

#### Core logic
- Builder stores all 37 configuration fields with defaults from `AiGenerationConfig`.
- Each setter assigns the parameter directly (no validation beyond null checks for `modelPath` and `null` handling for `String` fields via ternary fallback to default).
- `build()` constructs `LlamaCppJniConfig` via private constructor passing the builder.
- Private constructor copies all builder fields; no additional logic.
- `Collections.unmodifiableList` used for read-only list views in getters.

#### Public API
- `builder(modelPath) -> Builder`: starts builder with required `modelPath`.
- `modelPath() -> String`: returns model file path.
- `contextSize() -> int`: returns context window size.
- `maxOutputTokens() -> int`: returns max output tokens.
- `temperature() -> float`: returns sampling temperature.
- `threads() -> int`: returns CPU thread count.
- `topP() -> float`: returns top-p threshold.
- `topK() -> int`: returns top-k limit.
- `minP() -> float`: returns min-p threshold.
- `topNSigma() -> float`: returns top-n-sigma threshold.
- `repeatPenalty() -> float`: returns repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean`: returns chat template thinking mode (`null` if unset).
- `cachePrompt() -> boolean`: returns prompt caching flag.
- `swaFull() -> boolean`: returns full SWA KV flag.
- `cacheReuse() -> int`: returns cache reuse chunk size.
- `gpuLayers() -> int`: returns GPU layers.
- `seed() -> int`: returns RNG seed.
- `cpuMoeLayers() -> int`: returns CPU MoE layers.
- `cpuFfnLayers() -> int`: returns CPU FFN layers.
- `kvUnifiedPerSlot() -> int`: returns unified KV cap.
- `lazyMode() -> String`: returns tensor-read laziness mode.
- `repeatLastN() -> int`: returns repeat-last-n window.
- `cacheTypeK() -> String`: returns KV cache type K.
- `cacheTypeV() -> String`: returns KV cache type V.
- `flashAttn() -> boolean`: returns Flash Attention flag.
- `batchSize() -> int`: returns logical batch size.
- `ubatchSize() -> int`: returns physical batch size.
- `threadsBatch() -> int`: returns batch thread count.
- `mainGpu() -> int`: returns primary GPU index.
- `devices() -> String`: returns device list.
- `provider() -> String`: returns AI provider name.
- `remoteModel() -> String`: returns remote model ID.
- `baseUrl() -> String`: returns base URL.
- `apiKeyEnv() -> String`: returns API key env var name.
- `reasoningEffort() -> String`: returns reasoning effort ("low"/"medium"/"high").
- `reasoningBudgetTokens() -> int`: returns token budget.
- `dryMultiplier() -> float`: returns DRY multiplier.
- `dryBase() -> float`: returns DRY base.
- `dryAllowedLength() -> int`: returns DRY allowed length.
- `dryPenaltyLastN() -> int`: returns DRY penalty window.
- `drySequenceBreakers() -> List<String>`: returns unmodifiable list of DRY sequence breakers.
- `stopStrings() -> List<String>`: returns unmodifiable list of stop strings.
- Builder methods: each named `field(value)` (e.g., `contextSize(int)`) returns `this`; `build()` returns `LlamaCppJniConfig`.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `lombok.EqualsAndHashCode`, `lombok.ToString`

#### Exceptions / Errors
- `NullPointerException` thrown by `builder` constructor if `modelPath` is `null` (checked via `Objects.requireNonNull`).
- No other checked exceptions; builder setters accept `null` for `Boolean` and `String` fields (handled via ternary fallback to default).

#### Concurrency
- Builder is not thread-safe (explicitly stated in Javadoc); intended for single-threaded use.
- `LlamaCppJniConfig` is immutable; safe for concurrent reads.

> Note: The file contains no builder method duplication beyond the pattern; all builder setters are distinct and counted implicitly via source. No fields or methods are inferred or guessed.

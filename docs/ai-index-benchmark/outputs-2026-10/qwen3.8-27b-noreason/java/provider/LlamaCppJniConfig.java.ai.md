### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:58:02Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI and OpenAI-compatible AI generation parameters, built via a fluent builder with centralized defaults.

#### Purpose
- Immutable value object carrying 41 configuration knobs for the `llama.cpp` JNI provider (sampling, KV-cache, GPU offload, DRY penalty, stop strings).
- Supports multi-provider routing (`llamacpp-jni`, `openai-compatible`, `mock`) via `provider`, `remoteModel`, `baseUrl`, and `apiKeyEnv` fields.

#### Type
- `public final class LlamaCppJniConfig`
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok)
- Contains nested `public static final class Builder`

#### Input
- **Builder**: `modelPath` (required, non-null), 40 optional parameters defaulting to `AiGenerationConfig.DEFAULT_*` constants.
- **String setters**: `null` input restores the corresponding `AiGenerationConfig.DEFAULT_*` value.
- **List setters**: `null` input restores `Collections.emptyList()`.

#### Output
- **Accessors**: 41 record-style public methods returning field values.
- **Collections**: `drySequenceBreakers()` and `stopStrings()` return `Collections.unmodifiableList` views.
- **Builder**: `build()` returns a new immutable `LlamaCppJniConfig` instance.

#### Core logic
- **Builder Pattern**: Private constructor prevents direct instantiation; `builder(String)` is the sole entry point.
- **Default Centralization**: All 40 optional fields initialize to `AiGenerationConfig.DEFAULT_*` in the `Builder`, ensuring a single source of truth for defaults.
- **Null Safety**: String setters use ternary operators to replace `null` with defaults; list setters replace `null` with empty lists.
- **Immutability**: All fields are `final`; list accessors wrap contents in unmodifiable views.
- **Value Semantics**: Lombok generates `equals`, `hashCode`, and `toString` over all 41 fields, using `Float.floatToIntBits` for exact float comparison.

#### Public API
- `builder(String modelPath) -> Builder` - Creates builder with required model path
- `modelPath() -> String` - Returns GGUF model file path
- `contextSize() -> int` - Returns context window size
- `maxOutputTokens() -> int` - Returns max output tokens
- `temperature() -> float` - Returns sampling temperature
- `threads() -> int` - Returns CPU thread count
- `topP() -> float` - Returns nucleus sampling threshold
- `topK() -> int` - Returns top-k sampling limit
- `minP() -> float` - Returns min-p sampling threshold
- `topNSigma() -> float` - Returns top-n-sigma threshold
- `repeatPenalty() -> float` - Returns repetition penalty
- `chatTemplateEnableThinking() -> @Nullable Boolean` - Returns thinking mode flag
- `cachePrompt() -> boolean` - Returns prompt caching flag
- `swaFull() -> boolean` - Returns SWA full cache flag
- `cacheReuse() -> int` - Returns KV reuse chunk size
- `gpuLayers() -> int` - Returns GPU offload layer count
- `seed() -> int` - Returns RNG seed
- `cpuMoeLayers() -> int` - Returns CPU MoE layer count
- `cpuFfnLayers() -> int` - Returns CPU FFN layer count
- `kvUnifiedPerSlot() -> int` - Returns unified KV context cap
- `lazyMode() -> String` - Returns tensor-read laziness mode
- `repeatLastN() -> int` - Returns repeat penalty window
- `cacheTypeK() -> String` - Returns K-cache data type
- `cacheTypeV() -> String` - Returns V-cache data type
- `flashAttn() -> boolean` - Returns Flash Attention flag
- `batchSize() -> int` - Returns logical batch size
- `ubatchSize() -> int` - Returns physical batch size
- `threadsBatch() -> int` - Returns batch processing threads
- `mainGpu() -> int` - Returns primary GPU index
- `devices() -> String` - Returns device selection list
- `provider() -> String` - Returns AI provider identifier
- `remoteModel() -> String` - Returns remote model ID
- `baseUrl() -> String` - Returns API base URL
- `apiKeyEnv() -> String` - Returns API key env var name
- `reasoningEffort() -> String` - Returns reasoning effort level
- `reasoningBudgetTokens() -> int` - Returns reasoning token budget
- `dryMultiplier() -> float` - Returns DRY sampling multiplier
- `dryBase() -> float` - Returns DRY base value
- `dryAllowedLength() -> int` - Returns DRY allowed length
- `dryPenaltyLastN() -> int` - Returns DRY penalty window
- `drySequenceBreakers() -> List<String>` - Returns unmodifiable DRY breakers
- `stopStrings() -> List<String>` - Returns unmodifiable stop strings
- **Builder Methods**: `contextSize..stopStrings` (40 total) - Fluent setters for all optional fields
- `build() -> LlamaCppJniConfig` - Constructs immutable configuration instance

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` - Source of all default values
- `net.ladenthin.srcmorph.support.ConvertToRecord` - Migration marker annotation
- `org.jspecify.annotations.Nullable` - Nullability annotation
- `lombok.EqualsAndHashCode`, `lombok.ToString` - Code generation
- `java.util.Collections`, `java.util.List`, `java.util.Objects` - Standard library

#### Exceptions / Errors
- `NullPointerException` thrown by `Builder` constructor if `modelPath` is `null` (via `Objects.requireNonNull`).
- No other explicit exception handling; null-safety managed via ternary defaults in setters.

#### Concurrency
- **Immutable**: `LlamaCppJniConfig` instances are thread-safe once constructed.
- **Builder**: Not thread-safe; intended for single-threaded use during construction.

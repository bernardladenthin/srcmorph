### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:32Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration value object for the llama.cpp JNI AI provider, encapsulating model path, sampling parameters, GPU offload settings, and DRY/stop-string controls via a fluent builder.

#### Purpose
- Immutable record-shaped value type holding all 41 configuration knobs for the `llama.cpp` JNI inference provider.
- Centralizes default values from `AiGenerationConfig` so callers only override specific parameters.

#### Type
- `public final class LlamaCppJniConfig`
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok)
- Contains nested `public static final class Builder`

#### Input
- **Builder**: `modelPath` (required, non-null), plus 40 optional parameters initialized to `AiGenerationConfig.DEFAULT_*` constants.
- **String setters**: `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` (null-safe, restore default if null).
- **List setters**: `drySequenceBreakers`, `stopStrings` (null-safe, default to empty list).

#### Output
- **Accessors**: 41 public record-style methods returning field values.
- **List accessors**: `drySequenceBreakers()`, `stopStrings()` return `Collections.unmodifiableList` views.
- **Builder**: `build()` returns immutable `LlamaCppJniConfig` instance.

#### Core logic
- **Builder pattern**: Private constructor prevents direct instantiation; `builder(String modelPath)` is the sole entry point.
- **Default application**: All 40 optional fields initialize to `AiGenerationConfig.DEFAULT_*` in the Builder, ensuring consistent fallbacks.
- **Null safety**: String and List setters in Builder treat `null` as "restore default" (empty string or empty list).
- **Immutability**: All fields `final`; list accessors return unmodifiable views; Lombok generates value-based `equals`/`hashCode`/`toString`.
- **Nullable tri-state**: `chatTemplateEnableThinking` is `@Nullable Boolean` to distinguish "unset" (null) from explicit true/false.

#### Public API
- `builder(String modelPath) -> Builder` - Creates builder with required model path
- `modelPath() -> String` - Returns GGUF model file path
- `contextSize() -> int` - Returns context window size in tokens
- `maxOutputTokens() -> int` - Returns maximum output tokens per call
- `temperature() -> float` - Returns sampling temperature
- `threads() -> int` - Returns number of CPU threads
- `topP() -> float` - Returns nucleus-sampling probability threshold
- `topK() -> int` - Returns top-k sampling limit
- `minP() -> float` - Returns min-p sampling threshold
- `topNSigma() -> float` - Returns top-n-sigma sampling threshold
- `repeatPenalty() -> float` - Returns repetition penalty
- `chatTemplateEnableThinking() -> @Nullable Boolean` - Returns chat-template thinking mode
- `cachePrompt() -> boolean` - Returns prompt caching enabled flag
- `swaFull() -> boolean` - Returns full SWA KV cache flag
- `cacheReuse() -> int` - Returns KV prefix-reuse chunk size
- `gpuLayers() -> int` - Returns GPU offload layer count
- `seed() -> int` - Returns RNG seed for generation
- `cpuMoeLayers() -> int` - Returns CPU MoE layer count
- `cpuFfnLayers() -> int` - Returns CPU FFN layer count
- `kvUnifiedPerSlot() -> int` - Returns per-slot unified KV cap
- `lazyMode() -> String` - Returns tensor-read laziness mode
- `repeatLastN() -> int` - Returns repeat-penalty window size
- `cacheTypeK() -> String` - Returns KV-cache data type for K
- `cacheTypeV() -> String` - Returns KV-cache data type for V
- `flashAttn() -> boolean` - Returns Flash Attention enabled flag
- `batchSize() -> int` - Returns logical batch size
- `ubatchSize() -> int` - Returns physical batch size
- `threadsBatch() -> int` - Returns batch-processing thread count
- `mainGpu() -> int` - Returns primary GPU index
- `devices() -> String` - Returns comma-separated device list
- `provider() -> String` - Returns AI provider identifier
- `remoteModel() -> String` - Returns OpenAI-compatible model ID
- `baseUrl() -> String` - Returns OpenAI-compatible API base URL
- `apiKeyEnv() -> String` - Returns API key environment variable name
- `reasoningEffort() -> String` - Returns gpt-oss reasoning-effort level
- `reasoningBudgetTokens() -> int` - Returns reasoning token budget
- `dryMultiplier() -> float` - Returns DRY sampling multiplier
- `dryBase() -> float` - Returns DRY base value
- `dryAllowedLength() -> int` - Returns DRY allowed length
- `dryPenaltyLastN() -> int` - Returns DRY penalty look-back window
- `drySequenceBreakers() -> List<String>` - Returns unmodifiable DRY breakers
- `stopStrings() -> List<String>` - Returns unmodifiable stop strings
- **Builder methods** (41 total): `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings` (all return `Builder`), plus `build() -> LlamaCppJniConfig`

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` - Source of all `DEFAULT_*` constants
- `net.ladenthin.srcmorph.support.ConvertToRecord` - Marker annotation for future record migration
- `org.jspecify.annotations.Nullable` - Nullability annotation
- `lombok.EqualsAndHashCode`, `lombok.ToString` - Code generation
- `java.util.Collections`, `java.util.List`, `java.util.Objects` - Standard library

#### Exceptions / Errors
- `NullPointerException` thrown by `Builder` constructor if `modelPath` is null (via `Objects.requireNonNull`).
- No other exceptions; all setters are null-safe for String/List types.

#### Concurrency
- `LlamaCppJniConfig` is immutable and thread-safe.
- `Builder` is not thread-safe; intended for single-threaded use before `build()`.

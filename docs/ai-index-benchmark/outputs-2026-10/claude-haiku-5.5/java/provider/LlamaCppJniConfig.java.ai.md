### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:31:08Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable llama.cpp JNI model configuration with a fluent builder that applies shared default sampling and runtime settings.

#### Purpose
- Holds all runtime settings for the llama.cpp JNI AI generation provider as an immutable value.
- Provides `Builder` that fills unset values from `AiGenerationConfig.DEFAULT_*`.

#### Type
- `public final class LlamaCppJniConfig`, no superclass, no interfaces.
- Annotations: `@ConvertToRecord` (`net.ladenthin.srcmorph.support`), Lombok `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder`.
- Field type `@Nullable Boolean chatTemplateEnableThinking` (`org.jspecify.annotations.Nullable`).
- Lists: `List<String> drySequenceBreakers`, `List<String> stopStrings`.

#### Input
- `Builder(String modelPath)`: required, null-checked via `Objects.requireNonNull`.
- Builder setters: 40 setters, one per field except `modelPath`.
- Nullable inputs: `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` fall back to defaults on `null`; `drySequenceBreakers`, `stopStrings` fall back to empty list.
- Reads `AiGenerationConfig.DEFAULT_*` constants at field initialization.

#### Output
- `LlamaCppJniConfig` instance via `Builder.build()`.
- Accessors return stored values; list accessors return `Collections.unmodifiableList` views.

#### Core logic
- `builder(modelPath)` creates a `Builder` pre-filled with defaults.
- Each builder setter assigns its field and returns `this`.
- `build()` calls private constructor `LlamaCppJniConfig(Builder)`, which copies all 41 fields directly without normalization.
- Null-to-default normalization lives only in builder setters.
- Groups of settings: sampling (`temperature`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`), DRY (`dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`), GPU/CPU offload (`gpuLayers`, `mainGpu`, `cpuMoeLayers`, `cpuFfnLayers`, `devices`), KV cache (`cacheTypeK`, `cacheTypeV`, `cachePrompt`, `cacheReuse`, `swaFull`, `kvUnifiedPerSlot`), batching (`batchSize`, `ubatchSize`, `threadsBatch`), remote provider (`provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`).
- Javadoc states 37 fields; source declares 41 fields.

#### Public API
- `builder(String modelPath) -> Builder` : starts builder with defaults.
- `modelPath() -> String` : GGUF model file path.
- Accessors `name() -> type`: 41 total, one per field (`contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`).
- `chatTemplateEnableThinking() -> @Nullable Boolean` : null means unset.
- `drySequenceBreakers() -> List<String>` : unmodifiable view.
- `stopStrings() -> List<String>` : unmodifiable view.
- `Builder` setters `name(value) -> Builder`: 40 total, one per field except `modelPath`.
- `Builder.build() -> LlamaCppJniConfig` : builds immutable config.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `lombok.EqualsAndHashCode`, `lombok.ToString`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`

#### Exceptions / Errors
- `NullPointerException` from `Builder` constructor when `modelPath` is null.
- No validation of numeric ranges; values pass through unchanged.
- Null list inputs become empty lists; null string inputs become defaults.

#### Concurrency
- `LlamaCppJniConfig` is immutable after construction; lists are wrapped unmodifiable on read.
- `Builder` is not thread-safe; documented as single-thread use.

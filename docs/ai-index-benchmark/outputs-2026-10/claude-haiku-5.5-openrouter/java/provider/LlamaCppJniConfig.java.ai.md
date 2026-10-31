### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:35:11Z
- G: 
- A: 0.0.0
- X: file
---
> Holds immutable llama.cpp JNI and OpenAI-compatible model settings (sampling, GPU offload, KV cache, DRY penalty, stop strings) for AI text generation, built via a fluent builder with defaults from AiGenerationConfig.

#### Purpose
- Immutable value object carrying all generation and model-loading knobs for the llama.cpp JNI provider.
- Enforces named construction through `Builder`; each unset field takes its `AiGenerationConfig.DEFAULT_*` value.

#### Type
- `final class`, `LlamaCppJniConfig`, with a nested `public static final class Builder`.
- Annotations: `@ConvertToRecord` (srcmorph marker for a future Java 17+ record migration), `@ToString`, `@EqualsAndHashCode` (Lombok).
- Field nullability: `@Nullable Boolean chatTemplateEnableThinking` (jspecify).
- Private constructor takes `Builder`.
- Fields: 41 `private final` (1 `modelPath`, `String`; 2 `contextSize`…; `drySequenceBreakers` and `stopStrings` are `List<String>`). Javadoc says "thirty-seven" fields; the source actually declares 41.

#### Input
- `builder(String modelPath)`: required model path (GGUF file).
- Builder setters: 40 setters, one per field except `modelPath`, each returning `Builder`.
- Defaults read from `AiGenerationConfig.DEFAULT_*` constants.

#### Output
- `LlamaCppJniConfig` instance from `Builder.build()`.
- Read-only accessors return field values; list accessors return unmodifiable views.

#### Core logic
- `builder(modelPath)` calls `Objects.requireNonNull` through the Builder constructor, so a null path fails immediately.
- Each Builder field starts at its `AiGenerationConfig.DEFAULT_*` value; setters assign and return `this`.
- String setters (`lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`) substitute the default when passed `null`.
- List setters (`drySequenceBreakers`, `stopStrings`) substitute `Collections.emptyList()` when passed `null`.
- `chatTemplateEnableThinking` accepts `null` as "unset": the provider then omits the `enable_thinking` kwarg.
- Constructor copies every builder field; performs no normalisation.
- List getters wrap backing lists with `Collections.unmodifiableList`.
- Sentinel conventions in Javadoc: `-1` means leave binding/build default; `0.0` means disabled (minP, dryMultiplier); `-1.0` means disabled (topNSigma); empty string means leave default.

#### Public API
- `builder(String modelPath) -> Builder`: start builder with defaults.
- Accessors (41, names match fields): `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `minP()`, `topNSigma()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `cachePrompt()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `seed()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `repeatLastN()`, `cacheTypeK()`, `cacheTypeV()`, `flashAttn()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`, `provider()`, `remoteModel()`, `baseUrl()`, `apiKeyEnv()`, `reasoningEffort()`, `reasoningBudgetTokens()`, `dryMultiplier()`, `dryBase()`, `dryAllowedLength()`, `dryPenaltyLastN()`, `drySequenceBreakers() -> List<String>` (unmodifiable), `stopStrings() -> List<String>` (unmodifiable). Each returns the field value.
- Builder (public, chainable, 40 setters): same names as accessors, each `(value) -> Builder`, sets field and returns this.
- `Builder.build() -> LlamaCppJniConfig`: construct immutable config.
- Lombok-generated: `equals`, `hashCode`, `toString` over all fields.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (source of `DEFAULT_*` constants)
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `lombok.EqualsAndHashCode`, `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`

#### Exceptions / Errors
- `NullPointerException` from `Objects.requireNonNull` when `modelPath` is null.
- No other validation; invalid numeric values are stored as given.
- Null handling: string and list setters map `null` to defaults; `chatTemplateEnableThinking` keeps `null` as a meaningful value.

#### Concurrency
- Final class with final fields and unmodifiable list views: the built config is immutable and safe to share.
- Builder is not thread-safe by design (documented).
- Float fields compared bitwise by Lombok `equals`.

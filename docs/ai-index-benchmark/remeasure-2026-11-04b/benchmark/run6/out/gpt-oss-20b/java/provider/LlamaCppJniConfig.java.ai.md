### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model execution.

#### Purpose
- Holds 37 configuration parameters for llama.cpp JNI provider.
- Provides fluent builder with defaults from `AiGenerationConfig`.
- Exposes read‑only accessors for each parameter.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - 37 `private final` fields: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`.  
- `public static final class Builder` (class, final)  
  - No supertype beyond `Object`.  
  - 37 mutable fields mirroring the config, initialized to `AiGenerationConfig.DEFAULT_*`.  
  - Constructor `Builder(String modelPath)` (private).  
  - Setter methods for each field, returning `Builder`.  
  - `build()` returns `LlamaCppJniConfig`.

#### Input
- `Builder` constructor receives `modelPath` (non‑null).  
- Each setter accepts a value for its corresponding field.  
- `build()` consumes the populated `Builder`.

#### Output
- `LlamaCppJniConfig` instance with all fields set from the builder.  
- Accessor methods return primitive or immutable values; list getters return unmodifiable views.

#### Core logic
- `LlamaCppJniConfig(Builder)` copies each builder field to the final config fields.  
- `Builder` setters assign the provided value to the mutable field and return `this`.  
- `Builder.build()` creates a new `LlamaCppJniConfig` by invoking the private constructor.  
- List getters wrap the internal lists with `Collections.unmodifiableList`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – starts builder with required model path.  
- `String modelPath() -> model file path`  
- `int contextSize() -> context window size`  
- `int maxOutputTokens() -> maximum output tokens`  
- `float temperature() -> sampling temperature`  
- `int threads() -> CPU thread count`  
- `float topP() -> nucleus-sampling threshold`  
- `int topK() -> top‑k limit`  
- `float minP() -> min‑p threshold`  
- `float topNSigma() -> top‑n‑sigma threshold`  
- `float repeatPenalty() -> repetition penalty`  
- `@Nullable Boolean chatTemplateEnableThinking() -> chat‑template thinking flag`  
- `boolean cachePrompt() -> prompt caching enabled`  
- `boolean swaFull() -> full‑size SWA cache kept`  
- `int cacheReuse() -> KV prefix‑reuse chunk size`  
- `int gpuLayers() -> GPU layers offloaded`  
- `int seed() -> RNG seed`  
- `int cpuMoeLayers() -> CPU MoE layers`  
- `int cpuFfnLayers() -> CPU FFN layers`  
- `int kvUnifiedPerSlot() -> per‑slot KV cap`  
- `String lazyMode() -> tensor‑read laziness`  
- `int repeatLastN() -> repeat‑penalty window`  
- `String cacheTypeK() -> KV cache type K`  
- `String cacheTypeV() -> KV cache type V`  
- `boolean flashAttn() -> Flash Attention enabled`  
- `int batchSize() -> logical batch size`  
- `int ubatchSize() -> physical batch size`  
- `int threadsBatch() -> batch/prompt thread count`  
- `int mainGpu() -> primary GPU index`  
- `String devices() -> device selection`  
- `String provider() -> AI provider`  
- `String remoteModel() -> remote model ID`  
- `String baseUrl() -> OpenAI‑compatible base URL`  
- `String apiKeyEnv() -> API key environment variable`  
- `String reasoningEffort() -> reasoning‑effort value`  
- `int reasoningBudgetTokens() -> reasoning budget tokens`  
- `float dryMultiplier() -> DRY multiplier`  
- `float dryBase() -> DRY base`  
- `int dryAllowedLength() -> DRY allowed length`  
- `int dryPenaltyLastN() -> DRY penalty look‑back`  
- `List<String> drySequenceBreakers() -> unmodifiable DRY sequence breakers`  
- `List<String> stopStrings() -> unmodifiable stop strings`  
- Builder setters: `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`, `build() -> LlamaCppJniConfig`.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- `lombok.EqualsAndHashCode`, `lombok.ToString`.  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` if `modelPath` is null.  
- No other explicit exceptions.

#### Concurrency
- `Builder` is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

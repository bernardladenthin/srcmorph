### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:56Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model loading, sampling, caching, GPU usage, and API integration.

#### Purpose
- Holds 37 configuration parameters for llama.cpp JNI provider.
- Provides fluent builder with defaults from `AiGenerationConfig`.
- Exposes read‑only accessors for all fields.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - 37 private final fields: `String modelPath`, `int contextSize`, `int maxOutputTokens`, `float temperature`, `int threads`, `float topP`, `int topK`, `float minP`, `float topNSigma`, `float repeatPenalty`, `@Nullable Boolean chatTemplateEnableThinking`, `boolean cachePrompt`, `boolean swaFull`, `int cacheReuse`, `int gpuLayers`, `int seed`, `int cpuMoeLayers`, `int cpuFfnLayers`, `int kvUnifiedPerSlot`, `String lazyMode`, `int repeatLastN`, `String cacheTypeK`, `String cacheTypeV`, `boolean flashAttn`, `int batchSize`, `int ubatchSize`, `int threadsBatch`, `int mainGpu`, `String devices`, `String provider`, `String remoteModel`, `String baseUrl`, `String apiKeyEnv`, `String reasoningEffort`, `int reasoningBudgetTokens`, `float dryMultiplier`, `float dryBase`, `int dryAllowedLength`, `int dryPenaltyLastN`, `List<String> drySequenceBreakers`, `List<String> stopStrings`.  
- `public static final class Builder` (class, final)  
  - Fields: same names as above, initialized to `AiGenerationConfig.DEFAULT_*` or `modelPath`.  
  - Constructor: `Builder(String modelPath)` (private).  
  - Setter methods: `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`.  
  - `build()` returns `LlamaCppJniConfig`.

#### Input
- `builder(String modelPath)` requires non‑null `modelPath`.  
- Builder setters accept primitive or reference values; `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` treat `null` as default.  
- `drySequenceBreakers` and `stopStrings` accept `null` as empty list.

#### Output
- `LlamaCppJniConfig` instance with all fields set.  
- Accessor methods return primitive or immutable copies; list getters return unmodifiable views.

#### Core logic
- Constructor copies each builder field to its own field.  
- `builder(String)` creates a new `Builder` with required `modelPath`.  
- Each setter assigns the argument to the builder field and returns `this`.  
- `build()` constructs a new `LlamaCppJniConfig` via the private constructor.

#### Public API
- `static Builder builder(String) -> Builder` – start building.  
- `String modelPath() -> model file path`  
- `int contextSize() -> context window size`  
- `int maxOutputTokens() -> maximum output tokens`  
- `float temperature() -> sampling temperature`  
- `int threads() -> CPU threads`  
- `float topP() -> nucleus‑sampling threshold`  
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
- `String remoteModel() -> remote model id`  
- `String baseUrl() -> OpenAI‑compatible base URL`  
- `String apiKeyEnv() -> API key env var name`  
- `String reasoningEffort() -> reasoning‑effort value`  
- `int reasoningBudgetTokens() -> reasoning budget`  
- `float dryMultiplier() -> DRY multiplier`  
- `float dryBase() -> DRY base`  
- `int dryAllowedLength() -> DRY allowed length`  
- `int dryPenaltyLastN() -> DRY penalty window`  
- `List<String> drySequenceBreakers() -> unmodifiable list`  
- `List<String> stopStrings() -> unmodifiable list`  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` for default constants.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.  
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`.

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.  
- `Builder` constructor uses `Objects.requireNonNull` for `modelPath`.  
- No other runtime exceptions.

#### Concurrency
- Immutable `LlamaCppJniConfig` is thread‑safe.  
- `Builder` is not thread‑safe; intended for single‑thread use.

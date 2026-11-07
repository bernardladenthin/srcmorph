### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T12:50:13Z
- T: 2026-11-04T12:51:09Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model execution.

#### Purpose
- Holds 37 configuration parameters for llama.cpp JNI provider.
- Provides fluent builder with defaults from `AiGenerationConfig`.
- Supplies read‑only accessors for each parameter.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - Fields: 37 private final fields (see below).  
- `public static final class Builder` (class, final)  
  - No supertype beyond `Object`.  
  - Fields: 37 mutable fields matching config, plus `modelPath` final.  
  - Methods: 37 setters, `build()`.

#### Input
- `Builder(String modelPath)` requires non‑null `modelPath`.  
- Setter methods accept primitive or object values, many with defaults from `AiGenerationConfig`.  
- `drySequenceBreakers` and `stopStrings` accept `List<String>` (may be null → empty list).

#### Output
- `LlamaCppJniConfig` immutable instance with all fields set from builder.  
- Accessor methods return primitive or immutable views (`Collections.unmodifiableList` for lists).

#### Core logic
- `private LlamaCppJniConfig(Builder)` copies each builder field to the corresponding final field.  
- `Builder.build()` creates a new `LlamaCppJniConfig(this)`.  
- Setter methods assign the provided value to the builder field and return `this`.  
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` replace null with default constants from `AiGenerationConfig`.  
- `drySequenceBreakers` and `stopStrings` replace null with `Collections.emptyList()`.

#### Public API
- `static Builder builder(String modelPath)` → `Builder` (requires non‑null).  
- `String modelPath()` → model file path.  
- `int contextSize()` → context window size.  
- `int maxOutputTokens()` → max output tokens.  
- `float temperature()` → sampling temperature.  
- `int threads()` → CPU threads.  
- `float topP()` → nucleus‑sampling threshold.  
- `int topK()` → top‑k limit.  
- `float minP()` → min‑p threshold.  
- `float topNSigma()` → top‑n‑sigma threshold.  
- `float repeatPenalty()` → repetition penalty.  
- `@Nullable Boolean chatTemplateEnableThinking()` → chat‑template thinking flag.  
- `boolean cachePrompt()` → prompt caching enabled.  
- `boolean swaFull()` → full‑size SWA cache kept.  
- `int cacheReuse()` → KV prefix‑reuse chunk size.  
- `int gpuLayers()` → GPU layers offloaded.  
- `int seed()` → RNG seed.  
- `int cpuMoeLayers()` → CPU MoE layers.  
- `int cpuFfnLayers()` → CPU FFN layers.  
- `int kvUnifiedPerSlot()` → per‑slot KV context cap.  
- `String lazyMode()` → tensor‑read laziness.  
- `int repeatLastN()` → repeat‑penalty window.  
- `String cacheTypeK()` → KV‑cache data type for K.  
- `String cacheTypeV()` → KV‑cache data type for V.  
- `boolean flashAttn()` → Flash Attention enabled.  
- `int batchSize()` → logical batch size.  
- `int ubatchSize()` → physical batch size.  
- `int threadsBatch()` → batch/prompt‑processing threads.  
- `int mainGpu()` → primary GPU index.  
- `String devices()` → device selection.  
- `String provider()` → AI provider.  
- `String remoteModel()` → remote model ID.  
- `String baseUrl()` → OpenAI‑compatible base URL.  
- `String apiKeyEnv()` → API key environment variable name.  
- `String reasoningEffort()` → reasoning‑effort value.  
- `int reasoningBudgetTokens()` → reasoning token budget.  
- `float dryMultiplier()` → DRY multiplier.  
- `float dryBase()` → DRY base.  
- `int dryAllowedLength()` → DRY allowed length.  
- `int dryPenaltyLastN()` → DRY penalty look‑back.  
- `List<String> drySequenceBreakers()` → unmodifiable list of DRY sequence breakers.  
- `List<String> stopStrings()` → unmodifiable list of stop strings.  

Builder setters (public, return `Builder`):
- `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`.  
- `build()` → `LlamaCppJniConfig`.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- `lombok.EqualsAndHashCode`, `lombok.ToString`.  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `Objects.requireNonNull` throws `NullPointerException` if `modelPath` is null.  
- No other explicit exceptions.

#### Concurrency
- Builder is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

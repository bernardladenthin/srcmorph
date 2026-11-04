### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:41Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model generation.

#### Purpose
- Holds 37 configuration knobs for llama.cpp JNI provider.
- Provides a fluent builder that applies `AiGenerationConfig.DEFAULT_*` values.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
- `public static final class Builder` (class, static, final)  
  - No supertype beyond `Object`.  
  - Annotated with no special annotations.

#### Input
- `Builder` constructor: `String modelPath` (non‑null).  
- Builder setters: 36 optional parameters (int, float, boolean, `@Nullable Boolean`, `String`, `List<String>`).  
- `build()` consumes the populated `Builder`.

#### Output
- `LlamaCppJniConfig` immutable instance with 37 fields.  
- Getter methods return primitive values, `String`, `List<String>` (unmodifiable), or `@Nullable Boolean`.  
- `stopStrings()` and `drySequenceBreakers()` return unmodifiable lists.

#### Core logic
- `LlamaCppJniConfig(Builder)` copies all builder fields into final fields.  
- Builder initializes each field to `AiGenerationConfig.DEFAULT_*` or empty list.  
- Each setter assigns the provided value and returns `this`.  
- `build()` creates a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String modelPath)` → builder with defaults.  
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
- `int kvUnifiedPerSlot()` → per‑slot KV cap.  
- `String lazyMode()` → tensor‑read laziness.  
- `int repeatLastN()` → repeat‑penalty window.  
- `String cacheTypeK()` → KV‑cache type K.  
- `String cacheTypeV()` → KV‑cache type V.  
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
- `int reasoningBudgetTokens()` → reasoning budget tokens.  
- `float dryMultiplier()` → DRY multiplier.  
- `float dryBase()` → DRY base.  
- `int dryAllowedLength()` → DRY allowed length.  
- `int dryPenaltyLastN()` → DRY penalty look‑back.  
- `List<String> drySequenceBreakers()` → unmodifiable DRY sequence breakers.  
- `List<String> stopStrings()` → unmodifiable stop strings.  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (default constants).  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.  
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`.

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` if `modelPath` is null.

#### Concurrency
- Builder is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:40Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all model‑generation knobs and defaults.

#### Purpose
- Holds 37 configuration parameters for llama.cpp JNI generation.
- Provides a fluent builder that applies `AiGenerationConfig.DEFAULT_*` values.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - 37 private final fields (String, int, float, boolean, `@Nullable Boolean`, `List<String>`).  
- `public static final class Builder` (class, final)  
  - No supertype beyond `Object`.  
  - 37 private fields (matching config fields, with defaults from `AiGenerationConfig`).  
  - Annotated with Lombok annotations via builder methods.

#### Input
- `Builder(String modelPath)` requires non‑null `modelPath`.  
- Setter methods accept primitive or object values for each config knob.  
- `build()` consumes the populated builder.

#### Output
- `LlamaCppJniConfig` immutable instance with all fields set.  
- Getter methods return primitive or object values; list getters return unmodifiable views.

#### Core logic
- Constructor copies all builder fields to immutable fields.  
- Builder setters assign values and return `this`.  
- `build()` creates a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String)` → builder with defaults.  
- `String modelPath()` → model file path.  
- `int contextSize()` → context window size.  
- `int maxOutputTokens()` → max output tokens.  
- `float temperature()` → sampling temperature.  
- `int threads()` → CPU thread count.  
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
- `int reasoningBudgetTokens()` → reasoning token budget.  
- `float dryMultiplier()` → DRY multiplier.  
- `float dryBase()` → DRY base.  
- `int dryAllowedLength()` → DRY allowed length.  
- `int dryPenaltyLastN()` → DRY penalty look‑back.  
- `List<String> drySequenceBreakers()` → unmodifiable DRY sequence breakers.  
- `List<String> stopStrings()` → unmodifiable stop strings.  
- Builder setters: `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(@Nullable Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`, `build()` → `LlamaCppJniConfig`.

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
- Immutable `LlamaCppJniConfig` is thread‑safe.  
- `Builder` is not thread‑safe; intended for single‑thread use.

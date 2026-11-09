### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:54:13Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model execution.

#### Purpose
- Holds 37 configuration knobs for llama.cpp JNI provider.
- Provides fluent builder with defaults from `AiGenerationConfig`.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - Fields: 37 `private final` fields (see below).  
- `public static final class Builder` (class, static, final)  
  - Fields: 37 mutable fields with defaults.  
  - No supertype beyond `Object`.

#### Input
- `Builder` constructor: `String modelPath` (non‑null).  
- Builder setters: various primitives, `@Nullable Boolean`, `String`, `List<String>`.  
- `build()` consumes the builder’s fields.

#### Output
- `LlamaCppJniConfig` immutable instance with all fields copied from builder.  
- Getter methods return primitive values, `String`, `@Nullable Boolean`, or unmodifiable `List<String>`.

#### Core logic
- `private LlamaCppJniConfig(Builder)` copies each builder field to the corresponding final field.  
- `Builder` constructor assigns `modelPath` after `Objects.requireNonNull`.  
- Each setter assigns the argument to its field and returns `this`.  
- `build()` returns `new LlamaCppJniConfig(this)`.

#### Public API
- `static Builder builder(String modelPath)` → `Builder` (creates builder with defaults).  
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
- `int kvUnifiedPerSlot()` → per‑slot unified KV cap.  
- `String lazyMode()` → tensor‑read laziness.  
- `int repeatLastN()` → repeat‑penalty window.  
- `String cacheTypeK()` → KV‑cache type for K.  
- `String cacheTypeV()` → KV‑cache type for V.  
- `boolean flashAttn()` → Flash Attention enabled.  
- `int batchSize()` → logical batch size.  
- `int ubatchSize()` → physical batch size.  
- `int threadsBatch()` → batch/prompt‑processing threads.  
- `int mainGpu()` → primary GPU index.  
- `String devices()` → device selection.  
- `String provider()` → AI provider name.  
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
- `Builder contextSize(int)` → set context size.  
- `Builder maxOutputTokens(int)` → set max output tokens.  
- `Builder temperature(float)` → set temperature.  
- `Builder threads(int)` → set CPU threads.  
- `Builder topP(float)` → set nucleus‑sampling threshold.  
- `Builder topK(int)` → set top‑k limit.  
- `Builder minP(float)` → set min‑p threshold.  
- `Builder topNSigma(float)` → set top‑n‑sigma threshold.  
- `Builder repeatPenalty(float)` → set repetition penalty.  
- `Builder chatTemplateEnableThinking(@Nullable Boolean)` → set chat‑template flag.  
- `Builder cachePrompt(boolean)` → set prompt caching.  
- `Builder swaFull(boolean)` → set full‑size SWA cache.  
- `Builder cacheReuse(int)` → set KV prefix‑reuse chunk size.  
- `Builder gpuLayers(int)` → set GPU layers.  
- `Builder seed(int)` → set RNG seed.  
- `Builder cpuMoeLayers(int)` → set CPU MoE layers.  
- `Builder cpuFfnLayers(int)` → set CPU FFN layers.  
- `Builder kvUnifiedPerSlot(int)` → set per‑slot unified KV cap.  
- `Builder lazyMode(String)` → set tensor‑read laziness.  
- `Builder repeatLastN(int)` → set repeat‑penalty window.  
- `Builder cacheTypeK(String)` → set KV‑cache type K.  
- `Builder cacheTypeV(String)` → set KV‑cache type V.  
- `Builder flashAttn(boolean)` → set Flash Attention.  
- `Builder batchSize(int)` → set logical batch size.  
- `Builder ubatchSize(int)` → set physical batch size.  
- `Builder threadsBatch(int)` → set batch/prompt‑processing threads.  
- `Builder mainGpu(int)` → set primary GPU index.  
- `Builder devices(String)` → set device selection.  
- `Builder provider(String)` → set AI provider.  
- `Builder remoteModel(String)` → set remote model ID.  
- `Builder baseUrl(String)` → set base URL.  
- `Builder apiKeyEnv(String)` → set API key env name.  
- `Builder reasoningEffort(String)` → set reasoning‑effort.  
- `Builder reasoningBudgetTokens(int)` → set reasoning budget.  
- `Builder dryMultiplier(float)` → set DRY multiplier.  
- `Builder dryBase(float)` → set DRY base.  
- `Builder dryAllowedLength(int)` → set DRY allowed length.  
- `Builder dryPenaltyLastN(int)` → set DRY penalty look‑back.  
- `Builder drySequenceBreakers(List<String>)` → set DRY sequence breakers.  
- `Builder stopStrings(List<String>)` → set stop strings.  
- `Builder build()` → `LlamaCppJniConfig`.

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
- `Builder` is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

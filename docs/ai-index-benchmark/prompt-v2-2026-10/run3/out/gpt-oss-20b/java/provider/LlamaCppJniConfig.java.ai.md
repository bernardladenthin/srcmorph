### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:06:54Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model‑and‑runtime options.

#### Purpose
- Stores all llama.cpp JNI configuration parameters.
- Provides read‑only accessors and a fluent builder.

#### Type
`final class LlamaCppJniConfig`  
- annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`  
- 37 private final fields (String, int, float, boolean, @Nullable Boolean, List<String>)

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or overridden by setters.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields to final fields.
- `builder(String)` validates non‑null `modelPath` and returns a new `Builder`.
- Each accessor returns the corresponding field.
- `drySequenceBreakers()` and `stopStrings()` wrap lists with `Collections.unmodifiableList`.
- `Builder` setters assign values and return `this` for chaining.
- `build()` creates a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String)` → create builder  
- `String modelPath()` → model file path  
- `int contextSize()` → context window size  
- `int maxOutputTokens()` → max output tokens  
- `float temperature()` → sampling temperature  
- `int threads()` → CPU threads  
- `float topP()` → nucleus‑sampling threshold  
- `int topK()` → top‑k limit  
- `float minP()` → min‑p threshold  
- `float topNSigma()` → top‑n‑sigma threshold  
- `float repeatPenalty()` → repetition penalty  
- `@Nullable Boolean chatTemplateEnableThinking()` → chat‑template flag  
- `boolean cachePrompt()` → prompt caching  
- `boolean swaFull()` → full‑size SWA cache  
- `int cacheReuse()` → KV prefix‑reuse chunk size  
- `int gpuLayers()` → GPU offload layers  
- `int seed()` → RNG seed  
- `int cpuMoeLayers()` → CPU MoE layers  
- `int cpuFfnLayers()` → CPU FFN layers  
- `int kvUnifiedPerSlot()` → KV unified cap  
- `String lazyMode()` → tensor‑read laziness  
- `int repeatLastN()` → repeat‑penalty window  
- `String cacheTypeK()` → KV cache type K  
- `String cacheTypeV()` → KV cache type V  
- `boolean flashAttn()` → Flash Attention  
- `int batchSize()` → logical batch size  
- `int ubatchSize()` → physical batch size  
- `int threadsBatch()` → batch thread count  
- `int mainGpu()` → primary GPU index  
- `String devices()` → device list  
- `String provider()` → AI provider  
- `String remoteModel()` → remote model ID  
- `String baseUrl()` → OpenAI‑compatible base URL  
- `String apiKeyEnv()` → API key env var name  
- `String reasoningEffort()` → reasoning‑effort value  
- `int reasoningBudgetTokens()` → reasoning budget  
- `float dryMultiplier()` → DRY multiplier  
- `float dryBase()` → DRY base  
- `int dryAllowedLength()` → DRY allowed length  
- `int dryPenaltyLastN()` → DRY penalty window  
- `List<String> drySequenceBreakers()` → DRY sequence breakers (unmodifiable)  
- `List<String> stopStrings()` → stop strings (unmodifiable)  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.*`
- Lombok (`@EqualsAndHashCode`, `@ToString`)

#### Exceptions / Errors
- `NullPointerException` from `builder(String)` if `modelPath` is null.

#### Concurrency
- Immutable after construction; thread‑safe for reads.  
- `Builder` is not thread‑safe; intended for single‑thread use.

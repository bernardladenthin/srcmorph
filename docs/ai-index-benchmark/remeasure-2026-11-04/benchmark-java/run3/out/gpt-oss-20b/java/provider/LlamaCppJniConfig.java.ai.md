### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:18Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model‑generation knobs and defaults.

#### Purpose
- Stores llama.cpp runtime options.
- Supplies read‑only access to configuration values.

#### Type
`final class LlamaCppJniConfig` – immutable, Lombok‑generated `equals`, `hashCode`, `toString`. Implements `ConvertToRecord`. No superclass or interfaces.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller overrides.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all 37 builder fields to final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field.
- `drySequenceBreakers()` / `stopStrings()` wrap lists with `Collections.unmodifiableList`.

#### Public API
- `static Builder builder(String)` → builder with defaults  
- `String modelPath()` → model file path  
- `int contextSize()` → context window size  
- `int maxOutputTokens()` → max output tokens  
- `float temperature()` → sampling temperature  
- `int threads()` → CPU thread count  
- `float topP()` → nucleus‑sampling threshold  
- `int topK()` → top‑k limit  
- `float minP()` → min‑p threshold  
- `float topNSigma()` → top‑n‑sigma threshold  
- `float repeatPenalty()` → repetition penalty  
- `@Nullable Boolean chatTemplateEnableThinking()` → chat‑template flag  
- `boolean cachePrompt()` → prompt caching enabled  
- `boolean swaFull()` → full SWA cache kept  
- `int cacheReuse()` → KV prefix‑reuse chunk size  
- `int gpuLayers()` → GPU layers offloaded  
- `int seed()` → RNG seed  
- `int cpuMoeLayers()` → CPU MoE layers  
- `int cpuFfnLayers()` → CPU FFN layers  
- `int kvUnifiedPerSlot()` → KV unified cap  
- `String lazyMode()` → tensor‑read laziness  
- `int repeatLastN()` → repeat‑penalty window  
- `String cacheTypeK()` → KV cache type K  
- `String cacheTypeV()` → KV cache type V  
- `boolean flashAttn()` → Flash Attention enabled  
- `int batchSize()` → logical batch size  
- `int ubatchSize()` → physical batch size  
- `int threadsBatch()` → batch thread count  
- `int mainGpu()` → primary GPU index  
- `String devices()` → device list  
- `String provider()` → AI provider name  
- `String remoteModel()` → remote model ID  
- `String baseUrl()` → OpenAI‑compatible base URL  
- `String apiKeyEnv()` → API key environment variable  
- `String reasoningEffort()` → reasoning‑effort level  
- `int reasoningBudgetTokens()` → reasoning token budget  
- `float dryMultiplier()` → DRY multiplier  
- `float dryBase()` → DRY base  
- `int dryAllowedLength()` → DRY allowed length  
- `int dryPenaltyLastN()` → DRY penalty look‑back  
- `List<String> drySequenceBreakers()` → unmodifiable breakers list  
- `List<String> stopStrings()` → unmodifiable stop strings  

`Builder` public methods  
- `Builder contextSize(int)` → set context size  
- `Builder maxOutputTokens(int)` → set max output tokens  
- `Builder temperature(float)` → set temperature  
- `Builder threads(int)` → set CPU threads  
- `Builder topP(float)` → set nucleus threshold  
- `Builder topK(int)` → set top‑k limit  
- `Builder minP(float)` → set min‑p threshold  
- `Builder topNSigma(float)` → set top‑n‑sigma threshold  
- `Builder repeatPenalty(float)` → set repetition penalty  
- `Builder chatTemplateEnableThinking(@Nullable Boolean)` → set chat‑template flag  
- `Builder cachePrompt(boolean)` → set prompt caching  
- `Builder swaFull(boolean)` → set full SWA cache  
- `Builder cacheReuse(int)` → set KV prefix‑reuse size  
- `Builder gpuLayers(int)` → set GPU layers  
- `Builder seed(int)` → set RNG seed  
- `Builder cpuMoeLayers(int)` → set CPU MoE layers  
- `Builder cpuFfnLayers(int)` → set CPU FFN layers  
- `Builder kvUnifiedPerSlot(int)` → set KV unified cap  
- `Builder lazyMode(String)` → set tensor‑read laziness  
- `Builder repeatLastN(int)` → set repeat‑penalty window  
- `Builder cacheTypeK(String)` → set KV cache type K  
- `Builder cacheTypeV(String)` → set KV cache type V  
- `Builder flashAttn(boolean)` → set Flash Attention  
- `Builder batchSize(int)` → set logical batch size  
- `Builder ubatchSize(int)` → set physical batch size  
- `Builder threadsBatch(int)` → set batch thread count  
- `Builder mainGpu(int)` → set primary GPU index  
- `Builder devices(String)` → set device list  
- `Builder provider(String)` → set AI provider  
- `Builder remoteModel(String)` → set remote model ID  
- `Builder baseUrl(String)` → set base URL  
- `Builder apiKeyEnv(String)` → set API key env name  
- `Builder reasoningEffort(String)` → set reasoning effort  
- `Builder reasoningBudgetTokens(int)` → set reasoning budget  
- `Builder dryMultiplier(float)` → set DRY multiplier  
- `Builder dryBase(float)` → set DRY base  
- `Builder dryAllowedLength(int)` → set DRY allowed length  
- `Builder dryPenaltyLastN(int)` → set DRY penalty window  
- `Builder drySequenceBreakers(List<String>)` → set DRY breakers  
- `Builder stopStrings(List<String>)` → set stop strings  
- `LlamaCppJniConfig build()` → create immutable config  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` for default constants.  
- `net.ladenthin.srcmorph.support.ConvertToRecord` annotation.  
- `org.jspecify.annotations.Nullable`.  
- `java.util.*` for collections and `Objects`.  
- Lombok annotations `@EqualsAndHashCode`, `@ToString`.

#### Exceptions / Errors
- `Objects.requireNonNull` throws `NullPointerException` if `modelPath` is null in `Builder` constructor.

#### Concurrency
- Immutable after construction; thread‑safe.  
- Builder is not thread‑safe; intended single‑thread usage.

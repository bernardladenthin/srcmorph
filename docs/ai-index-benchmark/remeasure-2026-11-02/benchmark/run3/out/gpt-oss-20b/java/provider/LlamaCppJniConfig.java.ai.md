### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:53Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores all llama.cpp JNI configuration options.
- Provides immutable, value‑semantics access to settings.

#### Type
`final class LlamaCppJniConfig` – immutable, value type, Lombok `@EqualsAndHashCode`, `@ToString`, annotated `@ConvertToRecord`.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller‑supplied values.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all 37 builder fields into final fields.
- Getter methods return primitive or reference values.
- `drySequenceBreakers()` and `stopStrings()` wrap lists with `Collections.unmodifiableList`.
- `builder(String)` creates a new `Builder` with required `modelPath`.

#### Public API
- `static Builder builder(String)` → create builder with defaults  
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
- `boolean cachePrompt()` → prompt caching enabled  
- `boolean swaFull()` → full‑size SWA cache kept  
- `int cacheReuse()` → KV prefix‑reuse chunk size  
- `int gpuLayers()` → GPU layers offloaded  
- `int seed()` → RNG seed  
- `int cpuMoeLayers()` → CPU MoE layers kept  
- `int cpuFfnLayers()` → CPU FFN layers kept  
- `int kvUnifiedPerSlot()` → per‑slot KV cap  
- `String lazyMode()` → tensor‑read laziness  
- `int repeatLastN()` → repeat‑penalty window  
- `String cacheTypeK()` → KV‑cache type K  
- `String cacheTypeV()` → KV‑cache type V  
- `boolean flashAttn()` → Flash Attention enabled  
- `int batchSize()` → logical batch size  
- `int ubatchSize()` → physical batch size  
- `int threadsBatch()` → batch/prompt thread count  
- `int mainGpu()` → primary GPU index  
- `String devices()` → device selection  
- `String provider()` → AI provider (`llamacpp-jni`, `openai-compatible`, `mock`)  
- `String remoteModel()` → remote model ID for OpenAI‑compatible provider  
- `String baseUrl()` → OpenAI‑compatible API base URL  
- `String apiKeyEnv()` → environment variable name for API key  
- `String reasoningEffort()` → gpt‑oss reasoning‑effort value  
- `int reasoningBudgetTokens()` → reasoning token budget  
- `float dryMultiplier()` → DRY sampling multiplier  
- `float dryBase()` → DRY base  
- `int dryAllowedLength()` → DRY allowed length  
- `int dryPenaltyLastN()` → DRY penalty look‑back window  
- `List<String> drySequenceBreakers()` → unmodifiable DRY sequence breakers  
- `List<String> stopStrings()` → unmodifiable stop strings  

`Builder` (inner class) public methods  
- `Builder contextSize(int)` → set context size  
- `Builder maxOutputTokens(int)` → set max output tokens  
- `Builder temperature(float)` → set temperature  
- `Builder threads(int)` → set CPU threads  
- `Builder topP(float)` → set nucleus‑sampling threshold  
- `Builder topK(int)` → set top‑k limit  
- `Builder minP(float)` → set min‑p threshold  
- `Builder topNSigma(float)` → set top‑n‑sigma threshold  
- `Builder repeatPenalty(float)` → set repetition penalty  
- `Builder chatTemplateEnableThinking(@Nullable Boolean)` → set chat‑template flag  
- `Builder cachePrompt(boolean)` → set prompt caching  
- `Builder swaFull(boolean)` → set full‑size SWA cache  
- `Builder cacheReuse(int)` → set KV prefix‑reuse chunk size  
- `Builder gpuLayers(int)` → set GPU layers offloaded  
- `Builder seed(int)` → set RNG seed  
- `Builder cpuMoeLayers(int)` → set CPU MoE layers kept  
- `Builder cpuFfnLayers(int)` → set CPU FFN layers kept  
- `Builder kvUnifiedPerSlot(int)` → set per‑slot KV cap  
- `Builder lazyMode(String)` → set tensor‑read laziness  
- `Builder repeatLastN(int)` → set repeat‑penalty window  
- `Builder cacheTypeK(String)` → set KV‑cache type K  
- `Builder cacheTypeV(String)` → set KV‑cache type V  
- `Builder flashAttn(boolean)` → set Flash Attention  
- `Builder batchSize(int)` → set logical batch size  
- `Builder ubatchSize(int)` → set physical batch size  
- `Builder threadsBatch(int)` → set batch/prompt thread count  
- `Builder mainGpu(int)` → set primary GPU index  
- `Builder devices(String)` → set device selection  
- `Builder provider(String)` → set AI provider  
- `Builder remoteModel(String)` → set remote model ID  
- `Builder baseUrl(String)` → set OpenAI‑compatible base URL  
- `Builder apiKeyEnv(String)` → set API key environment variable name  
- `Builder reasoningEffort(String)` → set reasoning‑effort value  
- `Builder reasoningBudgetTokens(int)` → set reasoning token budget  
- `Builder dryMultiplier(float)` → set DRY multiplier  
- `Builder dryBase(float)` → set DRY base  
- `Builder dryAllowedLength(int)` → set DRY allowed length  
- `Builder dryPenaltyLastN(int)` → set DRY penalty look‑back window  
- `Builder drySequenceBreakers(List<String>)` → set DRY sequence breakers  
- `Builder stopStrings(List<String>)` → set stop strings  
- `LlamaCppJniConfig build()` → create immutable config

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.*`
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is `null`.

#### Concurrency
- Immutable after construction; thread‑safe for reads.
- Builder is not thread‑safe; intended for single‑thread use.

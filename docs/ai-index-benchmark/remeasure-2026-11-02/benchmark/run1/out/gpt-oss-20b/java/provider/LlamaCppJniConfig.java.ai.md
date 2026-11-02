### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:38Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model‑ and runtime‑tuning parameters.

#### Purpose
- Stores all llama.cpp JNI configuration options.
- Provides read‑only accessors for each setting.

#### Type
`final class LlamaCppJniConfig` – immutable, Lombok‑generated `equals`, `hashCode`, `toString`. Implements `ConvertToRecord`. 37 private final fields.

#### Input
- `Builder` instance supplied to private constructor.
- `Builder` receives `modelPath` (required) and optional setters for 36 other fields, each defaulting to `AiGenerationConfig.DEFAULT_*`.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields into final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each accessor returns the corresponding field.
- `drySequenceBreakers()` and `stopStrings()` wrap lists with `Collections.unmodifiableList`.

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
- `boolean swaFull()` → full‑size SWA cache kept  
- `int cacheReuse()` → KV prefix‑reuse chunk size  
- `int gpuLayers()` → GPU offload layers  
- `int seed()` → RNG seed  
- `int cpuMoeLayers()` → CPU MoE layers  
- `int cpuFfnLayers()` → CPU FFN layers  
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
- `String devices()` → device list  
- `String provider()` → AI provider name  
- `String remoteModel()` → remote model ID  
- `String baseUrl()` → OpenAI‑compatible base URL  
- `String apiKeyEnv()` → API key environment variable name  
- `String reasoningEffort()` → reasoning‑effort value  
- `int reasoningBudgetTokens()` → reasoning token budget  
- `float dryMultiplier()` → DRY multiplier  
- `float dryBase()` → DRY base  
- `int dryAllowedLength()` → DRY allowed length  
- `int dryPenaltyLastN()` → DRY penalty look‑back  
- `List<String> drySequenceBreakers()` → unmodifiable DRY breakers  
- `List<String> stopStrings()` → unmodifiable stop strings  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.*`
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is `null`.

#### Concurrency
- Immutable after construction; thread‑safe. Builder is not thread‑safe.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:16:26Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all generation and runtime knobs.

#### Purpose
- Holds all llama.cpp JNI configuration options.
- Provides immutable access to settings for model execution.

#### Type
`final class LlamaCppJniConfig` – immutable value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`. 37 private final fields.

#### Input
- `Builder` instance supplied to private constructor.
- `Builder` receives `modelPath` (required) and optional overrides for 36 other fields, defaulted from `AiGenerationConfig.DEFAULT_*`.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields to final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field.
- `drySequenceBreakers()` and `stopStrings()` wrap lists with `Collections.unmodifiableList`.
- `Builder` initializes all fields to defaults, provides fluent setters, and `build()` creates the config.

#### Public API
- `static Builder builder(String)` → builder with defaults  
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
- `List<String> drySequenceBreakers()` → unmodifiable list  
- `List<String> stopStrings()` → unmodifiable list  

`Builder` (inner class)  
- `Builder(String)` – constructor (requires `modelPath`)  
- 36 fluent setters (`contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`) – each returns `this`.  
- `LlamaCppJniConfig build()` → constructs immutable config.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is `null`.
- `Builder` constructor uses `Objects.requireNonNull` for `modelPath`.

#### Concurrency
- Immutable `LlamaCppJniConfig` is thread‑safe.
- `Builder` is not thread‑safe; intended for single‑thread use.

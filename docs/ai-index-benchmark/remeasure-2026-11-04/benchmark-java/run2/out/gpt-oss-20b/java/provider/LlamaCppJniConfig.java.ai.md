### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:21Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all model‑level and runtime options.

#### Purpose
- Holds all llama.cpp JNI parameters.
- Provides a fluent builder with defaults from `AiGenerationConfig`.

#### Type
`final class LlamaCppJniConfig`  
- Immutable, record‑style value type.  
- Annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
- 41 private final fields.

#### Input
- `Builder` instance supplied to private constructor.  
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller values.  
- `modelPath` required via `builder(String)`.

#### Output
- Immutable `LlamaCppJniConfig` instance.  
- Getter methods expose each field.  
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable lists.

#### Core logic
- Private constructor copies all 41 builder fields.  
- `builder(String)` creates a new `Builder` with required `modelPath`.  
- Each builder setter assigns a value and returns `this`.  
- `build()` returns a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.  
- Getters:  
  - `modelPath()` → model file path  
  - `contextSize()` → context window size  
  - `maxOutputTokens()` → max output tokens  
  - `temperature()` → sampling temperature  
  - `threads()` → CPU thread count  
  - `topP()` → nucleus‑sampling threshold  
  - `topK()` → top‑k limit  
  - `minP()` → min‑p threshold  
  - `topNSigma()` → top‑n‑sigma threshold  
  - `repeatPenalty()` → repetition penalty  
  - `chatTemplateEnableThinking()` → chat‑template thinking flag (nullable)  
  - `cachePrompt()` → prompt caching enabled  
  - `swaFull()` → full SWA cache kept  
  - `cacheReuse()` → KV prefix‑reuse chunk size  
  - `gpuLayers()` → GPU layers offloaded  
  - `seed()` → RNG seed  
  - `cpuMoeLayers()` → CPU MoE layers  
  - `cpuFfnLayers()` → CPU FFN layers  
  - `kvUnifiedPerSlot()` → per‑slot KV cap  
  - `lazyMode()` → tensor‑read laziness  
  - `repeatLastN()` → repeat‑penalty window  
  - `cacheTypeK()` → KV cache type K  
  - `cacheTypeV()` → KV cache type V  
  - `flashAttn()` → Flash Attention enabled  
  - `batchSize()` → logical batch size  
  - `ubatchSize()` → physical batch size  
  - `threadsBatch()` → batch thread count  
  - `mainGpu()` → primary GPU index  
  - `devices()` → device selection  
  - `provider()` → AI provider name  
  - `remoteModel()` → remote model ID  
  - `baseUrl()` → OpenAI‑compatible base URL  
  - `apiKeyEnv()` → API key environment variable name  
  - `reasoningEffort()` → reasoning‑effort value  
  - `reasoningBudgetTokens()` → reasoning token budget  
  - `dryMultiplier()` → DRY multiplier  
  - `dryBase()` → DRY base  
  - `dryAllowedLength()` → DRY allowed length  
  - `dryPenaltyLastN()` → DRY penalty look‑back  
  - `drySequenceBreakers()` → unmodifiable list of DRY breakers  
  - `stopStrings()` → unmodifiable list of stop strings  

- `Builder` methods:  
  - 37 setters (`contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`).  
  - `build()` → `LlamaCppJniConfig`.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` for default constants.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.  
- `java.util.*` (Collections, List, Objects).  
- Lombok annotations (`@EqualsAndHashCode`, `@ToString`).

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is `null`.  
- `Builder` constructor uses `Objects.requireNonNull` for `modelPath`.

#### Concurrency
- Immutable after construction; thread‑safe.  
- Builder is not thread‑safe; intended for single‑thread use.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:42:17Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores all llama.cpp JNI configuration parameters.
- Provides immutable, record‑style accessors.
- Builds via a fluent Builder with defaults from `AiGenerationConfig`.

#### Type
`final class LlamaCppJniConfig` (class, final).  
Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
Extends: none. Implements: none.  
Fields: 37 private final fields (see Input).

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller values.
- `modelPath` required non‑null.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Public getters return primitive or immutable views of lists.
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable lists.

#### Core logic
- Constructor copies all builder fields.
- `builder(String)` creates a new Builder with defaults.
- Each getter simply returns its field.
- Builder setters assign values and return `this`.
- `build()` returns a new `LlamaCppJniConfig`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.
- `String modelPath()` – GGUF model file path.
- `int contextSize()` – context window size.
- `int maxOutputTokens()` – max output tokens.
- `float temperature()` – sampling temperature.
- `int threads()` – CPU thread count.
- `float topP()` – nucleus‑sampling threshold.
- `int topK()` – top‑k limit.
- `float minP()` – min‑p threshold.
- `float topNSigma()` – top‑n‑sigma threshold.
- `float repeatPenalty()` – repetition penalty.
- `@Nullable Boolean chatTemplateEnableThinking()` – chat‑template thinking flag.
- `boolean cachePrompt()` – prompt caching enabled.
- `boolean swaFull()` – full‑size SWA cache kept.
- `int cacheReuse()` – KV prefix‑reuse chunk size.
- `int gpuLayers()` – GPU layers offloaded.
- `int seed()` – RNG seed.
- `int cpuMoeLayers()` – CPU MoE layers kept.
- `int cpuFfnLayers()` – CPU FFN layers kept.
- `int kvUnifiedPerSlot()` – per‑slot KV cap.
- `String lazyMode()` – tensor‑read laziness.
- `int repeatLastN()` – repeat‑penalty window.
- `String cacheTypeK()` – KV cache data type for K.
- `String cacheTypeV()` – KV cache data type for V.
- `boolean flashAttn()` – Flash Attention enabled.
- `int batchSize()` – logical batch size.
- `int ubatchSize()` – physical batch size.
- `int threadsBatch()` – batch/prompt thread count.
- `int mainGpu()` – primary GPU index.
- `String devices()` – device selection.
- `String provider()` – AI provider name.
- `String remoteModel()` – remote model ID.
- `String baseUrl()` – OpenAI‑compatible base URL.
- `String apiKeyEnv()` – API key environment variable.
- `String reasoningEffort()` – reasoning‑effort value.
- `int reasoningBudgetTokens()` – reasoning token budget.
- `float dryMultiplier()` – DRY multiplier.
- `float dryBase()` – DRY base.
- `int dryAllowedLength()` – DRY allowed length.
- `int dryPenaltyLastN()` – DRY penalty look‑back.
- `List<String> drySequenceBreakers()` – unmodifiable DRY breakers.
- `List<String> stopStrings()` – unmodifiable stop strings.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` setters accept any value; no validation beyond null checks for strings that may be null to restore defaults.

#### Concurrency
- Immutable after construction; thread‑safe read access.
- Builder is not thread‑safe; intended for single‑thread use.

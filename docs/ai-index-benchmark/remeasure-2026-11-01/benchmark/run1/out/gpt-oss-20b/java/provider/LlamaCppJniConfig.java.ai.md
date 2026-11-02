### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:26:27Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores all llama.cpp JNI configuration parameters.
- Provides immutable, record‑style accessors.
- Builds via a fluent builder with defaults from `AiGenerationConfig`.

#### Type
`final class LlamaCppJniConfig` – immutable, record‑style value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `Builder` instance populated via fluent setters; `modelPath` required.
- Defaults supplied by `AiGenerationConfig.DEFAULT_*` constants.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Private constructor copies all builder fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field.
- `Builder` methods set individual fields and return `this`.
- `build()` returns a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String modelPath)` – start builder.
- `String modelPath()` – model file path.
- `int contextSize()` – context window size.
- `int maxOutputTokens()` – max output tokens.
- `float temperature()` – sampling temperature.
- `int threads()` – CPU threads.
- `float topP()` – nucleus‑sampling threshold.
- `int topK()` – top‑k limit.
- `float minP()` – min‑p threshold.
- `float topNSigma()` – top‑n‑sigma threshold.
- `float repeatPenalty()` – repetition penalty.
- `@Nullable Boolean chatTemplateEnableThinking()` – chat‑template thinking flag.
- `boolean cachePrompt()` – prompt caching enabled.
- `boolean swaFull()` – full SWA cache kept.
- `int cacheReuse()` – KV prefix‑reuse chunk size.
- `int gpuLayers()` – GPU layers offloaded.
- `int seed()` – RNG seed.
- `int cpuMoeLayers()` – CPU MoE layers kept.
- `int cpuFfnLayers()` – CPU FFN layers kept.
- `int kvUnifiedPerSlot()` – per‑slot KV context cap.
- `String lazyMode()` – tensor‑read laziness.
- `int repeatLastN()` – repeat‑penalty window.
- `String cacheTypeK()` – KV‑cache data type for K.
- `String cacheTypeV()` – KV‑cache data type for V.
- `boolean flashAttn()` – Flash Attention enabled.
- `int batchSize()` – logical batch size.
- `int ubatchSize()` – physical batch size.
- `int threadsBatch()` – batch/prompt‑processing threads.
- `int mainGpu()` – primary GPU index.
- `String devices()` – device selection.
- `String provider()` – AI provider identifier.
- `String remoteModel()` – remote model ID for OpenAI‑compatible provider.
- `String baseUrl()` – base URL for OpenAI‑compatible API.
- `String apiKeyEnv()` – environment variable name for API key.
- `String reasoningEffort()` – reasoning‑effort kwarg value.
- `int reasoningBudgetTokens()` – reasoning token budget.
- `float dryMultiplier()` – DRY sampling multiplier.
- `float dryBase()` – DRY base.
- `int dryAllowedLength()` – DRY allowed length.
- `int dryPenaltyLastN()` – DRY penalty look‑back window.
- `List<String> drySequenceBreakers()` – unmodifiable DRY sequence breakers.
- `List<String> stopStrings()` – unmodifiable stop strings.
- `static final class Builder` – fluent builder.
  - `Builder contextSize(int)` … `Builder stopStrings(List<String>)` – 40 setter methods.
  - `LlamaCppJniConfig build()` – constructs immutable config.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is `null`.
- `Builder` constructor enforces non‑null `modelPath`.

#### Concurrency
- `LlamaCppJniConfig` is immutable and thread‑safe.
- `Builder` is not thread‑safe; intended for single‑thread use.

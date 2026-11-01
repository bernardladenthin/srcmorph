### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:52Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores all llama.cpp JNI configuration options.
- Provides immutable, record‑style accessors.
- Builds via a fluent Builder with defaults from `AiGenerationConfig`.

#### Type
`final class LlamaCppJniConfig` – immutable, record‑style value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `Builder` instance supplied to the private constructor.
- `Builder` receives a mandatory `modelPath` and optional overrides for 36 other fields.
- Defaults are sourced from `AiGenerationConfig.DEFAULT_*`.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields into final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field.
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable lists.
- `Builder` methods set individual fields and return `this` for chaining.
- `build()` constructs the immutable config.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.
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
- `boolean swaFull()` – full‑size SWA cache kept.
- `int cacheReuse()` – KV prefix‑reuse chunk size.
- `int gpuLayers()` – GPU layers offloaded.
- `int seed()` – RNG seed.
- `int cpuMoeLayers()` – CPU MoE layers kept.
- `int cpuFfnLayers()` – CPU FFN layers kept.
- `int kvUnifiedPerSlot()` – per‑slot unified KV cap.
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
- `String provider()` – AI provider name.
- `String remoteModel()` – remote model ID.
- `String baseUrl()` – OpenAI‑compatible base URL.
- `String apiKeyEnv()` – API key environment variable.
- `String reasoningEffort()` – reasoning‑effort kwarg.
- `int reasoningBudgetTokens()` – reasoning budget tokens.
- `float dryMultiplier()` – DRY sampling multiplier.
- `float dryBase()` – DRY base.
- `int dryAllowedLength()` – DRY allowed length.
- `int dryPenaltyLastN()` – DRY penalty look‑back window.
- `List<String> drySequenceBreakers()` – unmodifiable DRY sequence breakers.
- `List<String> stopStrings()` – unmodifiable stop strings.
- `Builder contextSize(int)` – set context size.
- `Builder maxOutputTokens(int)` – set max output tokens.
- `Builder temperature(float)` – set temperature.
- `Builder threads(int)` – set CPU threads.
- `Builder topP(float)` – set nucleus‑sampling threshold.
- `Builder topK(int)` – set top‑k limit.
- `Builder minP(float)` – set min‑p threshold.
- `Builder topNSigma(float)` – set top‑n‑sigma threshold.
- `Builder repeatPenalty(float)` – set repetition penalty.
- `Builder chatTemplateEnableThinking(@Nullable Boolean)` – set chat‑template thinking flag.
- `Builder cachePrompt(boolean)` – set prompt caching.
- `Builder swaFull(boolean)` – set full‑size SWA cache.
- `Builder cacheReuse(int)` – set KV prefix‑reuse chunk size.
- `Builder gpuLayers(int)` – set GPU layers offloaded.
- `Builder seed(int)` – set RNG seed.
- `Builder cpuMoeLayers(int)` – set CPU MoE layers.
- `Builder cpuFfnLayers(int)` – set CPU FFN layers.
- `Builder kvUnifiedPerSlot(int)` – set per‑slot unified KV cap.
- `Builder lazyMode(String)` – set tensor‑read laziness.
- `Builder repeatLastN(int)` – set repeat‑penalty window.
- `Builder cacheTypeK(String)` – set KV‑cache type K.
- `Builder cacheTypeV(String)` – set KV‑cache type V.
- `Builder flashAttn(boolean)` – set Flash Attention.
- `Builder batchSize(int)` – set logical batch size.
- `Builder ubatchSize(int)` – set physical batch size.
- `Builder threadsBatch(int)` – set batch/prompt‑processing threads.
- `Builder mainGpu(int)` – set primary GPU index.
- `Builder devices(String)` – set device selection.
- `Builder provider(String)` – set AI provider.
- `Builder remoteModel(String)` – set remote model ID.
- `Builder baseUrl(String)` – set base URL.
- `Builder apiKeyEnv(String)` – set API key env var.
- `Builder reasoningEffort(String)` – set reasoning‑effort kwarg.
- `Builder reasoningBudgetTokens(int)` – set reasoning budget tokens.
- `Builder dryMultiplier(float)` – set DRY multiplier.
- `Builder dryBase(float)` – set DRY base.
- `Builder dryAllowedLength(int)` – set DRY allowed length.
- `Builder dryPenaltyLastN(int)` – set DRY penalty look‑back.
- `Builder drySequenceBreakers(List<String>)` – set DRY sequence breakers.
- `Builder stopStrings(List<String>)` – set stop strings.
- `Builder build() -> LlamaCppJniConfig` – create immutable config.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `NullPointerException` if `modelPath` is null in `builder(String)` or `Builder` constructor.
- No other checked exceptions.

#### Concurrency
- Immutable after construction; thread‑safe.
- `Builder` is not thread‑safe; intended for single‑thread use.

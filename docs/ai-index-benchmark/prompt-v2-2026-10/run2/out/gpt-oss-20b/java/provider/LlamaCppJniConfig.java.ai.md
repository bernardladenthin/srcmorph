### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:51:13Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model and runtime options.

#### Purpose
- Stores llama.cpp JNI configuration values.
- Provides read‑only access to all parameters.

#### Type
`final class LlamaCppJniConfig` (class, final). Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`. Extends `Object`. No interfaces.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller overrides.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields to final fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Getters return stored values; list getters wrap with `Collections.unmodifiableList`.
- `Builder` setters assign values and return `this`; `build()` creates config.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start builder.
- `String modelPath()` – GGUF file path.
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
- `int gpuLayers()` – GPU offload layers.
- `int seed()` – RNG seed.
- `int cpuMoeLayers()` – CPU MoE layers.
- `int cpuFfnLayers()` – CPU FFN layers.
- `int kvUnifiedPerSlot()` – KV unified per‑slot cap.
- `String lazyMode()` – tensor‑read laziness.
- `int repeatLastN()` – repeat‑penalty window.
- `String cacheTypeK()` – KV cache type K.
- `String cacheTypeV()` – KV cache type V.
- `boolean flashAttn()` – Flash Attention enabled.
- `int batchSize()` – logical batch size.
- `int ubatchSize()` – physical batch size.
- `int threadsBatch()` – batch/prompt thread count.
- `int mainGpu()` – primary GPU index.
- `String devices()` – device list.
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
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` setters accept any value; no validation beyond null checks for optional strings.

#### Concurrency
- Immutable after construction; thread‑safe read access.
- `Builder` is not thread‑safe; intended single‑thread usage.

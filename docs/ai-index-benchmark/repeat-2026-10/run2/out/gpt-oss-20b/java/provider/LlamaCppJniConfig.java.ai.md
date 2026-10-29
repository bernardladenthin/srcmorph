### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:13:24Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model and runtime options.

#### Purpose
- Stores llama.cpp runtime parameters.
- Provides immutable, record‑style accessors.
- Built via a fluent builder with defaults from `AiGenerationConfig`.

#### Type
- `final class` with 37 private final fields.
- Annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Implements no interfaces.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller values.
- `modelPath` required in `builder(String)`.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields.
- Builder setters assign values and return `this`.
- `build()` creates new config via private constructor.
- Getters return stored values; list getters wrap with `Collections.unmodifiableList`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start builder.
- `String modelPath() -> String` – model file path.
- `int contextSize() -> int` – context window size.
- `int maxOutputTokens() -> int` – max output tokens.
- `float temperature() -> float` – sampling temperature.
- `int threads() -> int` – CPU thread count.
- `float topP() -> float` – nucleus‑sampling threshold.
- `int topK() -> int` – top‑k limit.
- `float minP() -> float` – min‑p threshold.
- `float topNSigma() -> float` – top‑n‑sigma threshold.
- `float repeatPenalty() -> float` – repetition penalty.
- `@Nullable Boolean chatTemplateEnableThinking() -> Boolean` – chat‑template thinking flag.
- `boolean cachePrompt() -> boolean` – prompt caching enabled.
- `boolean swaFull() -> boolean` – full‑size SWA cache kept.
- `int cacheReuse() -> int` – KV prefix‑reuse chunk size.
- `int gpuLayers() -> int` – GPU layers offloaded.
- `int seed() -> int` – RNG seed.
- `int cpuMoeLayers() -> int` – CPU MoE layers kept.
- `int cpuFfnLayers() -> int` – CPU FFN layers kept.
- `int kvUnifiedPerSlot() -> int` – per‑slot KV context cap.
- `String lazyMode() -> String` – tensor‑read laziness.
- `int repeatLastN() -> int` – repeat‑penalty window.
- `String cacheTypeK() -> String` – KV cache type K.
- `String cacheTypeV() -> String` – KV cache type V.
- `boolean flashAttn() -> boolean` – Flash Attention enabled.
- `int batchSize() -> int` – logical batch size.
- `int ubatchSize() -> int` – physical batch size.
- `int threadsBatch() -> int` – batch/prompt thread count.
- `int mainGpu() -> int` – primary GPU index.
- `String devices() -> String` – device selection.
- `String provider() -> String` – AI provider identifier.
- `String remoteModel() -> String` – remote model ID for OpenAI‑compatible provider.
- `String baseUrl() -> String` – OpenAI‑compatible base URL.
- `String apiKeyEnv() -> String` – API key environment variable name.
- `String reasoningEffort() -> String` – reasoning‑effort value.
- `int reasoningBudgetTokens() -> int` – reasoning token budget.
- `float dryMultiplier() -> float` – DRY sampling multiplier.
- `float dryBase() -> float` – DRY base.
- `int dryAllowedLength() -> int` – DRY allowed length.
- `int dryPenaltyLastN() -> int` – DRY penalty look‑back window.
- `List<String> drySequenceBreakers() -> List<String>` – unmodifiable DRY sequence breakers.
- `List<String> stopStrings() -> List<String>` – unmodifiable stop strings.

#### Dependencies
- `AiGenerationConfig` for default constants.
- `ConvertToRecord`, `ToString`, `EqualsAndHashCode` from Lombok.
- `org.jspecify.annotations.Nullable`.
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is null.

#### Concurrency
- Immutable after construction; thread‑safe.
- Builder is not thread‑safe; intended single‑thread use.

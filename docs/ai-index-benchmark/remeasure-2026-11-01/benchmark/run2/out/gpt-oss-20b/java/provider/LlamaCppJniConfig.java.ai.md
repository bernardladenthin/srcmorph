### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:46Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model and runtime options.

#### Purpose
- Stores llama.cpp JNI configuration values.
- Provides read‑only access to all options.

#### Type
`public final class LlamaCppJniConfig` – immutable value type, annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `Builder` instance supplied to the private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or overridden by setters.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all builder fields into final fields.
- Getters return primitive or reference values; list getters wrap with `Collections.unmodifiableList`.
- Builder initializes defaults, validates `modelPath`, and provides fluent setters for each field.
- `build()` creates the immutable configuration.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building with required model path.
- `String modelPath() -> model file path`
- `int contextSize() -> context window size`
- `int maxOutputTokens() -> max output tokens`
- `float temperature() -> sampling temperature`
- `int threads() -> CPU thread count`
- `float topP() -> nucleus‑sampling threshold`
- `int topK() -> top‑k limit`
- `float minP() -> min‑p threshold`
- `float topNSigma() -> top‑n‑sigma threshold`
- `float repeatPenalty() -> repetition penalty`
- `@Nullable Boolean chatTemplateEnableThinking() -> chat‑template thinking flag`
- `boolean cachePrompt() -> prompt caching enabled`
- `boolean swaFull() -> full‑size SWA cache kept`
- `int cacheReuse() -> KV prefix‑reuse chunk size`
- `int gpuLayers() -> GPU layers offloaded`
- `int seed() -> RNG seed`
- `int cpuMoeLayers() -> CPU MoE layers`
- `int cpuFfnLayers() -> CPU FFN layers`
- `int kvUnifiedPerSlot() -> per‑slot KV cap`
- `String lazyMode() -> tensor‑read laziness`
- `int repeatLastN() -> repeat‑penalty window`
- `String cacheTypeK() -> KV cache type K`
- `String cacheTypeV() -> KV cache type V`
- `boolean flashAttn() -> Flash Attention enabled`
- `int batchSize() -> logical batch size`
- `int ubatchSize() -> physical batch size`
- `int threadsBatch() -> batch thread count`
- `int mainGpu() -> primary GPU index`
- `String devices() -> device list`
- `String provider() -> AI provider`
- `String remoteModel() -> remote model ID`
- `String baseUrl() -> OpenAI‑compatible base URL`
- `String apiKeyEnv() -> API key environment variable`
- `String reasoningEffort() -> reasoning‑effort value`
- `int reasoningBudgetTokens() -> reasoning budget`
- `float dryMultiplier() -> DRY multiplier`
- `float dryBase() -> DRY base`
- `int dryAllowedLength() -> DRY allowed length`
- `int dryPenaltyLastN() -> DRY penalty look‑back`
- `List<String> drySequenceBreakers() -> unmodifiable DRY breakers`
- `List<String> stopStrings() -> unmodifiable stop strings`
- `public static final class Builder` – fluent builder
  - `Builder contextSize(int) -> Builder`
  - `Builder maxOutputTokens(int) -> Builder`
  - `Builder temperature(float) -> Builder`
  - `Builder threads(int) -> Builder`
  - `Builder topP(float) -> Builder`
  - `Builder topK(int) -> Builder`
  - `Builder minP(float) -> Builder`
  - `Builder topNSigma(float) -> Builder`
  - `Builder repeatPenalty(float) -> Builder`
  - `Builder chatTemplateEnableThinking(@Nullable Boolean) -> Builder`
  - `Builder cachePrompt(boolean) -> Builder`
  - `Builder swaFull(boolean) -> Builder`
  - `Builder cacheReuse(int) -> Builder`
  - `Builder gpuLayers(int) -> Builder`
  - `Builder seed(int) -> Builder`
  - `Builder cpuMoeLayers(int) -> Builder`
  - `Builder cpuFfnLayers(int) -> Builder`
  - `Builder kvUnifiedPerSlot(int) -> Builder`
  - `Builder lazyMode(String) -> Builder`
  - `Builder repeatLastN(int) -> Builder`
  - `Builder cacheTypeK(String) -> Builder`
  - `Builder cacheTypeV(String) -> Builder`
  - `Builder flashAttn(boolean) -> Builder`
  - `Builder batchSize(int) -> Builder`
  - `Builder ubatchSize(int) -> Builder`
  - `Builder threadsBatch(int) -> Builder`
  - `Builder mainGpu(int) -> Builder`
  - `Builder devices(String) -> Builder`
  - `Builder provider(String) -> Builder`
  - `Builder remoteModel(String) -> Builder`
  - `Builder baseUrl(String) -> Builder`
  - `Builder apiKeyEnv(String) -> Builder`
  - `Builder reasoningEffort(String) -> Builder`
  - `Builder reasoningBudgetTokens(int) -> Builder`
  - `Builder dryMultiplier(float) -> Builder`
  - `Builder dryBase(float) -> Builder`
  - `Builder dryAllowedLength(int) -> Builder`
  - `Builder dryPenaltyLastN(int) -> Builder`
  - `Builder drySequenceBreakers(List<String>) -> Builder`
  - `Builder stopStrings(List<String>) -> Builder`
  - `LlamaCppJniConfig build() -> LlamaCppJniConfig`

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`
- `java.util.Collections`, `java.util.List`, `java.util.Objects`
- Lombok annotations `@EqualsAndHashCode`, `@ToString`

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is null.
- `Objects.requireNonNull` used in builder constructor.

#### Concurrency
- Immutable after construction; thread‑safe read access.
- Builder is not thread‑safe; intended for single‑thread use.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:20Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model‑generation knobs and defaults.

#### Purpose
- Stores 37 llama.cpp runtime parameters.
- Provides a fluent builder that applies `AiGenerationConfig.DEFAULT_*` values.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - 37 private final fields (see Input).  
- `public static final class Builder` (class, final)  
  - Annotations: none.  
  - 40 private fields (builder state).  
  - Constructor `Builder(String modelPath)` (private).  
  - 40 public setter methods.  
  - `build()` returns `LlamaCppJniConfig`.

#### Input
- Constructor: `Builder` receives `String modelPath` (required).  
- Builder setters accept values for each of the 37 configuration knobs (ints, floats, booleans, `String`, `List<String>`, `@Nullable Boolean`).  
- Default values are sourced from `AiGenerationConfig.DEFAULT_*`.

#### Output
- `LlamaCppJniConfig` instances expose:
  - 37 public getter methods returning the configured values.
  - Unmodifiable views for `drySequenceBreakers` and `stopStrings`.
- Builder produces immutable configuration via `build()`.

#### Core logic
- `LlamaCppJniConfig` constructor copies all builder fields into final fields.
- Builder setters assign provided values to mutable fields; many accept `null` to restore defaults.
- `build()` creates a new `LlamaCppJniConfig` with the current builder state.

#### Public API
- `Builder builder(String modelPath) -> Builder` – creates a builder with defaults.  
- `String modelPath() -> String` – model file path.  
- `int contextSize() -> int` – context window size.  
- `int maxOutputTokens() -> int` – max output tokens.  
- `float temperature() -> float` – sampling temperature.  
- `int threads() -> int` – CPU threads.  
- `float topP() -> float` – nucleus‑sampling threshold.  
- `int topK() -> int` – top‑k limit.  
- `float minP() -> float` – min‑p threshold.  
- `float topNSigma() -> float` – top‑n‑sigma threshold.  
- `float repeatPenalty() -> float` – repetition penalty.  
- `@Nullable Boolean chatTemplateEnableThinking() -> Boolean` – chat‑template thinking flag.  
- `boolean cachePrompt() -> boolean` – prompt caching.  
- `boolean swaFull() -> boolean` – full‑size SWA cache.  
- `int cacheReuse() -> int` – KV prefix‑reuse chunk size.  
- `int gpuLayers() -> int` – GPU layers offloaded.  
- `int seed() -> int` – RNG seed.  
- `int cpuMoeLayers() -> int` – CPU MoE layers.  
- `int cpuFfnLayers() -> int` – CPU FFN layers.  
- `int kvUnifiedPerSlot() -> int` – KV unified per‑slot cap.  
- `String lazyMode() -> String` – tensor‑read laziness.  
- `int repeatLastN() -> int` – repeat‑penalty window.  
- `String cacheTypeK() -> String` – KV cache type K.  
- `String cacheTypeV() -> String` – KV cache type V.  
- `boolean flashAttn() -> boolean` – Flash Attention.  
- `int batchSize() -> int` – logical batch size.  
- `int ubatchSize() -> int` – physical batch size.  
- `int threadsBatch() -> int` – batch/prompt thread count.  
- `int mainGpu() -> int` – primary GPU index.  
- `String devices() -> String` – device list.  
- `String provider() -> String` – AI provider.  
- `String remoteModel() -> String` – remote model ID.  
- `String baseUrl() -> String` – OpenAI‑compatible base URL.  
- `String apiKeyEnv() -> String` – API key environment variable.  
- `String reasoningEffort() -> String` – reasoning‑effort level.  
- `int reasoningBudgetTokens() -> int` – reasoning token budget.  
- `float dryMultiplier() -> float` – DRY multiplier.  
- `float dryBase() -> float` – DRY base.  
- `int dryAllowedLength() -> int` – DRY allowed length.  
- `int dryPenaltyLastN() -> int` – DRY penalty look‑back.  
- `List<String> drySequenceBreakers() -> List<String>` – unmodifiable list.  
- `List<String> stopStrings() -> List<String>` – unmodifiable list.  
- Builder setters (40 total) each return `Builder` for chaining.  
- `LlamaCppJniConfig build() -> LlamaCppJniConfig` – creates immutable config.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- `lombok.EqualsAndHashCode`, `lombok.ToString`.  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `Objects.requireNonNull(modelPath, "modelPath")` throws `NullPointerException` if `modelPath` is null.  
- No other explicit exceptions.

#### Concurrency
- Builder is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

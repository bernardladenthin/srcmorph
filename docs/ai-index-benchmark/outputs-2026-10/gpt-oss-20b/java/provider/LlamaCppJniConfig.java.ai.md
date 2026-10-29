### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:50:00Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, built via a fluent builder.

#### Purpose
- Stores all runtime options for llama.cpp JNI provider.
- Provides read‑only accessors for configuration values.

#### Type
- `final class`  
- Extends `Object`.  
- Implements no interfaces.  
- Annotated with `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
- 37 private final fields.

#### Input
- `Builder` instance supplied to private constructor.  
- `Builder` receives `modelPath` (required) and optional setters for all other 36 fields.  
- Default values come from `AiGenerationConfig.DEFAULT_*`.

#### Output
- Immutable `LlamaCppJniConfig` instance.  
- Unmodifiable views for `drySequenceBreakers` and `stopStrings`.  
- No side effects.

#### Core logic
- Constructor copies all builder fields into final fields.  
- `builder(String)` creates a new `Builder` with required `modelPath`.  
- Each getter returns the corresponding field.  
- `drySequenceBreakers()` and `stopStrings()` wrap lists with `Collections.unmodifiableList`.  
- `Builder.build()` returns a new `LlamaCppJniConfig`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building.  
- `String modelPath() -> String` – model file path.  
- `int contextSize() -> int` – context window size.  
- `int maxOutputTokens() -> int` – max output tokens.  
- `float temperature() -> float` – sampling temperature.  
- `int threads() -> int` – CPU thread count.  
- `float topP() -> float` – nucleus sampling threshold.  
- `int topK() -> int` – top‑k limit.  
- `float minP() -> float` – min‑p threshold.  
- `float topNSigma() -> float` – top‑n‑sigma threshold.  
- `float repeatPenalty() -> float` – repetition penalty.  
- `@Nullable Boolean chatTemplateEnableThinking() -> Boolean` – chat‑template thinking flag.  
- `boolean cachePrompt() -> boolean` – prompt caching enabled.  
- `boolean swaFull() -> boolean` – full SWA cache kept.  
- `int cacheReuse() -> int` – KV prefix‑reuse chunk size.  
- `int gpuLayers() -> int` – GPU layers offloaded.  
- `int seed() -> int` – RNG seed.  
- `int cpuMoeLayers() -> int` – CPU MoE layers.  
- `int cpuFfnLayers() -> int` – CPU FFN layers.  
- `int kvUnifiedPerSlot() -> int` – per‑slot unified KV cap.  
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
- `String provider() -> String` – AI provider.  
- `String remoteModel() -> String` – remote model ID.  
- `String baseUrl() -> String` – OpenAI‑compatible base URL.  
- `String apiKeyEnv() -> String` – API key environment variable.  
- `String reasoningEffort() -> String` – reasoning‑effort value.  
- `int reasoningBudgetTokens() -> int` – reasoning token budget.  
- `float dryMultiplier() -> float` – DRY multiplier.  
- `float dryBase() -> float` – DRY base.  
- `int dryAllowedLength() -> int` – DRY allowed length.  
- `int dryPenaltyLastN() -> int` – DRY penalty look‑back.  
- `List<String> drySequenceBreakers() -> List<String>` – unmodifiable list.  
- `List<String> stopStrings() -> List<String>` – unmodifiable list.  

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` – default constants.  
- `net.ladenthin.srcmorph.support.ConvertToRecord` – annotation.  
- `org.jspecify.annotations.Nullable` – nullability marker.  
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- Lombok annotations `@EqualsAndHashCode`, `@ToString`.

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is null.  
- `Objects.requireNonNull` used in `Builder` constructor.

#### Concurrency
- Immutable after construction; thread‑safe.  
- `Builder` is not thread‑safe; intended for single‑thread use.

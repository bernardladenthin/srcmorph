### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:53:33Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, built via a fluent builder.

#### Purpose
- Stores all llama.cpp runtime options.
- Provides read‑only accessors for configuration values.

#### Type
- `final class`  
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`  
- Extends `Object`; implements no interfaces.

#### Input
- Constructor: `private LlamaCppJniConfig(Builder)`  
- Builder constructor: `Builder(String modelPath)` (requires non‑null `modelPath`)  
- Builder setters: 37 optional parameters, each named after a configuration knob.  
- `build()` consumes the populated builder.

#### Output
- Immutable `LlamaCppJniConfig` instance.  
- Accessors return primitive values, `String`, `List<String>`, or `@Nullable Boolean`.  
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable lists.

#### Core logic
- Builder initializes all fields to `AiGenerationConfig.DEFAULT_*`.  
- Each setter assigns the provided value to the corresponding field.  
- `build()` creates a new `LlamaCppJniConfig` by copying all builder fields.  
- The private constructor assigns each field from the builder; no further validation or normalization.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building with required model path.  
- `String modelPath() -> String` – get GGUF model file path.  
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
- `int cpuMoeLayers() -> int` – CPU MoE layers.  
- `int cpuFfnLayers() -> int` – CPU FFN layers.  
- `int kvUnifiedPerSlot() -> int` – per‑slot unified KV cap.  
- `String lazyMode() -> String` – tensor‑read laziness.  
- `int repeatLastN() -> int` – repeat‑penalty window.  
- `String cacheTypeK() -> String` – KV‑cache data type for K.  
- `String cacheTypeV() -> String` – KV‑cache data type for V.  
- `boolean flashAttn() -> boolean` – Flash Attention enabled.  
- `int batchSize() -> int` – logical batch size.  
- `int ubatchSize() -> int` – physical batch size.  
- `int threadsBatch() -> int` – batch/prompt‑processing threads.  
- `int mainGpu() -> int` – primary GPU index.  
- `String devices() -> String` – device selection.  
- `String provider() -> String` – AI provider name.  
- `String remoteModel() -> String` – remote model ID.  
- `String baseUrl() -> String` – OpenAI‑compatible base URL.  
- `String apiKeyEnv() -> String` – environment variable for API key.  
- `String reasoningEffort() -> String` – reasoning‑effort value.  
- `int reasoningBudgetTokens() -> int` – reasoning token budget.  
- `float dryMultiplier() -> float` – DRY multiplier.  
- `float dryBase() -> float` – DRY base.  
- `int dryAllowedLength() -> int` – DRY allowed length.  
- `int dryPenaltyLastN() -> int` – DRY penalty look‑back.  
- `List<String> drySequenceBreakers() -> List<String>` – DRY sequence breakers.  
- `List<String> stopStrings() -> List<String>` – stop strings.  
- `Builder contextSize(int) -> Builder` – set context size.  
- `Builder maxOutputTokens(int) -> Builder` – set max output tokens.  
- `Builder temperature(float) -> Builder` – set temperature.  
- `Builder threads(int) -> Builder` – set CPU threads.  
- `Builder topP(float) -> Builder` – set nucleus‑sampling threshold.  
- `Builder topK(int) -> Builder` – set top‑k limit.  
- `Builder minP(float) -> Builder` – set min‑p threshold.  
- `Builder topNSigma(float) -> Builder` – set top‑n‑sigma threshold.  
- `Builder repeatPenalty(float) -> Builder` – set repetition penalty.  
- `Builder chatTemplateEnableThinking(@Nullable Boolean) -> Builder` – set chat‑template flag.  
- `Builder cachePrompt(boolean) -> Builder` – set prompt caching.  
- `Builder swaFull(boolean) -> Builder` – set full‑size SWA cache.  
- `Builder cacheReuse(int) -> Builder` – set KV prefix‑reuse chunk size.  
- `Builder gpuLayers(int) -> Builder` – set GPU layers.  
- `Builder seed(int) -> Builder` – set RNG seed.  
- `Builder cpuMoeLayers(int) -> Builder` – set CPU MoE layers.  
- `Builder cpuFfnLayers(int) -> Builder` – set CPU FFN layers.  
- `Builder kvUnifiedPerSlot(int) -> Builder` – set per‑slot KV cap.  
- `Builder lazyMode(String) -> Builder` – set tensor‑read laziness.  
- `Builder repeatLastN(int) -> Builder` – set repeat‑penalty window.  
- `Builder cacheTypeK(String) -> Builder` – set KV‑cache type K.  
- `Builder cacheTypeV(String) -> Builder` – set KV‑cache type V.  
- `Builder flashAttn(boolean) -> Builder` – set Flash Attention.  
- `Builder batchSize(int) -> Builder` – set logical batch size.  
- `Builder ubatchSize(int) -> Builder` – set physical batch size.  
- `Builder threadsBatch(int) -> Builder` – set batch threads.  
- `Builder mainGpu(int) -> Builder` – set primary GPU.  
- `Builder devices(String) -> Builder` – set device selection.  
- `Builder provider(String) -> Builder` – set AI provider.  
- `Builder remoteModel(String) -> Builder` – set remote model ID.  
- `Builder baseUrl(String) -> Builder` – set base URL.  
- `Builder apiKeyEnv(String) -> Builder` – set API key env var.  
- `Builder reasoningEffort(String) -> Builder` – set reasoning effort.  
- `Builder reasoningBudgetTokens(int) -> Builder` – set reasoning budget.  
- `Builder dryMultiplier(float) -> Builder` – set DRY multiplier.  
- `Builder dryBase(float) -> Builder` – set DRY base.  
- `Builder dryAllowedLength(int) -> Builder` – set DRY allowed length.  
- `Builder dryPenaltyLastN(int) -> Builder` – set DRY penalty look‑back.  
- `Builder drySequenceBreakers(List<String>) -> Builder` – set DRY sequence breakers.  
- `Builder stopStrings(List<String>) -> Builder` – set stop strings.  
- `LlamaCppJniConfig build() -> LlamaCppJniConfig` – create immutable config.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`  
- `lombok.EqualsAndHashCode`, `lombok.ToString`  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`  
- `net.ladenthin.srcmorph.support.ConvertToRecord`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `NullPointerException` thrown by `builder(String)` if `modelPath` is null.  
- `Objects.requireNonNull` used in builder constructor.

#### Concurrency
- Immutable after construction; thread‑safe.  
- Builder is not thread‑safe; intended for single‑thread use.

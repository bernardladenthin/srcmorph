### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:50Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all runtime knobs and defaults.

#### Purpose
- Stores all llama.cpp JNI configuration options.
- Provides immutable, value‑semantics accessors.
- Supplies a fluent builder that applies `AiGenerationConfig` defaults.

#### Type
`public final class LlamaCppJniConfig`  
final class, immutable, Lombok `@EqualsAndHashCode`, `@ToString`, annotated `@ConvertToRecord`. No inheritance.

#### Input
- `Builder` instance with all fields set (via setters or defaults).
- `modelPath` mandatory constructor argument.

#### Output
- Immutable `LlamaCppJniConfig` instance.
- Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Private constructor copies all builder fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- Each getter returns the corresponding field.
- `drySequenceBreakers()` / `stopStrings()` wrap lists with `Collections.unmodifiableList`.
- `Builder.build()` returns a new `LlamaCppJniConfig`.

#### Public API
- `static Builder builder(String modelPath) -> Builder` – start building with defaults.
- `String modelPath()` – GGUF model file path.
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
- `String provider()` – AI provider identifier.
- `String remoteModel()` – remote model ID for OpenAI‑compatible provider.
- `String baseUrl()` – OpenAI‑compatible API base URL.
- `String apiKeyEnv()` – environment variable name for API key.
- `String reasoningEffort()` – reasoning‑effort keyword value.
- `int reasoningBudgetTokens()` – reasoning token budget.
- `float dryMultiplier()` – DRY sampling multiplier.
- `float dryBase()` – DRY base.
- `int dryAllowedLength()` – DRY allowed length.
- `int dryPenaltyLastN()` – DRY penalty look‑back window.
- `List<String> drySequenceBreakers()` – unmodifiable DRY sequence breakers.
- `List<String> stopStrings()` – unmodifiable stop strings.
- `static final class Builder` – fluent builder.
  - `Builder contextSize(int)` → Builder
  - `Builder maxOutputTokens(int)` → Builder
  - `Builder temperature(float)` → Builder
  - `Builder threads(int)` → Builder
  - `Builder topP(float)` → Builder
  - `Builder topK(int)` → Builder
  - `Builder minP(float)` → Builder
  - `Builder topNSigma(float)` → Builder
  - `Builder repeatPenalty(float)` → Builder
  - `Builder chatTemplateEnableThinking(@Nullable Boolean)` → Builder
  - `Builder cachePrompt(boolean)` → Builder
  - `Builder swaFull(boolean)` → Builder
  - `Builder cacheReuse(int)` → Builder
  - `Builder gpuLayers(int)` → Builder
  - `Builder seed(int)` → Builder
  - `Builder cpuMoeLayers(int)` → Builder
  - `Builder cpuFfnLayers(int)` → Builder
  - `Builder kvUnifiedPerSlot(int)` → Builder
  - `Builder lazyMode(String)` → Builder
  - `Builder repeatLastN(int)` → Builder
  - `Builder cacheTypeK(String)` → Builder
  - `Builder cacheTypeV(String)` → Builder
  - `Builder flashAttn(boolean)` → Builder
  - `Builder batchSize(int)` → Builder
  - `Builder ubatchSize(int)` → Builder
  - `Builder threadsBatch(int)` → Builder
  - `Builder mainGpu(int)` → Builder
  - `Builder devices(String)` → Builder
  - `Builder provider(String)` → Builder
  - `Builder remoteModel(String)` → Builder
  - `Builder baseUrl(String)` → Builder
  - `Builder apiKeyEnv(String)` → Builder
  - `Builder reasoningEffort(String)` → Builder
  - `Builder reasoningBudgetTokens(int)` → Builder
  - `Builder dryMultiplier(float)` → Builder
  - `Builder dryBase(float)` → Builder
  - `Builder dryAllowedLength(int)` → Builder
  - `Builder dryPenaltyLastN(int)` → Builder
  - `Builder drySequenceBreakers(List<String>)` → Builder
  - `Builder stopStrings(List<String>)` → Builder
  - `LlamaCppJniConfig build()` → LlamaCppJniConfig

#### Dependencies
- `java.util.*` (Collections, List, Objects)
- `lombok.*` (`@EqualsAndHashCode`, `@ToString`)
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` setters accept any value; no validation beyond null checks for optional strings.

#### Concurrency
- Immutable after construction; thread‑safe read access.
- Builder is not thread‑safe; intended single‑thread usage.

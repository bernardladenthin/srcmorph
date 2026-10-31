### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:13:12Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, built via a fluent builder with defaults from AiGenerationConfig.

#### Purpose
- Encapsulates all llama.cpp JNI parameters.
- Provides read‑only accessors and immutable state.

#### Type
- final class `LlamaCppJniConfig`; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `Builder` instance with 37 fields, each initialized to `AiGenerationConfig.DEFAULT_*` or supplied by caller.
- `modelPath` required, non‑null.

#### Output
- Immutable `LlamaCppJniConfig` instance exposing all configuration values via record‑style getters.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Private constructor copies all builder fields.
- `builder(String)` creates a new `Builder` with required `modelPath`.
- `Builder` initializes fields to defaults, provides fluent setters, and `build()` returns the immutable config.

#### Public API
- `static Builder builder(String)` → creates builder.
- `String modelPath()` → model file path.
- `int contextSize()` → context window size.
- `int maxOutputTokens()` → max output tokens.
- `float temperature()` → sampling temperature.
- `int threads()` → CPU threads.
- `float topP()` → nucleus threshold.
- `int topK()` → top‑k limit.
- `float minP()` → min‑p threshold.
- `float topNSigma()` → top‑n‑sigma threshold.
- `float repeatPenalty()` → repetition penalty.
- `@Nullable Boolean chatTemplateEnableThinking()` → chat‑template flag.
- `boolean cachePrompt()` → prompt caching.
- `boolean swaFull()` → full SWA cache.
- `int cacheReuse()` → KV prefix reuse size.
- `int gpuLayers()` → GPU offload layers.
- `int seed()` → RNG seed.
- `int cpuMoeLayers()` → CPU MoE layers.
- `int cpuFfnLayers()` → CPU FFN layers.
- `int kvUnifiedPerSlot()` → KV unified cap.
- `String lazyMode()` → tensor‑read laziness.
- `int repeatLastN()` → repeat‑penalty window.
- `String cacheTypeK()` → KV cache type K.
- `String cacheTypeV()` → KV cache type V.
- `boolean flashAttn()` → Flash Attention.
- `int batchSize()` → logical batch size.
- `int ubatchSize()` → physical batch size.
- `int threadsBatch()` → batch thread count.
- `int mainGpu()` → primary GPU index.
- `String devices()` → device list.
- `String provider()` → AI provider.
- `String remoteModel()` → remote model ID.
- `String baseUrl()` → OpenAI‑compatible base URL.
- `String apiKeyEnv()` → API key env var.
- `String reasoningEffort()` → reasoning effort.
- `int reasoningBudgetTokens()` → reasoning budget.
- `float dryMultiplier()` → DRY multiplier.
- `float dryBase()` → DRY base.
- `int dryAllowedLength()` → DRY allowed length.
- `int dryPenaltyLastN()` → DRY penalty window.
- `List<String> drySequenceBreakers()` → unmodifiable breakers.
- `List<String> stopStrings()` → unmodifiable stop strings.

#### Dependencies
- `AiGenerationConfig` for default constants.
- `ConvertToRecord`, `EqualsAndHashCode`, `ToString` from Lombok.
- `Objects`, `Collections`, `List`, `Nullable`.

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `lazyMode`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort` accept null to restore defaults.

#### Concurrency
- Immutable after construction; thread‑safe.
- Builder is not thread‑safe; intended single‑thread usage.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:31Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures local llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop behavior.

#### Purpose
- `LlamaCppJniConfig` holds immutable model-generation settings initialized from `AiGenerationConfig` defaults.
- `Builder` provides fluent configuration, with setters for model, sampling, runtime, provider, reasoning, and stopping options.

#### Type
- `LlamaCppJniConfig` — final class; annotated `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`; 41 final configuration fields.
- `Builder` — public static final class; nested in `LlamaCppJniConfig`; mutable fluent builder for the configuration.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- `Builder` setter parameters configure 40 remaining values: sampling, threads, cache/KV, GPU/CPU placement, batch sizes, provider/API settings, reasoning and DRY sampling, sequence breakers, and stop strings.
- All builder settings except `modelPath` start with corresponding `AiGenerationConfig.DEFAULT_*` values; list settings start empty.
- Nullable inputs: `chatTemplateEnableThinking`, and nullable strings/lists accepted by selected setters.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig` populated from the builder.
- Accessors expose all 41 settings; list accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` across configuration fields.

#### Core logic
- `builder(modelPath)` creates a builder and rejects a null path with `Objects.requireNonNull`.
- Builder setters replace their setting and return the same builder; nullable string setters restore the corresponding default, while null list setters restore empty lists.
- `build()` copies every builder value into the configuration; no further normalization or validation occurs.
- `chatTemplateEnableThinking == null` represents an unset option; provider can omit the chat-template kwarg.
- Empty strings and sentinel values (for example, `-1` or `0`) represent provider/binding defaults or disabled options as documented by accessors.

#### Public API
- `builder(String)` -> `Builder` — start with model path.
- `modelPath()` -> `String` — GGUF model path; `contextSize()` -> `int` — context window; `maxOutputTokens()` -> `int` — output token limit.
- `temperature()` -> `float` — sampling temperature; `threads()` -> `int` — CPU threads; `topP()` -> `float` — nucleus threshold; `topK()` -> `int` — top-k limit.
- `minP()` -> `float` — min-p threshold; `topNSigma()` -> `float` — top-n-sigma threshold; `repeatPenalty()` -> `float` — repetition penalty.
- `chatTemplateEnableThinking()` -> `@Nullable Boolean` — thinking mode; `cachePrompt()` -> `boolean` — prompt caching; `swaFull()` -> `boolean` — full SWA KV cache; `cacheReuse()` -> `int` — KV prefix reuse.
- `gpuLayers()` -> `int` — GPU offload layers; `seed()` -> `int` — generation seed; `cpuMoeLayers()` -> `int` — CPU MoE layers; `cpuFfnLayers()` -> `int` — CPU FFN layers; `kvUnifiedPerSlot()` -> `int` — per-slot KV cap.
- `lazyMode()` -> `String` — tensor-read laziness; `repeatLastN()` -> `int` — repetition window; `cacheTypeK()` -> `String` — K-cache type; `cacheTypeV()` -> `String` — V-cache type; `flashAttn()` -> `boolean` — Flash Attention.
- `batchSize()` -> `int` — logical batch; `ubatchSize()` -> `int` — physical batch; `threadsBatch()` -> `int` — batch threads; `mainGpu()` -> `int` — primary GPU; `devices()` -> `String` — device selection.
- `provider()` -> `String` — AI provider; `remoteModel()` -> `String` — remote model ID; `baseUrl()` -> `String` — API base URL; `apiKeyEnv()` -> `String` — API-key environment-variable name.
- `reasoningEffort()` -> `String` — reasoning effort; `reasoningBudgetTokens()` -> `int` — reasoning token budget.
- `dryMultiplier()` -> `float` — DRY multiplier; `dryBase()` -> `float` — DRY base; `dryAllowedLength()` -> `int` — DRY allowed length; `dryPenaltyLastN()` -> `int` — DRY look-back window.
- `drySequenceBreakers()` -> `List<String>` — DRY sequence breakers; `stopStrings()` -> `List<String>` — stop strings.
- `Builder.contextSize(int)` -> `Builder` — set context window; `maxOutputTokens(int)` -> `Builder` — set output token limit; `temperature(float)` -> `Builder` — set temperature; `threads(int)` -> `Builder` — set CPU threads.
- `Builder.topP(float)` -> `Builder` — set nucleus threshold; `topK(int)` -> `Builder` — set top-k limit; `minP(float)` -> `Builder` — set min-p threshold; `topNSigma(float)` -> `Builder` — set top-n-sigma threshold; `repeatPenalty(float)` -> `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` -> `Builder` — set thinking mode; `cachePrompt(boolean)` -> `Builder` — set prompt caching; `swaFull(boolean)` -> `Builder` — set full SWA KV cache; `cacheReuse(int)` -> `Builder` — set KV prefix reuse.
- `Builder.gpuLayers(int)` -> `Builder` — set GPU offload; `seed(int)` -> `Builder` — set RNG seed; `cpuMoeLayers(int)` -> `Builder` — set CPU MoE layers; `cpuFfnLayers(int)` -> `Builder` — set CPU FFN layers; `kvUnifiedPerSlot(int)` -> `Builder` — set per-slot KV cap.
- `Builder.lazyMode(String)` -> `Builder` — set tensor-read laziness; `repeatLastN(int)` -> `Builder` — set repetition window; `cacheTypeK(String)` -> `Builder` — set K-cache type; `cacheTypeV(String)` -> `Builder` — set V-cache type; `flashAttn(boolean)` -> `Builder` — set Flash Attention.
- `Builder.batchSize(int)` -> `Builder` — set logical batch; `ubatchSize(int)` -> `Builder` — set physical batch; `threadsBatch(int)` -> `Builder` — set batch threads; `mainGpu(int)` -> `Builder` — set primary GPU; `devices(String)` -> `Builder` — set device selection.
- `Builder.provider(String)` -> `Builder` — set AI provider; `remoteModel(String)` -> `Builder` — set remote model; `baseUrl(String)` -> `Builder` — set API base URL; `apiKeyEnv(String)` -> `Builder` — set API-key environment variable.
- `Builder.reasoningEffort(String)` -> `Builder` — set reasoning effort; `reasoningBudgetTokens(int)` -> `Builder` — set reasoning budget.
- `Builder.dryMultiplier(float)` -> `Builder` — set DRY multiplier; `dryBase(float)` -> `Builder` — set DRY base; `dryAllowedLength(int)` -> `Builder` — set DRY allowed length; `dryPenaltyLastN(int)` -> `Builder` — set DRY look-back.
- `Builder.drySequenceBreakers(List<String>)` -> `Builder` — set DRY breakers; `stopStrings(List<String>)` -> `Builder` — set stop strings; `build()` -> `LlamaCppJniConfig` — create configuration.

#### Dependencies
- `AiGenerationConfig`
- `ConvertToRecord`
- Lombok `EqualsAndHashCode`, `ToString`
- `Nullable`
- `Collections`, `List`, `Objects`

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- No range or semantic validation is performed by setters or `build()`.
- Nullable thinking mode is preserved; null selected string setters restore defaults, and null list setters produce empty lists.

#### Concurrency
- `LlamaCppJniConfig` is immutable; list accessors expose unmodifiable views.
- `Builder` is mutable and explicitly not thread-safe.

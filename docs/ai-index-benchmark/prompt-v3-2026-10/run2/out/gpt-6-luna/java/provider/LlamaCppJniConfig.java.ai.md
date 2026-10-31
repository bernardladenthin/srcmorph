### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:36Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI or OpenAI-compatible generation, including sampling, model placement, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` holds model and generation settings with defaults from `AiGenerationConfig`.
- `Builder` provides named fluent configuration; built configs expose immutable state.

#### Type
- `LlamaCppJniConfig` — `final` class; `@ConvertToRecord`, Lombok `@ToString` and `@EqualsAndHashCode`.
- Nested `public static final class Builder`; config constructor is private.
- 41 final configuration fields; `chatTemplateEnableThinking` is nullable.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Optional builder setters accept sampling, threading, cache, GPU/device, provider/API, reasoning, DRY, and stop settings.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- `build()` returns a `LlamaCppJniConfig` populated from the builder.
- Accessors expose scalar settings and unmodifiable views of DRY sequence breakers and stop strings.
- Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Core logic
- Builder initializes each setting to its corresponding `AiGenerationConfig` default.
- Fluent setters replace builder values; nullable string/list setters restore defaults when passed `null`.
- `lazyMode`, cache type, device, provider, remote model, URL, API-key environment variable, and reasoning-effort setters normalize `null` to defaults.
- List setters replace `null` with empty lists; config getters wrap stored lists with `Collections.unmodifiableList`.
- `build()` copies all builder fields into the immutable config; constructor performs no normalization.
- Null `modelPath` fails immediately through `Objects.requireNonNull`.

#### Public API
- `builder(String)` → `Builder` — start with model path and defaults.
- `modelPath()` → `String` — return GGUF model path.
- `contextSize()` → `int` — return context window.
- `maxOutputTokens()` → `int` — return output token limit.
- `temperature()` → `float` — return sampling temperature.
- `threads()` → `int` — return CPU thread count.
- `topP()` → `float` — return nucleus threshold.
- `topK()` → `int` — return top-k limit.
- `minP()` → `float` — return min-p threshold.
- `topNSigma()` → `float` — return top-n-sigma threshold.
- `repeatPenalty()` → `float` — return repetition penalty.
- `chatTemplateEnableThinking()` → `@Nullable Boolean` — return thinking-mode override.
- `cachePrompt()` → `boolean` — return prompt-cache setting.
- `swaFull()` → `boolean` — return full-SWA-cache setting.
- `cacheReuse()` → `int` — return KV prefix-reuse chunk size.
- `gpuLayers()` → `int` — return GPU layer count.
- `seed()` → `int` — return generation seed.
- `cpuMoeLayers()` → `int` — return CPU MoE layer count.
- `cpuFfnLayers()` → `int` — return CPU FFN layer count.
- `kvUnifiedPerSlot()` → `int` — return per-slot KV cap.
- `lazyMode()` → `String` — return tensor-read laziness.
- `repeatLastN()` → `int` — return repeat-penalty window.
- `cacheTypeK()` → `String` — return K cache type.
- `cacheTypeV()` → `String` — return V cache type.
- `flashAttn()` → `boolean` — return Flash Attention setting.
- `batchSize()` → `int` — return logical batch size.
- `ubatchSize()` → `int` — return physical batch size.
- `threadsBatch()` → `int` — return batch-processing thread count.
- `mainGpu()` → `int` — return primary GPU index.
- `devices()` → `String` — return device selection.
- `provider()` → `String` — return provider selection.
- `remoteModel()` → `String` — return remote model identifier.
- `baseUrl()` → `String` — return API base URL.
- `apiKeyEnv()` → `String` — return API-key environment variable name.
- `reasoningEffort()` → `String` — return reasoning-effort value.
- `reasoningBudgetTokens()` → `int` — return reasoning token budget.
- `dryMultiplier()` → `float` — return DRY multiplier.
- `dryBase()` → `float` — return DRY base.
- `dryAllowedLength()` → `int` — return DRY allowed length.
- `dryPenaltyLastN()` → `int` — return DRY look-back window.
- `drySequenceBreakers()` → `List<String>` — return unmodifiable DRY breakers.
- `stopStrings()` → `List<String>` — return unmodifiable stop strings.
- `Builder.contextSize(int)` → `Builder` — set context window.
- `Builder.maxOutputTokens(int)` → `Builder` — set output token limit.
- `Builder.temperature(float)` → `Builder` — set sampling temperature.
- `Builder.threads(int)` → `Builder` — set CPU thread count.
- `Builder.topP(float)` → `Builder` — set nucleus threshold.
- `Builder.topK(int)` → `Builder` — set top-k limit.
- `Builder.minP(float)` → `Builder` — set min-p threshold.
- `Builder.topNSigma(float)` → `Builder` — set top-n-sigma threshold.
- `Builder.repeatPenalty(float)` → `Builder` — set repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean)` → `Builder` — set thinking override.
- `Builder.cachePrompt(boolean)` → `Builder` — set prompt caching.
- `Builder.swaFull(boolean)` → `Builder` — set full-SWA caching.
- `Builder.cacheReuse(int)` → `Builder` — set KV reuse chunk size.
- `Builder.gpuLayers(int)` → `Builder` — set GPU layer count.
- `Builder.seed(int)` → `Builder` — set generation seed.
- `Builder.cpuMoeLayers(int)` → `Builder` — set CPU MoE layers.
- `Builder.cpuFfnLayers(int)` → `Builder` — set CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int)` → `Builder` — set per-slot KV cap.
- `Builder.lazyMode(String)` → `Builder` — set tensor-read laziness.
- `Builder.repeatLastN(int)` → `Builder` — set repeat-penalty window.
- `Builder.cacheTypeK(String)` → `Builder` — set K cache type.
- `Builder.cacheTypeV(String)` → `Builder` — set V cache type.
- `Builder.flashAttn(boolean)` → `Builder` — set Flash Attention.
- `Builder.batchSize(int)` → `Builder` — set logical batch size.
- `Builder.ubatchSize(int)` → `Builder` — set physical batch size.
- `Builder.threadsBatch(int)` → `Builder` — set batch thread count.
- `Builder.mainGpu(int)` → `Builder` — set primary GPU.
- `Builder.devices(String)` → `Builder` — set device selection.
- `Builder.provider(String)` → `Builder` — set provider selection.
- `Builder.remoteModel(String)` → `Builder` — set remote model identifier.
- `Builder.baseUrl(String)` → `Builder` — set API base URL.
- `Builder.apiKeyEnv(String)` → `Builder` — set API-key environment variable.
- `Builder.reasoningEffort(String)` → `Builder` — set reasoning effort.
- `Builder.reasoningBudgetTokens(int)` → `Builder` — set reasoning budget.
- `Builder.dryMultiplier(float)` → `Builder` — set DRY multiplier.
- `Builder.dryBase(float)` → `Builder` — set DRY base.
- `Builder.dryAllowedLength(int)` → `Builder` — set DRY allowed length.
- `Builder.dryPenaltyLastN(int)` → `Builder` — set DRY look-back window.
- `Builder.drySequenceBreakers(List<String>)` → `Builder` — set DRY breakers.
- `Builder.stopStrings(List<String>)` → `Builder` — set stop strings.
- `Builder.build()` → `LlamaCppJniConfig` — create immutable config.

#### Dependencies
- `AiGenerationConfig` — defaults for generation settings.
- `Collections`, `List`, `Objects` — list handling and null validation.
- Lombok `@EqualsAndHashCode`, `@ToString`; `@ConvertToRecord`; JSpecify `@Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- Nullable string/list builder inputs restore defaults; nullable thinking mode is retained.

#### Concurrency
- `LlamaCppJniConfig` is immutable; list getters return unmodifiable views.
- `Builder` is explicitly not thread-safe.

### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:48:05Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures llama.cpp JNI model loading, sampling, hardware, caching, and compatible remote-provider options for AI generation.

#### Purpose
- `LlamaCppJniConfig` holds immutable, defaulted generation settings for a GGUF model.
- `Builder` provides named fluent setters to customize those settings.

#### Type
- `LlamaCppJniConfig` — `public final class`; annotated `@ConvertToRecord`, Lombok `@ToString`, and `@EqualsAndHashCode`.
- `Builder` — `public static final class`; nested in `LlamaCppJniConfig`.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- Builder setters accept sampling, context, threading, cache, GPU/device, provider, reasoning, DRY, and stop-string options.
- Builder fields default to `AiGenerationConfig.DEFAULT_*`; list options default to empty lists.
- Private `LlamaCppJniConfig(Builder builder)` reads all builder fields.

#### Output
- Immutable config with 41 values and record-style accessors.
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString`.

#### Core logic
- `builder(String)` creates a builder; its constructor checks `modelPath` with `Objects.requireNonNull(modelPath, "modelPath")`.
- Builder setters store supplied values and return `this`; nullable string options restore their corresponding `AiGenerationConfig.DEFAULT_*` values, while nullable list options become `Collections.<String>emptyList()`.
- `build()` constructs `LlamaCppJniConfig(this)`, copying all 41 builder values without further normalization.
- `chatTemplateEnableThinking` preserves nullable `Boolean`; unset can represent omission of the chat-template kwarg.
- List accessors wrap the stored lists with `Collections.unmodifiableList(...)`.
- `@EqualsAndHashCode` applies value semantics across fields; source documentation notes exact-bit float comparison.

#### Public API
- `builder(String) -> Builder` — start with model and defaults.
- `modelPath() -> String` — model file path.
- `contextSize() -> int` — context window.
- `maxOutputTokens() -> int` — output token limit.
- `temperature() -> float` — sampling temperature.
- `threads() -> int` — CPU thread count.
- `topP() -> float` — nucleus threshold.
- `topK() -> int` — top-k limit.
- `minP() -> float` — min-p threshold.
- `topNSigma() -> float` — top-n-sigma threshold.
- `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — chat-template thinking mode.
- `cachePrompt() -> boolean` — prompt-prefix caching.
- `swaFull() -> boolean` — full-size SWA KV cache.
- `cacheReuse() -> int` — KV prefix-reuse chunk size.
- `gpuLayers() -> int` — GPU offload layers.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repetition-penalty window.
- `cacheTypeK() -> String` — K cache data type.
- `cacheTypeV() -> String` — V cache data type.
- `flashAttn() -> boolean` — Flash Attention setting.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — AI provider.
- `remoteModel() -> String` — remote model ID.
- `baseUrl() -> String` — compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning-effort value.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY sampling multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY look-back window.
- `drySequenceBreakers() -> List<String>` — DRY sequence breakers.
- `stopStrings() -> List<String>` — generation stop strings.
- `Builder.contextSize(int) -> Builder`, `maxOutputTokens(int) -> Builder`, `temperature(float) -> Builder`, `threads(int) -> Builder`, `topP(float) -> Builder`, `topK(int) -> Builder`, `minP(float) -> Builder`, `topNSigma(float) -> Builder`, `repeatPenalty(float) -> Builder` — set generation sampling and limits.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set nullable thinking mode.
- `Builder.cachePrompt(boolean) -> Builder`, `swaFull(boolean) -> Builder`, `cacheReuse(int) -> Builder` — set prompt and KV caching.
- `Builder.gpuLayers(int) -> Builder`, `seed(int) -> Builder`, `cpuMoeLayers(int) -> Builder`, `cpuFfnLayers(int) -> Builder`, `kvUnifiedPerSlot(int) -> Builder`, `lazyMode(String) -> Builder`, `repeatLastN(int) -> Builder`, `cacheTypeK(String) -> Builder`, `cacheTypeV(String) -> Builder`, `flashAttn(boolean) -> Builder`, `batchSize(int) -> Builder`, `ubatchSize(int) -> Builder`, `threadsBatch(int) -> Builder`, `mainGpu(int) -> Builder`, `devices(String) -> Builder` — set runtime and hardware options.
- `Builder.provider(String) -> Builder`, `remoteModel(String) -> Builder`, `baseUrl(String) -> Builder`, `apiKeyEnv(String) -> Builder`, `reasoningEffort(String) -> Builder`, `reasoningBudgetTokens(int) -> Builder` — set provider and reasoning options.
- `Builder.dryMultiplier(float) -> Builder`, `dryBase(float) -> Builder`, `dryAllowedLength(int) -> Builder`, `dryPenaltyLastN(int) -> Builder`, `drySequenceBreakers(List<String>) -> Builder` — set DRY sampling options.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings.
- `Builder.build() -> LlamaCppJniConfig` — create immutable config.

#### Dependencies
- `AiGenerationConfig`
- `ConvertToRecord`
- `Nullable`
- Lombok `EqualsAndHashCode`, `ToString`
- `Collections`, `List`, `Objects`

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`.
- `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, and `reasoningEffort` replace `null` with their `AiGenerationConfig.DEFAULT_*`.
- `drySequenceBreakers(null)` and `stopStrings(null)` become empty lists.
- `chatTemplateEnableThinking` accepts `null`.

#### Concurrency
- `LlamaCppJniConfig` is immutable; builder is documented as not thread-safe.

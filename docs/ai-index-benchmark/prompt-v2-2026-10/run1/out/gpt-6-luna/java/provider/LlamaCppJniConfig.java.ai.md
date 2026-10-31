### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:49Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI and compatible AI generation with model, sampling, hardware, caching, reasoning, and stopping options.

#### Purpose
- `LlamaCppJniConfig` holds immutable per-model generation settings, with defaults sourced from `AiGenerationConfig`.
- Nested `Builder` provides named fluent configuration instead of a long positional constructor.

#### Type
- `LlamaCppJniConfig` — `public final class`; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- `Builder` — `public static final class`; mutable fluent builder.
- Configuration comprises 41 final fields; `chatTemplateEnableThinking` is nullable. Lombok generates value-based `equals`, `hashCode`, and `toString`.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept model context and sampling parameters; CPU/GPU, batching, KV-cache, and device options; provider/API settings; reasoning and DRY settings; and DRY sequence breakers and stop strings.
- Every option except `modelPath` defaults from `AiGenerationConfig.DEFAULT_*`; lists default to empty.

#### Output
- `build()` returns an immutable `LlamaCppJniConfig`.
- Accessors return configured values; list accessors return unmodifiable views.
- No files, streams, or external services are read or written.

#### Core logic
- `Builder` initializes options to `AiGenerationConfig` defaults; the model path is checked with `Objects.requireNonNull`.
- Fluent setters replace individual options. String setters for nullable-default options restore the corresponding default when passed `null`; list setters replace `null` with an empty list.
- `build()` passes the builder to the private constructor, which copies all 41 values without further normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`.
- `@EqualsAndHashCode` and `@ToString` provide Lombok-generated value semantics over the fields.

#### Public API
- `builder(String) -> Builder` — start configuration with defaults.
- Accessors: `modelPath() -> String` — model path; `contextSize() -> int` — context window; `maxOutputTokens() -> int` — output limit; `temperature() -> float` — sampling temperature; `threads() -> int` — CPU threads; `topP() -> float` — nucleus threshold; `topK() -> int` — top-k limit; `minP() -> float` — min-p threshold; `topNSigma() -> float` — top-n-sigma threshold; `repeatPenalty() -> float` — repetition penalty; `chatTemplateEnableThinking() -> @Nullable Boolean` — thinking mode; `cachePrompt() -> boolean` — prompt caching; `swaFull() -> boolean` — full SWA KV cache; `cacheReuse() -> int` — KV prefix reuse; `gpuLayers() -> int` — GPU offload layers; `seed() -> int` — generation seed; `cpuMoeLayers() -> int` — CPU MoE layers; `cpuFfnLayers() -> int` — CPU FFN layers; `kvUnifiedPerSlot() -> int` — per-slot KV cap; `lazyMode() -> String` — tensor-read laziness; `repeatLastN() -> int` — repetition window; `cacheTypeK() -> String` — K-cache type; `cacheTypeV() -> String` — V-cache type; `flashAttn() -> boolean` — Flash Attention; `batchSize() -> int` — logical batch size; `ubatchSize() -> int` — physical batch size; `threadsBatch() -> int` — batch threads; `mainGpu() -> int` — primary GPU; `devices() -> String` — device selection; `provider() -> String` — provider name; `remoteModel() -> String` — remote model ID; `baseUrl() -> String` — compatible API base URL; `apiKeyEnv() -> String` — API-key environment variable name; `reasoningEffort() -> String` — reasoning effort; `reasoningBudgetTokens() -> int` — reasoning budget; `dryMultiplier() -> float` — DRY multiplier; `dryBase() -> float` — DRY base; `dryAllowedLength() -> int` — DRY allowed length; `dryPenaltyLastN() -> int` — DRY look-back window; `drySequenceBreakers() -> List<String>` — DRY breakers; `stopStrings() -> List<String>` — stop strings.
- `Builder` setters: `contextSize(int) -> Builder`, `maxOutputTokens(int) -> Builder`, `temperature(float) -> Builder`, `threads(int) -> Builder`, `topP(float) -> Builder`, `topK(int) -> Builder`, `minP(float) -> Builder`, `topNSigma(float) -> Builder`, `repeatPenalty(float) -> Builder`, `chatTemplateEnableThinking(@Nullable Boolean) -> Builder`, `cachePrompt(boolean) -> Builder`, `swaFull(boolean) -> Builder`, `cacheReuse(int) -> Builder`, `gpuLayers(int) -> Builder`, `seed(int) -> Builder`, `cpuMoeLayers(int) -> Builder`, `cpuFfnLayers(int) -> Builder`, `kvUnifiedPerSlot(int) -> Builder`, `lazyMode(String) -> Builder`, `repeatLastN(int) -> Builder`, `cacheTypeK(String) -> Builder`, `cacheTypeV(String) -> Builder`, `flashAttn(boolean) -> Builder`, `batchSize(int) -> Builder`, `ubatchSize(int) -> Builder`, `threadsBatch(int) -> Builder`, `mainGpu(int) -> Builder`, `devices(String) -> Builder`, `provider(String) -> Builder`, `remoteModel(String) -> Builder`, `baseUrl(String) -> Builder`, `apiKeyEnv(String) -> Builder`, `reasoningEffort(String) -> Builder`, `reasoningBudgetTokens(int) -> Builder`, `dryMultiplier(float) -> Builder`, `dryBase(float) -> Builder`, `dryAllowedLength(int) -> Builder`, `dryPenaltyLastN(int) -> Builder`, `drySequenceBreakers(List<String>) -> Builder`, `stopStrings(List<String>) -> Builder` — set option and chain.
- `Builder.build() -> LlamaCppJniConfig` — create immutable configuration.
- Lombok-generated `equals(Object) -> boolean`, `hashCode() -> int`, `toString() -> String` — value semantics and representation.

#### Dependencies
- `AiGenerationConfig` — defaults for generation options.
- `ConvertToRecord` — record-migration marker.
- `Collections`, `List`, `Objects` — list defaults/views and null check.
- Lombok `@EqualsAndHashCode`, `@ToString`; JSpecify `@Nullable`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `"modelPath"`.
- Other setters accept `null` only where explicitly normalized (nullable thinking mode, strings with fallback defaults, and lists); no range validation is performed.

#### Concurrency
- Built `LlamaCppJniConfig` has final fields and is documented as immutable.
- `Builder` is mutable and explicitly not thread-safe; list inputs are stored directly, while accessors expose unmodifiable views.

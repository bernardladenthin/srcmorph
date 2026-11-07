### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:29:07Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures local llama.cpp and OpenAI-compatible model generation, including sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- Carries model path and generation/provider settings in an immutable configuration.
- Applies `AiGenerationConfig` defaults through a fluent builder.

#### Type
- `public final class LlamaCppJniConfig`; annotated `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder`.
- No superclass or implemented interfaces.

#### Input
- `builder(String modelPath)` requires a non-null GGUF model path.
- Builder setters accept generation sampling, thread, GPU, KV-cache, provider, reasoning, DRY, and stop-string values.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- Immutable configuration with 41 fields and record-style accessors.
- `drySequenceBreakers()` and `stopStrings()` return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over all fields.

#### Core logic
- `LlamaCppJniConfig.builder(modelPath)` creates a builder initialized with defaults.
- Builder setters replace configured values and return the builder; nullable string/list inputs restore defaults when null.
- `build()` copies builder state into the immutable configuration.
- Nullable `chatTemplateEnableThinking` preserves unset state; null list inputs become empty lists.
- Constructor performs no normalization; list accessors wrap the stored lists as unmodifiable views.

#### Public API
- `builder(String) -> Builder` — start configuration.
- `modelPath() -> String` — GGUF path; `contextSize() -> int` — context window; `maxOutputTokens() -> int` — output limit.
- `temperature() -> float` — sampling temperature; `threads() -> int` — CPU threads; `topP() -> float` — nucleus threshold; `topK() -> int` — top-k limit.
- `minP() -> float` — min-p threshold; `topNSigma() -> float` — top-n-sigma threshold; `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — optional thinking mode; `cachePrompt() -> boolean` — prompt caching; `swaFull() -> boolean` — full SWA KV cache.
- `cacheReuse() -> int` — KV prefix reuse; `gpuLayers() -> int` — GPU offload layers; `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers; `cpuFfnLayers() -> int` — CPU FFN layers; `kvUnifiedPerSlot() -> int` — per-slot KV cap.
- `lazyMode() -> String` — tensor-read laziness; `repeatLastN() -> int` — repetition window; `cacheTypeK() -> String` — K-cache type; `cacheTypeV() -> String` — V-cache type.
- `flashAttn() -> boolean` — Flash Attention; `batchSize() -> int` — logical batch size; `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads; `mainGpu() -> int` — primary GPU; `devices() -> String` — device selection.
- `provider() -> String` — provider choice; `remoteModel() -> String` — remote model ID; `baseUrl() -> String` — API base URL; `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning effort; `reasoningBudgetTokens() -> int` — reasoning budget.
- `dryMultiplier() -> float` — DRY multiplier; `dryBase() -> float` — DRY base; `dryAllowedLength() -> int` — DRY allowed length; `dryPenaltyLastN() -> int` — DRY look-back window.
- `drySequenceBreakers() -> List<String>` — DRY sequence breakers; `stopStrings() -> List<String>` — stop strings.
- `Builder.contextSize(int) -> Builder`, `maxOutputTokens(int) -> Builder`, `temperature(float) -> Builder`, `threads(int) -> Builder`, `topP(float) -> Builder`, `topK(int) -> Builder`, `minP(float) -> Builder`, `topNSigma(float) -> Builder`, `repeatPenalty(float) -> Builder` — set generation sampling controls.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set optional thinking mode; `cachePrompt(boolean) -> Builder`, `swaFull(boolean) -> Builder`, `cacheReuse(int) -> Builder` — set cache controls.
- `Builder.gpuLayers(int) -> Builder`, `seed(int) -> Builder`, `cpuMoeLayers(int) -> Builder`, `cpuFfnLayers(int) -> Builder`, `kvUnifiedPerSlot(int) -> Builder` — set hardware and KV controls.
- `Builder.lazyMode(String) -> Builder`, `repeatLastN(int) -> Builder`, `cacheTypeK(String) -> Builder`, `cacheTypeV(String) -> Builder`, `flashAttn(boolean) -> Builder` — set inference controls.
- `Builder.batchSize(int) -> Builder`, `ubatchSize(int) -> Builder`, `threadsBatch(int) -> Builder`, `mainGpu(int) -> Builder`, `devices(String) -> Builder` — set batch and device controls.
- `Builder.provider(String) -> Builder`, `remoteModel(String) -> Builder`, `baseUrl(String) -> Builder`, `apiKeyEnv(String) -> Builder` — set provider connection options.
- `Builder.reasoningEffort(String) -> Builder`, `reasoningBudgetTokens(int) -> Builder` — set reasoning controls.
- `Builder.dryMultiplier(float) -> Builder`, `dryBase(float) -> Builder`, `dryAllowedLength(int) -> Builder`, `dryPenaltyLastN(int) -> Builder`, `drySequenceBreakers(List<String>) -> Builder` — set DRY sampling controls.
- `Builder.stopStrings(List<String>) -> Builder` — set stop strings; `Builder.build() -> LlamaCppJniConfig` — create immutable config.

#### Dependencies
- `AiGenerationConfig`; `ConvertToRecord`; Lombok `EqualsAndHashCode`, `ToString`; JSpecify `Nullable`.
- Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `modelPath`.
- Null string/list setter inputs restore their corresponding defaults; nullable thinking mode remains nullable.
- Numeric values are not validated.

#### Concurrency
- Configuration fields are final; configuration is immutable after construction.
- Builder is not thread-safe and is intended for single-threaded use.

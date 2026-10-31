### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:15:28Z
- G: 
- A: 0.0.0
- X: file
---
> Holds immutable inference settings for running local GGUF models via llama.cpp JNI.

#### Purpose
- Immutable value object carrying all llama.cpp JNI sampling, KV-cache, GPU-offload, remote-provider, reasoning, DRY, and stop-string settings.
- Single-defaults builder entry point backed by `AiGenerationConfig.DEFAULT_*`.

#### Type
- `final class LlamaCppJniConfig` with nested `public static final class Builder`.
- Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok, full value semantics, exact-bit float compare).
- 41 private final fields in `LlamaCppJniConfig`; 41 fields in `Builder` (1 required `modelPath` + 40 defaulted).

#### Input
- `Builder(String modelPath)` required GGUF path; `builder(String modelPath)` factory.
- 40 optional `Builder` setters: ints `contextSize, maxOutputTokens, threads, topK, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, repeatLastN, batchSize, ubatchSize, threadsBatch, mainGpu, reasoningBudgetTokens, dryAllowedLength, dryPenaltyLastN`; floats `temperature, topP, minP, topNSigma, repeatPenalty, dryMultiplier, dryBase`; booleans `cachePrompt, swaFull, flashAttn`; `Boolean chatTemplateEnableThinking`; Strings `lazyMode, cacheTypeK, cacheTypeV, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort`; Lists `drySequenceBreakers, stopStrings`.
- String/List null inputs coerced to `AiGenerationConfig.DEFAULT_*` or empty list; `AiGenerationConfig.DEFAULT_*` constants as initial defaults.

#### Output
- `LlamaCppJniConfig` immutable instance via `build()`; direct field copies, no normalization.
- 41 record-style accessors expose `modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings`.
- `drySequenceBreakers(), stopStrings()` return `Collections.unmodifiableList` views.

#### Core logic
- `builder(modelPath)` enforces required `modelPath`, pre-fills 40 other values from `AiGenerationConfig.DEFAULT_*`.
- Private `LlamaCppJniConfig(Builder)` copies all 41 builder values verbatim.
- `Builder` fluent setters mutate and return `this`; `build()` constructs immutable config.
- Null-tolerant setters for 11 fields: `lazyMode, cacheTypeK, cacheTypeV, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, drySequenceBreakers, stopStrings` restore defaults/empty.
- `chatTemplateEnableThinking` tri-state null means omit `enable_thinking` kwarg, defer to model template default.
- Sentinel conventions preserved: `-1` leave binding default, `0`/`0.0`/empty disable/omit.

#### Public API
- `builder(String modelPath) -> Builder` — create builder with defaults.
- 41 record-style accessors `modelPath()..stopStrings()` (41 total) — read config values.
- 40 fluent setters `contextSize(int)..stopStrings(List)` in `Builder` (40 total, no `modelPath` setter) — configure optionally.
- `build() -> LlamaCppJniConfig` — construct immutable config.

#### Dependencies
- `java.util.Collections, java.util.List, java.util.Objects`.
- `lombok.EqualsAndHashCode, lombok.ToString`.
- `net.ladenthin.srcmorph.config.AiGenerationConfig`, `net.ladenthin.srcmorph.support.ConvertToRecord`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `NullPointerException` if `modelPath` null in `Builder` via `Objects.requireNonNull`.
- No validation in private constructor / `build()`; null String/List handled by default-restore, not thrown.

#### Concurrency
- `LlamaCppJniConfig` immutable, thread-safe after publication.
- `Builder` explicitly not thread-safe, single-thread fill-then-build.

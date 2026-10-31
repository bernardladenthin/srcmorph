### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:22:23Z
- G: 
- A: 0.0.0
- X: file
---
> Holds immutable generation and backend tuning for llama.cpp local and OpenAI-compatible inference.

#### Purpose
- Immutable value object carrying LlamaCppJniConfig model, sampling, KV-cache, GPU, DRY, remote-provider settings.
- Single defaults source via Builder initialized from AiGenerationConfig.DEFAULT_*.

#### Type
- final class LlamaCppJniConfig, nested public static final class Builder.
- Annotations: @ConvertToRecord, Lombok @ToString, @EqualsAndHashCode over all fields.

#### Input
- Builder(String modelPath) required GGUF path, plus 40 optional setters for sampling, cache, batch, device, provider, reasoning, DRY, stopStrings.
- String setters null-tolerant: lazyMode, cacheTypeK, cacheTypeV, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort restore DEFAULT_* on null.
- List setters null-tolerant: drySequenceBreakers, stopStrings restore emptyList on null.
- Private LlamaCppJniConfig(Builder builder) copies all 41 builder fields without normalization.

#### Output
- Immutable snapshot with 41 record-style accessors; list accessors return unmodifiableList views.
- Builder.build() -> LlamaCppJniConfig produces configured instance.

#### Core logic
- Builder pre-loads every field from AiGenerationConfig.DEFAULT_* except modelPath.
- builder(String) enforces required modelPath at creation, avoids half-formed builder.
- String/List setters coalesce null to defaults/emptyList, preserving never-null invariant except nullable chatTemplateEnableThinking.
- Private copy constructor transfers builder state verbatim; no validation/duplicated guards.
- chatTemplateEnableThinking null means omit enable_thinking kwarg, use template default.
- drySequenceBreakers/stopStrings defensively wrapped on read via Collections.unmodifiableList.
- Lombok generates equals/hashCode/toString with exact-bit float comparison.

#### Public API
- builder(String modelPath) -> Builder — start config with defaults.
- modelPath() -> String — returns GGUF path.
- contextSize() -> int — returns context window.
- maxOutputTokens() -> int — returns max output tokens.
- temperature() -> float — returns sampling temperature.
- threads() -> int — returns CPU threads.
- topP() -> float — returns nucleus threshold.
- topK() -> int — returns top-k limit.
- minP() -> float — returns min-p threshold.
- topNSigma() -> float — returns top-n-sigma threshold.
- repeatPenalty() -> float — returns repetition penalty.
- chatTemplateEnableThinking() -> Boolean — returns thinking mode nullable.
- cachePrompt() -> boolean — returns prompt-cache enabled.
- swaFull() -> boolean — returns full SWA kept.
- cacheReuse() -> int — returns cache-reuse chunk.
- gpuLayers() -> int — returns GPU offload layers.
- seed() -> int — returns RNG seed.
- cpuMoeLayers() -> int — returns CPU MoE layers.
- cpuFfnLayers() -> int — returns CPU FFN layers.
- kvUnifiedPerSlot() -> int — returns unified KV cap.
- lazyMode() -> String — returns tensor-read laziness.
- repeatLastN() -> int — returns repeat window.
- cacheTypeK() -> String — returns K cache dtype.
- cacheTypeV() -> String — returns V cache dtype.
- flashAttn() -> boolean — returns Flash Attention enabled.
- batchSize() -> int — returns logical batch size.
- ubatchSize() -> int — returns physical batch size.
- threadsBatch() -> int — returns batch thread count.
- mainGpu() -> int — returns primary GPU index.
- devices() -> String — returns device selection.
- provider() -> String — returns AI provider id.
- remoteModel() -> String — returns remote model id.
- baseUrl() -> String — returns OpenAI-compatible base URL.
- apiKeyEnv() -> String — returns API key env name.
- reasoningEffort() -> String — returns reasoning effort.
- reasoningBudgetTokens() -> int — returns reasoning token budget.
- dryMultiplier() -> float — returns DRY multiplier.
- dryBase() -> float — returns DRY base.
- dryAllowedLength() -> int — returns DRY allowed length.
- dryPenaltyLastN() -> int — returns DRY look-back window.
- drySequenceBreakers() -> List<String> — returns DRY breakers view.
- stopStrings() -> List<String> — returns stop strings view.
- Builder.contextSize(int) -> Builder — sets context window.
- Builder.maxOutputTokens(int) -> Builder — sets max output tokens.
- Builder.temperature(float) -> Builder — sets sampling temperature.
- Builder.threads(int) -> Builder — sets CPU threads.
- Builder.topP(float) -> Builder — sets nucleus threshold.
- Builder.topK(int) -> Builder — sets top-k limit.
- Builder.minP(float) -> Builder — sets min-p threshold.
- Builder.topNSigma(float) -> Builder — sets top-n-sigma threshold.
- Builder.repeatPenalty(float) -> Builder — sets repetition penalty.
- Builder.chatTemplateEnableThinking(Boolean) -> Builder — sets thinking mode nullable.
- Builder.cachePrompt(boolean) -> Builder — sets prompt-cache enabled.
- Builder.swaFull(boolean) -> Builder — sets full SWA kept.
- Builder.cacheReuse(int) -> Builder — sets cache-reuse chunk.
- Builder.gpuLayers(int) -> Builder — sets GPU offload layers.
- Builder.seed(int) -> Builder — sets RNG seed.
- Builder.cpuMoeLayers(int) -> Builder — sets CPU MoE layers.
- Builder.cpuFfnLayers(int) -> Builder — sets CPU FFN layers.
- Builder.kvUnifiedPerSlot(int) -> Builder — sets unified KV cap.
- Builder.lazyMode(String) -> Builder — sets tensor-read laziness.
- Builder.repeatLastN(int) -> Builder — sets repeat window.
- Builder.cacheTypeK(String) -> Builder — sets K cache dtype.
- Builder.cacheTypeV(String) -> Builder — sets V cache dtype.
- Builder.flashAttn(boolean) -> Builder — sets Flash Attention enabled.
- Builder.batchSize(int) -> Builder — sets logical batch size.
- Builder.ubatchSize(int) -> Builder — sets physical batch size.
- Builder.threadsBatch(int) -> Builder — sets batch thread count.
- Builder.mainGpu(int) -> Builder — sets primary GPU index.
- Builder.devices(String) -> Builder — sets device selection.
- Builder.provider(String) -> Builder — sets AI provider id.
- Builder.remoteModel(String) -> Builder — sets remote model id.
- Builder.baseUrl(String) -> Builder — sets base URL.
- Builder.apiKeyEnv(String) -> Builder — sets API key env.
- Builder.reasoningEffort(String) -> Builder — sets reasoning effort.
- Builder.reasoningBudgetTokens(int) -> Builder — sets reasoning budget.
- Builder.dryMultiplier(float) -> Builder — sets DRY multiplier.
- Builder.dryBase(float) -> Builder — sets DRY base.
- Builder.dryAllowedLength(int) -> Builder — sets DRY allowed length.
- Builder.dryPenaltyLastN(int) -> Builder — sets DRY look-back window.
- Builder.drySequenceBreakers(List<String>) -> Builder — sets DRY breakers.
- Builder.stopStrings(List<String>) -> Builder — sets stop strings.
- Builder.build() -> LlamaCppJniConfig — builds immutable configuration.

#### Dependencies
- java.util.Collections, java.util.List, java.util.Objects.
- lombok.EqualsAndHashCode, lombok.ToString.
- net.ladenthin.srcmorph.config.AiGenerationConfig, net.ladenthin.srcmorph.support.ConvertToRecord.
- org.jspecify.annotations.Nullable.

#### Exceptions / Errors
- NullPointerException from builder(String) via Objects.requireNonNull when modelPath null.
- No validation in private constructor/build; setters perform null-coalescing only.

#### Concurrency
- LlamaCppJniConfig immutable, final fields, safe to share; list views unmodifiable.
- Builder not thread-safe, single-thread fill-then-build intended.

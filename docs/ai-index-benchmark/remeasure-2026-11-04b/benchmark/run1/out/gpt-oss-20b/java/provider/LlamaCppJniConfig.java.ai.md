### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for the llama.cpp JNI provider, exposing all model‑level and runtime knobs.

#### Purpose
- Holds 37 configuration knobs for llama.cpp JNI execution.
- Provides a fluent builder that defaults to `AiGenerationConfig.DEFAULT_*` values.

#### Type
- `final class LlamaCppJniConfig` (class, final).  
  - Supertypes: none.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
- `public static final class Builder` (class, final).  
  - Annotations: none.  
  - Supertypes: none.

#### Input
- `Builder` constructor: `String modelPath` (non‑null).  
- Builder setters: 36 optional parameters (int, float, boolean, `@Nullable Boolean`, `String`, `List<String>`).  
- `build()` consumes the populated `Builder`.

#### Output
- `LlamaCppJniConfig` immutable instance with 37 fields.  
- Getter methods return primitive values, `String`, `List<String>` (unmodifiable), or `@Nullable Boolean`.  
- `stopStrings()` and `drySequenceBreakers()` return unmodifiable lists.

#### Core logic
- `private LlamaCppJniConfig(Builder)` copies each field from the builder.  
- `builder(String)` creates a new `Builder` with the required `modelPath`.  
- Each `Builder` setter assigns the argument to its field and returns `this`.  
- `Builder.build()` returns `new LlamaCppJniConfig(this)`.

#### Public API
- `LlamaCppJniConfig.builder(String) -> Builder` – create builder.  
- `LlamaCppJniConfig.modelPath() -> String` – model file path.  
- `LlamaCppJniConfig.contextSize() -> int` – context window.  
- `LlamaCppJniConfig.maxOutputTokens() -> int` – max output.  
- `LlamaCppJniConfig.temperature() -> float` – sampling temperature.  
- `LlamaCppJniConfig.threads() -> int` – CPU threads.  
- `LlamaCppJniConfig.topP() -> float` – nucleus threshold.  
- `LlamaCppJniConfig.topK() -> int` – top‑k limit.  
- `LlamaCppJniConfig.minP() -> float` – min‑p threshold.  
- `LlamaCppJniConfig.topNSigma() -> float` – top‑n‑sigma threshold.  
- `LlamaCppJniConfig.repeatPenalty() -> float` – repetition penalty.  
- `LlamaCppJniConfig.chatTemplateEnableThinking() -> @Nullable Boolean` – chat‑template flag.  
- `LlamaCppJniConfig.cachePrompt() -> boolean` – prompt caching.  
- `LlamaCppJniConfig.swaFull() -> boolean` – full‑size SWA cache.  
- `LlamaCppJniConfig.cacheReuse() -> int` – KV prefix reuse size.  
- `LlamaCppJniConfig.gpuLayers() -> int` – GPU layers.  
- `LlamaCppJniConfig.seed() -> int` – RNG seed.  
- `LlamaCppJniConfig.cpuMoeLayers() -> int` – CPU MoE layers.  
- `LlamaCppJniConfig.cpuFfnLayers() -> int` – CPU FFN layers.  
- `LlamaCppJniConfig.kvUnifiedPerSlot() -> int` – KV unified cap.  
- `LlamaCppJniConfig.lazyMode() -> String` – tensor‑read laziness.  
- `LlamaCppJniConfig.repeatLastN() -> int` – repeat‑penalty window.  
- `LlamaCppJniConfig.cacheTypeK() -> String` – KV cache type K.  
- `LlamaCppJniConfig.cacheTypeV() -> String` – KV cache type V.  
- `LlamaCppJniConfig.flashAttn() -> boolean` – Flash Attention.  
- `LlamaCppJniConfig.batchSize() -> int` – logical batch size.  
- `LlamaCppJniConfig.ubatchSize() -> int` – physical batch size.  
- `LlamaCppJniConfig.threadsBatch() -> int` – batch thread count.  
- `LlamaCppJniConfig.mainGpu() -> int` – primary GPU index.  
- `LlamaCppJniConfig.devices() -> String` – device list.  
- `LlamaCppJniConfig.provider() -> String` – AI provider.  
- `LlamaCppJniConfig.remoteModel() -> String` – remote model ID.  
- `LlamaCppJniConfig.baseUrl() -> String` – OpenAI‑compatible base URL.  
- `LlamaCppJniConfig.apiKeyEnv() -> String` – API key env var.  
- `LlamaCppJniConfig.reasoningEffort() -> String` – reasoning effort.  
- `LlamaCppJniConfig.reasoningBudgetTokens() -> int` – reasoning budget.  
- `LlamaCppJniConfig.dryMultiplier() -> float` – DRY multiplier.  
- `LlamaCppJniConfig.dryBase() -> float` – DRY base.  
- `LlamaCppJniConfig.dryAllowedLength() -> int` – DRY allowed length.  
- `LlamaCppJniConfig.dryPenaltyLastN() -> int` – DRY penalty window.  
- `LlamaCppJniConfig.drySequenceBreakers() -> List<String>` – DRY breakers.  
- `LlamaCppJniConfig.stopStrings() -> List<String>` – stop strings.  
- `Builder.contextSize(int) -> Builder` – set context size.  
- `Builder.maxOutputTokens(int) -> Builder` – set max output.  
- `Builder.temperature(float) -> Builder` – set temperature.  
- `Builder.threads(int) -> Builder` – set threads.  
- `Builder.topP(float) -> Builder` – set top‑p.  
- `Builder.topK(int) -> Builder` – set top‑k.  
- `Builder.minP(float) -> Builder` – set min‑p.  
- `Builder.topNSigma(float) -> Builder` – set top‑n‑sigma.  
- `Builder.repeatPenalty(float) -> Builder` – set repeat penalty.  
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` – set chat‑template flag.  
- `Builder.cachePrompt(boolean) -> Builder` – set cache prompt.  
- `Builder.swaFull(boolean) -> Builder` – set SWA full.  
- `Builder.cacheReuse(int) -> Builder` – set cache reuse.  
- `Builder.gpuLayers(int) -> Builder` – set GPU layers.  
- `Builder.seed(int) -> Builder` – set seed.  
- `Builder.cpuMoeLayers(int) -> Builder` – set CPU MoE layers.  
- `Builder.cpuFfnLayers(int) -> Builder` – set CPU FFN layers.  
- `Builder.kvUnifiedPerSlot(int) -> Builder` – set KV unified cap.  
- `Builder.lazyMode(String) -> Builder` – set lazy mode.  
- `Builder.repeatLastN(int) -> Builder` – set repeat last‑N.  
- `Builder.cacheTypeK(String) -> Builder` – set cache type K.  
- `Builder.cacheTypeV(String) -> Builder` – set cache type V.  
- `Builder.flashAttn(boolean) -> Builder` – set flash attention.  
- `Builder.batchSize(int) -> Builder` – set batch size.  
- `Builder.ubatchSize(int) -> Builder` – set ubatch size.  
- `Builder.threadsBatch(int) -> Builder` – set threads batch.  
- `Builder.mainGpu(int) -> Builder` – set main GPU.  
- `Builder.devices(String) -> Builder` – set devices.  
- `Builder.provider(String) -> Builder` – set provider.  
- `Builder.remoteModel(String) -> Builder` – set remote model.  
- `Builder.baseUrl(String) -> Builder` – set base URL.  
- `Builder.apiKeyEnv(String) -> Builder` – set API key env.  
- `Builder.reasoningEffort(String) -> Builder` – set reasoning effort.  
- `Builder.reasoningBudgetTokens(int) -> Builder` – set reasoning budget.  
- `Builder.dryMultiplier(float) -> Builder` – set DRY multiplier.  
- `Builder.dryBase(float) -> Builder` – set DRY base.  
- `Builder.dryAllowedLength(int) -> Builder` – set DRY allowed length.  
- `Builder.dryPenaltyLastN(int) -> Builder` – set DRY penalty window.  
- `Builder.drySequenceBreakers(List<String>) -> Builder` – set DRY breakers.  
- `Builder.stopStrings(List<String>) -> Builder` – set stop strings.  
- `Builder.build() -> LlamaCppJniConfig` – create immutable config.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiGenerationConfig` – default values.  
- `net.ladenthin.srcmorph.support.ConvertToRecord` – annotation.  
- `org.jspecify.annotations.Nullable` – nullable annotation.  
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- Lombok annotations: `@EqualsAndHashCode`, `@ToString`.

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is `null`.  
- `Builder` setters accept any value; no validation beyond null checks for `String` parameters that may be `null` to restore defaults.

#### Concurrency
- `Builder` is not thread‑safe; intended single‑thread usage.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

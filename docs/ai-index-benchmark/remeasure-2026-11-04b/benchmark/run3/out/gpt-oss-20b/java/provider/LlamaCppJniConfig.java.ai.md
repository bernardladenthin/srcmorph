### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:56Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds an immutable configuration for llama.cpp JNI provider, exposing all runtime knobs and defaults for model execution.

#### Purpose
- Holds 37 configuration parameters for llama.cpp JNI provider.
- Provides fluent builder with defaults from `AiGenerationConfig`.

#### Type
- `final class LlamaCppJniConfig` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.  
  - Fields: 37 `private final` fields (see below).  
- `public static final class Builder` (class, static, final)  
  - Fields: 37 mutable fields, initialized to `AiGenerationConfig.DEFAULT_*`.  
  - Methods: 37 setter methods, `build()`.

#### Input
- `Builder` constructor: `String modelPath` (non‑null).  
- Setter methods accept primitive or object types matching the corresponding field.  
- `build()` consumes the populated `Builder`.

#### Output
- `LlamaCppJniConfig` immutable instance with all fields set from the builder.  
- Getter methods return primitive or object values; list getters return unmodifiable views.

#### Core logic
- `LlamaCppJniConfig(Builder)` copies each builder field to the corresponding final field.  
- `Builder` constructor stores `modelPath` after `Objects.requireNonNull`.  
- Each setter assigns the argument to its field and returns `this`.  
- `build()` creates a new `LlamaCppJniConfig(this)`.

#### Public API
- `LlamaCppJniConfig.builder(String) -> Builder` – start builder with required model path.  
- `modelPath() -> String` – model file path.  
- `contextSize() -> int` – context window size.  
- `maxOutputTokens() -> int` – max output tokens.  
- `temperature() -> float` – sampling temperature.  
- `threads() -> int` – CPU threads.  
- `topP() -> float` – nucleus‑sampling threshold.  
- `topK() -> int` – top‑k limit.  
- `minP() -> float` – min‑p threshold.  
- `topNSigma() -> float` – top‑n‑sigma threshold.  
- `repeatPenalty() -> float` – repetition penalty.  
- `chatTemplateEnableThinking() -> @Nullable Boolean` – chat‑template thinking flag.  
- `cachePrompt() -> boolean` – prompt caching enabled.  
- `swaFull() -> boolean` – full‑size SWA cache kept.  
- `cacheReuse() -> int` – KV prefix‑reuse chunk size.  
- `gpuLayers() -> int` – GPU layers offloaded.  
- `seed() -> int` – RNG seed.  
- `cpuMoeLayers() -> int` – CPU MoE layers.  
- `cpuFfnLayers() -> int` – CPU FFN layers.  
- `kvUnifiedPerSlot() -> int` – per‑slot unified KV cap.  
- `lazyMode() -> String` – tensor‑read laziness.  
- `repeatLastN() -> int` – repeat‑penalty window.  
- `cacheTypeK() -> String` – KV‑cache data type for K.  
- `cacheTypeV() -> String` – KV‑cache data type for V.  
- `flashAttn() -> boolean` – Flash Attention enabled.  
- `batchSize() -> int` – logical batch size.  
- `ubatchSize() -> int` – physical batch size.  
- `threadsBatch() -> int` – batch/prompt‑processing threads.  
- `mainGpu() -> int` – primary GPU index.  
- `devices() -> String` – device selection.  
- `provider() -> String` – AI provider name.  
- `remoteModel() -> String` – remote model ID.  
- `baseUrl() -> String` – OpenAI‑compatible base URL.  
- `apiKeyEnv() -> String` – environment variable for API key.  
- `reasoningEffort() -> String` – reasoning‑effort value.  
- `reasoningBudgetTokens() -> int` – reasoning token budget.  
- `dryMultiplier() -> float` – DRY sampling multiplier.  
- `dryBase() -> float` – DRY base.  
- `dryAllowedLength() -> int` – DRY allowed length.  
- `dryPenaltyLastN() -> int` – DRY penalty look‑back.  
- `drySequenceBreakers() -> List<String>` – unmodifiable list.  
- `stopStrings() -> List<String>` – unmodifiable list.  
- `Builder.contextSize(int) -> Builder` – set context size.  
- `Builder.maxOutputTokens(int) -> Builder` – set max output tokens.  
- `Builder.temperature(float) -> Builder` – set temperature.  
- `Builder.threads(int) -> Builder` – set CPU threads.  
- `Builder.topP(float) -> Builder` – set nucleus‑sampling threshold.  
- `Builder.topK(int) -> Builder` – set top‑k limit.  
- `Builder.minP(float) -> Builder` – set min‑p threshold.  
- `Builder.topNSigma(float) -> Builder` – set top‑n‑sigma threshold.  
- `Builder.repeatPenalty(float) -> Builder` – set repetition penalty.  
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` – set chat‑template thinking.  
- `Builder.cachePrompt(boolean) -> Builder` – set prompt caching.  
- `Builder.swaFull(boolean) -> Builder` – set full‑size SWA cache.  
- `Builder.cacheReuse(int) -> Builder` – set KV prefix‑reuse chunk size.  
- `Builder.gpuLayers(int) -> Builder` – set GPU layers.  
- `Builder.seed(int) -> Builder` – set RNG seed.  
- `Builder.cpuMoeLayers(int) -> Builder` – set CPU MoE layers.  
- `Builder.cpuFfnLayers(int) -> Builder` – set CPU FFN layers.  
- `Builder.kvUnifiedPerSlot(int) -> Builder` – set KV unified cap.  
- `Builder.lazyMode(String) -> Builder` – set tensor‑read laziness.  
- `Builder.repeatLastN(int) -> Builder` – set repeat‑penalty window.  
- `Builder.cacheTypeK(String) -> Builder` – set KV cache type K.  
- `Builder.cacheTypeV(String) -> Builder` – set KV cache type V.  
- `Builder.flashAttn(boolean) -> Builder` – set Flash Attention.  
- `Builder.batchSize(int) -> Builder` – set logical batch size.  
- `Builder.ubatchSize(int) -> Builder` – set physical batch size.  
- `Builder.threadsBatch(int) -> Builder` – set batch/prompt threads.  
- `Builder.mainGpu(int) -> Builder` – set primary GPU index.  
- `Builder.devices(String) -> Builder` – set device selection.  
- `Builder.provider(String) -> Builder` – set AI provider.  
- `Builder.remoteModel(String) -> Builder` – set remote model ID.  
- `Builder.baseUrl(String) -> Builder` – set base URL.  
- `Builder.apiKeyEnv(String) -> Builder` – set API key env var.  
- `Builder.reasoningEffort(String) -> Builder` – set reasoning effort.  
- `Builder.reasoningBudgetTokens(int) -> Builder` – set reasoning budget.  
- `Builder.dryMultiplier(float) -> Builder` – set DRY multiplier.  
- `Builder.dryBase(float) -> Builder` – set DRY base.  
- `Builder.dryAllowedLength(int) -> Builder` – set DRY allowed length.  
- `Builder.dryPenaltyLastN(int) -> Builder` – set DRY penalty look‑back.  
- `Builder.drySequenceBreakers(List<String>) -> Builder` – set DRY sequence breakers.  
- `Builder.stopStrings(List<String>) -> Builder` – set stop strings.  
- `Builder.build() -> LlamaCppJniConfig` – create immutable config.

#### Dependencies
- `java.util.Collections`, `java.util.List`, `java.util.Objects`.  
- `lombok.EqualsAndHashCode`, `lombok.ToString`.  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`.  
- `net.ladenthin.srcmorph.support.ConvertToRecord`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `Objects.requireNonNull` throws `NullPointerException` if `modelPath` is null.  
- No other explicit exceptions.

#### Concurrency
- `Builder` is not thread‑safe; intended for single‑thread use.  
- `LlamaCppJniConfig` is immutable and thread‑safe.

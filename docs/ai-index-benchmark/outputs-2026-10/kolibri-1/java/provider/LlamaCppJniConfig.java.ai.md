### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:24:41Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, built via a builder with defaults from AiGenerationConfig.

#### Purpose
- Immutable value object carrying all llama.cpp JNI generation parameters.
- Single source of truth for model path, sampling, caching, GPU/CPU offload, and OpenAI-compatible API settings.

#### Type
- Final class `LlamaCppJniConfig` with private constructor.
- Inner static nested class `Builder` with fluent setters.
- Annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode` (Lombok).
- Key generics: `List<String>` for `drySequenceBreakers` and `stopStrings`.

#### Input
- Constructor: `Builder` instance.
- Factory: `String modelPath`.
- Builder fields: all 37 config values, initialized from `AiGenerationConfig.DEFAULT_*` or explicit setters.
- Read resources: none.

#### Output
- `LlamaCppJniConfig` instance with all 37 fields set.
- Builder returns `this` for chaining.
- `build()` returns the immutable config.

#### Core logic
- Private constructor copies all 37 builder fields into final instance fields.
- `builder(String modelPath)` creates a new Builder with modelPath required and all other fields set to `AiGenerationConfig.DEFAULT_*`.
- All setters assign directly to builder fields without validation.
- `build()` constructs and returns a new `LlamaCppJniConfig` from the builder.
- Accessors return the corresponding final field or unmodifiable list view.

#### Public API
- `builder(String modelPath) -> Builder` creates a builder with defaults.
- `modelPath() -> String` returns the model file path.
- `contextSize() -> int` returns context window size.
- `maxOutputTokens() -> int` returns max output tokens.
- `temperature() -> float` returns sampling temperature.
- `threads() -> int` returns CPU thread count.
- `topP() -> float` returns nucleus-sampling threshold.
- `topK() -> int` returns top-k limit.
- `minP() -> float` returns min-p threshold.
- `topNSigma() -> float` returns top-n-sigma threshold.
- `repeatPenalty() -> float` returns repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` returns thinking mode or null.
- `cachePrompt() -> boolean` returns prompt caching flag.
- `swaFull() -> boolean` returns full SWA KV cache flag.
- `cacheReuse() -> int` returns cache-reuse chunk size.
- `gpuLayers() -> int` returns GPU layers to offload.
- `seed() -> int` returns RNG seed.
- `cpuMoeLayers() -> int` returns CPU MoE layers.
- `cpuFfnLayers() -> int` returns CPU FFN layers.
- `kvUnifiedPerSlot() -> int` returns per-slot KV cap.
- `lazyMode() -> String` returns tensor-read laziness.
- `repeatLastN() -> int` returns repeat-penalty window.
- `cacheTypeK() -> String` returns KV-cache K type.
- `cacheTypeV() -> String` returns KV-cache V type.
- `flashAttn() -> boolean` returns Flash Attention flag.
- `batchSize() -> int` returns logical batch size.
- `ubatchSize() -> int` returns physical batch size.
- `threadsBatch() -> int` returns batch thread count.
- `mainGpu() -> int` returns primary GPU index.
- `devices() -> String` returns device selection.
- `provider() -> String` returns AI provider.
- `remoteModel() -> String` returns remote model id.
- `baseUrl() -> String` returns base URL.
- `apiKeyEnv() -> String` returns API key env var name.
- `reasoningEffort() -> String` returns reasoning effort.
- `reasoningBudgetTokens() -> int` returns reasoning budget tokens.
- `dryMultiplier() -> float` returns DRY multiplier.
- `dryBase() -> float` returns DRY base.
- `dryAllowedLength() -> int` returns DRY allowed length.
- `dryPenaltyLastN() -> int` returns DRY penalty window.
- `drySequenceBreakers() -> List<String>` returns unmodifiable DRY breakers.
- `stopStrings() -> List<String>` returns unmodifiable stop strings.
- `Builder.contextSize(int) -> Builder` sets context window size.
- `Builder.maxOutputTokens(int) -> Builder` sets max output tokens.
- `Builder.temperature(float) -> Builder` sets sampling temperature.
- `Builder.threads(int) -> Builder` sets CPU thread count.
- `Builder.topP(float) -> Builder` sets nucleus-sampling threshold.
- `Builder.topK(int) -> Builder` sets top-k limit.
- `Builder.minP(float) -> Builder` sets min-p threshold.
- `Builder.topNSigma(float) -> Builder` sets top-n-sigma threshold.
- `Builder.repeatPenalty(float) -> Builder` sets repetition penalty.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` sets thinking mode.
- `Builder.cachePrompt(boolean) -> Builder` sets prompt caching flag.
- `Builder.swaFull(boolean) -> Builder` sets full SWA KV cache flag.
- `Builder.cacheReuse(int) -> Builder` sets cache-reuse chunk size.
- `Builder.gpuLayers(int) -> Builder` sets GPU layers to offload.
- `Builder.seed(int) -> Builder` sets RNG seed.
- `Builder.cpuMoeLayers(int) -> Builder` sets CPU MoE layers.
- `Builder.cpuFfnLayers(int) -> Builder` sets CPU FFN layers.
- `Builder.kvUnifiedPerSlot(int) -> Builder` sets per-slot KV cap.
- `Builder.lazyMode(String) -> Builder` sets tensor-read laziness.
- `Builder.repeatLastN(int) -> Builder` sets repeat-penalty window.
- `Builder.cacheTypeK(String) -> Builder` sets KV-cache K type.
- `Builder.cacheTypeV(String) -> Builder` sets KV-cache V type.
- `Builder.flashAttn(boolean) -> Builder` sets Flash Attention flag.
- `Builder.batchSize(int) -> Builder` sets logical batch size.
- `Builder.ubatchSize(int) -> Builder` sets physical batch size.
- `Builder.threadsBatch(int) -> Builder` sets batch thread count.
- `Builder.mainGpu(int) -> Builder` sets primary GPU index.
- `Builder.devices(String) -> Builder` sets device selection.
- `Builder.provider(String) -> Builder` sets AI provider.
- `Builder.remoteModel(String) -> Builder` sets remote model id.
- `Builder.baseUrl(String) -> Builder` sets base URL.
- `Builder.apiKeyEnv(String) -> Builder` sets API key env var name.
- `Builder.reasoningEffort(String) -> Builder` sets reasoning effort.
- `Builder.reasoningBudgetTokens(int) -> Builder` sets reasoning budget tokens.
- `Builder.dryMultiplier(float) -> Builder` sets DRY multiplier.
- `Builder.dryBase(float) -> Builder` sets DRY base.
- `Builder.dryAllowedLength(int) -> Builder` sets DRY allowed length.
- `Builder.dryPenaltyLastN(int) -> Builder` sets DRY penalty window.
- `Builder.drySequenceBreakers(List<String>) -> Builder` sets DRY sequence breakers.
- `Builder.stopStrings(List<String>) -> Builder` sets stop strings.
- `Builder.build() -> LlamaCppJniConfig` builds the immutable config.

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `NullPointerException` thrown by `Objects.requireNonNull(modelPath, "modelPath")` in Builder constructor.
- No other exceptions or error conditions documented.

#### Concurrency
- `LlamaCppJniConfig` is immutable and thread-safe.
- `Builder` is not thread-safe and not meant to be shared across threads.

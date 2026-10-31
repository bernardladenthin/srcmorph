### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:22Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp and OpenAI-compatible generation settings for a model, including sampling, hardware, caching, reasoning, and stopping behavior.

#### Purpose
- `LlamaCppJniConfig` holds generation and provider options with defaults sourced from `AiGenerationConfig`.
- Its fluent builder produces an immutable configuration value.

#### Type
- `public final class LlamaCppJniConfig` — class; annotated `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.
- Nested `public static final class Builder` — fluent builder for `LlamaCppJniConfig`.

#### Input
- `builder(String modelPath)` requires the GGUF model path.
- Builder setters accept sampling, threading, cache, GPU, provider/API, reasoning, DRY, and stopping options.
- Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty lists.

#### Output
- Record-style accessors expose all configuration values.
- `build()` returns a `LlamaCppJniConfig`; Lombok generates `equals`, `hashCode`, and `toString`.
- List accessors return unmodifiable views of the configured DRY sequence breakers and stop strings.

#### Core logic
- `Builder` initializes optional values to `AiGenerationConfig` defaults; callers can override them fluently.
- Null string setters for selected options restore their corresponding defaults; null DRY-breaker and stop-string lists become empty lists.
- The private config constructor copies builder values directly without further normalization.
- The config fields are final. List inputs are not copied, so external mutation of a supplied list can affect the stored contents despite the unmodifiable accessor view.

#### Public API
- `builder(String) -> Builder` — start with model path.
- `modelPath() -> String` — model file path.
- `contextSize() -> int` — context window.
- `maxOutputTokens() -> int` — output token limit.
- `temperature() -> float` — sampling temperature.
- `threads() -> int` — CPU thread count.
- `topP() -> float` — nucleus sampling threshold.
- `topK() -> int` — top-k sampling limit.
- `minP() -> float` — min-p sampling threshold.
- `topNSigma() -> float` — top-n-sigma threshold.
- `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — template thinking mode.
- `cachePrompt() -> boolean` — prompt caching flag.
- `swaFull() -> boolean` — full SWA KV cache flag.
- `cacheReuse() -> int` — KV prefix reuse chunk size.
- `gpuLayers() -> int` — GPU offload layers.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repetition penalty window.
- `cacheTypeK() -> String` — K-cache data type.
- `cacheTypeV() -> String` — V-cache data type.
- `flashAttn() -> boolean` — Flash Attention flag.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — AI provider.
- `remoteModel() -> String` — remote model identifier.
- `baseUrl() -> String` — OpenAI-compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment variable name.
- `reasoningEffort() -> String` — reasoning effort setting.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY sampling multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY penalty look-back window.
- `drySequenceBreakers() -> List<String>` — DRY sequence breakers.
- `stopStrings() -> List<String>` — generation stop strings.
- `contextSize(int) -> Builder` — set context window.
- `maxOutputTokens(int) -> Builder` — set output token limit.
- `temperature(float) -> Builder` — set sampling temperature.
- `threads(int) -> Builder` — set CPU thread count.
- `topP(float) -> Builder` — set nucleus threshold.
- `topK(int) -> Builder` — set top-k limit.
- `minP(float) -> Builder` — set min-p threshold.
- `topNSigma(float) -> Builder` — set top-n-sigma threshold.
- `repeatPenalty(float) -> Builder` — set repetition penalty.
- `chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set template thinking mode.
- `cachePrompt(boolean) -> Builder` — set prompt caching.
- `swaFull(boolean) -> Builder` — set full SWA KV caching.
- `cacheReuse(int) -> Builder` — set KV prefix reuse.
- `gpuLayers(int) -> Builder` — set GPU offload layers.
- `seed(int) -> Builder` — set RNG seed.
- `cpuMoeLayers(int) -> Builder` — set CPU MoE layers.
- `cpuFfnLayers(int) -> Builder` — set CPU FFN layers.
- `kvUnifiedPerSlot(int) -> Builder` — set per-slot KV cap.
- `lazyMode(String) -> Builder` — set tensor-read laziness.
- `repeatLastN(int) -> Builder` — set repetition window.
- `cacheTypeK(String) -> Builder` — set K-cache type.
- `cacheTypeV(String) -> Builder` — set V-cache type.
- `flashAttn(boolean) -> Builder` — set Flash Attention.
- `batchSize(int) -> Builder` — set logical batch size.
- `ubatchSize(int) -> Builder` — set physical batch size.
- `threadsBatch(int) -> Builder` — set batch-processing threads.
- `mainGpu(int) -> Builder` — set primary GPU.
- `devices(String) -> Builder` — set device selection.
- `provider(String) -> Builder` — set provider.
- `remoteModel(String) -> Builder` — set remote model identifier.
- `baseUrl(String) -> Builder` — set API base URL.
- `apiKeyEnv(String) -> Builder` — set API-key environment variable.
- `reasoningEffort(String) -> Builder` — set reasoning effort.
- `reasoningBudgetTokens(int) -> Builder` — set reasoning token budget.
- `dryMultiplier(float) -> Builder` — set DRY multiplier.
- `dryBase(float) -> Builder` — set DRY base.
- `dryAllowedLength(int) -> Builder` — set DRY allowed length.
- `dryPenaltyLastN(int) -> Builder` — set DRY look-back window.
- `drySequenceBreakers(List<String>) -> Builder` — set DRY breakers.
- `stopStrings(List<String>) -> Builder` — set stop strings.
- `build() -> LlamaCppJniConfig` — construct configuration.

#### Dependencies
- `AiGenerationConfig` — defaults for generation settings.
- `ConvertToRecord` — record-migration marker.
- Lombok `EqualsAndHashCode`, `ToString`.
- JSpecify `Nullable`.
- Java `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` via `Objects.requireNonNull`, with message `modelPath`.
- Null values passed to selected string setters restore defaults; null list setters use empty lists.
- Other values are assigned without validation.

#### Concurrency
- `LlamaCppJniConfig` fields are final; `Builder` is explicitly not thread-safe.
- List fields are retained by reference; returned unmodifiable views do not prevent mutation through another reference.

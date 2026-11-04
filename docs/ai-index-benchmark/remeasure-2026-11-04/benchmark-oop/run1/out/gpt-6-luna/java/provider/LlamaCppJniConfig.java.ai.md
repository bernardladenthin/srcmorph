### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:21:02Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures llama.cpp JNI and OpenAI-compatible text generation, including model loading, sampling, hardware, caching, reasoning, and stop controls.

#### Purpose
- `LlamaCppJniConfig` holds immutable model and generation settings; `Builder` applies defaults and assembles configurations.
- Exposes sampling, llama.cpp runtime, remote-provider, reasoning, DRY, and stop-string options.

#### Type
- `public final class LlamaCppJniConfig` — annotated `@ConvertToRecord`, Lombok `@ToString`, `@EqualsAndHashCode`; no supertypes declared. Contains 41 private final fields.
- `public static final class Builder` — nested builder; no supertypes declared. Holds mutable configuration values while building.
- `chatTemplateEnableThinking` is `@Nullable Boolean`; other fields comprise `String`, primitive numeric/boolean types, and `List<String>`.

#### Input
- `builder(String modelPath)` requires a GGUF model path; builder constructor rejects null via `Objects.requireNonNull`.
- 40 optional builder settings cover generation, hardware/runtime, provider/API, reasoning, DRY, and stop controls. Defaults come from `AiGenerationConfig.DEFAULT_*`; list defaults are empty.
- Null string settings for lazy mode, cache types, devices, provider, remote model, base URL, API-key environment-variable name, and reasoning effort restore their respective defaults. Null DRY sequence-breaker and stop-string lists become empty lists.
- Null `chatTemplateEnableThinking` represents an unset option.

#### Output
- `build()` produces an immutable `LlamaCppJniConfig`; its constructor copies builder values.
- Accessors expose all configuration values. List accessors return unmodifiable views.
- Lombok generates `equals`, `hashCode`, and `toString` over the fields.

#### Core logic
- `builder(modelPath)` creates a builder with the required model path and defaults for every other option.
- Fluent setters replace selected values; selected nullable inputs restore defaults or empty lists.
- `build()` transfers builder state into the configuration without further normalization.
- `drySequenceBreakers()` and `stopStrings()` wrap their stored lists with `Collections.unmodifiableList`.
- Configuration is immutable; the builder is documented as not thread-safe.

#### Public API
- `builder(String) -> Builder` — starts configuration with defaults.
- `modelPath() -> String` — GGUF model path.
- `contextSize() -> int` — context window.
- `maxOutputTokens() -> int` — output token limit.
- `temperature() -> float` — sampling temperature.
- `threads() -> int` — CPU thread count.
- `topP() -> float` — nucleus threshold.
- `topK() -> int` — top-k limit.
- `minP() -> float` — min-p threshold.
- `topNSigma() -> float` — top-n-sigma threshold.
- `repeatPenalty() -> float` — repetition penalty.
- `chatTemplateEnableThinking() -> @Nullable Boolean` — template thinking option.
- `cachePrompt() -> boolean` — prompt caching.
- `swaFull() -> boolean` — full SWA KV cache.
- `cacheReuse() -> int` — KV prefix-reuse chunk size.
- `gpuLayers() -> int` — GPU offload layers.
- `seed() -> int` — generation RNG seed.
- `cpuMoeLayers() -> int` — CPU MoE layers.
- `cpuFfnLayers() -> int` — CPU FFN layers.
- `kvUnifiedPerSlot() -> int` — per-slot KV context cap.
- `lazyMode() -> String` — tensor-read laziness.
- `repeatLastN() -> int` — repetition window.
- `cacheTypeK() -> String` — K-cache data type.
- `cacheTypeV() -> String` — V-cache data type.
- `flashAttn() -> boolean` — Flash Attention setting.
- `batchSize() -> int` — logical batch size.
- `ubatchSize() -> int` — physical batch size.
- `threadsBatch() -> int` — batch-processing threads.
- `mainGpu() -> int` — primary GPU index.
- `devices() -> String` — device selection.
- `provider() -> String` — provider selection.
- `remoteModel() -> String` — remote model identifier.
- `baseUrl() -> String` — OpenAI-compatible API base URL.
- `apiKeyEnv() -> String` — API-key environment-variable name.
- `reasoningEffort() -> String` — reasoning-effort option.
- `reasoningBudgetTokens() -> int` — reasoning token budget.
- `dryMultiplier() -> float` — DRY multiplier.
- `dryBase() -> float` — DRY base.
- `dryAllowedLength() -> int` — DRY allowed length.
- `dryPenaltyLastN() -> int` — DRY penalty window.
- `drySequenceBreakers() -> List<String>` — DRY sequence breakers.
- `stopStrings() -> List<String>` — generation stop strings.
- `Builder.contextSize(int) -> Builder`, `maxOutputTokens(int) -> Builder`, `temperature(float) -> Builder`, `threads(int) -> Builder`, `topP(float) -> Builder`, `topK(int) -> Builder`, `minP(float) -> Builder`, `topNSigma(float) -> Builder`, `repeatPenalty(float) -> Builder` — set generation options.
- `Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder` — set template thinking.
- `Builder.cachePrompt(boolean) -> Builder`, `swaFull(boolean) -> Builder`, `cacheReuse(int) -> Builder`, `gpuLayers(int) -> Builder`, `seed(int) -> Builder`, `cpuMoeLayers(int) -> Builder`, `cpuFfnLayers(int) -> Builder`, `kvUnifiedPerSlot(int) -> Builder`, `lazyMode(String) -> Builder`, `repeatLastN(int) -> Builder`, `cacheTypeK(String) -> Builder`, `cacheTypeV(String) -> Builder`, `flashAttn(boolean) -> Builder`, `batchSize(int) -> Builder`, `ubatchSize(int) -> Builder`, `threadsBatch(int) -> Builder`, `mainGpu(int) -> Builder`, `devices(String) -> Builder` — set runtime options.
- `Builder.provider(String) -> Builder`, `remoteModel(String) -> Builder`, `baseUrl(String) -> Builder`, `apiKeyEnv(String) -> Builder`, `reasoningEffort(String) -> Builder`, `reasoningBudgetTokens(int) -> Builder` — set provider and reasoning options.
- `Builder.dryMultiplier(float) -> Builder`, `dryBase(float) -> Builder`, `dryAllowedLength(int) -> Builder`, `dryPenaltyLastN(int) -> Builder`, `drySequenceBreakers(List<String>) -> Builder`, `stopStrings(List<String>) -> Builder` — set DRY and stop options.
- `Builder.build() -> LlamaCppJniConfig` — creates immutable configuration.

#### Dependencies
- `AiGenerationConfig` — defaults for configuration options.
- `ConvertToRecord`, Lombok `EqualsAndHashCode`, Lombok `ToString`, JSpecify `Nullable`.
- `Collections`, `List`, `Objects`.

#### Exceptions / Errors
- `builder(null)` throws `NullPointerException` with message `modelPath`.
- No other validation or normalization is performed during construction.

#### Concurrency
- `LlamaCppJniConfig` is immutable; builder is explicitly not thread-safe.

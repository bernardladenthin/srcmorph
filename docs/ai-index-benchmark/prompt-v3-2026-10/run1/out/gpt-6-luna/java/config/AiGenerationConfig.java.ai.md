### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:16Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, inference hardware, caching, reasoning, and stop behavior for an AI generation step.

#### Purpose
- Carries mutable per-model generation settings between Maven configuration and AI providers.
- Supplies defaults for inference, prompt trimming, provider routing, and llama.cpp tuning.

#### Type
- `public class AiGenerationConfig` — class; Lombok `@ToString`; no generated `equals` or `hashCode`.
- Holds nullable `modelPath`, tri-state `chatTemplateEnableThinking`, and optional `AiCalibration`; other settings include sampling, hardware, provider, cache, and stop-list configuration.

#### Input
- No-argument constructor; values are populated through setters.
- Accepts model path, token/context and prompt limits, sampling and reasoning values, llama.cpp options, provider/API settings, sequence breakers, stop strings, and calibration.
- `DEFAULT_THREADS` depends on `Runtime.getRuntime().availableProcessors()`.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate this configuration object. Null list inputs reset lists to empty; selected nullable strings reset to their defaults.
- Lombok-generated `toString()` supports build-log diagnostics.

#### Core logic
- Initializes fields from public defaults, including sampling values, prompt limits, provider settings, and inference options.
- Represents unset options with sentinels such as `-1`, empty strings, or nullable values; this class does not validate ranges.
- Preserves chat-template thinking as tri-state: `null` means omit the kwarg; `true` or `false` is an explicit setting.
- Uses `charsPerToken` and input-limit settings to support automatic prompt sizing in downstream code; `charsPerToken == 0` signals fallback behavior.
- Returns unmodifiable views of `drySequenceBreakers` and `stopStrings`; setters retain supplied non-null lists.

#### Public API
- `AiGenerationConfig()` — create configuration with defaults.
- `getModelPath()` / `setModelPath(String)` -> GGUF model path.
- `getContextSize()` / `setContextSize(int)` -> context token limit.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()` / `setTemperature(float)` -> sampling temperature.
- `getThreads()` / `setThreads(int)` -> inference CPU threads.
- `getCharsPerToken()` / `setCharsPerToken(int)` -> prompt sizing ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` -> input character limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()` / `setTopP(float)` -> nucleus sampling threshold.
- `getTopK()` / `setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()` / `setMinP(float)` -> min-p sampling threshold.
- `getTopNSigma()` / `setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` -> tri-state thinking mode.
- `isCachePrompt()` / `setCachePrompt(boolean)` -> prompt prefix caching.
- `isSwaFull()` / `setSwaFull(boolean)` -> full-size SWA KV cache.
- `getCacheReuse()` / `setCacheReuse(int)` -> KV prefix-reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` -> CPU MoE layer count.
- `getSeed()` / `setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` -> CPU FFN layer count.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()` / `setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()` / `setBatchSize(int)` -> logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()` / `setMainGpu(int)` -> primary GPU index.
- `getDevices()` / `setDevices(String)` -> backend device selection.
- `getProvider()` / `setProvider(String)` -> per-model AI provider.
- `getRemoteModel()` / `setRemoteModel(String)` -> remote provider model ID.
- `getBaseUrl()` / `setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()` / `setReasoningEffort(String)` -> reasoning effort kwarg.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` -> DRY repetition multiplier.
- `getDryBase()` / `setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` -> unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` -> DRY reset tokens.
- `getStopStrings()` / `setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` -> per-machine timing calibration.

#### Dependencies
- `AiCalibration`; `java.util.ArrayList`, `Collections`, `List`.
- Lombok `ToString`; JSpecify `Nullable`.
- `Runtime` for processor-count-based default threads.

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable model path, thinking mode, and calibration are supported; nullable list inputs become empty lists.
- `setLazyMode`, cache-type setters, device/provider/remote-model/base-URL/API-key setters map null to their respective defaults.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.
- List getters are unmodifiable views, not snapshots; underlying lists may change through setters.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:11:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a mutable configuration object for AI generation steps, exposing all model and inference parameters for Maven plugin integration.

#### Purpose
- Holds AI generation parameters for a single model run.
- Serves as a JavaBean populated by Maven plugin configuration.

#### Type
- Class, public, mutable, Lombok `@ToString` annotation.

#### Input
- Constructor: no arguments.
- Setters for each configuration field (e.g., `setModelPath`, `setContextSize`, etc.).
- Optional `AiCalibration` injected via `setCalibration`.

#### Output
- Getters return current configuration values.
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.
- `getCalibration` returns optional calibration.

#### Core logic
- Stores default constants for all AI generation parameters.
- Provides getters and setters that preserve defaults when `null` or empty values are passed.
- Handles tri‑state `chatTemplateEnableThinking` and optional `cachePrompt` flag.
- Exposes configuration for provider selection, model path, prompt size, sampling, GPU/offload, KV cache, batch, and DRY settings.

#### Public API
- `getModelPath() -> String?` – returns model file path.
- `setModelPath(String?) -> void` – sets model file path.
- `getContextSize() -> int` – returns context window size.
- `setContextSize(int) -> void` – sets context window size.
- `getMaxOutputTokens() -> int` – returns max output tokens.
- `setMaxOutputTokens(int) -> void` – sets max output tokens.
- `getTemperature() -> float` – returns sampling temperature.
- `setTemperature(float) -> void` – sets sampling temperature.
- `getThreads() -> int` – returns CPU thread count.
- `setThreads(int) -> void` – sets CPU thread count.
- `getCharsPerToken() -> int` – returns chars‑per‑token ratio.
- `setCharsPerToken(int) -> void` – sets chars‑per‑token ratio.
- `getMaxInputChars() -> int` – returns max input characters.
- `setMaxInputChars(int) -> void` – sets max input characters.
- `isWarnOnTrim() -> boolean` – returns trim‑warning flag.
- `setWarnOnTrim(boolean) -> void` – sets trim‑warning flag.
- `getTopP() -> float` – returns nucleus‑sampling threshold.
- `setTopP(float) -> void` – sets nucleus‑sampling threshold.
- `getTopK() -> int` – returns top‑k limit.
- `setTopK(int) -> void` – sets top‑k limit.
- `getRepeatPenalty() -> float` – returns repetition penalty.
- `setRepeatPenalty(float) -> void` – sets repetition penalty.
- `getMinP() -> float` – returns min‑p threshold.
- `setMinP(float) -> void` – sets min‑p threshold.
- `getTopNSigma() -> float` – returns top‑n‑sigma threshold.
- `setTopNSigma(float) -> void` – sets top‑n‑sigma threshold.
- `getChatTemplateEnableThinking() -> Boolean?` – returns chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?) -> void` – sets chat‑template thinking mode.
- `isCachePrompt() -> boolean` – returns prompt‑caching flag.
- `isSwaFull() -> boolean` – returns full‑SWA flag.
- `setSwaFull(boolean) -> void` – sets full‑SWA flag.
- `getCacheReuse() -> int` – returns KV prefix‑reuse chunk size.
- `setCacheReuse(int) -> void` – sets KV prefix‑reuse chunk size.
- `getGpuLayers() -> int` – returns GPU offload layers.
- `setGpuLayers(int) -> void` – sets GPU offload layers.
- `getCpuMoeLayers() -> int` – returns CPU MoE layers.
- `setCpuMoeLayers(int) -> void` – sets CPU MoE layers.
- `getSeed() -> int` – returns RNG seed.
- `setSeed(int) -> void` – sets RNG seed.
- `getCpuFfnLayers() -> int` – returns CPU FFN layers.
- `setCpuFfnLayers(int) -> void` – sets CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` – returns KV unified per‑slot cap.
- `setKvUnifiedPerSlot(int) -> void` – sets KV unified per‑slot cap.
- `getLazyMode() -> String` – returns tensor‑read laziness.
- `setLazyMode(String?) -> void` – sets tensor‑read laziness.
- `getRepeatLastN() -> int` – returns repeat‑penalty window.
- `setRepeatLastN(int) -> void` – sets repeat‑penalty window.
- `getCacheTypeK() -> String` – returns KV cache type K.
- `setCacheTypeK(String?) -> void` – sets KV cache type K.
- `getCacheTypeV() -> String` – returns KV cache type V.
- `setCacheTypeV(String?) -> void` – sets KV cache type V.
- `isFlashAttn() -> boolean` – returns Flash Attention flag.
- `setFlashAttn(boolean) -> void` – sets Flash Attention flag.
- `getBatchSize() -> int` – returns logical batch size.
- `setBatchSize(int) -> void` – sets logical batch size.
- `getUbatchSize() -> int` – returns physical batch size.
- `setUbatchSize(int) -> void` – sets physical batch size.
- `getThreadsBatch() -> int` – returns batch thread count.
- `setThreadsBatch(int) -> void` – sets batch thread count.
- `getMainGpu() -> int` – returns primary GPU index.
- `setMainGpu(int) -> void` – sets primary GPU index.
- `getDevices() -> String` – returns device list.
- `setDevices(String) -> void` – sets device list.
- `getProvider() -> String` – returns AI provider.
- `setProvider(String?) -> void` – sets AI provider.
- `getRemoteModel() -> String` – returns remote model ID.
- `setRemoteModel(String?) -> void` – sets remote model ID.
- `getBaseUrl() -> String` – returns base URL.
- `setBaseUrl(String?) -> void` – sets base URL.
- `getApiKeyEnv() -> String` – returns API key environment variable name.
- `setApiKeyEnv(String?) -> void` – sets API key environment variable name.
- `setCachePrompt(boolean) -> void` – sets prompt‑caching flag.
- `getReasoningEffort() -> String` – returns reasoning‑effort level.
- `setReasoningEffort(String) -> void` – sets reasoning‑effort level.
- `getReasoningBudgetTokens() -> int` – returns reasoning budget tokens.
- `setReasoningBudgetTokens(int) -> void` – sets reasoning budget tokens.
- `getDryMultiplier() -> float` – returns DRY multiplier.
- `setDryMultiplier(float) -> void` – sets DRY multiplier.
- `getDryBase() -> float` – returns DRY base.
- `setDryBase(float) -> void` – sets DRY base.
- `getDryAllowedLength() -> int` – returns DRY allowed length.
- `setDryAllowedLength(int) -> void` – sets DRY allowed length.
- `getDryPenaltyLastN() -> int` – returns DRY penalty look‑back window.
- `setDryPenaltyLastN(int) -> void` – sets DRY penalty look‑back window.
- `getDrySequenceBreakers() -> List<String>` – returns unmodifiable DRY sequence breakers.
- `setDrySequenceBreakers(List<String>?) -> void` – sets DRY sequence breakers.
- `getStopStrings() -> List<String>` – returns unmodifiable stop strings.
- `setStopStrings(List<String>?) -> void` – sets stop strings.
- `getCalibration() -> AiCalibration?` – returns calibration.
- `setCalibration(AiCalibration?) -> void` – sets calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters guard against `null` by resetting to defaults.

#### Concurrency
- No synchronization; intended for single‑threaded configuration usage.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:17Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, used by the Maven plugin to configure the chosen AI provider.

#### Purpose
- Stores configuration for a single AI generation step (model path, sampling, retry, prompt limits, stop strings).
- Exposed to Maven plugin and AI provider implementations.

#### Type
- `public class AiGenerationConfig` (class, public, mutable, Lombok `@ToString`).

#### Input
- Constructor: no arguments (defaults applied).
- Setter methods for each field (e.g., `setModelPath`, `setContextSize`, `setTemperature`, …).
- `setDrySequenceBreakers(List<String>)`, `setStopStrings(List<String>)` accept `null` to reset.
- `setCalibration(AiCalibration)`.

#### Output
- Getter methods return current values (e.g., `getModelPath`, `getContextSize`, `getTemperature`, …).
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- No side‑effects beyond mutating internal state.

#### Core logic
- All fields initialized to static defaults (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`).
- Setters assign values directly; some guard against `null` by resetting to defaults.
- No additional computation or validation beyond simple assignments.

#### Public API
- `getModelPath() -> String?` – current model file path.
- `setModelPath(String?) -> void` – set model file path.
- `getContextSize() -> int` – context window size.
- `setContextSize(int) -> void` – set context size.
- `getMaxOutputTokens() -> int` – max output tokens.
- `setMaxOutputTokens(int) -> void` – set max output tokens.
- `getTemperature() -> float` – sampling temperature.
- `setTemperature(float) -> void` – set temperature.
- `getThreads() -> int` – CPU threads.
- `setThreads(int) -> void` – set threads.
- `getCharsPerToken() -> int` – chars per token ratio.
- `setCharsPerToken(int) -> void` – set ratio.
- `getMaxInputChars() -> int` – max input characters.
- `setMaxInputChars(int) -> void` – set max input chars.
- `isWarnOnTrim() -> boolean` – trim warning flag.
- `setWarnOnTrim(boolean) -> void` – set flag.
- `getTopP() -> float` – nucleus‑sampling threshold.
- `setTopP(float) -> void` – set threshold.
- `getTopK() -> int` – top‑k limit.
- `setTopK(int) -> void` – set limit.
- `getRepeatPenalty() -> float` – repetition penalty.
- `setRepeatPenalty(float) -> void` – set penalty.
- `getMinP() -> float` – min‑p threshold.
- `setMinP(float) -> void` – set threshold.
- `getTopNSigma() -> float` – top‑n‑sigma threshold.
- `setTopNSigma(float) -> void` – set threshold.
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode.
- `setChatTemplateEnableThinking(Boolean?) -> void` – set mode.
- `isCachePrompt() -> boolean` – prompt‑caching flag.
- `setCachePrompt(boolean) -> void` – set flag.
- `isSwaFull() -> boolean` – full‑SWA flag.
- `setSwaFull(boolean) -> void` – set flag.
- `getCacheReuse() -> int` – KV prefix‑reuse chunk size.
- `setCacheReuse(int) -> void` – set size.
- `getGpuLayers() -> int` – GPU offload layers.
- `setGpuLayers(int) -> void` – set layers.
- `getCpuMoeLayers() -> int` – CPU MoE layers.
- `setCpuMoeLayers(int) -> void` – set layers.
- `getSeed() -> int` – RNG seed.
- `setSeed(int) -> void` – set seed.
- `getCpuFfnLayers() -> int` – CPU FFN layers.
- `setCpuFfnLayers(int) -> void` – set layers.
- `getKvUnifiedPerSlot() -> int` – unified KV context cap.
- `setKvUnifiedPerSlot(int) -> void` – set cap.
- `getLazyMode() -> String` – tensor‑read laziness.
- `setLazyMode(String?) -> void` – set mode.
- `getRepeatLastN() -> int` – repeat‑penalty window.
- `setRepeatLastN(int) -> void` – set window.
- `getCacheTypeK() -> String` – KV cache type K.
- `setCacheTypeK(String?) -> void` – set type.
- `getCacheTypeV() -> String` – KV cache type V.
- `setCacheTypeV(String?) -> void` – set type.
- `isFlashAttn() -> boolean` – Flash Attention flag.
- `setFlashAttn(boolean) -> void` – set flag.
- `getBatchSize() -> int` – logical batch size.
- `setBatchSize(int) -> void` – set size.
- `getUbatchSize() -> int` – physical batch size.
- `setUbatchSize(int) -> void` – set size.
- `getThreadsBatch() -> int` – batch thread count.
- `setThreadsBatch(int) -> void` – set count.
- `getMainGpu() -> int` – primary GPU index.
- `setMainGpu(int) -> void` – set index.
- `getDevices() -> String` – device list.
- `setDevices(String) -> void` – set list.
- `getProvider() -> String` – AI provider.
- `setProvider(String?) -> void` – set provider.
- `getRemoteModel() -> String` – remote model id.
- `setRemoteModel(String?) -> void` – set id.
- `getBaseUrl() -> String` – base URL.
- `setBaseUrl(String?) -> void` – set URL.
- `getApiKeyEnv() -> String` – API key environment variable name.
- `setApiKeyEnv(String?) -> void` – set name.
- `getReasoningEffort() -> String` – reasoning‑effort level.
- `setReasoningEffort(String) -> void` – set level.
- `getReasoningBudgetTokens() -> int` – reasoning budget.
- `setReasoningBudgetTokens(int) -> void` – set budget.
- `getDryMultiplier() -> float` – DRY multiplier.
- `setDryMultiplier(float) -> void` – set multiplier.
- `getDryBase() -> float` – DRY base.
- `setDryBase(float) -> void` – set base.
- `getDryAllowedLength() -> int` – DRY allowed length.
- `setDryAllowedLength(int) -> void` – set length.
- `getDryPenaltyLastN() -> int` – DRY penalty window.
- `setDryPenaltyLastN(int) -> void` – set window.
- `getDrySequenceBreakers() -> List<String>` – unmodifiable breakers list.
- `setDrySequenceBreakers(List<String>?) -> void` – set breakers.
- `getStopStrings() -> List<String>` – unmodifiable stop strings list.
- `setStopStrings(List<String>?) -> void` – set stop strings.
- `getCalibration() -> AiCalibration?` – per‑machine calibration.
- `setCalibration(AiCalibration?) -> void` – set calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCalibration` (used for calibration field).

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept `null` and reset to defaults.

#### Concurrency
- No synchronization; intended for single‑threaded configuration usage.

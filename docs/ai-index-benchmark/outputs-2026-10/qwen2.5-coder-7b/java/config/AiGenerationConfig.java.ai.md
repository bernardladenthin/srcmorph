### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:46Z
- G: 
- A: 0.0.0
- X: file
---
> Manages configuration parameters for AI generation steps, bridging Maven settings and AI provider implementations.

#### Purpose
- Holds parameters for AI generation.
- Configures model path, sampling, retry, and other settings.
- Provides getters and setters for these parameters.

#### Type
- Class
- Public
- Final
- Lombok `@ToString`
- `@Nullable` from `jspecify`

#### Input
- Constructor: Initializes default values.
- Method parameters: Various configuration settings.

#### Output
- Return types: Various configuration settings.
- Produced state: Stores configuration settings in instance fields.
- Mutated fields: All instance fields except `calibration`.
- Written resources: None.
- Side effects: None.

#### Core logic
- Initializes default values.
- Provides methods to get and set configuration parameters.
- Manages mutable state through setters.

#### Public API
- `getModelPath() -> String`: Returns model file path.
- `setModelPath(String)`: Sets model file path.
- `getContextSize() -> int`: Returns context window size.
- `setContextSize(int)`: Sets context window size.
- `getMaxOutputTokens() -> int`: Returns max output tokens.
- `setMaxOutputTokens(int)`: Sets max output tokens.
- `getTemperature() -> float`: Returns sampling temperature.
- `setTemperature(float)`: Sets sampling temperature.
- `getThreads() -> int`: Returns CPU threads.
- `setThreads(int)`: Sets CPU threads.
- `getCharsPerToken() -> int`: Returns chars per token.
- `setCharsPerToken(int)`: Sets chars per token.
- `getMaxInputChars() -> int`: Returns max input chars.
- `setMaxInputChars(int)`: Sets max input chars.
- `isWarnOnTrim() -> boolean`: Returns trim warning setting.
- `setWarnOnTrim(boolean)`: Sets trim warning setting.
- `getTopP() -> float`: Returns nucleus sampling threshold.
- `setTopP(float)`: Sets nucleus sampling threshold.
- `getTopK() -> int`: Returns top-k sampling limit.
- `setTopK(int)`: Sets top-k sampling limit.
- `getMinP() -> float`: Returns min-p sampling threshold.
- `setMinP(float)`: Sets min-p sampling threshold.
- `getTopNSigma() -> float`: Returns top-n-sigma sampling threshold.
- `setTopNSigma(float)`: Sets top-n-sigma sampling threshold.
- `getRepeatPenalty() -> float`: Returns repetition penalty.
- `setRepeatPenalty(float)`: Sets repetition penalty.
- `getChatTemplateEnableThinking() -> Boolean`: Returns chat template thinking mode.
- `setChatTemplateEnableThinking(Boolean)`: Sets chat template thinking mode.
- `isCachePrompt() -> boolean`: Returns prompt caching setting.
- `setCachePrompt(boolean)`: Sets prompt caching setting.
- `isSwaFull() -> boolean`: Returns full-size SWA KV cache setting.
- `setSwaFull(boolean)`: Sets full-size SWA KV cache setting.
- `getCacheReuse() -> int`: Returns KV prefix reuse chunk size.
- `setCacheReuse(int)`: Sets KV prefix reuse chunk size.
- `getGpuLayers() -> int`: Returns GPU layers.
- `setGpuLayers(int)`: Sets GPU layers.
- `getCpuMoeLayers() -> int`: Returns CPU MoE layers.
- `setCpuMoeLayers(int)`: Sets CPU MoE layers.
- `getCpuFfnLayers() -> int`: Returns CPU FFN layers.
- `setCpuFfnLayers(int)`: Sets CPU FFN layers.
- `getKvUnifiedPerSlot() -> int`: Returns KV context cap per slot.
- `setKvUnifiedPerSlot(int)`: Sets KV context cap per slot.
- `getLazyMode() -> String`: Returns tensor-read laziness setting.
- `setLazyMode(String)`: Sets tensor-read laziness setting.
- `getRepeatLastN() -> int`: Returns repeat-penalty window.
- `setRepeatLastN(int)`: Sets repeat-penalty window.
- `getCacheTypeK() -> String`: Returns KV-cache data type for K.
- `setCacheTypeK(String)`: Sets KV-cache data type for K.
- `getCacheTypeV() -> String`: Returns KV-cache data type for V.
- `setCacheTypeV(String)`: Sets KV-cache data type for V.
- `isFlashAttn() -> boolean`: Returns Flash Attention setting.
- `setFlashAttn(boolean)`: Sets Flash Attention setting.
- `getBatchSize() -> int`: Returns logical batch size.
- `setBatchSize(int)`: Sets logical batch size.
- `getUbatchSize() -> int`: Returns physical batch size.
- `setUbatchSize(int)`: Sets physical batch size.
- `getThreadsBatch() -> int`: Returns batch thread count.
- `setThreadsBatch(int)`: Sets batch thread count.
- `getMainGpu() -> int`: Returns primary GPU index.
- `setMainGpu(int)`: Sets primary GPU index.
- `getDevices() -> String`: Returns device selection.
- `setDevices(String)`: Sets device selection.
- `getProvider() -> String`: Returns AI provider.
- `setProvider(String)`: Sets AI provider.
- `getRemoteModel() -> String`: Returns remote model ID.
- `setRemoteModel(String)`: Sets remote model ID.
- `getBaseUrl() -> String`: Returns base URL.
- `setBaseUrl(String)`: Sets base URL.
- `getApiKeyEnv() -> String`: Returns API key environment variable.
- `setApiKeyEnv(String)`: Sets API key environment variable.
- `getReasoningEffort() -> String`: Returns reasoning effort level.
- `setReasoningEffort(String)`: Sets reasoning effort level.
- `getReasoningBudgetTokens() -> int`: Returns reasoning budget tokens.
- `setReasoningBudgetTokens(int)`: Sets reasoning budget tokens.
- `getDryMultiplier() -> float`: Returns DRY sampling multiplier.
- `setDryMultiplier(float)`: Sets DRY sampling multiplier.
- `getDryBase() -> float`: Returns DRY base.
- `setDryBase(float)`: Sets DRY base.
- `getDryAllowedLength() -> int`: Returns DRY allowed length.
- `setDryAllowedLength(int)`: Sets DRY allowed length.
- `getDryPenaltyLastN() -> int`: Returns DRY penalty look-back window.
- `setDryPenaltyLastN(int)`: Sets DRY penalty look-back window.
- `getDrySequenceBreakers() -> List<String>`: Returns unmodifiable list of DRY sequence breakers.
- `setDrySequenceBreakers(List<String>)`: Sets DRY sequence breakers.
- `getStopStrings() -> List<String>`: Returns unmodifiable list of stop strings.
- `setStopStrings(List<String>)`: Sets stop strings.
- `getCalibration() -> AiCalibration`: Returns per-machine timing calibration.
- `setCalibration(AiCalibration)`: Sets per-machine timing calibration.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `@Nullable` annotations handle potential null values.

#### Concurrency
- None.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:02:24Z
- G: 
- A: 0.0.0
- X: file
---
> This class encapsulates all mutable parameters required to configure a single AI generation step, bridging configuration layers to AI provider implementations.

#### Purpose
* Configuration container for AI generation parameters.
* Holds mutable state for AI generation settings.

#### Type
class AiGenerationConfig; @ToString

#### Input
* Constructor: Default initialization.
* Method parameters: All setters accept various types (String, int, float, boolean, List<String>, AiCalibration).
* Injected dependencies: None explicitly shown, relies on static defaults and runtime environment (Runtime.getRuntime().availableProcessors()).

#### Output
* Return types: Various getters return configured values (String, int, float, boolean, List<String>, AiCalibration).
* Mutated fields: All setter methods modify instance fields.

#### Core logic
* Initialization sets instance fields to static default values.
* Getters provide read access to configured parameters.
* Setters allow overriding default parameters for specific generation runs.

#### Public API
getModelPath(String) -> String + Retrieves the GGUF model file path.
setModelPath(String) -> void + Sets the GGUF model file path.
getContextSize() -> int + Returns the context window size in tokens.
setContextSize(int) -> void + Sets the context window size in tokens.
getMaxOutputTokens() -> int + Returns the maximum output tokens per call.
setMaxOutputTokens(int) -> void + Sets the maximum output tokens per call.
getTemperature() -> float + Returns the sampling temperature.
setTemperature(float) -> void + Sets the sampling temperature.
getThreads() -> int + Returns the number of CPU threads used.
setThreads(int) -> void + Sets the number of CPU threads used.
getCharsPerToken() -> int + Returns characters per token ratio.
setCharsPerToken(int) -> void + Sets the characters per token ratio.
getMaxInputChars() -> int + Returns the maximum input characters fed.
setMaxInputChars(int) -> void + Sets the maximum input characters fed.
isWarnOnTrim() -> boolean + Checks if prompt trimming warnings are enabled.
setWarnOnTrim(boolean) -> void + Enables or disables prompt trimming warnings.
getTopP() -> float + Returns the nucleus-sampling probability threshold.
setTopP(float) -> void + Sets the nucleus-sampling probability threshold.
getTopK() -> int + Returns the top-k sampling limit.
setTopK(int) -> void + Sets the top-k sampling limit.
getRepeatPenalty() -> float + Returns the repetition penalty.
setRepeatPenalty(float) -> void + Sets the repetition penalty.
getMinP() -> float + Returns the min-p sampling threshold.
setMinP(float) -> void + Sets the min-p sampling threshold.
getTopNSigma() -> float + Returns the top-n-sigma sampling threshold.
setTopNSigma(float) -> void + Sets the top-n-sigma sampling threshold.
getChatTemplateEnableThinking() -> Boolean + Retrieves chat-template thinking mode setting.
setChatTemplateEnableThinking(Boolean) -> void + Sets the model's chat-template thinking mode.
isCachePrompt() -> boolean + Checks if prompt caching is enabled.
setCachePrompt(boolean) -> void + Enables or disables prompt-prefix KV reuse.
isSwaFull() -> boolean + Checks if the full-size SWA KV cache is kept.
setSwaFull(boolean) -> void + Enables or disables full SWA KV cache.
getCacheReuse() -> int + Returns the KV prefix-reuse minimum chunk size.
setCacheReuse(int) -> void + Sets the KV prefix-reuse minimum chunk size.
getGpuLayers() -> int + Returns the number of model layers offloaded to GPU.
setGpuLayers(int) -> void + Sets the number of model layers offloaded to GPU.
getCpuMoeLayers() -> int + Returns the number of MoE layers kept on CPU.
setCpuMoeLayers(int) -> void + Sets the number of MoE layers kept on CPU.
getSeed() -> int + Returns the RNG seed used for generation.
setSeed(int) -> void + Sets the RNG seed used for generation.
getCpuFfnLayers() -> int + Returns the number of FFN layers kept on CPU.
setCpuFfnLayers(int) -> void + Sets the number of FFN layers kept on CPU.
getKvUnifiedPerSlot() -> int + Returns the per-slot unified KV context cap.
setKvUnifiedPerSlot(int) -> void + Sets the per-slot unified KV context cap.
getLazyMode() -> String + Returns the tensor-read laziness setting.
setLazyMode(String) -> void + Sets the tensor-read laziness mode.
getRepeatLastN() -> int + Returns the repeat-penalty window size.
setRepeatLastN(int) -> void + Sets the repeat-penalty window size.
getCacheTypeK() -> String + Returns the KV-cache data type for K.
setCacheTypeK(String) -> void + Sets the KV-cache data type for K.
getCacheTypeV() -> String + Returns the KV-cache data type for V.
setCacheTypeV(String) -> void + Sets the KV-cache data type for V.
isFlashAttn() -> boolean + Checks if Flash Attention is enabled.
setFlashAttn(boolean) -> void + Enables or disables Flash Attention.
getBatchSize() -> int + Returns the logical batch size.
setBatchSize(int) -> void + Sets the logical batch size.
getUbatchSize() -> int + Returns the physical batch size.
setUbatchSize(int) -> void + Sets the physical batch size.
getThreadsBatch() -> int + Returns the batch/prompt-processing thread count.
setThreadsBatch(int) -> void + Sets the batch/prompt-processing thread count.
getMainGpu() -> int + Returns the primary GPU index.
setMainGpu(int) -> void + Sets the primary GPU index.
getDevices() -> String + Returns the comma-separated device list.
setDevices(String) -> void + Sets the device selection list.
getProvider() -> String + Returns the AI provider of this model.
setProvider(String) -> void + Sets the AI provider of this model.
getRemoteModel() -> String + Returns the model ID for remote provider.
setRemoteModel(String) -> void + Sets the model ID for remote provider.
getBaseUrl() -> String + Returns the base URL of the OpenAI-compatible API.
setBaseUrl(String) -> void + Sets the base URL of the OpenAI-compatible API.
getApiKeyEnv() -> String + Returns the environment variable name for API key.
setApiKeyEnv(String) -> void + Sets the environment variable name for API key.
getReasoningEffort() -> String + Returns the gpt-oss reasoning-effort level.
setReasoningEffort(String) -> void + Sets the gpt-oss reasoning-effort level.
getReasoningBudgetTokens() -> int + Returns the reasoning/think-token budget.
setReasoningBudgetTokens(int) -> void + Sets the reasoning/think-token budget.
getDryMultiplier() -> float + Returns the DRY sampling multiplier.
setDryMultiplier(float) -> void + Sets the DRY sampling multiplier.
getDryBase() -> float + Returns the DRY base.
setDryBase(float) -> void + Sets the DRY base.
getDryAllowedLength() -> int + Returns the DRY allowed length.
setDryAllowedLength(int) -> void + Sets the DRY allowed length.
getDryPenaltyLastN() -> int + Returns the DRY penalty look-back window.
setDryPenaltyLastN(int) -> void + Sets the DRY penalty look-back window.
getDrySequenceBreakers() -> List<String> + Retrieves unmodifiable list of DRY sequence breakers.
setDrySequenceBreakers(List<String>) -> void + Sets the DRY sequence breakers.
getStopStrings() -> List<String> + Retrieves unmodifiable list of stop strings.
setStopStrings(List<String>) -> void + Sets the list of stop strings.
getCalibration() -> AiCalibration + Retrieves per-machine timing calibration.
setCalibration(AiCalibration) -> void + Sets the per-machine timing calibration.

#### Dependencies
java.util.ArrayList, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiCalibration

#### Exceptions / Errors
* Null handling present in setters/getters for optional fields (e.g., modelPath, calibration).
* Field validation implied by setter documentation (e.g., `kvUnifiedPerSlot` must be positive if set).

#### Concurrency
* Instance fields are mutable JavaBeans, managed by identity, not thread-safe by default.
* No explicit synchronization mechanisms are present.

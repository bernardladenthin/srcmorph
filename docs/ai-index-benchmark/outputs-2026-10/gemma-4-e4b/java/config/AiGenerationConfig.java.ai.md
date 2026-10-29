### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:55:41Z
- G: 
- A: 0.0.0
- X: file
---
> This class encapsulates all mutable parameters required to configure a single AI generation process, bridging configuration layers to AI provider implementations.

#### Purpose
* Configuration container for AI generation parameters.
* Holds state for a single AI generation step.

#### Type
class AiGenerationConfig; @ToString

#### Input
* Constructor: Default initialization.
* Methods: Setters for all configuration fields (e.g., `setModelPath(String)`, `setContextSize(int)`).
* Dependencies: None explicitly injected; relies on static defaults.

#### Output
* Methods: Getters returning configured values (e.g., `getContextSize() -> int`, `getTopP() -> float`).
* Side Effects: None observed during configuration retrieval.

#### Core logic
* Initialization applies numerous static defaults for AI generation parameters (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TOP_P`).
* Provides setters to override these defaults for specific generation runs.
* Implements getters to expose the current state of the configuration.

#### Public API
getModelPath(String) -> String + Returns GGUF model file path.
setModelPath(String) -> void + Sets the GGUF model file path.
getContextSize() -> int + Returns context window size in tokens.
setContextSize(int) -> void + Sets the context window size in tokens.
getMaxOutputTokens() -> int + Returns maximum output tokens.
setMaxOutputTokens(int) -> void + Sets the maximum output tokens.
getTemperature() -> float + Returns sampling temperature.
setTemperature(float) -> void + Sets the sampling temperature.
getThreads() -> int + Returns number of CPU threads.
setThreads(int) -> void + Sets the number of CPU threads.
getCharsPerToken() -> int + Returns chars-per-token ratio.
setCharsPerToken(int) -> void + Sets the number of characters per token.
getMaxInputChars() -> int + Returns maximum input characters.
setMaxInputChars(int) -> void + Sets the maximum number of input characters.
isWarnOnTrim() -> boolean + Checks if trim warnings are enabled.
setWarnOnTrim(boolean) -> void + Enables or disables trim warnings.
getTopP() -> float + Returns nucleus-sampling probability threshold.
setTopP(float) -> void + Sets the nucleus-sampling probability threshold.
getTopK() -> int + Returns top-k sampling limit.
setTopK(int) -> void + Sets the top-k sampling limit.
getRepeatPenalty() -> float + Returns repetition penalty.
setRepeatPenalty(float) -> void + Sets the repetition penalty.
getMinP() -> float + Returns min-p sampling threshold.
setMinP(float) -> void + Sets the min-p sampling threshold.
getTopNSigma() -> float + Returns top-n-sigma sampling threshold.
setTopNSigma(float) -> void + Sets the top-n-sigma sampling threshold.
getChatTemplateEnableThinking() -> Boolean + Returns chat-template thinking mode.
setChatTemplateEnableThinking(Boolean) -> void + Sets the model's chat-template thinking mode.
isCachePrompt() -> boolean + Checks if prompt caching is enabled.
isSwaFull() -> boolean + Checks if full SWA KV cache is kept.
setSwaFull(boolean) -> void + Sets whether full SWA KV cache is kept.
getCacheReuse() -> int + Returns KV prefix-reuse minimum chunk size.
setCacheReuse(int) -> void + Sets the KV prefix-reuse minimum chunk size.
getGpuLayers() -> int + Returns number of model layers to offload.
setGpuLayers(int) -> void + Sets the number of model layers to offload.
getCpuMoeLayers() -> int + Returns number of MoE layers on CPU.
setCpuMoeLayers(int) -> void + Sets the number of MoE layers on CPU.
getCpuFfnLayers() -> int + Returns number of FFN layers on CPU.
setCpuFfnLayers(int) -> void + Sets the number of FFN layers on CPU.
getKvUnifiedPerSlot() -> int + Returns per-slot unified KV context cap.
setKvUnifiedPerSlot(int) -> void + Sets the per-slot unified KV context cap.
getLazyMode() -> String + Returns tensor-read laziness mode.
setLazyMode(String) -> void + Sets the tensor-read laziness mode.
getRepeatLastN() -> int + Returns repeat-penalty window.
setRepeatLastN(int) -> void + Sets the repeat-penalty window.
getCacheTypeK() -> String + Returns KV-cache data type for K.
setCacheTypeK(String) -> void + Sets the KV-cache data type for K.
getCacheTypeV() -> String + Returns KV-cache data type for V.
setCacheTypeV(String) -> void + Sets the KV-cache data type for V.
isFlashAttn() -> boolean + Checks if Flash Attention is enabled.
setFlashAttn(boolean) -> void + Enables or disables Flash Attention.
getBatchSize() -> int + Returns the logical batch size.
setBatchSize(int) -> void + Sets the logical batch size.
getUbatchSize() -> int + Returns the physical batch size.
setUbatchSize(int) -> void + Sets the physical batch size.
getThreadsBatch() -> int + Returns batch/prompt-processing thread count.
setThreadsBatch(int) -> void + Sets the batch/prompt-processing thread count.
getMainGpu() -> int + Returns the primary GPU index.
setMainGpu(int) -> void + Sets the primary GPU index.
getDevices() -> String + Returns the device selection string.
setDevices(String) -> void + Sets the device selection string.
getProvider() -> String + + Returns the AI provider of this model.
setProvider(String) -> void + Sets the AI provider of this model.
getRemoteModel() -> String + Returns the model ID for remote provider.
setRemoteModel(String) -> void + Sets the model ID for remote provider.
getBaseUrl() -> String + Returns the base URL of the API.
setBaseUrl(String) -> void + Sets the base URL of the API.
getApiKeyEnv() -> String + Returns API key environment variable name.
setApiKeyEnv(String) -> void + Sets the API key environment variable name.
getReasoningEffort() -> String + Returns gpt-oss reasoning-effort level.
setReasoningEffort(String) -> void + Sets the gpt-oss reasoning-effort level.
getReasoningBudgetTokens() -> int + Returns reasoning/think-token budget.
setReasoningBudgetTokens(int) -> void + Sets the reasoning/think-token budget.
getDryMultiplier() -> float + Returns DRY sampling multiplier.
setDryMultiplier(float) -> void + Sets the DRY sampling multiplier.
getDryBase() -> float + Returns DRY base.
setDryBase(float) -> void + Sets the DRY base.
getDryAllowedLength() -> int + Returns DRY allowed length.
setDryAllowedLength(int) -> void + Sets the DRY allowed length.
getDryPenaltyLastN() -> int + Returns DRY penalty look-back window.
setDryPenaltyLastN(int) -> void + Sets the DRY penalty look-back window.
getDrySequenceBreakers() -> List<String> + Returns unmodifiable DRY sequence breakers.
setDrySequenceBreakers(List<String>) -> void + Sets the DRY sequence breakers.
getStopStrings() -> List<String> + Returns unmodifiable stop strings.
setStopStrings(List<String>) -> void + Sets the list of stop strings.
getCalibration() -> AiCalibration + Returns per-machine timing calibration.
setCalibration(AiCalibration) -> void + Sets the per-machine timing calibration.

#### Dependencies
java.util.ArrayList, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, AiCalibration

#### Exceptions / Errors
* Null handling present for `modelPath`, `chatTemplateEnableThinking`, `calibration`, and list setters.
* Configuration setters handle `null` inputs by resetting to default or empty state.

#### Concurrency
* Class is mutable (JavaBeans pattern).
* No explicit synchronization mechanisms are present in the provided code.

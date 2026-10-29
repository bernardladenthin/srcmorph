### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:55:25Z
- G: 
- A: 0.0.0
- X: file
---
> This class encapsulates all mutable parameters required to configure a single AI generation step, bridging configuration layers to AI provider implementations.

#### Purpose
* Holds configuration parameters for AI generation steps.
* Manages model path, sampling settings, and provider-specific parameters.

#### Type
class AiGenerationConfig; @ToString; @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})

#### Input
* Constructor: Default initialization.
* Methods: Setters for all configuration fields (e.g., `setModelPath(String)`, `setContextSize(int)`).
* Dependencies: Injected/Used constants (e.g., `DEFAULT_CONTEXT_SIZE`).

#### Output
* Methods: Getters for all configuration fields (e.g., `getModelPath() -> String`).
* State: Mutates internal fields upon setter calls.

#### Core logic
* Provides default values for numerous AI generation parameters (e.g., `DEFAULT_TEMPERATURE`, `DEFAULT_TOP_P`).
* Stores and manages configuration state for model path, token limits, sampling strategies, and hardware offloading settings.
* Exposes methods to retrieve the current state of all configured parameters.

#### Public API
getModelPath() -> String: Retrieves the GGUF model file path.
setModelPath(String) -> void: Sets the GGUF model file path.
getContextSize() -> int: Returns the context window size in tokens.
setContextSize(int) -> void: Sets the context window size in tokens.
getMaxOutputTokens() -> int: Returns the maximum output tokens per call.
setMaxOutputTokens(int) -> void: Sets the maximum output tokens per call.
getTemperature() -> float: Returns the sampling temperature.
setTemperature(float) -> void: Sets the sampling temperature.
getThreads() -> int: Returns the number of CPU threads used.
setThreads(int) -> void: Sets the number of CPU threads used.
getCharsPerToken() -> int: Returns chars-per-token ratio for input trimming.
setCharsPerToken(int) -> void: Sets the chars-per-token ratio.
getMaxInputChars() -> int: Returns maximum input characters fed to prompt.
setMaxInputChars(int) -> void: Sets the maximum input characters fed to prompt.
isWarnOnTrim() -> boolean: Checks if prompt trimming warnings are enabled.
setWarnOnTrim(boolean) -> void: Enables or disables prompt trimming warnings.
getTopP() -> float: Returns the nucleus-sampling probability threshold.
setTopP(float) -> void: Sets the nucleus-sampling probability threshold.
getTopK() -> int: Returns the top-k sampling limit.
setTopK(int) -> void: Sets the top-k sampling limit.
getRepeatPenalty() -> float: Returns the repetition penalty factor.
setRepeatPenalty(float) -> void: Sets the repetition penalty factor.
getMinP() -> float: Returns the min-p sampling threshold.
setMinP(float) -> void: Sets the min-p sampling threshold.
getTopNSigma() -> float: Returns the top-n-sigma sampling threshold.
setTopNSigma(float) -> void: Sets the top-n-sigma sampling threshold.
getChatTemplateEnableThinking() -> Boolean: Gets chat-template thinking mode setting.
setChatTemplateEnableThinking(Boolean) -> void: Sets the model's chat-template thinking mode.
isCachePrompt() -> boolean: Checks if prompt caching is enabled.
isSwaFull() -> boolean: Checks if full SWA KV cache is kept.
setSwaFull(boolean) -> void: Sets whether full SWA KV cache is kept.
getCacheReuse() -> int: Returns KV prefix-reuse minimum chunk size.
setCacheReuse(int) -> void: Sets the KV prefix-reuse minimum chunk size.
getGpuLayers() -> int: Returns the number of model layers offloaded to GPU.
setGpuLayers(int) -> void: Sets the number of model layers offloaded to GPU.
getCpuMoeLayers() -> int: Returns the number of MoE layers kept on CPU.
setCpuMoeLayers(int) -> void: Sets the number of MoE layers kept on CPU.
getSeed() -> int: Returns the RNG seed used for generation.
setSeed(int) -> void: Sets the RNG seed used for generation.
getCpuFfnLayers() -> int: Returns the number of FFN layers kept on CPU.
setCpuFfnLayers(int) -> void: Sets the number of FFN layers kept on CPU.
getKvUnifiedPerSlot() -> int: Returns the per-slot unified KV context cap.
setKvUnifiedPerSlot(int) -> void: Sets the per-slot unified KV context cap.
getLazyMode() -> String: Returns the tensor-read laziness mode.
setLazyMode(String) -> void: Sets the tensor-read laziness mode.
getRepeatLastN() -> int: Returns the repeat-penalty window size.
setRepeatLastN(int) -> void: Sets the repeat-penalty window size.
getCacheTypeK() -> String: Returns the KV-cache data type for K.
setCacheTypeK(String) -> void: Sets the KV-cache data type for K.
getCacheTypeV() -> String: Returns the KV-cache data type for V.
setCacheTypeV(String) -> void: Sets the KV-cache data type for V.
isFlashAttn() -> boolean: Checks if Flash Attention is enabled.
setFlashAttn(boolean) -> void: Enables or disables Flash Attention.
getBatchSize() -> int: Returns the logical batch size.
setBatchSize(int) -> void: Sets the logical batch size.
getUbatchSize() -> int: Returns the physical batch size.
setUbatchSize(int) -> void: Sets the physical batch size.
getThreadsBatch() -> int: Returns the batch/prompt-processing thread count.
setThreadsBatch(int) -> void: Sets the batch/prompt-processing thread count.
getMainGpu() -> int: Returns the primary GPU index.
setMainGpu(int) -> void: Sets the primary GPU index.
getDevices() -> String: Returns the device selection string.
setDevices(String) -> void: Sets the device selection string.
getProvider() -> String: Returns the AI provider of this model.
setProvider(String) -> void: Sets the AI provider of this model.
getRemoteModel() -> String: Returns the model ID for remote providers.
setRemoteModel(String) -> void: Sets the model ID for remote providers.
getBaseUrl() -> String: Returns the base URL for OpenAI-compatible API.
setBaseUrl(String) -> void: Sets the base URL for OpenAI-compatible API.
getApiKeyEnv() -> String: Returns the environment variable name for API key.
setApiKeyEnv(String) -> void: Sets the environment variable name for API key.
getReasoningEffort() -> String: Returns the gpt-oss reasoning-effort level.
setReasoningEffort(String) -> void: Sets the gpt-oss reasoning-effort level.
getReasoningBudgetTokens() -> int: Returns the reasoning/think-token budget.
setReasoningBudgetTokens(int) -> void: Sets the reasoning/think-token budget.
getDryMultiplier() -> float: Returns the DRY sampling multiplier.
setDryMultiplier(float) -> void: Sets the DRY sampling multiplier.
getDryBase() -> float: Returns the DRY base for repetition penalty.
setDryBase(float) -> void: Sets the DRY base.
getDryAllowedLength() -> int: Returns the DRY allowed length.
setDryAllowedLength(int) -> void: Sets the DRY allowed length.
getDryPenaltyLastN() -> int: Returns the DRY penalty look-back window.
setDryPenaltyLastN(int) -> void: Sets the DRY penalty look-back window.
getDrySequenceBreakers() -> List<String>: Retrieves unmodifiable DRY sequence breakers.
setDrySequenceBreakers(List<String>) -> void: Sets the DRY sequence breakers.
getStopStrings() -> List<String>: Retrieves unmodifiable configured stop strings.
setStopStrings(List<String>) -> void: Sets the list of stop strings.
getCalibration() -> AiCalibration: Retrieves per-machine timing calibration.
setCalibration(AiCalibration) -> void: Sets the per-machine timing calibration.

#### Dependencies
java.util.ArrayList, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiCalibration

#### Exceptions / Errors
* Null handling present for `modelPath`, `chatTemplateEnableThinking`, `calibration`, and list setters.
* Input validation implied for batch sizes and KV caps (must be positive when set).

#### Concurrency
* Instance fields are mutable JavaBeans; thread safety is not guaranteed by the class itself.
* Lombok's `@ToString` is used for diagnostic logging.

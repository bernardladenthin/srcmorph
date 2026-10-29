### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:22:28Z
- G: 
- A: 0.0.0
- X: file
---
> This class serves as a mutable configuration container for AI generation parameters, bridging Maven configuration with AI provider implementations.

#### Purpose
* Holds all parameters for a single AI generation step (model path, sampling, retry policy, input limits, stop strings).
* Acts as a JavaBean for population by the Maven plugin framework.

#### Type
class; public.

#### Input
* Constructor: No-op.
* Setters: Various types including `String`, `int`, `float`, `boolean`, `Boolean`, `List<String>`, and `AiCalibration`.

#### Output
* Getters: Provide access to all configuration fields.
* State: Mutates internal fields for model path, context size, sampling parameters, and hardware-specific settings (GPU layers, threads).

#### Core logic
* Provides default values for all AI parameters (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`, `DEFAULT_TOP_P`).
* Manages hardware-specific defaults like `DEFAULT_THREADS` (calculated based on available processors).
* Handles "tri-state" logic for `chatTemplateEnableThinking` (null = omit kwarg, true/false = forward verbatim).
* Provides logic for DRY (Don't Repeat Yourself) sampling parameters.
* Manages list-based configurations for `stopStrings` and `drySequenceBreakers`.

#### Public API
* `AiGenerationConfig()` -> void (Constructor)
* `getModelPath()` -> `String` (Returns model path)
* `setModelPath(String)` -> void (Sets model path)
* `getContextSize()` -> `int` (Returns context size)
* `setContextSize(int)` -> void (Sets context size)
* `getMaxOutputTokens()` -> `int` (Returns max output tokens)
* `setMaxOutputTokens(int)` -> void (Sets max output tokens)
* `getTemperature()` -> `float` (Returns temperature)
* `setTemperature(float)` -> void (Sets temperature)
* `getThreads()` -> `int` (Returns CPU threads)
* `setThreads(int)` -> void (Sets CPU threads)
* `getCharsPerToken()` -> `int` (Returns chars per token)
* `setCharsPerToken(int)` -> void (Sets chars per token)
* `getMaxInputChars()` -> `int` (Returns max input chars)
* `setMaxInputChars(int)` -> void (Sets max input chars)
* `isWarnOnTrim()` -> `boolean` (Returns trim warning status)
* `setWarnOnTrim(boolean)` -> void (Sets trim warning status)
* `getTopP()` -> `float` (Returns top-p)
* `setTopP(float)` -> void (Sets top-p)
* `getTopK()` -> `int` (Returns top-k)
* `setTopK(int)` -> void (Sets top-k)
* `getRepeatPenalty()` -> `float` (Returns repeat penalty)
* `setRepeatPenalty(float)` -> void (Sets repeat penalty)
* `getMinP()` -> `float` (Returns min-p)
* `setMinP(float)` -> void (Sets min-p)
* `getTopNSigma()` -> `float` (Returns top-n-sigma)
* `setTopNSigma(float)` -> void (Sets top-n-sigma)
* `getChatTemplateEnableThinking()` -> `Boolean` (Returns thinking mode)
* `setChatTemplateEnableThinking(Boolean)` -> void (Sets thinking mode)
* `isCachePrompt()` -> `boolean` (Returns prompt cache status)
* `isSwaFull()` -> `boolean` (Returns SWA full status)
* `setSwaFull(boolean)` -> void (Sets SWA full status)
* `getCacheReuse()` -> `int` (Returns cache reuse size)
* `setCacheReuse(int)` -> void (Sets cache reuse size)
* `getGpuLayers()` -> `int` (Returns GPU layers)
* `setGpuLayers(int)` -> void (Sets GPU layers)
* `getCpuMoeLayers()` -> `int` (Returns CPU MoE layers)
* `setCpuMoeLayers(int)` -> void (Sets CPU MoE layers)
* `getSeed()` -> `int` (Returns RNG seed)
* `setSeed(int)` -> void (Sets RNG seed)
* `getCpuFfnLayers()` -> `int` (Returns CPU FFN layers)
* `setCpuFfnLayers(int)` -> void (Sets CPU FFN layers)
* `getKvUnifiedPerSlot()` -> `int` (Returns KV unified cap)
* `setKvUnifiedPerSlot(int)` -> void (Sets KV unified cap)
* `getLazyMode()` -> `String` (Returns tensor lazy mode)
* `setLazyMode(String)` -> void (Sets tensor lazy mode)
* `getRepeatLastN()` -> `int` (Returns repeat window)
* `setRepeatLastN(int)` -> void (Sets repeat window)
* `getCacheTypeK()` -> `String` (Returns K cache type)
* `setCacheTypeK(String)` -> void (Sets K cache type)
* `getCacheTypeV()` -> `String` (Returns V cache type)
* `setCacheTypeV(String)` -> void (Sets V cache type)
* `isFlashAttn()` -> `boolean` (Returns flash attn status)
* `setFlashAttn(boolean)` -> void (Sets flash attn status)
* `getBatchSize()` -> `int` (Returns logical batch size)
* `setBatchSize(int)` -> void (Sets logical batch size)
* `getUbatchSize()` -> `int` (Returns physical batch size)
* `setUbatchSize(int)` -> void (Sets physical batch size)
* `getThreadsBatch()` -> `int` (Returns batch thread count)
* `setThreadsBatch(int)` -> void (Sets batch thread count)
* `getMainGpu()` -> `int` (Returns main GPU index)
* `setMainGpu(int)` -> void (Sets main GPU index)
* `getDevices()` -> `String` (Returns device list)
* `setDevices(String)` -> void (Sets device list)
* `getProvider()` -> `String` (Returns AI provider)
* `setProvider(String)` -> void (Sets AI provider)
* `getRemoteModel()` -> `String` (Returns remote model ID)
* `setRemoteModel(String)` -> void (Sets remote model ID)
* `getBaseUrl()` -> `String` (Returns base URL)
* `setBaseUrl(String)` -> void (Sets base URL)
* `getApiKeyEnv()` -> `String` (Returns API key env name)
* `setApiKeyEnv(String)` -> void (Sets API key env name)
* `setCachePrompt(boolean)` -> void (Sets prompt cache status)
* `getReasoningEffort()` -> `String` (Returns reasoning effort)
* `setReasoningEffort(String)` -> void (Sets reasoning effort)
* `getReasoningBudgetTokens()` -> `int` (Returns reasoning budget)
* `setReasoningBudgetTokens(int)` -> void (Sets reasoning budget)
* `getDryMultiplier()` -> `float` (Returns DRY multiplier)
* `setDryMultiplier(float)` -> void (Sets DRY multiplier)
* `getDryBase()` -> `float` (Returns DRY base)
* `setDryBase(float)` -> void (Sets DRY base)
* `getDryAllowedLength()` -> `int` (Returns DRY allowed length)
* `setDryAllowedLength(int)` -> void (Sets DRY allowed length)
* `getDryPenaltyLastN()` -> `int` (Returns DRY penalty window)
* `setDryPenaltyLastN(int)` -> void (Sets DRY penalty window)
* `getDrySequenceBreakers()` -> `List<String>` (Returns DRY breakers)
* `setDrySequenceBreakers(List<String>)` -> void (Sets DRY breakers)
* `getStopStrings()` -> `List<String>` (Returns stop strings)
* `setStopStrings(List<String>)` -> void (Sets stop strings)
* `getCalibration()` -> `AiCalibration` (Returns calibration)
* `setCalibration(AiCalibration)` -> void (Sets calibration)

#### Dependencies
* java.util.ArrayList
* java.util.Collections
* java.util.List
* lombok.ToString
* org.jspecify.annotations.Nullable

#### Exceptions / Errors
* Handles null inputs in setters by reverting to default values (e.g., `setLazyMode`, `setProvider`).
* Uses `@Nullable` for `modelPath`, `chatTemplateEnableThinking`, and `calibration`.

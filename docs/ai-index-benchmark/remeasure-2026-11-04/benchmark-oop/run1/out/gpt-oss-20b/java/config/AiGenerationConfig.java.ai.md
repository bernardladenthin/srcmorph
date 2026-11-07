### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:38Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, including model path, sampling settings, prompt‑caching flags, GPU/offload options, and provider details.

#### Purpose
- Stores configuration for a single AI generation step.
- Exposes getters/setters for Maven plugin and provider layers.
- Provides default constants for all parameters.

#### Type
- `public class AiGenerationConfig`  
  - Modifiers: `public`  
  - Annotations: `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`, `@ToString`  
  - Fields: 73 private fields (including 2 `List<String>` and one `AiCalibration`), all mutable.  
  - Constructor: public no‑arg constructor (no-op).  
  - No supertype or interfaces.

#### Input
- Constructor: none.  
- Setters accept:
  - `String` (modelPath, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, devices, lazyMode, cacheTypeK, cacheTypeV, etc.)
  - `int` (contextSize, maxOutputTokens, threads, charsPerToken, maxInputChars, topK, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, repeatLastN, batchSize, ubatchSize, threadsBatch, mainGpu, reasoningBudgetTokens, dryAllowedLength, dryPenaltyLastN)
  - `float` (temperature, topP, minP, topNSigma, repeatPenalty, dryMultiplier, dryBase)
  - `boolean` (warnOnTrim, cachePrompt, swaFull, flashAttn, cacheReuse, etc.)
  - `List<String>` (drySequenceBreakers, stopStrings)
  - `AiCalibration` (calibration)

#### Output
- Getters return current field values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- No side effects beyond mutating internal state.

#### Core logic
- All setters simply assign the provided value to the corresponding field.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace `null` with default constants.
- `setDrySequenceBreakers` and `setStopStrings` replace `null` with empty `ArrayList`.
- No additional computation or validation beyond these assignments.

#### Public API
- `AiGenerationConfig()` -> void  
- `String getModelPath()` -> model path  
- `void setModelPath(String)` -> set model path  
- `int getContextSize()` -> context size  
- `void setContextSize(int)` -> set context size  
- `int getMaxOutputTokens()` -> max output tokens  
- `void setMaxOutputTokens(int)` -> set max output tokens  
- `float getTemperature()` -> temperature  
- `void setTemperature(float)` -> set temperature  
- `int getThreads()` -> CPU threads  
- `void setThreads(int)` -> set CPU threads  
- `int getCharsPerToken()` -> chars per token  
- `void setCharsPerToken(int)` -> set chars per token  
- `int getMaxInputChars()` -> max input chars  
- `void setMaxInputChars(int)` -> set max input chars  
- `boolean isWarnOnTrim()` -> trim warning flag  
- `void setWarnOnTrim(boolean)` -> set trim warning flag  
- `float getTopP()` -> nucleus threshold  
- `void setTopP(float)` -> set nucleus threshold  
- `int getTopK()` -> top‑k limit  
- `void setTopK(int)` -> set top‑k limit  
- `float getRepeatPenalty()` -> repetition penalty  
- `void setRepeatPenalty(float)` -> set repetition penalty  
- `float getMinP()` -> min‑p threshold  
- `void setMinP(float)` -> set min‑p threshold  
- `float getTopNSigma()` -> top‑n‑sigma threshold  
- `void setTopNSigma(float)` -> set top‑n‑sigma threshold  
- `Boolean getChatTemplateEnableThinking()` -> chat‑template thinking mode  
- `void setChatTemplateEnableThinking(Boolean)` -> set chat‑template thinking mode  
- `boolean isCachePrompt()` -> prompt‑caching flag  
- `boolean isSwaFull()` -> full‑SWA flag  
- `void setSwaFull(boolean)` -> set full‑SWA flag  
- `int getCacheReuse()` -> cache‑reuse chunk size  
- `void setCacheReuse(int)` -> set cache‑reuse chunk size  
- `int getGpuLayers()` -> GPU offload layers  
- `void setGpuLayers(int)` -> set GPU offload layers  
- `int getSeed()` -> RNG seed  
- `void setSeed(int)` -> set RNG seed  
- `int getCpuMoeLayers()` -> CPU MoE layers  
- `void setCpuMoeLayers(int)` -> set CPU MoE layers  
- `int getCpuFfnLayers()` -> CPU FFN layers  
- `void setCpuFfnLayers(int)` -> set CPU FFN layers  
- `int getKvUnifiedPerSlot()` -> KV unified per‑slot cap  
- `void setKvUnifiedPerSlot(int)` -> set KV unified per‑slot cap  
- `String getLazyMode()` -> tensor‑read laziness  
- `void setLazyMode(String)` -> set tensor‑read laziness  
- `int getRepeatLastN()` -> repeat‑penalty window  
- `void setRepeatLastN(int)` -> set repeat‑penalty window  
- `String getCacheTypeK()` -> KV cache type K  
- `void setCacheTypeK(String)` -> set KV cache type K  
- `String getCacheTypeV()` -> KV cache type V  
- `void setCacheTypeV(String)` -> set KV cache type V  
- `boolean isFlashAttn()` -> Flash Attention flag  
- `void setFlashAttn(boolean)` -> set Flash Attention flag  
- `int getBatchSize()` -> logical batch size  
- `void setBatchSize(int)` -> set logical batch size  
- `int getUbatchSize()` -> physical batch size  
- `void setUbatchSize(int)` -> set physical batch size  
- `int getThreadsBatch()` -> batch thread count  
- `void setThreadsBatch(int)` -> set batch thread count  
- `int getMainGpu()` -> primary GPU index  
- `void setMainGpu(int)` -> set primary GPU index  
- `String getDevices()` -> device list  
- `void setDevices(String)` -> set device list  
- `String getProvider()` -> AI provider  
- `void setProvider(String)` -> set AI provider  
- `String getRemoteModel()` -> remote model ID  
- `void setRemoteModel(String)` -> set remote model ID  
- `String getBaseUrl()` -> base URL  
- `void setBaseUrl(String)` -> set base URL  
- `String getApiKeyEnv()` -> API key env var name  
- `void setApiKeyEnv(String)` -> set API key env var name  
- `String getReasoningEffort()` -> reasoning effort level  
- `void setReasoningEffort(String)` -> set reasoning effort level  
- `int getReasoningBudgetTokens()` -> reasoning budget tokens  
- `void setReasoningBudgetTokens(int)` -> set reasoning budget tokens  
- `float getDryMultiplier()` -> DRY multiplier  
- `void setDryMultiplier(float)` -> set DRY multiplier  
- `float getDryBase()` -> DRY base  
- `void setDryBase(float)` -> set DRY base  
- `int getDryAllowedLength()` -> DRY allowed length  
- `void setDryAllowedLength(int)` -> set DRY allowed length  
- `int getDryPenaltyLastN()` -> DRY penalty last‑n  
- `void setDryPenaltyLastN(int)` -> set DRY penalty last‑n  
- `List<String> getDrySequenceBreakers()` -> unmodifiable DRY sequence breakers  
- `void setDrySequenceBreakers(List<String>)` -> set DRY sequence breakers  
- `List<String> getStopStrings()` -> unmodifiable stop strings  
- `void setStopStrings(List<String>)` -> set stop strings  
- `AiCalibration getCalibration()` -> calibration  
- `void setCalibration(AiCalibration)` -> set calibration  

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCalibration` (used as a field type)

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Null handling: setters replace `null` with defaults; getters may return `null` for nullable fields.

#### Concurrency
- No synchronization; the class is not thread‑safe.  
- Intended for single‑threaded configuration building.

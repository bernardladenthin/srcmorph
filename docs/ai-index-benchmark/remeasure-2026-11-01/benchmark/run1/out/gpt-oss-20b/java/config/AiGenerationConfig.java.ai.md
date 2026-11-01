### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:42Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider APIs.

#### Purpose
- Stores AI generation settings (model path, sampling, caching, GPU, etc.) for a single generation step.

#### Type
`public class AiGenerationConfig`  
class, public, mutable, Lombok `@ToString`, suppresses null‑away warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each configuration field (model path, context size, output tokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings, calibration.

#### Output
- Getter methods expose current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `getCalibration()` returns optional per‑machine timing calibration.

#### Core logic
- Simple JavaBean: fields initialized to static defaults; setters assign values; getters return current state.
- List setters replace internal lists with new instances or empty lists.
- Boolean getters use `isX` naming; setters accept primitives or boxed types.
- No additional computation or validation beyond null checks for list setters.

#### Public API
```
getModelPath() -> String
setModelPath(String)
getContextSize() -> int
setContextSize(int)
getMaxOutputTokens() -> int
setMaxOutputTokens(int)
getTemperature() -> float
setTemperature(float)
getThreads() -> int
setThreads(int)
getCharsPerToken() -> int
setCharsPerToken(int)
getMaxInputChars() -> int
setMaxInputChars(int)
isWarnOnTrim() -> boolean
setWarnOnTrim(boolean)
getTopP() -> float
setTopP(float)
getTopK() -> int
setTopK(int)
getMinP() -> float
setMinP(float)
getTopNSigma() -> float
setTopNSigma(float)
getRepeatPenalty() -> float
setRepeatPenalty(float)
getChatTemplateEnableThinking() -> Boolean
setChatTemplateEnableThinking(Boolean)
isCachePrompt() -> boolean
setCachePrompt(boolean)
isSwaFull() -> boolean
setSwaFull(boolean)
getCacheReuse() -> int
setCacheReuse(int)
getGpuLayers() -> int
setGpuLayers(int)
getSeed() -> int
setSeed(int)
getCpuMoeLayers() -> int
setCpuMoeLayers(int)
getCpuFfnLayers() -> int
setCpuFfnLayers(int)
getKvUnifiedPerSlot() -> int
setKvUnifiedPerSlot(int)
getLazyMode() -> String
setLazyMode(String)
getRepeatLastN() -> int
setRepeatLastN(int)
getCacheTypeK() -> String
setCacheTypeK(String)
getCacheTypeV() -> String
setCacheTypeV(String)
isFlashAttn() -> boolean
setFlashAttn(boolean)
getBatchSize() -> int
setBatchSize(int)
getUbatchSize() -> int
setUbatchSize(int)
getThreadsBatch() -> int
setThreadsBatch(int)
getMainGpu() -> int
setMainGpu(int)
getDevices() -> String
setDevices(String)
getProvider() -> String
setProvider(String)
getRemoteModel() -> String
setRemoteModel(String)
getBaseUrl() -> String
setBaseUrl(String)
getApiKeyEnv() -> String
setApiKeyEnv(String)
getReasoningEffort() -> String
setReasoningEffort(String)
getReasoningBudgetTokens() -> int
setReasoningBudgetTokens(int)
getDryMultiplier() -> float
setDryMultiplier(float)
getDryBase() -> float
setDryBase(float)
getDryAllowedLength() -> int
setDryAllowedLength(int)
getDryPenaltyLastN() -> int
setDryPenaltyLastN(int)
getDrySequenceBreakers() -> List<String>
setDrySequenceBreakers(List<String>)
getStopStrings() -> List<String>
setStopStrings(List<String>)
getCalibration() -> AiCalibration
setCalibration(AiCalibration)
```

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No checked exceptions thrown.
- List setters replace internal lists; null arguments reset to empty lists.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

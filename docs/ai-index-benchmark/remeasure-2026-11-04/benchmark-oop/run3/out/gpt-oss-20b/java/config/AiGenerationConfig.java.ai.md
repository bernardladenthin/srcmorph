### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:40Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, used by the Maven plugin to configure the AI provider.

#### Purpose
- Stores configuration for a single AI generation step (model path, sampling, retry, prompt trimming, stop strings, etc.).
- Exposes getters/setters for Maven plugin binding and provider code.

#### Type
- `public class AiGenerationConfig`  
  - Modifiers: `public`  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString` (Lombok)  
  - No supertype declared.

#### Input
- Constructor: no arguments (defaults applied).
- Fields (mutable via setters):
  - `modelPath` (`String?`)
  - `contextSize` (`int`)
  - `maxOutputTokens` (`int`)
  - `temperature` (`float`)
  - `threads` (`int`)
  - `charsPerToken` (`int`)
  - `maxInputChars` (`int`)
  - `warnOnTrim` (`boolean`)
  - `topP` (`float`)
  - `topK` (`int`)
  - `minP` (`float`)
  - `topNSigma` (`float`)
  - `repeatPenalty` (`float`)
  - `chatTemplateEnableThinking` (`Boolean?`)
  - `cachePrompt` (`boolean`)
  - `swaFull` (`boolean`)
  - `cacheReuse` (`int`)
  - `gpuLayers` (`int`)
  - `seed` (`int`)
  - `cpuMoeLayers` (`int`)
  - `cpuFfnLayers` (`int`)
  - `kvUnifiedPerSlot` (`int`)
  - `lazyMode` (`String`)
  - `repeatLastN` (`int`)
  - `cacheTypeK` (`String`)
  - `cacheTypeV` (`String`)
  - `flashAttn` (`boolean`)
  - `batchSize` (`int`)
  - `ubatchSize` (`int`)
  - `threadsBatch` (`int`)
  - `mainGpu` (`int`)
  - `devices` (`String`)
  - `provider` (`String`)
  - `remoteModel` (`String`)
  - `baseUrl` (`String`)
  - `apiKeyEnv` (`String`)
  - `reasoningEffort` (`String`)
  - `reasoningBudgetTokens` (`int`)
  - `dryMultiplier` (`float`)
  - `dryBase` (`float`)
  - `dryAllowedLength` (`int`)
  - `dryPenaltyLastN` (`int`)
  - `drySequenceBreakers` (`List<String>`)
  - `stopStrings` (`List<String>`)
  - `calibration` (`AiCalibration?`)

#### Output
- Getter methods return current field values.
- Setter methods mutate fields; some normalize `null` to defaults.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` (via Lombok) prints all fields for diagnostics.

#### Core logic
- All fields initialized to static defaults.
- Setters perform simple assignments; some guard against `null` by resetting to default constants.
- No additional computation or side effects beyond field mutation.

#### Public API
- `AiGenerationConfig()` -> void (default constructor)
- `getModelPath()` -> String? (model file path)
- `setModelPath(String?)` -> void
- `getContextSize()` -> int
- `setContextSize(int)` -> void
- `getMaxOutputTokens()` -> int
- `setMaxOutputTokens(int)` -> void
- `getTemperature()` -> float
- `setTemperature(float)` -> void
- `getThreads()` -> int
- `setThreads(int)` -> void
- `getCharsPerToken()` -> int
- `setCharsPerToken(int)` -> void
- `getMaxInputChars()` -> int
- `setMaxInputChars(int)` -> void
- `isWarnOnTrim()` -> boolean
- `setWarnOnTrim(boolean)` -> void
- `getTopP()` -> float
- `setTopP(float)` -> void
- `getTopK()` -> int
- `setTopK(int)` -> void
- `getRepeatPenalty()` -> float
- `setRepeatPenalty(float)` -> void
- `getMinP()` -> float
- `setMinP(float)` -> void
- `getTopNSigma()` -> float
- `setTopNSigma(float)` -> void
- `getChatTemplateEnableThinking()` -> Boolean? (nullable)
- `setChatTemplateEnableThinking(Boolean?)` -> void
- `isCachePrompt()` -> boolean
- `setCachePrompt(boolean)` -> void
- `isSwaFull()` -> boolean
- `setSwaFull(boolean)` -> void
- `getCacheReuse()` -> int
- `setCacheReuse(int)` -> void
- `getGpuLayers()` -> int
- `setGpuLayers(int)` -> void
- `getSeed()` -> int
- `setSeed(int)` -> void
- `getCpuMoeLayers()` -> int
- `setCpuMoeLayers(int)` -> void
- `getCpuFfnLayers()` -> int
- `setCpuFfnLayers(int)` -> void
- `getKvUnifiedPerSlot()` -> int
- `setKvUnifiedPerSlot(int)` -> void
- `getLazyMode()` -> String
- `setLazyMode(String?)` -> void
- `getRepeatLastN()` -> int
- `setRepeatLastN(int)` -> void
- `getCacheTypeK()` -> String
- `setCacheTypeK(String?)` -> void
- `getCacheTypeV()` -> String
- `setCacheTypeV(String?)` -> void
- `isFlashAttn()` -> boolean
- `setFlashAttn(boolean)` -> void
- `getBatchSize()` -> int
- `setBatchSize(int)` -> void
- `getUbatchSize()` -> int
- `setUbatchSize(int)` -> void
- `getThreadsBatch()` -> int
- `setThreadsBatch(int)` -> void
- `getMainGpu()` -> int
- `setMainGpu(int)` -> void
- `getDevices()` -> String
- `setDevices(String)` -> void
- `getProvider()` -> String
- `setProvider(String)` -> void
- `getRemoteModel()` -> String
- `setRemoteModel(String)` -> void
- `getBaseUrl()` -> String
- `setBaseUrl(String)` -> void
- `getApiKeyEnv()` -> String
- `setApiKeyEnv(String)` -> void
- `getReasoningEffort()` -> String
- `setReasoningEffort(String)` -> void
- `getReasoningBudgetTokens()` -> int
- `setReasoningBudgetTokens(int)` -> void
- `getDryMultiplier()` -> float
- `setDryMultiplier(float)` -> void
- `getDryBase()` -> float
- `setDryBase(float)` -> void
- `getDryAllowedLength()` -> int
- `setDryAllowedLength(int)` -> void
- `getDryPenaltyLastN()` -> int
- `setDryPenaltyLastN(int)` -> void
- `getDrySequenceBreakers()` -> List<String> (unmodifiable)
- `setDrySequenceBreakers(List<String>?)` -> void
- `getStopStrings()` -> List<String> (unmodifiable)
- `setStopStrings(List<String>?)` -> void
- `getCalibration()` -> AiCalibration? (nullable)
- `setCalibration(AiCalibration?)` -> void

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration` (used as a field type)

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept `null` and replace with defaults.

#### Concurrency
- No synchronization; intended for single‑threaded configuration usage.

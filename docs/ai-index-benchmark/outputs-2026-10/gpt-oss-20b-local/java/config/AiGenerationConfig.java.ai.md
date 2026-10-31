### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:08:07Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all parameters for a single AI generation step, passed from Maven configuration to the provider.

#### Purpose
- Stores AI generation settings (model path, sampling, cache, GPU, etc.)  
- Exposes mutable JavaBean API for Maven plugin configuration.

#### Type
- Class, public, mutable, Lombok `@ToString`  
- No inheritance or interfaces.

#### Input
- Constructor: no arguments, defaults applied.  
- Setter methods for each field (modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings, calibration).

#### Output
- Getter methods return current configuration values.  
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.  
- `toString()` (via Lombok) prints all fields for diagnostics.

#### Core logic
- Simple field accessors; no additional computation.  
- Default constants provide sensible values for each parameter.  
- Setter methods normalize nulls to defaults (e.g., `setLazyMode`, `setCacheTypeK`, etc.).  
- `setCachePrompt` is separate from `isCachePrompt`.  
- `isSwaFull` and `isCachePrompt` expose boolean flags.

#### Public API
- 90 public methods:  
  - `getModelPath() -> String?`  
  - `setModelPath(String?)`  
  - `getContextSize() -> int`  
  - `setContextSize(int)`  
  - `getMaxOutputTokens() -> int`  
  - `setMaxOutputTokens(int)`  
  - `getTemperature() -> float`  
  - `setTemperature(float)`  
  - `getThreads() -> int`  
  - `setThreads(int)`  
  - `getCharsPerToken() -> int`  
  - `setCharsPerToken(int)`  
  - `getMaxInputChars() -> int`  
  - `setMaxInputChars(int)`  
  - `isWarnOnTrim() -> boolean`  
  - `setWarnOnTrim(boolean)`  
  - `getTopP() -> float`  
  - `setTopP(float)`  
  - `getTopK() -> int`  
  - `setTopK(int)`  
  - `getRepeatPenalty() -> float`  
  - `setRepeatPenalty(float)`  
  - `getMinP() -> float`  
  - `setMinP(float)`  
  - `getTopNSigma() -> float`  
  - `setTopNSigma(float)`  
  - `getChatTemplateEnableThinking() -> Boolean?`  
  - `setChatTemplateEnableThinking(Boolean?)`  
  - `isCachePrompt() -> boolean`  
  - `isSwaFull() -> boolean`  
  - `setSwaFull(boolean)`  
  - `getCacheReuse() -> int`  
  - `setCacheReuse(int)`  
  - `getGpuLayers() -> int`  
  - `setGpuLayers(int)`  
  - `getCpuMoeLayers() -> int`  
  - `setCpuMoeLayers(int)`  
  - `getSeed() -> int`  
  - `setSeed(int)`  
  - `getCpuFfnLayers() -> int`  
  - `setCpuFfnLayers(int)`  
  - `getKvUnifiedPerSlot() -> int`  
  - `setKvUnifiedPerSlot(int)`  
  - `getLazyMode() -> String`  
  - `setLazyMode(String?)`  
  - `getRepeatLastN() -> int`  
  - `setRepeatLastN(int)`  
  - `getCacheTypeK() -> String`  
  - `setCacheTypeK(String?)`  
  - `getCacheTypeV() -> String`  
  - `setCacheTypeV(String?)`  
  - `isFlashAttn() -> boolean`  
  - `setFlashAttn(boolean)`  
  - `getBatchSize() -> int`  
  - `setBatchSize(int)`  
  - `getUbatchSize() -> int`  
  - `setUbatchSize(int)`  
  - `getThreadsBatch() -> int`  
  - `setThreadsBatch(int)`  
  - `getMainGpu() -> int`  
  - `setMainGpu(int)`  
  - `getDevices() -> String`  
  - `setDevices(String)`  
  - `getProvider() -> String`  
  - `setProvider(String?)`  
  - `getRemoteModel() -> String`  
  - `setRemoteModel(String?)`  
  - `getBaseUrl() -> String`  
  - `setBaseUrl(String?)`  
  - `getApiKeyEnv() -> String`  
  - `setApiKeyEnv(String?)`  
  - `setCachePrompt(boolean)`  
  - `getReasoningEffort() -> String`  
  - `setReasoningEffort(String)`  
  - `getReasoningBudgetTokens() -> int`  
  - `setReasoningBudgetTokens(int)`  
  - `getDryMultiplier() -> float`  
  - `setDryMultiplier(float)`  
  - `getDryBase() -> float`  
  - `setDryBase(float)`  
  - `getDryAllowedLength() -> int`  
  - `setDryAllowedLength(int)`  
  - `getDryPenaltyLastN() -> int`  
  - `setDryPenaltyLastN(int)`  
  - `getDrySequenceBreakers() -> List<String>`  
  - `setDrySequenceBreakers(List<String>?)`  
  - `getStopStrings() -> List<String>`  
  - `setStopStrings(List<String>?)`  
  - `getCalibration() -> AiCalibration?`  
  - `setCalibration(AiCalibration?)`

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Setters accept nulls and replace with defaults; no validation errors.

#### Concurrency
- No synchronization; intended for single‑threaded configuration usage.  
- Immutable lists returned via `Collections.unmodifiableList`.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:27Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider calls.

#### Purpose
- Stores all AI generation settings (model path, sampling, retry, trimming, stop strings).
- Exposes getters/setters for Maven configuration binding.

#### Type
- Class, public, mutable JavaBean.
- Annotated `@ToString` (Lombok) for diagnostics.
- No inheritance or interfaces.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods receive primitive values, `String`, `List<String>`, or `@Nullable` objects.
- Optional `AiCalibration` calibration object.

#### Output
- Getter methods return current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` provides field values for logging.

#### Core logic
- Default constants define sensible AI defaults (context size, temperature, thread count, etc.).
- Setters enforce non‑null defaults for optional strings.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace `null` with empty defaults.
- `setDevices` and `setProvider` reset to empty defaults when `null`.
- `setCachePrompt` toggles prompt‑caching flag.
- `getDrySequenceBreakers`/`getStopStrings` expose immutable views.

#### Public API
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
- `setDevices(String?)`  
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

**Total public methods: 89**

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters accept `null` for optional fields, resetting to defaults.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building. Instances are mutable but not thread‑safe.

### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:19Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, exposing getters/setters for configuration values used by Maven plugin and AI providers.

#### Purpose
- Stores AI generation settings (model path, sampling, retry, prompt trimming, stop strings) for a single run.
- Provides default constants for all configurable parameters.
- Supplies Lombok‑generated `toString` for diagnostics.

#### Type
- `public class AiGenerationConfig` (class, public, mutable, JavaBean)
  - Extends `Object`.
  - Annotated with `@ToString` (Lombok) and `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.
  - Declares 1 constructor (no‑arg).

#### Input
- Constructor: none.
- Public setters:
  - `setModelPath(String)`
  - `setContextSize(int)`
  - `setMaxOutputTokens(int)`
  - `setTemperature(float)`
  - `setThreads(int)`
  - `setCharsPerToken(int)`
  - `setMaxInputChars(int)`
  - `setWarnOnTrim(boolean)`
  - `setTopP(float)`
  - `setTopK(int)`
  - `setMinP(float)`
  - `setTopNSigma(float)`
  - `setRepeatPenalty(float)`
  - `setChatTemplateEnableThinking(Boolean)`
  - `setSwaFull(boolean)`
  - `setCacheReuse(int)`
  - `setGpuLayers(int)`
  - `setSeed(int)`
  - `setCpuMoeLayers(int)`
  - `setCpuFfnLayers(int)`
  - `setKvUnifiedPerSlot(int)`
  - `setLazyMode(String)`
  - `setRepeatLastN(int)`
  - `setCacheTypeK(String)`
  - `setCacheTypeV(String)`
  - `setFlashAttn(boolean)`
  - `setBatchSize(int)`
  - `setUbatchSize(int)`
  - `setThreadsBatch(int)`
  - `setMainGpu(int)`
  - `setDevices(String)`
  - `setProvider(String)`
  - `setRemoteModel(String)`
  - `setBaseUrl(String)`
  - `setApiKeyEnv(String)`
  - `setReasoningEffort(String)`
  - `setReasoningBudgetTokens(int)`
  - `setDryMultiplier(float)`
  - `setDryBase(float)`
  - `setDryAllowedLength(int)`
  - `setDryPenaltyLastN(int)`
  - `setDrySequenceBreakers(List<String>)`
  - `setStopStrings(List<String>)`
  - `setCalibration(AiCalibration)`
  - `setCachePrompt(boolean)` (duplicate of `setCachePrompt` earlier)
- Fields injected via setters or Maven plugin.

#### Output
- Public getters return current configuration values:
  - `getModelPath()`, `getContextSize()`, `getMaxOutputTokens()`, `getTemperature()`, `getThreads()`, `getCharsPerToken()`, `getMaxInputChars()`, `isWarnOnTrim()`, `getTopP()`, `getTopK()`, `getMinP()`, `getTopNSigma()`, `getRepeatPenalty()`, `getChatTemplateEnableThinking()`, `isCachePrompt()`, `isSwaFull()`, `getCacheReuse()`, `getGpuLayers()`, `getSeed()`, `getCpuMoeLayers()`, `getCpuFfnLayers()`, `getKvUnifiedPerSlot()`, `getLazyMode()`, `getRepeatLastN()`, `getCacheTypeK()`, `getCacheTypeV()`, `isFlashAttn()`, `getBatchSize()`, `getUbatchSize()`, `getThreadsBatch()`, `getMainGpu()`, `getDevices()`, `getProvider()`, `getRemoteModel()`, `getBaseUrl()`, `getApiKeyEnv()`, `getReasoningEffort()`, `getReasoningBudgetTokens()`, `getDryMultiplier()`, `getDryBase()`, `getDryAllowedLength()`, `getDryPenaltyLastN()`, `getDrySequenceBreakers()`, `getStopStrings()`, `getCalibration()`.
- `toString()` provides diagnostic view of all fields.

#### Core logic
- No complex algorithms; the class is a plain data holder.
- Default constants are defined for every configurable parameter.
- Setters perform minimal validation (e.g., null checks, default fallbacks).
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace null with default constants.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.

#### Public API
- Constructor: `AiGenerationConfig() -> AiGenerationConfig` (no params).
- Getters (public, no args):
  - `getModelPath() -> String?`
  - `getContextSize() -> int`
  - `getMaxOutputTokens() -> int`
  - `getTemperature() -> float`
  - `getThreads() -> int`
  - `getCharsPerToken() -> int`
  - `getMaxInputChars() -> int`
  - `isWarnOnTrim() -> boolean`
  - `getTopP() -> float`
  - `getTopK() -> int`
  - `getMinP() -> float`
  - `getTopNSigma() -> float`
  - `getRepeatPenalty() -> float`
  - `getChatTemplateEnableThinking() -> Boolean?`
  - `isCachePrompt() -> boolean`
  - `isSwaFull() -> boolean`
  - `getCacheReuse() -> int`
  - `getGpuLayers() -> int`
  - `getSeed() -> int`
  - `getCpuMoeLayers() -> int`
  - `getCpuFfnLayers() -> int`
  - `getKvUnifiedPerSlot() -> int`
  - `getLazyMode() -> String`
  - `getRepeatLastN() -> int`
  - `getCacheTypeK() -> String`
  - `getCacheTypeV() -> String`
  - `isFlashAttn() -> boolean`
  - `getBatchSize() -> int`
  - `getUbatchSize() -> int`
  - `getThreadsBatch() -> int`
  - `getMainGpu() -> int`
  - `getDevices() -> String`
  - `getProvider() -> String`
  - `getRemoteModel() -> String`
  - `getBaseUrl() -> String`
  - `getApiKeyEnv() -> String`
  - `getReasoningEffort() -> String`
  - `getReasoningBudgetTokens() -> int`
  - `getDryMultiplier() -> float`
  - `getDryBase() -> float`
  - `getDryAllowedLength() -> int`
  - `getDryPenaltyLastN() -> int`
  - `getDrySequenceBreakers() -> List<String>`
  - `getStopStrings() -> List<String>`
  - `getCalibration() -> AiCalibration?`
- Setters (public, one param):
  - `setModelPath(String?)`
  - `setContextSize(int)`
  - `setMaxOutputTokens(int)`
  - `setTemperature(float)`
  - `setThreads(int)`
  - `setCharsPerToken(int)`
  - `setMaxInputChars(int)`
  - `setWarnOnTrim(boolean)`
  - `setTopP(float)`
  - `setTopK(int)`
  - `setMinP(float)`
  - `setTopNSigma(float)`
  - `setRepeatPenalty(float)`
  - `setChatTemplateEnableThinking(Boolean?)`
  - `setCachePrompt(boolean)`
  - `setSwaFull(boolean)`
  - `setCacheReuse(int)`
  - `setGpuLayers(int)`
  - `setSeed(int)`
  - `setCpuMoeLayers(int)`
  - `setCpuFfnLayers(int)`
  - `setKvUnifiedPerSlot(int)`
  - `setLazyMode(String?)`
  - `setRepeatLastN(int)`
  - `setCacheTypeK(String?)`
  - `setCacheTypeV(String?)`
  - `setFlashAttn(boolean)`
  - `setBatchSize(int)`
  - `setUbatchSize(int)`
  - `setThreadsBatch(int)`
  - `setMainGpu(int)`
  - `setDevices(String?)`
  - `setProvider(String?)`
  - `setRemoteModel(String?)`
  - `setBaseUrl(String?)`
  - `setApiKeyEnv(String?)`
  - `setReasoningEffort(String)`
  - `setReasoningBudgetTokens(int)`
  - `setDryMultiplier(float)`
  - `setDryBase(float)`
  - `setDryAllowedLength(int)`
  - `setDryPenaltyLastN(int)`
  - `setDrySequenceBreakers(List<String>?)`
  - `setStopStrings(List<String>?)`
  - `setCalibration(AiCalibration?)`

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration` (used as calibration type)

#### Exceptions / Errors
- No explicit exceptions thrown; setters perform null checks and default substitutions.

#### Concurrency
- No thread‑safety guarantees; class is mutable and intended for single‑thread use by Maven plugin.

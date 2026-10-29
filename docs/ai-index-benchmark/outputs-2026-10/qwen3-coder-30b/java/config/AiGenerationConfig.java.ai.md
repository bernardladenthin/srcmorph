### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:09:16Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for model inference, including context size, sampling settings, and provider-specific options.

#### Purpose
- Carries mutable configuration for a single AI generation step between Maven and AI provider layers.
- Holds parameters like model path, sampling settings, retry policy, and input trimming limits.

#### Type
- `public class AiGenerationConfig` with Lombok `@ToString` annotation.
- No explicit extends or implements clauses.
- Fields are mostly primitive types or `@Nullable String` and `List<String>`.

#### Input
- Constructor takes no parameters.
- Setters accept various types: `String`, `int`, `float`, `boolean`, `@Nullable Boolean`, `List<String>`, `@Nullable AiCalibration`.
- Fields are populated via setters by Maven plugin framework.

#### Output
- Getters return field values or unmodifiable views of lists.
- Mutated fields reflect updated configuration state.
- Side effects: `setDrySequenceBreakers` and `setStopStrings` replace internal lists.

#### Core logic
- Provides getter/setter pairs for all configuration fields.
- Uses default constants for all fields, with sensible defaults for AI inference parameters.
- Manages mutable state for AI generation configuration.
- Handles nullable fields with `@Nullable` annotations.
- Provides unmodifiable views of mutable lists (`drySequenceBreakers`, `stopStrings`).

#### Public API
- `getModelPath()` → `@Nullable String`
- `setModelPath(String)` → `void`
- `getContextSize()` → `int`
- `setContextSize(int)` → `void`
- `getMaxOutputTokens()` → `int`
- `setMaxOutputTokens(int)` → `void`
- `getTemperature()` → `float`
- `setTemperature(float)` → `void`
- `getThreads()` → `int`
- `setThreads(int)` → `void`
- `getCharsPerToken()` → `int`
- `setCharsPerToken(int)` → `void`
- `getMaxInputChars()` → `int`
- `setMaxInputChars(int)` → `void`
- `isWarnOnTrim()` → `boolean`
- `setWarnOnTrim(boolean)` → `void`
- `getTopP()` → `float`
- `setTopP(float)` → `void`
- `getTopK()` → `int`
- `setTopK(int)` → `void`
- `getRepeatPenalty()` → `float`
- `setRepeatPenalty(float)` → `void`
- `getMinP()` → `float`
- `setMinP(float)` → `void`
- `getTopNSigma()` → `float`
- `setTopNSigma(float)` → `void`
- `getChatTemplateEnableThinking()` → `@Nullable Boolean`
- `setChatTemplateEnableThinking(@Nullable Boolean)` → `void`
- `isCachePrompt()` → `boolean`
- `isSwaFull()` → `boolean`
- `setSwaFull(boolean)` → `void`
- `getCacheReuse()` → `int`
- `setCacheReuse(int)` → `void`
- `getGpuLayers()` → `int`
- `setGpuLayers(int)` → `void`
- `getCpuMoeLayers()` → `int`
- `setCpuMoeLayers(int)` → `void`
- `getSeed()` → `int`
- `setSeed(int)` → `void`
- `getCpuFfnLayers()` → `int`
- `setCpuFfnLayers(int)` → `void`
- `getKvUnifiedPerSlot()` → `int`
- `setKvUnifiedPerSlot(int)` → `void`
- `getLazyMode()` → `String`
- `setLazyMode(@Nullable String)` → `void`
- `getRepeatLastN()` → `int`
- `setRepeatLastN(int)` → `void`
- `getCacheTypeK()` → `String`
- `setCacheTypeK(@Nullable String)` → `void`
- `getCacheTypeV()` → `String`
- `setCacheTypeV(@Nullable String)` → `void`
- `isFlashAttn()` → `boolean`
- `setFlashAttn(boolean)` → `void`
- `getBatchSize()` → `int`
- `setBatchSize(int)` → `void`
- `getUbatchSize()` → `int`
- `setUbatchSize(int)` → `void`
- `getThreadsBatch()` → `int`
- `setThreadsBatch(int)` → `void`
- `getMainGpu()` → `int`
- `setMainGpu(int)` → `void`
- `getDevices()` → `String`
- `setDevices(String)` → `void`
- `getProvider()` → `String`
- `setProvider(String)` → `void`
- `getRemoteModel()` → `String`
- `setRemoteModel(String)` → `void`
- `getBaseUrl()` → `String`
- `setBaseUrl(String)` → `void`
- `getApiKeyEnv()` → `String`
- `setApiKeyEnv(String)` → `void`
- `setCachePrompt(boolean)` → `void`
- `getReasoningEffort()` → `String`
- `setReasoningEffort(String)` → `void`
- `getReasoningBudgetTokens()` → `int`
- `setReasoningBudgetTokens(int)` → `void`
- `getDryMultiplier()` → `float`
- `setDryMultiplier(float)` → `void`
- `getDryBase()` → `float`
- `setDryBase(float)` → `void`
- `getDryAllowedLength()` → `int`
- `setDryAllowedLength(int)` → `void`
- `getDryPenaltyLastN()` → `int`
- `setDryPenaltyLastN(int)` → `void`
- `getDrySequenceBreakers()` → `List<String>`
- `setDrySequenceBreakers(@Nullable List<String>)` → `void`
- `getStopStrings()` → `List<String>`
- `setStopStrings(@Nullable List<String>)` → `void`
- `getCalibration()` → `@Nullable AiCalibration`
- `setCalibration(@Nullable AiCalibration)` → `void`

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions thrown.
- Null handling for `@Nullable` fields via setter guards.
- Default values used when null is passed to setters.

#### Concurrency
- Not thread-safe due to mutable state.
- Instances are managed by identity, not equality.
- No synchronization mechanisms present.

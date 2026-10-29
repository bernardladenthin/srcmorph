### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:07:57Z
- G: 
- A: 0.0.0
- X: file
---
> Mutable configuration object that carries all parameters for a single AI generation step (model path, sampling parameters, retry policy, input trimming limits, stop strings) between the Maven configuration layer and the AI provider implementations.

#### Purpose
- Mutable configuration object for AI generation parameters

#### Type
- Class, final, implements Serializable

#### Input
- Model path, context size, max output tokens, temperature, threads, chars per token, max input chars, warning on trim, top P, top K, min P, top N sigma, repeat penalty, chat template thinking, cache prompt, SWA full, cache reuse, GPU layers, seed, CPU MoE layers, CPU FFN layers, KV unified per slot, lazy mode, repeat last N, cache type K, cache type V, Flash Attn, batch size, ubatch size, threads batch, main GPU, devices, provider, remote model, base URL, API key environment, reasoning effort, reasoning budget tokens, DRY multiplier, DRY base, DRY allowed length, DRY penalty last N, dry sequence breakers, stop strings, calibration

#### Output
- Returns and sets model path, context size, max output tokens, temperature, threads, chars per token, max input chars, warning on trim, top P, top K, min P, top N sigma, repeat penalty, chat template thinking, cache prompt, SWA full, cache reuse, GPU layers, seed, CPU MoE layers, CPU FFN layers, KV unified per slot, lazy mode, repeat last N, cache type K, cache type V, Flash Attn, batch size, ubatch size, threads batch, main GPU, devices, provider, remote model, base URL, API key environment, reasoning effort, reasoning budget tokens, DRY multiplier, DRY base, DRY allowed length, DRY penalty last N, dry sequence breakers, stop strings, calibration

#### Core logic
- Initializes with default values
- Provides getters and setters for all configuration parameters

#### Public API
- `getModelPath()`, `setModelPath(String)`: Returns and sets the model path
- `getContextSize()`, `setContextSize(int)`: Returns and sets the context size
- `getMaxOutputTokens()`, `setMaxOutputTokens(int)`: Returns and sets the max output tokens
- `getTemperature()`, `setTemperature(float)`: Returns and sets the temperature
- `getThreads()`, `setThreads(int)`: Returns and sets the threads
- `getCharsPerToken()`, `setCharsPerToken(int)`: Returns and sets chars per token
- `getMaxInputChars()`, `setMaxInputChars(int)`: Returns and sets max input chars
- `isWarnOnTrim()`, `setWarnOnTrim(boolean)`: Returns and sets warning on trim
- `getTopP()`, `setTopP(float)`: Returns and sets top P
- `getTopK()`, `setTopK(int)`: Returns and sets top K
- `getMinP()`, `setMinP(float)`: Returns and sets min P
- `getTopNSigma()`, `setTopNSigma(float)`: Returns and sets top N sigma
- `getRepeatPenalty()`, `setRepeatPenalty(float)`: Returns and sets repeat penalty
- `getChatTemplateEnableThinking()`, `setChatTemplateEnableThinking(@Nullable Boolean)`: Returns and sets chat template thinking
- `isCachePrompt()`, `setCachePrompt(boolean)`: Returns and sets cache prompt
- `isSwaFull()`, `setSwaFull(boolean)`: Returns and sets SWA full
- `getCacheReuse()`, `setCacheReuse(int)`: Returns and sets cache reuse
- `getGpuLayers()`, `setGpuLayers(int)`: Returns and sets GPU layers
- `getCpuMoeLayers()`, `setCpuMoeLayers(int)`: Returns and sets CPU MoE layers
- `getCpuFfnLayers()`, `setCpuFfnLayers(int)`: Returns and sets CPU FFN layers
- `getKvUnifiedPerSlot()`, `setKvUnifiedPerSlot(int)`: Returns and sets KV unified per slot
- `getLazyMode()`, `setLazyMode(@Nullable String)`: Returns and sets lazy mode
- `getRepeatLastN()`, `setRepeatLastN(int)`: Returns and sets repeat last N
- `getCacheTypeK()`, `setCacheTypeK(@Nullable String)`: Returns and sets cache type K
- `getCacheTypeV()`, `setCacheTypeV(@Nullable String)`: Returns and sets cache type V
- `isFlashAttn()`, `setFlashAttn(boolean)`: Returns and sets Flash Attn
- `getBatchSize()`, `setBatchSize(int)`: Returns and sets batch size
- `getUbatchSize()`, `setUbatchSize(int)`: Returns and sets ubatch size
- `getThreadsBatch()`, `setThreadsBatch(int)`: Returns and sets threads batch
- `getMainGpu()`, `setMainGpu(int)`: Returns and sets main GPU
- `getDevices()`, `setDevices(String)`: Returns and sets devices
- `getProvider()`, `setProvider(String)`: Returns and sets provider
- `getRemoteModel()`, `setRemoteModel(@Nullable String)`: Returns and sets remote model
- `getBaseUrl()`, `setBaseUrl(String)`: Returns and sets base URL
- `getApiKeyEnv()`, `setApiKeyEnv(@Nullable String)`: Returns and sets API key environment
- `getReasoningEffort()`, `setReasoningEffort(String)`: Returns and sets reasoning effort
- `getReasoningBudgetTokens()`, `setReasoningBudgetTokens(int)`: Returns and sets reasoning budget tokens
- `getDryMultiplier()`, `setDryMultiplier(float)`: Returns and sets DRY multiplier
- `getDryBase()`, `setDryBase(float)`: Returns and sets DRY base
- `getDryAllowedLength()`, `setDryAllowedLength(int)`: Returns and sets DRY allowed length
- `getDryPenaltyLastN()`, `setDryPenaltyLastN(int)`: Returns and sets DRY penalty last N
- `getDrySequenceBreakers()`, `setDrySequenceBreakers(@Nullable List<String>)`: Returns and sets DRY sequence breakers
- `getStopStrings()`, `setStopStrings(@Nullable List<String>)`: Returns and sets stop strings
- `getCalibration()`, `setCalibration(@Nullable AiCalibration)`: Returns and sets calibration

#### Dependencies
- Uses `lombok.ToString`, `org.jspecify.annotations.Nullable`
- Imports `java.util.ArrayList`, `java.util.Collections`, `java.util.List`

#### Exceptions / Errors
- None explicitly mentioned

#### Concurrency
- Thread-safe as immutable after construction

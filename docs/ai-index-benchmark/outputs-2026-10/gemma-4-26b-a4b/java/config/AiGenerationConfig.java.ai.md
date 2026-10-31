### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:29:48Z
- G: 
- A: 0.0.0
- X: file
---
> <Carries parameters for a single AI generation step, including model paths, sampling parameters, and provider-specific configurations, between Maven and AI implementations.>

#### Purpose
* Acts as a mutable configuration container for AI inference parameters.
* Bridges the gap between Maven configuration layers and AI provider implementations.

#### Type
class; public; Lombok `@ToString`.

#### Input
* Constructor (no-op, applies defaults).
* Setters for all configuration fields.
* Injected `AiCalibration` object.

#### Output
* Getter methods for all configuration fields.
* Mutated internal state via setters.
* Unmodifiable views of `drySequenceBreakers` and `stopStrings`.

#### Core logic
* Manages a large set of default constants for inference (e.g., `DEFAULT_TEMPERATURE`, `DEFAULT_TOP_P`, `DEFAULT_THREADS`).
* Implements automatic calculation logic for `maxInputChars` based on `charsPerToken`, `contextSize`, and `maxOutputTokens` (implied by documentation).
* Handles tri-state logic for `chatTemplateEnableThinking` (true/false/null).
* Provides fallback mechanisms for null inputs in setters (e.g., resetting lists or strings to empty/defaults).
* Manages complex sampling parameters including Nucleus, Min-P, Top-K, Top-N-Sigma, and DRY (Don't Repeat Yourself) repetition penalties.

#### Public API
* `AiGenerationConfig()` -> `AiGenerationConfig` (constructor)
* `getModelPath()`, `setModelPath(String)` -> `String` (GGUF model file path)
* `getContextSize()`, `setContextSize(int)` -> `int` (context window size in tokens)
* `getMaxOutputTokens()`, `setMaxOutputTokens(int)` -> `int` (max output tokens)
* `getTemperature()`, `setTemperature(float)` -> `float` (sampling temperature)
* `getThreads()`, `setThreads(int)` -> `int` (CPU inference threads)
* `getCharsPerToken()`, `setCharsPerToken(int)` -> `int` (chars-per-token ratio)
* `getMaxInputChars()`, `setMaxInputChars(int)` -> `int` (max input characters)
* `isWarnOnTrim()`, `setWarnOnTrim(boolean)` -> `boolean` (trim warning flag)
* `getTopP()`, `setTopP(float)` -> `float` (nucleus sampling threshold)
* `getTopK()`, `setTopK(int)` -> `int` (top-k sampling limit)
* `getRepeatPenalty()`, `setRepeatPenalty(float)` -> `float` (repetition penalty)
* `getMinP()`, `setMinP(float)` -> `float` (min-p sampling threshold)
* `getTopNSigma()`, `setTopNSigma(float)` -> `float` (top-n-sigma threshold)
* `getChatTemplateEnableThinking()`, `setChatTemplateEnableThinking(Boolean)` -> `Boolean` (chat-template thinking mode)
* `isCachePrompt()`, `setCachePrompt(boolean)` -> `boolean` (prompt caching flag)
* `isSwaFull()`, `setSwaFull(boolean)` -> `boolean` (full SWA KV cache flag)
* `getCacheReuse()`, `setCacheReuse(int)` -> `int` (KV prefix-reuse chunk size)
* `getGpuLayers()`, `setGpuLayers(int)` -> `int` (GPU layer offload count)
* `getCpuMoeLayers()`, `setCpuMoeLayers(int)` -> `int` (CPU MoE layer count)
* `getSeed()`, `setSeed(int)` -> `int` (RNG seed)
* `getCpuFfnLayers()`, `setCpuFfnLayers(int)` -> `int` (CPU FFN layer count)
* `getKvUnifiedPerSlot()`, `setKvUnifiedPerSlot(int)` -> `int` (unified KV context cap)
* `getLazyMode()`, `setLazyMode(String)` -> `String` (tensor-read laziness)
* `getRepeatLastN()`, `setRepeatLastN(int)` -> `int` (repeat penalty window)
* `getCacheTypeK()`, `setCacheTypeK(String)` -> `String` (KV-cache K data type)
* `getCacheTypeV()`, `setCacheTypeV(String)` -> `String` (KV-cache V data type)
* `isFlashAttn()`, `setFlashAttn(boolean)` -> `boolean` (Flash Attention flag)
* `getBatchSize()`, `setBatchSize(int)` -> `int` (logical batch size)
* `getUbatchSize()`, `setUbatchSize(int)` -> `int` (physical batch size)
* `getThreadsBatch()`, `setThreadsBatch(int)` -> `int` (batch processing threads)
* `getMainGpu()`, `setMainGpu(int)` -> `int` (primary GPU index)
* `getDevices()`, `setDevices(String)` -> `String` (device selection list)
* `getProvider()`, `setProvider(String)` -> `String` (AI provider type)
* `getRemoteModel()`, `setRemoteModel(String)` -> `String` (remote model ID)
* `getBaseUrl()`, `setBaseUrl(String)` -> `String` (OpenAI-compatible base URL)
* `getApiKeyEnv()`, `setApiKeyEnv(String)` -> `String` (API key environment variable name)
* `getReasoningEffort()`, `setReasoningEffort(String)` -> `String` (gpt-oss reasoning effort)
* `getReasoningBudgetTokens()`, `setReasoningBudgetTokens(int)` -> `int` (reasoning token budget)
* `getDryMultiplier()`, `setDryMultiplier(float)` -> `float` (DRY sampling multiplier)
* `getDryBase()`, `setDryBase(float)` -> `float` (DRY exponential base)
* `getDryAllowedLength()`, `setDryAllowedLength(int)` -> `int` (DRY allowed n-gram length)
* `getDryPenaltyLastN()`, `setDryPenaltyLastN(int)` -> `int` (DRY look-back window)
* `getDrySequenceBreakers()`, `setDrySequenceBreakers(List<String>)` -> `List<String>` (DRY sequence breakers)
* `getStopStrings()`, `setStopStrings(List<String>)` -> `List<String>` (stop strings list)
* `getCalibration()`, `setCalibration(AiCalibration)` -> `AiCalibration` (timing calibration)

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collections`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
* Handles `null` for various string and list parameters by reverting to defaults or empty collections.
* `kvUnifiedPerSlot` and `batchSize`/`ubatchSize` require positive values (enforced by provider/binding, not this class).

#### Concurrency
* Not thread-safe; designed as a mutable JavaBean for population by a framework.

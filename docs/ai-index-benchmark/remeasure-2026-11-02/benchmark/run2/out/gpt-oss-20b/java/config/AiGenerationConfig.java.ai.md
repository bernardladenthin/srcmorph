### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:14Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> <blockquote>Mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider implementations.</blockquote>

#### Purpose
- Stores AI generation settings (model path, sampling, retry, trimming, stop strings).
- Provides getters/setters for Maven plugin configuration.

#### Type
- `public class AiGenerationConfig` (class, public, mutable, Lombok `@ToString`).

#### Input
- Constructor: no arguments.
- Setters for all configuration fields (modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings, calibration).

#### Output
- Getters return current field values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `getCalibration()` returns optional calibration.

#### Core logic
- Default constants defined for all parameters.
- Setters perform minimal validation (e.g., null → default, empty → default).
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` replace null with default strings.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` reset to defaults when null.
- `setDrySequenceBreakers` and `setStopStrings` replace null with empty list.
- No complex algorithms; class is a plain JavaBean.

#### Public API
- `AiGenerationConfig()` -> constructor
- `getModelPath() -> @Nullable String` – model file path
- `setModelPath(@Nullable String)` – set model path
- `getContextSize() -> int` – context window
- `setContextSize(int)` – set context size
- `getMaxOutputTokens() -> int` – max output tokens
- `setMaxOutputTokens(int)` – set max output tokens
- `getTemperature() -> float` – sampling temperature
- `setTemperature(float)` – set temperature
- `getThreads() -> int` – CPU threads
- `setThreads(int)` – set threads
- `getCharsPerToken() -> int` – chars per token
- `setCharsPerToken(int)` – set chars per token
- `getMaxInputChars() -> int` – max input chars
- `setMaxInputChars(int)` – set max input chars
- `isWarnOnTrim() -> boolean` – trim warning flag
- `setWarnOnTrim(boolean)` – set trim warning
- `getTopP() -> float` – nucleus sampling
- `setTopP(float)` – set top‑p
- `getTopK() -> int` – top‑k
- `setTopK(int)` – set top‑k
- `getRepeatPenalty() -> float` – repetition penalty
- `setRepeatPenalty(float)` – set repeat penalty
- `getMinP() -> float` – min‑p
- `setMinP(float)` – set min‑p
- `getTopNSigma() -> float` – top‑n‑sigma
- `setTopNSigma(float)` – set top‑n‑sigma
- `getChatTemplateEnableThinking() -> @Nullable Boolean` – chat‑template thinking flag
- `setChatTemplateEnableThinking(@Nullable Boolean)` – set flag
- `isCachePrompt() -> boolean` – prompt cache flag
- `isSwaFull() -> boolean` – full‑SWA flag
- `setSwaFull(boolean)` – set full‑SWA
- `getCacheReuse() -> int` – cache‑reuse chunk size
- `setCacheReuse(int)` – set cache‑reuse
- `getGpuLayers() -> int` – GPU layers
- `setGpuLayers(int)` – set GPU layers
- `getCpuMoeLayers() -> int` – CPU MoE layers
- `setCpuMoeLayers(int)` – set CPU MoE layers
- `getSeed() -> int` – RNG seed
- `setSeed(int)` – set seed
- `getCpuFfnLayers() -> int` – CPU FFN layers
- `setCpuFfnLayers(int)` – set CPU FFN layers
- `getKvUnifiedPerSlot() -> int` – KV unified per‑slot cap
- `setKvUnifiedPerSlot(int)` – set cap
- `getLazyMode() -> String` – tensor‑read laziness
- `setLazyMode(@Nullable String)` – set laziness
- `getRepeatLastN() -> int` – repeat‑penalty window
- `setRepeatLastN(int)` – set window
- `getCacheTypeK() -> String` – KV cache type K
- `setCacheTypeK(@Nullable String)` – set type K
- `getCacheTypeV() -> String` – KV cache type V
- `setCacheTypeV(@Nullable String)` – set type V
- `isFlashAttn() -> boolean` – Flash Attention flag
- `setFlashAttn(boolean)` – set flag
- `getBatchSize() -> int` – logical batch size
- `setBatchSize(int)` – set batch size
- `getUbatchSize() -> int` – physical batch size
- `setUbatchSize(int)` – set ubatch size
- `getThreadsBatch() -> int` – batch thread count
- `setThreadsBatch(int)` – set batch threads
- `getMainGpu() -> int` – primary GPU index
- `setMainGpu(int)` – set GPU index
- `getDevices() -> String` – device list
- `setDevices(String)` – set devices
- `getProvider() -> String` – AI provider
- `setProvider(String)` – set provider
- `getRemoteModel() -> String` – remote model id
- `setRemoteModel(String)` – set remote model
- `getBaseUrl() -> String` – base URL
- `setBaseUrl(String)` – set base URL
- `getApiKeyEnv() -> String` – API key env var
- `setApiKeyEnv(String)` – set env var
- `setCachePrompt(boolean)` – set prompt cache
- `getReasoningEffort() -> String` – reasoning effort
- `setReasoningEffort(String)` – set effort
- `getReasoningBudgetTokens() -> int` – reasoning budget
- `setReasoningBudgetTokens(int)` – set budget
- `getDryMultiplier() -> float` – DRY multiplier
- `setDryMultiplier(float)` – set multiplier
- `getDryBase() -> float` – DRY base
- `setDryBase(float)` – set base
- `getDryAllowedLength() -> int` – DRY allowed length
- `setDryAllowedLength(int)` – set length
- `getDryPenaltyLastN() -> int` – DRY penalty window
- `setDryPenaltyLastN(int)` – set window
- `getDrySequenceBreakers() -> List<String>` – DRY breakers
- `setDrySequenceBreakers(@Nullable List<String>)` – set breakers
- `getStopStrings() -> List<String>` – stop strings
- `setStopStrings(@Nullable List<String>)` – set stop strings
- `getCalibration() -> @Nullable AiCalibration` – calibration
- `setCalibration(@Nullable AiCalibration)` – set calibration

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters perform null‑to‑default conversion; no validation errors.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances are intended for single‑thread configuration use.

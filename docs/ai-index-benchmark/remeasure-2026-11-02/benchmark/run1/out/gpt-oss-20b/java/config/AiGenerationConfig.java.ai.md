### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:50Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A mutable bean holding all AI‑generation parameters (model path, sampling, retry, prompt trimming, stop strings, provider settings) for a single generation step.

#### Purpose
- Stores configuration for a single AI generation request.
- Exposes getters/setters for Maven plugin and provider layers.

#### Type
`public class AiGenerationConfig`  
class, mutable JavaBean, Lombok `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each field (e.g., `setModelPath`, `setTemperature`, `setProvider`, etc.).
- `setCalibration(AiCalibration)` for per‑machine timing.

#### Output
- Getter methods return current values (e.g., `getModelPath`, `getTemperature`, `getStopStrings`).
- `getDrySequenceBreakers` / `getStopStrings` return unmodifiable lists.
- `setCachePrompt`, `setSwaFull`, etc. mutate internal state.

#### Core logic
- Default constants define sensible values for all parameters.
- Setters validate nulls by resetting to defaults (e.g., `setLazyMode`, `setCacheTypeK`).
- `setDevices` treats `null` as empty default.
- `getDrySequenceBreakers` / `getStopStrings` expose immutable views.
- No additional computation; the class is a plain data holder.

#### Public API
- `AiGenerationConfig()` -> constructor
- `String getModelPath()` -> model file path
- `void setModelPath(String)` -> set model path
- `int getContextSize()` -> context window size
- `void setContextSize(int)` -> set context size
- `int getMaxOutputTokens()` -> max output tokens
- `void setMaxOutputTokens(int)` -> set max output tokens
- `float getTemperature()` -> sampling temperature
- `void setTemperature(float)` -> set temperature
- `int getThreads()` -> CPU thread count
- `void setThreads(int)` -> set thread count
- `int getCharsPerToken()` -> chars per token ratio
- `void setCharsPerToken(int)` -> set chars per token
- `int getMaxInputChars()` -> max input characters
- `void setMaxInputChars(int)` -> set max input characters
- `boolean isWarnOnTrim()` -> trim warning flag
- `void setWarnOnTrim(boolean)` -> set trim warning flag
- `float getTopP()` -> nucleus sampling threshold
- `void setTopP(float)` -> set top‑p
- `int getTopK()` -> top‑k limit
- `void setTopK(int)` -> set top‑k
- `float getMinP()` -> min‑p threshold
- `void setMinP(float)` -> set min‑p
- `float getTopNSigma()` -> top‑n‑sigma threshold
- `void setTopNSigma(float)` -> set top‑n‑sigma
- `float getRepeatPenalty()` -> repetition penalty
- `void setRepeatPenalty(float)` -> set repetition penalty
- `Boolean getChatTemplateEnableThinking()` -> chat‑template thinking mode
- `void setChatTemplateEnableThinking(Boolean)` -> set chat‑template thinking mode
- `boolean isCachePrompt()` -> prompt‑caching flag
- `boolean isSwaFull()` -> full‑SWA flag
- `void setSwaFull(boolean)` -> set full‑SWA flag
- `int getCacheReuse()` -> KV prefix reuse chunk size
- `void setCacheReuse(int)` -> set cache reuse
- `int getGpuLayers()` -> GPU layer offload count
- `void setGpuLayers(int)` -> set GPU layers
- `int getCpuMoeLayers()` -> CPU MoE layers
- `void setCpuMoeLayers(int)` -> set CPU MoE layers
- `int getSeed()` -> RNG seed
- `void setSeed(int)` -> set seed
- `int getCpuFfnLayers()` -> CPU FFN layers
- `void setCpuFfnLayers(int)` -> set CPU FFN layers
- `int getKvUnifiedPerSlot()` -> unified KV context cap
- `void setKvUnifiedPerSlot(int)` -> set KV cap
- `String getLazyMode()` -> tensor‑read laziness
- `void setLazyMode(String)` -> set lazy mode
- `int getRepeatLastN()` -> repeat‑penalty window
- `void setRepeatLastN(int)` -> set repeat‑penalty window
- `String getCacheTypeK()` -> KV cache type K
- `void setCacheTypeK(String)` -> set cache type K
- `String getCacheTypeV()` -> KV cache type V
- `void setCacheTypeV(String)` -> set cache type V
- `boolean isFlashAttn()` -> Flash Attention flag
- `void setFlashAttn(boolean)` -> set Flash Attention
- `int getBatchSize()` -> logical batch size
- `void setBatchSize(int)` -> set logical batch size
- `int getUbatchSize()` -> physical batch size
- `void setUbatchSize(int)` -> set physical batch size
- `int getThreadsBatch()` -> batch thread count
- `void setThreadsBatch(int)` -> set batch thread count
- `int getMainGpu()` -> primary GPU index
- `void setMainGpu(int)` -> set primary GPU
- `String getDevices()` -> device list
- `void setDevices(String)` -> set device list
- `String getProvider()` -> AI provider
- `void setProvider(String)` -> set provider
- `String getRemoteModel()` -> remote model ID
- `void setRemoteModel(String)` -> set remote model
- `String getBaseUrl()` -> base URL
- `void setBaseUrl(String)` -> set base URL
- `String getApiKeyEnv()` -> API key env var
- `void setApiKeyEnv(String)` -> set API key env var
- `void setCachePrompt(boolean)` -> set prompt‑caching
- `String getReasoningEffort()` -> reasoning effort level
- `void setReasoningEffort(String)` -> set reasoning effort
- `int getReasoningBudgetTokens()` -> reasoning budget
- `void setReasoningBudgetTokens(int)` -> set reasoning budget
- `float getDryMultiplier()` -> DRY multiplier
- `void setDryMultiplier(float)` -> set DRY multiplier
- `float getDryBase()` -> DRY base
- `void setDryBase(float)` -> set DRY base
- `int getDryAllowedLength()` -> DRY allowed length
- `void setDryAllowedLength(int)` -> set DRY allowed length
- `int getDryPenaltyLastN()` -> DRY penalty window
- `void setDryPenaltyLastN(int)` -> set DRY penalty window
- `List<String> getDrySequenceBreakers()` -> unmodifiable DRY breakers
- `void setDrySequenceBreakers(List<String>)` -> set DRY breakers
- `List<String> getStopStrings()` -> unmodifiable stop strings
- `void setStopStrings(List<String>)` -> set stop strings
- `AiCalibration getCalibration()` -> calibration
- `void setCalibration(AiCalibration)` -> set calibration

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: setters replace `null` with defaults or empty collections.

#### Concurrency
- No synchronization; intended for single‑threaded configuration use.

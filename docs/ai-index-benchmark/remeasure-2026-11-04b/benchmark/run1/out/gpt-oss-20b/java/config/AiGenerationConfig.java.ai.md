### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:51:49Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable configuration holder for a single AI generation step, exposing all model, sampling, retry, and runtime options for Maven‑plugin driven AI providers.

#### Purpose
- Stores AI generation parameters (model path, context size, sampling, GPU/CPU settings, prompt‑caching, API details).
- Provides getters/setters for Maven configuration binding.
- Supplies defaults for every option.

#### Type
- `public class AiGenerationConfig` (class, public, mutable, Lombok `@ToString`).

#### Input
- Constructor: no arguments (defaults applied).
- Setter methods for each field (e.g. `setModelPath`, `setContextSize`, `setTemperature`, …).
- Optional `AiCalibration` calibration object.
- Lists: `drySequenceBreakers`, `stopStrings` (modifiable via setters).

#### Output
- Getter methods return current values (e.g. `getModelPath`, `getContextSize`, `getTemperature`, …).
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.
- `getCalibration` returns optional calibration.

#### Core logic
- All fields initialized to static defaults (`DEFAULT_*` constants).
- Setters assign values directly; some normalize `null` to defaults (e.g. `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`).
- List setters replace internal list with new list or empty list if `null`.
- No additional computation; configuration is a plain data holder.

#### Public API
- `AiGenerationConfig()` -> constructor
- `String getModelPath() -> model path`
- `void setModelPath(String)` -> set model path
- `int getContextSize() -> context size`
- `void setContextSize(int)` -> set context size
- `int getMaxOutputTokens() -> max output tokens`
- `void setMaxOutputTokens(int)` -> set max output tokens
- `float getTemperature() -> sampling temperature`
- `void setTemperature(float)` -> set temperature
- `int getThreads() -> CPU threads`
- `void setThreads(int)` -> set CPU threads
- `int getCharsPerToken() -> chars per token`
- `void setCharsPerToken(int)` -> set chars per token
- `int getMaxInputChars() -> max input chars`
- `void setMaxInputChars(int)` -> set max input chars
- `boolean isWarnOnTrim() -> trim warning flag`
- `void setWarnOnTrim(boolean)` -> set trim warning flag
- `float getTopP() -> nucleus threshold`
- `void setTopP(float)` -> set nucleus threshold
- `int getTopK() -> top‑k limit`
- `void setTopK(int)` -> set top‑k limit
- `float getMinP() -> min‑p threshold`
- `void setMinP(float)` -> set min‑p threshold
- `float getTopNSigma() -> top‑n‑sigma threshold`
- `void setTopNSigma(float)` -> set top‑n‑sigma threshold
- `float getRepeatPenalty() -> repetition penalty`
- `void setRepeatPenalty(float)` -> set repetition penalty
- `Boolean getChatTemplateEnableThinking() -> chat‑template thinking flag`
- `void setChatTemplateEnableThinking(Boolean)` -> set chat‑template thinking flag
- `boolean isCachePrompt() -> prompt‑caching flag`
- `void setCachePrompt(boolean)` -> set prompt‑caching flag
- `boolean isSwaFull() -> SWA full flag`
- `void setSwaFull(boolean)` -> set SWA full flag
- `int getCacheReuse() -> cache‑reuse chunk size`
- `void setCacheReuse(int)` -> set cache‑reuse chunk size
- `int getGpuLayers() -> GPU layers`
- `void setGpuLayers(int)` -> set GPU layers
- `int getSeed() -> RNG seed`
- `void setSeed(int)` -> set RNG seed
- `int getCpuMoeLayers() -> CPU MoE layers`
- `void setCpuMoeLayers(int)` -> set CPU MoE layers
- `int getCpuFfnLayers() -> CPU FFN layers`
- `void setCpuFfnLayers(int)` -> set CPU FFN layers
- `int getKvUnifiedPerSlot() -> KV unified per‑slot cap`
- `void setKvUnifiedPerSlot(int)` -> set KV unified per‑slot cap
- `String getLazyMode() -> tensor‑read laziness`
- `void setLazyMode(String)` -> set tensor‑read laziness
- `int getRepeatLastN() -> repeat‑penalty window`
- `void setRepeatLastN(int)` -> set repeat‑penalty window
- `String getCacheTypeK() -> KV cache type K`
- `void setCacheTypeK(String)` -> set KV cache type K
- `String getCacheTypeV() -> KV cache type V`
- `void setCacheTypeV(String)` -> set KV cache type V
- `boolean isFlashAttn() -> Flash Attention flag`
- `void setFlashAttn(boolean)` -> set Flash Attention flag
- `int getBatchSize() -> logical batch size`
- `void setBatchSize(int)` -> set logical batch size
- `int getUbatchSize() -> physical batch size`
- `void setUbatchSize(int)` -> set physical batch size
- `int getThreadsBatch() -> batch thread count`
- `void setThreadsBatch(int)` -> set batch thread count
- `int getMainGpu() -> primary GPU index`
- `void setMainGpu(int)` -> set primary GPU index
- `String getDevices() -> device list`
- `void setDevices(String)` -> set device list
- `String getProvider() -> AI provider`
- `void setProvider(String)` -> set AI provider
- `String getRemoteModel() -> remote model id`
- `void setRemoteModel(String)` -> set remote model id
- `String getBaseUrl() -> API base URL`
- `void setBaseUrl(String)` -> set API base URL
- `String getApiKeyEnv() -> API key env var`
- `void setApiKeyEnv(String)` -> set API key env var
- `String getReasoningEffort() -> reasoning effort`
- `void setReasoningEffort(String)` -> set reasoning effort
- `int getReasoningBudgetTokens() -> reasoning budget`
- `void setReasoningBudgetTokens(int)` -> set reasoning budget
- `float getDryMultiplier() -> DRY multiplier`
- `void setDryMultiplier(float)` -> set DRY multiplier
- `float getDryBase() -> DRY base`
- `void setDryBase(float)` -> set DRY base
- `int getDryAllowedLength() -> DRY allowed length`
- `void setDryAllowedLength(int)` -> set DRY allowed length
- `int getDryPenaltyLastN() -> DRY penalty last‑n`
- `void setDryPenaltyLastN(int)` -> set DRY penalty last‑n
- `List<String> getDrySequenceBreakers() -> DRY sequence breakers`
- `void setDrySequenceBreakers(List<String>)` -> set DRY sequence breakers
- `List<String> getStopStrings() -> stop strings`
- `void setStopStrings(List<String>)` -> set stop strings
- `AiCalibration getCalibration() -> calibration`
- `void setCalibration(AiCalibration)` -> set calibration

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters accept `null` for optional fields and normalize to defaults.

#### Concurrency
- No synchronization; intended for single‑threaded Maven configuration use.

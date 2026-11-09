### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:04Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, used by the Maven plugin to configure the chosen AI provider.

#### Purpose
- Stores configuration for a single AI generation step (model path, sampling, retry, prompt limits, stop strings).
- Exposes getters/setters for Maven plugin binding and provider code.

#### Type
- `public class AiGenerationConfig`  
  - Modifiers: `public`  
  - Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`  
  - Fields: 73 (including 2 lists, 1 calibration, 71 primitive/boxed fields).  
  - No supertype.

#### Input
- Constructor: no-arg, no-op.  
- Setter methods for each field (e.g., `setModelPath`, `setContextSize`, `setTemperature`, …).  
- `setDrySequenceBreakers(List<String>)`, `setStopStrings(List<String>)` accept `null` to reset.  
- `setCalibration(AiCalibration)`.

#### Output
- Getter methods return current field values.  
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.  
- `toString()` (via Lombok) prints all fields for diagnostics.

#### Core logic
- Default constants define sensible values for all parameters (e.g., `DEFAULT_CONTEXT_SIZE = 32768`, `DEFAULT_TEMPERATURE = 0.15f`, `DEFAULT_THREADS = Math.min(4, Runtime.getRuntime().availableProcessors())`).  
- Setters perform minimal validation:  
  - `setLazyMode` replaces `null` with `DEFAULT_LAZY_MODE`.  
  - `setCacheTypeK/V` replace `null` with `DEFAULT_CACHE_TYPE_K/V`.  
  - `setDevices` replaces `null` with `DEFAULT_DEVICES`.  
  - `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` replace `null` with their respective defaults.  
- No additional computation or side effects.

#### Public API
- `String getModelPath() -> model file path`  
- `void setModelPath(String)`  
- `int getContextSize() -> context window`  
- `void setContextSize(int)`  
- `int getMaxOutputTokens() -> max output tokens`  
- `void setMaxOutputTokens(int)`  
- `float getTemperature() -> sampling temperature`  
- `void setTemperature(float)`  
- `int getThreads() -> CPU threads`  
- `void setThreads(int)`  
- `int getCharsPerToken() -> chars/token ratio`  
- `void setCharsPerToken(int)`  
- `int getMaxInputChars() -> max input chars`  
- `void setMaxInputChars(int)`  
- `boolean isWarnOnTrim() -> trim warning flag`  
- `void setWarnOnTrim(boolean)`  
- `float getTopP() -> nucleus threshold`  
- `void setTopP(float)`  
- `int getTopK() -> top-k limit`  
- `void setTopK(int)`  
- `float getMinP() -> min-p threshold`  
- `void setMinP(float)`  
- `float getTopNSigma() -> top-n-sigma threshold`  
- `void setTopNSigma(float)`  
- `float getRepeatPenalty() -> repetition penalty`  
- `void setRepeatPenalty(float)`  
- `Boolean getChatTemplateEnableThinking() -> chat‑template flag`  
- `void setChatTemplateEnableThinking(Boolean)`  
- `boolean isCachePrompt() -> prompt‑caching flag`  
- `boolean isSwaFull() -> full‑SWA flag`  
- `void setSwaFull(boolean)`  
- `int getCacheReuse() -> prefix‑reuse chunk size`  
- `void setCacheReuse(int)`  
- `int getGpuLayers() -> GPU offload layers`  
- `void setGpuLayers(int)`  
- `int getCpuMoeLayers() -> CPU MoE layers`  
- `void setCpuMoeLayers(int)`  
- `int getSeed() -> RNG seed`  
- `void setSeed(int)`  
- `int getCpuFfnLayers() -> CPU FFN layers`  
- `void setCpuFfnLayers(int)`  
- `int getKvUnifiedPerSlot() -> KV cap`  
- `void setKvUnifiedPerSlot(int)`  
- `String getLazyMode() -> tensor‑read laziness`  
- `void setLazyMode(String)`  
- `int getRepeatLastN() -> repeat‑penalty window`  
- `void setRepeatLastN(int)`  
- `String getCacheTypeK() -> KV‑K type`  
- `void setCacheTypeK(String)`  
- `String getCacheTypeV() -> KV‑V type`  
- `void setCacheTypeV(String)`  
- `boolean isFlashAttn() -> Flash‑Attention flag`  
- `void setFlashAttn(boolean)`  
- `int getBatchSize() -> logical batch size`  
- `void setBatchSize(int)`  
- `int getUbatchSize() -> physical batch size`  
- `void setUbatchSize(int)`  
- `int getThreadsBatch() -> batch thread count`  
- `void setThreadsBatch(int)`  
- `int getMainGpu() -> primary GPU index`  
- `void setMainGpu(int)`  
- `String getDevices() -> device list`  
- `void setDevices(String)`  
- `String getProvider() -> AI provider`  
- `void setProvider(String)`  
- `String getRemoteModel() -> remote model id`  
- `void setRemoteModel(String)`  
- `String getBaseUrl() -> API base URL`  
- `void setBaseUrl(String)`  
- `String getApiKeyEnv() -> API key env var`  
- `void setApiKeyEnv(String)`  
- `String getReasoningEffort() -> reasoning effort`  
- `void setReasoningEffort(String)`  
- `int getReasoningBudgetTokens() -> reasoning budget`  
- `void setReasoningBudgetTokens(int)`  
- `float getDryMultiplier() -> DRY multiplier`  
- `void setDryMultiplier(float)`  
- `float getDryBase() -> DRY base`  
- `void setDryBase(float)`  
- `int getDryAllowedLength() -> DRY allowed length`  
- `void setDryAllowedLength(int)`  
- `int getDryPenaltyLastN() -> DRY penalty window`  
- `void setDryPenaltyLastN(int)`  
- `List<String> getDrySequenceBreakers() -> DRY breakers`  
- `void setDrySequenceBreakers(List<String>)`  
- `List<String> getStopStrings() -> stop strings`  
- `void setStopStrings(List<String>)`  
- `AiCalibration getCalibration() -> calibration`  
- `void setCalibration(AiCalibration)`  

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCalibration` (used as calibration type)

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Null handling: setters replace `null` with defaults; getters may return `null` for optional fields (`modelPath`, `chatTemplateEnableThinking`, `calibration`).

#### Concurrency
- No synchronization; intended for single‑threaded configuration use.  
- Immutable lists returned via `Collections.unmodifiableList`.

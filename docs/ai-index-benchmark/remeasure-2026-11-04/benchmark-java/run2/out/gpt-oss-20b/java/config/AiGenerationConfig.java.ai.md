### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:51Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A mutable bean that holds all AI‑generation parameters for a single model run, exposing getters/setters for configuration and default constants.

#### Purpose
- Stores AI generation settings (model path, sampling, prompt limits, GPU/offload, provider details).
- Serves as data transfer object between Maven plugin configuration and AI provider implementations.

#### Type
`public class AiGenerationConfig`  
- Class, mutable JavaBean.  
- Annotated `@ToString` (Lombok).  
- Suppresses null‑away warnings.

#### Input
- Constructor: no arguments.  
- Setters for all fields (e.g., `setModelPath`, `setContextSize`, `setTemperature`, …).  
- `setDrySequenceBreakers`, `setStopStrings`, `setCalibration` accept lists or nullable values.

#### Output
- Getters return current configuration values (e.g., `getModelPath`, `getContextSize`, `getTemperature`, …).  
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.  
- `getCalibration` returns optional calibration.

#### Core logic
- Defines numerous `public static final` defaults for context size, output tokens, temperature, threads, sampling thresholds, GPU/offload settings, cache types, batch sizes, device selection, provider, API key, DRY knobs, etc.  
- Each setter assigns the provided value, sometimes normalizing `null` to a default (e.g., `setLazyMode`, `setCacheTypeK`).  
- No additional computation; configuration is purely stored.

#### Public API
- `AiGenerationConfig()` → constructor  
- `String getModelPath()` → model file path  
- `void setModelPath(String)` → set model path  
- `int getContextSize()` → context window size  
- `void setContextSize(int)` → set context size  
- `int getMaxOutputTokens()` → max output tokens  
- `void setMaxOutputTokens(int)` → set max output tokens  
- `float getTemperature()` → sampling temperature  
- `void setTemperature(float)` → set temperature  
- `int getThreads()` → CPU thread count  
- `void setThreads(int)` → set threads  
- `int getCharsPerToken()` → chars per token ratio  
- `void setCharsPerToken(int)` → set chars per token  
- `int getMaxInputChars()` → max input characters  
- `void setMaxInputChars(int)` → set max input chars  
- `boolean isWarnOnTrim()` → trim warning flag  
- `void setWarnOnTrim(boolean)` → set trim warning flag  
- `float getTopP()` → nucleus sampling threshold  
- `void setTopP(float)` → set top‑p  
- `int getTopK()` → top‑k limit  
- `void setTopK(int)` → set top‑k  
- `float getMinP()` → min‑p threshold  
- `void setMinP(float)` → set min‑p  
- `float getTopNSigma()` → top‑n‑sigma threshold  
- `void setTopNSigma(float)` → set top‑n‑sigma  
- `float getRepeatPenalty()` → repetition penalty  
- `void setRepeatPenalty(float)` → set repeat penalty  
- `Boolean getChatTemplateEnableThinking()` → chat‑template thinking mode  
- `void setChatTemplateEnableThinking(Boolean)` → set chat‑template thinking mode  
- `boolean isCachePrompt()` → prompt‑caching flag  
- `boolean isSwaFull()` → full‑SWA flag  
- `void setSwaFull(boolean)` → set full‑SWA flag  
- `int getCacheReuse()` → cache‑reuse chunk size  
- `void setCacheReuse(int)` → set cache‑reuse  
- `int getGpuLayers()` → GPU offload layers  
- `void setGpuLayers(int)` → set GPU layers  
- `int getCpuMoeLayers()` → CPU MoE layers  
- `void setCpuMoeLayers(int)` → set CPU MoE layers  
- `int getSeed()` → RNG seed  
- `void setSeed(int)` → set seed  
- `int getCpuFfnLayers()` → CPU FFN layers  
- `void setCpuFfnLayers(int)` → set CPU FFN layers  
- `int getKvUnifiedPerSlot()` → KV unified per‑slot cap  
- `void setKvUnifiedPerSlot(int)` → set KV cap  
- `String getLazyMode()` → tensor‑read laziness  
- `void setLazyMode(String)` → set lazy mode  
- `int getRepeatLastN()` → repeat‑penalty window  
- `void setRepeatLastN(int)` → set repeat‑penalty window  
- `String getCacheTypeK()` → KV cache type K  
- `void setCacheTypeK(String)` → set cache type K  
- `String getCacheTypeV()` → KV cache type V  
- `void setCacheTypeV(String)` → set cache type V  
- `boolean isFlashAttn()` → Flash Attention flag  
- `void setFlashAttn(boolean)` → set Flash Attention  
- `int getBatchSize()` → logical batch size  
- `void setBatchSize(int)` → set batch size  
- `int getUbatchSize()` → physical batch size  
- `void setUbatchSize(int)` → set ubatch size  
- `int getThreadsBatch()` → batch thread count  
- `void setThreadsBatch(int)` → set batch threads  
- `int getMainGpu()` → primary GPU index  
- `void setMainGpu(int)` → set main GPU  
- `String getDevices()` → device list  
- `void setDevices(String)` → set devices  
- `String getProvider()` → AI provider  
- `void setProvider(String)` → set provider  
- `String getRemoteModel()` → remote model ID  
- `void setRemoteModel(String)` → set remote model  
- `String getBaseUrl()` → base URL  
- `void setBaseUrl(String)` → set base URL  
- `String getApiKeyEnv()` → API key env var name  
- `void setApiKeyEnv(String)` → set API key env var  
- `String getReasoningEffort()` → reasoning effort level  
- `void setReasoningEffort(String)` → set reasoning effort  
- `int getReasoningBudgetTokens()` → reasoning budget  
- `void setReasoningBudgetTokens(int)` → set reasoning budget  
- `float getDryMultiplier()` → DRY multiplier  
- `void setDryMultiplier(float)` → set DRY multiplier  
- `float getDryBase()` → DRY base  
- `void setDryBase(float)` → set DRY base  
- `int getDryAllowedLength()` → DRY allowed length  
- `void setDryAllowedLength(int)` → set DRY allowed length  
- `int getDryPenaltyLastN()` → DRY penalty window  
- `void setDryPenaltyLastN(int)` → set DRY penalty window  
- `List<String> getDrySequenceBreakers()` → DRY sequence breakers  
- `void setDrySequenceBreakers(List<String>)` → set DRY sequence breakers  
- `List<String> getStopStrings()` → stop strings  
- `void setStopStrings(List<String>)` → set stop strings  
- `AiCalibration getCalibration()` → calibration  
- `void setCalibration(AiCalibration)` → set calibration  

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCalibration`.

#### Exceptions / Errors
- No checked exceptions thrown.  
- Setters accept `null` for nullable fields, normalizing to defaults.

#### Concurrency
- No synchronization; the bean is intended for single‑threaded configuration use.  
- Immutable lists returned via `Collections.unmodifiableList`.

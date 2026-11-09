### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:41Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A mutable JavaBean that holds all AI‑generation parameters for a single model run, exposing getters/setters for configuration values used by Maven plugin and AI providers.

#### Purpose
- Stores AI generation settings (model path, sampling, prompt limits, provider details, GPU options, etc.).
- Serves as a data transfer object between Maven configuration and AI provider implementations.

#### Type
`public class AiGenerationConfig`  
- Modifiers: `public`  
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`  
- Extends: none  
- Implements: none  

#### Input
- Constructor: no-arg, defaults to all static defaults.  
- Setters for each field: `setModelPath`, `setContextSize`, `setMaxOutputTokens`, `setTemperature`, `setThreads`, `setCharsPerToken`, `setMaxInputChars`, `setWarnOnTrim`, `setTopP`, `setTopK`, `setMinP`, `setTopNSigma`, `setRepeatPenalty`, `setChatTemplateEnableThinking`, `setCachePrompt`, `setSwaFull`, `setCacheReuse`, `setGpuLayers`, `setSeed`, `setCpuMoeLayers`, `setCpuFfnLayers`, `setKvUnifiedPerSlot`, `setLazyMode`, `setRepeatLastN`, `setCacheTypeK`, `setCacheTypeV`, `setFlashAttn`, `setBatchSize`, `setUbatchSize`, `setThreadsBatch`, `setMainGpu`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`, `setReasoningEffort`, `setReasoningBudgetTokens`, `setDryMultiplier`, `setDryBase`, `setDryAllowedLength`, `setDryPenaltyLastN`, `setDrySequenceBreakers`, `setStopStrings`, `setCalibration`.

#### Output
- Getters for all fields: `getModelPath`, `getContextSize`, `getMaxOutputTokens`, `getTemperature`, `getThreads`, `getCharsPerToken`, `getMaxInputChars`, `isWarnOnTrim`, `getTopP`, `getTopK`, `getMinP`, `getTopNSigma`, `getRepeatPenalty`, `getChatTemplateEnableThinking`, `isCachePrompt`, `isSwaFull`, `getCacheReuse`, `getGpuLayers`, `getSeed`, `getCpuMoeLayers`, `getCpuFfnLayers`, `getKvUnifiedPerSlot`, `getLazyMode`, `getRepeatLastN`, `getCacheTypeK`, `getCacheTypeV`, `isFlashAttn`, `getBatchSize`, `getUbatchSize`, `getThreadsBatch`, `getMainGpu`, `getDevices`, `getProvider`, `getRemoteModel`, `getBaseUrl`, `getApiKeyEnv`, `getReasoningEffort`, `getReasoningBudgetTokens`, `getDryMultiplier`, `getDryBase`, `getDryAllowedLength`, `getDryPenaltyLastN`, `getDrySequenceBreakers`, `getStopStrings`, `getCalibration`.  
- `getDrySequenceBreakers` and `getStopStrings` return unmodifiable lists.

#### Core logic
- All fields initialized to static defaults.  
- Setters perform minimal validation (e.g., null → default, empty → default).  
- No additional computation; the class is a plain data holder.

#### Public API
- `AiGenerationConfig()` -> empty config  
- `getModelPath() -> String?` → model file path  
- `setModelPath(String?) -> void` → set model path  
- `getContextSize() -> int` → context window  
- `setContextSize(int) -> void` → set context size  
- `getMaxOutputTokens() -> int` → max output tokens  
- `setMaxOutputTokens(int) -> void` → set max output tokens  
- `getTemperature() -> float` → sampling temperature  
- `setTemperature(float) -> void` → set temperature  
- `getThreads() -> int` → CPU threads  
- `setThreads(int) -> void` → set threads  
- `getCharsPerToken() -> int` → chars per token  
- `setCharsPerToken(int) -> void` → set chars per token  
- `getMaxInputChars() -> int` → max input chars  
- `setMaxInputChars(int) -> void` → set max input chars  
- `isWarnOnTrim() -> boolean` → trim warning flag  
- `setWarnOnTrim(boolean) -> void` → set trim warning  
- `getTopP() -> float` → nucleus threshold  
- `setTopP(float) -> void` → set top‑p  
- `getTopK() -> int` → top‑k limit  
- `setTopK(int) -> void` → set top‑k  
- `getMinP() -> float` → min‑p threshold  
- `setMinP(float) -> void` → set min‑p  
- `getTopNSigma() -> float` → top‑n‑sigma threshold  
- `setTopNSigma(float) -> void` → set top‑n‑sigma  
- `getRepeatPenalty() -> float` → repetition penalty  
- `setRepeatPenalty(float) -> void` → set repeat penalty  
- `getChatTemplateEnableThinking() -> Boolean?` → chat‑template thinking flag  
- `setChatTemplateEnableThinking(Boolean?) -> void` → set flag  
- `isCachePrompt() -> boolean` → prompt‑caching flag  
- `isSwaFull() -> boolean` → full‑SWA flag  
- `setSwaFull(boolean) -> void` → set full‑SWA  
- `getCacheReuse() -> int` → cache‑reuse chunk size  
- `setCacheReuse(int) -> void` → set cache‑reuse  
- `getGpuLayers() -> int` → GPU offload layers  
- `setGpuLayers(int) -> void` → set GPU layers  
- `getSeed() -> int` → RNG seed  
- `setSeed(int) -> void` → set seed  
- `getCpuMoeLayers() -> int` → CPU MoE layers  
- `setCpuMoeLayers(int) -> void` → set CPU MoE  
- `getCpuFfnLayers() -> int` → CPU FFN layers  
- `setCpuFfnLayers(int) -> void` → set CPU FFN  
- `getKvUnifiedPerSlot() -> int` → KV unified per‑slot cap  
- `setKvUnifiedPerSlot(int) -> void` → set cap  
- `getLazyMode() -> String` → tensor‑read laziness  
- `setLazyMode(String?) -> void` → set laziness  
- `getRepeatLastN() -> int` → repeat‑penalty window  
- `setRepeatLastN(int) -> void` → set window  
- `getCacheTypeK() -> String` → KV cache type K  
- `setCacheTypeK(String?) -> void` → set type K  
- `getCacheTypeV() -> String` → KV cache type V  
- `setCacheTypeV(String?) -> void` → set type V  
- `isFlashAttn() -> boolean` → Flash Attention flag  
- `setFlashAttn(boolean) -> void` → set flag  
- `getBatchSize() -> int` → logical batch size  
- `setBatchSize(int) -> void` → set batch size  
- `getUbatchSize() -> int` → physical batch size  
- `setUbatchSize(int) -> void` → set physical batch size  
- `getThreadsBatch() -> int` → batch thread count  
- `setThreadsBatch(int) -> void` → set batch threads  
- `getMainGpu() -> int` → primary GPU index  
- `setMainGpu(int) -> void` → set GPU index  
- `getDevices() -> String` → device list  
- `setDevices(String) -> void` → set devices  
- `getProvider() -> String` → AI provider  
- `setProvider(String?) -> void` → set provider  
- `getRemoteModel() -> String` → remote model id  
- `setRemoteModel(String?) -> void` → set remote model  
- `getBaseUrl() -> String` → base URL  
- `setBaseUrl(String?) -> void` → set base URL  
- `getApiKeyEnv() -> String` → API key env var name  
- `setApiKeyEnv(String?) -> void` → set env var  
- `getReasoningEffort() -> String` → reasoning effort level  
- `setReasoningEffort(String) -> void` → set effort  
- `getReasoningBudgetTokens() -> int` → reasoning budget  
- `setReasoningBudgetTokens(int) -> void` → set budget  
- `getDryMultiplier() -> float` → DRY multiplier  
- `setDryMultiplier(float) -> void` → set multiplier  
- `getDryBase() -> float` → DRY base  
- `setDryBase(float) -> void` → set base  
- `getDryAllowedLength() -> int` → DRY allowed length  
- `setDryAllowedLength(int) -> void` → set length  
- `getDryPenaltyLastN() -> int` → DRY penalty window  
- `setDryPenaltyLastN(int) -> void` → set window  
- `getDrySequenceBreakers() -> List<String>` → DRY sequence breakers  
- `setDrySequenceBreakers(List<String>?) -> void` → set breakers  
- `getStopStrings() -> List<String>` → stop strings  
- `setStopStrings(List<String>?) -> void` → set stop strings  
- `getCalibration() -> AiCalibration?` → calibration  
- `setCalibration(AiCalibration?) -> void` → set calibration  

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCalibration`  

#### Exceptions / Errors
- No checked exceptions thrown.  
- Setters perform null checks, defaulting to static defaults; no validation errors.

#### Concurrency
- No synchronization; the class is not thread‑safe.  
- Intended for single‑threaded configuration building.

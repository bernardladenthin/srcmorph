### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:09Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> <blockquote>Mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider implementations.</blockquote>

#### Purpose
- Stores AI generation settings (model path, sampling, retry, prompt trimming, stop strings).
- Exposes getters/setters for Maven plugin configuration.

#### Type
- `public class AiGenerationConfig`  
  - Modifiers: `public`  
  - Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`  
  - Extends: none  
  - Implements: none

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each field (e.g., `setModelPath(String)`, `setTemperature(float)`, etc.).
- `setDrySequenceBreakers(List<String>)`, `setStopStrings(List<String>)`, `setCalibration(AiCalibration)` accept `null` to reset defaults.

#### Output
- Getter methods return current configuration values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.
- `toString()` (via Lombok) outputs all fields for diagnostics.

#### Core logic
- Default constants define standard AI generation parameters.
- Fields initialized to these defaults.
- Setters replace values; `null` arguments reset to defaults or empty lists.
- No additional computation; configuration is purely data‑carrying.

#### Public API
- `getModelPath() -> String?` – model file path  
- `setModelPath(String?)` – set model path  
- `getContextSize() -> int` – context window size  
- `setContextSize(int)` – set context size  
- `getMaxOutputTokens() -> int` – max output tokens  
- `setMaxOutputTokens(int)` – set max output tokens  
- `getTemperature() -> float` – sampling temperature  
- `setTemperature(float)` – set temperature  
- `getThreads() -> int` – CPU threads  
- `setThreads(int)` – set threads  
- `getCharsPerToken() -> int` – chars per token ratio  
- `setCharsPerToken(int)` – set chars per token  
- `getMaxInputChars() -> int` – max input chars  
- `setMaxInputChars(int)` – set max input chars  
- `isWarnOnTrim() -> boolean` – trim warning flag  
- `setWarnOnTrim(boolean)` – set trim warning flag  
- `getTopP() -> float` – nucleus sampling threshold  
- `setTopP(float)` – set top‑p  
- `getTopK() -> int` – top‑k limit  
- `setTopK(int)` – set top‑k  
- `getRepeatPenalty() -> float` – repetition penalty  
- `setRepeatPenalty(float)` – set repeat penalty  
- `getMinP() -> float` – min‑p threshold  
- `setMinP(float)` – set min‑p  
- `getTopNSigma() -> float` – top‑n‑sigma threshold  
- `setTopNSigma(float)` – set top‑n‑sigma  
- `getChatTemplateEnableThinking() -> Boolean?` – chat‑template thinking mode  
- `setChatTemplateEnableThinking(Boolean?)` – set thinking mode  
- `isCachePrompt() -> boolean` – prompt‑caching flag  
- `isSwaFull() -> boolean` – full‑SWA flag  
- `setSwaFull(boolean)` – set full‑SWA  
- `getCacheReuse() -> int` – cache‑reuse chunk size  
- `setCacheReuse(int)` – set cache‑reuse  
- `getGpuLayers() -> int` – GPU offload layers  
- `setGpuLayers(int)` – set GPU layers  
- `getCpuMoeLayers() -> int` – CPU MoE layers  
- `setCpuMoeLayers(int)` – set CPU MoE layers  
- `getSeed() -> int` – RNG seed  
- `setSeed(int)` – set seed  
- `getCpuFfnLayers() -> int` – CPU FFN layers  
- `setCpuFfnLayers(int)` – set CPU FFN layers  
- `getKvUnifiedPerSlot() -> int` – KV unified per‑slot cap  
- `setKvUnifiedPerSlot(int)` – set KV cap  
- `getLazyMode() -> String` – tensor‑read laziness  
- `setLazyMode(String?)` – set lazy mode  
- `getRepeatLastN() -> int` – repeat‑penalty window  
- `setRepeatLastN(int)` – set repeat‑penalty window  
- `getCacheTypeK() -> String` – KV cache type K  
- `setCacheTypeK(String?)` – set cache type K  
- `getCacheTypeV() -> String` – KV cache type V  
- `setCacheTypeV(String?)` – set cache type V  
- `isFlashAttn() -> boolean` – Flash Attention flag  
- `setFlashAttn(boolean)` – set Flash Attention  
- `getBatchSize() -> int` – logical batch size  
- `setBatchSize(int)` – set logical batch size  
- `getUbatchSize() -> int` – physical batch size  
- `setUbatchSize(int)` – set physical batch size  
- `getThreadsBatch() -> int` – batch thread count  
- `setThreadsBatch(int)` – set batch thread count  
- `getMainGpu() -> int` – primary GPU index  
- `setMainGpu(int)` – set primary GPU  
- `getDevices() -> String` – device list  
- `setDevices(String)` – set device list  
- `getProvider() -> String` – AI provider  
- `setProvider(String?)` – set provider  
- `getRemoteModel() -> String` – remote model ID  
- `setRemoteModel(String?)` – set remote model ID  
- `getBaseUrl() -> String` – base URL  
- `setBaseUrl(String?)` – set base URL  
- `getApiKeyEnv() -> String` – API key env var name  
- `setApiKeyEnv(String?)` – set API key env var  
- `getReasoningEffort() -> String` – reasoning effort level  
- `setReasoningEffort(String)` – set reasoning effort  
- `getReasoningBudgetTokens() -> int` – reasoning budget tokens  
- `setReasoningBudgetTokens(int)` – set reasoning budget  
- `getDryMultiplier() -> float` – DRY multiplier  
- `setDryMultiplier(float)` – set DRY multiplier  
- `getDryBase() -> float` – DRY base  
- `setDryBase(float)` – set DRY base  
- `getDryAllowedLength() -> int` – DRY allowed length  
- `setDryAllowedLength(int)` – set DRY allowed length  
- `getDryPenaltyLastN() -> int` – DRY penalty window  
- `setDryPenaltyLastN(int)` – set DRY penalty window  
- `getDrySequenceBreakers() -> List<String>` – DRY sequence breakers  
- `setDrySequenceBreakers(List<String>?)` – set DRY sequence breakers  
- `getStopStrings() -> List<String>` – stop strings  
- `setStopStrings(List<String>?)` – set stop strings  
- `getCalibration() -> AiCalibration?` – calibration  
- `setCalibration(AiCalibration?)` – set calibration  

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters accept `null` for optional fields; they reset to defaults or empty collections.

#### Concurrency
- No synchronization; class is mutable and not thread‑safe. Use per‑thread instances or external synchronization.

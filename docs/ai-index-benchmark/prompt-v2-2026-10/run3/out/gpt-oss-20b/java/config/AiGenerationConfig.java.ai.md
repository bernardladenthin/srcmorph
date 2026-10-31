### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:02:21Z
- G: 
- A: 0.0.0
- X: file
---
> Holds mutable AI generation parameters for a single model run, exposed as JavaBeans for Maven plugin configuration.

#### Purpose
- Stores all AI generation settings (model path, sampling, cache, GPU, API, etc.) for a single run.
- Provides getters/setters for Maven configuration binding.

#### Type
`public class AiGenerationConfig`  
class, public, mutable JavaBean, Lombok `@ToString`.

#### Input
- Constructor: no arguments.
- Setter methods for each configuration field (44 fields).
- Optional `AiCalibration` object via `setCalibration`.

#### Output
- Getter methods return current field values.
- `getDrySequenceBreakers` / `getStopStrings` return unmodifiable lists.
- `toString` (Lombok) for diagnostics.

#### Core logic
- No complex algorithms; simply stores and exposes configuration values.
- Default constants provide sensible defaults for all parameters.
- Setter methods normalize nulls to defaults (e.g., `DEFAULT_LAZY_MODE`, `DEFAULT_DEVICES`).

#### Public API
- `AiGenerationConfig()` → construct empty config  
- `getModelPath()` / `setModelPath(String)` → model file path  
- `getContextSize()` / `setContextSize(int)` → context window size  
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` → max output tokens  
- `getTemperature()` / `setTemperature(float)` → sampling temperature  
- `getThreads()` / `setThreads(int)` → CPU thread count  
- `getCharsPerToken()` / `setCharsPerToken(int)` → chars‑per‑token ratio  
- `getMaxInputChars()` / `setMaxInputChars(int)` → max input chars  
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` → trim warning flag  
- `getTopP()` / `setTopP(float)` → nucleus sampling threshold  
- `getTopK()` / `setTopK(int)` → top‑k limit  
- `getMinP()` / `setMinP(float)` → min‑p threshold  
- `getTopNSigma()` / `setTopNSigma(float)` → top‑n‑sigma threshold  
- `getRepeatPenalty()` / `setRepeatPenalty(float)` → repetition penalty  
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` → chat‑template thinking mode  
- `isCachePrompt()` / `setCachePrompt(boolean)` → prompt‑caching flag  
- `isSwaFull()` / `setSwaFull(boolean)` → full‑SWA flag  
- `getCacheReuse()` / `setCacheReuse(int)` → KV prefix reuse chunk size  
- `getGpuLayers()` / `setGpuLayers(int)` → GPU layer offload count  
- `getSeed()` / `setSeed(int)` → RNG seed  
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` → CPU MoE layers  
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` → CPU FFN layers  
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` → unified KV cap  
- `getLazyMode()` / `setLazyMode(String)` → tensor‑read laziness  
- `getRepeatLastN()` / `setRepeatLastN(int)` → repeat‑penalty window  
- `getCacheTypeK()` / `setCacheTypeK(String)` → KV cache type K  
- `getCacheTypeV()` / `setCacheTypeV(String)` → KV cache type V  
- `isFlashAttn()` / `setFlashAttn(boolean)` → Flash Attention flag  
- `getBatchSize()` / `setBatchSize(int)` → logical batch size  
- `getUbatchSize()` / `setUbatchSize(int)` → physical batch size  
- `getThreadsBatch()` / `setThreadsBatch(int)` → batch thread count  
- `getMainGpu()` / `setMainGpu(int)` → primary GPU index  
- `getDevices()` / `setDevices(String)` → device list  
- `getProvider()` / `setProvider(String)` → AI provider name  
- `getRemoteModel()` / `setRemoteModel(String)` → remote model ID  
- `getBaseUrl()` / `setBaseUrl(String)` → API base URL  
- `getApiKeyEnv()` / `setApiKeyEnv(String)` → API key env var name  
- `getReasoningEffort()` / `setReasoningEffort(String)` → reasoning‑effort level  
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` → reasoning budget  
- `getDryMultiplier()` / `setDryMultiplier(float)` → DRY multiplier  
- `getDryBase()` / `setDryBase(float)` → DRY base  
- `getDryAllowedLength()` / `setDryAllowedLength(int)` → DRY allowed length  
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` → DRY penalty window  
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` → DRY sequence breakers  
- `getStopStrings()` / `setStopStrings(List<String>)` → stop strings  
- `getCalibration()` / `setCalibration(AiCalibration)` → per‑machine timing calibration

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Setters accept `null` and normalize to defaults; no `NullPointerException`.

#### Concurrency
- No synchronization; intended for single‑threaded configuration use.  
- Immutable lists returned via `Collections.unmodifiableList`.

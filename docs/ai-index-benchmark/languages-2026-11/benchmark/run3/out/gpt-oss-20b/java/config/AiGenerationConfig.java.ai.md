### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:09Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration holder for AI generation parameters, bridging Maven plugin settings to AI provider APIs.

#### Purpose
- Holds AI generation settings for a single model run.
- Exposes getters/setters for Maven plugin configuration.

#### Type
`public class AiGenerationConfig` – mutable JavaBean, Lombok `@ToString`, no special modifiers.

#### Input
- Constructor: no arguments.
- All fields set via public setters or Maven plugin injection.

#### Output
- Getter methods expose current configuration.
- `toString()` (Lombok) for diagnostics.

#### Core logic
- Stores default constants for all AI generation knobs.
- Provides getters/setters that preserve defaults when `null` or empty values are passed.
- Uses `Collections.unmodifiableList` for list getters to prevent external mutation.
- Handles nullable strings and booleans with default fallbacks.

#### Public API
```
getModelPath() -> String?  // model file path
setModelPath(String?) -> void  // set model path
getContextSize() -> int  // context window size
setContextSize(int) -> void  // set context size
getMaxOutputTokens() -> int  // max output tokens
setMaxOutputTokens(int) -> void  // set max output tokens
getTemperature() -> float  // sampling temperature
setTemperature(float) -> void  // set temperature
getThreads() -> int  // CPU threads
setThreads(int) -> void  // set threads
getCharsPerToken() -> int  // chars per token ratio
setCharsPerToken(int) -> void  // set chars per token
getMaxInputChars() -> int  // max input chars
setMaxInputChars(int) -> void  // set max input chars
isWarnOnTrim() -> boolean  // trim warning flag
setWarnOnTrim(boolean) -> void  // set trim warning
getTopP() -> float  // nucleus sampling threshold
setTopP(float) -> void  // set top‑p
getTopK() -> int  // top‑k limit
setTopK(int) -> void  // set top‑k
getMinP() -> float  // min‑p threshold
setMinP(float) -> void  // set min‑p
getTopNSigma() -> float  // top‑n‑sigma threshold
setTopNSigma(float) -> void  // set top‑n‑sigma
getRepeatPenalty() -> float  // repetition penalty
setRepeatPenalty(float) -> void  // set repeat penalty
getChatTemplateEnableThinking() -> Boolean?  // chat‑template thinking mode
setChatTemplateEnableThinking(Boolean?) -> void  // set thinking mode
isCachePrompt() -> boolean  // prompt‑caching flag
isSwaFull() -> boolean  // full‑SWA flag
setSwaFull(boolean) -> void  // set full‑SWA
getCacheReuse() -> int  // KV prefix‑reuse chunk size
setCacheReuse(int) -> void  // set cache reuse
getGpuLayers() -> int  // GPU offload layers
setGpuLayers(int) -> void  // set GPU layers
getSeed() -> int  // RNG seed
setSeed(int) -> void  // set seed
getCpuMoeLayers() -> int  // CPU MoE layers
setCpuMoeLayers(int) -> void  // set CPU MoE layers
getCpuFfnLayers() -> int  // CPU FFN layers
setCpuFfnLayers(int) -> void  // set CPU FFN layers
getKvUnifiedPerSlot() -> int  // unified KV cap
setKvUnifiedPerSlot(int) -> void  // set KV cap
getLazyMode() -> String  // tensor‑read laziness
setLazyMode(String?) -> void  // set lazy mode
getRepeatLastN() -> int  // repeat‑penalty window
setRepeatLastN(int) -> void  // set repeat‑last‑n
getCacheTypeK() -> String  // KV cache type K
setCacheTypeK(String?) -> void  // set cache type K
getCacheTypeV() -> String  // KV cache type V
setCacheTypeV(String?) -> void  // set cache type V
isFlashAttn() -> boolean  // Flash Attention flag
setFlashAttn(boolean) -> void  // set Flash Attn
getBatchSize() -> int  // logical batch size
setBatchSize(int) -> void  // set batch size
getUbatchSize() -> int  // physical batch size
setUbatchSize(int) -> void  // set ubatch size
getThreadsBatch() -> int  // batch thread count
setThreadsBatch(int) -> void  // set threads‑batch
getMainGpu() -> int  // primary GPU index
setMainGpu(int) -> void  // set main GPU
getDevices() -> String  // device list
setDevices(String) -> void  // set devices
getProvider() -> String  // AI provider
setProvider(String) -> void  // set provider
getRemoteModel() -> String  // remote model ID
setRemoteModel(String) -> void  // set remote model
getBaseUrl() -> String  // base URL
setBaseUrl(String) -> void  // set base URL
getApiKeyEnv() -> String  // API key env var
setApiKeyEnv(String) -> void  // set API key env
getReasoningEffort() -> String  // reasoning effort level
setReasoningEffort(String) -> void  // set reasoning effort
getReasoningBudgetTokens() -> int  // reasoning budget
setReasoningBudgetTokens(int) -> void  // set reasoning budget
getDryMultiplier() -> float  // DRY multiplier
setDryMultiplier(float) -> void  // set DRY multiplier
getDryBase() -> float  // DRY base
setDryBase(float) -> void  // set DRY base
getDryAllowedLength() -> int  // DRY allowed length
setDryAllowedLength(int) -> void  // set DRY allowed length
getDryPenaltyLastN() -> int  // DRY penalty window
setDryPenaltyLastN(int) -> void  // set DRY penalty last‑n
getDrySequenceBreakers() -> List<String>  // DRY sequence breakers
setDrySequenceBreakers(List<String>?) -> void  // set DRY breakers
getStopStrings() -> List<String>  // stop strings
setStopStrings(List<String>?) -> void  // set stop strings
getCalibration() -> AiCalibration?  // per‑machine calibration
setCalibration(AiCalibration?) -> void  // set calibration
```

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: setters replace `null` with defaults; getters may return `null` for nullable fields.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building. Instances are mutable JavaBeans.

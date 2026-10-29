### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:23Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and carries all parameters for a single AI generation step between the Maven configuration layer and AI provider implementations.

#### Purpose
Configuration object that carries all parameters for a single AI generation step (model path, sampling parameters, retry policy, input trimming limits, stop strings) between the Maven configuration layer and the AI provider implementations.

#### Type
class; final; key generics: java.util.List<java.lang.String>; notable annotations: @ToString from Lombok; suppresses NullAway.Init and initialization.fields.uninitialized warnings.

#### Input
Constructor: AiGenerationConfig() with no-op body. Injected dependencies: none. Consumed fields from Maven plugin framework: populated through setters by the framework and managed by identity. Read resources: none directly; relies on static defaults and setter-injected values.

#### Output
Return types: getModelPath() -> @Nullable String, getContextSize() -> int, getMaxOutputTokens() -> int, getTemperature() -> float, getThreads() -> int, getCharsPerToken() -> int, getMaxInputChars() -> int, isWarnOnTrim() -> boolean, getTopP() -> float, getTopK() -> int, getRepeatPenalty() -> float, getMinP() -> float, getTopNSigma() -> float, getChatTemplateEnableThinking() -> @Nullable Boolean, isCachePrompt() -> boolean, isSwaFull() -> boolean, getCacheReuse() -> int, getGpuLayers() -> int, getSeed() -> int, getCpuMoeLayers() -> int, getCpuFfnLayers() -> int, getKvUnifiedPerSlot() -> int, getLazyMode() -> String, getRepeatLastN() -> int, getCacheTypeK() -> String, getCacheTypeV() -> String, isFlashAttn() -> boolean, getBatchSize() -> int, getUbatchSize() -> int, getThreadsBatch() -> int, getMainGpu() -> int, getDevices() -> String, getProvider() -> String, getRemoteModel() -> String, getBaseUrl() -> String, getApiKeyEnv() -> String, getReasoningEffort() -> String, getReasoningBudgetTokens() -> int, getDryMultiplier() -> float, getDryBase() -> float, getDryAllowedLength() -> int, getDryPenaltyLastN() -> int, getDrySequenceBreakers() -> java.util.List<java.lang.String> (unmodifiable view), getStopStrings() -> java.util.List<java.lang.String> (unmodifiable view), getCalibration() -> @Nullable AiCalibration. Produced state: instance fields set via setters; side effects: none beyond field mutation.

#### Core logic
- Provides a mutable JavaBeans-style configuration holder for AI generation parameters.
- All instance fields have corresponding getters and setters for framework-driven population.
- Static final fields define defaults for context size (32768 tokens), max output tokens (128), temperature (0.15f), threads (min(4, available processors)), chars per token (4), max input chars (120000), warn on trim (true), top-p (0.9f), top-k (40), min-p (0.0f), top-n-sigma (-1.0f), repeat penalty (1.0f), chat template thinking (null), cache prompt (true), SWA full (true), cache reuse (256), GPU layers (-1), seed (-1), CPU MoE layers (-1), CPU FFN layers (-1), KV unified per slot (-1), lazy mode (""), repeat last n (-1), cache type K and V (""), Flash Attention (false), batch size (-1), physical batch size (-1), threads batch (-1), main GPU (-1), devices (""), provider (""), remote model (""), base URL ("https://openrouter.ai/api/v1"), API key env (""), dry multiplier (0.0f), dry base (1.75f), dry allowed length (2), dry penalty last n (-1).
- Fields initialized to defaults: modelPath (null), contextSize (DEFAULT_CONTEXT_SIZE), maxOutputTokens (DEFAULT_MAX_OUTPUT_TOKENS), temperature (DEFAULT_TEMPERATURE), threads (DEFAULT_THREADS), charsPerToken (DEFAULT_CHARS_PER_TOKEN), maxInputChars (DEFAULT_MAX_INPUT_CHARS), warnOnTrim (DEFAULT_WARN_ON_TRIM), topP (DEFAULT_TOP_P), topK (DEFAULT_TOP_K), minP (DEFAULT_MIN_P), topNSigma (DEFAULT_TOP_N_SIGMA), repeatPenalty (DEFAULT_REPEAT_PENALTY), chatTemplateEnableThinking (DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING), cachePrompt (DEFAULT_CACHE_PROMPT), swaFull (DEFAULT_SWA_FULL), cacheReuse (DEFAULT_CACHE_REUSE), gpuLayers (DEFAULT_GPU_LAYERS), seed (DEFAULT_SEED), cpuMoeLayers (DEFAULT_CPU_MOE_LAYERS), cpuFfnLayers (DEFAULT_CPU_FFN_LAYERS), kvUnifiedPerSlot (DEFAULT_KV_UNIFIED_PER_SLOT), lazyMode (DEFAULT_LAZY_MODE), repeatLastN (DEFAULT_REPEAT_LAST_N), cacheTypeK (DEFAULT_CACHE_TYPE_K), cacheTypeV (DEFAULT_CACHE_TYPE_V), flashAttn (DEFAULT_FLASH_ATTN), batchSize (DEFAULT_BATCH_SIZE), ubatchSize (DEFAULT_UBATCH_SIZE), threadsBatch (DEFAULT_THREADS_BATCH), mainGpu (DEFAULT_MAIN_GPU), devices (DEFAULT_DEVICES), provider (DEFAULT_PROVIDER), remoteModel (DEFAULT_REMOTE_MODEL), baseUrl (DEFAULT_BASE_URL), apiKeyEnv (DEFAULT_API_KEY_ENV), reasoningEffort (DEFAULT_REASONING_EFFORT), reasoningBudgetTokens (DEFAULT_REASONING_BUDGET_TOKENS), dryMultiplier (DEFAULT_DRY_MULTIPLIER), dryBase (DEFAULT_DRY_BASE), dryAllowedLength (DEFAULT_DRY_ALLOWED_LENGTH), dryPenaltyLastN (DEFAULT_DRY_PENALTY_LAST_N), drySequenceBreakers (new ArrayList<>()), stopStrings (new ArrayList<>()). calibration (null).
- Provides getDrySequenceBreakers() returning unmodifiable view of drySequenceBreakers list; setDrySequenceBreakers() resets to empty list on null input.
- Provides getStopStrings() returning unmodifiable view of stopStrings list; setStopStrings() resets to empty list on null input.

#### Public API
- AiGenerationConfig() -> new AiGenerationConfig with defaults applied
- getModelPath() -> @Nullable String
- setModelPath(@Nullable String modelPath) -> void
- getContextSize() -> int
- setContextSize(int contextSize) -> void
- getMaxOutputTokens() -> int
- setMaxOutputTokens(int maxOutputTokens) -> void
- getTemperature() -> float
- setTemperature(float temperature) -> void
- getThreads() -> int
- setThreads(int threads) -> void
- getCharsPerToken() -> int
- setCharsPerToken(int charsPerToken) -> void
- getMaxInputChars() -> int
- setMaxInputChars(int maxInputChars) -> void
- isWarnOnTrim() -> boolean
- setWarnOnTrim(boolean warnOnTrim) -> void
- getTopP() -> float
- setTopP(float topP) -> void
- getTopK() -> int
- setTopK(int topK) -> void
- getRepeatPenalty() -> float
- setRepeatPenalty(float repeatPenalty) -> void
- getMinP() -> float
- setMinP(float minP) -> void
- getTopNSigma() -> float
- setTopNSigma(float topNSigma) -> void
- getChatTemplateEnableThinking() -> @Nullable Boolean
- setChatTemplateEnableThinking(@Nullable Boolean chatTemplateEnableThinking) -> void
- isCachePrompt() -> boolean
- isSwaFull() -> boolean
- setSwaFull(boolean swaFull) -> void
- getCacheReuse() -> int
- setCacheReuse(int cacheReuse) -> void
- getGpuLayers() -> int
- setGpuLayers(int gpuLayers) -> void
- getCpuMoeLayers() -> int
- setCpuMoeLayers(int cpuMoeLayers) -> void
- getSeed() -> int
- setSeed(int seed) -> void
- getCpuFfnLayers() -> int
- setCpuFfnLayers(int cpuFfnLayers) -> void
- getKvUnifiedPerSlot() -> int
- setKvUnifiedPerSlot(int kvUnifiedPerSlot) -> void
- getLazyMode() -> String
- setLazyMode(@Nullable String lazyMode) -> void
- getRepeatLastN() -> int
- setRepeatLastN(int repeatLastN) -> void
- getCacheTypeK() -> String
- setCacheTypeK(@Nullable String cacheTypeK) -> void
- getCacheTypeV() -> String
- setCacheTypeV(@Nullable String cacheTypeV) -> void
- isFlashAttn() -> boolean
- setFlashAttn(boolean flashAttn) -> void
- getBatchSize() -> int
- setBatchSize(int batchSize) -> void
- getUbatchSize() -> int
- setUbatchSize(int ubatchSize) -> void
- getThreadsBatch() -> int
- setThreadsBatch(int threadsBatch) -> void
- getMainGpu() -> int
- setMainGpu(int mainGpu) -> void
- getDevices() -> String
- setDevices(String devices) -> void
- getProvider() -> String
- setProvider(String provider) -> void
- getRemoteModel() -> String
- setRemoteModel(String remoteModel) -> void
- getBaseUrl() -> String
- setBaseUrl(String baseUrl) -> void
- getApiKeyEnv() -> String
- setApiKeyEnv(String apiKeyEnv) -> void
- getReasoningEffort() -> String
- setReasoningEffort(String reasoningEffort) -> void
- getReasoningBudgetTokens() -> int
- setReasoningBudgetTokens(int reasoningBudgetTokens) -> void
- getDryMultiplier() -> float
- setDryMultiplier(float dryMultiplier) -> void
- getDryBase() -> float
- setDryBase(float dryBase) -> void
- getDryAllowedLength() -> int
- setDryAllowedLength(int dryAllowedLength) -> void
- getDryPenaltyLastN() -> int
- setDryPenaltyLastN(int dryPenaltyLastN) -> void
- getDrySequenceBreakers() -> java.util.List<java.lang.String>
- setDrySequenceBreakers(@Nullable java.util.List<java.lang.String> drySequenceBreakers) -> void
- getStopStrings() -> java.util.List<java.lang.String>
- setStopStrings(@Nullable java.util.List<java.lang.String> stopStrings) -> void
- getCalibration() -> @Nullable AiCalibration
- setCalibration(@Nullable AiCalibration calibration) -> void

Count of public/protected members: 58

#### Dependencies
import java.util.ArrayList; import java.util.Collections; import java.util.List; import lombok.ToString; import org.jspecify.annotations.Nullable; referenced types: net.ladenthin.srcmorph.config.AiCalibration

#### Exceptions / Errors
No explicit throws declarations on any method. Nullable types used: @Nullable on modelPath parameter/return, chatTemplateEnableThinking getter/return, AiCalibration getter/return. Setters accept @Nullable parameters for modelPath, chatTemplateEnableThinking, cacheTypeK, cacheTypeV, devices, provider, remoteModel, baseUrl, apiKeyEnv, drySequenceBreakers, stopStrings, calibration. Setters null-safe: setLazyMode replaces null with DEFAULT_LAZY_MODE; setCacheTypeK replaces null with DEFAULT_CACHE_TYPE_K; setCacheTypeV replaces null with DEFAULT_CACHE_TYPE_V; setDevices resets to DEFAULT_DEVICES on null; setProvider resets to DEFAULT_PROVIDER on null; setRemoteModel resets to DEFAULT_REMOTE_MODEL on null; setBaseUrl resets to DEFAULT_BASE_URL on null; setApiKeyEnv resets to DEFAULT_API_KEY_ENV on null.

#### Concurrency
No threading or synchronization mechanisms. Instances are mutable JavaBeans populated through setters by the Maven plugin framework and managed by identity — not thread-safe by design, intended for single-threaded configuration setup.

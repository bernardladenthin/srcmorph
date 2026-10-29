### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:34:06Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates AI generation parameters and defaults for model inference, carrying all step configuration between the Maven plugin layer and AI provider implementations.

#### Purpose
- Mutable configuration object carrying all parameters for a single AI generation step
- Bridges Maven configuration layer and AI provider implementations
- Holds model path, sampling parameters, retry policy, input trimming limits, stop strings

#### Type
- Class: `AiGenerationConfig`
- Annotations: `@ToString` (Lombok), `@SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")`
- No explicit extends / implements; all fields are mutable JavaBeans populated via setters
- Key static defaults: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`

#### Input
- Constructor: `AiGenerationConfig()` with all defaults applied
- Setter methods for every field: `setModelPath`, `setContextSize`, `setMaxOutputTokens`, `setTemperature`, `setThreads`, `setCharsPerToken`, `setMaxInputChars`, `setWarnOnTrim`, `setTopP`, `setTopK`, `setRepeatPenalty`, `setMinP`, `setTopNSigma`, `setChatTemplateEnableThinking`, `setCachePrompt`, `setSwaFull`, `setCacheReuse`, `setGpuLayers`, `setCpuMoeLayers`, `setSeed`, `setCpuFfnLayers`, `setKvUnifiedPerSlot`, `setLazyMode`, `setRepeatLastN`, `setCacheTypeK`, `setCacheTypeV`, `setFlashAttn`, `setBatchSize`, `setUbatchSize`, `setThreadsBatch`, `setMainGpu`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`, `setReasoningEffort`, `setReasoningBudgetTokens`, `setDryMultiplier`, `setDryBase`, `setDryAllowedLength`, `setDryPenaltyLastN`
- Fields: `modelPath` (@Nullable String), `contextSize` (int), `maxOutputTokens` (int), `temperature` (float), `threads` (int), `charsPerToken` (int), `maxInputChars` (int), `warnOnTrim` (boolean), `topP` (float), `topK` (int), `minP` (float), `topNSigma` (float), `repeatPenalty` (float), `chatTemplateEnableThinking` (@Nullable Boolean), `cachePrompt` (boolean), `swaFull` (boolean), `cacheReuse` (int), `gpuLayers` (int), `seed` (int), `cpuMoeLayers` (int), `cpuFfnLayers` (int), `kvUnifiedPerSlot` (int), `lazyMode` (String), `repeatLastN` (int), `cacheTypeK` (String), `cacheTypeV` (String), `flashAttn` (boolean), `batchSize` (int), `ubatchSize` (int), `threadsBatch` (int), `mainGpu` (int), `devices` (String), `provider` (String), `remoteModel` (String), `baseUrl` (String), `apiKeyEnv` (String), `reasoningEffort` (String), `reasoningBudgetTokens` (int), `dryMultiplier` (float), `dryBase` (float), `dryAllowedLength` (int), `dryPenaltyLastN` (int), `drySequenceBreakers` (List<String>), `stopStrings` (List<String>), `calibration` (@Nullable AiCalibration)

#### Output
- Getter methods returning all field values
- `getDrySequenceBreakers()` returns `Collections.unmodifiableList(drySequenceBreakers)`
- `getStopStrings()` returns `Collections.unmodifiableList(stopStrings)`
- No explicit return values beyond configuration state; side effects via provider parameter construction

#### Core logic
- Holds default values for all AI generation sampling parameters used by llama.cpp and provider implementations
- Provides fluent setter/getter pairs for every configurable parameter with documented defaults
- Static final fields define sensible defaults: context size (32768 tokens), max output tokens (128), temperature (0.15f), threads (min 4 or available processors), top-p (0.9f), top-k (40), min-p (0.0f), repeat penalty (1.0f), cache prompt (true), SWA full (true), cache reuse (256 tokens), GPU layers (-1 = default), seed (-1 = random per request), CPU MoE/FFN layers (-1 = default), KV unified per slot (-1 = default), lazy mode (""), repeat last n (-1 = default), cache types ("" = default), Flash Attention (false), batch size (-1 = default), physical batch size (-1 = default), threads batch (-1 = default), main GPU (-1 = default), devices (""), provider (""), remote model (""), base URL ("https://openrouter.ai/api/v1"), API key env (""), dry multiplier (0.0 = disabled), dry base (1.75f), dry allowed length (2), dry penalty last n (-1 = not sent)
- `setLazyMode` normalizes null to DEFAULT_LAZY_MODE ("")
- `setCacheTypeK` / `setCacheTypeV` normalize null to their respective defaults
- `setProvider` normalizes null to DEFAULT_PROVIDER ("")
- `setBaseUrl` normalizes null to DEFAULT_BASE_URL
- `setApiKeyEnv` normalizes null to DEFAULT_API_KEY_ENV
- `setDryPenaltyLastN` accepts -1 (llama.cpp window), 0 (disabled), or positive values
- `setTopNSigma` accepts -1.0 (disabled) or positive values
- `setMinP` accepts 0.0 (disabled) or positive values
- `setRepeatLastN` accepts -1 (llama.cpp window), 0 (disabled), or positive values
- `setCharsPerToken` accepts 0 to disable automatic maxInputChars calculation
- `setSeed` accepts -1 for upstream random-per-request default
- `setCalibration` accepts @Nullable AiCalibration or null
- `setDrySequenceBreakers` / `setStopStrings` accept @Nullable List<String>, null resets to empty

#### Public API
- `getModelPath() -> String` - Returns model file path, or null if not set
- `setModelPath(String modelPath) -> void` - Sets model file path
- `getContextSize() -> int` - Returns context window size in tokens
- `setContextSize(int contextSize) -> void` - Sets context window size in tokens
- `getMaxOutputTokens() -> int` - Returns maximum output tokens per inference call
- `setMaxOutputTokens(int maxOutputTokens) -> void` - Sets maximum output tokens per inference call
- `getTemperature() -> float` - Returns sampling temperature
- `setTemperature(float temperature) -> void` - Sets sampling temperature; lower is more deterministic
- `getThreads() -> int` - Returns number of CPU threads used for inference
- `setThreads(int threads) -> void` - Sets number of CPU threads used for inference
- `getCharsPerToken() -> int` - Returns chars-per-token ratio; 0 disables automatic calculation
- `setCharsPerToken(int charsPerToken) -> void` - Sets chars-per-token; use 0 to disable automatic calculation and fall back to getMaxInputChars()
- `getMaxInputChars() -> int` - Returns maximum input characters fed to the prompt
- `setMaxInputChars(int maxInputChars) -> void` - Sets maximum input characters fed to the prompt
- `isWarnOnTrim() -> boolean` - Returns true if trim warnings are enabled
- `setWarnOnTrim(boolean warnOnTrim) -> void` - Sets whether a warning is emitted when prompt source text is trimmed
- `getTopP() -> float` - Returns top-p value
- `setTopP(float topP) -> void` - Sets nucleus-sampling probability threshold
- `getTopK() -> int` - Returns top-k sampling limit
- `setTopK(int topK) -> void` - Sets top-k sampling limit
- `getRepeatPenalty() -> float` - Returns repeat penalty
- `setRepeatPenalty(float repeatPenalty) -> void` - Sets repeat penalty
- `getMinP() -> float` - Returns min-p threshold; 0.0 = disabled
- `setMinP(float minP) -> void` - Sets min-p threshold; 0.0 = disabled
- `getTopNSigma() -> float` - Returns top-n-sigma threshold; -1.0 = disabled
- `setTopNSigma(float topNSigma) -> void` - Sets top-n-sigma threshold; -1.0 = disabled
- `getChatTemplateEnableThinking() -> @Nullable Boolean` - Returns true/false when set, null when unset; null omits enable_thinking kwarg
- `setChatTemplateEnableThinking(@Nullable Boolean chatTemplateEnableThinking) -> void` - Sets thinking mode; null omits kwarg
- `isCachePrompt() -> boolean` - Returns true when prompt-prefix KV reuse across files is enabled
- `isSwaFull() -> boolean` - Returns true when full SWA KV is kept
- `setSwaFull(boolean swaFull) -> void` - Sets full SWA KV; true enables cross-request prefix reuse, more RAM
- `getCacheReuse() -> int` - Returns cache-reuse chunk size; 0 = disabled
- `setCacheReuse(int cacheReuse) -> void` - Sets cache-reuse chunk size; 0 = disabled
- `getGpuLayers() -> int` - Returns GPU layers; -1 = leave binding/build default
- `setGpuLayers(int gpuLayers) -> void` - Sets GPU layers; -1 = leave default, 0 = force CPU, >0 = offload
- `getCpuMoeLayers() -> int` - Returns CPU MoE layers; -1 = leave default
- `setCpuMoeLayers(int cpuMoeLayers) -> void` - Sets CPU MoE layers; -1 = leave default, >= 0 = keep that many
- `getSeed() -> int` - Returns the seed; defaults to -1 (random per request)
- `setSeed(int seed) -> void` - Sets the seed; -1 = leave upstream's random-per-request default
- `getCpuFfnLayers() -> int` - Returns CPU FFN layers; -1 = leave default
- `setCpuFfnLayers(int cpuFfnLayers) -> void` - Sets CPU FFN layers; -1 = leave default, >= 0 = keep that many
- `getKvUnifiedPerSlot() -> int` - Returns per-slot unified KV context cap; -1 = leave default
- `setKvUnifiedPerSlot(int kvUnifiedPerSlot) -> void` - Sets per-slot unified KV context cap; -1 = leave default, must be positive when set
- `getLazyMode() -> String` - Returns off/auto/on or empty to leave default
- `setLazyMode(@Nullable String lazyMode) -> void` - Sets tensor-read laziness; null resets to empty default
- `getRepeatLastN() -> int` - Returns repeat-penalty window; 0 = disabled, -1 = leave llama.cpp window
- `setRepeatLastN(int repeatLastN) -> void` - Sets repeat-penalty window; -1 = leave default, 0 = disable penalty
- `getCacheTypeK() -> String` - Returns K cache type (q8_0, f16, etc.) or empty to leave default
- `setCacheTypeK(@Nullable String cacheTypeK) -> void` - Sets K cache type; null resets to empty default
- `getCacheTypeV() -> String` - Returns V cache type or empty to leave default
- `setCacheTypeV(@Nullable String cacheTypeV) -> void` - Sets V cache type; null resets to empty default; usually requires setFlashAttn
- `isFlashAttn() -> boolean` - Returns true when Flash Attention is enabled
- `setFlashAttn(boolean flashAttn) -> void` - Sets Flash Attention; true enables lower KV memory, precondition for quantized V cache
- `getBatchSize() -> int` - Returns logical batch size; -1 = leave default
- `setBatchSize(int batchSize) -> void` - Sets logical batch size; -1 = leave default, must be positive when set
- `getUbatchSize() -> int` - Returns physical batch size; -1 = leave default
- `setUbatchSize(int ubatchSize) -> void` - Sets physical batch size; -1 = leave default, must be positive when set
- `getThreadsBatch() -> int` - Returns batch/prompt-processing thread count; -1 = reuse threads
- `setThreadsBatch(int threadsBatch) -> void` - Sets batch thread count; -1 = reuse threads, must be positive when set
- `getMainGpu() -> int` - Returns primary GPU index; -1 = leave default
- `setMainGpu(int mainGpu) -> void` - Sets primary GPU index; -1 = leave default, non-negative selects device
- `getDevices() -> String` - Returns comma-separated device list or empty to leave default
- `setDevices(String devices) -> void` - Sets device list; null resets to empty default
- `getProvider() -> String` - Returns AI provider (llamacpp-jni, openai-compatible, mock); empty uses run's generationProvider
- `setProvider(String provider) -> void` - Sets AI provider; null resets to default
- `getRemoteModel() -> String` - Returns model id for openai-compatible provider; empty sends none
- `setRemoteModel(String remoteModel) -> void` - Sets model id; null resets to default
- `getBaseUrl() -> String` - Returns base URL of OpenAI-compatible API
- `setBaseUrl(String baseUrl) -> void` - Sets base URL; null resets to default
- `getApiKeyEnv() -> String` - Returns env var name holding API key; empty sends none
- `setApiKeyEnv(String apiKeyEnv) -> void` - Sets env var name; null resets to default
- `getReasoningEffort() -> String` - Returns reasoning effort (low/medium/high) or empty to omit
- `setReasoningEffort(String reasoningEffort) -> void` - Sets reasoning effort; empty/blank omits kwarg
- `getReasoningBudgetTokens() -> int` - Returns reasoning budget tokens; -1 = unrestricted
- `setReasoningBudgetTokens(int reasoningBudgetTokens) -> void` - Sets budget; -1 = unrestricted, 0 = no thinking
- `getDryMultiplier() -> float` - Returns DRY multiplier; 0.0 = disabled
- `setDryMultiplier(float dryMultiplier) -> void` - Sets DRY multiplier; 0.0 = disabled
- `getDryBase() -> float` - Returns DRY base
- `setDryBase(float dryBase) -> void` - Sets DRY base
- `getDryAllowedLength() -> int` - Returns DRY allowed length
- `setDryAllowedLength(int dryAllowedLength) -> void` - Sets DRY allowed length
- `getDryPenaltyLastN() -> int` - Returns DRY penalty look-back window; -1 = not sent, llama.cpp window; 0 = disabled
- `setDryPenaltyLastN(int dryPenaltyLastN) -> void` - Sets DRY penalty last-n; -1 = not sent, 0 = disabled
- `getDrySequenceBreakers() -> List<String>` - Returns unmodifiable list of DRY sequence breakers; empty = use model/binding default
- `setDrySequenceBreakers(@Nullable List<String> drySequenceBreakers) -> void` - Sets DRY sequence breakers; null resets to empty
- `getStopStrings() -> List<String>` - Returns unmodifiable list of stop strings; empty = no stop strings configured
- `setStopStrings(@Nullable List<String> stopStrings) -> void` - Sets stop strings; null clears to empty
- `getCalibration() -> @Nullable AiCalibration` - Returns per-machine timing calibration, or null when not calibrated
- `setCalibration(@Nullable AiCalibration calibration) -> void` - Sets per-machine timing calibration

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration` (referenced type for calibration field)

#### Exceptions / Errors
- No explicit throws declarations in getters/setters
- `setLazyMode` guards against null by resetting to DEFAULT_LAZY_MODE ("")
- `setCacheTypeK` / `setCacheTypeV` guard against null by resetting to respective defaults
- `setProvider` guards against null by resetting to DEFAULT_PROVIDER ("")
- `setBaseUrl` guards against null by resetting to DEFAULT_BASE_URL
- `setApiKeyEnv` guards against null by resetting to DEFAULT_API_KEY_ENV
- `setDryPenaltyLastN` documented: -

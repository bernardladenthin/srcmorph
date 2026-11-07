### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:33Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures model routing, prompt limits, sampling, reasoning, hardware usage, caching, and stop behavior for AI-generated source summaries.

#### Purpose
- Carries one generation step’s settings from Maven configuration to AI providers.
- Supplies defaults and mutable JavaBean accessors; instances are managed by identity.

#### Type
- `public class AiGenerationConfig` — class; Lombok `@ToString`; nullable fields use JSpecify `@Nullable`.

#### Input
- No-argument constructor; callers set model path, generation parameters, provider/API settings, lists, and optional `AiCalibration`.
- List setters accept nullable `List<String>`; several string setters normalize `null` to defaults.

#### Output
- Returns configured values through getters; stores changes in mutable instance fields.
- DRY sequence-breaker and stop-string getters return unmodifiable views.
- Lombok generates `toString()` over instance fields.

#### Core logic
- Initializes model, prompt, sampling, reasoning, caching, hardware, provider, and repetition settings from public defaults.
- `charsPerToken` and `maxInputChars` provide inputs for automatic prompt-size calculation; this class does not perform that calculation.
- Nullable chat-template thinking distinguishes unset from explicit `true` or `false`.
- Uses sentinel defaults such as `-1` and empty strings to leave provider or llama.cpp defaults in effect.
- Resets nullable lazy mode, cache types, device/provider/model/URL/API-key-env strings, and lists to defaults or empty values.
- Keeps API-key values out of configuration: stores only the environment-variable name.

#### Public API
- `AiGenerationConfig()` — construct with defaults.
- `getModelPath()`/`setModelPath(String)` -> GGUF model path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference CPU threads.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> input sizing ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> input character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warnings.
- `getTopP()`/`setTopP(float)` -> nucleus sampling.
- `getTopK()`/`setTopK(int)` -> top-k sampling.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p sampling.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma sampling.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> tri-state thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt-prefix caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse threshold.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layers.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layers.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repeat-penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model AI provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment-variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition penalty.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> optional machine timing calibration.
- Public defaults: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.

#### Dependencies
- `AiCalibration`; `ArrayList`, `Collections`, `List`; Lombok `ToString`; JSpecify `Nullable`.
- `Runtime.getRuntime().availableProcessors()` determines the default thread count, capped at four.

#### Exceptions / Errors
- Setters generally do not validate parameter ranges; provider/binding validation may reject invalid values.
- Nullable model path, calibration, and thinking mode are preserved; selected nullable setters reset to defaults.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

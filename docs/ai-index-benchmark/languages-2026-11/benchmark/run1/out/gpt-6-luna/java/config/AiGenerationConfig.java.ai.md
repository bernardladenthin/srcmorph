### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:13:59Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI model selection, prompt limits, sampling, inference hardware, provider routing, and generation controls for a single generation step.

#### Purpose
- `AiGenerationConfig` carries mutable per-model settings from Maven configuration to AI providers.
- Supplies defaults for inference, prompt trimming, provider routing, and optional machine calibration.

#### Type
- `AiGenerationConfig` — public class; mutable JavaBean.
- `@ToString`; suppresses initialization/nullness warnings; no generated `equals` or `hashCode`.

#### Input
- No-argument constructor; fields begin with declared defaults.
- Setters accept model path, context/output limits, sampling and repetition parameters, prompt/cache controls, hardware and batching settings, provider/API configuration, reasoning options, DRY controls, stop strings, and calibration.
- Nullable inputs include model path, chat-template thinking mode, calibration, lists, and select strings; some string setters normalize `null` to defaults.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate instance fields; no files, network requests, or inference are performed here.
- Lombok-generated `toString()` exposes instance fields for diagnostics.

#### Core logic
- Initializes context, token, sampling, thread, trimming, caching, hardware, provider, and generation defaults through constants.
- Uses `charsPerToken` and `maxInputChars` to provide inputs for automatic prompt-size limiting; calculation is not performed in this class.
- Uses nullable `chatTemplateEnableThinking` to distinguish unset from explicit `true` or `false`.
- Represents provider and native-runtime “leave default” choices with empty strings or sentinel values such as `-1`.
- Normalizes `null` lists to empty lists and selected nullable strings to their defaults; list getters prevent mutation through the returned view.
- Keeps optional `AiCalibration` for per-machine timing data.

#### Public API
- `AiGenerationConfig()` — initialize defaults.
- `getModelPath()`/`setModelPath(String)` -> model file path.
- `getContextSize()`/`setContextSize(int)` -> context token limit.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference thread count.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> automatic trim estimate.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p sampling threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> thinking-mode override.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt caching setting.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA cache setting.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse threshold.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layer count.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layer count.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition-penalty window.
- `getCacheTypeK()`/`setCacheTypeK(String)` -> K cache data type.
- `getCacheTypeV()`/`setCacheTypeV(String)` -> V cache data type.
- `isFlashAttn()`/`setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()`/`setBatchSize(int)` -> logical batch size.
- `getUbatchSize()`/`setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()`/`setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition penalty.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY reset tokens.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> machine timing calibration.
- Public defaults: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Same package: `AiCalibration`.

#### Exceptions / Errors
- No explicit exception handling or validation.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` replace `null` with defaults.
- List setters replace `null` with empty lists; other numeric values are stored without validation.

#### Concurrency
- Mutable and unsynchronized; no thread-safety guarantees.
- Unmodifiable list views prevent caller mutation through getters but reflect changes to the underlying configured lists.

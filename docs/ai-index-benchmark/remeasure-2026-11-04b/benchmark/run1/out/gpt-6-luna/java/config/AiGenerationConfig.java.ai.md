### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:05Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Carries per-model AI generation settings, including prompt limits, sampling, runtime tuning, provider routing, and stopping rules.

#### Purpose
- `AiGenerationConfig` supplies mutable defaults and Maven-settable parameters to AI generation providers.
- Holds model/runtime options, remote-provider configuration, token budgets, and optional machine calibration.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString` and `@SuppressWarnings`; no explicit supertypes.
- `@ToString` generates a diagnostic string; `equals` and `hashCode` are not generated.

#### Input
- No-argument constructor; configuration values are supplied through setters.
- Settings cover model path, context and input limits, sampling, CPU/GPU execution, KV cache, provider routing, reasoning, DRY repetition controls, and stop strings.
- `setCalibration` accepts optional `AiCalibration`; nullable inputs are also accepted for model path, thinking mode, and selected string/list settings.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate instance fields. Nullable list and selected string setters reset to defaults or empty lists.
- Public `DEFAULT_*` constants provide configuration defaults; `DEFAULT_THREADS` is capped at four or the available processor count.

#### Core logic
- Initializes fields from defaults for context size, output tokens, temperature, threads, prompt trimming, sampling, cache/runtime options, provider routing, and reasoning; initializes `drySequenceBreakers` and `stopStrings` as empty `ArrayList`s.
- `setLazyMode`, `setCacheTypeK`, and `setCacheTypeV` replace `null` with their corresponding empty-string defaults.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` replace `null` with their corresponding defaults.
- `setDrySequenceBreakers` and `setStopStrings` replace `null` with new empty lists; `getDrySequenceBreakers` and `getStopStrings` wrap the current lists with `Collections.unmodifiableList`.
- `getModelPath`, `getChatTemplateEnableThinking`, `getCalibration`, and their setters preserve nullable values; `null` thinking mode means unset.
- Other setters directly assign values without validation.

#### Public API
- `AiGenerationConfig()` — initialize defaults.
- `getModelPath()` -> `@Nullable String` / `setModelPath(@Nullable String)` -> model file path.
- `getContextSize()` -> `int` / `setContextSize(int)` -> context window.
- `getMaxOutputTokens()` -> `int` / `setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()` -> `float` / `setTemperature(float)` -> sampling temperature.
- `getThreads()` -> `int` / `setThreads(int)` -> inference thread count.
- `getCharsPerToken()` -> `int` / `setCharsPerToken(int)` -> input sizing ratio.
- `getMaxInputChars()` -> `int` / `setMaxInputChars(int)` -> prompt input limit.
- `isWarnOnTrim()` -> `boolean` / `setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()` -> `float` / `setTopP(float)` -> nucleus sampling threshold.
- `getTopK()` -> `int` / `setTopK(int)` -> top-k limit.
- `getRepeatPenalty()` -> `float` / `setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()` -> `float` / `setMinP(float)` -> min-p threshold.
- `getTopNSigma()` -> `float` / `setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()` -> `@Nullable Boolean` / `setChatTemplateEnableThinking(@Nullable Boolean)` -> thinking mode.
- `isCachePrompt()` -> `boolean` / `setCachePrompt(boolean)` -> prompt cache setting.
- `isSwaFull()` -> `boolean` / `setSwaFull(boolean)` -> full SWA cache setting.
- `getCacheReuse()` -> `int` / `setCacheReuse(int)` -> KV prefix reuse chunk.
- `getGpuLayers()` -> `int` / `setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()` -> `int` / `setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()` -> `int` / `setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()` -> `int` / `setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()` -> `int` / `setKvUnifiedPerSlot(int)` -> per-slot KV cap.
- `getLazyMode()` -> `String` / `setLazyMode(@Nullable String)` -> tensor-read laziness.
- `getRepeatLastN()` -> `int` / `setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()` -> `String` / `setCacheTypeK(@Nullable String)` -> K-cache data type.
- `getCacheTypeV()` -> `String` / `setCacheTypeV(@Nullable String)` -> V-cache data type.
- `isFlashAttn()` -> `boolean` / `setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()` -> `int` / `setBatchSize(int)` -> logical batch size.
- `getUbatchSize()` -> `int` / `setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()` -> `int` / `setThreadsBatch(int)` -> batch-processing threads.
- `getMainGpu()` -> `int` / `setMainGpu(int)` -> primary GPU index.
- `getDevices()` -> `String` / `setDevices(String)` -> selected backend devices.
- `getProvider()` -> `String` / `setProvider(String)` -> per-model provider.
- `getRemoteModel()` -> `String` / `setRemoteModel(String)` -> remote model ID.
- `getBaseUrl()` -> `String` / `setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()` -> `String` / `setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()` -> `String` / `setReasoningEffort(String)` -> reasoning-effort level.
- `getReasoningBudgetTokens()` -> `int` / `setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()` -> `float` / `setDryMultiplier(float)` -> DRY penalty multiplier.
- `getDryBase()` -> `float` / `setDryBase(float)` -> DRY penalty base.
- `getDryAllowedLength()` -> `int` / `setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` -> `int` / `setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()` -> `List<String>` / `setDrySequenceBreakers(@Nullable List<String>)` -> DRY sequence breakers.
- `getStopStrings()` -> `List<String>` / `setStopStrings(@Nullable List<String>)` -> generation stop strings.
- `getCalibration()` -> `@Nullable AiCalibration` / `setCalibration(@Nullable AiCalibration)` -> per-machine timing calibration.
- Public constants: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `java.lang.Runtime`

#### Exceptions / Errors
- No explicit exceptions or validation checks.
- Nullable setters preserve `null` for model path, thinking mode, and calibration; selected other setters normalize `null` to defaults or empty lists.

#### Concurrency
- Mutable, unsynchronized JavaBean; list getters are unmodifiable views, not defensive snapshots.

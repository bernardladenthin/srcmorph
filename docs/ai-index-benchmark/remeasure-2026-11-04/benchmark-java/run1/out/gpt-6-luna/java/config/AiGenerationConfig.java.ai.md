### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:19Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures model routing, prompt limits, sampling, reasoning, hardware, caching, and stopping behavior for AI-generated project summaries.

#### Purpose
- Carries one AI generation step’s settings between Maven configuration and AI providers.
- Supplies defaults and mutable JavaBean accessors for local and OpenAI-compatible generation.

#### Type
- `public class AiGenerationConfig` — class; Lombok `@ToString`; mutable identity-managed bean.
- Nullable `modelPath`, thinking mode, and calibration; no `equals`/`hashCode` generation.

#### Input
- Constructor takes no arguments; all settings are configured through setters.
- Settings include model path, context/output limits, sampling, trimming, provider/API endpoint, reasoning, cache, hardware, DRY controls, stop strings, and optional `AiCalibration`.

#### Output
- Exposes configuration values through getters; fields are mutated by setters.
- Returns unmodifiable views of DRY sequence breakers and stop strings.
- Lombok-generated `toString()` renders instance fields for diagnostics.

#### Core logic
- Initializes instance settings from public defaults, including available-processor-capped inference threads and sampling defaults.
- Uses nullable chat-template thinking mode to distinguish unset from explicitly enabled or disabled.
- Uses sentinel defaults (such as `-1` and empty strings) to represent provider/binding defaults or omitted settings.
- Resets nullable list and string inputs to empty/default values where implemented; list getters wrap current lists as unmodifiable views.
- Stores provider routing, remote model, base URL, and API-key environment-variable name; does not store the API key itself.
- Holds optional per-machine calibration without applying it.

#### Public API
- `AiGenerationConfig()` -> constructor; initializes defaults.
- Constants: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.
- `getModelPath()`/`setModelPath(String)` -> model file path.
- `getContextSize()`/`setContextSize(int)` -> context window.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference threads.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> input budget estimate ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt input character limit.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()`/`setMinP(float)` -> min-p threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> nullable thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt KV caching.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse chunk.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()`/`setCpuMoeLayers(int)` -> CPU MoE layer count.
- `getSeed()`/`setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()`/`setCpuFfnLayers(int)` -> CPU FFN layer count.
- `getKvUnifiedPerSlot()`/`setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()`/`setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()`/`setRepeatLastN(int)` -> repetition penalty window.
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
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning effort level.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition penalty multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty growth base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> optional timing calibration.

#### Dependencies
- `AiCalibration`
- `ArrayList`, `Collections`, `List`
- Lombok `ToString`
- JSpecify `Nullable`
- `Runtime` for available processor count

#### Exceptions / Errors
- No explicit exceptions thrown or caught.
- Nullable model path, thinking mode, and calibration are supported; selected nullable setter inputs reset to defaults or empty lists.

#### Concurrency
- Mutable, unsynchronized configuration bean; no thread-safety guarantees.

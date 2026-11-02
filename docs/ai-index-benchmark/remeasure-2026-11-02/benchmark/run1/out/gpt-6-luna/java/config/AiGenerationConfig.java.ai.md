### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:35Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures model routing, prompt limits, sampling, reasoning, repetition controls, and llama.cpp runtime options for AI-generated source summaries.

#### Purpose
- Carries per-model generation settings from Maven configuration to AI providers.
- Supplies defaults for inference, input trimming, provider routing, hardware, caching, and calibration.

#### Type
- `public class AiGenerationConfig` — mutable JavaBean; annotated `@ToString`; no `equals`/`hashCode` generation.
- Uses nullable `modelPath`, nullable tri-state `chatTemplateEnableThinking`, and nullable `AiCalibration`.

#### Input
- No-argument constructor; fields initialized from `DEFAULT_*` constants.
- Setters accept model path, generation and runtime parameters, provider/API settings, stop strings, DRY sequence breakers, and optional calibration.

#### Output
- Getters expose configuration values; list getters return unmodifiable views.
- Setters mutate instance fields. Null list inputs reset to empty; several nullable strings reset to their defaults.
- Lombok generates `toString()` over instance fields.

#### Core logic
- Defines defaults for context, output and input limits, sampling, threads, reasoning, caching, hardware, providers, and repetition controls.
- Stores per-run inference settings, including token limits, temperature, top-p/top-k/min-p/top-n-sigma, and repeat penalty.
- Represents chat-template thinking as tri-state: `null` leaves the model template default in effect.
- Supports local llama.cpp and OpenAI-compatible provider routing, remote model ID, base URL, and API-key environment-variable name.
- Captures llama.cpp KV/cache, GPU/device, batch/thread, and tensor-read options.
- Provides `charsPerToken` and `maxInputChars` settings for input trimming; this class does not calculate the limit.
- Exposes stop strings and DRY sequence breakers through unmodifiable views; setters replace the backing lists.
- `setLazyMode`, `setCacheTypeK`, and `setCacheTypeV` map `null` to empty defaults; provider and endpoint setters similarly restore their declared defaults.

#### Public API
- `AiGenerationConfig()` -> constructor.
- `getModelPath()`/`setModelPath(String)` -> GGUF model path.
- `getContextSize()`/`setContextSize(int)` -> context token limit.
- `getMaxOutputTokens()`/`setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()`/`setTemperature(float)` -> sampling temperature.
- `getThreads()`/`setThreads(int)` -> inference CPU threads.
- `getCharsPerToken()`/`setCharsPerToken(int)` -> input sizing ratio.
- `getMaxInputChars()`/`setMaxInputChars(int)` -> prompt source character cap.
- `isWarnOnTrim()`/`setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()`/`setTopP(float)` -> nucleus sampling threshold.
- `getTopK()`/`setTopK(int)` -> top-k sampling limit.
- `getRepeatPenalty()`/`setRepeatPenalty(float)` -> repetition penalty strength.
- `getMinP()`/`setMinP(float)` -> min-p sampling threshold.
- `getTopNSigma()`/`setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()`/`setChatTemplateEnableThinking(Boolean)` -> template thinking mode.
- `isCachePrompt()`/`setCachePrompt(boolean)` -> prompt-prefix cache setting.
- `isSwaFull()`/`setSwaFull(boolean)` -> full SWA KV cache setting.
- `getCacheReuse()`/`setCacheReuse(int)` -> KV prefix reuse chunk size.
- `getGpuLayers()`/`setGpuLayers(int)` -> GPU layer offload count.
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
- `getThreadsBatch()`/`setThreadsBatch(int)` -> batch-processing threads.
- `getMainGpu()`/`setMainGpu(int)` -> primary GPU index.
- `getDevices()`/`setDevices(String)` -> backend device selection.
- `getProvider()`/`setProvider(String)` -> per-model AI provider.
- `getRemoteModel()`/`setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()`/`setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()`/`setApiKeyEnv(String)` -> API key environment variable name.
- `getReasoningEffort()`/`setReasoningEffort(String)` -> reasoning-effort kwarg.
- `getReasoningBudgetTokens()`/`setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()`/`setDryMultiplier(float)` -> DRY repetition multiplier.
- `getDryBase()`/`setDryBase(float)` -> DRY penalty base.
- `getDryAllowedLength()`/`setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()`/`setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()`/`setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()`/`setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()`/`setCalibration(AiCalibration)` -> machine timing calibration.
- Public constants: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.

#### Dependencies
- `AiCalibration`; `java.util.ArrayList`, `Collections`, `List`; Lombok `@ToString`; JSpecify `@Nullable`.
- References `Runtime.getRuntime().availableProcessors()` for the default thread count.

#### Exceptions / Errors
- No explicit exceptions or validation. Setters generally store provided values without range checks.
- Nullable list setters replace `null` with an empty list; selected nullable string setters restore defaults.

#### Concurrency
- Mutable, unsynchronized configuration object; no thread-safety guarantees.

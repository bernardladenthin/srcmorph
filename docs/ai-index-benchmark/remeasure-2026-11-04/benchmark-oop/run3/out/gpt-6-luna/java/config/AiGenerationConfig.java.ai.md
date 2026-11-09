### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:41Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Carries per-model AI generation, prompt-trimming, sampling, provider-routing, and llama.cpp runtime settings from configuration into generation workflows.

#### Purpose
- `AiGenerationConfig` stores mutable defaults and per-model overrides for one AI generation step.
- Covers model selection, input limits, sampling/repetition controls, provider credentials configuration, hardware tuning, stop strings, and optional timing calibration.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString`, `@SuppressWarnings`; no supertypes declared.
- `DEFAULT_*` constants define defaults for context/output limits, sampling, trimming, threads, cache, hardware, provider routing, reasoning, and DRY settings.

#### Input
- No-argument constructor; values are populated through setters.
- Configuration values include nullable `modelPath`, nullable tri-state `chatTemplateEnableThinking`, model/runtime parameters, provider URL/model and API-key environment-variable name, reasoning and DRY settings.
- `drySequenceBreakers` and `stopStrings` accept lists; `calibration` accepts nullable `AiCalibration`.
- `DEFAULT_THREADS` reads `Runtime.getRuntime().availableProcessors()`.

#### Output
- Getters expose configured values; list getters return unmodifiable views.
- Setters mutate instance fields. Nullable strings for lazy/cache types and nullable lists reset to defaults or empty lists as applicable.
- Lombok `@ToString` generates diagnostic string output; no `equals`/`hashCode` generation.

#### Core logic
- Initializes fields from constants, including sampling defaults (`temperature`, `topP`, `topK`, `minP`, `topNSigma`, repetition penalty) and input-trimming defaults.
- Uses `charsPerToken` and `maxInputChars` as configuration for automatic input-limit calculation elsewhere; this class does not calculate the limit.
- Represents unset options with sentinels such as `-1`, empty strings, and nullable values; forwarding decisions are delegated to providers.
- Supports provider overrides (`llamacpp-jni`, `openai-compatible`, `mock`), remote model selection, base URL, and API-key environment-variable naming.
- Stores inference runtime controls including prompt/SWA cache reuse, GPU/CPU layer allocation, KV cache types, batch sizes, devices, and thread counts.
- Stores stop strings, DRY sequence breakers, and optional per-machine calibration.

#### Public API
- `AiGenerationConfig()` -> `void` — initialize defaults.
- Constants: `DEFAULT_CONTEXT_SIZE: int`, `DEFAULT_MAX_OUTPUT_TOKENS: int`, `DEFAULT_TEMPERATURE: float`, `DEFAULT_THREADS: int`, `DEFAULT_MAX_INPUT_CHARS: int`, `DEFAULT_CHARS_PER_TOKEN: int`, `DEFAULT_SAFETY_MARGIN_CHARS: int`, `DEFAULT_WARN_ON_TRIM: boolean`, `DEFAULT_TOP_P: float`, `DEFAULT_TOP_K: int`, `DEFAULT_MIN_P: float`, `DEFAULT_TOP_N_SIGMA: float`, `DEFAULT_REPEAT_PENALTY: float`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING: @Nullable Boolean`, `DEFAULT_CACHE_PROMPT: boolean`, `DEFAULT_REASONING_EFFORT: String`, `DEFAULT_REASONING_BUDGET_TOKENS: int`, `DEFAULT_SWA_FULL: boolean`, `DEFAULT_CACHE_REUSE: int`, `DEFAULT_GPU_LAYERS: int`, `DEFAULT_SEED: int`, `DEFAULT_CPU_MOE_LAYERS: int`, `DEFAULT_CPU_FFN_LAYERS: int`, `DEFAULT_KV_UNIFIED_PER_SLOT: int`, `DEFAULT_LAZY_MODE: String`, `DEFAULT_REPEAT_LAST_N: int`, `DEFAULT_CACHE_TYPE_K: String`, `DEFAULT_CACHE_TYPE_V: String`, `DEFAULT_FLASH_ATTN: boolean`, `DEFAULT_BATCH_SIZE: int`, `DEFAULT_UBATCH_SIZE: int`, `DEFAULT_THREADS_BATCH: int`, `DEFAULT_MAIN_GPU: int`, `DEFAULT_DEVICES: String`, `DEFAULT_PROVIDER: String`, `DEFAULT_REMOTE_MODEL: String`, `DEFAULT_BASE_URL: String`, `DEFAULT_API_KEY_ENV: String`, `DEFAULT_DRY_MULTIPLIER: float`, `DEFAULT_DRY_BASE: float`, `DEFAULT_DRY_ALLOWED_LENGTH: int`, `DEFAULT_DRY_PENALTY_LAST_N: int`.
- `getModelPath()` -> `@Nullable String` / `setModelPath(@Nullable String)` -> model path.
- `getContextSize()` -> `int` / `setContextSize(int)` -> context window.
- `getMaxOutputTokens()` -> `int` / `setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()` -> `float` / `setTemperature(float)` -> sampling temperature.
- `getThreads()` -> `int` / `setThreads(int)` -> inference thread count.
- `getCharsPerToken()` -> `int` / `setCharsPerToken(int)` -> input sizing ratio.
- `getMaxInputChars()` -> `int` / `setMaxInputChars(int)` -> input character limit.
- `isWarnOnTrim()` -> `boolean` / `setWarnOnTrim(boolean)` -> trim warning toggle.
- `getTopP()` -> `float` / `setTopP(float)` -> nucleus sampling.
- `getTopK()` -> `int` / `setTopK(int)` -> top-k sampling.
- `getRepeatPenalty()` -> `float` / `setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()` -> `float` / `setMinP(float)` -> min-p sampling.
- `getTopNSigma()` -> `float` / `setTopNSigma(float)` -> top-n-sigma sampling.
- `getChatTemplateEnableThinking()` -> `@Nullable Boolean` / `setChatTemplateEnableThinking(@Nullable Boolean)` -> chat-template thinking mode.
- `isCachePrompt()` -> `boolean` / `setCachePrompt(boolean)` -> prompt caching.
- `isSwaFull()` -> `boolean` / `setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()` -> `int` / `setCacheReuse(int)` -> KV prefix reuse threshold.
- `getGpuLayers()` -> `int` / `setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()` -> `int` / `setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()` -> `int` / `setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()` -> `int` / `setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()` -> `int` / `setKvUnifiedPerSlot(int)` -> per-slot KV cap.
- `getLazyMode()` -> `String` / `setLazyMode(@Nullable String)` -> tensor read laziness.
- `getRepeatLastN()` -> `int` / `setRepeatLastN(int)` -> repetition window.
- `getCacheTypeK()` -> `String` / `setCacheTypeK(@Nullable String)` -> K-cache data type.
- `getCacheTypeV()` -> `String` / `setCacheTypeV(@Nullable String)` -> V-cache data type.
- `isFlashAttn()` -> `boolean` / `setFlashAttn(boolean)` -> Flash Attention toggle.
- `getBatchSize()` -> `int` / `setBatchSize(int)` -> logical batch size.
- `getUbatchSize()` -> `int` / `setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()` -> `int` / `setThreadsBatch(int)` -> batch thread count.
- `getMainGpu()` -> `int` / `setMainGpu(int)` -> primary GPU index.
- `getDevices()` -> `String` / `setDevices(String)` -> backend device selection.
- `getProvider()` -> `String` / `setProvider(String)` -> model provider.
- `getRemoteModel()` -> `String` / `setRemoteModel(String)` -> remote model id.
- `getBaseUrl()` -> `String` / `setBaseUrl(String)` -> API base URL.
- `getApiKeyEnv()` -> `String` / `setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()` -> `String` / `setReasoningEffort(String)` -> reasoning-effort setting.
- `getReasoningBudgetTokens()` -> `int` / `setReasoningBudgetTokens(int)` -> reasoning token cap.
- `getDryMultiplier()` -> `float` / `setDryMultiplier(float)` -> DRY repetition penalty.
- `getDryBase()` -> `float` / `setDryBase(float)` -> DRY growth base.
- `getDryAllowedLength()` -> `int` / `setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` -> `int` / `setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()` -> `List<String>` / `setDrySequenceBreakers(@Nullable List<String>)` -> DRY reset tokens.
- `getStopStrings()` -> `List<String>` / `setStopStrings(@Nullable List<String>)` -> generation stop strings.
- `getCalibration()` -> `@Nullable AiCalibration` / `setCalibration(@Nullable AiCalibration)` -> machine timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCalibration`
- `net.ladenthin.llama.parameters.ModelParameters` (documentation reference)

#### Exceptions / Errors
- No explicit exceptions or validation checks.
- Nullable model path, chat-template thinking mode, and calibration are preserved; nullable lazy/cache-type/list inputs reset to defaults or empty lists.
- `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` map `null` to their defaults.

#### Concurrency
- Mutable JavaBean with no synchronization; not thread-safe by implementation.

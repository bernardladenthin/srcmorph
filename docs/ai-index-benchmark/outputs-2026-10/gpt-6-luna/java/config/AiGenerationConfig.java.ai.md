### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:19:48Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model routing, prompt limits, sampling, hardware execution, caching, and stopping behavior for AI-generated source summaries.

#### Purpose
- Carries one generation step’s settings between Maven configuration and AI providers.
- Supplies defaults for inference, prompt trimming, provider routing, and hardware tuning.

#### Type
- Mutable `public class AiGenerationConfig`; no superclass or implemented interfaces declared.
- `@ToString`; Lombok generates a field-based `toString`, but not `equals` or `hashCode`.
- Nullable fields use JSpecify `@Nullable`; one nullable `AiCalibration` stores optional per-machine timing data.

#### Input
- No-argument constructor; initializes fields with defaults.
- Setters accept model path, context and input limits, sampling values, provider and endpoint settings, hardware/cache options, reasoning controls, DRY settings, stop strings, sequence breakers, and optional calibration.
- List setters accept nullable lists; selected string setters map `null` to their defaults.
- Thread default is `min(4, availableProcessors())`.

#### Output
- Getters expose configured values; list getters return unmodifiable views.
- State is stored in mutable instance fields; no files, streams, or external services are accessed.
- Lombok-generated `toString` supports diagnostic logging.

#### Core logic
- Defines defaults for context size, output length, temperature, thread count, input trimming, sampling, and repetition controls.
- Provides llama.cpp options for prompt/SWA KV caching, GPU and CPU layer placement, cache types, attention, batching, devices, and lazy tensor reads.
- Supports per-model provider selection and OpenAI-compatible model, URL, and API-key environment-variable configuration.
- Represents chat-template thinking as tri-state (`null`, `true`, `false`); `null` means omit the kwarg.
- Supports reasoning effort/budget, DRY repetition controls, stop strings, and per-machine calibration.
- Does not validate numeric ranges or normalize non-null values; provider code is responsible for interpreting sentinels such as `-1`.

#### Public API
- Constructor: `AiGenerationConfig()`; creates default configuration.
- Public constants: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.
- `getModelPath() -> @Nullable String` / `setModelPath(@Nullable String) -> void`; access GGUF path.
- `getContextSize() -> int` / `setContextSize(int) -> void`; access token context.
- `getMaxOutputTokens() -> int` / `setMaxOutputTokens(int) -> void`; access output cap.
- `getTemperature() -> float` / `setTemperature(float) -> void`; access sampling temperature.
- `getThreads() -> int` / `setThreads(int) -> void`; access inference threads.
- `getCharsPerToken() -> int` / `setCharsPerToken(int) -> void`; configure automatic input-limit estimate.
- `getMaxInputChars() -> int` / `setMaxInputChars(int) -> void`; access source character cap.
- `isWarnOnTrim() -> boolean` / `setWarnOnTrim(boolean) -> void`; configure trim warnings.
- `getTopP() -> float` / `setTopP(float) -> void`; access nucleus threshold.
- `getTopK() -> int` / `setTopK(int) -> void`; access top-k limit.
- `getRepeatPenalty() -> float` / `setRepeatPenalty(float) -> void`; access repetition penalty.
- `getMinP() -> float` / `setMinP(float) -> void`; access min-p threshold.
- `getTopNSigma() -> float` / `setTopNSigma(float) -> void`; access top-n-sigma threshold.
- `getChatTemplateEnableThinking() -> @Nullable Boolean` / `setChatTemplateEnableThinking(@Nullable Boolean) -> void`; configure thinking kwarg.
- `isCachePrompt() -> boolean` / `setCachePrompt(boolean) -> void`; configure prompt caching.
- `isSwaFull() -> boolean` / `setSwaFull(boolean) -> void`; configure full SWA KV cache.
- `getCacheReuse() -> int` / `setCacheReuse(int) -> void`; configure KV prefix reuse.
- `getGpuLayers() -> int` / `setGpuLayers(int) -> void`; configure GPU layer offload.
- `getCpuMoeLayers() -> int` / `setCpuMoeLayers(int) -> void`; configure CPU MoE layers.
- `getSeed() -> int` / `setSeed(int) -> void`; configure generation seed.
- `getCpuFfnLayers() -> int` / `setCpuFfnLayers(int) -> void`; configure CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` / `setKvUnifiedPerSlot(int) -> void`; configure per-slot KV cap.
- `getLazyMode() -> String` / `setLazyMode(@Nullable String) -> void`; configure tensor-read laziness.
- `getRepeatLastN() -> int` / `setRepeatLastN(int) -> void`; configure repetition window.
- `getCacheTypeK() -> String` / `setCacheTypeK(@Nullable String) -> void`; configure K-cache type.
- `getCacheTypeV() -> String` / `setCacheTypeV(@Nullable String) -> void`; configure V-cache type.
- `isFlashAttn() -> boolean` / `setFlashAttn(boolean) -> void`; configure Flash Attention.
- `getBatchSize() -> int` / `setBatchSize(int) -> void`; configure logical batch size.
- `getUbatchSize() -> int` / `setUbatchSize(int) -> void`; configure physical batch size.
- `getThreadsBatch() -> int` / `setThreadsBatch(int) -> void`; configure prompt-processing threads.
- `getMainGpu() -> int` / `setMainGpu(int) -> void`; select primary GPU.
- `getDevices() -> String` / `setDevices(String) -> void`; configure backend device list.
- `getProvider() -> String` / `setProvider(String) -> void`; select per-model provider.
- `getRemoteModel() -> String` / `setRemoteModel(String) -> void`; configure remote model ID.
- `getBaseUrl() -> String` / `setBaseUrl(String) -> void`; configure API base URL.
- `getApiKeyEnv() -> String` / `setApiKeyEnv(String) -> void`; name API-key environment variable.
- `getReasoningEffort() -> String` / `setReasoningEffort(String) -> void`; configure reasoning-effort kwarg.
- `getReasoningBudgetTokens() -> int` / `setReasoningBudgetTokens(int) -> void`; configure reasoning token cap.
- `getDryMultiplier() -> float` / `setDryMultiplier(float) -> void`; configure DRY penalty activation.
- `getDryBase() -> float` / `setDryBase(float) -> void`; configure DRY growth base.
- `getDryAllowedLength() -> int` / `setDryAllowedLength(int) -> void`; configure unpenalized n-gram length.
- `getDryPenaltyLastN() -> int` / `setDryPenaltyLastN(int) -> void`; configure DRY look-back window.
- `getDrySequenceBreakers() -> List<String>` / `setDrySequenceBreakers(@Nullable List<String>) -> void`; access DRY sequence breakers.
- `getStopStrings() -> List<String>` / `setStopStrings(@Nullable List<String>) -> void`; access generation stop strings.
- `getCalibration() -> @Nullable AiCalibration` / `setCalibration(@Nullable AiCalibration) -> void`; access machine timing calibration.

#### Dependencies
- Java: `ArrayList`, `Collections`, `List`, `Runtime`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project type: `AiCalibration`.
- Referenced integration concepts include llama.cpp, `net.ladenthin.llama.parameters.ModelParameters`, and `FlashAttn`.

#### Exceptions / Errors
- No exceptions are explicitly thrown or caught.
- No validation is performed by setters.
- `setLazyMode`, `setCacheTypeK`, and `setCacheTypeV` map `null` to empty defaults; provider/model/URL/API-key setters map `null` to their defaults.
- `setDevices(null)` maps to empty; nullable list setters replace `null` with an empty mutable list.

#### Concurrency
- Mutable JavaBean with no synchronization or thread-safety guarantees.
- List getters return unmodifiable views, but setters retain the supplied non-null list reference.

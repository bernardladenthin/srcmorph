### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:04:57Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Stores per-model AI generation settings for prompt limits, sampling, inference hardware, provider routing, and output controls.

#### Purpose
- Carries mutable generation parameters from Maven configuration to AI providers.
- Supplies defaults for model inference, prompt trimming, sampling, provider routing, and calibration.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString` and `@SuppressWarnings`; no declared supertypes.
- `AiCalibration` — referenced nullable calibration type; not declared in this file.

#### Input
- No-argument constructor; initializes instance fields from defaults.
- Setter parameters configure model path, context and output limits, sampling, threading, prompt caching, GPU/device settings, provider and remote API options, reasoning and DRY controls, stop strings, and calibration.
- `setModelPath`, `setChatTemplateEnableThinking`, and `setCalibration` accept nullable values. Several string setters reset null to defaults; list setters reset null to empty lists.
- `DEFAULT_THREADS` reads `Runtime.getRuntime().availableProcessors()`.

#### Output
- Getter methods expose configured values; list getters return unmodifiable views.
- Setters mutate configuration fields. Null list inputs become new empty lists; selected nullable strings become their default values.
- Lombok `@ToString` generates a string representation of instance fields; equals/hashCode are not generated.

#### Core logic
- Initializes inference and prompt settings from constants, including context size, output tokens, thread count, input character limits, and sampling defaults.
- Defines defaults for llama.cpp execution: prompt caching, SWA/cache reuse, GPU and CPU layer selection, KV settings, attention, batching, devices, and seeds.
- Defines provider defaults: empty per-model provider override, empty remote model ID, OpenRouter base URL, and empty API-key environment-variable name.
- Defines reasoning and repetition controls, including chat-template thinking mode, reasoning effort/budget, and DRY sampling parameters.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` substitute defaults for null inputs.
- `getDrySequenceBreakers()` and `getStopStrings()` wrap their backing lists with `Collections.unmodifiableList`; setters replace the backing list or reset it to an empty `ArrayList`.
- `DEFAULT_THREADS` is `Math.min(4, Runtime.getRuntime().availableProcessors())`; `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING` is null to represent an unset tri-state.

#### Public API
- `AiGenerationConfig()` → constructor; initializes defaults.
- Public constants: `DEFAULT_CONTEXT_SIZE`, `DEFAULT_MAX_OUTPUT_TOKENS`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`, `DEFAULT_MAX_INPUT_CHARS`, `DEFAULT_CHARS_PER_TOKEN`, `DEFAULT_SAFETY_MARGIN_CHARS`, `DEFAULT_WARN_ON_TRIM`, `DEFAULT_TOP_P`, `DEFAULT_TOP_K`, `DEFAULT_MIN_P`, `DEFAULT_TOP_N_SIGMA`, `DEFAULT_REPEAT_PENALTY`, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`, `DEFAULT_CACHE_PROMPT`, `DEFAULT_REASONING_EFFORT`, `DEFAULT_REASONING_BUDGET_TOKENS`, `DEFAULT_SWA_FULL`, `DEFAULT_CACHE_REUSE`, `DEFAULT_GPU_LAYERS`, `DEFAULT_SEED`, `DEFAULT_CPU_MOE_LAYERS`, `DEFAULT_CPU_FFN_LAYERS`, `DEFAULT_KV_UNIFIED_PER_SLOT`, `DEFAULT_LAZY_MODE`, `DEFAULT_REPEAT_LAST_N`, `DEFAULT_CACHE_TYPE_K`, `DEFAULT_CACHE_TYPE_V`, `DEFAULT_FLASH_ATTN`, `DEFAULT_BATCH_SIZE`, `DEFAULT_UBATCH_SIZE`, `DEFAULT_THREADS_BATCH`, `DEFAULT_MAIN_GPU`, `DEFAULT_DEVICES`, `DEFAULT_PROVIDER`, `DEFAULT_REMOTE_MODEL`, `DEFAULT_BASE_URL`, `DEFAULT_API_KEY_ENV`, `DEFAULT_DRY_MULTIPLIER`, `DEFAULT_DRY_BASE`, `DEFAULT_DRY_ALLOWED_LENGTH`, `DEFAULT_DRY_PENALTY_LAST_N`.
- `getModelPath()` / `setModelPath(String)` → `String` / `void`; model file path.
- `getContextSize()` / `setContextSize(int)` → `int` / `void`; context window.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` → `int` / `void`; output token limit.
- `getTemperature()` / `setTemperature(float)` → `float` / `void`; sampling temperature.
- `getThreads()` / `setThreads(int)` → `int` / `void`; inference thread count.
- `getCharsPerToken()` / `setCharsPerToken(int)` → `int` / `void`; automatic input sizing ratio.
- `getMaxInputChars()` / `setMaxInputChars(int)` → `int` / `void`; prompt source character limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` → `boolean` / `void`; trim warning toggle.
- `getTopP()` / `setTopP(float)` → `float` / `void`; nucleus-sampling threshold.
- `getTopK()` / `setTopK(int)` → `int` / `void`; top-k sampling limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` → `float` / `void`; repetition penalty.
- `getMinP()` / `setMinP(float)` → `float` / `void`; min-p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` → `float` / `void`; top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` → `@Nullable Boolean` / `void`; nullable thinking-mode override.
- `isCachePrompt()` / `setCachePrompt(boolean)` → `boolean` / `void`; prompt-prefix caching toggle.
- `isSwaFull()` / `setSwaFull(boolean)` → `boolean` / `void`; full SWA KV cache toggle.
- `getCacheReuse()` / `setCacheReuse(int)` → `int` / `void`; KV prefix-reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` → `int` / `void`; GPU offload layer count.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` → `int` / `void`; CPU MoE layer count.
- `getSeed()` / `setSeed(int)` → `int` / `void`; generation RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` → `int` / `void`; CPU FFN layer count.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` → `int` / `void`; per-slot KV context cap.
- `getLazyMode()` / `setLazyMode(String)` → `String` / `void`; tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` → `int` / `void`; repetition-penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` → `String` / `void`; K-cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` → `String` / `void`; V-cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` → `boolean` / `void`; Flash Attention toggle.
- `getBatchSize()` / `setBatchSize(int)` → `int` / `void`; logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` → `int` / `void`; physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` → `int` / `void`; prompt-processing thread count.
- `getMainGpu()` / `setMainGpu(int)` → `int` / `void`; primary GPU index.
- `getDevices()` / `setDevices(String)` → `String` / `void`; backend device selection.
- `getProvider()` / `setProvider(String)` → `String` / `void`; model-specific provider override.
- `getRemoteModel()` / `setRemoteModel(String)` → `String` / `void`; remote API model ID.
- `getBaseUrl()` / `setBaseUrl(String)` → `String` / `void`; OpenAI-compatible API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` → `String` / `void`; API-key environment variable name.
- `getReasoningEffort()` / `setReasoningEffort(String)` → `String` / `void`; reasoning-effort level.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` → `int` / `void`; reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` → `float` / `void`; DRY sampling multiplier.
- `getDryBase()` / `setDryBase(float)` → `float` / `void`; DRY penalty growth base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` → `int` / `void`; DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` → `int` / `void`; DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` → `List<String>` / `void`; DRY sequence breakers.
- `getStopStrings()` / `setStopStrings(List<String>)` → `List<String>` / `void`; generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` → `@Nullable AiCalibration` / `void`; optional timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCalibration`
- `Runtime`

#### Exceptions / Errors
- Nullable annotations mark `modelPath`, `chatTemplateEnableThinking`, and `calibration`; nullable list setters clear their lists.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` normalize null to defaults.
- Other setters do not validate values; downstream consumers may reject invalid configuration.

#### Concurrency
- Mutable JavaBean state is not synchronized or documented as thread-safe.
- List getters are unmodifiable views of mutable backing lists; they are not snapshots.

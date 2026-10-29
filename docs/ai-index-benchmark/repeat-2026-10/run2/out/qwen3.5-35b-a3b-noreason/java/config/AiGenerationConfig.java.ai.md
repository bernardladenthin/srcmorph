### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:29Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters including model paths, sampling strategies, GPU offloading, and performance tuning for the srcmorph Maven plugin.

#### Purpose
*   Acts as a mutable JavaBean to carry all AI generation step parameters between the Maven configuration layer and AI provider implementations.
*   Stores model paths, sampling parameters, retry policies, input trimming limits, and stop strings for a single generation run.

#### Type
*   `public class AiGenerationConfig`
*   Annotations: `@ToString` (Lombok), `@Nullable` (Jspecify)
*   No explicit `extends` or `implements` clauses.

#### Input
*   Constructor: `AiGenerationConfig()` (no-arg, applies defaults).
*   Setter methods: `setModelPath`, `setContextSize`, `setMaxOutputTokens`, `setTemperature`, `setThreads`, `setCharsPerToken`, `setMaxInputChars`, `setWarnOnTrim`, `setTopP`, `setTopK`, `setMinP`, `setTopNSigma`, `setRepeatPenalty`, `setChatTemplateEnableThinking`, `setCachePrompt`, `setSwaFull`, `setCacheReuse`, `setGpuLayers`, `setSeed`, `setCpuMoeLayers`, `setCpuFfnLayers`, `setKvUnifiedPerSlot`, `setLazyMode`, `setRepeatLastN`, `setCacheTypeK`, `setCacheTypeV`, `setFlashAttn`, `setBatchSize`, `setUbatchSize`, `setThreadsBatch`, `setMainGpu`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`, `setReasoningEffort`, `setReasoningBudgetTokens`, `setDryMultiplier`, `setDryBase`, `setDryAllowedLength`, `setDryPenaltyLastN`, `setDrySequenceBreakers`, `setStopStrings`, `setCalibration`.
*   Dependencies: `AiCalibration` (injected via `setCalibration`).

#### Output
*   Getter methods: `getModelPath`, `getContextSize`, `getMaxOutputTokens`, `getTemperature`, `getThreads`, `getCharsPerToken`, `getMaxInputChars`, `isWarnOnTrim`, `getTopP`, `getTopK`, `getRepeatPenalty`, `getMinP`, `getTopNSigma`, `getChatTemplateEnableThinking`, `isCachePrompt`, `isSwaFull`, `getCacheReuse`, `getGpuLayers`, `getCpuMoeLayers`, `getSeed`, `getCpuFfnLayers`, `getKvUnifiedPerSlot`, `getLazyMode`, `getRepeatLastN`, `getCacheTypeK`, `getCacheTypeV`, `isFlashAttn`, `getBatchSize`, `getUbatchSize`, `getThreadsBatch`, `getMainGpu`, `getDevices`, `getProvider`, `getRemoteModel`, `getBaseUrl`, `getApiKeyEnv`, `getReasoningEffort`, `getReasoningBudgetTokens`, `getDryMultiplier`, `getDryBase`, `getDryAllowedLength`, `getDryPenaltyLastN`, `getDrySequenceBreakers`, `getStopStrings`, `getCalibration`.
*   State: Mutates internal fields (`modelPath`, `contextSize`, `maxOutputTokens`, etc.) to reflect configuration.
*   Side effects: `toString` generates diagnostic output via Lombok.

#### Core logic
*   Initializes all fields to static default constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`, `DEFAULT_THREADS`) upon instantiation.
*   Provides JavaBean-style setters that handle `null` inputs by resetting to specific defaults (e.g., `null` for `provider` resets to `DEFAULT_PROVIDER`).
*   Maintains a tri-state logic for `chatTemplateEnableThinking` (`null` omits the kwarg, `true`/`false` forwards it).
*   Maintains a tri-state logic for GPU/CPU layer offloading (`-1` leaves default, `0` forces CPU, positive value offloads layers).
*   Maintains a tri-state logic for DRY penalty (`-1` uses llama.cpp default, `0` disables, positive sets window).
*   Maintains a tri-state logic for reasoning budget (`-1` unrestricted, `0` no thinking, positive caps tokens).
*   Stores lists for `drySequenceBreakers` and `stopStrings`, returning unmodifiable views via getters.
*   Calculates `maxInputChars` automatically based on `charsPerToken` if not explicitly set (logic implied by field description).
*   Enforces guards on setters (e.g., `threadsBatch` must be `> 0` if set, `cacheTypeK` must be positive if set).

#### Public API
*   `AiGenerationConfig() -> void` (Constructor)
*   `getModelPath() -> String` (Returns model file path)
*   `setModelPath(String) -> void` (Sets model file path)
*   `getContextSize() -> int` (Returns context window size)
*   `setContextSize(int) -> void` (Sets context window size)
*   `getMaxOutputTokens() -> int` (Returns max output tokens)
*   `setMaxOutputTokens(int) -> void` (Sets max output tokens)
*   `getTemperature() -> float` (Returns sampling temperature)
*   `setTemperature(float) -> void` (Sets sampling temperature)
*   `getThreads() -> int` (Returns CPU thread count)
*   `setThreads(int) -> void` (Sets CPU thread count)
*   `getCharsPerToken() -> int` (Returns chars per token ratio)
*   `setCharsPerToken(int) -> void` (Sets chars per token ratio)
*   `getMaxInputChars() -> int` (Returns max input characters)
*   `setMaxInputChars(int) -> void` (Sets max input characters)
*   `isWarnOnTrim() -> boolean` (Returns trim warning status)
*   `setWarnOnTrim(boolean) -> void` (Sets trim warning status)
*   `getTopP() -> float` (Returns nucleus-sampling threshold)
*   `setTopP(float) -> void` (Sets nucleus-sampling threshold)
*   `getTopK() -> int` (Returns top-k limit)
*   `setTopK(int) -> void` (Sets top-k limit)
*   `getRepeatPenalty() -> float` (Returns repetition penalty)
*   `setRepeatPenalty(float) -> void` (Sets repetition penalty)
*   `getMinP() -> float` (Returns min-p threshold)
*   `setMinP(float) -> void` (Sets min-p threshold)
*   `getTopNSigma() -> float` (Returns top-n-sigma threshold)
*   `setTopNSigma(float) -> void` (Sets top-n-sigma threshold)
*   `getChatTemplateEnableThinking() -> Boolean` (Returns thinking mode state)
*   `setChatTemplateEnableThinking(Boolean) -> void` (Sets thinking mode state)
*   `isCachePrompt() -> boolean` (Returns prompt caching status)
*   `setCachePrompt(boolean) -> void` (Sets prompt caching status)
*   `isSwaFull() -> boolean` (Returns full SWA KV status)
*   `setSwaFull(boolean) -> void` (Sets full SWA KV status)
*   `getCacheReuse() -> int` (Returns cache reuse chunk size)
*   `setCacheReuse(int) -> void` (Sets cache reuse chunk size)
*   `getGpuLayers() -> int` (Returns GPU offload layers)
*   `setGpuLayers(int) -> void` (Sets GPU offload layers)
*   `getCpuMoeLayers() -> int` (Returns CPU MoE layers)
*   `setCpuMoeLayers(int) -> void` (Sets CPU MoE layers)
*   `getSeed() -> int` (Returns RNG seed)
*   `setSeed(int) -> void` (Sets RNG seed)
*   `getCpuFfnLayers() -> int` (Returns CPU FFN layers)
*   `setCpuFfnLayers(int) -> void` (Sets CPU FFN layers)
*   `getKvUnifiedPerSlot() -> int` (Returns KV context cap)
*   `setKvUnifiedPerSlot(int) -> void` (Sets KV context cap)
*   `getLazyMode() -> String` (Returns tensor-read laziness)
*   `setLazyMode(String) -> void` (Sets tensor-read laziness)
*   `getRepeatLastN() -> int` (Returns repeat penalty window)
*   `setRepeatLastN(int) -> void` (Sets repeat penalty window)
*   `getCacheTypeK() -> String` (Returns KV cache type K)
*   `setCacheTypeK(String) -> void` (Sets KV cache type K)
*   `getCacheTypeV() -> String` (Returns KV cache type V)
*   `setCacheTypeV(String) -> void` (Sets KV cache type V)
*   `isFlashAttn() -> boolean` (Returns Flash Attention status)
*   `setFlashAttn(boolean) -> void` (Sets Flash Attention status)
*   `getBatchSize() -> int` (Returns logical batch size)
*   `setBatchSize(int) -> void` (Sets logical batch size)
*   `getUbatchSize() -> int` (Returns physical batch size)
*   `setUbatchSize(int) -> void` (Sets physical batch size)
*   `getThreadsBatch() -> int` (Returns batch thread count)
*   `setThreadsBatch(int) -> void` (Sets batch thread count)
*   `getMainGpu() -> int` (Returns primary GPU index)
*   `setMainGpu(int) -> void` (Sets primary GPU index)
*   `getDevices() -> String` (Returns device selection list)
*   `setDevices(String) -> void` (Sets device selection list)
*   `getProvider() -> String` (Returns AI provider name)
*   `setProvider(String) -> void` (Sets AI provider name)
*   `getRemoteModel() -> String` (Returns remote model ID)
*   `setRemoteModel(String) -> void` (Sets remote model ID)
*   `getBaseUrl() -> String` (Returns API base URL)
*   `setBaseUrl(String) -> void` (Sets API base URL)
*   `getApiKeyEnv() -> String` (Returns API key env var name)
*   `setApiKeyEnv(String) -> void` (Sets API key env var name)
*   `getReasoningEffort() -> String` (Returns reasoning effort level)
*   `setReasoningEffort(String) -> void` (Sets reasoning effort level)
*   `getReasoningBudgetTokens() -> int` (Returns reasoning budget)
*   `setReasoningBudgetTokens(int) -> void` (Sets reasoning budget)
*   `getDryMultiplier() -> float` (Returns DRY multiplier)
*   `setDryMultiplier(float) -> void` (Sets DRY multiplier)
*   `getDryBase() -> float` (Returns DRY base)
*   `setDryBase(float) -> void` (Sets DRY base)
*   `getDryAllowedLength() -> int` (Returns DRY allowed length)
*   `setDryAllowedLength(int) -> void` (Sets DRY allowed length)
*   `getDryPenaltyLastN() -> int` (Returns DRY penalty last-n)
*   `setDryPenaltyLastN(int) -> void` (Sets DRY penalty last-n)
*   `getDrySequenceBreakers() -> List<String>` (Returns DRY sequence breakers)
*   `setDrySequenceBreakers(List<String>) -> void` (Sets DRY sequence breakers)
*   `getStopStrings() -> List<String>` (Returns stop strings)
*   `setStopStrings(List<String>) -> void` (Sets stop strings)
*   `getCalibration() -> AiCalibration` (Returns calibration object)
*   `setCalibration(AiCalibration) -> void` (Sets calibration object)

#### Dependencies
*   `java.util.ArrayList`
*   `java.util.Collections`
*   `java.util.List`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
*   No checked exceptions declared.
*   Null handling: Setters for `String` and `List` fields reset to defaults if `null` is passed.
*   Validation: Some setters imply guards (e.g., `threadsBatch` > 0, `cacheTypeK` positive) but do not throw exceptions in the shown code; upstream binding may reject invalid values.

#### Concurrency
*   Mutable JavaBean; not thread-safe.
*   Managed by identity in the Maven plugin framework.
*   `getDrySequenceBreakers` and `getStopStrings` return unmodifiable views to prevent external mutation.

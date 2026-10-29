### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:51:00Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration object carrying parameters for AI generation steps, bridging Maven configuration and AI provider implementations.

#### Purpose
* Stores sampling, hardware offloading, and provider-specific settings for AI inference.
* Provides a set of domain-specific defaults for llama.cpp and OpenAI-compatible providers.

#### Type
* Class; final (implied by lack of extends/implements, though not explicitly marked final).
* Annotations: `@ToString` (Lombok), `@SuppressWarnings`.

#### Input
* Constructor: No-arg default.
* Setters: Various primitives (`int`, `float`, `boolean`), `String`, `Boolean`, `List<String>`, and `AiCalibration`.

#### Output
* Getters: Return the current state of all configuration fields.
* `getDrySequenceBreakers()` and `getStopStrings()`: Return unmodifiable `List<String>`.

#### Core logic
* **Default Management**: Defines a comprehensive set of constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`) to ensure consistent behavior across different AI providers.
* **Input Trimming**: Supports automatic calculation of `maxInputChars` based on `charsPerToken`, context size, and a safety margin.
* **Hardware Tuning**: Manages GPU layer offloading, CPU MoE/FFN layer counts, and thread allocation for both decode and batch processing.
* **Sampling Control**: Implements various sampling strategies including Nucleus (top-p), top-k, min-p, top-n-sigma, and DRY (Don't Repeat Yourself) penalties.
* **KV Cache Optimization**: Configures prompt caching, SWA full-size cache, and KV cache quantization types (K and V).
* **Provider Routing**: Allows overriding the global generation provider on a per-model basis via `provider` and `baseUrl`.

#### Public API
* `getModelPath() -> @Nullable String` / `setModelPath(@Nullable String) -> void`
* `getContextSize() -> int` / `setContextSize(int) -> void`
* `getMaxOutputTokens() -> int` / `setMaxOutputTokens(int) -> void`
* `getTemperature() -> float` / `setTemperature(float) -> void`
* `getThreads() -> int` / `setThreads(int) -> void`
* `getCharsPerToken() -> int` / `setCharsPerToken(int) -> void`
* `getMaxInputChars() -> int` / `setMaxInputChars(int) -> void`
* `isWarnOnTrim() -> boolean` / `setWarnOnTrim(boolean) -> void`
* `getTopP() -> float` / `setTopP(float) -> void`
* `getTopK() -> int` / `setTopK(int) -> void`
* `getRepeatPenalty() -> float` / `setRepeatPenalty(float) -> void`
* `getMinP() -> float` / `setMinP(float) -> void`
* `getTopNSigma() -> float` / `setTopNSigma(float) -> void`
* `getChatTemplateEnableThinking() -> @Nullable Boolean` / `setChatTemplateEnableThinking(@Nullable Boolean) -> void`
* `isCachePrompt() -> boolean` / `setCachePrompt(boolean) -> void`
* `isSwaFull() -> boolean` / `setSwaFull(boolean) -> void`
* `getCacheReuse() -> int` / `setCacheReuse(int) -> void`
* `getGpuLayers() -> int` / `setGpuLayers(int) -> void`
* `getSeed() -> int` / `setSeed(int) -> void`
* `getCpuMoeLayers() -> int` / `setCpuMoeLayers(int) -> void`
* `getCpuFfnLayers() -> int` / `setCpuFfnLayers(int) -> void`
* `getKvUnifiedPerSlot() -> int` / `setKvUnifiedPerSlot(int) -> void`
* `getLazyMode() -> String` / `setLazyMode(@Nullable String) -> void`
* `getRepeatLastN() -> int` / `setRepeatLastN(int) -> void`
* `getCacheTypeK() -> String` / `setCacheTypeK(@Nullable String) -> void`
* `getCacheTypeV() -> String` / `setCacheTypeV(@Nullable String) -> void`
* `isFlashAttn() -> boolean` / `setFlashAttn(boolean) -> void`
* `getBatchSize() -> int` / `setBatchSize(int) -> void`
* `getUbatchSize() -> int` / `setUbatchSize(int) -> void`
* `getThreadsBatch() -> int` / `setThreadsBatch(int) -> void`
* `getMainGpu() -> int` / `setMainGpu(int) -> void`
* `getDevices() -> String` / `setDevices(String) -> void`
* `getProvider() -> String` / `setProvider(String) -> void`
* `getRemoteModel() -> String` / `setRemoteModel(String) -> void`
* `getBaseUrl() -> String` / `setBaseUrl(String) -> void`
* `getApiKeyEnv() -> String` / `setApiKeyEnv(String) -> void`
* `getReasoningEffort() -> String` / `setReasoningEffort(String) -> void`
* `getReasoningBudgetTokens() -> int` / `setReasoningBudgetTokens(int) -> void`
* `getDryMultiplier() -> float` / `setDryMultiplier(float) -> void`
* `getDryBase() -> float` / `setDryBase(float) -> void`
* `getDryAllowedLength() -> int` / `setDryAllowedLength(int) -> void`
* `getDryPenaltyLastN() -> int` / `setDryPenaltyLastN(int) -> void`
* `getDrySequenceBreakers() -> List<String>` / `setDrySequenceBreakers(@Nullable List<String>) -> void`
* `getStopStrings() -> List<String>` / `setStopStrings(@Nullable List<String>) -> void`
* `getCalibration() -> @Nullable AiCalibration` / `setCalibration(@Nullable AiCalibration) -> void`

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collections`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCalibration`

#### Concurrency
* Mutable JavaBean; not thread-safe. Managed by identity.

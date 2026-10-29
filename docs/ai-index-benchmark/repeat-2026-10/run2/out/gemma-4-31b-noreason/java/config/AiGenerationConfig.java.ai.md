### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:27Z
- G: 
- A: 0.0.0
- X: file
---
> Mutable configuration object carrying parameters for AI generation steps, bridging Maven configuration and AI provider implementations.

#### Purpose
* Stores sampling, hardware offloading, and provider-specific settings for AI inference.
* Provides a set of defaults for llama.cpp and OpenAI-compatible providers.

#### Type
Class; final (implicit); `@ToString` (Lombok).

#### Input
* Constructor: No-arg.
* Setters: Primitive types, `String`, `Boolean`, `List<String>`, and `AiCalibration`.

#### Output
* Getters: Corresponding primitive types, `String`, `Boolean`, `List<String>`, and `AiCalibration`.
* `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.

#### Core logic
* Acts as a JavaBean for the Maven plugin framework to populate via setters.
* Maintains a comprehensive set of constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`) to ensure consistent fallback behavior.
* Manages tri-state logic for `chatTemplateEnableThinking` (`null` = omit kwarg, `true`/`false` = forward verbatim).
* Handles null-to-default conversion in setters for `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, and `apiKeyEnv`.

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
* Mutable state; not thread-safe. Managed by identity within the Maven plugin framework.

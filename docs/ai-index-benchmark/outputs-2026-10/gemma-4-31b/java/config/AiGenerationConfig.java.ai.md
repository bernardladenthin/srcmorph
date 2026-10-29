### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:26:06Z
- G: 
- A: 0.0.0
- X: file
---
> Mutable configuration object carrying parameters for AI generation steps between Maven configuration and AI provider implementations.

#### Purpose
* Acts as a data carrier for model paths, sampling parameters, and hardware offloading settings.
* Provides default values for llama.cpp and OpenAI-compatible inference engines.

#### Type
Class; final (implied by JavaBean usage); annotated with `@ToString` (Lombok) and `@SuppressWarnings`.

#### Input
* Constructor: No-op default constructor.
* Setters: Various primitives (`int`, `float`, `boolean`), `String`, `Boolean`, `List<String>`, and `AiCalibration`.

#### Output
* Getters: Return the current state of configuration fields.
* `getDrySequenceBreakers()` / `getStopStrings()`: Return unmodifiable `List<String>`.

#### Core logic
* Maintains a large set of mutable fields representing AI inference knobs (e.g., `temperature`, `topP`, `gpuLayers`).
* Implements a "sentinel" pattern where specific values (e.g., `-1` or empty strings) signal the underlying provider to use its own internal defaults rather than overriding them.
* Handles automatic input character limit calculations via `charsPerToken` and `maxInputChars` logic (though the calculation itself is performed by the consumer of this config).
* Manages tri-state logic for `chatTemplateEnableThinking` using a nullable `Boolean` to distinguish between "enabled", "disabled", and "unset".

#### Public API
* `getModelPath() -> String` / `setModelPath(String) -> void`
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
* `getChatTemplateEnableThinking() -> Boolean` / `setChatTemplateEnableThinking(Boolean) -> void`
* `isCachePrompt() -> boolean` / `setCachePrompt(boolean) -> void`
* `isSwaFull() -> boolean` / `setSwaFull(boolean) -> void`
* `getCacheReuse() -> int` / `setCacheReuse(int) -> void`
* `getGpuLayers() -> int` / `setGpuLayers(int) -> void`
* `getCpuMoeLayers() -> int` / `setCpuMoeLayers(int) -> void`
* `getSeed() -> int` / `setSeed(int) -> void`
* `getCpuFfnLayers() -> int` / `setCpuFfnLayers(int) -> void`
* `getKvUnifiedPerSlot() -> int` / `setKvUnifiedPerSlot(int) -> void`
* `getLazyMode() -> String` / `setLazyMode(String) -> void`
* `getRepeatLastN() -> int` / `setRepeatLastN(int) -> void`
* `getCacheTypeK() -> String` / `setCacheTypeK(String) -> void`
* `getCacheTypeV() -> String` / `setCacheTypeV(String) -> void`
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
* `getDrySequenceBreakers() -> List<String>` / `setDrySequenceBreakers(List<String>) -> void`
* `getStopStrings() -> List<String>` / `setStopStrings(List<String>) -> void`
* `getCalibration() -> AiCalibration` / `setCalibration(AiCalibration) -> void`

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collections`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCalibration`

#### Concurrency
* Not thread-safe; designed as a mutable JavaBean for population by the Maven plugin framework.

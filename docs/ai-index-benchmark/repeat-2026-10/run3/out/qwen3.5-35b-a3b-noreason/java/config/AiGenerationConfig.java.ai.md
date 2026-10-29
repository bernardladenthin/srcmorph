### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:32Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters including model paths, sampling strategies, hardware offloading, and performance tuning for the srcmorph Maven plugin.

#### Purpose
*   Acts as a mutable JavaBean to carry all AI generation step parameters between the Maven configuration layer and AI provider implementations.
*   Stores model paths, sampling parameters (temperature, top-p, top-k), retry policies, input limits, and stop strings.

#### Type
*   `public class AiGenerationConfig`
*   Annotated with `@ToString` (Lombok) for build-log diagnostics.
*   Implements no interfaces; extends `Object`.
*   Fields are mutable with explicit getters/setters; `equals`/`hashCode` are intentionally omitted.

#### Input
*   **Constructor:** `AiGenerationConfig()` initializes all fields to default constants.
*   **Setters:** All configuration parameters are injected via public setter methods (e.g., `setModelPath`, `setTemperature`, `setGpuLayers`).
*   **Dependencies:** `AiCalibration` object injected via `setCalibration`.
*   **Lists:** `drySequenceBreakers` and `stopStrings` are initialized as empty `ArrayList`s.

#### Output
*   **Return Values:** Getters return current configuration state (e.g., `getContextSize()`, `getProvider()`).
*   **State Mutation:** Instance fields are mutated directly by setters to reflect the current configuration.
*   **Side Effects:** None directly; the object serves as a data carrier for downstream AI providers.

#### Core logic
*   **Default Initialization:** Applies static final constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`) to all fields upon instantiation.
*   **Parameter Forwarding:** Stores specific flags (e.g., `cachePrompt`, `swaFull`, `flashAttn`) to control llama.cpp behavior like prompt caching and Flash Attention.
*   **Hardware Tuning:** Manages GPU/CPU offloading parameters (`gpuLayers`, `cpuMoeLayers`, `cpuFfnLayers`) and thread counts (`threads`, `threadsBatch`).
*   **Sampling Control:** Configures nucleus sampling (`topP`, `topK`, `minP`, `topNSigma`) and repetition penalties (`repeatPenalty`, `dryMultiplier`).
*   **Input/Output Limits:** Calculates and stores `maxInputChars` based on `charsPerToken` and `contextSize`, with optional trimming warnings.
*   **Provider Routing:** Stores `provider` type (`llamacpp-jni`, `openai-compatible`, `mock`) and associated remote details (`baseUrl`, `remoteModel`, `apiKeyEnv`).
*   **Safety & Reproducibility:** Manages `seed` for deterministic generation and `reasoningBudgetTokens` to limit chain-of-thought token usage.

#### Public API
*   `getModelPath() -> String`
*   `setModelPath(String) -> void`
*   `getContextSize() -> int`
*   `setContextSize(int) -> void`
*   `getMaxOutputTokens() -> int`
*   `setMaxOutputTokens(int) -> void`
*   `getTemperature() -> float`
*   `setTemperature(float) -> void`
*   `getThreads() -> int`
*   `setThreads(int) -> void`
*   `getCharsPerToken() -> int`
*   `setCharsPerToken(int) -> void`
*   `getMaxInputChars() -> int`
*   `setMaxInputChars(int) -> void`
*   `isWarnOnTrim() -> boolean`
*   `setWarnOnTrim(boolean) -> void`
*   `getTopP() -> float`
*   `setTopP(float) -> void`
*   `getTopK() -> int`
*   `setTopK(int) -> void`
*   `getRepeatPenalty() -> float`
*   `setRepeatPenalty(float) -> void`
*   `getMinP() -> float`
*   `setMinP(float) -> void`
*   `getTopNSigma() -> float`
*   `setTopNSigma(float) -> void`
*   `getChatTemplateEnableThinking() -> Boolean`
*   `setChatTemplateEnableThinking(Boolean) -> void`
*   `isCachePrompt() -> boolean`
*   `setCachePrompt(boolean) -> void`
*   `isSwaFull() -> boolean`
*   `setSwaFull(boolean) -> void`
*   `getCacheReuse() -> int`
*   `setCacheReuse(int) -> void`
*   `getGpuLayers() -> int`
*   `setGpuLayers(int) -> void`
*   `getCpuMoeLayers() -> int`
*   `setCpuMoeLayers(int) -> void`
*   `getSeed() -> int`
*   `setSeed(int) -> void`
*   `getCpuFfnLayers() -> int`
*   `setCpuFfnLayers(int) -> void`
*   `getKvUnifiedPerSlot() -> int`
*   `setKvUnifiedPerSlot(int) -> void`
*   `getLazyMode() -> String`
*   `setLazyMode(String) -> void`
*   `getRepeatLastN() -> int`
*   `setRepeatLastN(int) -> void`
*   `getCacheTypeK() -> String`
*   `setCacheTypeK(String) -> void`
*   `getCacheTypeV() -> String`
*   `setCacheTypeV(String) -> void`
*   `isFlashAttn() -> boolean`
*   `setFlashAttn(boolean) -> void`
*   `getBatchSize() -> int`
*   `setBatchSize(int) -> void`
*   `getUbatchSize() -> int`
*   `setUbatchSize(int) -> void`
*   `getThreadsBatch() -> int`
*   `setThreadsBatch(int) -> void`
*   `getMainGpu() -> int`
*   `setMainGpu(int) -> void`
*   `getDevices() -> String`
*   `setDevices(String) -> void`
*   `getProvider() -> String`
*   `setProvider(String) -> void`
*   `getRemoteModel() -> String`
*   `setRemoteModel(String) -> void`
*   `getBaseUrl() -> String`
*   `setBaseUrl(String) -> void`
*   `getApiKeyEnv() -> String`
*   `setApiKeyEnv(String) -> void`
*   `getReasoningEffort() -> String`
*   `setReasoningEffort(String) -> void`
*   `getReasoningBudgetTokens() -> int`
*   `setReasoningBudgetTokens(int) -> void`
*   `getDryMultiplier() -> float`
*   `setDryMultiplier(float) -> void`
*   `getDryBase() -> float`
*   `setDryBase(float) -> void`
*   `getDryAllowedLength() -> int`
*   `setDryAllowedLength(int) -> void`
*   `getDryPenaltyLastN() -> int`
*   `setDryPenaltyLastN(int) -> void`
*   `getDrySequenceBreakers() -> List<String>`
*   `setDrySequenceBreakers(List<String>) -> void`
*   `getStopStrings() -> List<String>`
*   `setStopStrings(List<String>) -> void`
*   `getCalibration() -> AiCalibration`
*   `setCalibration(AiCalibration) -> void`

#### Dependencies
*   `net.ladenthin.srcmorph.config.AiCalibration`
*   `java.util.ArrayList`
*   `java.util.Collections`
*   `java.util.List`
*   `org.jspecify.annotations.Nullable`
*   `lombok.ToString`

#### Exceptions / Errors
*   **Null Handling:** Many setters accept `null` to reset to default values (e.g., `setProvider`, `setDevices`).
*   **Validation:** Some setters enforce guards (e.g., `setKvUnifiedPerSlot` requires positive values; `setRepeatLastN` rejects negatives).
*   **No Checked Exceptions:** No `throws` clauses declared on public methods.

#### Concurrency
*   **Not Thread-Safe:** The class is mutable and intended for single-threaded configuration population by the Maven plugin framework.
*   **Immutable Views:** `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable list views to prevent external mutation.

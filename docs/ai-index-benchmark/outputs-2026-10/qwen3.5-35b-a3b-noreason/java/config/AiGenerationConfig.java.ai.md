### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:55:50Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters including model paths, sampling strategies, hardware offloading, and prompt handling for the srcmorph Maven plugin.

#### Purpose
*   Acts as a mutable JavaBean configuration object to pass AI generation parameters between the Maven plugin layer and AI provider implementations.
*   Carries all necessary settings for a single AI generation step, including model paths, sampling parameters, retry policies, and input limits.

#### Type
*   `public class AiGenerationConfig`
*   Annotated with `@ToString` (Lombok) for build-log diagnostics.
*   Implements no interfaces; extends `Object`.
*   Uses `@Nullable` annotations from `org.jspecify.annotations` for optional fields.
*   Suppressed warnings for `NullAway.Init` and `initialization.fields.uninitialized`.

#### Input
*   **Constructor:** `AiGenerationConfig()` (no-arg, applies defaults).
*   **Setter Methods:** 46 public setters (e.g., `setModelPath`, `setTemperature`, `setGpuLayers`, `setProvider`) used by the Maven plugin framework to populate fields.
*   **Dependencies:** `AiCalibration` object injected via `setCalibration`.
*   **Fields:** 43 instance fields initialized to static default constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`).

#### Output
*   **Getters:** 46 public getters (e.g., `getModelPath`, `getTemperature`, `getGpuLayers`) returning current configuration state.
*   **State:** Mutable internal state of the instance fields (e.g., `modelPath`, `contextSize`, `gpuLayers`).
*   **Collections:** Returns unmodifiable views of `drySequenceBreakers` and `stopStrings` via `Collections.unmodifiableList`.

#### Core logic
*   **Default Initialization:** All instance fields are initialized to static `DEFAULT_*` constants upon instantiation (e.g., `DEFAULT_CONTEXT_SIZE = 32768`, `DEFAULT_TEMPERATURE = 0.15f`).
*   **JavaBean Pattern:** Relies entirely on setter methods to modify state; `equals` and `hashCode` are intentionally omitted to support identity-based management by the Maven framework.
*   **Null-Safe Defaults:** Setter methods for String fields (e.g., `setProvider`, `setBaseUrl`) reset to `DEFAULT_*` constants if `null` is passed.
*   **Collection Handling:** `setDrySequenceBreakers` and `setStopStrings` replace the internal `ArrayList` with the provided list or a new empty one if `null`.
*   **Configuration Propagation:** Fields map directly to `llama.cpp` command-line arguments (e.g., `--gpu-layers`, `--cache-type-k`, `--dry-multiplier`) or OpenAI-compatible API parameters.
*   **Tri-State Logic:** `chatTemplateEnableThinking` uses `null` to omit the kwarg entirely, `true`/`false` to force values, avoiding sending noise to models that don't support the parameter.

#### Public API
*   `AiGenerationConfig()` -> Creates instance with all defaults applied.
*   `getModelPath() -> String` -> Returns GGUF model file path.
*   `setModelPath(String) -> void` -> Sets the GGUF model file path.
*   `getContextSize() -> int` -> Returns context window size in tokens.
*   `setContextSize(int) -> void` -> Sets the context window size in tokens.
*   `getMaxOutputTokens() -> int` -> Returns maximum output tokens per call.
*   `setMaxOutputTokens(int) -> void` -> Sets maximum output tokens per call.
*   `getTemperature() -> float` -> Returns sampling temperature.
*   `setTemperature(float) -> void` -> Sets sampling temperature.
*   `getThreads() -> int` -> Returns number of CPU threads for inference.
*   `setThreads(int) -> void` -> Sets number of CPU threads for inference.
*   `getCharsPerToken() -> int` -> Returns chars-per-token ratio.
*   `setCharsPerToken(int) -> void` -> Sets chars-per-token ratio.
*   `getMaxInputChars() -> int` -> Returns maximum input characters.
*   `setMaxInputChars(int) -> void` -> Sets maximum input characters.
*   `isWarnOnTrim() -> boolean` -> Returns trim warning status.
*   `setWarnOnTrim(boolean) -> void` -> Sets trim warning status.
*   `getTopP() -> float` -> Returns nucleus-sampling threshold.
*   `setTopP(float) -> void` -> Sets nucleus-sampling threshold.
*   `getTopK() -> int` -> Returns top-k sampling limit.
*   `setTopK(int) -> void` -> Sets top-k sampling limit.
*   `getRepeatPenalty() -> float` -> Returns repetition penalty.
*   `setRepeatPenalty(float) -> void` -> Sets repetition penalty.
*   `getMinP() -> float` -> Returns min-p sampling threshold.
*   `setMinP(float) -> void` -> Sets min-p sampling threshold.
*   `getTopNSigma() -> float` -> Returns top-n-sigma threshold.
*   `setTopNSigma(float) -> void` -> Sets top-n-sigma threshold.
*   `getChatTemplateEnableThinking() -> Boolean` -> Returns chat-template thinking mode.
*   `setChatTemplateEnableThinking(Boolean) -> void` -> Sets chat-template thinking mode.
*   `isCachePrompt() -> boolean` -> Returns prompt caching status.
*   `setCachePrompt(boolean) -> void` -> Sets prompt caching status.
*   `isSwaFull() -> boolean` -> Returns full SWA KV cache status.
*   `setSwaFull(boolean) -> void` -> Sets full SWA KV cache status.
*   `getCacheReuse() -> int` -> Returns KV prefix-reuse chunk size.
*   `setCacheReuse(int) -> void` -> Sets KV prefix-reuse chunk size.
*   `getGpuLayers() -> int` -> Returns GPU offload layer count.
*   `setGpuLayers(int) -> void` -> Sets GPU offload layer count.
*   `getCpuMoeLayers() -> int` -> Returns CPU MoE layer count.
*   `setCpuMoeLayers(int) -> void` -> Sets CPU MoE layer count.
*   `getSeed() -> int` -> Returns RNG seed.
*   `setSeed(int) -> void` -> Sets RNG seed.
*   `getCpuFfnLayers() -> int` -> Returns CPU FFN layer count.
*   `setCpuFfnLayers(int) -> void` -> Sets CPU FFN layer count.
*   `getKvUnifiedPerSlot() -> int` -> Returns per-slot KV context cap.
*   `setKvUnifiedPerSlot(int) -> void` -> Sets per-slot KV context cap.
*   `getLazyMode() -> String` -> Returns tensor-read laziness mode.
*   `setLazyMode(String) -> void` -> Sets tensor-read laziness mode.
*   `getRepeatLastN() -> int` -> Returns repeat-penalty window.
*   `setRepeatLastN(int) -> void` -> Sets repeat-penalty window.
*   `getCacheTypeK() -> String` -> Returns KV-cache K data type.
*   `setCacheTypeK(String) -> void` -> Sets KV-cache K data type.
*   `getCacheTypeV() -> String` -> Returns KV-cache V data type.
*   `setCacheTypeV(String) -> void` -> Sets KV-cache V data type.
*   `isFlashAttn() -> boolean` -> Returns Flash Attention status.
*   `setFlashAttn(boolean) -> void` -> Sets Flash Attention status.
*   `getBatchSize() -> int` -> Returns logical batch size.
*   `setBatchSize(int) -> void` -> Sets logical batch size.
*   `getUbatchSize() -> int` -> Returns physical batch size.
*   `setUbatchSize(int) -> void` -> Sets physical batch size.
*   `getThreadsBatch() -> int` -> Returns batch thread count.
*   `setThreadsBatch(int) -> void` -> Sets batch thread count.
*   `getMainGpu() -> int` -> Returns primary GPU index.
*   `setMainGpu(int) -> void` -> Sets primary GPU index.
*   `getDevices() -> String` -> Returns device selection list.
*   `setDevices(String) -> void` -> Sets device selection list.
*   `getProvider() -> String` -> Returns AI provider name.
*   `setProvider(String) -> void` -> Sets AI provider name.
*   `getRemoteModel() -> String` -> Returns remote model ID.
*   `setRemoteModel(String) -> void` -> Sets remote model ID.
*   `getBaseUrl() -> String` -> Returns OpenAI-compatible API base URL.
*   `setBaseUrl(String) -> void` -> Sets OpenAI-compatible API base URL.
*   `getApiKeyEnv() -> String` -> Returns API key environment variable name.
*   `setApiKeyEnv(String) -> void` -> Sets API key environment variable name.
*   `getReasoningEffort() -> String` -> Returns reasoning effort level.
*   `setReasoningEffort(String) -> void` -> Sets reasoning effort level.
*   `getReasoningBudgetTokens() -> int` -> Returns reasoning token budget.
*   `setReasoningBudgetTokens(int) -> void` -> Sets reasoning token budget.
*   `getDryMultiplier() -> float` -> Returns DRY sampling multiplier.
*   `setDryMultiplier(float) -> void` -> Sets DRY sampling multiplier.
*   `getDryBase() -> float` -> Returns DRY base.
*   `setDryBase(float) -> void` -> Sets DRY base.
*   `getDryAllowedLength() -> int` -> Returns DRY allowed length.
*   `setDryAllowedLength(int) -> void` -> Sets DRY allowed length.
*   `getDryPenaltyLastN() -> int` -> Returns DRY penalty look-back window.
*   `setDryPenaltyLastN(int) -> void` -> Sets DRY penalty look-back window.
*   `getDrySequenceBreakers() -> List<String>` -> Returns unmodifiable DRY sequence breakers.
*   `setDrySequenceBreakers(List<String>) -> void` -> Sets DRY sequence breakers.
*   `getStopStrings() -> List<String>` -> Returns unmodifiable stop strings.
*   `setStopStrings(List<String>) -> void` -> Sets stop strings.
*   `getCalibration() -> AiCalibration` -> Returns per-machine timing calibration.
*   `setCalibration(AiCalibration) -> void` -> Sets per-machine timing calibration.

#### Dependencies
*   `java.util.ArrayList`
*   `java.util.Collections`
*   `java.util.List`
*   `java.lang.Runtime`
*   `org.jspecify.annotations.Nullable`
*   `lombok.ToString`
*   `net.ladenthin.srcmorph.config.AiCalibration`

#### Exceptions / Errors
*   No checked exceptions declared.
*   `setKvUnifiedPerSlot` and `setBatchSize` documentation notes that negative or zero values may be rejected by the underlying binding or are invalid.
*   `setDryPenaltyLastN` documentation notes that negative values are rejected by the binding (sentinel `-1` is handled by the caller, not the setter).

#### Concurrency
*   Not thread-safe; mutable JavaBean intended for single-threaded configuration population by the Maven plugin.
*   No synchronization mechanisms present.

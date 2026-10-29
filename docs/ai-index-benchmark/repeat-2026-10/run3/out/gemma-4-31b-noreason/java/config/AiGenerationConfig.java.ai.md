### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:25:51Z
- G: 
- A: 0.0.0
- X: file
---
> A mutable configuration object carrying parameters for AI generation steps, bridging Maven configuration and AI provider implementations.

#### Purpose
* Stores sampling, hardware offloading, and provider-specific settings for AI inference.
* Provides a set of domain-specific defaults for llama.cpp and OpenAI-compatible APIs.

#### Type
* Class, final (implied by lack of extends/implements, though not explicitly marked final).
* Annotations: `@ToString` (Lombok), `@SuppressWarnings`.

#### Input
* Constructor: No-arg default.
* Setters: Various primitives (`int`, `float`, `boolean`), `String`, `Boolean`, `List<String>`, and `AiCalibration`.

#### Output
* Getters: Return the current state of the configuration fields.
* Side effects: Mutates internal state via setter methods.

#### Core logic
* **Default Management**: Defines a comprehensive set of constants (e.g., `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`) to ensure consistent behavior across different AI providers.
* **Input Trimming**: Supports automatic calculation of `maxInputChars` based on `charsPerToken`, context size, and a safety margin.
* **Hardware Tuning**: Manages GPU layer offloading, CPU MoE/FFN layer counts, and thread allocation for both general inference and batch processing.
* **Sampling Control**: Implements parameters for nucleus sampling (`topP`), top-k, min-p, top-n-sigma, and repetition penalties.
* **KV Cache Optimization**: Configures prompt caching, SWA full-size cache, and KV cache quantization types (`cacheTypeK`/`cacheTypeV`).
* **DRY Sampling**: Provides a set of "Don't Repeat Yourself" parameters to prevent n-gram repetition loops.
* **Provider Routing**: Allows overriding the global generation provider on a per-model basis.

#### Public API
* `getModelPath() -> String` / `setModelPath(String) -> void`: GGUF model file path.
* `getContextSize() -> int` / `setContextSize(int) -> void`: Context window in tokens.
* `getMaxOutputTokens() -> int` / `setMaxOutputTokens(int) -> void`: Max output tokens per call.
* `getTemperature() -> float` / `setTemperature(float) -> void`: Sampling temperature.
* `getThreads() -> int` / `setThreads(int) -> void`: CPU threads for inference.
* `getCharsPerToken() -> int` / `setCharsPerToken(int) -> void`: Ratio for input char calculation.
* `getMaxInputChars() -> int` / `setMaxInputChars(int) -> void`: Max input characters.
* `isWarnOnTrim() -> boolean` / `setWarnOnTrim(boolean) -> void`: Toggle for trim warnings.
* `getTopP() -> float` / `setTopP(float) -> void`: Nucleus-sampling threshold.
* `getTopK() -> int` / `setTopK(int) -> void`: Top-k sampling limit.
* `getRepeatPenalty() -> float` / `setRepeatPenalty(float) -> void`: Repetition penalty.
* `getMinP() -> float` / `setMinP(float) -> void`: Min-p sampling threshold.
* `getTopNSigma() -> float` / `setTopNSigma(float) -> void`: Top-n-sigma threshold.
* `getChatTemplateEnableThinking() -> Boolean` / `setChatTemplateEnableThinking(Boolean) -> void`: Tri-state thinking mode.
* `isCachePrompt() -> boolean` / `setCachePrompt(boolean) -> void`: Enable prompt-prefix KV reuse.
* `isSwaFull() -> boolean` / `setSwaFull(boolean) -> void`: Keep full SWA KV cache.
* `getCacheReuse() -> int` / `setCacheReuse(int) -> void`: KV prefix-reuse chunk size.
* `getGpuLayers() -> int` / `setGpuLayers(int) -> void`: GPU offload layer count.
* `getSeed() -> int` / `setSeed(int) -> void`: RNG seed for reproducibility.
* `getCpuMoeLayers() -> int` / `setCpuMoeLayers(int) -> void`: MoE layers on CPU.
* `getCpuFfnLayers() -> int` / `setCpuFfnLayers(int) -> void`: FFN layers on CPU.
* `getKvUnifiedPerSlot() -> int` / `setKvUnifiedPerSlot(int) -> void`: Per-slot KV context cap.
* `getLazyMode() -> String` / `setLazyMode(String) -> void`: Tensor-read laziness setting.
* `getRepeatLastN() -> int` / `setRepeatLastN(int) -> void`: Repeat-penalty window.
* `getCacheTypeK() -> String` / `setCacheTypeK(String) -> void`: KV-cache data type for K.
* `getCacheTypeV() -> String` / `setCacheTypeV(String) -> void`: KV-cache data type for V.
* `isFlashAttn() -> boolean` / `setFlashAttn(boolean) -> void`: Flash Attention toggle.
* `getBatchSize() -> int` / `setBatchSize(int) -> void`: Logical batch size.
* `getUbatchSize() -> int` / `setUbatchSize(int) -> void`: Physical batch size.
* `getThreadsBatch() -> int` / `setThreadsBatch(int) -> void`: Batch processing thread count.
* `getMainGpu() -> int` / `setMainGpu(int) -> void`: Primary GPU index.
* `getDevices() -> String` / `setDevices(String) -> void`: Comma-separated device list.
* `getProvider() -> String` / `setProvider(String) -> void`: Model-specific AI provider.
* `getRemoteModel() -> String` / `setRemoteModel(String) -> void`: OpenAI-compatible model ID.
* `getBaseUrl() -> String` / `setBaseUrl(String) -> void`: OpenAI-compatible API base URL.
* `getApiKeyEnv() -> String` / `setApiKeyEnv(String) -> void`: API key environment variable name.
* `getReasoningEffort() -> String` / `setReasoningEffort(String) -> void`: gpt-oss reasoning effort.
* `getReasoningBudgetTokens() -> int` / `setReasoningBudgetTokens(int) -> void`: Reasoning token budget.
* `getDryMultiplier() -> float` / `setDryMultiplier(float) -> void`: DRY sampling multiplier.
* `getDryBase() -> float` / `setDryBase(float) -> void`: DRY exponential base.
* `getDryAllowedLength() -> int` / `setDryAllowedLength(int) -> void`: DRY max unpenalized n-gram.
* `getDryPenaltyLastN() -> int` / `setDryPenaltyLastN(int) -> void`: DRY look-back window.
* `getDrySequenceBreakers() -> List<String>` / `setDrySequenceBreakers(List<String>) -> void`: DRY sequence breaker tokens.
* `getStopStrings() -> List<String>` / `setStopStrings(List<String>) -> void`: Generation stop strings.
* `getCalibration() -> AiCalibration` / `setCalibration(AiCalibration) -> void`: Per-machine timing calibration.

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collections`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCalibration`

#### Concurrency
* Not thread-safe; mutable JavaBean intended for identity-based management.

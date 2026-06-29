### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:12:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures LLM inference parameters for Maven plugin field generation by defining a reusable POJO for model paths, context sizes, and sampling algorithms.

#### Purpose
*   Defines a single-source configuration object for AI model parameters used in Java-based field generation within Maven plugins.
*   Supports reusability across multiple generation goals via a unique lookup key mechanism.

#### Type
*   Class: `public class AiModelDefinition`
*   Annotations: `@ToString`, `@SuppressWarnings`
*   Fields: Mutable JavaBean with getters/setters; uses `java.util.List` (via `ArrayList`/`Collections`) for stop strings.

#### Input
*   Constructor: None (default constructor applies).
*   Parameters: `String key`, `String modelPath`, `int contextSize`, `int maxOutputTokens`, `float temperature`, `int threads`, `int charsPerToken`, `boolean warnOnTrim`, `int maxRetries`, `float retryTemperatureIncrement`, `float topP`, `int topK`, `float repeatPenalty`, `boolean chatTemplateEnableThinking`, `Collection<String> stopStrings`.

#### Output
*   Returns: String (key, modelPath), Int (contextSize, maxOutputTokens, threads, charsPerToken, maxRetries, topK), Float (temperature, retryTemperatureIncrement, topP, repeatPenalty), Boolean (warnOnTrim, chatTemplateEnableThinking), List<String> (stopStrings).
*   State: Mutable fields updated via setters to configure LLM behavior.

#### Core logic
*   Initializes numeric fields with defaults from `AiGenerationConfig` unless explicitly overridden.
*   Stores a unique string key for mapping between field-generation configs and this model definition.
*   Tracks the file path to the GGUF model binary for inference execution.
*   Configures context window size to limit input token consumption.
*   Sets sampling parameters (temperature, top-p, top-k) to control generation randomness and diversity.
*   Defines retry logic thresholds including maximum attempts and temperature increments per failure.
*   Calculates automatic max input character limits based on `charsPerToken` ratio for source code trimming.
*   Manages stop sequences to terminate generation upon specific string patterns.

#### Public API
*   `getKey() -> String`: Retrieves unique model identifier.
*   `setKey(String) -> void`: Assigns unique model identifier.
*   `getModelPath() -> String`: Retrieves GGUF model file path.
*   `setModelPath(String) -> void`: Assigns GGUF model file path.
*   `getContextSize() -> int`: Retrieves context window size in tokens.
*   `setContextSize(int) -> void`: Sets context window size in tokens.
*   `getMaxOutputTokens() -> int`: Retrieves max output token limit.
*   `setMaxOutputTokens(int) -> void`: Sets max output token limit.
*   `getTemperature() -> float`: Retrieves base sampling temperature.
*   `setTemperature(float) -> void`: Sets base sampling temperature.
*   `getThreads() -> int`: Retrieves CPU thread count for inference.
*   `setThreads(int) -> void`: Sets CPU thread count for inference.
*   `getCharsPerToken() -> int`: Retrieves chars-per-token ratio for input calculation.
*   `setCharsPerToken(int) -> void`: Sets chars-per-token ratio for input calculation.
*   `isWarnOnTrim() -> boolean`: Checks warning flag on source trimming.
*   `setWarnOnTrim(boolean) -> void`: Enables/disables warning on source trimming.
*   `getMaxRetries() -> int`: Retrieves max retry attempt count.
*   `setMaxRetries(int) -> void`: Sets max retry attempt count.
*   `getRetryTemperatureIncrement() -> float`: Retrieves temp increment per retry.
*   `setRetryTemperatureIncrement(float) -> void`: Sets temp increment per retry.
*   `getTopP() -> float`: Retrieves nucleus sampling threshold.
*   `setTopP(float) -> void`: Sets nucleus sampling threshold.
*   `getTopK() -> int`: Retrieves top-k sampling limit.
*   `setTopK(int) -> void`: Sets top-k sampling limit.
*   `getRepeatPenalty() -> float`: Retrieves repetition penalty factor.
*   `setRepeatPenalty(float) -> void`: Sets repetition penalty factor.
*   `isChatTemplateEnableThinking() -> boolean`: Checks chat template thinking mode.
*   `setChatTemplateEnableThinking(boolean) -> void`: Toggles chat template thinking mode.
*   `getStopStrings() -> List<String>`: Retrieves list of generation stop strings.
*   `setStopStrings(Collection<String>) -> void`: Sets list of generation stop strings.

#### Dependencies
*   `java.util.ArrayList`
*   `java.util.Collection`
*   `java.util.Collections`
*   `java.util.List`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `AiGenerationConfig`
*   `net.ladenthin.llama.parameters.ModelParameters`

#### Exceptions / Errors
*   Throws: None declared.
*   Null Handling: Allows null for key, modelPath, stopStrings; returns unmodifiable lists for safe access.
*   Error Conditions: Relies on defaults from `AiGenerationConfig` if fields are unset.

#### Concurrency
*   Thread Safety: Not explicitly synchronized; assumes configuration is set before use by inference engine.
*   Immutability: Mutable state via setters; `getStopStrings()` returns unmodifiable view to prevent runtime mutation.

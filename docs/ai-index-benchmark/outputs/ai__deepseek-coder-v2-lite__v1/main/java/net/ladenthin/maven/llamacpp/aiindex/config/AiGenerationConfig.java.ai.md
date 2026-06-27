### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:07:51Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Provide a mutable configuration object for AI generation parameters.
- Support default values and provide access to these parameters through setters and getters.

#### Type
- Class (`public final class AiGenerationConfig`)
- Lombok annotations: `@ToString`

#### Input
- No constructor parameters.
- Default values are applied in the constructors.

#### Output
- Return types for getter methods (e.g., `String getModelPath()`, `int getContextSize()`).
- Mutation of instance fields through setter methods (e.g., `setModelPath(String modelPath)`, `setStopStrings(@Nullable List<String> stopStrings)`).

#### Core logic
- **Initialization**: No explicit constructor logic beyond default values.
- **Parameter Setting**: Methods like `setModelPath(String modelPath)`, `setContextSize(int contextSize)`, etc., to modify configuration parameters.
- **Default Values**: Defined constants for each parameter (e.g., `DEFAULT_CONTEXT_SIZE`).

#### Public API
- `getModelPath() -> String`: Returns the GGUF model file path.
- `setModelPath(String modelPath) -> void`: Sets the GGUF model file path.
- `getContextSize() -> int`: Returns the context window size in tokens.
- `setContextSize(int contextSize) -> void`: Sets the context window size in tokens.
- `getMaxOutputTokens() -> int`: Returns the maximum number of output tokens per inference call.
- `setMaxOutputTokens(int maxOutputTokens) -> void`: Sets the maximum number of output tokens per inference call.
- `getTemperature() -> float`: Returns the sampling temperature.
- `setTemperature(float temperature) -> void`: Sets the sampling temperature.
- `getThreads() -> int`: Returns the number of CPU threads used for inference.
- `setThreads(int threads) -> void`: Sets the number of CPU threads used for inference.
- `getCharsPerToken() -> int`: Returns the number of characters per token used in automatic maxInputChars calculation.
- `setCharsPerToken(int charsPerToken) -> void`: Sets the number of characters per token.
- `getMaxInputChars() -> int`: Returns the maximum number of input characters fed to the prompt.
- `setMaxInputChars(int maxInputChars) -> void`: Sets the maximum number of input characters fed to the prompt.
- `isWarnOnTrim() -> boolean`: Returns whether a warning is emitted when the prompt source text is trimmed.
- `setWarnOnTrim(boolean warnOnTrim) -> void`: Sets whether a warning is emitted when the prompt source text is trimmed.
- `getMaxRetries() -> int`: Returns the maximum number of retry attempts on empty-body responses.
- `setMaxRetries(int maxRetries) -> void`: Sets the maximum number of retry attempts on empty-body responses.
- `getRetryTemperatureIncrement() -> float`: Returns the temperature increment added on each retry attempt.
- `setRetryTemperatureIncrement(float retryTemperatureIncrement) -> void`: Sets the temperature increment added on each retry attempt.
- `getTopP() -> float`: Returns the nucleus-sampling probability threshold.
- `setTopP(float topP) -> void`: Sets the nucleus-sampling probability threshold.
- `getTopK() -> int`: Returns the top-k sampling limit.
- `setTopK(int topK) -> void`: Sets the top-k sampling limit.
- `getRepeatPenalty() -> float`: Returns the repetition penalty.
- `setRepeatPenalty(float repeatPenalty) -> void`: Sets the repetition penalty.
- `isChatTemplateEnableThinking() -> boolean`: Returns whether the model's chat-template thinking mode is enabled.
- `setChatTemplateEnableThinking(boolean chatTemplateEnableThinking) -> void`: Sets whether the model's chat-template thinking mode is enabled.
- `getStopStrings() -> @Nullable List<String>`: Returns an unmodifiable view of the configured stop strings.
- `setStopStrings(@Nullable List<String> stopStrings) -> void`: Sets the list of stop strings.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- No explicit error handling or exception throwing mentioned.
- Nullable handling for `@Nullable List<String> stopStrings`.

#### Concurrency
- Class is final, implying it is not thread-safe.
- Fields are mutable but accessed through setter methods, suggesting controlled mutation in a single-threaded context.

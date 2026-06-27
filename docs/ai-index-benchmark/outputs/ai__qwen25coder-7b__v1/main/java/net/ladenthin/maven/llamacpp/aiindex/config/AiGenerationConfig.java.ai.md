### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:22:58Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Manages configuration parameters for AI generation steps in a Maven project.

#### Purpose
- Holds all parameters for an AI generation step.
- Transports these parameters between the Maven configuration layer and AI provider implementations.

#### Type
- Class
- Final
- Lombok annotations: `@ToString`

#### Input
- No constructor parameters.
- No injected dependencies.
- Consumed fields:
  - `modelPath`
  - `contextSize`
  - `maxOutputTokens`
  - `temperature`
  - `threads`
  - `charsPerToken`
  - `maxInputChars`
  - `warnOnTrim`
  - `maxRetries`
  - `retryTemperatureIncrement`
  - `topP`
  - `topK`
  - `repeatPenalty`
  - `chatTemplateEnableThinking`
  - `stopStrings`

#### Output
- No return types.
- Mutated fields:
  - `modelPath`
  - `contextSize`
  - `maxOutputTokens`
  - `temperature`
  - `threads`
  - `charsPerToken`
  - `maxInputChars`
  - `warnOnTrim`
  - `maxRetries`
  - `retryTemperatureIncrement`
  - `topP`
  - `topK`
  - `repeatPenalty`
  - `chatTemplateEnableThinking`
  - `stopStrings`

#### Core logic
- Defines default values for AI generation parameters.
- Provides getter and setter methods to configure and retrieve these parameters.

#### Public API
- `AiGenerationConfig()`: Creates a new instance with default settings.
- `getModelPath() -> String`: Returns the model file path.
- `setModelPath(String modelPath)`: Sets the model file path.
- `getContextSize() -> int`: Returns the context window size in tokens.
- `setContextSize(int contextSize)`: Sets the context window size in tokens.
- `getMaxOutputTokens() -> int`: Returns the maximum number of output tokens per inference call.
- `setMaxOutputTokens(int maxOutputTokens)`: Sets the maximum number of output tokens per inference call.
- `getTemperature() -> float`: Returns the sampling temperature.
- `setTemperature(float temperature)`: Sets the sampling temperature.
- `getThreads() -> int`: Returns the number of CPU threads used for inference.
- `setThreads(int threads)`: Sets the number of CPU threads used for inference.
- `getCharsPerToken() -> int`: Returns the number of characters per token used in automatic `maxInputChars` calculation.
- `setCharsPerToken(int charsPerToken)`: Sets the number of characters per token.
- `getMaxInputChars() -> int`: Returns the maximum number of input characters fed to the prompt.
- `setMaxInputChars(int maxInputChars)`: Sets the maximum number of input characters fed to the prompt.
- `isWarnOnTrim() -> boolean`: Returns whether a warning is emitted when the prompt source text is trimmed.
- `setWarnOnTrim(boolean warnOnTrim)`: Sets whether a warning is emitted when the prompt source text is trimmed.
- `getMaxRetries() -> int`: Returns the maximum number of retry attempts on empty-body responses.
- `setMaxRetries(int maxRetries)`: Sets the maximum number of retry attempts on empty-body responses.
- `getRetryTemperatureIncrement() -> float`: Returns the temperature increment added on each retry attempt.
- `setRetryTemperatureIncrement(float retryTemperatureIncrement)`: Sets the temperature increment added on each retry attempt.
- `getTopP() -> float`: Returns the nucleus-sampling probability threshold.
- `setTopP(float topP)`: Sets the nucleus-sampling probability threshold.
- `getTopK() -> int`: Returns the top-k sampling limit.
- `setTopK(int topK)`: Sets the top-k sampling limit.
- `getRepeatPenalty() -> float`: Returns the repetition penalty.
- `setRepeatPenalty(float repeatPenalty)`: Sets the repetition penalty.
- `isChatTemplateEnableThinking() -> boolean`: Returns whether the model's chat-template thinking mode is enabled.
- `setChatTemplateEnableThinking(boolean chatTemplateEnableThinking)`: Sets whether the model's chat-template thinking mode is enabled.
- `getStopStrings() -> List<String>`: Returns an unmodifiable view of the configured stop strings.
- `setStopStrings(List<String> stopStrings)`: Sets the list of stop strings.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable exceptions.
- Null-handling: `stopStrings` is set to an empty list if `null` is passed.

#### Concurrency
- Not concurrency-related.

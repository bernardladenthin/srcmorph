### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:23:10Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents a Maven plugin configuration POJO that pairs a lookup key with a complete set of AI model parameters.

#### Purpose
- Holds the configuration for an AI model definition.
- Used in a Maven plugin to manage and reuse AI model configurations across multiple field-generation entries and goals.

#### Type
```java
public class AiModelDefinition
```

#### Input
- `key` (String): Unique lookup key for this definition.
- `modelPath` (String): Path to the GGUF model file.
- `contextSize` (int): Context window size (in tokens).
- `maxOutputTokens` (int): Maximum number of output tokens per inference call.
- `temperature` (float): Base sampling temperature.
- `threads` (int): Number of CPU threads for inference.
- `charsPerToken` (int): Number of characters per token.
- `warnOnTrim` (boolean): Whether to emit a warning when source text is trimmed.
- `maxRetries` (int): Maximum number of retry attempts.
- `retryTemperatureIncrement` (float): Temperature increment applied on each successive retry attempt.
- `topP` (float): Nucleus-sampling probability threshold.
- `topK` (int): Top-k sampling limit.
- `repeatPenalty` (float): Repetition penalty.
- `chatTemplateEnableThinking` (boolean): Whether the model's chat-template thinking mode is enabled.
- `stopStrings` (List<String>): List of stop strings that terminate generation when encountered.

#### Output
- None

#### Core logic
- Manages and stores configuration for AI model definitions.
- Provides getters and setters for all configuration fields.

#### Public API
- `getKey() -> String`: Returns the unique lookup key for this definition.
- `getModelPath() -> String`: Returns the path to the GGUF model file.
- `getContextSize() -> int`: Returns the context window size (in tokens).
- `getMaxOutputTokens() -> int`: Returns the maximum number of output tokens per inference call.
- `getTemperature() -> float`: Returns the base sampling temperature.
- `getThreads() -> int`: Returns the number of CPU threads for inference.
- `getCharsPerToken() -> int`: Returns the number of characters per token.
- `isWarnOnTrim() -> boolean`: Returns whether to emit a warning when source text is trimmed.
- `getMaxRetries() -> int`: Returns the maximum number of retry attempts.
- `getRetryTemperatureIncrement() -> float`: Returns the temperature increment applied on each successive retry attempt.
- `getTopP() -> float`: Returns the nucleus-sampling probability threshold.
- `getTopK() -> int`: Returns the top-k sampling limit.
- `getRepeatPenalty() -> float`: Returns the repetition penalty.
- `isChatTemplateEnableThinking() -> boolean`: Returns whether the model's chat-template thinking mode is enabled.
- `getStopStrings() -> List<String>`: Returns the list of stop strings that terminate generation when encountered.
- `setKey(String key)`: Sets the unique lookup key for this definition.
- `setModelPath(String modelPath)`: Sets the path to the GGUF model file.
- `setContextSize(int contextSize)`: Sets the context window size (in tokens).
- `setMaxOutputTokens(int maxOutputTokens)`: Sets the maximum number of output tokens per inference call.
- `setTemperature(float temperature)`: Sets the base sampling temperature.
- `setThreads(int threads)`: Sets the number of CPU threads for inference.
- `setCharsPerToken(int charsPerToken)`: Sets the number of characters per token.
- `setWarnOnTrim(boolean warnOnTrim)`: Sets whether to emit a warning when source text is trimmed.
- `setMaxRetries(int maxRetries)`: Sets the maximum number of retry attempts.
- `setRetryTemperatureIncrement(float retryTemperatureIncrement)`: Sets the temperature increment applied on each successive retry attempt.
- `setTopP(float topP)`: Sets the nucleus-sampling probability threshold.
- `setTopK(int topK)`: Sets the top-k sampling limit.
- `setRepeatPenalty(float repeatPenalty)`: Sets the repetition penalty.
- `setChatTemplateEnableThinking(boolean chatTemplateEnableThinking)`: Sets whether the model's chat-template thinking mode is enabled.
- `setStopStrings(Collection<String> stopStrings)`: Sets the list of stop strings that terminate generation when encountered.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None

#### Concurrency
- Not applicable

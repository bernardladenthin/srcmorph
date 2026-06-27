### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:29:37Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents a configuration POJO for AI model definitions used in a Maven plugin.

#### Purpose
- Stores and manages parameters for AI models.
- Configurable via Maven plugin.
- Shared across multiple field-generation entries.

#### Type
- Class
- Lombok `@ToString` annotation

#### Input
- No constructor parameters
- Injected dependencies: None noted
- Consumed fields:
  - `key`
  - `modelPath`
  - `contextSize`
  - `maxOutputTokens`
  - `temperature`
  - `threads`
  - `charsPerToken`
  - `warnOnTrim`
  - `maxRetries`
  - `retryTemperatureIncrement`
  - `topP`
  - `topK`
  - `repeatPenalty`
  - `chatTemplateEnableThinking`
  - `stopStrings`

#### Output
- Produced state:
  - `key`
  - `modelPath`
  - `contextSize`
  - `maxOutputTokens`
  - `temperature`
  - `threads`
  - `charsPerToken`
  - `warnOnTrim`
  - `maxRetries`
  - `retryTemperatureIncrement`
  - `topP`
  - `topK`
  - `repeatPenalty`
  - `chatTemplateEnableThinking`
  - `stopStrings`

#### Core logic
- Holds configuration parameters for AI models.
- Provides getter and setter methods for each parameter.
- Ensures default values are set from `AiGenerationConfig` when not explicitly defined.

#### Public API
- `AiModelDefinition()` -> Creates a new instance with default values.
- `String getKey()` -> Returns the unique lookup key.
- `void setKey(String key)` -> Sets the unique lookup key.
- `String getModelPath()` -> Returns the path to the GGUF model file.
- `void setModelPath(String modelPath)` -> Sets the path to the GGUF model file.
- `int getContextSize()` -> Returns the context window size.
- `void setContextSize(int contextSize)` -> Sets the context window size.
- `int getMaxOutputTokens()` -> Returns the maximum number of output tokens.
- `void setMaxOutputTokens(int maxOutputTokens)` -> Sets the maximum number of output tokens.
- `float getTemperature()` -> Returns the base sampling temperature.
- `void setTemperature(float temperature)` -> Sets the base sampling temperature.
- `int getThreads()` -> Returns the number of CPU threads for inference.
- `void setThreads(int threads)` -> Sets the number of CPU threads for inference.
- `int getCharsPerToken()` -> Returns the number of characters per token.
- `void setCharsPerToken(int charsPerToken)` -> Sets the number of characters per token.
- `boolean isWarnOnTrim()` -> Returns whether a warning is emitted when source text is trimmed.
- `void setWarnOnTrim(boolean warnOnTrim)` -> Sets whether a warning is emitted when source text is trimmed.
- `int getMaxRetries()` -> Returns the maximum number of retry attempts.
- `void setMaxRetries(int maxRetries)` -> Sets the maximum number of retry attempts.
- `float getRetryTemperatureIncrement()` -> Returns the temperature increment applied on each successive retry attempt.
- `void setRetryTemperatureIncrement(float retryTemperatureIncrement)` -> Sets the temperature increment applied on each successive retry attempt.
- `float getTopP()` -> Returns the nucleus-sampling probability threshold.
- `void setTopP(float topP)` -> Sets the nucleus-sampling probability threshold.
- `int getTopK()` -> Returns the top-k sampling limit.
- `void setTopK(int topK)` -> Sets the top-k sampling limit.
- `float getRepeatPenalty()` -> Returns the repetition penalty.
- `void setRepeatPenalty(float repeatPenalty)` -> Sets the repetition penalty applied to already-generated tokens.
- `boolean isChatTemplateEnableThinking()` -> Returns whether the model's chat-template thinking mode is enabled.
- `void setChatTemplateEnableThinking(boolean chatTemplateEnableThinking)` -> Sets whether the model's chat-template thinking mode is enabled.
- `List<String> getStopStrings()` -> Returns the list of stop strings that terminate generation when encountered.
- `void setStopStrings(Collection<String> stopStrings)` -> Sets the list of stop strings that terminate generation when encountered.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable exceptions or error handling explicitly noted.

#### Concurrency
- No concurrency-related notes or synchronization mechanisms.

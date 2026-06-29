### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:16:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Manages configuration parameters for AI generation steps between Maven and AI provider implementations.

#### Purpose
- Carries parameters for a single AI generation step.
- Provides getters/setters for configuration properties.
- Implements `toString` using Lombok for diagnostics.
- Ensures instances are mutable JavaBeans managed by identity.

#### Type
```java
public class AiGenerationConfig
```

#### Core logic
- Initializes default values for various AI generation parameters.
- Provides getter and setter methods for each parameter.

#### Public API
- `AiGenerationConfig()` -> Creates a new configuration with default values.
- `getModelPath() -> String` -> Returns the model file path.
- `setModelPath(String)` -> Sets the model file path.
- `getContextSize() -> int` -> Returns the context window size.
- `setContextSize(int)` -> Sets the context window size.
- `getMaxOutputTokens() -> int` -> Returns the maximum number of output tokens.
- `setMaxOutputTokens(int)` -> Sets the maximum number of output tokens.
- `getTemperature() -> float` -> Returns the sampling temperature.
- `setTemperature(float)` -> Sets the sampling temperature.
- `getThreads() -> int` -> Returns the number of CPU threads.
- `setThreads(int)` -> Sets the number of CPU threads.
- `getCharsPerToken() -> int` -> Returns the number of characters per token.
- `setCharsPerToken(int)` -> Sets the number of characters per token.
- `getMaxInputChars() -> int` -> Returns the maximum number of input characters.
- `setMaxInputChars(int)` -> Sets the maximum number of input characters.
- `isWarnOnTrim() -> boolean` -> Returns whether to warn on trim.
- `setWarnOnTrim(boolean)` -> Sets whether to warn on trim.
- `getMaxRetries() -> int` -> Returns the maximum number of retries.
- `setMaxRetries(int)` -> Sets the maximum number of retries.
- `getRetryTemperatureIncrement() -> float` -> Returns the retry temperature increment.
- `setRetryTemperatureIncrement(float)` -> Sets the retry temperature increment.
- `getTopP() -> float` -> Returns the nucleus-sampling probability threshold.
- `setTopP(float)` -> Sets the nucleus-sampling probability threshold.
- `getTopK() -> int` -> Returns the top-k sampling limit.
- `setTopK(int)` -> Sets the top-k sampling limit.
- `getRepeatPenalty() -> float` -> Returns the repetition penalty.
- `setRepeatPenalty(float)` -> Sets the repetition penalty.
- `isChatTemplateEnableThinking() -> boolean` -> Returns whether chat-template thinking mode is enabled.
- `setChatTemplateEnableThinking(boolean)` -> Sets whether chat-template thinking mode is enabled.
- `getStopStrings() -> List<String>` -> Returns an unmodifiable list of stop strings.
- `setStopStrings(List<String>)` -> Sets the list of stop strings.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None

#### Concurrency
- None

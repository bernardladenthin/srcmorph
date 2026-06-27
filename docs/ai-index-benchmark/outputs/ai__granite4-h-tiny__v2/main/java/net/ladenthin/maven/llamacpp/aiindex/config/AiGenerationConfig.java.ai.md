### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:30:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This file defines a mutable configuration object for AI generation steps, managing parameters like model path, context size, output tokens, temperature, retry policies, input trimming, and stop strings.

#### Purpose
- Mutable configuration object for AI generation parameters.
- Manages model path, context size, output tokens, temperature, retry policies, input trimming, and stop strings.

#### Type
- Class (`AiGenerationConfig`) extends `java.lang.Object`; implements `java.io.Serializable`.

#### Input
- Constructor parameters: none.
- Dependencies: none.
- Consumed fields: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`.

#### Output
- Returns: instance fields (`modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`).
- Produced state: none.
- Written resources: none.
- Side effects: none.

#### Core logic
- Initializes with default values.
- Provides getter and setter methods for all configuration parameters.
- Manages stop strings as an unmodifiable list.

#### Public API
- `AiGenerationConfig()`: Creates a new instance with default parameters.
- `getModelPath()`: Returns the model path.
- `setModelPath(String)`: Sets the model path.
- `getContextSize()`: Returns the context size.
- `setContextSize(int)`: Sets the context size.
- `getMaxOutputTokens()`: Returns the max output tokens.
- `setMaxOutputTokens(int)`: Sets the max output tokens.
- `getTemperature()`: Returns the temperature.
- `setTemperature(float)`: Sets the temperature.
- `getThreads()`: Returns the number of threads.
- `setThreads(int)`: Sets the number of threads.
- `getCharsPerToken()`: Returns chars per token.
- `setCharsPerToken(int)`: Sets chars per token.
- `getMaxInputChars()`: Returns max input chars.
- `setMaxInputChars(int)`: Sets max input chars.
- `isWarnOnTrim()`: Returns trim warning status.
- `setWarnOnTrim(boolean)`: Sets trim warning status.
- `getMaxRetries()`: Returns max retries.
- `setMaxRetries(int)`: Sets max retries.
- `getRetryTemperatureIncrement()`: Returns retry temperature increment.
- `setRetryTemperatureIncrement(float)`: Sets retry temperature increment.
- `getTopP()`: Returns top-p value.
- `setTopP(float)`: Sets top-p value.
- `getTopK()`: Returns top-k value.
- `setTopK(int)`: Sets top-k value.
- `getRepeatPenalty()`: Returns repeat penalty.
- `setRepeatPenalty(float)`: Sets repeat penalty.
- `isChatTemplateEnableThinking()`: Returns chat-template thinking status.
- `setChatTemplateEnableThinking(boolean)`: Sets chat-template thinking status.
- `getStopStrings()`: Returns unmodifiable list of stop strings.
- `setStopStrings(List<String>)`: Sets stop strings.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None explicitly mentioned.

#### Concurrency
- Thread-safe for immutable fields; mutable fields are managed within the class.

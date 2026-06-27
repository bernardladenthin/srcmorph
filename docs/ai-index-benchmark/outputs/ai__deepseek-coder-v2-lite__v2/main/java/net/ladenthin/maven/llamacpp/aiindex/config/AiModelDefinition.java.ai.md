### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:42:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a mutable JavaBean configuration class for AI model parameters used in a Maven plugin, facilitating the reuse of model configurations across multiple field-generation entries and goals.

#### Purpose
- **Configuration**: Provides a mutable POJO for storing and managing AI model parameters.
- **Reusability**: Allows a single model configuration to be defined once and reused across multiple field-generation entries and goals.

#### Type
- `class AiModelDefinition`
- Modifiers: `public`
- Extends/Implements: None
- Notable Annotations: `@SuppressWarnings`, `@ToString`

#### Input
- Constructor parameters: None
- Injected dependencies: None
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
- Return types: 
  - `String getKey()`
  - `String getModelPath()`
  - `int getContextSize()`
  - `int getMaxOutputTokens()`
  - `float getTemperature()`
  - `int getThreads()`
  - `int getCharsPerToken()`
  - `boolean isWarnOnTrim()`
  - `int getMaxRetries()`
  - `float getRetryTemperatureIncrement()`
  - `float getTopP()`
  - `int getTopK()`
  - `float getRepeatPenalty()`
  - `boolean isChatTemplateEnableThinking()`
  - `@Nullable List<String> getStopStrings()`
- Produced/Mutated state: 
  - Fields are set via public setter methods.

#### Core logic
- **Default Values**: Initializes fields with default values from `AiGenerationConfig`.
- **Getter and Setter Methods**: Provide access to private fields, ensuring flexibility in configuration management.

#### Public API
- `String getKey()`: Returns the unique lookup key for this definition.
- `void setKey(final String key)`: Sets the unique lookup key for this definition.
- `String getModelPath()`: Returns the path to the GGUF model file.
- `void setModelPath(final String modelPath)`: Sets the path to the GGUF model file.
- `int getContextSize()`: Returns the context window size (in tokens).
- `void setContextSize(final int contextSize)`: Sets the context window size (in tokens).
- `int getMaxOutputTokens()`: Returns the maximum number of output tokens per inference call.
- `void setMaxOutputTokens(final int maxOutputTokens)`: Sets the maximum number of output tokens per inference call.
- `float getTemperature()`: Returns the base sampling temperature.
- `void setTemperature(final float temperature)`: Sets the base sampling temperature.
- `int getThreads()`: Returns the number of CPU threads for inference.
- `void setThreads(final int threads)`: Sets the number of CPU threads for inference.
- `int getCharsPerToken()`: Returns the number of characters per token used to automatically calculate the maximum input characters for the source code.
- `void setCharsPerToken(final int charsPerToken)`: Sets the number of characters per token for automatic max-input-chars calculation.
- `boolean isWarnOnTrim()`: Returns whether a warning is emitted when source text is trimmed.
- `void setWarnOnTrim(final boolean warnOnTrim)`: Sets whether a warning is emitted when source text is trimmed.
- `int getMaxRetries()`: Returns the maximum number of retry attempts when the provider returns an empty body.
- `void setMaxRetries(final int maxRetries)`: Sets the maximum number of retry attempts when the provider returns an empty body.
- `float getRetryTemperatureIncrement()`: Returns the temperature increment applied on each successive retry attempt.
- `void setRetryTemperatureIncrement(final float retryTemperatureIncrement)`: Sets the temperature increment applied on each successive retry attempt.
- `float getTopP()`: Returns the nucleus-sampling probability threshold.
- `void setTopP(final float topP)`: Sets the nucleus-sampling probability threshold.
- `int getTopK()`: Returns the top-k sampling limit.
- `void setTopK(final int topK)`: Sets the top-k sampling limit.
- `float getRepeatPenalty()`: Returns the repetition penalty.
- `void setRepeatPenalty(final float repeatPenalty)`: Sets the repetition penalty applied to already-generated tokens.
- `boolean isChatTemplateEnableThinking()`: Returns whether the model's chat-template thinking mode is enabled.
- `void setChatTemplateEnableThinking(final boolean chatTemplateEnableThinking)`: Sets whether the model's chat-template thinking mode is enabled.
- `@Nullable List<String> getStopStrings()`: Returns the list of stop strings that terminate generation when encountered.
- `void setStopStrings(final @Nullable Collection<String> stopStrings)`: Sets the list of stop strings that terminate generation when encountered.

#### Dependencies
- Referenced types: 
  - `java.util.ArrayList`
  - `java.util.Collection`
  - `java.util.Collections`
  - `org.jspecify.annotations.Nullable`
  - `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`

#### Exceptions / Errors
- Throws: None
- Handles: Null values in setters, especially for collections like `stopStrings`.

#### Concurrency
- The class is designed to be used in a single-threaded context as it does not manage any thread-safe operations internally.
- It is assumed that the Maven plugin framework managing instances of this class ensures thread safety when multiple instances are created and manipulated concurrently.

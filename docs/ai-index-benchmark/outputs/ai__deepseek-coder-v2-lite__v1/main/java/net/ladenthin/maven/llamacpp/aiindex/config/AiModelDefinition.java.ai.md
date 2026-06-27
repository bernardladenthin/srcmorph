### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:11:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices by defining and managing AI model configurations for a Maven plugin, allowing for customization of parameters such as context size, number of threads, and more.

#### Purpose
- **Configure AI model settings** for use in a Maven plugin that generates code based on specified AI model configurations.
- **Enable or disable thinking mode** for chat templates to control the chain-of-thought reasoning during generation.

#### Type
- Class: `AiModelDefinition`
- Modifiers: `public`
- Extends: None
- Implements: None
- Key Generics or Bounds: None
- Notable Annotations: `@ToString`, `@SuppressWarnings`

#### Input
- Parameters in constructors and setters:
  - `key`: Unique identifier for the AI model definition.
  - `modelPath`: Path to the GGUF model file.
  - `contextSize`: Number of tokens in the model context window.
  - `maxOutputTokens`: Maximum number of output tokens per inference call.
  - `temperature`: Base sampling temperature for text generation.
  - `threads`: Number of CPU threads for inference.
  - `charsPerToken`: Characters per token for automatic max-input-chars calculation.
  - `warnOnTrim`: Whether to emit a warning when source text is trimmed.
  - `maxRetries`: Maximum number of retry attempts on empty body response.
  - `retryTemperatureIncrement`: Temperature increment applied on each retry.
  - `topP`: Nucleus-sampling probability threshold.
  - `topK`: Top-k sampling limit.
  - `repeatPenalty`: Repetition penalty applied to generated tokens.
  - `chatTemplateEnableThinking`: Whether to enable thinking mode for chat templates.
  - `stopStrings`: List of stop strings that terminate generation.

#### Output
- Returns:
  - `String getKey()`: Unique key for the AI model definition.
  - `String getModelPath()`: Path to the GGUF model file.
  - `int getContextSize()`: Context size in tokens.
  - `int getMaxOutputTokens()`: Maximum output tokens per inference call.
  - `float getTemperature()`: Base sampling temperature.
  - `int getThreads()`: Number of threads for inference.
  - `int getCharsPerToken()`: Characters per token for input character calculation.
  - `boolean isWarnOnTrim()`: Whether to warn on trim.
  - `int getMaxRetries()`: Maximum number of retry attempts.
  - `float getRetryTemperatureIncrement()`: Temperature increment for retries.
  - `float getTopP()`: Top-p value.
  - `int getTopK()`: Top-k value.
  - `float getRepeatPenalty()`: Repetition penalty.
  - `boolean isChatTemplateEnableThinking()`: Whether thinking mode is enabled.
  - `List<String> getStopStrings()`: List of stop strings.
- Sets:
  - Various fields through setter methods to configure the AI model settings.

#### Core logic
1. **Default Constructor**: Initializes the object with default values from `AiGenerationConfig`.
2. **Setters and Getters**: Provide access to private fields for reading and updating the AI model configuration parameters.
3. **Utility Methods**:
   - **getStopStrings()**: Returns an unmodifiable list of stop strings, ensuring immutability.
   - **setStopStrings(Collection<String>)**: Sets the stop strings collection, converting it to a mutable ArrayList internally.

#### Public API
- `getKey() -> String`: Returns the unique key for the AI model definition.
- `getModelPath() -> String`: Returns the path to the GGUF model file.
- `getContextSize() -> int`: Returns the context size in tokens.
- `getMaxOutputTokens() -> int`: Returns the maximum output tokens per inference call.
- `getTemperature() -> float`: Returns the base sampling temperature.
- `getThreads() -> int`: Returns the number of threads for inference.
- `getCharsPerToken() -> int`: Returns the characters per token for input character calculation.
- `isWarnOnTrim() -> boolean`: Returns whether to warn on trim.
- `getMaxRetries() -> int`: Returns the maximum number of retry attempts.
- `getRetryTemperatureIncrement() -> float`: Returns the temperature increment for retries.
- `getTopP() -> float`: Returns the top-p value.
- `getTopK() -> int`: Returns the top-k value.
- `getRepeatPenalty() -> float`: Returns the repetition penalty.
- `isChatTemplateEnableThinking() -> boolean`: Returns whether thinking mode is enabled.
- `getStopStrings() -> List<String>`: Returns the list of stop strings.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws no exceptions.
- Handles null values by using `@Nullable` annotations and defensive copying where necessary.

#### Concurrency
- The class is not inherently thread-safe as it does not synchronize access to its fields. However, the immutability of the configuration parameters ensures that concurrent access within a single thread is safe.

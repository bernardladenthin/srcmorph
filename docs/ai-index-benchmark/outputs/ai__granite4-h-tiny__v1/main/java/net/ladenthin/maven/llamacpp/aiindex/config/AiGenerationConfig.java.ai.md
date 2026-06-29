### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:07:42Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for Maven-based AI model interactions

#### Purpose
- Mutable configuration object for AI generation steps
- Carries parameters between Maven configuration and AI provider implementations

#### Type
- Class: `AiGenerationConfig`
- Final: Yes
- Extends: None
- Implements: None
- Key generics and type bounds: None
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- Constructor and method parameters: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`
- Injected dependencies: None
- Consumed fields: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`
- Read resources: None

#### Output
- Return types: `String`, `int`, `float`, `List<String>`
- Produced state: None
- Mutated fields: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`
- Written resources: None
- Side effects: None

#### Core logic
- Default values for configuration parameters
- Getter and setter methods for each configuration parameter
- Automatic calculation of `maxInputChars` based on context size, output tokens, prompt overhead, and safety margin
- Unmodifiable view of stop strings

#### Public API
- `getModelPath() -> String`: Returns the GGUF model file path
- `setModelPath(String) -> void`: Sets the GGUF model file path
- `getContextSize() -> int`: Returns the context window size in tokens
- `setContextSize(int) -> void`: Sets the context window size in tokens
- `getMaxOutputTokens() -> int`: Returns the maximum number of output tokens per inference call
- `setMaxOutputTokens(int) -> void`: Sets the maximum number of output tokens per inference call
- `getTemperature() -> float`: Returns the sampling temperature
- `setTemperature(float) -> void`: Sets the sampling temperature
- `getThreads() -> int`: Returns the number of CPU threads used for inference
- `setThreads(int) -> void`: Sets the number of CPU threads used for inference
- `getCharsPerToken() -> int`: Returns the number of characters per token used in automatic `maxInputChars` calculation
- `setCharsPerToken(int) -> void`: Sets the number of characters per token
- `getMaxInputChars() -> int`: Returns the maximum number of input characters fed to the prompt
- `setMaxInputChars(int) -> void`: Sets the maximum number of input characters fed to the prompt
- `isWarnOnTrim() -> boolean`: Returns whether a warning is emitted when the prompt source text is trimmed
- `setWarnOnTrim(boolean) -> void`: Sets whether a warning is emitted when the prompt source text is trimmed
- `getMaxRetries() -> int`: Returns the maximum number of retry attempts on empty-body responses
- `setMaxRetries(int) -> void`: Sets the maximum number of retry attempts on empty-body responses
- `getRetryTemperatureIncrement() -> float`: Returns the temperature increment added on each retry attempt
- `setRetryTemperatureIncrement(float) -> void`: Sets the temperature increment added on each retry attempt
- `getTopP() -> float`: Returns the nucleus-sampling probability threshold
- `setTopP(float) -> void`: Sets the nucleus-sampling probability threshold
- `getTopK() -> int`: Returns the top-k sampling limit
- `setTopK(int) -> void`: Sets the top-k sampling limit
- `getRepeatPenalty() -> float`: Returns the repetition penalty
- `setRepeatPenalty(float) -> void`: Sets the repetition penalty
- `isChatTemplateEnableThinking() -> boolean`: Returns whether the model's chat-template thinking mode is enabled
- `setChatTemplateEnableThinking(boolean) -> void`: Sets whether the model's chat-template thinking mode is enabled
- `getStopStrings() -> @Nullable List<String>`: Returns an unmodifiable view of the configured stop strings
- `setStopStrings(@Nullable List<String>) -> void`: Sets the list of stop strings

#### Dependencies
- Imports: `java.util.ArrayList`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`
- Referenced types: `ArrayList`, `Collections`, `Lombok`, `Net.ladenthin.maven.llamacpp.aiindex.config`, `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable thrown/caught exceptions
- No notable null-handling or error conditions

#### Concurrency
- No threading, synchronization, immutability, or thread-safety notes

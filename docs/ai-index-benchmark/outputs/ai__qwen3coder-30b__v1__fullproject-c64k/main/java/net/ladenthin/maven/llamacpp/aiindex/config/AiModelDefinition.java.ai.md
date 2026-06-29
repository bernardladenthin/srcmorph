### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T15:56:43Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines reusable AI model configurations for Maven plugin field generation.

#### Purpose
- Stores AI model parameters for reuse across multiple generation tasks.
- Supports Maven plugin configuration via JavaBean setters.

#### Type
Class, final. Implements no interfaces. Generics: none. Annotations: @ToString, @SuppressWarnings.

#### Input
- Constructor: no parameters.
- Setters: key, modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings.
- Dependencies: AiGenerationConfig defaults.

#### Output
- Getters return stored values or unmodifiable collections.
- State changes via setter methods.

#### Core logic
- Holds configuration values for AI model inference.
- Provides default values from AiGenerationConfig.
- Supports immutable access to stopStrings list.
- Enables Maven plugin instantiation via reflection-based injection.

#### Public API
- getKey() -> String
- setKey(String)
- getModelPath() -> String
- setModelPath(String)
- getContextSize() -> int
- setContextSize(int)
- getMaxOutputTokens() -> int
- setMaxOutputTokens(int)
- getTemperature() -> float
- setTemperature(float)
- getThreads() -> int
- setThreads(int)
- getCharsPerToken() -> int
- setCharsPerToken(int)
- isWarnOnTrim() -> boolean
- setWarnOnTrim(boolean)
- getMaxRetries() -> int
- setMaxRetries(int)
- getRetryTemperatureIncrement() -> float
- setRetryTemperatureIncrement(float)
- getTopP() -> float
- setTopP(float)
- getTopK() -> int
- setTopK(int)
- getRepeatPenalty() -> float
- setRepeatPenalty(float)
- isChatTemplateEnableThinking() -> boolean
- setChatTemplateEnableThinking(boolean)
- getStopStrings() -> List<String>
- setStopStrings(Collection<String>)

#### Dependencies
- java.util.ArrayList, java.util.Collection, java.util.Collections, java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig

#### Exceptions / Errors
- Null handling: stopStrings may be null; returned list is unmodifiable.
- No explicit exceptions thrown.

#### Concurrency
- Not thread-safe due to mutable state.
- No synchronization mechanisms.

### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:09:53Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures parameters for AI model inference steps including context size, sampling settings, and retry logic.

#### Purpose
- Carries mutable configuration for AI generation steps between Maven plugin and AI providers.
- Holds defaults and calculation constants for input trimming and retry policies.

#### Type
Public class with Lombok @ToString; no implements or extends.

#### Input
- Constructor takes no parameters.
- Setters accept primitive types, strings, lists, and nullables.

#### Output
- Getters return primitives, strings, and unmodifiable lists.
- Mutates internal state via setters.

#### Core logic
- Holds configuration for GGUF model path, context size, output tokens, sampling parameters (temperature, top-p, top-k, repeat penalty).
- Manages retry behavior on empty responses with incremental temperature.
- Calculates max input characters based on context and token estimates.
- Controls prompt trimming warnings and stop strings.

#### Public API
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
- getMaxInputChars() -> int
- setMaxInputChars(int)
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
- setStopStrings(List<String>)

#### Dependencies
None beyond java.util.ArrayList, java.util.Collections, org.jspecify.annotations.Nullable.

#### Exceptions / Errors
- Uses @Nullable annotation on stopStrings list.
- No explicit throws; relies on Java null handling.

#### Concurrency
Not thread-safe due to mutable fields and lack of synchronization. Managed by identity in Maven plugin framework.

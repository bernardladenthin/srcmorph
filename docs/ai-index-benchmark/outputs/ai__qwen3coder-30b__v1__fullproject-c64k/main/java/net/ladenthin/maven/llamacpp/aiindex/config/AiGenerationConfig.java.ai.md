### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T15:53:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures parameters for AI model inference steps including context size, sampling settings, and retry logic.

#### Purpose
- Carries AI generation parameters between Maven configuration and provider implementations.
- Supports mutable JavaBean style configuration through setters.

#### Type
class public final
extends Object
implements

#### Input
- Constructor parameters: none
- Method parameters: all setter methods accept primitive or collection types
- Dependencies: Lombok for `@ToString`, JSpecify for `@Nullable`

#### Output
- Return types: primitives, String, List<String>
- State mutations: fields modified by setters
- Side effects: none explicitly

#### Core logic
- Holds mutable configuration state for AI inference
- Provides default values for all configurable parameters
- Manages automatic calculation of input character limits based on token counts
- Supports retry behavior with increasing temperature
- Exposes stop strings as an unmodifiable list

#### Public API
- getModelPath() -> String
- setModelPath(String) -> void
- getContextSize() -> int
- setContextSize(int) -> void
- getMaxOutputTokens() -> int
- setMaxOutputTokens(int) -> void
- getTemperature() -> float
- setTemperature(float) -> void
- getThreads() -> int
- setThreads(int) -> void
- getCharsPerToken() -> int
- setCharsPerToken(int) -> void
- getMaxInputChars() -> int
- setMaxInputChars(int) -> void
- isWarnOnTrim() -> boolean
- setWarnOnTrim(boolean) -> void
- getMaxRetries() -> int
- setMaxRetries(int) -> void
- getRetryTemperatureIncrement() -> float
- setRetryTemperatureIncrement(float) -> void
- getTopP() -> float
- setTopP(float) -> void
- getTopK() -> int
- setTopK(int) -> void
- getRepeatPenalty() -> float
- setRepeatPenalty(float) -> void
- isChatTemplateEnableThinking() -> boolean
- setChatTemplateEnableThinking(boolean) -> void
- getStopStrings() -> List<String>
- setStopStrings(List<String>) -> void

#### Dependencies
- java.util.ArrayList
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No explicit exceptions thrown
- Null handling via `@Nullable` annotation on `getStopStrings()` and `setStopStrings()`
- Default values prevent null field access

#### Concurrency
- Not thread-safe due to mutable state
- Intended for use in single-threaded Maven plugin context
- Identity-based equality used instead of generated equals/hashCode

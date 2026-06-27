### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:42:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures parameters for AI model inference steps including context size, sampling, retries, and input limits.

#### Purpose
- Carries AI generation settings between Maven plugin and provider implementations.
- Manages mutable configuration state for llama.cpp inference parameters.

#### Type
class public final
extends Object
implements

#### Input
- Constructor takes no parameters; initializes defaults.
- Setters accept primitive types, strings, lists, and nullables.
- Dependencies: Lombok for `@ToString`, `org.jspecify.annotations.Nullable`.

#### Output
- Getters return fields directly or unmodifiable views of collections.
- Side effects: mutation of instance state via setters.

#### Core logic
- Holds mutable configuration fields for AI inference parameters.
- Provides default constants for context size, output tokens, temperature, threads, etc.
- Manages automatic input character calculation based on token ratios.
- Supports retry logic with incremental temperature adjustments.
- Controls stop strings and chat template thinking mode.

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
- No explicit throws.
- Null handling via `@Nullable` annotation on `stopStrings`.
- Default values prevent null pointer access.

#### Concurrency
- Instance is mutable and not thread-safe.
- Intended for use in single-threaded Maven plugin context.
- Fields are accessed directly without synchronization.

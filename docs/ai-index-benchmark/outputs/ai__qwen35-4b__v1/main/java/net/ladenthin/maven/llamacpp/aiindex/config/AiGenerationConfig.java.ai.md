### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:08:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI generation parameters including model paths, sampling rates, and retry policies for Llama.cpp inference.

#### Purpose
*   Holds mutable configuration for AI model inference steps including context size, temperature, and token limits.
*   Provides JavaBean setters for Maven plugin injection and log diagnostics via Lombok.

#### Type
*   `public class AiGenerationConfig` with `@ToString` annotation.
*   Contains 25 default constant fields (`DEFAULT_*`) and 16 mutable instance fields.
*   Uses `List<String>` for stop strings without custom collection management.

#### Input
*   Constructor: None (uses defaults).
*   Setters: Accepts `String` (model path), `int` (context size, max output tokens, threads, chars per token, max input chars, retries, top k), `float` (temperature, retry increment, top p, repeat penalty), `boolean` (warn on trim, chat template thinking), and `List<String>` (stop strings).

#### Output
*   Returns: `String`, `int`, `float`, `boolean`, or `List<String>` via getter methods.
*   State: Mutates internal fields directly without validation or side effects.
*   Resources: None produced or consumed within this class scope.

#### Core logic
*   Initializes all instance fields with corresponding static default constants on construction.
*   Allows external configuration via setter methods for model path, sampling parameters, and retry policies.
*   Calculates maximum input characters dynamically if `charsPerToken` is non-zero using a safety margin formula.
*   Returns unmodifiable list views of stop strings to prevent runtime modification.

#### Public API
*   `getModelPath()` -> String (returns model file path)
*   `setModelPath(String)` -> void (sets GGUF model file path)
*   `getContextSize()` -> int (returns context window size in tokens)
*   `setContextSize(int)` -> void (sets context window size in tokens)
*   `getMaxOutputTokens()` -> int (returns max output tokens per call)
*   `setMaxOutputTokens(int)` -> void (sets max output tokens per call)
*   `getTemperature()` -> float (returns sampling temperature value)
*   `setTemperature(float)` -> void (sets sampling temperature value)
*   `getThreads()` -> int (returns number of CPU threads for inference)
*   `setThreads(int)` -> void (sets number of CPU threads for inference)
*   `getCharsPerToken()` -> int (returns chars per token ratio)
*   `setCharsPerToken(int)` -> void (sets chars per token ratio)
*   `getMaxInputChars()` -> int (returns max input characters limit)
*   `setMaxInputChars(int)` -> void (sets max input characters limit)
*   `isWarnOnTrim()` -> boolean (returns trim warning enabled status)
*   `setWarnOnTrim(boolean)` -> void (sets trim warning enabled status)
*   `getMaxRetries()` -> int (returns max retry attempts count)
*   `setMaxRetries(int)` -> void (sets max retry attempts count)
*   `getRetryTemperatureIncrement()` -> float (returns temp increment per retry)
*   `setRetryTemperatureIncrement(float)` -> void (sets temp increment per retry)
*   `getTopP()` -> float (returns top-p sampling threshold)
*   `setTopP(float)` -> void (sets top-p sampling threshold)
*   `getTopK()` -> int (returns top-k sampling limit)
*   `setTopK(int)` -> void (sets top-k sampling limit)
*   `getRepeatPenalty()` -> float (returns repetition penalty value)
*   `setRepeatPenalty(float)` -> void (sets repetition penalty value)
*   `isChatTemplateEnableThinking()` -> boolean (returns thinking mode enabled status)
*   `setChatTemplateEnableThinking(boolean)` -> void (sets chat-template thinking mode)
*   `getStopStrings()` -> List<String> (returns unmodifiable stop strings list)
*   `setStopStrings(List<String>)` -> void (sets list of stop strings)

#### Dependencies
*   `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`

#### Exceptions / Errors
*   No explicit exceptions thrown by methods.
*   Handles null input in `setStopStrings` by resetting to empty list.
*   Allows null model path return via getter if not set.

#### Concurrency
*   Not thread-safe; fields are mutable and accessed via setters.
*   Designed for single-instance use managed by Maven plugin framework.
*   No synchronization or immutability guarantees provided.

### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:46:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Mutable configuration carrier for AI generation parameters including model paths, sampling settings, retry policies, and input limits between Maven plugin and AI provider.

#### Purpose
- Holds all configurable parameters for a single AI generation step (model path, sampling, retries, input limits).
- Provides Lombok-generated `toString()` for build logs while remaining mutable via setters managed by the Maven plugin.

#### Type
- public class; extends Object; implements nothing; annotated with @ToString; fields initialized to DEFAULT_* constants.

#### Input
- Constructor arguments: none (no-arg constructor uses defaults).
- Setter parameters: modelPath (String), contextSize (int), maxOutputTokens (int), temperature (float), threads (int), charsPerToken (int), maxInputChars (int), warnOnTrim (boolean), maxRetries (int), retryTemperatureIncrement (float), topP (float), topK (int), repeatPenalty (float), chatTemplateEnableThinking (boolean), stopStrings (@Nullable List<String>).

#### Output
- Getter returns: modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxInputChars, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings (unmodifiable view).
- State mutations via setters; no resource writes or side effects.

#### Core logic
- Defaults for all numeric and boolean fields are initialized to static final constants upon instantiation.
- `getStopStrings()` safely returns an unmodifiable list or null based on `stopStrings` state.
- `setStopStrings()` normalizes null input to an empty ArrayList before assignment.
- No algorithmic computation; pure data storage and retrieval.

#### Public API
- getModelPath() -> String: GGUF model file path.
- setModelPath(String) -> void: sets GGUF model file path.
- getContextSize() -> int: context window size in tokens.
- setContextSize(int) -> void: sets context window size in tokens.
- getMaxOutputTokens() -> int: max output tokens per inference call.
- setMaxOutputTokens(int) -> void: sets max output tokens per inference call.
- getTemperature() -> float: sampling temperature (lower = deterministic).
- setTemperature(float) -> void: sets sampling temperature.
- getThreads() -> int: CPU threads for llama.cpp inference.
- setThreads(int) -> void: sets CPU threads for inference.
- getCharsPerToken() -> int: chars per token ratio; 0 disables auto calc.
- setCharsPerToken(int) -> void: sets chars per token (0 disables auto calc).
- getMaxInputChars() -> int: max input characters fed to prompt.
- setMaxInputChars(int) -> void: sets max input characters fed to prompt.
- isWarnOnTrim() -> boolean: whether trim warnings are emitted.
- setWarnOnTrim(boolean) -> void: enables/disables trim warnings.
- getMaxRetries() -> int: max retry attempts on empty-body responses.
- setMaxRetries(int) -> void: sets max retry attempts on empty-body responses.
- getRetryTemperatureIncrement() -> float: temp increment per retry attempt.
- setRetryTemperatureIncrement(float) -> void: sets temp increment per retry attempt.
- getTopP() -> float: nucleus-sampling probability threshold.
- setTopP(float) -> void: sets nucleus-sampling probability threshold.
- getTopK() -> int: top-k sampling limit.
- setTopK(int) -> void: sets top-k sampling limit.
- getRepeatPenalty() -> float: repetition penalty factor.
- setRepeatPenalty(float) -> void: sets repetition penalty factor.
- isChatTemplateEnableThinking() -> boolean: whether chat-template thinking mode is enabled.
- setChatTemplateEnableThinking(boolean) -> void: enables/disables chat-template thinking mode.
- getStopStrings() -> List<String>: unmodifiable list of stop strings.
- setStopStrings(List<String>) -> void: sets list of stop strings (null clears).

#### Dependencies
- java.util.ArrayList
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- None; no checked exceptions or null-handling logic beyond standard field assignment.

#### Concurrency
- Mutable state; not thread-safe; managed by Maven plugin framework via setters.

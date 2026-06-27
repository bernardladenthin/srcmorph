### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:50:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures LLM model parameters for a Maven plugin to define reusable AI generation settings.

#### Purpose
- Defines a mutable JavaBean POJO to hold a unique key and all configurable parameters for a specific LLM model in the plugin system.
- Serves as a single source of truth that can be reused across multiple field-generation entries and plugin goals.

#### Type
Class; extends `Object`; implements no interfaces; annotated with `@ToString` and `@Nullable` for fields; mutable via reflection-safe setters.

#### Input
Constructor parameters: None (defaults applied).
Fields: `key`, `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `warnOnTrim`, `maxRetries`, `retryTemperatureIncrement`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`.

#### Output
Returns: String (key, modelPath), int/float (contextSize, maxOutputTokens, temperature, threads, charsPerToken, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking), boolean (warnOnTrim, chatTemplateEnableThinking), List<String> (stopStrings).
State: Modifies internal fields via setters; no external resources written or consumed.

#### Core logic
- Initializes instance variables with default values from `AiGenerationConfig` if not explicitly set by the user.
- Stores unique identifiers (`key`) and file paths (`modelPath`) for model resolution.
- Manages inference tuning parameters including context window, output limits, sampling strategies (temperature, top-p, top-k), and repetition penalties.
- Configures runtime behavior regarding source code trimming warnings, retry logic for empty responses, and chat-template thinking mode.
- Handles stop strings by converting collections to unmodifiable lists upon retrieval to prevent external modification.

#### Public API
`getKey() -> String`: Retrieves the unique lookup identifier.
`setKey(String) -> void`: Assigns a new unique identifier.
`getModelPath() -> String`: Retrieves the path to the GGUF model file.
`setModelPath(String) -> void`: Sets the path to the model file.
`getContextSize() -> int`: Retrieves the context window size in tokens.
`setContextSize(int) -> void`: Sets the context window size.
`getMaxOutputTokens() -> int`: Retrieves max output tokens per inference call.
`setMaxOutputTokens(int) -> void`: Sets max output tokens limit.
`getTemperature() -> float`: Retrieves base sampling temperature.
`setTemperature(float) -> void`: Sets base sampling temperature.
`getThreads() -> int`: Retrieves CPU thread count for inference.
`setThreads(int) -> void`: Sets number of threads for llama.cpp inference.
`getCharsPerToken() -> int`: Retrieves characters per token ratio for auto-calculation.
`setCharsPerToken(int) -> void`: Sets chars per token ratio (0 disables auto-calc).
`isWarnOnTrim() -> boolean`: Checks if warnings are emitted on source trimming.
`setWarnOnTrim(boolean) -> void`: Enables or disables trim warnings.
`getMaxRetries() -> int`: Retrieves max retry attempts for empty bodies.
`setMaxRetries(int) -> void`: Sets max retries (0 disables).
`getRetryTemperatureIncrement() -> float`: Gets temperature increment per retry.
`setRetryTemperatureIncrement(float) -> void`: Sets retry temperature increment.
`getTopP() -> float`: Retrieves nucleus-sampling probability threshold.
`setTopP(float) -> void`: Sets top-p sampling threshold.
`getTopK() -> int`: Retrieves top-k sampling limit.
`setTopK(int) -> void`: Sets top-k sampling limit.
`getRepeatPenalty() -> float`: Retrieves repetition penalty factor.
`setRepeatPenalty(float) -> void`: Sets repetition penalty.
`isChatTemplateEnableThinking() -> boolean`: Checks if chat-template thinking mode is enabled.
`setChatTemplateEnableThinking(boolean) -> void`: Toggles chat-template thinking mode.
`getStopStrings() -> List<String>`: Retrieves immutable list of generation stop strings.
`setStopStrings(Collection<String>) -> void`: Sets collection of stop strings.

#### Dependencies
`AiGenerationConfig`, `AiModelDefinitionSupport`, `net.ladenthin.llama.parameters.ModelParameters`.

#### Exceptions / Errors
- Returns `null` for optional fields (`key`, `modelPath`, `stopStrings`) if not set.
- No checked exceptions thrown; unchecked exceptions handled internally or propagated by caller.
- Null checks performed on `stopStrings` before conversion to unmodifiable list.

#### Concurrency
- Mutable state managed via setters; safe for single-threaded configuration injection by Maven plugin framework.
- `getStopStrings()` returns an unmodifiable view to prevent concurrent modification from external code.

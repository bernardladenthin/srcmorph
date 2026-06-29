### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:40:19Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configuration POJO for AI model parameters, supporting Maven plugin integration and reusable configurations.

#### Purpose
- Define AI model parameters
- Support Maven plugin configuration
- Enable reuse of model configurations

#### Type
class public final @ToString

#### Input
- Constructor: none
- Setters: all fields (key, modelPath, contextSize, etc.)

#### Output
- Getters: all fields (key, modelPath, contextSize, etc.)
- Returns: various types (String, int, float, boolean, List<String>)

#### Core logic
- Store and retrieve AI model parameters
- Handle default values from AiGenerationConfig
- Manage collection of stop strings
- Provide immutable access to stop strings list

#### Public API
- getKey() -> String: Retrieve unique identifier
- setKey(String) -> void: Set unique identifier
- getModelPath() -> String: Get model file path
- setModelPath(String) -> void: Set model file path
- getContextSize() -> int: Get context window size
- setContextSize(int) -> void: Set context window size
- getMaxOutputTokens() -> int: Get max output tokens
- setMaxOutputTokens(int) -> void: Set max output tokens
- getTemperature() -> float: Get sampling temperature
- setTemperature(float) -> void: Set sampling temperature
- getThreads() -> int: Get thread count
- setThreads(int) -> void: Set thread count
- getCharsPerToken() -> int: Get chars per token ratio
- setCharsPerToken(int) -> void: Set chars per token ratio
- isWarnOnTrim() -> boolean: Check trim warning setting
- setWarnOnTrim(boolean) -> void: Set trim warning setting
- getMaxRetries() -> int: Get max retry attempts
- setMaxRetries(int) -> void: Set max retry attempts
- getRetryTemperatureIncrement() -> float: Get retry temperature increment
- setRetryTemperatureIncrement(float) -> void: Set retry temperature increment
- getTopP() -> float: Get nucleus sampling threshold
- setTopP(float) -> void: Set nucleus sampling threshold
- getTopK() -> int: Get top-k sampling limit
- setTopK(int) -> void: Set top-k sampling limit
- getRepeatPenalty() -> float: Get repetition penalty
- setRepeatPenalty(float) -> void: Set repetition penalty
- isChatTemplateEnableThinking() -> boolean: Check chat template thinking mode
- setChatTemplateEnableThinking(boolean) -> void: Set chat template thinking mode
- getStopStrings() -> List<String>: Get stop strings
- setStopStrings(Collection<String>) -> void: Set stop strings

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- NullPointerException: Possible from getStopStrings() if not initialized
- UnsupportedOperationException: From unmodifiableList in getStopStrings()

#### Concurrency
- Not thread-safe due to mutable fields
- Collections.unmodifiableList provides some level of protection for stop strings

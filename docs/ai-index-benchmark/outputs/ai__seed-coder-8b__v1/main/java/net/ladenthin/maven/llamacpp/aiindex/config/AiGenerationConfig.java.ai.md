### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:06:01Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for Maven build integration

#### Purpose
- Stores configuration for AI generation steps
- Facilitates communication between Maven config and AI providers

#### Type
- Class
- Public fields
- Lombok-generated toString()

#### Input
- Constructor: no input
- Setter methods for all configurable parameters

#### Output
- Getter methods for all parameters
- Mutable state through setter methods

#### Core logic
- Manages default values for AI generation settings
- Provides getter/setter pairs for each configuration option
- Handles stop strings list with null safety

#### Public API
- getModelPath() -> String: Retrieves model file path
- setModelPath(String) -> void: Sets model file path
- getContextSize() -> int: Gets context window size
- setContextSize(int) -> void: Sets context window size
- getMaxOutputTokens() -> int: Retrieves max output tokens
- setMaxOutputTokens(int) -> void: Sets max output tokens
- getTemperature() -> float: Gets sampling temperature
- setTemperature(float) -> void: Sets sampling temperature
- getThreads() -> int: Gets CPU threads count
- setThreads(int) -> void: Sets CPU threads count
- getCharsPerToken() -> int: Gets chars per token ratio
- setCharsPerToken(int) -> void: Sets chars per token ratio
- getMaxInputChars() -> int: Gets max input characters
- setMaxInputChars(int) -> void: Sets max input characters
- isWarnOnTrim() -> boolean: Checks trim warning setting
- setWarnOnTrim(boolean) -> void: Sets trim warning setting
- getMaxRetries() -> int: Gets max retry attempts
- setMaxRetries(int) -> void: Sets max retry attempts
- getRetryTemperatureIncrement() -> float: Gets retry temp increment
- setRetryTemperatureIncrement(float) -> void: Sets retry temp increment
- getTopP() -> float: Gets nucleus sampling threshold
- setTopP(float) -> void: Sets nucleus sampling threshold
- getTopK() -> int: Gets top-k sampling limit
- setTopK(int) -> void: Sets top-k sampling limit
- getRepeatPenalty() -> float: Gets repeat penalty
- setRepeatPenalty(float) -> void: Sets repeat penalty
- isChatTemplateEnableThinking() -> boolean: Checks chat template thinking mode
- setChatTemplateEnableThinking(boolean) -> void: Sets chat template thinking mode
- getStopStrings() -> List<String>: Retrieves stop strings list
- setStopStrings(List<String>) -> void: Sets stop strings list

#### Dependencies
- java.util.ArrayList
- java.util.Collections
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Null safety for stop strings list
- No explicit exception handling in methods

#### Concurrency
- Mutable state allows for concurrent modifications
- Thread-safety depends on external usage

### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:10:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI model parameters for Maven plugin integration

#### Purpose
- Defines AI model configurations for Maven plugin use
- Allows reuse of model definitions across multiple field generations

#### Type
- Class
- Public
- Lombok @ToString annotation

#### Input
- Constructor: No input
- Setters: Various AI parameter values and paths

#### Output
- Getters: Return configured AI parameters and paths
- Mutable state: Field values can be modified after construction

#### Core logic
- Stores AI model configuration settings
- Provides getter/setter methods for each configurable parameter
- Manages default values from AiGenerationConfig
- Handles nullable stop strings collection

#### Public API
- getKey() -> String: Retrieves unique identifier
- setKey(String) -> void: Sets unique identifier
- getModelPath() -> String: Gets model file path
- setModelPath(String) -> void: Sets model file path
- getContextSize() -> int: Retrieves context window size
- setContextSize(int) -> void: Sets context window size
- getMaxOutputTokens() -> int: Gets max output tokens
- setMaxOutputTokens(int) -> void: Sets max output tokens
- getTemperature() -> float: Retrieves temperature setting
- setTemperature(float) -> void: Sets temperature
- getThreads() -> int: Gets thread count
- setThreads(int) -> void: Sets thread count
- getCharsPerToken() -> int: Retrieves chars-per-token ratio
- setCharsPerToken(int) -> void: Sets chars-per-token ratio
- isWarnOnTrim() -> boolean: Checks trim warning setting
- setWarnOnTrim(boolean) -> void: Sets trim warning
- getMaxRetries() -> int: Gets max retry attempts
- setMaxRetries(int) -> void: Sets max retry attempts
- getRetryTemperatureIncrement() -> float: Retrieves retry temp increment
- setRetryTemperatureIncrement(float) -> void: Sets retry temp increment
- getTopP() -> float: Gets nucleus sampling threshold
- setTopP(float) -> void: Sets nucleus sampling threshold
- getTopK() -> int: Gets top-k sampling limit
- setTopK(int) -> void: Sets top-k sampling limit
- getRepeatPenalty() -> float: Retrieves repeat penalty
- setRepeatPenalty(float) -> void: Sets repeat penalty
- isChatTemplateEnableThinking() -> boolean: Checks chat template thinking mode
- setChatTemplateEnableThinking(boolean) -> void: Sets chat template thinking mode
- getStopStrings() -> List<String>: Gets stop strings
- setStopStrings(Collection<String>) -> void: Sets stop strings

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No explicit exception handling
- Null values allowed for optional fields (stopStrings)

#### Concurrency
- Mutable class, not thread-safe
- Caller must ensure thread-safety when accessing shared instances

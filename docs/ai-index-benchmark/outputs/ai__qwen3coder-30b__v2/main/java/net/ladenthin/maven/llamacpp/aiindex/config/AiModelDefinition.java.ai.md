### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:12:44Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines reusable AI model configurations for Maven plugin field generation.

#### Purpose
- Encapsulates AI model parameters for reuse across multiple generation tasks.
- Serves as a configuration POJO for Maven plugin settings.

#### Type
Public class with Lombok @ToString; implements no interfaces; key generics: List<String>; notable annotations: @Nullable, @ToString.

#### Input
Constructor takes no parameters; setters accept all fields including String, int, float, boolean, and Collection<String>.

#### Output
Getters return field values; setters mutate internal state; stopStrings returns unmodifiable list view.

#### Core logic
- Holds immutable model configuration data.
- Provides mutable JavaBean accessors for Maven plugin instantiation via reflection.
- Defaults numeric fields to values from AiGenerationConfig class.

#### Public API
- getKey() -> String: retrieves lookup key
- setKey(String): sets lookup key
- getModelPath() -> String: retrieves model file path
- setModelPath(String): sets model file path
- getContextSize() -> int: retrieves context window size
- setContextSize(int): sets context window size
- getMaxOutputTokens() -> int: retrieves max output tokens
- setMaxOutputTokens(int): sets max output tokens
- getTemperature() -> float: retrieves sampling temperature
- setTemperature(float): sets sampling temperature
- getThreads() -> int: retrieves thread count
- setThreads(int): sets thread count
- getCharsPerToken() -> int: retrieves chars-per-token ratio
- setCharsPerToken(int): sets chars-per-token ratio
- isWarnOnTrim() -> boolean: retrieves trim warning flag
- setWarnOnTrim(boolean): sets trim warning flag
- getMaxRetries() -> int: retrieves max retry attempts
- setMaxRetries(int): sets max retry attempts
- getRetryTemperatureIncrement() -> float: retrieves retry temperature increment
- setRetryTemperatureIncrement(float): sets retry temperature increment
- getTopP() -> float: retrieves top-p sampling threshold
- setTopP(float): sets top-p sampling threshold
- getTopK() -> int: retrieves top-k sampling limit
- setTopK(int): sets top-k sampling limit
- getRepeatPenalty() -> float: retrieves repetition penalty
- setRepeatPenalty(float): sets repetition penalty
- isChatTemplateEnableThinking() -> boolean: retrieves chat template thinking flag
- setChatTemplateEnableThinking(boolean): sets chat template thinking flag
- getStopStrings() -> List<String>: retrieves stop strings list
- setStopStrings(Collection<String>): sets stop strings collection

#### Dependencies
AiGenerationConfig, Collection, List, ArrayList, Collections, org.jspecify.annotations.Nullable

#### Exceptions / Errors
Null handling for stopStrings; no explicit exception throwing.

#### Concurrency
No concurrency considerations noted; class is designed for Maven plugin use with reflection-based instantiation.

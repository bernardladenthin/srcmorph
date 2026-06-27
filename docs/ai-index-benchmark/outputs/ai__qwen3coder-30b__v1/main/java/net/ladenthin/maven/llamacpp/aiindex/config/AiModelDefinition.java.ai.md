### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:45:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines reusable AI model configurations for Maven plugin field generation.

#### Purpose
- Stores AI model parameters for reuse across multiple field-generation tasks.
- Maps configuration entries to lookup keys for reference by other plugin components.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation. Fields are private with public getters/setters.

#### Input
- Constructor accepts no arguments; defaults initialized from AiGenerationConfig.
- Setters accept parameters to configure model-specific properties including key, modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, and stopStrings.

#### Output
- Getters return internal state values or unmodifiable views of collections.
- Setters mutate internal fields; stopStrings is copied to new ArrayList before assignment.

#### Core logic
- Provides a JavaBean configuration object for Maven plugin AI model settings.
- Supports default value inheritance from AiGenerationConfig constants.
- Enables lookup by key from AiFieldGenerationConfig.
- Manages mutable state through standard getter/setter accessors.

#### Public API
- getKey() -> String
- setKey(String) -> void
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
- setStopStrings(Collection<String>) -> void

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig
- net.ladenthin.llama.parameters.ModelParameters

#### Exceptions / Errors
- Null handling for stopStrings; null-checks and defensive copying applied.
- No explicit exception throwing.

#### Concurrency
- Not thread-safe due to mutable state.
- Designed for single-threaded use during Maven plugin configuration phase.

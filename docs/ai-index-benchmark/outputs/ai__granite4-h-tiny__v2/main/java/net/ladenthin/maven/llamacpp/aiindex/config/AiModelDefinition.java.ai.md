### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:32:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Maven plugin configuration POJO that pairs a lookup key with a complete set of AI model parameters.

#### Purpose
- Represents AI model configuration for Maven plugin.
- Allows reuse across multiple field-generation entries and goals.

#### Type
- Class, `public class AiModelDefinition`, extends `java.lang.Object`, implements `java.io.Serializable`.

#### Input
- Constructor parameters: none.
- Dependencies: none.

#### Output
- Returns: getters for configuration parameters.
- Produces: mutable state changes via setters.

#### Core Logic
- Initializes with default values from `AiGenerationConfig`.
- Provides getters/setters for configuration parameters.
- Manages stop strings list.

#### Public API
- `getKey() -> String`: Returns the unique lookup key.
- `setKey(String) -> void`: Sets the unique lookup key.
- `getModelPath() -> String`: Returns the model path.
- `setModelPath(String) -> void`: Sets the model path.
- `getContextSize() -> int`: Returns context size.
- `setContextSize(int) -> void`: Sets context size.
- `getMaxOutputTokens() -> int`: Returns max output tokens.
- `setMaxOutputTokens(int) -> void`: Sets max output tokens.
- `getTemperature() -> float`: Returns temperature.
- `setTemperature(float) -> void`: Sets temperature.
- `getThreads() -> int`: Returns thread count.
- `setThreads(int) -> void`: Sets thread count.
- `getCharsPerToken() -> int`: Returns chars-per-token ratio.
- `setCharsPerToken(int) -> void`: Sets chars-per-token ratio.
- `isWarnOnTrim() -> boolean`: Returns warning flag.
- `setWarnOnTrim(boolean) -> void`: Sets warning flag.
- `getMaxRetries() -> int`: Returns max retries.
- `setMaxRetries(int) -> void`: Sets max retries.
- `getRetryTemperatureIncrement() -> float`: Returns retry temperature increment.
- `setRetryTemperatureIncrement(float) -> void`: Sets retry temperature increment.
- `getTopP() -> float`: Returns top-p value.
- `setTopP(float) -> void`: Sets top-p value.
- `getTopK() -> int`: Returns top-k value.
- `setTopK(int) -> void`: Sets top-k value.
- `getRepeatPenalty() -> float`: Returns repeat penalty.
- `setRepeatPenalty(float) -> void`: Sets repeat penalty.
- `isChatTemplateEnableThinking() -> boolean`: Returns chat template thinking flag.
- `setChatTemplateEnableThinking(boolean) -> void`: Sets chat template thinking flag.
- `getStopStrings() -> @Nullable List<String>`: Returns stop strings.
- `setStopStrings(Collection<String>) -> void`: Sets stop strings.

#### Dependencies
- `AiGenerationConfig`: referenced for default values.

#### Exceptions / Errors
- None notable.

#### Concurrency
- Not explicitly noted, but all setters allow mutable state changes.

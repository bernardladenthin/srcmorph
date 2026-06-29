### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:36:21Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configuration object for AI generation parameters between Maven and provider implementations

#### Purpose
- Mutable configuration for AI generation steps
- Carries all parameters for a single AI generation step

#### Type
class public @ToString

#### Input
- Constructor: no input
- Setters: various primitive and String parameters

#### Output
- Getters: return various primitive types, Strings, or Lists
- Produced state: mutable fields

#### Core logic
- Stores AI generation parameters
- Provides getter/setter methods for each parameter
- Manages a list of stop strings

#### Public API
- `getModelPath() -> String`: Returns model file path
- `setModelPath(String) -> void`: Sets model file path
- `getContextSize() -> int`: Returns context size
- `setMaxOutputTokens(int) -> void`: Sets max output tokens
- `getTemperature() -> float`: Returns temperature
- `setTemperature(float) -> void`: Sets temperature
- `getThreads() -> int`: Returns thread count
- `setThreads(int) -> void`: Sets thread count
- `getCharsPerToken() -> int`: Returns chars per token
- `setCharsPerToken(int) -> void`: Sets chars per token
- `getMaxInputChars() -> int`: Returns max input chars
- `setMaxInputChars(int) -> void`: Sets max input chars
- `isWarnOnTrim() -> boolean`: Checks trim warning setting
- `setWarnOnTrim(boolean) -> void`: Sets trim warning setting
- `getMaxRetries() -> int`: Returns max retries
- `setMaxRetries(int) -> void`: Sets max retries
- `getRetryTemperatureIncrement() -> float`: Returns retry temp increment
- `setRetryTemperatureIncrement(float) -> void`: Sets retry temp increment
- `getTopP() -> float`: Returns top-p value
- `setTopP(float) -> void`: Sets top-p value
- `getTopK() -> int`: Returns top-k value
- `setTopK(int) -> void`: Sets top-k value
- `getRepeatPenalty() -> float`: Returns repeat penalty
- `setRepeatPenalty(float) -> void`: Sets repeat penalty
- `isChatTemplateEnableThinking() -> boolean`: Checks chat template thinking mode
- `setChatTemplateEnableThinking(boolean) -> void`: Sets chat template thinking mode
- `getStopStrings() -> List<String>`: Returns stop strings list
- `setStopStrings(List<String>) -> void`: Sets stop strings list

#### Dependencies
- java.util.ArrayList
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

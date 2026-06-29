### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:09:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Provides a configuration POJO for pairing a lookup key with AI model parameters.

#### Type
- Class, final, no extends/implements, uses lombok.ToString, lombok.NonNull, org.jspecify.annotations.Nullable.

#### Input
- Constructor and method parameters: key, modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings.

#### Output
- Returns: key, modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings.
- Mutates: key, modelPath, contextSize, maxOutputTokens, temperature, threads, charsPerToken, warnOnTrim, maxRetries, retryTemperatureIncrement, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings.

#### Core logic
- Provides getters and setters for all configuration parameters.
- Manages default values from `AiGenerationConfig`.
- Handles nullable stop strings.

#### Public API
- `getKey() -> String`: Returns the lookup key.
- `setKey(String) -> void`: Sets the lookup key.
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
- Imports: java.util.ArrayList, java.util.Collection, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig.

#### Exceptions / Errors
- No notable exceptions or errors.

#### Concurrency
- No concurrency considerations noted.

### AiGenerationConfig.java
- H: 1.0
- C: 59512635
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:57:38Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Mutable configuration carrying AI generation parameters between Maven config and AI provider implementations.  

#### Purpose  
- Stores AI model path, sampling, retry, and prompt limits.  
- Exposes getters/setters for Maven plugin configuration.  

#### Type  
`class AiGenerationConfig; public; Lombok @ToString; no equals/hashCode; mutable JavaBean.`  

#### Input  
- `new AiGenerationConfig()` (no args).  
- `setModelPath(String)`, `setContextSize(int)`, `setMaxOutputTokens(int)`, `setTemperature(float)`, `setThreads(int)`, `setCharsPerToken(int)`, `setMaxInputChars(int)`, `setWarnOnTrim(boolean)`, `setMaxRetries(int)`, `setRetryTemperatureIncrement(float)`, `setTopP(float)`, `setTopK(int)`, `setRepeatPenalty(float)`, `setChatTemplateEnableThinking(boolean)`, `setStopStrings(List<String>)`.  

#### Output  
- `getModelPath() -> String`.  
- `getContextSize() -> int`.  
- `getMaxOutputTokens() -> int`.  
- `getTemperature() -> float`.  
- `getThreads() -> int`.  
- `getCharsPerToken() -> int`.  
- `getMaxInputChars() -> int`.  
- `isWarnOnTrim() -> boolean`.  
- `getMaxRetries() -> int`.  
- `getRetryTemperatureIncrement() -> float`.  
- `getTopP() -> float`.  
- `getTopK() -> int`.  
- `getRepeatPenalty() -> float`.  
- `isChatTemplateEnableThinking() -> boolean`.  
- `getStopStrings() -> @Nullable List<String> (unmodifiable)`.  

#### Core logic  
- Declares default constants for context size, output tokens, temperature, threads, input char limits, etc.  
- Initializes fields with default values.  
- Provides mutable JavaBean setters that update fields.  
- `getStopStrings()` returns `Collections.unmodifiableList` of internal list.  
- `setStopStrings(null)` resets to an empty `ArrayList`.  
- `@ToString` generates a diagnostic string over all fields.  

#### Public API  
- `getModelPath() -> String returns GGUF model path`.  
- `setModelPath(String) -> void assigns model file path`.  
- `getContextSize() -> int returns token window size`.  
- `setContextSize(int) -> void sets context size`.  
- `getMaxOutputTokens() -> int returns max output tokens`.  
- `setMaxOutputTokens(int) -> void sets max output tokens`.  
- `getTemperature() -> float returns sampling temperature`.  
- `setTemperature(float) -> void sets sampling temperature`.  
- `getThreads() -> int returns CPU thread count`.  
- `setThreads(int) -> void sets thread count`.  
- `getCharsPerToken() -> int returns chars‑per‑token ratio`.  
- `setCharsPerToken(int) -> void sets ratio`.  
- `getMaxInputChars() -> int returns max input chars`.  
- `setMaxInputChars(int) ->

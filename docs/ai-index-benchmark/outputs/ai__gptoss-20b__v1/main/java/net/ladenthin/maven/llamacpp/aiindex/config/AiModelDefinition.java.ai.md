### AiModelDefinition.java
- H: 1.0
- C: F0FA9D43
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:12:08Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a Maven‑plugin configuration bean for AI model parameters such as model path, context size, temperature, and stop strings, allowing reuse across field‑generation goals.  

#### Purpose
- Holds AI model configuration for Maven plugin.  
- Enables definition sharing via lookup key.  

#### Type
- Public class, mutable JavaBean, annotated @ToString, @SuppressWarnings.  

#### Input
- Constructor: no-arg, default values.  
- Setters accept:  
  - key (String)  
  - modelPath (String)  
  - contextSize (int)  
  - maxOutputTokens (int)  
  - temperature (float)  
  - threads (int)  
  - charsPerToken (int)  
  - warnOnTrim (boolean)  
  - maxRetries (int)  
  - retryTemperatureIncrement (float)  
  - topP (float)  
  - topK (int)  
  - repeatPenalty (float)  
  - chatTemplateEnableThinking (boolean)  
  - stopStrings (Collection\<String>)  

#### Output
- Getters return stored values; getStopStrings returns unmodifiable view or null.  
- Mutators assign provided values; setStopStrings clones collection.  

#### Core logic
- Numeric fields initialized with defaults from AiGenerationConfig.  
- No validation or error handling.  
- List handling: defensive copy on set, unmodifiable view on get.  

#### Public API
- `getKey() -> String` – retrieve lookup key.  
- `setKey(String) -> void` – assign lookup key.  
- `getModelPath() -> String`

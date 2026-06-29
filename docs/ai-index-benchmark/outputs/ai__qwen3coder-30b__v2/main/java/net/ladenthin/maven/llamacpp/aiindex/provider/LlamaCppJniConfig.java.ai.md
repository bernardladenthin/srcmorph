### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:19:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures native llama.cpp inference via JNI with immutable settings for model, context, sampling, and threading.

#### Purpose
- Encapsulates configuration for llama.cpp JNI provider.
- Provides immutable access to inference parameters.

#### Type
Final class implementing value semantics via Lombok annotations; marked for future record conversion.

#### Input
Constructor accepts: libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings; modelPath is required.

#### Output
Accessors return: libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings; stopStrings returned as unmodifiable list.

#### Core logic
- Validates non-null modelPath.
- Stores all parameters as final fields.
- Normalizes stopStrings to empty list if null.
- Generates equals/hashCode/toString via Lombok.

#### Public API
libraryPath() -> String  
modelPath() -> String  
contextSize() -> int  
maxOutputTokens() -> int  
temperature() -> float  
threads() -> int  
topP() -> float  
topK() -> int  
repeatPenalty() -> float  
chatTemplateEnableThinking() -> boolean  
stopStrings() -> List<String>

#### Dependencies
ConvertToRecord, Collections, Objects

#### Exceptions / Errors
Throws NullPointerException if modelPath is null.

#### Concurrency
Immutable; thread-safe.

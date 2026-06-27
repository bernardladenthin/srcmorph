### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:49:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configuration for llama.cpp JNI provider, managing native library paths and AI model parameters.

#### Purpose
- Immutable configuration for llama.cpp JNI provider
- Manages native library paths and AI model parameters

#### Type
class final; implements ConvertToRecord; Lombok annotations: @ToString, @EqualsAndHashCode

#### Input
- Constructor parameters: libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings
- List<String> for stopStrings (may be null)

#### Output
- Getter methods returning primitive types or String
- List<String> for stopStrings (unmodifiable)

#### Core logic
- Validates modelPath in constructor
- Initializes immutable fields with provided values
- Provides access to configuration parameters via getter methods

#### Public API
- libraryPath() -> String: Returns native library path or null
- modelPath() -> String: Returns model file path
- contextSize() -> int: Returns context window size
- maxOutputTokens() -> int: Returns maximum output tokens
- temperature() -> float: Returns sampling temperature
- threads() -> int: Returns number of CPU threads
- topP() -> float: Returns nucleus-sampling probability threshold
- topK() -> int: Returns top-k sampling limit
- repeatPenalty() -> float: Returns repetition penalty
- chatTemplateEnableThinking() -> boolean: Returns whether chat-template thinking mode is enabled
- stopStrings() -> List<String>: Returns unmodifiable list of stop strings

#### Dependencies
- ConvertToRecord
- Collections
- Objects
- List
- String

#### Exceptions / Errors
- NullPointerException if modelPath is null in constructor

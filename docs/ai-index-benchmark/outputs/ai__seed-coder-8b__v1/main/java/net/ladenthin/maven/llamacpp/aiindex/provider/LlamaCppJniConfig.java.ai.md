### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:19:37Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides configuration for llama.cpp JNI provider with immutable fields and accessor methods.

#### Purpose
- Configures llama.cpp JNI settings
- Immutable value type for safe concurrent use

#### Type
- `final class`
- Annotated with Lombok annotations
- Implements `ConvertToRecord` interface

#### Input
- Constructor parameters: library path, model path, context size, etc.
- List of stop strings (may be null)

#### Output
- Accessor methods returning configuration values
- Immutable list of stop strings

#### Core logic
- Validates non-null model path in constructor
- Initializes all fields, setting stopStrings to empty list if null
- Provides read-only access to all configuration properties

#### Public API
- `libraryPath() -> String` - Returns native library path or null
- `modelPath() -> String` - Returns model file path
- `contextSize() -> int` - Returns context window size
- `maxOutputTokens() -> int` - Returns max output tokens
- `temperature() -> float` - Returns sampling temperature
- `threads() -> int` - Returns number of CPU threads
- `topP() -> float` - Returns nucleus-sampling threshold
- `topK() -> int` - Returns top-k sampling limit
- `repeatPenalty() -> float` - Returns repetition penalty
- `chatTemplateEnableThinking() -> boolean` - Returns chat-template mode status
- `stopStrings() -> List<String>` - Returns unmodifiable list of stop strings

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- Lombok annotations
- `ConvertToRecord` interface

#### Exceptions / Errors
- Throws `NullPointerException` if modelPath is null in constructor

#### Concurrency
- Immutable, thread-safe class

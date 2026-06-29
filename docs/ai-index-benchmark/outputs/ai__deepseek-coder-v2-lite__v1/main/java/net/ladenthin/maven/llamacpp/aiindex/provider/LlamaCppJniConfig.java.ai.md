### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:18:22Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Define and encapsulate configuration parameters for the llama.cpp JNI provider.
- Provide a record-shaped value type for future Java 17+ migration.

#### Type
- Class (`final`): `LlamaCppJniConfig`
- Lombok annotations: `@ToString`, `@EqualsAndHashCode`
- Interface implemented: `ConvertToRecord` (annotation)

#### Input
- Constructor parameters:
  - `libraryPath`: String, may be null
  - `modelPath`: String, must not be null
  - `contextSize`: int
  - `maxOutputTokens`: int
  - `temperature`: float
  - `threads`: int
  - `topP`: float
  - `topK`: int
  - `repeatPenalty`: float
  - `chatTemplateEnableThinking`: boolean
  - `stopStrings`: List<String>, may be null (treated as empty list)

#### Output
- Public methods:
  - `libraryPath() -> String`
  - `modelPath() -> String`
  - `contextSize() -> int`
  - `maxOutputTokens() -> int`
  - `temperature() -> float`
  - `threads() -> int`
  - `topP() -> float`
  - `topK() -> int`
  - `repeatPenalty() -> float`
  - `chatTemplateEnableThinking() -> boolean`
  - `stopStrings() -> List<String>`

#### Core logic
- **Initialization**: Ensures `modelPath` is not null.
- **Accessors**: Provide read-only access to all configuration fields.
- **Immutability**: All fields are final, ensuring the object cannot be modified once created.

#### Public API
- `libraryPath() -> String`: Returns the native library path.
- `modelPath() -> String`: Returns the GGUF model file path.
- `contextSize() -> int`: Returns the context window size in tokens.
- `maxOutputTokens() -> int`: Returns the maximum number of output tokens per call.
- `temperature() -> float`: Returns the sampling temperature.
- `threads() -> int`: Returns the number of CPU threads.
- `topP() -> float`: Returns the nucleus-sampling probability threshold.
- `topK() -> int`: Returns the top-k sampling limit.
- `repeatPenalty() -> float`: Returns the repetition penalty.
- `chatTemplateEnableThinking() -> boolean`: Returns whether chat-template thinking mode is enabled.
- `stopStrings() -> List<String>`: Returns an unmodifiable view of the configured stop strings.

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- Lombok annotations (`@ToString`, `@EqualsAndHashCode`)
- Interface `ConvertToRecord` (annotation)

#### Exceptions / Errors
- Throws a `NullPointerException` if `modelPath` is null during initialization.
- Handles null `stopStrings` by treating them as empty lists.

#### Concurrency
- The class is immutable, making it inherently thread-safe and not requiring synchronization.

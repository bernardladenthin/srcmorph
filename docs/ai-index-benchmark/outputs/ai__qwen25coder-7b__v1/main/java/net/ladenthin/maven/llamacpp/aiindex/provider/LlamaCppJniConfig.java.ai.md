### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:43:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Immutable configuration for the llama.cpp JNI provider.
- Used to store and access various parameters for the provider's operation.

#### Type
- Class
- Final
- `@ConvertToRecord`
- `@ToString`
- `@EqualsAndHashCode`
- Extends: `java.lang.Object`

#### Input
- Constructor parameters:
  - `libraryPath`: native library path; may be `null`
  - `modelPath`: path to the GGUF model file (required)
  - `contextSize`: context window size in tokens
  - `maxOutputTokens`: maximum number of output tokens per call
  - `temperature`: sampling temperature
  - `threads`: number of CPU threads
  - `topP`: nucleus-sampling probability threshold
  - `topK`: top-k sampling limit
  - `repeatPenalty`: repetition penalty
  - `chatTemplateEnableThinking`: whether chat-template thinking mode is enabled
  - `stopStrings`: stop strings; may be `null` (treated as empty)

#### Output
- Return types:
  - `String`: native library path, or `null` to use the bundled library
  - `String`: model file path
  - `int`: context window size
  - `int`: maximum output tokens
  - `float`: sampling temperature
  - `int`: number of CPU threads
  - `float`: nucleus-sampling probability threshold
  - `int`: top-k value
  - `float`: repeat penalty
  - `boolean`: whether chat-template thinking mode is enabled
  - `List<String>`: unmodifiable list of stop strings

#### Core logic
- Immutable configuration class with eleven fields.
- Constructor validates the `modelPath` and initializes all fields.
- Accessor methods return the values of the fields.

#### Public API
- `LlamaCppJniConfig(String libraryPath, String modelPath, int contextSize, int maxOutputTokens, float temperature, int threads, float topP, int topK, float repeatPenalty, boolean chatTemplateEnableThinking, List<String> stopStrings) -> void` : Creates a new configuration instance.
- `String libraryPath() -> String` : Returns the native library path.
- `String modelPath() -> String` : Returns the GGUF model file path.
- `int contextSize() -> int` : Returns the context window size in tokens.
- `int maxOutputTokens() -> int` : Returns the maximum number of output tokens per call.
- `float temperature() -> float` : Returns the sampling temperature.
- `int threads() -> int` : Returns the number of CPU threads.
- `float topP() -> float` : Returns the nucleus-sampling probability threshold.
- `int topK() -> int` : Returns the top-k sampling limit.
- `float repeatPenalty() -> float` : Returns the repetition penalty.
- `boolean chatTemplateEnableThinking() -> boolean` : Returns whether chat-template thinking mode is enabled.
- `List<String> stopStrings() -> List<String>` : Returns an unmodifiable view of the configured stop strings.

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- `NullPointerException` when `modelPath` is `null`.

#### Concurrency
- Immutable, thread-safe.

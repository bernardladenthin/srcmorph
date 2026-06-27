### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:49:22Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Provides immutable configuration settings for the llama.cpp JNI provider.
- Uses a record-shaped value type marked with `@ConvertToRecord` for future Java 17+ migration.

#### Type
```java
public final class LlamaCppJniConfig
```

#### Input
- `libraryPath`: native library path; may be `null`.
- `modelPath`: path to the GGUF model file.
- `contextSize`: context window size in tokens.
- `maxOutputTokens`: maximum number of output tokens per call.
- `temperature`: sampling temperature.
- `threads`: number of CPU threads.
- `topP`: nucleus-sampling probability threshold.
- `topK`: top-k sampling limit.
- `repeatPenalty`: repetition penalty.
- `chatTemplateEnableThinking`: whether chat-template thinking mode is enabled.
- `stopStrings`: stop strings; may be `null` (treated as empty).

#### Output
- Returns:
  - `libraryPath`: native library path, or `null` to use the bundled library.
  - `modelPath`: model file path.
  - `contextSize`: context window size.
  - `maxOutputTokens`: maximum output tokens.
  - `temperature`: sampling temperature.
  - `threads`: number of CPU threads.
  - `topP`: top-p value.
  - `topK`: top-k value.
  - `repeatPenalty`: repeat penalty.
  - `chatTemplateEnableThinking`: whether chat-template thinking mode is enabled.
  - `stopStrings`: unmodifiable list of stop strings.

#### Core logic
1. Ensures `modelPath` is not `null`.
2. Initializes all fields with provided values, treating `null` stop strings as an empty list.

#### Public API
- `libraryPath() -> String`
  - Returns the native library path.
- `modelPath() -> String`
  - Returns the GGUF model file path.
- `contextSize() -> int`
  - Returns the context window size in tokens.
- `maxOutputTokens() -> int`
  - Returns the maximum number of output tokens per call.
- `temperature() -> float`
  - Returns the sampling temperature.
- `threads() -> int`
  - Returns the number of CPU threads.
- `topP() -> float`
  - Returns the nucleus-sampling probability threshold.
- `topK() -> int`
  - Returns the top-k sampling limit.
- `repeatPenalty() -> float`
  - Returns the repetition penalty.
- `chatTemplateEnableThinking() -> boolean`
  - Returns whether chat-template thinking mode is enabled.
- `stopStrings() -> List<String>`
  - Returns an unmodifiable view of the configured stop strings.

#### Dependencies
- `java.util.Objects`
- `java.util.List`
- `java.util.Collections`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- Throws `NullPointerException` if `modelPath` is `null`.

#### Concurrency
- The class is immutable, hence thread-safe.

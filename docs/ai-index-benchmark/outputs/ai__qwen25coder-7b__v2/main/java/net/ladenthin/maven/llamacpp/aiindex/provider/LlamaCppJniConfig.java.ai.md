### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:37:19Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Immutable data class to hold configuration parameters for the llama.cpp JNI provider.
- Provides methods to access individual configuration properties.

#### Type
```java
public final class LlamaCppJniConfig implements java.io.Serializable {
    // ...
}
```

#### Input
- Constructor parameters:
  - `libraryPath`: native library path; may be `null`
  - `modelPath`: path to the GGUF model file
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
  - `libraryPath()`: native library path, or `null` to use the bundled library
  - `modelPath()`: model file path
  - `contextSize()`: context window size
  - `maxOutputTokens()`: maximum output tokens
  - `temperature()`: sampling temperature
  - `threads()`: number of CPU threads
  - `topP()`: nucleus-sampling probability threshold
  - `topK()`: top-k sampling limit
  - `repeatPenalty()`: repetition penalty
  - `chatTemplateEnableThinking()`: whether chat-template thinking mode is enabled
  - `stopStrings()`: unmodifiable list of stop strings

#### Core logic
- Immutable configuration class with all fields marked as `final`.
- Constructor validates the `modelPath` parameter.
- Provides getter methods for each configuration property.

#### Public API
- `LlamaCppJniConfig(String libraryPath, String modelPath, int contextSize, int maxOutputTokens, float temperature, int threads, float topP, int topK, float repeatPenalty, boolean chatTemplateEnableThinking, List<String> stopStrings) -> Constructs a new configuration instance.`
- JavaBean getters/setters for: `libraryPath, modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, repeatPenalty, chatTemplateEnableThinking, stopStrings`

#### Dependencies
- `java.util.List`
- `java.util.Objects`
- `java.util.Collections`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
- `NullPointerException` if `modelPath` is `null`.

#### Concurrency
- Thread-safe due to immutability.

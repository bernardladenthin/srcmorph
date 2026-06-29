### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:21:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures native llama.cpp inference parameters for Java AI indexing providers.

#### Purpose
*   Immutable configuration holder for llama.cpp JNI provider settings.
*   Defines model paths, sampling parameters, and thread counts for LLM execution.

#### Type
Class: `final` with Lombok-generated `equals`, `hashCode`, `toString`.
Fields: 11 private final fields (`libraryPath`, `modelPath`, numeric configs).
Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
Constructor parameters: `String libraryPath`, `String modelPath` (required), `int contextSize`, `int maxOutputTokens`, `float temperature`, `int threads`, `float topP`, `int topK`, `float repeatPenalty`, `boolean chatTemplateEnableThinking`, `List<String> stopStrings`.

#### Output
Return types: 11 getter methods returning respective config values (`String`, `int`, `float`, `boolean`, `List<String>`).
Side effects: Validates `modelPath` non-null; converts null `stopStrings` to empty list.

#### Core logic
*   Validates `modelPath` is not null during construction.
*   Initializes immutable fields with provided configuration values.
*   Converts null `stopStrings` to an unmodifiable empty list for safety.
*   Generates value-based equality and hash code via Lombok over all fields.

#### Public API
`libraryPath() -> String` (native lib path or null)
`modelPath() -> String` (GGUF model file path)
`contextSize() -> int` (context window token count)
`maxOutputTokens() -> int` (max output token limit)
`temperature() -> float` (sampling temperature factor)
`threads() -> int` (CPU thread count)
`topP() -> float` (nucleus sampling threshold)
`topK() -> int` (top-k sampling limit)
`repeatPenalty() -> float` (repetition penalty weight)
`chatTemplateEnableThinking() -> boolean` (thinking mode flag)
`stopStrings() -> List<String>` (unmodifiable stop tokens list)

#### Dependencies
`java.util.Collections`, `java.util.List`, `java.util.Objects`, `lombok.EqualsAndHashCode`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`.

#### Exceptions / Errors
Throws `NullPointerException` if `modelPath` is null.
Handles null `stopStrings` gracefully by converting to empty list.

#### Concurrency
Thread-safe due to immutability; no synchronization required.

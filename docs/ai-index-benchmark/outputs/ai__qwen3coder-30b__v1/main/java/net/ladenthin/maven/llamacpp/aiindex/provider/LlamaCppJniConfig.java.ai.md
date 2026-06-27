### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:52:15Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures native llama.cpp inference parameters for AI model execution.

#### Purpose
- Encapsulates immutable settings for JNI-based llama.cpp model invocation.
- Provides a structured interface for configuring model behavior and resource limits.

#### Type
Final class implementing value semantics via Lombok annotations. Implements `equals`, `hashCode`, and `toString`. Fields are private and final; accessors follow record-style naming.

#### Input
Constructor accepts 11 parameters including `libraryPath`, `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, and `stopStrings`. `modelPath` is required; `stopStrings` may be null, treated as empty list.

#### Output
Accessors return field values directly. `stopStrings()` returns an immutable view of the internal list. No mutation or side effects occur during access.

#### Core logic
- Validates that `modelPath` is not null.
- Assigns constructor parameters to private fields.
- Converts null `stopStrings` to empty immutable list.
- Exposes all configuration values through getter-style methods.

#### Public API
- `libraryPath() -> String` retrieves native library path.
- `modelPath() -> String` retrieves GGUF model file path.
- `contextSize() -> int` retrieves context window size.
- `maxOutputTokens() -> int` retrieves maximum output tokens.
- `temperature() -> float` retrieves sampling temperature.
- `threads() -> int` retrieves CPU thread count.
- `topP() -> float` retrieves nucleus-sampling threshold.
- `topK() -> int` retrieves top-k sampling limit.
- `repeatPenalty() -> float` retrieves repetition penalty.
- `chatTemplateEnableThinking() -> boolean` retrieves thinking mode flag.
- `stopStrings() -> List<String>` retrieves immutable stop strings.

#### Dependencies
java.util.Collections, java.util.List, java.util.Objects, lombok.EqualsAndHashCode, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord

#### Exceptions / Errors
Throws `NullPointerException` if `modelPath` is null. Null `stopStrings` results in empty list, not exception.

#### Concurrency
Immutable design ensures thread safety. No synchronization required. All fields are final and accessed via immutable views.

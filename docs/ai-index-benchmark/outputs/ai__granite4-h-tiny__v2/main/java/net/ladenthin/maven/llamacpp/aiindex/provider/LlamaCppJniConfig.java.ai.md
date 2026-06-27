### LlamaCppJniConfig.java
- H: 1.0
- C: 0B74C5FA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:35:40Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider.

#### Purpose
- Immutable configuration class for llama.cpp JNI provider.
- Record-shaped value type with generated equals, hashCode, and toString.

#### Type
- Class `LlamaCppJniConfig` (final) extends `Object`; implements `java.lang.AutoCloseable` (not present in source).

#### Input
- Constructor parameters: `libraryPath`, `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `topP`, `topK`, `repeatPenalty`, `chatTemplateEnableThinking`, `stopStrings`.

#### Output
- Returns: `libraryPath()`, `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `stopStrings()`.

#### Core logic
- Ensures `modelPath` is not null.
- Initializes fields with provided values; `stopStrings` defaults to empty list if null.

#### Public API
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

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`

#### Exceptions / Errors
- None notable.

#### Concurrency
- Immutable configuration; thread-safe.

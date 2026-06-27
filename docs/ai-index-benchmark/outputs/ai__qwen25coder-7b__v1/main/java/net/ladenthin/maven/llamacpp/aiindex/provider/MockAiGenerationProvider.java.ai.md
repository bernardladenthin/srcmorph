### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:46:40Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a mock implementation of the `AiGenerationProvider` interface for testing purposes.

#### Purpose
- Returns a mock summary for an AI generation request, used for testing.

#### Type
- Class
- Public
- Implements `AiGenerationProvider`
- Annotated with `@ToString`

#### Input
- `AiGenerationRequest request` (constructor parameter)
- Consumed field: `sourceFile()` from `AiGenerationRequest`

#### Output
- Returns a string: "Mock summary for [filename]"

#### Core logic
- Extracts the file name from the `sourceFile()` in the `AiGenerationRequest`
- Returns a mock summary string using the file name

#### Public API
- `MockAiGenerationProvider() -> void` - Creates a new `MockAiGenerationProvider`.
- `generate(AiGenerationRequest request) -> String` - Generates a mock summary for the provided request.

#### Dependencies
- `java.io.IOException`
- `java.nio.file.Path`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if an I/O error occurs while accessing the file name

#### Concurrency
- No concurrency concerns; the implementation is stateless and thread-safe

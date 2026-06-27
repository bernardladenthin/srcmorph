### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:36:16Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> A deterministic AI generation provider that returns a mock summary for testing.

#### Purpose
- Provides a deterministic mock summary for testing AI generation requests.

#### Type
- Class: `MockAiGenerationProvider` implements `AiGenerationProvider`.

#### Input
- `AiGenerationRequest request`: Input request containing source file details.

#### Output
- `String`: Returns a mock summary string for the provided request.

#### Core Logic
- Retrieves the file name from the request.
- Constructs and returns a mock summary string prefixed with "Mock summary for " followed by the file name.

#### Public API
- `MockAiGenerationProvider()`: No-op constructor.
- `String generate(final AiGenerationRequest request) throws IOException`: Generates a mock summary for the given request.

#### Dependencies
- `java.io.IOException`: Exception thrown if an I/O error occurs.
- `lombok.ToString`: Annotation for generating toString method.
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`: Interface for AI generation requests.

#### Exceptions / Errors
- `IOException`: Thrown if an I/O error occurs during request processing.

#### Concurrency
- Not explicitly noted in the source.

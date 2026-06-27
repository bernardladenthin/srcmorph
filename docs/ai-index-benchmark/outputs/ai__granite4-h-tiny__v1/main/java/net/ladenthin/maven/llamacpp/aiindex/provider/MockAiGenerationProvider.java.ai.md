### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:13:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates deterministic mock summaries for AI generation requests in testing.

#### Purpose
- Provides deterministic mock summaries for AI generation requests.

#### Type
- Class
- Final
- Implements `AiGenerationProvider`

#### Input
- `AiGenerationRequest request`: Source file path and name.

#### Output
- Returns a mock summary string for the provided request.

#### Core Logic
- Retrieves file name from `request`.
- Constructs and returns a mock summary string.

#### Public API
- `MockAiGenerationProvider()`: No-op constructor.
- `String generate(final AiGenerationRequest request) throws IOException`: Generates a mock summary.

#### Dependencies
- `java.io.IOException`
- `java.nio.file.Path`
- `lombok.ToString`

#### Exceptions / Errors
- Throws `IOException` if file name retrieval fails.

#### Concurrency
- Not explicitly noted, but the class is thread-safe due to its stateless nature.

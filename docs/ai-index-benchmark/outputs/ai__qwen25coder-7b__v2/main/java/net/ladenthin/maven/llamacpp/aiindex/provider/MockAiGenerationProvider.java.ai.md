### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:40:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a mock implementation of `AiGenerationProvider` for testing purposes.

#### Purpose
- To provide a deterministic implementation of `AiGenerationProvider` that returns a mock summary for testing.

#### Type
```java
public class MockAiGenerationProvider implements AiGenerationProvider
```

#### Core logic
- Creates a new `MockAiGenerationProvider`.
- Generates a mock summary for the given `AiGenerationRequest`.

#### Public API
- `generate(AiGenerationRequest request) -> String` : Generates a mock summary for the given request.

#### Dependencies
- `AiGenerationRequest`
- `Path`

#### Exceptions / Errors
- Throws `IOException` if an I/O error occurs.

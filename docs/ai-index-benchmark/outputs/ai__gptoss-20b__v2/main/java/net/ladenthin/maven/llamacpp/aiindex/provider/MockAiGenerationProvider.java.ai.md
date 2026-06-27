### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:18:24Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Deterministic provider returning mock summaries for AiGenerationRequests during testing.

#### Purpose
- Deterministic mock for `AiGenerationProvider` used in unit tests.

#### Type
- public class `MockAiGenerationProvider` implements `AiGenerationProvider`; @ToString.

#### Input
- `AiGenerationRequest request` – retrieves source file via `request.sourceFile()`.

#### Output
- `String` "Mock summary for " + fileName; may throw `IOException`.

#### Core logic
- Retrieve `Path file = request.sourceFile()`.
- Extract file name with `file.getFileName()`.
- Fallback to full path string if `getFileName()` is null.
- Return `"Mock summary for " + fileName`.

#### Public API
- `generate(AiGenerationRequest) -> String` deterministic mock summary.

#### Dependencies
- `AiGenerationRequest`, `AiGenerationProvider`, `java.io.IOException`, `java.nio.file.Path`, `lombok.ToString`.

#### Exceptions / Errors
- Declares `throws IOException` (no runtime checks performed).

#### Concurrency
- None.

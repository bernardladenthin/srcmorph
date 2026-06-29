### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:00:03Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> A deterministic mock provider generating static summaries for testing AI generation logic.

#### Purpose
- Provides a no-op implementation of AiGenerationProvider for unit testing.
- Returns a predictable string based on the input file name instead of real processing.

#### Type
Class; implements AiGenerationProvider; annotated with @ToString; public constructor.

#### Input
AiGenerationRequest request containing sourceFile path; derived file name from Path object.

#### Output
String summary formatted as "Mock summary for <fileName>".

#### Core logic
- Extracts fileName from request.sourceFile().
- Handles null file name fallback by converting full path to string.
- Concatenates static text with fileName to return mock result.

#### Public API
generate(request) -> String: Creates a deterministic mock summary string.

#### Dependencies
AiGenerationProvider, AiGenerationRequest, java.io.IOException, java.nio.file.Path.

#### Exceptions / Errors
Throws IOException; no explicit null checks beyond basic Path utility usage.

#### Concurrency
Synchronous method with no concurrency concerns.

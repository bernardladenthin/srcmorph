### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:20:01Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides deterministic mock AI generation results for testing purposes.

#### Purpose
- Supplies fake AI summaries to enable testing of downstream logic without actual AI inference.
- Facilitates unit testing of components that depend on AI-generated content.

#### Type
- Class public implements AiGenerationProvider; @ToString

#### Input
- `AiGenerationRequest` consumed via `request.sourceFile()`

#### Output
- Returns deterministic string summary based on input file name

#### Core logic
- Extracts file name from input request
- Constructs mock summary string using extracted file name

#### Public API
- `generate(request) -> String` produces mock AI summary for testing

#### Dependencies
- AiGenerationRequest
- Path

#### Exceptions / Errors
- Throws IOException (though implementation does not)

#### Concurrency
- Not applicable; stateless and immutable behavior

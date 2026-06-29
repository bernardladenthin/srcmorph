### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:24:52Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides deterministic mock summaries for AI generation testing.

#### Purpose
- Implements a testable AI generation provider that returns fixed, predictable outputs.
- Used to simulate AI response behavior during unit and integration tests.

#### Type
class MockAiGenerationProvider implements AiGenerationProvider

#### Input
- AiGenerationRequest request (sourceFile())

#### Output
- String (mock summary of the source file)

#### Core logic
- Extracts source file name from request.
- Constructs a deterministic mock summary using the file name.
- Returns the generated summary string.

#### Public API
generate(AiGenerationRequest request) -> String: Generates mock summary from input file name

#### Dependencies
AiGenerationRequest

#### Exceptions / Errors
- Throws IOException on generation failure (per contract)

#### Concurrency
- Not applicable (stateless, single-threaded operation)

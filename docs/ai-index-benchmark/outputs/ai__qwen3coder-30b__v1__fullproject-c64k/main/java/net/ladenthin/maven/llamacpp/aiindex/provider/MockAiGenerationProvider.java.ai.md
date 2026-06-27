### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:04:57Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides deterministic mock AI summaries for testing purposes.

#### Purpose
- Supplies fake AI-generated content for unit testing.
- Enables isolated testing of components dependent on AI generation.

#### Type
Class, final. Implements AiGenerationProvider. Uses Lombok @ToString annotation.

#### Input
- Constructor takes no parameters.
- Method generate consumes AiGenerationRequest with sourceFile field.
- Reads file path from request for summary construction.

#### Output
- Returns fixed string "Mock summary for {filename}".
- Produces no side effects or state changes.

#### Core logic
- Extracts filename from input file path.
- Constructs deterministic mock response based on filename.
- Handles null filename case by falling back to full path.

#### Public API
generate(request) -> String: Creates mock AI summary from file.

#### Dependencies
AiGenerationProvider, AiGenerationRequest, Path, IOException, lombok.ToString

#### Exceptions / Errors
Throws IOException when reading file path fails (though not actually doing I/O).

#### Concurrency
No concurrency concerns; stateless and immutable.

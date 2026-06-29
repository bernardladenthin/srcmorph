### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:53:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides deterministic mock AI summaries for testing purposes.

#### Purpose
- Supplies fake AI-generated content for unit tests.
- Enables isolated testing of components dependent on AI generation.

#### Type
- Class, final
- Implements AiGenerationProvider
- Uses Lombok @ToString annotation

#### Input
- Constructor takes no parameters
- Method generate() consumes AiGenerationRequest with sourceFile Path

#### Output
- Returns String summary prefixed with "Mock summary for "
- Side effect: none

#### Core logic
- Extracts filename from request's source file path
- Constructs deterministic mock response using filename

#### Public API
- generate(request) → String Generates mock AI summary for given request

#### Dependencies
- java.io.IOException
- java.nio.file.Path
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider

#### Exceptions / Errors
- Throws IOException from generate method (not handled internally)

#### Concurrency
- No concurrency considerations; stateless implementation

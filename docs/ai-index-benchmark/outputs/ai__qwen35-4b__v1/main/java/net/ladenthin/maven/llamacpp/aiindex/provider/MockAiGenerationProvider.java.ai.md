### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:23:03Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a deterministic mock implementation of AI generation summaries for testing purposes without external dependencies.

#### Purpose
- Replaces real AI generation with static mock summaries during unit testing.
- Simulates the `AiGenerationProvider` interface for deterministic validation.

#### Type
Class implementing `AiGenerationProvider`, annotated with `@ToString`.

#### Input
- `AiGenerationRequest` containing a `Path` source file representing the document to process.

#### Output
- Returns a `String` formatted as "Mock summary for [filename]".

#### Core logic
- Extracts filename from `sourceFile()` parameter of the request.
- Fallbacks to full path string if filename extraction fails or returns null.
- Concatenates static prefix with filename to generate mock response.

#### Public API
- `generate(AiGenerationRequest) -> String` + generates mock summary for requested file.

#### Dependencies
- `AiGenerationProvider` interface.
- `AiGenerationRequest` request container.
- `java.nio.file.Path` for file path handling.

#### Exceptions / Errors
- Throws `IOException` on method execution (mock implementation ignores actual I/O errors).
- Handles null filename gracefully by falling back to full path string representation.

#### Concurrency
- Thread-safe via immutability; no shared mutable state or synchronization required.

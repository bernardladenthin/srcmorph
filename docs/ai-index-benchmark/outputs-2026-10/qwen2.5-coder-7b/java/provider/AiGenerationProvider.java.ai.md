### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:09Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for generating text using AI, supporting both local and mock implementations.

#### Purpose
- Defines a contract for AI backend providers that can generate text based on an `AiGenerationRequest`.

#### Type
- Interface
- Extends `AutoCloseable`

#### Core logic
- `generate(AiGenerationRequest request)`: Generates text for the given request.
- `generateWithTimings(AiGenerationRequest request)`: Generates text and returns it along with timing information.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text for the given request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: Generates text and returns it along with timing information.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails.

#### Concurrency
- No explicit concurrency handling or synchronization notes.

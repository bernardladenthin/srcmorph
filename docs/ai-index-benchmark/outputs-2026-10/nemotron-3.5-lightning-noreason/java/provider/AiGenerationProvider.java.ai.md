### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:16Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that produces text for an AiGenerationRequest.

#### Purpose
- Interface for pluggable AI backends that generate text for AiGenerationRequest.

#### Type
- Interface; extends AutoCloseable.
- Key method: `generate(AiGenerationRequest) -> String`.

#### Input
- `AiGenerationRequest request`: generation request containing prompt key, source file, source text, and current header.

#### Output
- `String`: generated text; never null, but may be blank.
- `AiGenerationTimings`: generated text plus timing (default rates 0).

#### Core logic
- Provides `generate` method for text generation using default sampling parameters.
- Provides `generateWithTimings` default implementation that delegates to `generate` and reports no timings (rates 0).
- Provides `close` default implementation for resource cleanup.

#### Public API
- `generate(AiGenerationRequest request) -> String`
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`
- `close() -> void`

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException`: thrown if the underlying provider fails.

#### Concurrency
- No threading or synchronization notes.

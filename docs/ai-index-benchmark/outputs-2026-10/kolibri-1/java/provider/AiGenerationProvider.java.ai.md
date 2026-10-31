### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:23:01Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable text generation for source-document AI requests.

#### Purpose
- Defines the contract for AI text generation backends in the srcmorph project.
- Supports local models and test mocks.

#### Type
- Interface `AiGenerationProvider` in package `net.ladenthin.srcmorph.provider`.
- Implements `AutoCloseable`.
- Key type `AiGenerationRequest` from `net.ladenthin.srcmorph.document`.
- Key return type `AiGenerationTimings`.

#### Input
- `AiGenerationRequest request` parameter for both generation methods.
- Request contains prompt key, source file, source text, and current header.

#### Output
- `String generate(...)` returns generated text; never null, may be blank.
- `AiGenerationTimings generateWithTimings(...)` returns text plus timing metrics.
- `close()` produces no output; releases provider resources.

#### Core logic
- `generate(request)` is the core method; implementations produce text for the request.
- `generateWithTimings(request)` delegates to `generate(request)` by default.
- Default timings report zero rates and zero durations when unavailable.
- `close()` is a no-op default implementation.

#### Public API
- `generate(AiGenerationRequest request) -> String` generates text using default sampling parameters.
- `generateWithTimings(final AiGenerationRequest request) -> AiGenerationTimings` generates text with measured timings.
- `close() -> void` closes the provider; default implementation does nothing.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by both generation methods if the underlying provider fails.
- `null` return value prohibited for `generate`; blank string allowed when no tokens produced.

#### Concurrency
- No concurrency controls specified in the interface.

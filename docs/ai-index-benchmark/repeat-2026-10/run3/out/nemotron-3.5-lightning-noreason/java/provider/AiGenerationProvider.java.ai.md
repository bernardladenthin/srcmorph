### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:37:03Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates text generation for AI requests with pluggable backends and timing support.

#### Purpose
Interface for pluggable AI backends that generate text for `AiGenerationRequest` objects, supporting both real providers (e.g., llama.cpp) and mock/test implementations.

#### Type
Interface; implements AutoCloseable; key method: `generate(AiGenerationRequest) -> String`; default method: `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings`

#### Input
- `AiGenerationRequest request` — prompt key, source file, source text, and current header

#### Output
- `String` from `generate` — generated text, never null but may be blank
- `AiGenerationTimings` from `generateWithTimings` — generated text plus timing metrics (rates 0 when unavailable)

#### Core logic
- `generate(AiGenerationRequest)` produces text using provider's default sampling parameters
- `generateWithTimings` delegates to `generate` and reports no timings (rates 0) unless overridden
- `close()` is a no-op in the default implementation

#### Public API
- `String generate(AiGenerationRequest request) throws IOException`
- `AiGenerationTimings generateWithTimings(AiGenerationRequest request) throws IOException`
- `void close() throws IOException` (default)

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings` if the underlying provider fails
- `close` may throw `IOException`

#### Concurrency
No threading or synchronization concerns; intended as a pluggable interface with default no-op close.

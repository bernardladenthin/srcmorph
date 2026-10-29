### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:41Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates text generation for AI requests with pluggable backends and timing support.

#### Purpose
A pluggable AI backend interface that produces text for generation requests, supporting both real providers (e.g., llama.cpp) and mock implementations for testing.

#### Type
Interface; extends AutoCloseable; key method: `String generate(AiGenerationRequest request) throws IOException`.

#### Input
- `AiGenerationRequest request` — prompt key, source file, source text, and current header.

#### Output
- `String` — generated text; never null, but may be blank.
- `AiGenerationTimings` — generated text plus timing data from `generateWithTimings`.

#### Core logic
- Core method `generate(AiGenerationRequest request)` produces text using the provider's default sampling parameters.
- Default `generateWithTimings` delegates to `generate` and reports zero timings; providers override to expose measured rates and timing data.

#### Public API
- `String generate(AiGenerationRequest request) throws IOException` — generates text for the given request.
- `AiGenerationTimings generateWithTimings(AiGenerationRequest request) throws IOException` — generates text with measured timing.
- `void close() throws IOException` — closes the provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings` if the underlying provider fails.
- `close` may also throw `IOException`.

#### Concurrency
No threading or synchronization concerns; pure interface contract.

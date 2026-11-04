### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:53Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides an interface for AI text generation backends, exposing generation and optional timing metrics.

#### Purpose
- Declares contract for AI text generation providers.
- Supplies default timing wrapper for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `String generate(AiGenerationRequest) throws IOException`, `AiGenerationTimings generateWithTimings(AiGenerationRequest) throws IOException`, `void close() throws IOException`.
  - Default implementation of `generateWithTimings` returns `AiGenerationTimings` with zeroed metrics.
  - Default implementation of `close` does nothing.

#### Input
- `AiGenerationRequest request` parameter to `generate` and `generateWithTimings`.
- `AiGenerationRequest` contains prompt key, source file, source text, current header.

#### Output
- `String` from `generate`; never `null`, may be blank.
- `AiGenerationTimings` from `generateWithTimings`; contains generated text and timing metrics (rates default to 0).
- `close` has no return.

#### Core logic
- `generate(request)` – abstract; provider implements text generation.
- `generateWithTimings(request)` – default: calls `generate(request)` and constructs `AiGenerationTimings(generate(request), 0, 0, 0.0d, 0, 0.0d)`.
- `close()` – default: empty body.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timings.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.
- No other exceptions.

#### Concurrency
- No explicit concurrency constructs; interface is stateless.

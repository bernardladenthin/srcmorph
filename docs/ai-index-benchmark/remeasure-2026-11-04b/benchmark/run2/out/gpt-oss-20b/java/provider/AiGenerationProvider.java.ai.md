### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:32Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides a pluggable AI backend for generating text from an `AiGenerationRequest`.

#### Purpose
- Supplies AI text generation for source‑code morphing.
- Supports default and timed generation.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `generate(AiGenerationRequest)`, `generateWithTimings(AiGenerationRequest)`, `close()`.
  - Default `generateWithTimings` returns `AiGenerationTimings` with zero rates.

#### Input
- `generate(AiGenerationRequest request)` receives:
  - `AiGenerationRequest` containing prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` receives same request.

#### Output
- `generate` returns `String` (never `null`, may be blank).
- `generateWithTimings` returns `AiGenerationTimings` (generated text + timing data).
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings`:
  1. Calls `generate(request)`.
  2. Wraps result in `AiGenerationTimings` with all timing fields set to zero.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timings.
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.

#### Concurrency
- No explicit concurrency handling; interface is stateless.

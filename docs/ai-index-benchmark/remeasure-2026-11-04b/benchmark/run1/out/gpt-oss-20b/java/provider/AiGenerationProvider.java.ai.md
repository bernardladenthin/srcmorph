### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:29Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides an interface for AI text generation backends, exposing generation and optional timing metrics.

#### Purpose
- Declares contract for AI backends that generate text from a request.
- Supplies default timing wrapper for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `String generate(AiGenerationRequest) throws IOException`, `AiGenerationTimings generateWithTimings(AiGenerationRequest) throws IOException`, `void close() throws IOException`.
  - Default implementation of `generateWithTimings` returns `AiGenerationTimings(generate(request), 0, 0, 0.0d, 0, 0.0d)`.
  - Default `close` does nothing.

#### Input
- `AiGenerationRequest request` parameter to `generate` and `generateWithTimings`.
- `AiGenerationRequest` contains prompt key, source file, source text, current header.

#### Output
- `generate`: non‑null `String` (may be blank).
- `generateWithTimings`: `AiGenerationTimings` containing generated text and timing metrics (rates default to 0).
- `close`: no output.

#### Core logic
- `generate` is abstract; implementation must produce text.
- `generateWithTimings`:
  1. Calls `generate(request)`.
  2. Wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.
- No explicit null checks; contract guarantees non‑null return.

#### Concurrency
- No thread‑safety guarantees; implementations may manage their own synchronization.

### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:29Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Provides a pluggable AI backend that generates text for an `AiGenerationRequest`.

#### Purpose
- Supplies AI text generation for source morphing tasks.
- Supports optional timing metrics for calibration.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
  - Methods: `generate(AiGenerationRequest)`, `generateWithTimings(AiGenerationRequest)`, `close()`.
  - Dependencies: `java.io.IOException`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.provider.AiGenerationTimings`.

#### Input
- `generate(AiGenerationRequest request)`  
  - `request`: prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)`  
  - Same `request`.

#### Output
- `generate`: `String` (never null, may be blank).
- `generateWithTimings`: `AiGenerationTimings` (text + timing metrics).
- `close`: no return, may throw `IOException`.

#### Core logic
- `generate`: abstract; implementation must produce text.
- `generateWithTimings`: default; calls `generate(request)` and returns `AiGenerationTimings` with zeroed timing fields.
- `close`: default no-op; may throw `IOException`.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce AI text.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` may throw `IOException` if provider fails.

#### Concurrency
- No explicit concurrency handling; implementations may be thread‑safe.

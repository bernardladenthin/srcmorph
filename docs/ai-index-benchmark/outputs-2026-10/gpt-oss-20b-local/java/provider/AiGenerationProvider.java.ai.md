### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:11:53Z
- G: 
- A: 0.0.0
- X: file
---
> Provides AI text generation for source‑code morphing requests.

#### Purpose
- Supplies generated text for an `AiGenerationRequest`.
- Supports optional timing metrics for calibration.

#### Type
- Interface `AiGenerationProvider` (public, extends `AutoCloseable`).

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing data.
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings` default: calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timings.
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.
- `generateWithTimings` throws `IOException` if underlying `generate` fails.

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.

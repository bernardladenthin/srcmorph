### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:21Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies AI-generated text for source‑code transformation requests.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider` – interface, extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` – request contains prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` with generated text and timing data.
- `close` performs no action by default.

#### Core logic
- `generate` is abstract; implementations provide text generation.
- `generateWithTimings` delegates to `generate` and supplies zeroed timing values unless overridden.
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.
- `generateWithTimings` throws `IOException` if underlying `generate` fails.
- `close` throws `IOException` (default no‑op).

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.

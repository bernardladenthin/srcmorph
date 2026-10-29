### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:53:06Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend that generates text for an AiGenerationRequest.

#### Purpose
- Supplies AI-generated text for source‑code transformations.
- Supports optional timing metrics for calibration.

#### Type
- Interface `AiGenerationProvider` (public, extends `AutoCloseable`).

#### Input
- `AiGenerationRequest request` (prompt key, source file, source text, current header).

#### Output
- `String` from `generate`.
- `AiGenerationTimings` from `generateWithTimings` (text + timing data).
- No state mutation.

#### Core logic
- `generate`: abstract; implementations produce text from the request.
- `generateWithTimings`: default; calls `generate` and returns timings with zeros.
- `close`: default no‑op; may be overridden.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce AI text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings` if provider fails.

#### Concurrency
- Stateless interface; thread‑safe by default.

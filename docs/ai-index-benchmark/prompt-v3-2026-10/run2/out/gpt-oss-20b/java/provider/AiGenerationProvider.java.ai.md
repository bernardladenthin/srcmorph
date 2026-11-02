### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:31Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies AI-generated text for a given request.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider`  
extends `AutoCloseable`; no generics.

#### Input
- `AiGenerationRequest request` in `generate` and `generateWithTimings`.

#### Output
- `String` from `generate`.
- `AiGenerationTimings` from `generateWithTimings` (text + timing data).
- No state mutation; `close` performs no action.

#### Core logic
- `generate`: abstract; implementations produce text.
- `generateWithTimings`: default; calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close`: default no‑op.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timing.
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings`.

#### Concurrency
- Stateless interface; thread‑safe by default.

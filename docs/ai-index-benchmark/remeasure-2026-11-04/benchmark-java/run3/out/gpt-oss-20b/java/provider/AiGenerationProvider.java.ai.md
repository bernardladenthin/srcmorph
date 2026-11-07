### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:07Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Provides AI text generation for source‑code transformation requests, supporting optional timing metrics.

#### Purpose
- Supplies generated text for an `AiGenerationRequest`.
- Enables optional timing data for calibration.

#### Type
`public interface AiGenerationProvider` – interface, extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` – request with prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing metrics.
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations supply text.
- `generateWithTimings` default: calls `generate`, returns timings with zeros.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.
- `generateWithTimings` throws `IOException` if underlying `generate` fails.

#### Concurrency
- No explicit synchronization; implementations decide thread safety.

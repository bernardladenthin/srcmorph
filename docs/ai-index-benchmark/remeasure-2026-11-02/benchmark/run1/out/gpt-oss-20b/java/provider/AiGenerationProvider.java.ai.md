### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:26Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies AI-generated text for a request.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider` – interface, extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` – request with prompt key, source file, source text, header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` containing text and timing data.
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations produce text.
- `generateWithTimings` default: calls `generate`, returns timings with zeros.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.
- `generateWithTimings` throws `IOException` if underlying `generate` fails.
- `close` throws `IOException` (default no‑op).

#### Concurrency
- No explicit synchronization; provider implementations decide thread safety.

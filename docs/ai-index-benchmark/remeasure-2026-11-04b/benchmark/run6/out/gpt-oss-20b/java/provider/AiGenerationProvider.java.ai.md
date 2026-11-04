### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:48Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Provides a pluggable AI backend that generates text for an AI generation request.

#### Purpose
- Supplies AI-generated text for `AiGenerationRequest`.
- Supports optional timing metrics for calibration.

#### Type
- `public interface AiGenerationProvider` (interface, extends `AutoCloseable`)

#### Input
- `generate(AiGenerationRequest request)` – request contains prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)` – same request.

#### Output
- `generate` returns `String` (never null, may be blank).
- `generateWithTimings` returns `AiGenerationTimings` (generated text, token rate, time per token, etc.).
- `close` performs no action by default.

#### Core logic
- `generate` – abstract; implementations provide text generation.
- `generateWithTimings` – default implementation:
  1. Calls `generate(request)`.
  2. Wraps result in `AiGenerationTimings` with all timing fields set to `0` or `0.0d`.
- `close` – default empty implementation.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timings.
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`

#### Exceptions / Errors
- `generate` throws `IOException` if provider fails.
- `generateWithTimings` throws `IOException` if provider fails.
- `close` throws `IOException`.

#### Concurrency
- No explicit concurrency handling; interface is thread‑safe only if implementations ensure it.

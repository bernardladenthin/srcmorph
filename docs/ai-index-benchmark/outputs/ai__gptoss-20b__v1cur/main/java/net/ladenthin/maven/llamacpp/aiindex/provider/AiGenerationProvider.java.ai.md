### AiGenerationProvider.java
- H: 1.0
- C: 14BD5D6E
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:11:35Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 1; TODO/FIXME: 0; @Override: 1; methods (approx): 3; constructors: 0; field declarations (w/ modifier): 0

> Provides a pluggable AI backend that generates text for an AI generation request, with optional timing metrics.

#### Purpose
- Supplies generated text for `AiGenerationRequest`.
- Optional timing information for calibration.

#### Type
- Interface `AiGenerationProvider` (public, extends `AutoCloseable`).

#### Input
- Method `generate(AiGenerationRequest request)` – receives `AiGenerationRequest` containing prompt key, source file, source text, and current header.
- Method `generateWithTimings(AiGenerationRequest request)` – same request input.
- Inherited `close()` – no parameters.

#### Output
- `generate` returns a non‑null `String` (possibly blank).
- `generateWithTimings` returns an `AiGenerationTimings` containing generated text and timing metrics.
- `close` has no return value.

#### Core logic
- `generate` is abstract; implementations provide model inference.
- `generateWithTimings` default: calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.
- `close` default: no action.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce text from request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – generate text with timing.
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationTimings` (used in default method).

#### Exceptions / Errors
- `generate` may throw `IOException` if provider fails.
- `generateWithTimings` may throw `IOException`.
- `close` may throw `IOException`.

#### Concurrency
- No explicit thread‑safety guarantees; implementations may manage concurrency.

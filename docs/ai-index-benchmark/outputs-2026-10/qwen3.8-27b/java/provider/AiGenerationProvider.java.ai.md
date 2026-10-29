### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:42:05Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable AI text-generation backend contract used by srcmorph to produce code or documentation from a prompt request.

#### Purpose
- SPI interface for AI backends (local llama.cpp, mock, remote) that generate text from an `AiGenerationRequest`.
- Supports optional timing metrics for the `srcmorph:calibrate` goal.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
- No generics, no annotations.

#### Input
- `AiGenerationRequest request` — contains prompt key, source file, source text, and current header.

#### Output
- `String` — generated text (never null, may be blank).
- `AiGenerationTimings` — generated text plus timing metrics (tokens/sec, etc.).
- `close()` — releases underlying resources.

#### Core logic
- `generate` is the sole abstract method; all providers must implement it.
- `generateWithTimings` is a default method that delegates to `generate` and wraps the result in an `AiGenerationTimings` with all timing fields zeroed; providers that expose real timings override it.
- `close` is a no-op default; providers with native resources (e.g., llama.cpp) override to release them.

#### Public API
- `generate(AiGenerationRequest) -> String` — core text generation entry point.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — generation with timing metrics.
- `close() -> void` — release provider resources.

#### Dependencies
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`
- `java.io.IOException`

#### Exceptions / Errors
- Both `generate` and `generateWithTimings` declare `throws IOException` for underlying provider failures.
- `close` also declares `throws IOException`.

#### Concurrency
- No synchronization or thread-safety guarantees declared; thread-safety is left to implementations.

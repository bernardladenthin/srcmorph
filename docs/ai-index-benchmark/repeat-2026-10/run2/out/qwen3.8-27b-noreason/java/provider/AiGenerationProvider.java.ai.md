### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:16Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the contract for pluggable AI backends that generate text from source-morphing requests, with optional timing metrics for calibration.

#### Purpose
- SPI interface for AI text-generation backends (local llama.cpp, mocks) consumed by the `srcmorph` document pipeline.
- Provides a default no-op `close()` and a default `generateWithTimings` that delegates to `generate` with zeroed timing fields.

#### Type
- `public interface AiGenerationProvider`
- `extends AutoCloseable`
- No generics, no annotations.

#### Input
- `AiGenerationRequest request` — carries prompt key, source file, source text, and current header.

#### Output
- `String` — generated text (never null, may be blank).
- `AiGenerationTimings` — generated text plus timing metrics (tokens/sec, latency, etc.); default returns zeros.
- `void` — `close()` releases provider resources.

#### Core logic
- `generate(request)` is the sole abstract method; implementations perform the actual model inference.
- `generateWithTimings(request)` is a `default` method that calls `generate(request)` and wraps the result in `new AiGenerationTimings(text, 0, 0, 0.0d, 0, 0.0d)` — six zero-valued timing fields.
- `close()` is a `default` no-op; providers with native resources (e.g., llama.cpp context) override to free them.

#### Public API
- `generate(AiGenerationRequest request) -> String` — abstract; runs model inference.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` — default; delegates + zero timings.
- `close() -> void` — default no-op; releases resources.

#### Dependencies
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (referenced in return type)
- `java.io.IOException`

#### Exceptions / Errors
- Both `generate` and `generateWithTimings` declare `throws IOException` for underlying provider failures.
- `close()` declares `throws IOException`.
- Contract: `generate` never returns null; blank string indicates zero tokens produced.

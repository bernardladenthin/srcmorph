### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:12Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable contract for AI text-generation backends (local llama.cpp or mocks) used by srcmorph to produce code text from an `AiGenerationRequest`.

#### Purpose
- SPI interface for AI generation providers that turn an `AiGenerationRequest` into generated text.
- Supports timing-aware generation for the `srcmorph:calibrate` goal and resource cleanup via `AutoCloseable`.

#### Type
- `public interface AiGenerationProvider extends AutoCloseable`
- No generics, no annotations.

#### Input
- `AiGenerationRequest request` — carries prompt key, source file, source text, and current header.

#### Output
- `String` — generated text (never null, may be blank).
- `AiGenerationTimings` — generated text plus measured timing metrics (tokens/sec, etc.).
- `void` — `close()` releases provider resources.

#### Core logic
- `generate(request)` — abstract; each backend implementation produces text using its default sampling parameters.
- `generateWithTimings(request)` — default method; delegates to `generate(request)` and wraps result in `AiGenerationTimings` with all timing fields zeroed (0, 0, 0.0d, 0, 0.0d); providers that expose real timings override this.
- `close()` — default no-op; implementations override to release model handles, sockets, or file resources.

#### Public API
- `generate(AiGenerationRequest) -> String` — produce text for request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — produce text with timing data.
- `close() -> void` — release provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (referenced in default method return)

#### Exceptions / Errors
- `IOException` thrown by `generate`, `generateWithTimings`, and `close` when the underlying provider fails.
- Generated text is contractually never null but may be blank if the model produced no tokens.

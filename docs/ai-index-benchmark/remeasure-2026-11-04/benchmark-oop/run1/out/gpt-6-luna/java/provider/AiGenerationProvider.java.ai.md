### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:45Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Generates text from AI generation requests and optionally reports model timing for calibration.

#### Purpose
- Defines the pluggable AI backend contract for producing text from `AiGenerationRequest`.
- Supports providers with optional generation timing and a no-op close operation.

#### Type
- `public interface AiGenerationProvider` — interface; extends `AutoCloseable`.

#### Input
- `generate(AiGenerationRequest request)` and `generateWithTimings(AiGenerationRequest request)` consume a request containing a prompt key, source file, source text, and current header.

#### Output
- `generate(...)` returns generated `String` text.
- `generateWithTimings(...)` returns `AiGenerationTimings`; its default implementation uses zero rates and timing values.
- Default `close()` performs no action.

#### Core logic
- `generate(...)` is the provider implementation contract; implementations supply generated text.
- Default `generateWithTimings(...)` delegates to `generate(request)` and wraps the text with zero timing values.
- Providers exposing timing data can override `generateWithTimings(...)`.
- Default `close()` is empty.

#### Public API
- `generate(AiGenerationRequest)` -> `String` — generate text.
- `generateWithTimings(AiGenerationRequest)` -> `AiGenerationTimings` — generate text with timing data.
- `close()` -> `void` — close provider resources.

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings`

#### Exceptions / Errors
- `generate(...)`, `generateWithTimings(...)`, and `close()` declare `IOException`.
- Generated text is documented as non-null; it may be blank.

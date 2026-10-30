### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:52Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable AI text-generation backend contract that turns a source-file generation request into generated text, with timing support for calibration.

#### Purpose
- Abstracts AI text generation behind one interface, so local (llama.cpp) and mock providers are interchangeable.
- Supplies timing-aware generation for the `srcmorph:calibrate` goal.

#### Type
- Interface `AiGenerationProvider`, public.
- extends `AutoCloseable`.
- Overrides `close()` as a default no-op method.

#### Input
- `generate(AiGenerationRequest request)`: prompt key, source file, source text, current header.
- `generateWithTimings(final AiGenerationRequest request)`: same request type.

#### Output
- `generate` returns generated `String`, never `null`, possibly blank.
- `generateWithTimings` returns `AiGenerationTimings` wrapping generated text and timing values.
- Default `generateWithTimings` reports zero timings: `0`, `0`, `0.0d`, `0`, `0.0d`.

#### Core logic
- `generateWithTimings` default delegates to `generate(request)`.
- Default path constructs `AiGenerationTimings` with text and zeroed rates.
- Providers exposing timings override `generateWithTimings`.
- `close()` default performs no action.

#### Public API
- `generate(AiGenerationRequest request) -> String`: produces generated text using default sampling.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: generated text plus measured timings.
- `close() -> void`: releases provider resources, default no-op.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `AiGenerationTimings` (same package, `net.ladenthin.srcmorph.provider`, not imported)
- `AutoCloseable`

#### Exceptions / Errors
- `generate` and `generateWithTimings` throw `IOException` when the provider fails.
- `close` throws `IOException`.
- Null handling: `generate` contract forbids `null` return.

#### Concurrency
- No synchronization or thread-safety guarantees stated in the source.

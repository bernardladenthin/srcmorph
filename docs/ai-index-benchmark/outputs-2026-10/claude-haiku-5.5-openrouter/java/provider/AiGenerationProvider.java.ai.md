### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:53Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the pluggable AI text-generation contract (with timing variant) that srcmorph providers, such as local llama.cpp or test mocks, implement to produce text for generation requests.

#### Purpose
- Interface abstracting AI text generation for a single `AiGenerationRequest`, letting backends be swapped (local llama.cpp, mocks for tests).
- Adds an optional timing-aware generation path used by the `srcmorph:calibrate` goal.

#### Type
- Interface, public, extends `AutoCloseable`.
- No generics, no sealed/final modifiers.
- Annotations: none (`@Override` only on `close()`).

#### Input
- `generate(AiGenerationRequest request)`: request carrying prompt key, source file, source text, and current header.
- `generateWithTimings(final AiGenerationRequest request)`: same request type, passed as final parameter.

#### Output
- `String` generated text from `generate`.
- `AiGenerationTimings` from `generateWithTimings`, wrapping text plus timing values.
- `close()` produces no output; default no-op.

#### Core logic
- `generate` is abstract; implementations must return generated text (never null, possibly blank) or throw `IOException`.
- `generateWithTimings` default delegates to `generate` and wraps result in `AiGenerationTimings` with constructor args `(text, 0, 0, 0.0d, 0, 0.0d)`, so all timing rates are zero.
- Providers exposing timings override `generateWithTimings`.
- `close` default is an empty body, so providers holding resources (e.g. native llama.cpp handles) override it.

#### Public API
- `generate(AiGenerationRequest request) -> String` : produces text for request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` : text plus timing; default zero rates.
- `close() -> void` : releases provider resources; default no-op.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (same package, referenced but not imported)
- `AutoCloseable`

#### Exceptions / Errors
- `IOException` declared by `generate`, `generateWithTimings`, and `close`; signals underlying provider failure.
- Null handling: `generate` contract forbids returning `null`.

#### Concurrency
- No synchronization or thread-safety guarantees declared in the interface; implementations define them.

### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:41:00Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense, structured markdown summary of the AiGenerationProvider.java source file.

The interface defines a pluggable AI backend that generates text from an AI generation request, optionally with model timings. It serves as a contract for backends used by srcmorph.

It extends AutoCloseable and defines default sampling and calibration timings. The generate method is abstract and returns a non-null String. The generateWithTimings method provides default timing values when overridden. Close is a no-op default.

The default generateWithTimings delegates to generate and wraps the result in AiGenerationTimings with zeroed fields. All three methods are public. Dependencies include java.io.IOException, AiGenerationRequest, AiGenerationTimings, and AutoCloseable. IOException is thrown by all three methods.> Defines a pluggable AI backend that generates text for an AI generation request, optionally with model timings.

#### Purpose
- Contract for backends that produce text from an `AiGenerationRequest` (local llama.cpp or test mocks).
- Supplies a timings wrapper for the `srcmorph:calibrate` goal and is closeable.

#### Type
- Interface; extends `AutoCloseable`. No class-level annotations.
- Default methods: `generateWithTimings`, `close`. Abstract method: `generate`.

#### Input
- `AiGenerationRequest`: prompt key, source file, source text, and current header (javadoc).
- Consumed only as the argument to `generate` / `generateWithTimings`. No fields, files, or injected dependencies.

#### Output
- `generate` returns non-null `String` (blank allowed if the model emits no tokens).
- `generateWithTimings` returns `AiGenerationTimings` (generated text plus rates; zeros when timings are unavailable).
- `close` is a no-op by default; implementations may release the backend.

#### Core logic
- `generate` is the required hook: produce text with the provider’s default sampling parameters.
- Default `generateWithTimings` delegates to `generate` and wraps the result as `new AiGenerationTimings(text, 0, 0, 0.0d, 0, 0.0d)`.
- Providers that can measure model timing override `generateWithTimings`.
- Default `close` does nothing so unused providers need not override it.

#### Public API
- `generate(AiGenerationRequest request) -> String` — generate text, default sampling
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` — text plus measured timings
- `close() -> void` — release provider resources

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (same package, unqualified)

#### Exceptions / Errors
- `generate`, `generateWithTimings`, and `close` declare `IOException` when the underlying provider fails.
- Contract: generated text is never `null`; blank text is valid.

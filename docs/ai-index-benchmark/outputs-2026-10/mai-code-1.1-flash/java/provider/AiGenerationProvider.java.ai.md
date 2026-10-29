### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:44:44Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that generates text for source-aware coding requests and optionally reports timing metadata without requiring provider-specific boilerplate.

#### Purpose
- Defines the contract for AI text generation providers used by `srcmorph` requests.
- Provides default timing and close behavior for providers without custom implementations.

#### Type
- Kind: interface
- Modifiers: public
- Extends: `AutoCloseable`
- Generic parameters: none
- Annotations: none

#### Input
- `generate(AiGenerationRequest request)`: consumes the request object containing prompt key, source file, source text, and current header.
- `generateWithTimings(AiGenerationRequest request)`: consumes the same request and delegates to `generate`.
- `close()`: no input; default no-op cleanup hook.

#### Output
- `generate(...) -> String`: returns generated text, never `null`, may be blank.
- `generateWithTimings(...) -> AiGenerationTimings`: returns generated text plus timing values, defaulting to zero rates when unavailable.
- `close() -> void`: no output; default no-op.

#### Core logic
- `AiGenerationProvider` is a pluggable contract for AI backends producing text for `AiGenerationRequest`.
- `generateWithTimings(...)` default implementation calls `generate(request)` and wraps the result in `new AiGenerationTimings(generate(request), 0, 0, 0.0d, 0, 0.0d)`.
- Providers that expose measured timing can override `generateWithTimings(...)`; otherwise timings remain zeroed.
- `close()` default implementation is empty and does not throw beyond `IOException` contract.

#### Public API
- `generate(AiGenerationRequest request) -> String` - generates text for request
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` - generates text with timing metadata
- `close() -> void` - closes provider resources

#### Dependencies
- `java.io.IOException`
- `java.lang.AutoCloseable`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate(...)` throws `IOException` if the underlying provider fails.
- `generateWithTimings(...)` throws `IOException` if the underlying provider fails.
- `close()` declares `IOException` via `AutoCloseable` but default implementation is no-op.
- Return contract: generated text is never `null`, but may be blank if no tokens are produced.
- Timing values default to `0` and `0.0d` when unavailable.

#### Concurrency
- No synchronization or shared mutable state is defined.
- Default implementations are stateless and side-effect free except for provider-specific overrides.

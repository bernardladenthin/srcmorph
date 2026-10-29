### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:01:11Z
- G: 
- A: 0.0.0
- X: file
---
> Generates AI text for srcmorph document requests

#### Purpose
- Pluggable AI text generation for `AiGenerationRequest` contracts

#### Type
- `interface AiGenerationProvider extends AutoCloseable`

#### Input
- `AiGenerationRequest request` parameter in `generate`

#### Output
- `generate` returns `String` (generated text, never null but may be blank)
- `generateWithTimings` returns `AiGenerationTimings` with measured rates
- `close` signals resource cleanup with no-op default

#### Core logic
- Declares generation contract for pluggable AI backends
- Provides default `generateWithTimings` delegating to `generate` with zero timings
- Provides default `close` as no-argument no-throw override

#### Public API
- `generate(AiGenerationRequest request) -> String` Generates text from request
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` Returns text with timing data
- `close() -> void` Closes provider resources

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException` thrown by `generate`, `generateWithTimings`, `close`
- `generate` returns never-null but may-be-blank string per javadoc

#### Concurrency
- No explicit threading contract; implementations control synchronization

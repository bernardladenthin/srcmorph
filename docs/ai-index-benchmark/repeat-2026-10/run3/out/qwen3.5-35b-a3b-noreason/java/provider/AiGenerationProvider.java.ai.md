### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:55Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a pluggable interface for generating text responses to AI generation requests with optional timing metrics.

#### Purpose
- Provides a contract for pluggable AI backends to generate text based on generation requests.
- Supports both standard generation and timing-aware generation for calibration purposes.

#### Type
- `interface` `AiGenerationProvider`
- Extends `AutoCloseable`
- Key methods: `generate`, `generateWithTimings`, `close`
- Notable annotations: None

#### Input
- `AiGenerationRequest request`: Contains prompt key, source file, source text, and current header.
- Injected dependencies: None (interface only).

#### Output
- `String`: Generated text (never null, may be blank).
- `AiGenerationTimings`: Wrapper containing generated text and timing metrics (tokens per second, total time, etc.).
- Side effects: None defined in interface; implementations may have side effects.

#### Core logic
- `generate`: Delegates to the underlying AI model to produce text for the provided request.
- `generateWithTimings`: Default implementation calls `generate` and returns a `AiGenerationTimings` object with zeroed timing values; overridden by providers to report actual performance metrics.
- `close`: Default no-op implementation; allows providers to release resources if needed.

#### Public API
- `generate(AiGenerationRequest) -> String` Generates text for the request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` Generates text with timing metrics.
- `close() -> void` Closes the provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `IOException`: Thrown by `generate` and `generateWithTimings` if the underlying provider fails.
- `IOException`: Thrown by `close` if cleanup fails (though default is no-op).

#### Concurrency
- No concurrency guarantees or synchronization mechanisms defined in the interface.

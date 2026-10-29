### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:53Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable interface for generating text responses to AI generation requests with optional timing metrics.

#### Purpose
- Defines the contract for pluggable AI backends that produce text based on generation requests.
- Enables local execution (e.g., llama.cpp) or mock implementations for testing.

#### Type
interface `AiGenerationProvider` extends `AutoCloseable`.
Notable annotations: None.

#### Input
- Constructor parameters: None (interface).
- Method parameters: `AiGenerationRequest request` containing prompt key, source file, source text, and current header.
- Injected dependencies: None explicitly defined; implementations manage their own backend state.

#### Output
- Return types: `String` generated text (never null); `AiGenerationTimings` wrapper containing text and performance metrics.
- Side effects: Underlying provider execution (local model inference or mock response).
- Resource handling: Implements `close()` to release resources.

#### Core logic
- `generate`: Delegates to the specific backend implementation to produce text using default sampling parameters.
- `generateWithTimings`: Default implementation calls `generate` and wraps result in `AiGenerationTimings` with zeroed rates/times; overridden by providers exposing actual metrics.
- `close`: Default no-op implementation; providers override to clean up resources.

#### Public API
- generate(AiGenerationRequest request) -> String Generates text for a request.
- generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings Generates text with timing data.
- close() -> void Closes the provider.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings` (referenced in return type)

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails during generation or closing.
- Returns empty string rather than null if model produces no tokens.

#### Concurrency
- No explicit synchronization or thread-safety guarantees provided in the interface definition.

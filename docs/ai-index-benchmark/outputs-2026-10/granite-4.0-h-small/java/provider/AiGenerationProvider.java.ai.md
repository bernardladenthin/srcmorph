### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:59:13Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for AI backends to generate text for source morphing requests.

#### Purpose
- Allows different AI generation backends (local or remote) to be plugged in for generating text based on source morphing requests.
- Supports both simple text generation and generation with timing information.

#### Type
- Interface `AiGenerationProvider` extending `AutoCloseable`.
- Annotations: None.

#### Input
- `AiGenerationRequest` object containing:
  - Prompt key
  - Source file
  - Source text
  - Current header

#### Output
- `String` containing the generated text (never `null`, may be blank).
- `AiGenerationTimings` object containing:
  - Generated text
  - Timing information (rates, tokens, etc.)

#### Core logic
- `generate` method:
  - Takes an `AiGenerationRequest` as input.
  - Generates text using the provider's default sampling parameters.
  - Returns the generated text.
  - Throws `IOException` if the underlying provider fails.
- `generateWithTimings` method (default implementation):
  - Delegates to `generate` method.
  - Returns `AiGenerationTimings` with generated text and default timing values (rates set to 0).
- `close` method (default implementation):
  - Does nothing (empty implementation).

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text for the given request using default sampling parameters.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings`: Generates text and returns it with timing information (default implementation delegates to `generate`).
- `close()`: Closes the provider (default implementation does nothing).

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `IOException`: Thrown by `generate` method if the underlying provider fails.

#### Concurrency
- No specific concurrency notes or thread-safety considerations mentioned.

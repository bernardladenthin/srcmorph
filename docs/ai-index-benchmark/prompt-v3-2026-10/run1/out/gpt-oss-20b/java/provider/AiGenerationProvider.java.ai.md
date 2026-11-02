### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:53:46Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an AI backend that generates text for a generation request.

#### Purpose
- Supplies generated text for AI generation requests.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Interface, extends `AutoCloseable`.  
- No annotations.

#### Input
- `generate(AiGenerationRequest request)`  
  - `AiGenerationRequest` containing prompt key, source file, source text, current header.  
- `generateWithTimings(AiGenerationRequest request)`  
  - Same request object.  
- `close()`  
  - No parameters.

#### Output
- `generate` returns `String` (never null, may be blank).  
- `generateWithTimings` returns `AiGenerationTimings` (generated text + timing data).  
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations provide text generation.  
- `generateWithTimings` default: calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – generate text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – generate text with timing.  
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings` (used in default method)

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.  
- `generateWithTimings` throws `IOException` (delegates).  
- `close` throws `IOException` (default no-op).

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.

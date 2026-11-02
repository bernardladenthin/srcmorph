### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:34Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies AI-generated text for `AiGenerationRequest`.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Implements `AutoCloseable`.  
- No annotations.

#### Input
- `generate(AiGenerationRequest request)`  
  - `AiGenerationRequest` containing prompt key, source file, source text, current header.  
- `generateWithTimings(AiGenerationRequest request)`  
  - Same request.  
- `close()`  
  - No parameters.

#### Output
- `generate` returns `String` (never null).  
- `generateWithTimings` returns `AiGenerationTimings` (generated text + timing data).  
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations provide text generation.  
- `generateWithTimings` default: calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce AI text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.  
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.  
- `generateWithTimings` throws `IOException` (delegates).  
- `close` throws `IOException` (default no-op).

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.

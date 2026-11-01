### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:52Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend that generates text for an AI generation request.

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
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.  
- No other exceptions.

#### Concurrency
- No thread‑safety guarantees; implementations may manage concurrency.

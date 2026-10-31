### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:04:35Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend for generating text from an {@link AiGenerationRequest}.

#### Purpose
- Supplies AI-generated text for source‑code transformations.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Default method `generateWithTimings` returns `AiGenerationTimings`.  
- Annotation: none.

#### Input
- `generate(AiGenerationRequest request)`  
  - `AiGenerationRequest` containing prompt key, source file, source text, current header.  
- `generateWithTimings(AiGenerationRequest request)`  
  - Same request object.  
- `close()`  
  - No parameters.

#### Output
- `generate` → `String` (never null, may be blank).  
- `generateWithTimings` → `AiGenerationTimings` (text + timing metrics).  
- `close` → no return; may throw `IOException`.

#### Core logic
- `generate` is abstract; implementations provide model inference.  
- `generateWithTimings` calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` is a no‑op default; implementations may override.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.  
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.  
- `close` declares `throws IOException`.

#### Concurrency
- No explicit synchronization; implementations decide thread safety.

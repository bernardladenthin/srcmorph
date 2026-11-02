### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:40:52Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI backends for generating text from an {@link AiGenerationRequest}.

#### Purpose
- Supplies AI-generated text for source‑code morphing tasks.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- No generics.  
- Default method `generateWithTimings`.  
- Annotation: none.

#### Input
- `generate(AiGenerationRequest request)` – prompt key, source file, source text, current header.  
- `generateWithTimings(AiGenerationRequest request)` – same request.  
- `close()` – no parameters.

#### Output
- `generate` returns non‑null `String` (may be blank).  
- `generateWithTimings` returns `AiGenerationTimings` (text + timing data).  
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations supply text.  
- `generateWithTimings` delegates to `generate` and returns default timings (rates 0).  
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.  
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.  
- `close` declares `throws IOException`.

#### Concurrency
- No explicit synchronization; implementations decide thread safety.

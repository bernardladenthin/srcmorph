### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:50:47Z
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
- Default method `generateWithTimings(AiGenerationRequest)`  
- Abstract method `generate(AiGenerationRequest)`  
- Default `close()`.

#### Input
- `AiGenerationRequest request` (prompt key, source file, source text, current header).

#### Output
- `String` from `generate`; never `null`, may be blank.  
- `AiGenerationTimings` from `generateWithTimings`; includes text and timing data.  
- `close()` performs no action.

#### Core logic
- `generate` is abstract; concrete providers implement text generation.  
- `generateWithTimings` calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.  
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.  
- `close` declares `throws IOException` but default implementation does nothing.

#### Concurrency
- No explicit synchronization; provider implementations decide thread safety.

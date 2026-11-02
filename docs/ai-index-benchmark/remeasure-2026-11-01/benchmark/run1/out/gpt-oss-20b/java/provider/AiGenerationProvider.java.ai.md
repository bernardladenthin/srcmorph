### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:26:01Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI backends that generate text for a generation request.

#### Purpose
- Supplies AI-generated text for `AiGenerationRequest`.
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- No generics.  
- Default method `generateWithTimings`.  
- Annotation: none.

#### Input
- `generate(AiGenerationRequest request)`  
- `generateWithTimings(AiGenerationRequest request)`  
- `close()` (no parameters).

#### Output
- `generate` returns `String` (never null).  
- `generateWithTimings` returns `AiGenerationTimings` (text + timing data).  
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations supply text.  
- `generateWithTimings` calls `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` default no‑op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.  
- `close() -> void` – close provider.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings` (used in default method).

#### Exceptions / Errors
- `generate` and `generateWithTimings` declare `throws IOException`.  
- `close` declares `throws IOException`.

#### Concurrency
- No explicit synchronization; interface is stateless.  
- Implementations may be thread‑safe or not.

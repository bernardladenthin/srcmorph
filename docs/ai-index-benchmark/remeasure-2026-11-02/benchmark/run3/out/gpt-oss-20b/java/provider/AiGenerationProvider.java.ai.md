### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:39Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Provides a pluggable AI backend that generates text for a generation request.

#### Purpose
- Supplies AI-generated text for a document generation request.  
- Supports optional timing metrics for calibration.

#### Type
`public interface AiGenerationProvider`  
extends `AutoCloseable`; no generics.

#### Input
- `generate(AiGenerationRequest request)` – request with prompt key, source file, source text, current header.  
- `generateWithTimings(AiGenerationRequest request)` – same request.  
- `close()` – no parameters.

#### Output
- `generate` returns non‑null `String` (may be blank).  
- `generateWithTimings` returns `AiGenerationTimings` containing generated text and timing data.  
- `close` performs no action.

#### Core logic
- `generate` is abstract; concrete providers implement text generation.  
- `generateWithTimings` default: call `generate`, wrap result in `AiGenerationTimings` with zeroed timing fields.  
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timings.  
- `close() -> void` – release resources.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.  
- `generateWithTimings` throws `IOException` if underlying `generate` fails.  
- `close` throws `IOException` (default no‑op).

#### Concurrency
- No explicit synchronization; implementations may manage thread safety.

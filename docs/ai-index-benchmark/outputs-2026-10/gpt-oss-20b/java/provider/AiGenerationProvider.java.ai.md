### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:49:24Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend for generating text from an AiGenerationRequest.

#### Purpose
- Supplies AI-generated text for source‑code transformation requests.
- Supports optional timing metrics for calibration.

#### Type
- Interface `AiGenerationProvider`  
- Extends `AutoCloseable`  
- No annotations.

#### Input
- `generate(AiGenerationRequest request)`  
  - `AiGenerationRequest` containing prompt key, source file, source text, current header.
- `generateWithTimings(AiGenerationRequest request)`  
  - Same request object.

#### Output
- `generate` returns non‑null `String` (may be blank).
- `generateWithTimings` returns `AiGenerationTimings` with generated text and timing data.
- `close` performs no action.

#### Core logic
- `generate` is abstract; implementations provide model inference.
- `generateWithTimings` default: calls `generate`, returns timings all zero.
- `close` default: no-op.

#### Public API
- `generate(AiGenerationRequest request) -> String` – produce AI text.  
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` – produce text with timing.  
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` throws `IOException` on provider failure.  
- `generateWithTimings` throws `IOException` if underlying `generate` fails.  
- `close` throws `IOException` (default no‑op).

#### Concurrency
- No explicit synchronization; implementations decide thread safety.

### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:39Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI backends for generating text from requests.

#### Purpose
- Define contract for AI text generation.  
- Offer default timing measurement for calibration.

#### Type
`public interface AiGenerationProvider extends AutoCloseable`  
- Default methods: `generateWithTimings`, `close`.  
- No generics.

#### Input
- `generate(AiGenerationRequest request)` – request contains prompt key, source file, source text, header.  
- `generateWithTimings(AiGenerationRequest request)` – same request.  
- `close()` – no parameters.

#### Output
- `generate` returns `String` (never null).  
- `generateWithTimings` returns `AiGenerationTimings` (text + timing).  
- `close` returns `void`.

#### Core logic
- `generate` is abstract; implementations supply text generation.  
- `generateWithTimings` delegates to `generate`, wraps result in `AiGenerationTimings` with zeroed timing fields.  
- `close` is a no‑op default.

#### Public API
- `generate(AiGenerationRequest) -> String` – produce text.  
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` – produce text with timing.  
- `close() -> void` – close resources.

#### Dependencies
- `java.io.IOException`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- All public methods declare `throws IOException` for provider failures.

#### Concurrency
- No explicit thread‑safety guarantees; implementations may be stateful.

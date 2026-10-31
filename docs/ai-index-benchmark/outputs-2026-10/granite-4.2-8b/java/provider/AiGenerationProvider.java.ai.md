### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:29:42Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
Pluggable AI backend interface for generating text from source code analysis requests, optionally reporting timing metrics.

#### Purpose
- Defines contract for AI providers that generate textual output from code analysis requests.
- Supports optional timing measurement for calibration/testing goals.
- Provides clean shutdown via `close()`.

#### Type
Kind: interface; modifiers: none.  
extends: `AutoCloseable`; implements: none.  
Generics: none.  
Annotations: none.

#### Input
- `AiGenerationRequest request` passed to `generate` and `generateWithTimings`.

#### Output
- `String` from `generate`; never `null`, may be blank.
- `AiGenerationTimings` from `generateWithTimings` (includes generated text and timing fields).
- Side effect: `close()` releases resources.

#### Core logic
- `generate`: required implementation; produces text from request.
- `generateWithTimings`: default implementation calls `generate` and wraps result in `AiGenerationTimings` with zero timings (rates 0, internal times 0.0).
- `close`: default no-op; implementations may release external resources.

#### Public API
- `generate(AiGenerationRequest request) -> String` — produce generated text for request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` — produce text plus timing metrics (default delegates to `generate`).
- `close() -> void` — release resources (default no-op).

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.document.AiGenerationTimings` (used in default method)

#### Exceptions / Errors
- `IOException` thrown by `generate` and `generateWithTimings` if underlying provider fails; caught by default implementations as part of contract.
- `generate` guarantees return is never `null` (may be blank).

#### Concurrency
- No concurrency guarantees stated; interface is thread-safe by contract (stateless expected). Default methods are safe.

#### Notes on Public API enumeration
- Two public methods (`generate`, `generateWithTimings`) and one protected/default method (`close`) are explicitly declared.
- No family of similar methods (e.g., `fooN`) present.
- Total public API members listed: `generate`, `generateWithTimings`, `close` (3 total).

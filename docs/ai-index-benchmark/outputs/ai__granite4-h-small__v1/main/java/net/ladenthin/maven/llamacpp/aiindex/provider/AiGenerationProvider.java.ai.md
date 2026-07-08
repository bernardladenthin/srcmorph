### AiGenerationProvider.java
- H: 1.0
- C: 14BD5D6E
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:06:25Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 1; TODO/FIXME: 0; @Override: 1; methods (approx): 3; constructors: 0; field declarations (w/ modifier): 0

> Provides an AI backend for generating text from a prompt request, optionally returning timing data.

#### Purpose
- Interface for pluggable AI generation backends.
- Supports local models (llama.cpp) or test mocks.

#### Type
- `interface` `AiGenerationProvider` with `AutoCloseable` supertype.

#### Input
- `generate(AiGenerationRequest request)` receives a request containing prompt key, source file, source text, and current header.
- `generateWithTimings(AiGenerationRequest request)` same input.

#### Output
- `generate` returns non‑null generated text (may be blank).
- `generateWithTimings` returns an `AiGenerationTimings` instance containing text and timing metrics (default rates 0).
- `close` performs no action by default.

#### Core logic
- `generate`: abstract; implementations provide text generation logic.
- `generateWithTimings`: default implementation calls `generate`, wraps result in `AiGenerationTimings` with zeroed metrics; overrideable by providers exposing real timings.
- `close`: default empty implementation; allows resources to be released if needed.

#### Public API
- `generate(AiGenerationRequest request) -> String` generates text for the request.
- `generateWithTimings(AiGenerationRequest request) -> AiGenerationTimings` generates text with timing data.
- `close() -> void` closes provider resources.

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationTimings`

#### Exceptions / Errors
- `generate` and `generateWithTimings` throw `IOException` if the underlying provider fails.

#### Concurrency
- No explicit thread‑safety guarantees; providers may be stateful.

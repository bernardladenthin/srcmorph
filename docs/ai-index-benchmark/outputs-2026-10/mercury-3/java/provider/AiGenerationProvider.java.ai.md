### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:28Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that produces text for an AiGenerationRequest, with optional timing for the srcmorph:calibrate goal.

#### Purpose
- Abstraction over AI generation backends (e.g., llama.cpp) used by srcmorph.
- Enables mock providers for tests and timing-aware providers for calibration.

#### Type
- Interface, extends AutoCloseable.
- Default methods: generateWithTimings, close.

#### Input
- AiGenerationRequest request: generation request containing prompt key, source file, source text, and current header.

#### Output
- generate returns String: generated text, never null, may be blank.
- generateWithTimings returns AiGenerationTimings: generated text plus timing rates (0 when unavailable).
- close: no action by default.

#### Core logic
- generate: contract method; implementations produce text using default sampling parameters.
- generateWithTimings: default implementation delegates to generate and returns timing with all rates 0.
- close: default no-op implementation for AutoCloseable.

#### Public API
- `generate(AiGenerationRequest) -> String` — Generates text for the request.
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` — Generates text with measured timing.
- `close() -> void` — Closes provider resources (no-op by default).

#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.provider.AiGenerationTimings
- AutoCloseable

#### Exceptions / Errors
- generate declares IOException if the underlying provider fails.
- generateWithTimings declares IOException if the underlying provider fails.

#### Concurrency
- Interface contract; no threading or synchronization specified.

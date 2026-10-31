### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:44:34Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that generates text for source morphing requests, supporting local or mock implementations with optional timing measurement.

#### Purpose
- Defines a contract for AI backends that transform source code via generation requests.
- Provides a default no-timing implementation for calibration and a closeable lifecycle.

#### Type
- Interface, public, extends AutoCloseable.
- No generics or type bounds.
- Referenced types: AiGenerationRequest, AiGenerationTimings.

#### Input
- generate(AiGenerationRequest request): consumes a request containing prompt key, source file, source text, and current header.
- generateWithTimings(final AiGenerationRequest request): consumes the same request type.
- close(): no parameters.

#### Output
- generate: returns String, never null but may be blank.
- generateWithTimings: returns AiGenerationTimings wrapping generated text with zeroed timing rates by default.
- close: returns void; default implementation is a no-op.

#### Core logic
- AiGenerationProvider declares an abstract generate method for text production using default sampling parameters.
- Implementations may run locally (llama.cpp) or act as mock providers for tests.
- generateWithTimings defaults to delegating to generate and constructing AiGenerationTimings with zero rates.
- close provides a default no-op release of provider resources.

#### Public API
- generate(AiGenerationRequest) -> String: produces text from a generation request.
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings: returns text with timing data.
- close() -> void: releases provider resources.

#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- AiGenerationTimings

#### Exceptions / Errors
- generate throws IOException if the underlying provider fails.
- generateWithTimings throws IOException if the underlying provider fails.
- close throws IOException but the default implementation does nothing.

#### Concurrency
- No synchronization, immutability, or thread-safety guarantees specified.

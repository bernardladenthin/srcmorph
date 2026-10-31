### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:50:20Z
- G: 
- A: 0.0.0
- X: file
---
> Pluggable AI backend that generates text for srcmorph AI generation requests.

#### Purpose
- Pluggable AI backend for text generation.
- Supports local llama.cpp and mock providers.

#### Type
- Interface, public, extends AutoCloseable.

#### Input
- AiGenerationRequest request containing prompt key, source file, source text, and current header.
- IOException from underlying provider.

#### Output
- String from generate.
- AiGenerationTimings from generateWithTimings.
- Void from close.

#### Core logic
- Default generateWithTimings delegates to generate and reports zero timings.
- generate is abstract; implementations provide actual text generation.
- close releases resources.

#### Public API
- generate(request) -> String: Generates text for the given request.
- generateWithTimings(request) -> AiGenerationTimings: Reports generated text and timing info.
- close() -> void: Closes the provider and releases resources.

#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.provider package

#### Exceptions / Errors
- IOException on generation or close failures.
- No null handling specified.

#### Concurrency
- No explicit thread-safety; interface is stateless.

### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:14:21Z
- G: 
- A: 0.0.0
- X: file
---
> Provides pluggable AI text generation for source code morphing tasks, supporting local and mock implementations.

#### Purpose
- Defines a contract for AI text generation in source code transformation workflows
- Supports both real AI backends and test mocks

#### Type
Interface public

#### Input
- `AiGenerationRequest` parameter in `generate` and `generateWithTimings` methods
- No constructor parameters

#### Output
- `String` from `generate` method, never null
- `AiGenerationTimings` from `generateWithTimings` method
- `void` from `close` method

#### Core logic
- Defines contract for AI text generation from source code prompts
- Provides default implementation for timing-aware generation that returns zeroed metrics
- Supports resource cleanup through AutoCloseable interface

#### Public API
- `generate(AiGenerationRequest) -> String` produces AI-generated text
- `generateWithTimings(AiGenerationRequest) -> AiGenerationTimings` produces text with timing data
- `close() -> void` releases provider resources

#### Dependencies
- `java.io.IOException`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- Throws `IOException` from `generate` and `generateWithTimings` methods
- `close` method may throw `IOException`

#### Concurrency
- No explicit concurrency handling mentioned
- Interface is stateless by design, allowing concurrent usage

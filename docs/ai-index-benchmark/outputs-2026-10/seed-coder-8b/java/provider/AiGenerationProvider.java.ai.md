### AiGenerationProvider.java
- H: 1.0
- C: 87DF69D2
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:14:00Z
- G: 
- A: 0.0.0
- X: file
---
> Interface for AI text generation providers, handling requests and returning generated content.

#### Purpose
- Defines interface for AI-driven text generation
- Allows pluggable backends for various AI implementations

#### Type
- Interface
- Implements AutoCloseable
- Part of net.ladenthin.srcmorph.provider package

#### Input
- AiGenerationRequest object containing prompt and source information

#### Output
- Generated text as String
- AiGenerationTimings object (in generateWithTimings method)

#### Core logic
- Defines generate method for text production
- Provides default implementation for generateWithTimings
- Offers close method for resource management

#### Public API
- generate(AiGenerationRequest) -> String
- generateWithTimings(AiGenerationRequest) -> AiGenerationTimings
- close() -> void

#### Dependencies
- java.io.IOException
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.document.AiGenerationTimings

#### Exceptions
- Throws IOException for underlying provider failures
- Returns non-null String, potentially empty

#### Concurrency
- Interface, no explicit concurrency handling

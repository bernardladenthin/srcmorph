### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:49:53Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides pluggable text generation for AI inference requests, supporting local and mock backends.

#### Purpose
- Defines a contract for generating text from AI prompts.
- Enables flexible backend implementations for inference tasks.

#### Type
Interface, no modifiers. Implements AutoCloseable.

#### Input
- `AiGenerationRequest` object containing prompt key, source file, source text, and current header.
- Optional `float temperatureOverride` parameter in overloaded method.

#### Output
- Generated text as `String`; never null.
- Throws `IOException` on failure.

#### Core logic
- Generates textual output based on input request.
- Supports overriding sampling temperature for retry strategies.
- Delegates to default implementation if not overridden.

#### Public API
- `generate(AiGenerationRequest) -> String` produces text from request.
- `generate(AiGenerationRequest, float) -> String` overrides temperature for a single call.

#### Dependencies
- `AiGenerationRequest`
- `IOException`
- `AutoCloseable`

#### Exceptions / Errors
- Throws `IOException` on backend failure.
- Guarantees non-null return value.

#### Concurrency
- No explicit concurrency handling; assumes thread-safe usage of implementations.

### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:16:55Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a contract for pluggable AI text generation backends that produce responses based on structured requests.

#### Purpose
- Provides a standardized interface for AI text generation.
- Supports both local and mock implementations for testing.

#### Type
Interface public; extends AutoCloseable

#### Input
- `AiGenerationRequest` object containing prompt key, source file, source text, and current header
- Optional `float temperatureOverride` parameter in overloaded method

#### Output
- Generated text as `String`; never null but may be blank
- Throws `IOException` on failure

#### Core logic
- Delegate generation to underlying AI backend using request data
- Override default temperature for retry attempts when needed
- Close resource via inherited AutoCloseable behavior

#### Public API
- `generate(AiGenerationRequest) -> String` produce text from request
- `generate(AiGenerationRequest, float) -> String` produce text with custom temperature

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest

#### Exceptions / Errors
- Throws `IOException` on underlying provider failure
- Handles null return values gracefully by contract

#### Concurrency
- No explicit concurrency handling; relies on implementation thread safety

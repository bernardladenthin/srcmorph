### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:34:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> A pluggable AI backend that generates text for an AiGenerationRequest, supporting both local and mock providers.

#### Purpose
- Provides AI text generation functionality.
- Supports both local and mock implementations.

#### Type
- Interface: `AiGenerationProvider` extends `AutoCloseable`.

#### Input
- `AiGenerationRequest` for prompt key, source file, source text, and current header.

#### Output
- Generated text as a `String`.
- May throw `IOException`.

#### Core Logic
- `generate(AiGenerationRequest request)`: Generates text using default parameters.
- `generate(AiGenerationRequest request, float temperatureOverride)`: Overrides temperature for generation.

#### Public API
- `String generate(AiGenerationRequest request) throws IOException`: Generates text with default settings.
- `String generate(AiGenerationRequest request, float temperatureOverride) throws IOException`: Generates text with specified temperature.

#### Dependencies
- `AiGenerationRequest`: Used for generating text.

#### Exceptions / Errors
- `IOException`: Thrown by the provider on failure.

#### Concurrency
- Not explicitly mentioned, but the interface is designed to be thread-safe as it is stateless.

### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:11:58Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides AI text generation for prompts using specified or default sampling parameters.

#### Purpose
- Pluggable AI backend for generating text based on prompts.

#### Type
- Interface: `AiGenerationProvider` extending `AutoCloseable`.

#### Input
- `AiGenerationRequest` (prompt key, source file, source text, current header).

#### Output
- Generated text (`String`); never `null`, but may be blank.

#### Core Logic
- `generate(AiGenerationRequest request)`: Generates text using default parameters.
- `generate(AiGenerationRequest request, float temperatureOverride)`: Overrides temperature for sampling.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text with default settings.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates text with specified temperature.

#### Dependencies
- `java.io.IOException`: For handling I/O exceptions.

#### Exceptions / Errors
- `IOException`: Thrown by underlying provider failures.

#### Concurrency
- Not explicitly noted; assumes thread safety based on interface design.

### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:38:52Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for generating text based on AI requests.

#### Purpose
- Defines a contract for generating text from AI requests.
- Supports both default and temperature-overridden generations.
- Ensures proper resource management with `AutoCloseable`.

#### Type
- Interface
- Extends `AutoCloseable`

#### Core logic
- `generate(AiGenerationRequest request)`: Generates text using default parameters.
- `generate(AiGenerationRequest request, float temperatureOverride)`: Generates text with a specified temperature override.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text using the provider's default sampling parameters.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates text with the specified temperature override.
- `close() -> void`: Closes the provider, ensuring resources are properly released.

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails.

#### Concurrency
- Not explicitly addressed in the provided source.

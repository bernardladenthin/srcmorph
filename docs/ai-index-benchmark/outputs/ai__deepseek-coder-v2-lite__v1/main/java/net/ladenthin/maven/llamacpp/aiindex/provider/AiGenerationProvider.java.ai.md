### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:15:55Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a pluggable AI backend for generating text based on specific requests.

#### Purpose
- To enable various AI backend providers to generate text based on request specifications.
- To allow implementations to be either local (using llama.cpp) or mock for testing purposes.

#### Type
- Interface (`public interface AiGenerationProvider extends AutoCloseable`)
- Extends: `AutoCloseable`

#### Input
- Constructor and method parameters: 
  - `AiGenerationRequest request` in `generate(AiGenerationRequest request) throws IOException`
  - `AiGenerationRequest request, float temperatureOverride` in `generate(final AiGenerationRequest request, final float temperatureOverride) throws IOException`

#### Output
- Return type: `String`
- Side effects: Throws `IOException` if the underlying provider fails.

#### Core logic
- **Primary Method**: 
  - Takes an `AiGenerationRequest` and generates text using the provider's default sampling parameters.
- **Default Method**: 
  - Allows overriding the temperature for specific generation calls, which can help in breaking EOS-early failure modes.

#### Public API
- `String generate(AiGenerationRequest request) throws IOException` -> Generates text using default sampling parameters.
- `default String generate(final AiGenerationRequest request, final float temperatureOverride) throws IOException` -> Generates text using a specified temperature override.

#### Dependencies
- Referenced types: `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`

#### Exceptions / Errors
- Throws `IOException` if the underlying provider fails.

#### Concurrency
- The interface extends `AutoCloseable`, suggesting that implementations should handle resource management and potential concurrency issues.

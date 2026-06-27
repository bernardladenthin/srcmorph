### AiGenerationProvider.java
- H: 1.0
- C: 450236CF
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:55:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides a pluggable interface for generating AI text from requests, supporting local models and test mocks.

#### Purpose
- Defines the contract for generating text from `AiGenerationRequest` objects.
- Supports both live backend implementations and mock providers for testing.

#### Type
Interface; extends `AutoCloseable`.

#### Input
- `AiGenerationRequest`: Contains prompt key, source file, source text, and current header.
- `float temperatureOverride`: Sampling temperature parameter (optional).

#### Output
- `String`: Generated text (never null, may be blank).
- Side effect: Resources released via `close()`.

#### Core logic
- Delegates `generate(AiGenerationRequest)` to underlying backend or mock implementation.
- Applies optional `temperatureOverride` if implemented; otherwise ignores it.
- Handles cleanup and error propagation via `AutoCloseable` contract.

#### Public API
- `generate(AiGenerationRequest request) -> String`: Generates text using default parameters.
- `generate(AiGenerationRequest request, float temperatureOverride) -> String`: Generates text with overridden temperature.
- `close()`: Releases resources.

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `java.io.IOException`

#### Exceptions / Errors
- Throws `IOException` if underlying provider fails or during closure.
- Returns blank string if model produces no tokens.

#### Concurrency
- Interface does not explicitly declare thread-safety; implementations must ensure safety.

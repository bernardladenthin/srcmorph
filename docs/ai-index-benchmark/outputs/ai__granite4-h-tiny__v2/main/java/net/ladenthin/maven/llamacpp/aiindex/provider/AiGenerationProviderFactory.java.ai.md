### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:34:39Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> A factory for selecting and instantiating an AI generation provider by name.

#### Purpose
- Factory class to instantiate `AiGenerationProvider` implementations based on a provider name.

#### Type
- Class: `AiGenerationProviderFactory` extends `Object`; implements no interfaces; no generics; `@ToString`.

#### Input
- Constructor: none.
- Method `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport)`.

#### Output
- Returns: `AiGenerationProvider` instance.
- Produces: `MockAiGenerationProvider` or `LlamaCppJniAiGenerationProvider` based on input.

#### Core Logic
- If `providerName` is null or blank, return `MockAiGenerationProvider`.
- If `providerName` is "mock", return `MockAiGenerationProvider`.
- If `providerName` is "llamacpp-jni", return `LlamaCppJniAiGenerationProvider`.
- Otherwise, throw `IllegalArgumentException`.

#### Public API
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`  
  Creates an AI generation provider based on the provider name.

#### Dependencies
- `Java8CompatibilityHelper`
- `LlamaCppJniConfig`
- `AiPromptSupport`

#### Exceptions / Errors
- Throws `IllegalArgumentException` for unsupported provider names.

#### Concurrency
- Not explicitly noted; assumes single-threaded usage.

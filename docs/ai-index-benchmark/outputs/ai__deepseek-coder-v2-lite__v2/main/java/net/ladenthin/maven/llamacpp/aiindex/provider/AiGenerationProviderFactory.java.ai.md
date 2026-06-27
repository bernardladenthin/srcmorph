### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:47:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an {@link AiGenerationProvider} implementation by name.

#### Purpose
- **Purpose**: Defines a factory for creating instances of `AiGenerationProvider` based on the provided name.

#### Type
- **Type**: Class (`public final class`), extends no other class, implements no interfaces.

#### Input
- **Input**: 
  - `providerName`: A string representing the provider key (e.g., "mock" or "llamacpp-jni").
  - `llamaConfig`: Configuration for the llama.cpp JNI provider.
  - `promptSupport`: Support for prompt lookup passed to providers that need it.

#### Output
- **Output**: 
  - Returns an instance of `AiGenerationProvider`.
  - Throws `IllegalArgumentException` if the provider name is not recognized.

#### Core logic
- **Core logic**:
  - Check if `providerName` is null or blank, and default to "mock" if true.
  - Use a switch statement to return the appropriate `AiGenerationProvider` based on the `providerName`.

#### Public API
- **Public API**:
  - `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`
    - **Purpose**: Creates an `AiGenerationProvider` for the given provider name.

#### Dependencies
- **Dependencies**:
  - `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
  - `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`
  - `net.ladenthin.maven.llamacpp.aiindex.provider.MockAiGenerationProvider`
  - `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniAiGenerationProvider`

#### Exceptions / Errors
- **Exceptions**:
  - Throws `IllegalArgumentException` if the provider name is not recognized.

#### Concurrency
- **Concurrency**: Not applicable (no threading or state mutations mentioned).

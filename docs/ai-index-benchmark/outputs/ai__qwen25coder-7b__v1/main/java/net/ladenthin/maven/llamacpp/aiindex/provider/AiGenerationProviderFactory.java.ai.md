### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:39:52Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Purpose
- Selects and instantiates an `AiGenerationProvider` based on the provided name.

#### Type
- Class: `public class AiGenerationProviderFactory`
- Modifiers: None
- Extends: None
- Implements: None
- Generics: None
- Annotations: `@ToString`

#### Input
- Constructor: No parameters
- Method: 
  - `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`
    - Parameters:
      - `providerName`: The name of the provider to create.
      - `llamaConfig`: Configuration for the llama.cpp JNI provider.
      - `promptSupport`: Support for prompt lookup passed to providers.

#### Output
- Return type: `AiGenerationProvider`
- Produced state: None
- Mutated fields: None
- Written resources: None
- Side effects: None

#### Core logic
- Checks if `providerName` is null or blank using `compatibilityHelper.isBlank(providerName)`.
- If `providerName` is null or blank, returns a new `MockAiGenerationProvider`.
- Switches on `providerName`:
  - If `"mock"`, returns a new `MockAiGenerationProvider`.
  - If `"llamacpp-jni"`, returns a new `LlamaCppJniAiGenerationProvider` with `llamaConfig` and `promptSupport`.
  - Throws an `IllegalArgumentException` if the provider name is not recognized.

#### Public API
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`: Creates an `AiGenerationProvider` based on the given provider name.

#### Dependencies
- Imports:
  - `lombok.ToString`
  - `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory`
  - `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
  - `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`

#### Exceptions / Errors
- Throws `IllegalArgumentException` if the provider name is not recognized.

#### Concurrency
- No concurrency or synchronization notes.

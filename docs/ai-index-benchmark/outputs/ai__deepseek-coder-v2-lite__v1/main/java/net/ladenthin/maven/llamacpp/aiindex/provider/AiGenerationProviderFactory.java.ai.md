### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:16:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an {@link AiGenerationProvider} implementation by name.

#### Purpose
- To provide a factory method for creating different types of {@link AiGenerationProvider} implementations based on the provided name.

#### Type
- Class (`public class AiGenerationProviderFactory`)
- Modifiers: `public`
- Extends: None
- Implements: None
- Key Generics or Type Bounds: None
- Notable Annotations: `@ToString` from Lombok

#### Input
- Parameters:
  - `providerName`: A string representing the provider key, which can be `"mock"` or `"llamacpp-jni"`.
  - `llamaConfig`: Configuration for the llama.cpp JNI provider.
  - `promptSupport`: Prompt lookup support passed to providers that need it.

#### Output
- Return Type: {@link AiGenerationProvider}
- Produced State: A newly-created provider instance based on the provided name.
- Mutated Fields: None
- Written Resources: None
- Side Effects: Throws an `IllegalArgumentException` if the provider name is not recognized.

#### Core logic
- **Default Provider**: If `providerName` is `null` or blank, return a {@link MockAiGenerationProvider}.
- **Switch Case**: Based on the `providerName`, instantiate and return the appropriate provider:
  - `"mock"`: Returns a {@link MockAiGenerationProvider}.
  - `"llamacpp-jni"`: Returns a {@link LlamaCppJniAiGenerationProvider} with the given configuration and prompt support.

#### Public API
- `create(String providerName, LlamaCppJniConfig llamaConfig, AiPromptSupport promptSupport) -> AiGenerationProvider`
  - Creates and returns an {@link AiGenerationProvider} instance based on the provided name.

#### Dependencies
- Imports:
  - `lombok.ToString`
  - `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
  - `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`

#### Exceptions / Errors
- Throws `IllegalArgumentException` if the provider name is not recognized.

#### Concurrency
- The class and its methods do not appear to handle concurrency explicitly, but it should be thread-safe given the immutability of the objects created and the absence of mutable state in the class itself.

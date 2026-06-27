### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:46:54Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an `AiGenerationProvider` implementation by name.

#### Purpose
- Instantiates `AiGenerationProvider` based on a given name.
- Handles default and specific provider cases.

#### Type
class public; implements none; extends none; key generics none; notable annotations: @ToString

#### Input
- `String providerName`: Provider key (mock/llamacpp-jni).
- `LlamaCppJniConfig llamaConfig`: Configuration for llama.cpp JNI provider.
- `AiPromptSupport promptSupport`: Prompt lookup support for providers needing it.

#### Output
- `AiGenerationProvider`: Newly-created provider instance.
- `IllegalArgumentException`: If `providerName` is unrecognized.

#### Core logic
1. Check if `providerName` is null or blank.
2. Switch on `providerName`:
   - Return `MockAiGenerationProvider` for "mock".
   - Return `LlamaCppJniAiGenerationProvider` for "llamacpp-jni".
   - Throw exception for unsupported providers.

#### Public API
- `create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider`: Creates and returns an AI generation provider based on the given name and configurations.

#### Dependencies
- `AiGenerationProvider`
- `MockAiGenerationProvider`
- `LlamaCppJniAiGenerationProvider`
- `Java8CompatibilityHelper`
- `LlamaCppJniConfig`
- `AiPromptSupport`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown for unsupported provider names.

#### Concurrency
No explicit concurrency considerations in the source.

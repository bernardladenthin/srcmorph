### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:16:52Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AiGenerationProvider implementation by name.

#### Purpose
- Creates instances of AiGenerationProvider based on a given name
- Handles provider selection logic

#### Type
- Class
- Public
- Annotated with @ToString

#### Input
- String providerName
- LlamaCppJniConfig llamaConfig
- AiPromptSupport promptSupport

#### Output
- AiGenerationProvider instance
- Throws IllegalArgumentException for unsupported providers

#### Core logic
- Checks if providerName is null or blank
- Uses switch statement to select appropriate provider implementation
- Returns new MockAiGenerationProvider for "mock" or null/blank input
- Returns new LlamaCppJniAiGenerationProvider for "llamacpp-jni"
- Throws exception for unsupported providers

#### Public API
- create(String, LlamaCppJniConfig, AiPromptSupport) -> AiGenerationProvider
  Selects and instantiates an AI generation provider based on the given name.

#### Dependencies
- lombok.ToString
- Java8CompatibilityHelper
- MockAiGenerationProvider
- LlamaCppJniAiGenerationProvider
- AiPromptSupport

#### Exceptions / Errors
- Throws IllegalArgumentException for unsupported providers
- Handles null or blank providerName by defaulting to mock provider

#### Concurrency
- Thread-safe due to immutable state and stateless factory methods

### AiGenerationProviderFactory.java
- H: 1.0
- C: 0ADDB961
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:17:29Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects and instantiates an AI generation provider implementation by name for use in a Maven plugin.

#### Purpose
- Instantiates AI generation providers based on configuration
- Provides mock fallback for testing and development

#### Type
class public final AiGenerationProviderFactory extends java.lang.Object

#### Input
- `providerName` String: identifies desired provider type
- `llamaConfig` LlamaCppJniConfig: configuration for llama.cpp JNI provider
- `promptSupport` AiPromptSupport: prompt lookup support for providers that need it

#### Output
- `AiGenerationProvider`: newly created instance based on provider name
- throws `IllegalArgumentException` for unrecognized provider names

#### Core logic
- Checks if provider name is null or blank, returns mock provider
- Uses switch statement to map provider names to concrete implementations
- Throws exception for unsupported provider names

#### Public API
create(providerName, llamaConfig, promptSupport) -> AiGenerationProvider: creates AI provider by name

#### Dependencies
Java8CompatibilityHelper, LlamaCppJniAiGenerationProvider, MockAiGenerationProvider, LlamaCppJniConfig, AiPromptSupport

#### Exceptions / Errors
IllegalArgumentException: thrown when providerName is not recognized

#### Concurrency
Not applicable

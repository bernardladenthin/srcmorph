### main/java/net/ladenthin/maven/llamacpp/aiindex/provider
- H: 1.0
- C: BA4DB05D
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:53:58Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [MockAiGenerationProvider.java](MockAiGenerationProvider.java.ai.md)
---
> This package provides a flexible AI generation provider framework, supporting multiple backends including local LLMs via JNI and mock implementations for testing.

#### Purpose
- Offers pluggable AI text generation providers
- Enables integration of various AI backends
- Facilitates testing through mock implementations

#### Responsibilities
- Provider selection and instantiation
- Interface definition for AI generation
- Handling of different provider types (mock, local LLMs)
- Resource management and lifecycle control

#### Key units
- `AiGenerationProvider`: Core interface for text generation providers
- `AiGenerationProviderFactory`: Factory for creating provider instances
- `LlamaCppJniAiGenerationProvider`: Local LLM implementation via JNI
- `MockAiGenerationProvider`: Mock implementation for testing

#### Data flow
AI generation requests flow through the factory to create appropriate providers, which then generate text based on the provided configuration and input parameters.

#### Dependencies
- Cross-unit collaborations: Provider implementations depend on shared interfaces and utility classes
- External modules: JNI bindings for local LLM integration

#### Cross-cutting
- Error handling: IOException thrown for various failure scenarios
- Configuration management: Immutable configuration objects for provider settings
- Concurrency: Potential for concurrent access to shared resources in multi-threaded environments

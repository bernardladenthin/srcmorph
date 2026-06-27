### main
- H: 1.0
- C: 0D4DE298
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:03:09Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
---
> This package provides AI field generation and text generation capabilities, supporting configuration through templates and models, and enabling multiple backend integrations including local LLMs and mock implementations.

#### Purpose
- Manage AI field generation configurations
- Offer pluggable AI text generation providers

#### Responsibilities
- Configure AI field generation via prompt templates and AI models
- Associate configurations with specific file extensions or use as a fallback
- Provide public API for configuration management
- Offer pluggable AI text generation providers
- Enable integration of various AI backends
- Facilitate testing through mock implementations

#### Key units
- `AiFieldGenerationConfig`: Manages individual configuration settings
- `AiFieldGenerationSelector`: Selects appropriate configuration based on file extensions
- `AiModelDefinition`: Defines AI model parameters and supports Maven plugin integration
- `AiGenerationProvider`: Core interface for text generation providers
- `AiGenerationProviderFactory`: Factory for creating provider instances
- `LlamaCppJniAiGenerationProvider`: Local LLM implementation via JNI
- `MockAiGenerationProvider`: Mock implementation for testing

#### Data flow
- AI field generation: Inputs move through `AiFieldGenerationSelector` to produce `AiFieldGenerationConfig`.
- Text generation: Requests flow through `AiGenerationProviderFactory` to instantiate appropriate providers, which generate text.

#### Dependencies
- `AiModelDefinition`, `AiPromptDefinition` (implied), `List`, `ArrayList`, `Collections` for data structures
- Cross-unit collaborations between provider implementations and shared interfaces/utility classes
- JNI bindings for local LLM integration

#### Cross-cutting
- Error handling for null configurations, extensions, and provider failures
- Mutable state management in `AiFieldGenerationConfig`
- Immutable access to stop strings list in `AiModelDefinition`
- Configuration management: Immutable configuration objects for provider settings
- Concurrency considerations for shared resources in multi-threaded environments

### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: 83E61DEB
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:26:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> This package provides AI integration capabilities for Maven builds, supporting configuration and field generation for various AI models.

#### Purpose
- Facilitates AI model integration in Maven projects
- Supports configuration and field generation for different AI models

#### Responsibilities
- AI model configuration management
- Field generation based on AI prompts
- Integration with Maven build processes
- Support for multiple AI provider implementations

#### Key units
- `AiFieldGenerationConfig`: Configures AI field generation for Maven plugin
- `AiGenerationConfig`: Manages AI generation parameters for Maven build integration
- `AiModelDefinition`: Defines AI model configurations for Maven plugin use
- `LlamaCppJniAiGenerationProvider`: Local GGUF model provider for AI text generation

#### Data flow
AI configurations are applied to source files, with the `AiFieldGenerationConfig` and `AiModelDefinition` driving the process. The `LlamaCppJniAiGenerationProvider` handles the actual text generation using local models.

#### Dependencies
- Java collections framework (List, Map)
- Lombok for annotations (ToString)
- JSpecify for nullability annotations
- llama.cpp JNI bindings for local model support

#### Cross-cutting
- Configuration management across multiple classes
- Error handling for null inputs and missing configurations
- Thread-safety considerations for mutable state in generation providers

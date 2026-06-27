### main/java
- H: 1.0
- C: E9AF7086
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:30:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [net/](net/package.ai.md)
---
> This package streamlines AI integration within Maven projects, enabling seamless configuration and field generation for various AI models.

#### Purpose
- Simplifies AI model integration in Maven builds
- Automates configuration and field generation for different AI models

#### Responsibilities
- Manages AI model configurations
- Generates fields based on AI prompts
- Integrates with Maven build processes
- Supports multiple AI provider implementations

#### Key units
- `AiFieldGenerationConfig`: Configures AI field generation for Maven plugin
- `AiGenerationConfig`: Manages AI generation parameters for Maven build integration
- `AiModelDefinition`: Defines AI model configurations for Maven plugin use
- `LlamaCppJniAiGenerationProvider`: Local GGUF model provider for AI text generation

#### Data flow
AI configurations are applied to source files, with `AiFieldGenerationConfig` and `AiModelDefinition` driving the process. The `LlamaCppJniAiGenerationProvider` handles the actual text generation using local models.

#### Dependencies
- Java collections framework (List, Map)
- Lombok for annotations (ToString)
- JSpecify for nullability annotations
- llama.cpp JNI bindings for local model support

#### Cross-cutting
- Configuration management across multiple classes
- Error handling for null inputs and missing configurations
- Thread-safety considerations for mutable state in generation providers

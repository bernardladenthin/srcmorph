### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:21:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiFieldGenerationConfig.java](AiFieldGenerationConfig.java.ai.md)
- F: [AiFieldGenerationSelector.java](AiFieldGenerationSelector.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiGenerationKind.java](AiGenerationKind.java.ai.md)
- F: [AiModelDefinition.java](AiModelDefinition.java.ai.md)
- F: [AiModelDefinitionSupport.java](AiModelDefinitionSupport.java.ai.md)
---
> This package provides AI configuration and field generation capabilities for Maven builds, facilitating integration with various AI models and generation settings.

#### Purpose
- Facilitates AI model configuration and field generation in Maven projects
- Supports integration with different AI models and generation parameters

#### Responsibilities
- Configuration management for AI models and generation settings
- Field generation based on AI prompts and model definitions
- Selection of appropriate configurations for source files

#### Key units
- `AiFieldGenerationConfig`: Configures AI field generation for Maven plugin
- `AiFieldGenerationSelector`: Selects AI field generation configuration based on file extensions
- `AiGenerationConfig`: Manages AI generation parameters for Maven build integration
- `AiGenerationKind`: Enumerates AI generation targets (per-file or aggregate)
- `AiModelDefinition`: Defines AI model configurations for Maven plugin use
- `AiModelDefinitionSupport`: Resolves AI model definitions to generation configurations

#### Data flow
AI field generation configurations are applied to source files based on their extensions. The `AiFieldGenerationSelector` matches these configurations to input files, which are then processed using the associated `AiGenerationConfig` and `AiModelDefinition`.

#### Dependencies
- Java collections framework (List, Map)
- Lombok for annotations (ToString)
- JSpecify for nullability annotations

#### Cross-cutting
- Configuration management across multiple classes
- Error handling for null inputs and missing configurations
- Thread-safety considerations for mutable state

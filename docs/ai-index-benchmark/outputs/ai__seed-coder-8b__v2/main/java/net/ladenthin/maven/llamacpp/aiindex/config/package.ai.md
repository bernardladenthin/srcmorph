### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:51:39Z
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
> Maven plugin configuration for AI field generation, associating prompt templates with AI model definitions for field generation across various file extensions.

#### Purpose
- Configure AI field generation via prompt templates and AI models
- Associate configurations with specific file extensions or use as a fallback

#### Responsibilities
- Manage AI field generation configurations
- Handle file extension-based selection of configurations
- Provide public API for configuration management

#### Key units
- `AiFieldGenerationConfig`: Manages individual configuration settings
- `AiFieldGenerationSelector`: Selects appropriate configuration based on file extensions
- `AiModelDefinition`: Defines AI model parameters and supports Maven plugin integration

#### Data flow
- Input: Source file name, prompt template key, AI model definition key, file extensions
- Process: Configuration selection based on file extensions
- Output: Matching `AiFieldGenerationConfig` or null

#### Dependencies
- `AiModelDefinition`
- `AiPromptDefinition` (implied)
- `List`, `ArrayList`, `Collections` (for data structures)

#### Cross-cutting
- Error handling for null configurations and extensions
- Mutable state management in `AiFieldGenerationConfig`
- Immutable access to stop strings list in `AiModelDefinition`

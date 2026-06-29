### main/java
- H: 1.0
- C: E9AF7086
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T20:06:45Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [net/](net/package.ai.md)
---
> Manages AI-related configurations and operations within a Maven plugin, including field generation, model definitions, and configuration support.

#### Purpose
- Handles the configuration and management of AI models and operations within a Maven project.
- Provides mechanisms for selecting and applying AI configurations based on file types.
- Facilitates the execution of AI operations such as generating fields for source files or packages.

#### Responsibilities
- **AI Configuration Management**: Defines and manages parameters for AI generation steps, including model settings and generation parameters.
- **Configuration Resolution**: Resolves and validates AI model definitions to generate ready-to-use configurations.
- **Field Generation Configuration**: Manages the configuration for associating prompt templates with AI models for field generation.
- **File Extension-based Selection**: Selects appropriate AI field generation settings based on file extensions.

#### Key Units
- **AiGenerationConfig**: Manages all parameters for an AI generation step, ensuring they are correctly configured and transported between layers.
- **AiModelDefinition**: Represents a configuration POJO for AI model definitions, storing parameters that can be used across multiple field-generation entries.
- **AiFieldGenerationConfig**: Holds configuration for associating prompt templates with AI models for field generation, used within a list in the plugin configuration.
- **AiFieldGenerationSelector**: Selects the appropriate AI field generation settings for source files based on their extensions, ensuring that the correct configurations are applied.

#### Data Flow
- Configuration data flows from the Maven plugin configuration to `AiModelDefinition` and `AiGenerationConfig`, where it is validated and stored.
- When a source file needs to be processed, the `AiFieldGenerationSelector` selects the appropriate `AiFieldGenerationConfig` based on the file's extension, and the `AiGenerationConfig` is used to execute the AI operations.

#### Dependencies
- **Internal Collaborations**:
  - `AiModelDefinitionSupport`: Provides a lookup table for AI model configurations and converts them into ready-to-use generation configurations.
  - `Java8CompatibilityHelper`: Ensures compatibility with Java 8 features.

#### Cross-cutting
- **Common Exception/Error Handling**: Handles `NullPointerException` and `IllegalArgumentException` for invalid configurations.

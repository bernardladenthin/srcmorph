### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T20:40:59Z
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
> Manages configuration and selection logic for AI field generation steps within Maven projects, integrating with AI model definitions and providing mechanisms to generate summaries for individual files or entire packages.

#### Responsibilities
- **Configuration Management**: Holds and manages configuration parameters for AI generation steps.
- **Selection Logic**: Selects appropriate AI field generation configurations based on file extensions.
- **Model Definition Support**: Resolves AI model definitions to their corresponding generation configurations.

#### Key Units
- `AiFieldGenerationConfig`: Represents configuration for a single AI field generation step.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration for a given source file.
- `AiGenerationConfig`: Manages parameters for a single AI generation step.
- `AiModelDefinition`: Holds the configuration for an AI model definition.
- `AiModelDefinitionSupport`: Resolves AI model definitions to their corresponding generation configurations.

#### Data Flow
1. **Input**: `AiFieldGenerationConfig` and `AiModelDefinition` instances are configured.
2. **Processing**:
   - `AiFieldGenerationSelector` iterates over `AiFieldGenerationConfig` entries to select the most appropriate configuration based on file extensions.
   - `AiModelDefinitionSupport` resolves an `AiModelDefinition` key to an `AiGenerationConfig`.
3. **Output**: Selected `AiFieldGenerationConfig` and resolved `AiGenerationConfig`.

#### Dependencies
- **Internal Collaborations**:
  - `AiFieldGenerationConfig` depends on `AiPromptDefinition`.
  - `AiFieldGenerationSelector` depends on `AiFieldGenerationConfig`.
  - `AiModelDefinitionSupport` depends on `AiModelDefinition` and `AiGenerationConfig`.
- **External Modules**: None explicitly mentioned.

#### Cross-cutting
- **Configuration Management**: Uses Lombok for JavaBean design and easy configuration management.
- **Null Handling**: Handles null entries gracefully using Lombok and manual checks.

### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: 83E61DEB
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T20:49:54Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Manages configuration and selection logic for AI field generation steps within Maven projects, integrating with AI model definitions and providing mechanisms to generate summaries for individual files or entire packages.

#### Purpose
- Configures and selects AI generation parameters based on file types.
- Integrates with AI model definitions to produce summaries.

#### Responsibilities
- **Configuration Management**: Holds and manages configuration parameters for AI generation steps.
- **Selection Logic**: Selects appropriate AI field generation configurations based on file extensions.
- **Model Definition Support**: Resolves AI model definitions to their corresponding generation configurations.

#### Key Units
- `AiFieldGenerationConfig`: Manages configuration for a single AI field generation step.
- `AiFieldGenerationSelector`: Chooses the most suitable AI field generation config based on file types.
- `AiGenerationConfig`: Stores parameters for a single AI generation step.
- `AiModelDefinition`: Contains the configuration for an AI model definition.
- `AiModelDefinitionSupport`: Resolves AI model definitions to their corresponding generation configs.

#### Data Flow
1. **Input**: `AiFieldGenerationConfig` and `AiModelDefinition` instances are configured.
2. **Processing**:
   - `AiFieldGenerationSelector` iterates over `AiFieldGenerationConfig` entries to select the most appropriate config based on file extensions.
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

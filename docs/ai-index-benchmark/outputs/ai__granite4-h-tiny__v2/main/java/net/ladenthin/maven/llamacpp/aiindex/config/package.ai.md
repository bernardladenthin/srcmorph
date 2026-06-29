### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:36:36Z
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
> This package provides a comprehensive configuration and generation framework for AI field generation in Maven plugins, facilitating the association of prompt templates with AI model definitions and the selection of appropriate configurations based on source file extensions.

#### Purpose
- Config POJO for associating prompt templates with AI model definitions.
- Chooses the correct AI field generation configuration for a given source file based on file extensions.
- Defines mutable configuration object for AI generation parameters.
- Identifies whether an AI generation operates on a single source file or a whole package.
- Represents AI model configuration for Maven plugin.
- Provides a lookup table for AI model definitions.

#### Responsibilities
- `AiFieldGenerationConfig`: Config POJO for associating prompt templates with AI model definitions.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration based on a source file's extensions.
- `AiGenerationConfig`: Mutable configuration object for AI generation parameters.
- `AiGenerationKind`: Enum for AI generation kind: single file or package summary.
- `AiModelDefinition`: Maven plugin configuration POJO that pairs a lookup key with a complete set of AI model parameters.
- `AiModelDefinitionSupport`: Resolves AI model definitions by key to generate AI generation configurations.

#### Key units
- `AiFieldGenerationConfig`: Config POJO for associating prompt templates with AI model definitions.
- `AiFieldGenerationSelector`: Chooses the correct AI field generation configuration for a given source file based on file extensions.
- `AiGenerationConfig`: Mutable configuration object for AI generation parameters.
- `AiGenerationKind`: Enum for AI generation kind: single file or package summary.
- `AiModelDefinition`: Represents AI model configuration for Maven plugin.
- `AiModelDefinitionSupport`: Resolves AI model definitions by key to generate AI generation configurations.

#### Data flow
- `AiFieldGenerationConfig` is instantiated and configured with prompt keys, AI model definition keys, and file extensions.
- `AiFieldGenerationSelector` selects the appropriate `AiFieldGenerationConfig` based on source file extensions.
- `AiGenerationConfig` is used to configure AI generation parameters.
- `AiGenerationKind` identifies the type of AI generation.
- `AiModelDefinition` is used to configure AI model parameters.
- `AiModelDefinitionSupport` resolves `AiModelDefinition` entries to `AiGenerationConfig` objects.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `HashMap`
- `Objects`
- `Java8CompatibilityHelper`

#### Cross-cutting
- Immutable configuration objects for thread safety.
- Default values for mutable configurations.
- Error handling for missing keys and null inputs.

#### Summary
This package offers a robust framework for AI field generation in Maven plugins, providing configuration POJOs, selection mechanisms, mutable generation configurations, and a lookup system for AI model definitions, all designed to streamline the integration and customization of AI functionalities within Maven projects.

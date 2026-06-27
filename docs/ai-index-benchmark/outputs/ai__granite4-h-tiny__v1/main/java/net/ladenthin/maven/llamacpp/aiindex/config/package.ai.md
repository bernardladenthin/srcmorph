### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:14:16Z
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
> Calculates VAT for invoices

#### Purpose
- Provides configuration and selection mechanisms for AI field generation based on file extensions and model definitions.

#### Responsibilities
- `AiFieldGenerationConfig`: Associates prompt and AI model definition keys with file extensions for field generation.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration based on source file extensions.
- `AiGenerationConfig`: Configures AI generation parameters for Maven-based AI model interactions.
- `AiGenerationKind`: Identifies whether AI generation operates on a single source file or a whole package.
- `AiModelDefinition`: Provides a configuration POJO for pairing a lookup key with AI model parameters.
- `AiModelDefinitionSupport`: Resolves AI model definition entries by their key, returning the corresponding AI generation configuration.

#### Key Units
- `AiFieldGenerationConfig`: Associates prompt template keys with AI model definition keys for field generation.
- `AiFieldGenerationSelector`: Selects the appropriate AI field generation configuration based on source file extensions.
- `AiGenerationConfig`: Carries parameters between Maven configuration and AI provider implementations.
- `AiGenerationKind`: Identifies AI generation operation scope: single file or package.
- `AiModelDefinition`: Provides a configuration POJO for pairing a lookup key with AI model parameters.
- `AiModelDefinitionSupport`: Resolves AI model definition entries by their key, returning the corresponding AI generation configuration.

#### Data Flow
- `AiFieldGenerationConfig` is instantiated with prompt and AI model definition keys and file extensions.
- `AiFieldGenerationSelector` iterates over configurations to select one based on source file extensions.
- `AiGenerationConfig` is used to configure AI generation parameters.
- `AiGenerationKind` determines the scope of AI generation (single file or package).
- `AiModelDefinition` provides configuration parameters for AI model interactions.
- `AiModelDefinitionSupport` resolves AI model definition entries by their key, returning the corresponding AI generation configuration.

#### Dependencies
- `AiFieldGenerationConfig` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiFieldGenerationSelector` imports `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiGenerationConfig` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiGenerationKind` has no imports.
- `AiModelDefinition` imports `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`, `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`.
- `AiModelDefinitionSupport` imports `java.util.HashMap`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`, `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`.

#### Cross-cutting
- No recurring patterns across files noted.

#### Summary
This package provides a comprehensive set of classes and configurations for calculating VAT for invoices through AI field generation, selection, and AI model interactions, with clear dependencies and responsibilities structured around specific functional roles.

### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:20:17Z
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
> Calculates VAT for invoices by configuring AI-driven field generation in a Maven plugin for code assistance.

#### Purpose
- **Configure AI-driven field generation** for specific file types and prompts.
- **Associate prompts with AI model definitions** for generating content based on specified criteria.

#### Responsibilities
- **AiFieldGenerationConfig**: Manages configuration for AI-driven field generation, including prompt keys and AI model definitions.
- **AiFieldGenerationSelector**: Selects the appropriate `AiFieldGenerationConfig` for a given source file based on its file extensions.
- **AiGenerationConfig**: Provides a mutable configuration object for AI generation parameters, supporting default values and access through setters and getters.
- **AiGenerationKind**: Identifies whether an AI generation operates on a single source file or a whole package.
- **AiModelDefinition**: Defines and manages AI model configurations for use in a Maven plugin, allowing customization of parameters such as context size, number of threads, and more.
- **AiModelDefinitionSupport**: Resolves AI model definitions by their key, returning the corresponding generation configurations.

#### Key units
- **AiFieldGenerationConfig**: Manages configuration for AI-driven field generation.
- **AiFieldGenerationSelector**: Selects appropriate configurations based on file extensions.
- **AiGenerationConfig**: Mutable configuration object for AI generation parameters.
- **AiGenerationKind**: Enumerates the scope of AI generation (single file or package).
- **AiModelDefinition**: Defines and manages AI model configurations.
- **AiModelDefinitionSupport**: Resolves AI model definitions by key.

#### Data flow
Inputs are typically passed through constructors, setters, and getters to configure and retrieve parameters for AI field generation and model management.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Cross-cutting
- **Shared configuration parameters**: Many classes share configuration parameters such as model paths, context sizes, and more.
- **Exception handling**: No exceptions are explicitly thrown in the provided summaries, but null values are handled with annotations.
- `java.util.List` is frequently used for collections of configurations or extensions.

#### EOF

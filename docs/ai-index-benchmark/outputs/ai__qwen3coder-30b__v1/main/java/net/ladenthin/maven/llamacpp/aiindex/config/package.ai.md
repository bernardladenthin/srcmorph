### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:54:02Z
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
> Configures and manages AI-driven code generation workflows for Maven plugins, enabling field-level analysis and summarization of source files using customizable model parameters and prompt templates.

#### Purpose
- Defines AI model configurations and generation parameters for Maven plugin execution.
- Supports declarative setup of AI inference settings with fallback behaviors.

#### Responsibilities
- **Configuration Management**: Handles AI model definitions, generation configs, and field generation rules.
- **File Selection Logic**: Matches source files to appropriate AI processing configurations based on extensions.
- **Inference Parameters**: Manages mutable settings for context size, sampling, retries, and input limits used in llama.cpp inference.

#### Key units
- AiFieldGenerationConfig: Maps prompt templates to model definitions with file extension filters.
- AiFieldGenerationSelector: Selects appropriate config by file name using extension matching and fallback rules.
- AiGenerationConfig: Carries mutable AI inference parameters like context size, temperature, and stop strings.
- AiGenerationKind: Enum distinguishing FILE_SUMMARY and PACKAGE_SUMMARY processing modes.
- AiModelDefinition: Reusable model configuration with key-based lookup support.
- AiModelDefinitionSupport: Resolves model definitions into generation configs with validation.

#### Data flow
- Maven plugin reads AiFieldGenerationConfig entries to determine which prompt/template to apply per file.
- AiFieldGenerationSelector matches input file names against configured extensions and selects the appropriate config.
- Selected config references an AiModelDefinition through aiDefinitionKey, which is resolved via AiModelDefinitionSupport into a concrete AiGenerationConfig.
- AiGenerationConfig carries inference parameters used by the llama.cpp backend for AI processing.

#### Dependencies
- Internal: AiFieldGenerationConfig, AiFieldGenerationSelector, AiGenerationConfig, AiGenerationKind, AiModelDefinition, AiModelDefinitionSupport
- External: Lombok (@ToString), org.jspecify.annotations.Nullable, java.util packages

#### Cross-cutting
- Defensive copying in setters ensures immutability of returned collections.
- Null handling via @Nullable annotations and explicit null checks.
- Thread safety considerations: classes are not thread-safe; intended for single-threaded Maven plugin use only.
- Configuration validation at construction time (e.g., null keys in AiModelDefinitionSupport).

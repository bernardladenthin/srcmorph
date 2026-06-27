### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T23:20:25Z
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
> Configures and orchestrates AI-driven code generation tasks within Maven plugins, managing model definitions, prompt templates, and generation parameters for targeted file processing.

#### Purpose
- Defines reusable AI model configurations and generation parameters for Maven plugin execution
- Links prompt templates with AI model settings for field-level code analysis

#### Responsibilities
- AI model configuration management: AiModelDefinition, AiGenerationConfig
- Field generation setup: AiFieldGenerationConfig, AiFieldGenerationSelector
- Generation scope control: AiGenerationKind
- Model resolution and parameter mapping: AiModelDefinitionSupport

#### Key units
AiFieldGenerationConfig manages prompt-template-to-model mappings with file extension filtering
AiFieldGenerationSelector matches source files to appropriate AI configurations by extension
AiGenerationConfig holds mutable inference parameters for GGUF models including context, sampling, and retry logic
AiModelDefinitionSupport resolves model definitions by key to provide configured generation settings
AiGenerationKind enumerates processing scopes for AI tools: FILE_SUMMARY and PACKAGE_SUMMARY

#### Data flow
Configuration objects are constructed from Maven plugin inputs, validated for required keys, then mapped to generation tasks; file names are matched against extension rules to select appropriate field generation configurations; these configs are used to instantiate AiGenerationConfig objects with model-specific parameters

#### Dependencies
AiModelDefinition collaborates with AiGenerationConfig to provide reusable model settings
AiFieldGenerationConfig depends on AiModelDefinition and AiPromptDefinition for linking templates to models
AiModelDefinitionSupport relies on AiModelDefinition and AiGenerationConfig for resolution and mapping
AiFieldGenerationSelector uses AiFieldGenerationConfig for matching file extensions to configurations

#### Cross-cutting
Shared null handling via @Nullable annotations and defensive copying of mutable collections
Immutable view patterns for file extension lists and stop strings
Configuration validation through required key checks in AiModelDefinitionSupport
Default value management from AiGenerationConfig constants in AiModelDefinition

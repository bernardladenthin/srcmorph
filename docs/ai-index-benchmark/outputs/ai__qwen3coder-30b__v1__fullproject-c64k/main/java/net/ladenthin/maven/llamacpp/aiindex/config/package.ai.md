### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: D29D30C8
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:10:35Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiFieldGenerationConfig.java](AiFieldGenerationConfig.java.ai.md)
- F: [AiFieldGenerationSelector.java](AiFieldGenerationSelector.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiGenerationKind.java](AiGenerationKind.java.ai.md)
- F: [AiModelDefinition.java](AiModelDefinition.java.ai.md)
- F: [AiModelDefinitionSupport.java](AiModelDefinitionSupport.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Configures and manages AI-driven code indexing and generation for Maven projects using model definitions, prompt templates, and generation parameters.

#### Purpose
- Defines AI model configurations and generation parameters for Maven plugin execution.
- Supports field-level code generation based on file extensions and prompt templates.

#### Responsibilities
- AI configuration management: encapsulates settings for AI inference including context size, sampling, and retry logic.
- Prompt and model mapping: links prompt templates to AI model definitions for targeted field generation.
- File extension filtering: selects appropriate configurations based on source file types.
- Model lookup and validation: resolves model definitions by key with build-time checks.

#### Key units
- AiFieldGenerationConfig: maps prompt templates to model definitions and filters files by extension.
- AiFieldGenerationSelector: matches source files to field generation configurations using extension-based logic.
- AiGenerationConfig: carries AI inference parameters such as context size, temperature, and retry settings.
- AiGenerationKind: distinguishes between file-level and package-level AI processing scopes.
- AiModelDefinition: stores reusable AI model parameters for Maven plugin use.
- AiModelDefinitionSupport: resolves model definitions into ready-to-use generation configurations with key-based lookup.

#### Data flow
- Input files are matched to AiFieldGenerationConfig entries by extension.
- Matching configs link to AiModelDefinition instances via keys.
- AiModelDefinitionSupport resolves these definitions into AiGenerationConfig objects.
- These configurations drive AI inference parameters for code analysis or generation tasks.

#### Dependencies
- java.util.ArrayList, Collections, List
- lombok.ToString, @SuppressWarnings
- org.jspecify.annotations.Nullable
- AiGenerationConfig, AiModelDefinition
- Java8CompatibilityHelper

#### Cross-cutting
- Shared use of Lombok annotations for boilerplate reduction.
- Null safety via @Nullable annotations and defensive copying in collections.
- Immutable access patterns for configuration lists like stopStrings.
- Thread-safety considerations: all classes are mutable and not thread-safe; intended for single-threaded Maven contexts.

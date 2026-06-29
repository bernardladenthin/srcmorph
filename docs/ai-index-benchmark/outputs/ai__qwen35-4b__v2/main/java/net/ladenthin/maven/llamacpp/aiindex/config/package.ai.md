### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T19:00:38Z
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
> Executes AI-driven field generation for Maven plugin builds by mapping source file extensions to prompt templates and model configurations.

#### Purpose
- Orchestrates Llama.cpp-based code analysis by resolving file extensions to specific AI generation prompts and model parameters.
- Manages configuration lifecycles for single-field steps, distinguishing between file-level and package-level generation scopes.

#### Responsibilities
- **Configuration Management**: Stores and validates prompt keys, model definitions, and extension filters within `AiFieldGenerationConfig`.
- **Selector Logic**: Maps source file names to appropriate generation configurations via `AiFieldGenerationSelector`, handling fallbacks for unlisted extensions.
- **Parameter Resolution**: Converts high-level `AiModelDefinition` entries into executable inference parameters via `AiModelDefinitionSupport`.
- **Scope Definition**: Enumerates generation granularity modes (`FILE_SUMMARY`, `PACKAGE_SUMMARY`) to control output scope.

#### Key units
- `AiFieldGenerationConfig`: Links prompt templates and model definitions to specific file extension filters.
- `AiFieldGenerationSelector`: Selects the correct generation config based on source file extension or fallback rules.
- `AiGenerationConfig`: Holds mutable runtime parameters for inference (context size, temperature, threads).
- `AiModelDefinition`: Defines reusable model settings keyed by unique identifiers for plugin reflection.
- `AiModelDefinitionSupport`: Resolves definition keys to active generation configurations via a lookup map.
- `AiGenerationKind`: Enumerates operation scopes (`FILE_SUMMARY`, `PACKAGE_SUMMARY`).

#### Data flow
- User POM configuration defines `AiFieldGenerationConfig` entries mapping extensions to prompts/models.
- `AiFieldGenerationSelector` iterates these configs during build execution to match incoming source files.
- Matching files trigger generation calls using parameters resolved from `AiModelDefinition` via `AiModelDefinitionSupport`.
- Output is determined by the scope defined in `AiGenerationKind` (file vs package).

#### Dependencies
- Internal: `AiPromptDefinition`, `net.ladenthin.llama.parameters.ModelParameters`, `Java8CompatibilityHelper`.
- External: Maven Plugin reflection framework, Llama.cpp native bindings (via model paths), standard Java collections.

#### Cross-cutting
- **Immutable Views**: All collection outputs (`getFileExtensions()`, `getStopStrings()`) return unmodifiable lists to prevent external mutation.
- **Defensive Copying**: List modifications (e.g., `setFileExtensions`) create defensive copies before assignment.
- **Null Safety**: Selectors and configs handle null entries in configuration lists gracefully by skipping or falling back.
- **Thread-Safety**: Configuration objects are mutable but managed by the single-threaded Maven Mojo lifecycle; no explicit locking required.

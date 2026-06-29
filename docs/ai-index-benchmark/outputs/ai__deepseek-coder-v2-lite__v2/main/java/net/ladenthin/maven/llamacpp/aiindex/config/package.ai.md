### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:51:12Z
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
> This package provides a comprehensive set of tools for configuring and managing AI field generation in a Maven plugin, ensuring flexibility and reusability of AI model configurations across various file types and goals.

#### Purpose
- **Flexibility**: Allows for the association of prompt templates with specific AI models tailored for different file types.
- **Reusability**: Enables the reuse of AI model configurations across multiple field generation entries and Maven plugin goals.

#### Responsibilities
- **Configuration Management**: Defines and manages AI model parameters in a mutable POJO format, suitable for configuration via Maven settings or programmatically.
- **Lookup Mechanism**: Provides a lookup table to convert AI model definitions into ready-to-use generation configurations, ensuring efficient access and error handling for missing keys.

#### Key units
- **AiFieldGenerationConfig**: A mutable POJO for configuring field generation with prompt templates and AI models, associated with specific file extensions.
- **AiFieldGenerationSelector**: A utility class for selecting the appropriate `AiFieldGenerationConfig` based on the file extension of a source file.
- **AiGenerationConfig**: A configuration object encapsulating various parameters required for AI generation, including model path, context size, and more.
- **AiModelDefinition**: Defines constants for specifying the kind of AI generation, such as FILE_SUMMARY or PACKAGE_SUMMARY.
- **AiModelDefinitionSupport**: A support class providing a lookup mechanism for converting `AiModelDefinition` entries into `AiGenerationConfig` instances.

#### Data flow
- Inputs to the package include various configuration parameters and a list of `AiModelDefinition` entries.
- The data flows through internal mechanisms to populate a map of `AiGenerationConfig` instances, which are then used by the `AiFieldGenerationSelector` to determine the appropriate configuration for a given file.

#### Dependencies
- **Internal Collaborations**: The package depends on other units within the same package for configuration and lookup purposes.
- **External Modules**: No external modules are explicitly listed as dependencies, focusing on internal collaborations.

#### Cross-cutting
- **Shared Base Types**: Utilizes Java's base types and collections for data handling.
- **Common Patterns**: Emphasizes the use of mutable POJOs and lookup tables to manage configurations efficiently.
- **Error Handling**: Implements exception handling for null keys and missing definitions, ensuring robust operation.

#### Cross-cutting
- **Thread Safety**: The package does not inherently handle thread safety, relying on external frameworks to ensure thread-safe operations when multiple instances are created and manipulated concurrently.

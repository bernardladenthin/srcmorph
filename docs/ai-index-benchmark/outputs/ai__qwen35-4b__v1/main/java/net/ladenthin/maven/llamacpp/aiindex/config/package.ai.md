### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: 7C13CB6A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:23:44Z
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
> Provides Llama.cpp inference configuration and field generation selection logic for Maven plugins, mapping file extensions to AI models while managing context windows, sampling parameters, and retry policies.

#### Purpose
*   Defines POJOs for configuring AI model paths, sampling algorithms (temperature, top-p), and inference limits for Java source code processing.
*   Implements a selector mechanism to dynamically assign AI generation rules based on source file extensions or fallback to global defaults.

#### Responsibilities
*   **AI Generation Configuration**: Manages mutable settings for context size, token limits, threading, and sampling parameters (temperature, top-p, top-k) within `AiGenerationConfig` and `AiModelDefinition`.
*   **Field Generation Selection**: Filters and selects specific AI prompt templates based on source file extensions using `AiFieldGenerationSelector`.
*   **Definition Resolution**: Converts Maven POM `AiModelDefinition` entries into executable `AiGenerationConfig` instances via `AiModelDefinitionSupport`.

#### Key units
*   `AiGenerationConfig`: Holds 25+ constants and 16 mutable fields for model paths, context sizes, sampling rates, and retry policies.
*   `AiModelDefinition`: Defines reusable parameters including GGUF model paths, token limits, and stop strings with a unique lookup key.
*   `AiFieldGenerationConfig`: Maps prompt keys to model definitions and optional file extension filters (e.g., `.java`).
*   `AiFieldGenerationSelector`: Iterates through configs to select the appropriate generation rule for a given source filename.
*   `AiModelDefinitionSupport`: Validates definition keys and resolves `AiModelDefinition` entries into ready-to-use `AiGenerationConfig` objects.
*   `AiGenerationKind`: Enumerates scope types (`FILE_SUMMARY`, `PACKAGE_SUMMARY`) for generation operations.

#### Data flow
*   Maven POM configuration provides a list of `AiModelDefinition` objects.
*   `AiModelDefinitionSupport` validates these definitions and populates an internal map, converting each to a new `AiGenerationConfig`.
*   The plugin framework queries `AiModelDefinitionSupport.getConfig()` using a specific key to retrieve active inference parameters.
*   `AiFieldGenerationSelector` receives the list of `AiFieldGenerationConfig` rules and iterates them against a source file name.
*   If a file extension matches a rule, that rule's prompt and model definition are applied; otherwise, a fallback rule or null is returned.

#### Dependencies
*   **Internal**: `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFieldGenerationSelector`, `AiModelDefinitionSupport`.
*   **External Modules/Types**: `net.ladenthin.llama.parameters.ModelParameters`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
*   **Java Standard Libs**: `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.HashMap`, `java.util.List`, `java.util.Map`.

#### Cross-cutting
*   **Immutability Patterns**: Public getters for lists (e.g., `getStopStrings()`) return unmodifiable views to prevent runtime mutation, while internal state remains mutable for configuration updates.
*   **Null Handling**: Default constructors rely on static constants; null inputs in setters often trigger graceful resets (e.g., empty lists) or allow null fallbacks rather than throwing exceptions immediately.
*   **Thread Safety**: Configuration objects are generally designed for single-instance use within the plugin lifecycle, though `AiFieldGenerationSelector` is stateless and thread-safe. Concurrent access to mutable fields requires external synchronization outside these classes.
*   **Error Strategy**: Fail-fast validation on definition keys during construction (`NullPointerException`), with runtime errors primarily manifested as missing key lookups (`IllegalArgumentException`).

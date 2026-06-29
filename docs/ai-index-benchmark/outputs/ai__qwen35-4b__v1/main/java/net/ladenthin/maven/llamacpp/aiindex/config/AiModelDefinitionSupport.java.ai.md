### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:15:37Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to generate ready-to-use configuration objects for plugin goals.

#### Purpose
*   Converts `AiModelDefinition` entries from Maven POM configuration into `AiGenerationConfig` instances.
*   Enforces strict contract validation on definition keys to fail fast at build time.

#### Type
`public final class AiModelDefinitionSupport` extends `Object`. Annotations: `@ToString`. Fields: `private final Map<String, AiGenerationConfig> configs`.

#### Input
*   Constructor parameter: `List<AiModelDefinition> definitions` (may be null or empty).
*   Internal state: Presized `HashMap` populated from input list.

#### Output
*   Method return: `AiGenerationConfig` object for a given key via `getConfig()`.
*   Side effects: Mutates internal `configs` map during construction; creates new config objects in `toConfig()`.

#### Core logic
*   Validates every `AiModelDefinition` entry has a non-null `key`, throwing `NullPointerException` with index details on failure.
*   Presizes internal HashMap capacity to optimize insertion performance.
*   Iterates definitions, calls `toConfig()` for each, storing result in key-value map.
*   `toConfig()` clones all parameters (model path, context size, tokens, temperature, etc.) into a new `AiGenerationConfig`.
*   Lookup retrieves config via key; throws `IllegalArgumentException` if key missing to ensure eager error detection.

#### Public API
*   `AiModelDefinitionSupport(List<AiModelDefinition>) -> void`: Constructs lookup table from definitions.
*   `getConfig(String) -> AiGenerationConfig`: Retrieves configuration by definition key.

#### Dependencies
`java.util.HashMap`, `java.util.List`, `java.util.Map`, `java.util.Objects`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`. Referenced types: `AiModelDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
*   Throws `NullPointerException` if any definition key is null during construction.
*   Throws `IllegalArgumentException` if requested key is not found in the map.
*   Null definitions list or empty list handled gracefully (initialized with zero capacity).

#### Concurrency
*   Thread-safe via immutable internal state; `configs` map populated once in constructor and never modified after creation.

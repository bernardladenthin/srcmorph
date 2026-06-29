### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:53:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key into ready-to-use generation configurations for Maven Mojo execution.

#### Purpose
*   Converts `AiModelDefinition` entries from plugin configuration into executable `AiGenerationConfig` objects.
*   Enforces strict validation that every definition key is non-null to fail fast at build time.

#### Type
public final class AiModelDefinitionSupport; extends Object; implements none; annotated with @ToString; uses Lombok.

#### Input
Constructor parameter: `List<AiModelDefinition> definitions` (may be null or empty).

#### Output
Returns `AiGenerationConfig` objects for lookup by key; constructs new instances via internal conversion logic.

#### Core logic
*   Validates that all `AiModelDefinition` entries in the input list have non-null keys, throwing `NullPointerException` if not.
*   Presizes an internal `HashMap` to avoid rehashing during population.
*   Iterates through definitions, extracting values via `toConfig()` which clones fields into a new `AiGenerationConfig`.
*   Performs key-based lookup in the map, throwing `IllegalArgumentException` if a requested key is missing.

#### Public API
`AiModelDefinitionSupport(List<AiModelDefinition> definitions) -> void` - Constructs lookup table from definitions.
`AiGenerationConfig getConfig(String key) -> AiGenerationConfig` - Retrieves config for a specific model key.

#### Dependencies
`AiModelDefinition`, `AiGenerationConfig`, `Java8CompatibilityHelper`, `HashMap`, `List`, `Map`, `Objects`.

#### Exceptions / Errors
*   Throws `NullPointerException` if any definition entry has a null key (contract enforcement).
*   Throws `IllegalArgumentException` with message "Missing AI model definition for key: <key>" if lookup fails.
*   Null input list is handled gracefully, resulting in an empty map.

#### Concurrency
No explicit concurrency logic; uses mutable HashMap populated in constructor (thread-safety not guaranteed during construction or concurrent access).

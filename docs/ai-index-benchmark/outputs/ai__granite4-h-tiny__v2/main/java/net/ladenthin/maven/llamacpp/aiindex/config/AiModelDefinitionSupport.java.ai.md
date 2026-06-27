### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:33:21Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to generate AI generation configurations.

#### Purpose
- Provides a lookup table for AI model definitions.
- Converts `AiModelDefinition` entries to `AiGenerationConfig` objects.

#### Type
- Class + final + lombok.ToString

#### Input
- List of `AiModelDefinition` objects
- Java8CompatibilityHelper dependency

#### Output
- Map of `String` keys to `AiGenerationConfig` objects
- Throws `NullPointerException` for null keys
- Throws `IllegalArgumentException` for missing keys

#### Core logic
- Constructs a map of `AiGenerationConfig` objects from `AiModelDefinition` entries
- Ensures each `AiModelDefinition` has a non-null `key`
- Converts `AiModelDefinition` fields to `AiGenerationConfig` fields

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition>)`: Builds the configuration map
- `AiGenerationConfig getConfig(String)`: Retrieves config by key

#### Dependencies
- `HashMap`
- `Objects`
- `Java8CompatibilityHelper`

#### Exceptions / Errors
- `NullPointerException` for null keys
- `IllegalArgumentException` for missing keys

#### Concurrency
- Thread-safe due to immutable `HashMap` usage

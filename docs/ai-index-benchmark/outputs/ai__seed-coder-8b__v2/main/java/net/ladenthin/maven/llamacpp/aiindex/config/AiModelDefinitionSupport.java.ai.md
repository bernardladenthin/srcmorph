### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:43:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Manages AI model definitions, converting them to generation configurations for use in Maven plugin configurations.

#### Purpose
- Resolves `AiModelDefinition` entries into `AiGenerationConfig` objects
- Enforces non-null keys in configuration
- Provides eager error detection for missing definitions

#### Type
class final + @ToString

#### Input
- List of `AiModelDefinition` objects (may be null or empty)
- Individual `AiModelDefinition` entries must have non-null keys

#### Output
- Map of `String` to `AiGenerationConfig`
- `AiGenerationConfig` objects populated from `AiModelDefinition` fields

#### Core logic
1. Initialize config map with optimized capacity
2. Iterate through definitions, validate keys, and populate config map
3. Convert `AiModelDefinition` to `AiGenerationConfig` using field values
4. Provide lookup method for generation configs by key

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition>)` -> Constructs the support object
- `getConfig(String)` -> Returns the corresponding `AiGenerationConfig` for a given key

#### Dependencies
- `HashMap`
- `List`
- `Objects`
- `Java8CompatibilityHelper`
- `AiModelDefinition`
- `AiGenerationConfig`

#### Exceptions / Errors
- `NullPointerException` for null keys in definitions
- `IllegalArgumentException` for missing definition lookups

#### Concurrency
- Thread-safe due to immutable state after construction

### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:35:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key, converting them to ready-to-use generation configurations.

#### Purpose
- Provides a lookup table for AI model configurations.
- Validates and processes AI model definition entries.
- Converts `AiModelDefinition` entries into `AiGenerationConfig` objects.

#### Type
- Class: final
- Extends: None
- Implements: None
- Key generics and type bounds: `<String, AiGenerationConfig>`
- Lombok annotations: `@ToString`

#### Input
- Constructor parameter: 
  - `definitions`: List of `AiModelDefinition`
- Dependencies:
  - `Java8CompatibilityHelper`

#### Output
- Return type: None (throws exceptions)
- Produced state: `configs`: Map of AI model keys to generation configurations
- Mutated fields: None
- Written resources: None
- Side effects: Throws exceptions for invalid configurations

#### Core logic
- Validates each `AiModelDefinition` entry to ensure it has a non-null key.
- Constructs a `HashMap` to store the configuration mappings.
- Converts each `AiModelDefinition` into an `AiGenerationConfig`.
- Throws `NullPointerException` if any entry has a null key.
- Throws `IllegalArgumentException` if no definition is registered for a given key.

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition> definitions) -> Constructs a new AiModelDefinitionSupport from the supplied definitions list.`
- `AiGenerationConfig getConfig(String key) -> Returns the AiGenerationConfig associated with the given key.`

#### Dependencies
- Imports:
  - `java.util.HashMap`
  - `java.util.List`
  - `java.util.Map`
  - `java.util.Objects`
  - `lombok.ToString`
  - `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`

#### Exceptions / Errors
- Throws `NullPointerException` if any entry has a null key.
- Throws `IllegalArgumentException` if no definition is registered for a given key.

#### Concurrency
- Not explicitly handled; thread safety is not guaranteed.

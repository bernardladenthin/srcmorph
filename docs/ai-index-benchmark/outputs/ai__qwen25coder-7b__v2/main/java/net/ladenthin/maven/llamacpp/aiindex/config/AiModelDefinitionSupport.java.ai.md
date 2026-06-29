### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:29:24Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key, returning corresponding generation configurations.

#### Purpose
- Lookup table for converting `AiModelDefinition` entries to `AiGenerationConfig` objects.
- Ensures non-null keys and throws exceptions for misconfigured entries.
- Provides a method to retrieve `AiGenerationConfig` based on a key.

#### Type
- `public final class`
- Implements none

#### Input
- `List<AiModelDefinition> definitions`: List of AI model definitions.

#### Output
- Returns `AiGenerationConfig` associated with the given key.
- Throws `NullPointerException` if any entry has a `null` key.
- Throws `IllegalArgumentException` if no definition is registered for the key.

#### Core logic
- Initializes a `HashMap` to store `AiGenerationConfig` objects.
- Iterates over the list of `AiModelDefinition` entries, converting each to an `AiGenerationConfig`.
- Validates non-null keys and throws exceptions for invalid entries.
- Provides a method to retrieve an `AiGenerationConfig` by key.

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition> definitions) -> Constructs a new AiModelDefinitionSupport from the supplied definitions list.`
- `AiGenerationConfig getConfig(String key) -> Returns the AiGenerationConfig associated with the given key.`

#### Dependencies
- `java.util.HashMap`
- `java.util.List`
- `java.util.Map`
- `java.util.Objects`
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`

#### Exceptions / Errors
- Throws `NullPointerException` if any entry has a `null` key.
- Throws `IllegalArgumentException` if no definition is registered for the key.

#### Concurrency
- Not applicable

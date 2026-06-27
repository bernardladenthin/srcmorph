### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:14:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by their key, returning the corresponding generation configurations.

#### Purpose
- To provide a lookup table for AI model definitions to convert them into ready-to-use generation configurations.
- To enforce contract by ensuring non-null keys in the supplied list of definitions.

#### Type
- Class (`public final class AiModelDefinitionSupport`)
- Uses Lombok annotations (`@ToString`).

#### Input
- Constructor parameter: `definitions` (List<AiModelDefinition>)
- Method parameter: `key` (String)

#### Output
- Return type: `AiGenerationConfig`
- Side effect: Throws exceptions for missing keys.

#### Core logic
- Constructs a lookup table from the supplied list of AI model definitions.
- Ensures non-null keys and populates the map with corresponding generation configurations.
- Provides a method to retrieve the configuration by key.

#### Public API
- `public AiModelDefinitionSupport(List<AiModelDefinition> definitions)`
  - Constructs a new `AiModelDefinitionSupport` from the supplied definitions list.
- `public AiGenerationConfig getConfig(String key)`
  - Returns the generation config associated with the given key.

#### Dependencies
- `import java.util.HashMap;`
- `import java.util.List;`
- `import java.util.Map;`
- `import java.util.Objects;`
- `import lombok.ToString;`
- `import net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper;`

#### Exceptions / Errors
- Throws `NullPointerException` if any entry has a null key.
- Throws `IllegalArgumentException` if no definition is registered for the given key.

#### Concurrency
- The class and its methods are not thread-safe, as they rely on mutable state.
- Immutable objects or thread-local storage are not used within this class.

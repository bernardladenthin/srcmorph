### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:45:21Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This file defines a support class for resolving AI model definitions into generation configurations, ensuring efficient lookups and contract enforcement for missing keys.

#### Purpose
- Provides a lookup table for AI model definitions to convert them into ready-to-use generation configurations.
- Ensures that every entry in the supplied list has a non-null key, throwing exceptions for null keys.

#### Type
```java
public final class AiModelDefinitionSupport extends Object
```

#### Input
- `definitions`: List of `AiModelDefinition` entries; may be null or empty.

#### Output
- A `Map<String, AiGenerationConfig>` containing the resolved configurations.

#### Core logic
1. Initializes the `configs` map with a capacity based on the size of the definitions list.
2. Iterates through the definitions list to populate the map, ensuring each entry has a non-null key.
3. Converts an `AiModelDefinition` into an `AiGenerationConfig` by copying all field values.

#### Public API
```java
public AiGenerationConfig getConfig(String key) -> AiGenerationConfig
```
- Purpose: Retrieves the generation configuration associated with the given key, throwing an exception if the key is not found.

#### Dependencies
- `Map<String, AiGenerationConfig>`
- `Java8CompatibilityHelper`

#### Exceptions / Errors
- Throws `NullPointerException` if any entry in the definitions list has a null key.
- Throws `IllegalArgumentException` if no definition is registered for the given key.

#### Concurrency
- The class is not thread-safe as it uses mutable state and external dependencies.

#### Purpose
- Converts an `AiModelDefinition` into an `AiGenerationConfig`.

```java
private static AiGenerationConfig toConfig(AiModelDefinition definition) -> AiGenerationConfig
```

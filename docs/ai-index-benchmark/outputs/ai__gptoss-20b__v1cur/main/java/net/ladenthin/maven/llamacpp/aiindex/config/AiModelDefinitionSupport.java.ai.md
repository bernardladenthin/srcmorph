### AiModelDefinitionSupport.java
- H: 1.0
- C: 05A1DA33
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:07:53Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 3; TODO/FIXME: 0; @Override: 0; methods (approx): 3; constructors: 1; field declarations (w/ modifier): 3

> Builds a lookup table that maps AI model keys to ready‑to‑use generation configurations.

#### Purpose
- Provides fast retrieval of `AiGenerationConfig` by model key.
- Validates configuration entries at construction time.

#### Type
- `final class AiModelDefinitionSupport`
- Lombok `@ToString`

#### Input
- `List<AiModelDefinition> definitions` (may be null or empty)
- Each `AiModelDefinition` must have a non‑null `key` and other fields.

#### Output
- `AiGenerationConfig` instances stored in an internal `Map<String, AiGenerationConfig> configs`.

#### Core logic
- If `definitions` is null, create empty map.
- Presize map using `Java8CompatibilityHelper.hashMapCapacityFor(count)`.
- Iterate over `definitions`:
  - Validate non‑null `definition.getKey()`; throw `NullPointerException` with list index if null.
  - Convert `AiModelDefinition` to `AiGenerationConfig` via `toConfig` and put into map keyed by `definition.getKey()`.
- `getConfig(key)`:
  - Retrieve from map; if absent, throw `IllegalArgumentException` with missing key message.
- `toConfig(definition)`:
  - Instantiate `AiGenerationConfig`.
  - Copy all fields from `definition` to the config.

#### Public API
- `AiGenerationConfig getConfig(String key) -> retrieves config for key`
- `AiModelDefinitionSupport(List<AiModelDefinition> definitions) -> builds lookup table`

#### Dependencies
- `java.util.*`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`

#### Exceptions / Errors
- `NullPointerException` if any definition has a null key (includes index and entry in message).
- `IllegalArgumentException` if `getConfig` is called with an unregistered key.

#### Concurrency
- Immutable after construction; safe for concurrent reads.

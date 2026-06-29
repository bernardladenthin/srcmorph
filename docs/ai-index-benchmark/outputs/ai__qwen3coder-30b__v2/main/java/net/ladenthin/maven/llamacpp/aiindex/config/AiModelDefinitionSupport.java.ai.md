### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:15:15Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to provide configured generation settings for AI field generation tasks.

#### Purpose
- Maps AI model definition keys to ready-to-use generation configurations.
- Enforces required keys in configuration lists at build time.

#### Type
Final class implementing no interfaces; key generics: `Map<String, AiGenerationConfig>`; notable annotations: `@ToString`.

#### Input
- Constructor takes `List<AiModelDefinition>`; null or empty list treated as no definitions.
- `getConfig(key)` method consumes a `String` key.

#### Output
- `getConfig(key)` returns `AiGenerationConfig`.
- Constructor builds internal `Map<String, AiGenerationConfig>` from input list.

#### Core logic
- Validates each `AiModelDefinition` has a non-null key; throws `NullPointerException` if not.
- Populates internal map with key → `AiGenerationConfig` mapping via `toConfig()` helper.
- Looks up config by key; throws `IllegalArgumentException` if not found.

#### Public API
- `getConfig(key) -> AiGenerationConfig`: retrieves generation config for a given definition key.

#### Dependencies
- `AiModelDefinition`, `AiGenerationConfig`, `Java8CompatibilityHelper`

#### Exceptions / Errors
- Throws `NullPointerException` on null keys in input list.
- Throws `IllegalArgumentException` when `getConfig()` is called with unknown key.
- Uses `Objects.requireNonNull` for null key validation.

#### Concurrency
- Immutable internal state; thread-safe read access to `configs` map.

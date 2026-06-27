### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:07:02Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This class maps AI model definitions to ready‑to‑use generation configurations for the llama‑cpp AI plugin.

#### Purpose
- Maps `AiModelDefinition` entries to `AiGenerationConfig` objects.  
- Validates non‑null keys and provides eager lookup errors.

#### Type
final class `AiModelDefinitionSupport`; Lombok `@ToString`; holds `configs` map and a `Java8CompatibilityHelper`.

#### Input
- Constructor `AiModelDefinitionSupport(List<AiModelDefinition> definitions)` – list may be `null`/empty.  
- `getConfig(String key)` – key must not be `null`.

#### Output
- Constructor builds the internal `configs` map.  
- `getConfig` returns the matching `AiGenerationConfig` or throws.

#### Core logic
- **Constructor**:  
  - If `definitions` is `null`, create an empty map.  
  - Otherwise pre‑size a `HashMap` using `Java8CompatibilityHelper`.  
  - Iterate definitions; for each, `Objects.requireNonNull` on `definition.getKey()` with an index‑based message.  
  - Put `definition.getKey()` → `toConfig(definition)` into `configs`.  
- **getConfig**: Retrieve config from `configs`; if `null`, throw `IllegalArgumentException` with `"Missing AI model definition for key: " + key`.  
- **toConfig**: Instantiate `AiGenerationConfig` and copy all fields from `AiModelDefinition`.

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition>)` → builds lookup map.  
- `getConfig(String)` → `AiGenerationConfig`; throws if key absent.

#### Dependencies
`AiModelDefinition`, `AiGenerationConfig`, `Java8CompatibilityHelper`, `HashMap`, `List`, `Map`, `Objects`, Lombok `@ToString`.

#### Exceptions / Errors
- Constructor: `NullPointerException` when any `definition.getKey()` is `null`.  
- `getConfig`: `IllegalArgumentException` if key not found.

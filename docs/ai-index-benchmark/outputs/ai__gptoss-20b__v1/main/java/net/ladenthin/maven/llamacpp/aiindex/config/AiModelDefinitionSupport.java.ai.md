### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:16:01Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Builds a lookup table for AI model definitions, returning ready‑to‑use generation configs.  

#### Purpose
- Provides mapping from a definition key to a fully populated `AiGenerationConfig`.  
- Ensures early validation of configuration entries.  

#### Type
- `final class` `AiModelDefinitionSupport`  
- Annotated `@ToString`.  

#### Input
- Constructor `AiModelDefinitionSupport(List<AiModelDefinition> definitions)`  
  - Accepts `null` or empty list → no configs.  
  - Each `AiModelDefinition` must have non‑null `key`.  
- `getConfig(String key)` receives key for lookup.  

#### Output
- Constructor initializes `configs: Map<String, AiGenerationConfig>`.  
- `getConfig` returns the corresponding config or throws `IllegalArgumentException`.  

#### Core logic
- Constructor
  - If `definitions` null → empty `HashMap` with capacity for 0.  
  - Else pre‑size `HashMap` for count.  
  - Iterate over definitions:  
    - Validate key non‑null, error message includes index and definition.  
    - Convert definition to config via `toConfig`.  
    - Put key‑config pair into map.  
- `getConfig`
  - Retrieve from map; if missing → throw `IllegalArgumentException` with prefix `Missing AI model definition for key: `.  
- `toConfig`
  - Instantiates `AiGenerationConfig`.  
  - Copies all properties from `AiModelDefinition`.  

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition> definitions) -> void` – build lookup map.  
- `AiGenerationConfig getConfig(String key) -> config` – fetch config by key.  

#### Dependencies
- `java.util.HashMap`, `java.util.List`, `java.util.Map`, `java.util.Objects`  
- `lombok.ToString`  
- `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`  
- `AiModelDefinition`, `AiGenerationConfig`  

#### Exceptions / Errors
- Constructor: throws `NullPointerException` if any `AiModelDefinition.key` is null, message includes list index.  
- `getConfig`: throws `IllegalArgumentException` when key not found.  

#### Concurrency
- Immutable after construction; `configs` not modified elsewhere. No synchronization required.

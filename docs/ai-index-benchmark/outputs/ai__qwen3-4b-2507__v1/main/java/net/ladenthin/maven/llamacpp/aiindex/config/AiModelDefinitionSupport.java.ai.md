### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:50:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to produce ready-to-use generation configurations

#### Purpose
- Acts as a lookup table to convert AI model keys into full generation configurations  
- Enforces configuration validity at build time by validating non-null keys and missing definitions  

#### Type
- final class with @ToString annotation  
- implements no interfaces, extends no type  
- generic: none  
- key dependencies: AiModelDefinition → AiGenerationConfig via key lookup  

#### Input
- List<AiModelDefinition> definitions (may be null or empty)  
- Individual entries must have non-null key; null key triggers immediate NullPointerException  

#### Output
- Map<String, AiGenerationConfig> internal state (immutable after construction)  
- Returns AiGenerationConfig on lookup by key  
- Throws IllegalArgumentException for missing keys  

#### Core logic
- Presizes map to avoid rehashing during bulk population  
- Validates each definition's key at insertion time and enforces non-null requirement  
- Converts each AiModelDefinition into a full AiGenerationConfig via field-by-field copy  
- Returns null-config if key not found, then throws IllegalArgumentException with descriptive message  

#### Public API
- AiModelDefinitionSupport(List<AiModelDefinition> definitions) → void (builds lookup map from list)  
- getConfig(String key) → AiGenerationConfig (returns config for given key or throws error)  

#### Dependencies
AiModelDefinition, AiGenerationConfig, Java8CompatibilityHelper, Objects  

#### Exceptions / Errors
- NullPointerException if any entry has null key (with index and entry detail)  
- IllegalArgumentException if key not found in map (with prefix message)  
- Null key in getConfig() throws IllegalArgumentException  

#### Concurrency
- Immutable state after construction  
- No shared mutable state; thread-safe via final fields and immutable config objects  
- Safe for concurrent access without synchronization

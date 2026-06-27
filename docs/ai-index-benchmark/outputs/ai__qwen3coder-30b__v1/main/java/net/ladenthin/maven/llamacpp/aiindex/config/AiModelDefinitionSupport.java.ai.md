### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:48:08Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to produce generation configurations for use in field generation tasks.

#### Purpose
- Maps AI model definition keys to ready-to-use generation configs.
- Enforces required fields and validates configuration at build time.

#### Type
Final class extending no types; implements no interfaces. Generics: none. Annotations: @ToString.

#### Input
Constructor takes List<AiModelDefinition>; throws NullPointerException for null keys. getConfig method takes String key.

#### Output
getConfig returns AiGenerationConfig; toConfig produces AiGenerationConfig from AiModelDefinition. Side effect: throws IllegalArgumentException for missing keys.

#### Core logic
- Builds internal HashMap from list of AiModelDefinition entries using their keys.
- Validates each definition has a non-null key during construction.
- Converts each AiModelDefinition into an AiGenerationConfig via field copying.
- Looks up AiGenerationConfig by key; throws exception if not found.

#### Public API
getConfig(key) -> AiGenerationConfig: retrieves generation config by key  
toConfig(definition) -> AiGenerationConfig: converts definition to config  

#### Dependencies
Imports: java.util.HashMap, java.util.List, java.util.Map, java.util.Objects, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper, net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition, net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig

#### Exceptions / Errors
Throws NullPointerException for null keys in definitions list. Throws IllegalArgumentException for missing keys during lookup.

#### Concurrency
No concurrency controls; class is immutable after construction.

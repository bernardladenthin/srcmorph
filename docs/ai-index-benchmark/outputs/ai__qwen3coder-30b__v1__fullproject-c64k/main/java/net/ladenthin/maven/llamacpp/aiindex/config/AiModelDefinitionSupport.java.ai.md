### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T15:59:12Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to provide configured generation settings for AI field generation.

#### Purpose
- Maps AI model definition keys to ready-to-use generation configurations.
- Enforces required fields and validates configuration at build time.

#### Type
Final class extending no types; implements no interfaces. Uses generics: Map<String, AiGenerationConfig>. Key annotations: @ToString.

#### Input
Constructor takes List<AiModelDefinition>; each definition must have a non-null key. Dependencies include Java8CompatibilityHelper.

#### Output
Returns AiGenerationConfig for valid keys; throws IllegalArgumentException for missing keys. Produces HashMap of configs from definitions.

#### Core logic
- Builds lookup map from input definitions, ensuring no null keys.
- Maps each definition to a generation config via field copying.
- Provides fast key-based retrieval with error handling for missing keys.

#### Public API
getConfig(key) -> AiGenerationConfig: retrieves config by key
toConfig(definition) -> AiGenerationConfig: converts model def to gen config

#### Dependencies
Imports: java.util.HashMap, java.util.List, java.util.Map, java.util.Objects, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper, net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition, net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig

#### Exceptions / Errors
Throws NullPointerException for null keys in definitions; IllegalArgumentException for missing keys during lookup.

#### Concurrency
No concurrency considerations noted. Class is immutable post-construction.

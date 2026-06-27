### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:11:00Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Resolves {@link AiModelDefinition} entries by their key, returning the corresponding {@link AiGenerationConfig}.

#### Type
- Class, final, implements Java8CompatibilityHelper.

#### Input
- List<AiModelDefinition> definitions.

#### Output
- Map<String, AiGenerationConfig> configs; AiGenerationConfig getConfig(String key).

#### Core Logic
- Constructs a lookup table from AI model definitions.
- Ensures each definition has a non-null key.
- Converts definitions to generation configs.
- Throws NullPointerException for null keys.
- Throws IllegalArgumentException for missing keys.

#### Public API
- AiGenerationConfig getConfig(String key) -> AiGenerationConfig; Returns generation config for a key.
- toConfig(AiModelDefinition) -> AiGenerationConfig; Converts definition to config.

#### Dependencies
- java.util.HashMap; java.util.List; java.util.Map; java.util.Objects; net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper.

#### Exceptions / Errors
- NullPointerException for null keys.
- IllegalArgumentException for missing keys.

#### Concurrency
- HashMap is not thread-safe; consider synchronization for concurrent access.

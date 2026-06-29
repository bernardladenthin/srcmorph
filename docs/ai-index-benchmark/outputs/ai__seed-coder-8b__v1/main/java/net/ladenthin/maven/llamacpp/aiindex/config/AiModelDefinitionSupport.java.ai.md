### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:13:40Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by converting them to generation configurations

#### Purpose
- Maps AI model definitions to generation configurations
- Provides lookup for AI models based on keys

#### Type
- Class
- Final
- Notable annotations: @ToString

#### Input
- List of AiModelDefinition objects in constructor
- String key in getConfig method

#### Output
- Map of String keys to AiGenerationConfig objects
- AiGenerationConfig object from getConfig method

#### Core logic
- Builds map of definitions during construction
- Converts definitions to configs using toConfig method
- Performs null checks on definition keys
- Throws exceptions for missing or null keys

#### Public API
- `AiModelDefinitionSupport(List<AiModelDefinition>)` -> Constructor
- `AiGenerationConfig getConfig(String)` -> Retrieves config by key

#### Dependencies
- java.util.HashMap
- java.util.List
- java.util.Map
- java.util.Objects
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper

#### Exceptions / Errors
- NullPointerException for null keys during construction
- IllegalArgumentException for missing keys in getConfig
- NullPointerExceptions in toConfig if definition is null

#### Concurrency
- Thread-safe due to final class and immutable state

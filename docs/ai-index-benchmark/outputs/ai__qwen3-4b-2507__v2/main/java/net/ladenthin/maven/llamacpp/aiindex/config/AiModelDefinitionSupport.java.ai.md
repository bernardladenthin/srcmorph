### AiModelDefinitionSupport.java
- H: 1.0
- C: 0A078A71
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:19:56Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves AI model definitions by key to generate configuration objects in the AI modeling domain

#### Purpose
- Acts as a lookup table for converting AI model definitions into runtime generation configurations  
- Enforces validation of non-null keys during initialization to prevent silent configuration failures  

#### Type
class final public @ToString + implements none; extends none; generics: Map<String, AiGenerationConfig>; notable annotations: @ToString

#### Input
- List<AiModelDefinition> definitions (may be null or empty)

#### Output
- AiGenerationConfig object for a given key; throws IllegalArgumentException on missing key

#### Core logic
- Initializes map with presized capacity to avoid rehashing during bulk insertion  
- Validates each definition's key at insertion, throwing NullPointerException if null  
- Converts each AiModelDefinition into an AiGenerationConfig via field copying  
- Returns config on lookup; throws IllegalArgumentException if key not found  

#### Public API
getConfig(String key) -> AiGenerationConfig: returns generation config by key; throws if missing  
toConfig(AiModelDefinition) -> AiGenerationConfig: converts definition to config (private)

#### Dependencies
AiModelDefinition, AiGenerationConfig, Java8CompatibilityHelper

#### Exceptions / Errors
- NullPointerException if any entry has null key (with index and entry details)  
- IllegalArgumentException if lookup key not found in map  

#### Concurrency
Immutable state; no shared mutable state; thread-safe via final fields and immutable config objects

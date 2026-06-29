### AiPromptDefinition.java
- H: 1.0
- C: 149C1FBD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:56:54Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a configuration object for AI prompt templates used in a Maven plugin, associating lookup keys with template strings.

#### Purpose
- Stores and manages prompt template configurations for AI processing.
- Supports Maven plugin integration via JavaBean property mapping.

#### Type
class public final
Implements: none
Generics: none
Annotations: @ToString

#### Input
- Constructor: no parameters.
- Methods: setKey(String), setTemplate(String).
- Fields read: key, template (via getters).

#### Output
- Methods: getKey(), getTemplate() return field values.
- Side effects: field mutation via setters.

#### Core logic
- Encapsulates a key-template pair for AI prompt configuration.
- Provides standard JavaBean accessors for configuration properties.
- Uses Lombok-generated toString for diagnostics without value-based equality.

#### Public API
getKey() -> String returns lookup key
setKey(String) -> void sets lookup key
getTemplate() -> String returns template string
setTemplate(String) -> void sets template string

#### Dependencies
lombok.ToString

#### Exceptions / Errors
Null handling: fields may be null due to reflection initialization.
No explicit exception throwing.

#### Concurrency
Not thread-safe. Instances are managed by identity in plugin context; mutable state implies no concurrency guarantees.

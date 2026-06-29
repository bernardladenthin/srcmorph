### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:29:25Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Maven plugin configuration POJO associating prompt templates with AI model definitions for field generation.

#### Purpose
- Config POJO for associating prompt templates with AI model definitions.
- Used in Maven plugin configuration for field generation.

#### Type
- Class: `AiFieldGenerationConfig`
- Extends: `java.lang.Object`
- Implements: `java.io.Serializable`
- Key generics: `java.util.List<String>`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- Constructor parameters: none
- Dependencies: none
- Consumed fields: `promptKey`, `aiDefinitionKey`, `fileExtensions`
- Read resources: none

#### Output
- Return types: `String` for getters, `void` for setters
- Produced/mutated state: none
- Written resources: none
- Side effects: none

#### Core logic
- Creates a new instance with no-op.
- Sets and gets `promptKey`, `aiDefinitionKey`, and `fileExtensions`.
- `getFileExtensions()` returns an unmodifiable list or `null`.

#### Public API
- `AiFieldGenerationConfig()`: no-op constructor
- `getPromptKey() -> String`: returns prompt template key
- `setPromptKey(String)`: sets prompt template key
- `getAiDefinitionKey() -> String`: returns AI model definition key
- `setAiDefinitionKey(String)`: sets AI model definition key
- `getFileExtensions() -> @Nullable List<String>`: returns file extensions or `null`
- `setFileExtensions(Collection<String>)`: sets file extensions, defensively copied

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None notable

#### Concurrency
- Not applicable (no threading or concurrency concerns)

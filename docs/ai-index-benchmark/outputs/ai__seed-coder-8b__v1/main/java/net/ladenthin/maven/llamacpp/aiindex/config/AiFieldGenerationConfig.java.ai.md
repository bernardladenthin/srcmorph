### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:03:19Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI field generation for Maven plugin using prompt templates and model definitions

#### Purpose
- Associates prompt templates with AI models for single field-generation steps
- Used in Maven plugin configuration

#### Type
- Class
- Public
- Uses Lombok's @ToString annotation

#### Input
- Constructor: No parameters
- Setters: promptKey, aiDefinitionKey, fileExtensions

#### Output
- Getters: promptKey, aiDefinitionKey, fileExtensions
- Mutated fields: promptKey, aiDefinitionKey, fileExtensions

#### Core logic
- Stores configuration for AI field generation
- Manages associations between prompts and AI models
- Handles file extension selection for field generation

#### Public API
- `getPromptKey() -> String`: Returns prompt template key
- `setPromptKey(String) -> void`: Sets prompt template key
- `getAiDefinitionKey() -> String`: Returns AI model definition key
- `setAiDefinitionKey(String) -> void`: Sets AI model definition key
- `getFileExtensions() -> List<String>`: Returns file extensions or null
- `setFileExtensions(Collection<String>) -> void`: Sets file extensions

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No explicit exceptions thrown
- Null handling for fileExtensions in getFileExtensions()

#### Concurrency
- Not explicitly thread-safe
- Mutable state, may require external synchronization

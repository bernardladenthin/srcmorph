### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:44:05Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a mutable Maven plugin configuration POJO linking a prompt template key to an AI model definition for single-field-generation steps in the Llama.cpp index builder.

#### Purpose
- Represents configuration for one field-generation step within the `<fieldGenerations>` list of the Maven plugin POM.
- Associates a specific prompt template (`promptKey`) with an AI model definition (`aiDefinitionKey`) to drive generation calls on indexed files or packages.

#### Type
public class AiFieldGenerationConfig extends Object; annotated with @ToString; uses JavaBeans pattern with mutable fields and setters for Maven reflection injection.

#### Input
- `String promptKey`: Key referencing a prompt template in `AiPromptDefinition`.
- `String aiDefinitionKey`: Key referencing an AI model definition in `AiModelDefinition`.
- `Collection<String> fileExtensions`: Optional list of source file extensions (.java, .sql) that restrict applicability; null or empty acts as fallback.

#### Output
- Modifies internal state: `promptKey`, `aiDefinitionKey`, and `fileExtensions` fields.
- Returns unmodifiable `List<String>` for `getFileExtensions()` if the list is not null.

#### Core logic
- Validates and stores prompt template key, AI model definition key, and optional file extension filters.
- Ensures immutability of returned collection via `Collections.unmodifiableList`.
- Converts input `Collection` to `ArrayList` defensively during `setFileExtensions`.

#### Public API
- `getPromptKey()`: Retrieves the prompt template identifier.
- `setPromptKey(String)`: Assigns a new prompt template identifier.
- `getAiDefinitionKey()`: Retrieves the AI model definition identifier.
- `setAiDefinitionKey(String)`: Assigns a new AI model definition identifier.
- `getFileExtensions()`: Retrieves the list of file extensions or null for fallback.
- `setFileExtensions(Collection<String>)`: Updates the list of file extensions, creating a defensive copy.

#### Dependencies
- AiModelDefinition
- AiPromptDefinition
- AiFieldGenerationSelector
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List

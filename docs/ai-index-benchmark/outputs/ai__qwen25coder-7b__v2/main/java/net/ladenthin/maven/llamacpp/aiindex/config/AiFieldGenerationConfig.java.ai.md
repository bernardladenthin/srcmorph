### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T20:12:55Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents a configuration for an AI field generation step within a Maven plugin, associating a prompt template and AI model definition.

#### Purpose
- Holds configuration for a single field-generation step in an AI-based Maven plugin.
- Used to define the prompt template and AI model for generating content in files based on specified criteria.

#### Type
`public class AiFieldGenerationConfig`

#### Input
- `promptKey`: Key that references an `AiModelDefinition` registered in the plugin configuration.
- `aiDefinitionKey`: Key that references the AI model definition to use.
- `fileExtensions`: Optional list of file extensions that select this field generation for specific files.

#### Output
- Returns the prompt template key, AI model definition key, and source file extensions.

#### Core logic
- Initializes with default values.
- Provides setters and getters for `promptKey`, `aiDefinitionKey`, and `fileExtensions`.

#### Public API
- `AiFieldGenerationConfig()`: Creates a new `AiFieldGenerationConfig` instance.
- `String getPromptKey()`: Returns the prompt template key.
- `void setPromptKey(String promptKey)`: Sets the prompt template key.
- `String getAiDefinitionKey()`: Returns the AI model definition key.
- `void setAiDefinitionKey(String aiDefinitionKey)`: Sets the AI model definition key.
- `@Nullable List<String> getFileExtensions()`: Returns the source file extensions that select this entry.
- `void setFileExtensions(@Nullable Collection<String> fileExtensions)`: Sets the source file extensions that select this entry.

#### Dependencies
- `AiModelDefinition`
- `AiPromptDefinition`

#### Exceptions / Errors
- None explicitly handled; relies on Lombok annotations for null handling.

#### Concurrency
- Not applicable; mutable JavaBean designed for reflection-based configuration injection.

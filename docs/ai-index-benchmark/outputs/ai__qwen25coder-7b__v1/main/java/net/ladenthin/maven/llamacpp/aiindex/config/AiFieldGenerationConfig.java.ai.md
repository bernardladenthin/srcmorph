### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T19:18:48Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Manages AI field generation configuration in Maven plugin

#### Purpose
- Holds configuration for associating prompt templates with AI models for field generation.
- Used within a list in the plugin configuration to define individual field-generation steps.

#### Type
- Class
- Abstract: No
- Final: Yes
- Sealed: No
- Extends: None
- Implements: None
- Generics: None
- Key annotations: `@ToString`, `@Nullable`

#### Input
- Constructor parameters: None
- Injected dependencies: None
- Consumed fields: None
- Read resources: None

#### Output
- Return types: 
  - `String` (getters)
  - `List<String>` (getters)
- Produced state: 
  - `promptKey`
  - `aiDefinitionKey`
  - `fileExtensions`
- Written resources: None
- Side effects: None

#### Core logic
- Holds configuration for a single field-generation step.
- References an AI model definition by key.
- Optionally specifies file extensions to select this entry.

#### Public API
- `AiFieldGenerationConfig() -> void` — Creates a new configuration instance.
- `String getPromptKey() -> String` — Returns the prompt template key.
- `void setPromptKey(final String promptKey) -> void` — Sets the prompt template key.
- `String getAiDefinitionKey() -> String` — Returns the AI model definition key.
- `void setAiDefinitionKey(final String aiDefinitionKey) -> void` — Sets the AI model definition key.
- `@Nullable List<String> getFileExtensions() -> @Nullable List<String>` — Returns the source file extensions that select this entry.
- `void setFileExtensions(final @Nullable Collection<String> fileExtensions) -> void` — Sets the source file extensions that select this entry.

#### Dependencies
- Imports:
  - `java.util.ArrayList`
  - `java.util.Collection`
  - `java.util.Collections`
  - `java.util.List`
  - `lombok.ToString`
  - `org.jspecify.annotations.Nullable`
- Referenced types:
  - `AiModelDefinition`
  - `AiPromptDefinition`

#### Exceptions / Errors
- Notable thrown/caught exceptions: None
- Null-handling: Uses `@Nullable` and defensive copying for list operations
- Error conditions: None

#### Concurrency
- Threading: None
- Synchronization: None
- Immutability: None
- Thread-safety notes: None

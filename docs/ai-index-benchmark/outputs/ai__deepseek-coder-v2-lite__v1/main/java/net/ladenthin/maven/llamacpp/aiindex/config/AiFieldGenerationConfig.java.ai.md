### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:05:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices by configuring AI-driven field generation in a Maven plugin for code assistance.

#### Purpose
- **Configure AI-driven field generation** for specific file types and prompts.
- **Associate prompts with AI model definitions** for generating content based on specified criteria.

#### Type
- Class (`public`)
- Extends: `Object`
- Annotations: `@SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")`, `@ToString`

#### Input
- Constructor parameters: None
- Method parameters: 
  - `promptKey`: String
  - `aiDefinitionKey`: String
  - `fileExtensions`: Collection<String> (nullable)
- Injected dependencies: None
- Consumed fields: None
- Read resources: None

#### Output
- Return types: 
  - `String getPromptKey()`: Returns the prompt template key.
  - `String getAiDefinitionKey()`: Returns the AI model definition key.
  - `List<String> getFileExtensions()`: Returns the source file extensions that select this entry, or `null`.
- Produced state: None
- Mutated fields: 
  - `promptKey`
  - `aiDefinitionKey`
  - `fileExtensions`
- Written resources: None
- Side effects: None

#### Core logic
- **Initialize the configuration** with default values.
- **Set and get prompt keys** for AI generation.
- **Set and get AI model definition keys** for specific configurations.
- **Manage file extensions** to determine applicability based on file types.

#### Public API
- `getPromptKey() -> String`: Returns the key that identifies the prompt template.
- `setPromptKey(promptKey) -> void`: Sets the key that references an `AiPromptDefinition`.
- `getAiDefinitionKey() -> String`: Returns the key that references the `AiModelDefinition`.
- `setAiDefinitionKey(aiDefinitionKey) -> void`: Sets the key that references an `AiModelDefinition`.
- `getFileExtensions() -> List<String>`: Returns the selecting file extensions, or `null`.
- `setFileExtensions(fileExtensions) -> void`: Sets the selecting file extensions.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws no exceptions.
- Handles null file extensions by setting them to `null`.

#### Concurrency
- The class is not thread-safe as it does not manage any shared resources or synchronization mechanisms.

#### EOF

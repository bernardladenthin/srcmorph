### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T03:37:48Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This file defines a configuration POJO for field generation in a Maven plugin, associating prompt templates with AI model definitions for specific file types.

#### Purpose
- **Associates** a prompt template with an AI model definition for field generation.
- **Declares** within the `<fieldGenerations>` list in the plugin configuration.

#### Type
```java
public class AiFieldGenerationConfig {
    // Constructor, setters, and getters for private fields
}
```

#### Input
- `promptKey`: Key referencing an {@link AiPromptDefinition}.
- `aiDefinitionKey`: Key referencing an {@link AiModelDefinition}.
- `fileExtensions`: Optional list of file extensions to select this field generation.

#### Output
- Returns: `String` for prompt key, AI definition key, and file extensions.
- Sets: `promptKey`, `aiDefinitionKey`, and `fileExtensions`.

#### Core logic
1. **Constructor**: Initializes a new {@link AiFieldGenerationConfig}.
2. **getters/setters**: Provide access to private fields for prompt key, AI definition key, and file extensions.

#### Public API
- `getPromptKey() -> String`: Returns the key for the prompt template.
- `setPromptKey(String promptKey)`: Sets the key for the prompt template.
- `getAiDefinitionKey() -> String`: Returns the key for the AI model definition.
- `setAiDefinitionKey(String aiDefinitionKey)`: Sets the key for the AI model definition.
- `getFileExtensions() -> @Nullable List<String>`: Returns the file extensions that select this entry, or null if it's the fallback.
- `setFileExtensions(@Nullable Collection<String> fileExtensions)`: Sets the file extensions that select this entry.

#### Dependencies
- {@link AiModelDefinition}
- {@link AiModelDefinitionSupport}
- {@link net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition}

#### Exceptions / Errors
- No notable exceptions or error conditions mentioned.

#### Concurrency
- The class is mutable and instantiated via reflection, suggesting thread-unsafe usage without synchronization.

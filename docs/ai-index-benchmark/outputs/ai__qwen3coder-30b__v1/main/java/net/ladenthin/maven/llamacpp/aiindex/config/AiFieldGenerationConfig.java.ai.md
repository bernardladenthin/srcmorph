### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:39:52Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI-driven field generation by linking prompt templates and model definitions for Maven plugin execution.

#### Purpose
- Associates AI prompt templates with model configurations for field generation.
- Supports file extension-based selection of generation rules.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation. Generics: none. Type bounds: none.

#### Input
- Constructor takes no parameters.
- Setters accept: String promptKey, String aiDefinitionKey, @Nullable Collection<String> fileExtensions.
- Dependencies: java.util.ArrayList, java.util.Collection, java.util.Collections, java.util.List, org.jspecify.annotations.Nullable, lombok.ToString.

#### Output
- Getters return: String promptKey, String aiDefinitionKey, @Nullable List<String> fileExtensions.
- Setters mutate internal fields: promptKey, aiDefinitionKey, fileExtensions.
- Defensive copy in setFileExtensions ensures immutability of returned list.

#### Core logic
- Maps a prompt template key to an AI model definition key for field generation.
- Filters files by optional extensions; fallback behavior when no match.
- Maven plugin configuration object for declarative field generation.

#### Public API
- getPromptKey() -> String: Retrieves the prompt template identifier.
- setPromptKey(String): Sets the prompt template identifier.
- getAiDefinitionKey() -> String: Retrieves the AI model definition identifier.
- setAiDefinitionKey(String): Sets the AI model definition identifier.
- getFileExtensions() -> @Nullable List<String>: Returns file extension filter list.
- setFileExtensions(@Nullable Collection<String>): Assigns file extension filter.

#### Dependencies
java.util.ArrayList, java.util.Collection, java.util.Collections, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable

#### Exceptions / Errors
No explicit throws. Defensive copying prevents external mutation of fileExtensions list.

#### Concurrency
No concurrency handling; class is not thread-safe. Intended for Maven plugin configuration use only.

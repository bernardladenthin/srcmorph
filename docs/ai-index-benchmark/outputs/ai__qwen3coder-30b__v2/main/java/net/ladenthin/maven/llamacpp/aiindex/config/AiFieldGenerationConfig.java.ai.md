### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T23:07:40Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI-driven field generation by linking prompt templates and AI model definitions for Maven plugin execution.

#### Purpose
- Associates AI prompt templates with model definitions for field generation
- Supports file extension filtering for targeted generation

#### Type
class public final AiFieldGenerationConfig extends java.lang.Object implements none; key generics: none; notable annotations: @ToString, @Nullable

#### Input
- Constructor: no parameters
- Methods: setPromptKey(String), setAiDefinitionKey(String), setFileExtensions(Collection<String>)
- Fields read: promptKey, aiDefinitionKey, fileExtensions

#### Output
- Methods: getPromptKey() → String, getAiDefinitionKey() → String, getFileExtensions() → List<String>
- Fields mutated: promptKey, aiDefinitionKey, fileExtensions

#### Core logic
- Links AI model definition key to prompt template key for field generation
- Filters files by extension when specified
- Provides immutable view of file extensions list

#### Public API
- getPromptKey() → String
- setPromptKey(String)
- getAiDefinitionKey() → String
- setAiDefinitionKey(String)
- getFileExtensions() → List<String>
- setFileExtensions(Collection<String>)

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition  
net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition

#### Exceptions / Errors
- Null handling for fileExtensions field
- Defensive copying of input collection in setFileExtensions

#### Concurrency
- No explicit concurrency concerns; class is intended for Maven plugin configuration use only

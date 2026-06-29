### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:33:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Maven plugin configuration for associating prompt templates with AI model definitions for field generation.

#### Purpose
- Configure AI field generation via prompt templates and AI models.
- Associate configurations with specific file extensions or use as a fallback.

#### Type
class public @ToString

#### Input
- String promptKey, aiDefinitionKey
- Collection<String> fileExtensions

#### Output
- String promptKey, aiDefinitionKey
- List<String> fileExtensions (unmodifiable copy)

#### Core logic
- Store and retrieve configuration for AI field generation.
- Handle file extension-based selection or fallback.
- Provide defensive copying for file extensions.

#### Public API
- getPromptKey() -> String: Get prompt template key
- setPromptKey(String) -> void: Set prompt template key
- getAiDefinitionKey() -> String: Get AI model definition key
- setAiDefinitionKey(String) -> void: Set AI model definition key
- getFileExtensions() -> List<String>: Get file extensions (unmodifiable)
- setFileExtensions(Collection<String>) -> void: Set file extensions

#### Dependencies
- AiModelDefinition
- AiModelDefinitionSupport
- AiPromptDefinition
- List, ArrayList, Collections

#### Exceptions / Errors
No explicit exceptions. Null handling for fileExtensions is documented.

#### Concurrency
Not explicitly thread-safe. Mutable state with potential race conditions in multi-threaded environments.

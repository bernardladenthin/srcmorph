### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T06:13:14Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI prompt and model keys for field generation by file extension in Maven plugin

#### Purpose
- Defines mapping between prompt templates and AI models for field generation  
- Enables selective application based on source file extensions  

#### Type
class @ToString public mutable JavaBean with setters; key generics: none; notable annotations: @ToString, @Nullable

#### Input
- promptKey (String): key to prompt template  
- aiDefinitionKey (String): key to AI model definition  
- fileExtensions (List<String>): optional list of file extensions to match  

#### Output
- promptKey (String) – retrieved from config  
- aiDefinitionKey (String) – retrieved from config  
- fileExtensions (List<String>) – returned as unmodifiable view or null  

#### Core logic
- Stores prompt and model keys for AI field generation  
- Uses file extensions to filter which files trigger generation  
- Defensively copies fileExtensions list on set to prevent mutation  
- Returns immutable view of fileExtensions when queried  

#### Public API
- getPromptKey() -> String: returns prompt template key  
- setPromptKey(String) -> void: sets prompt template key  
- getAiDefinitionKey() -> String: returns AI model definition key  
- setAiDefinitionKey(String) -> void: sets AI model definition key  
- getFileExtensions() -> List<String> (nullable): returns file extensions list or null  
- setFileExtensions(Collection<String>) -> void: sets file extensions; null/empty makes it fallback  

#### Dependencies
AiModelDefinition, AiPromptDefinition, AiFieldGenerationSelector

#### Exceptions / Errors
None explicitly thrown; null handling via @Nullable and defensive copying  

#### Concurrency
Immutable output view; no thread-safety guarantees; mutable state only in instance fields

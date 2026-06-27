### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:42:19Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Associates a prompt template with an AI model definition to generate field values for specific file types in Maven plugin processing

#### Purpose
- Defines a mapping between a prompt template and an AI model for field generation per file type  
- Enables selective AI processing based on source file extensions  

#### Type
class, public, final none; @ToString; implements none; extends none; generics: none  
key annotations: @ToString, @Nullable (on fields), @SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")

#### Input
- Constructor: no parameters  
- Method parameters: promptKey (String), aiDefinitionKey (String), fileExtensions (Collection<String>)  
- Injected via Maven plugin reflection through setters  

#### Output
- Returns promptKey (String)  
- Returns aiDefinitionKey (String)  
- Returns fileExtensions (List<String> or null) as unmodifiable view  
- Produces immutable list when fileExtensions is set  

#### Core logic
- Maps a prompt key to an AI model definition by key  
- Applies field generation only to files matching specified extensions  
- Uses fallback behavior when no extensions are defined  
- Defensively copies fileExtensions list to prevent external modification  

#### Public API
- getPromptKey() → String (returns prompt template key)  
- setPromptKey(String) → void (sets prompt template key)  
- getAiDefinitionKey() → String (returns AI model definition key)  
- setAiDefinitionKey(String) → void (sets AI model definition key)  
- getFileExtensions() → List<String> or null (returns file extensions or fallback)  
- setFileExtensions(Collection<String>) → void (sets file extensions; null = fallback)  

#### Dependencies
AiModelDefinition, AiPromptDefinition, Collection, List, ArrayList, Collections, lombok.ToString  

#### Exceptions / Errors
- No explicit exceptions thrown  
- Null handling via @Nullable on fileExtensions and method parameters  
- Null values are allowed and treated as fallback  

#### Concurrency
- Immutable output for fileExtensions after set  
- No thread-safety or synchronization annotations  
- Safe for concurrent access due to defensive copying of list  
- Mutable state only in setter methods, not exposed externally

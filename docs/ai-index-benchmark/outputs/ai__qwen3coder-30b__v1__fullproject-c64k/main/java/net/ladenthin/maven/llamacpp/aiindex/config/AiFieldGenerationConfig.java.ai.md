### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T15:50:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI-driven field generation by linking prompt templates and model definitions for Maven plugin execution.

#### Purpose
- Maps AI prompt templates to model configurations for field generation.
- Supports file extension filtering for targeted generation.

#### Type
class public final
extends java.lang.Object
implements no interfaces
generics no type bounds
annotations @ToString, @SuppressWarnings

#### Input
- Constructor: no parameters.
- Method parameters: setPromptKey(String), setAiDefinitionKey(String), setFileExtensions(Collection<String>).
- Fields read: promptKey, aiDefinitionKey, fileExtensions.
- Dependencies injected via setters.

#### Output
- Return types: getPromptKey(), getAiDefinitionKey(), getFileExtensions().
- State mutated: promptKey, aiDefinitionKey, fileExtensions.
- Side effects: defensive copy in setFileExtensions().

#### Core logic
- Associates a prompt template key with an AI model definition key.
- Filters files by extension list or applies as fallback.
- Provides getter/setter access for configuration fields.

#### Public API
- getPromptKey() -> String selects the prompt template.
- setPromptKey(String) -> void assigns the prompt template key.
- getAiDefinitionKey() -> String selects the AI model definition.
- setAiDefinitionKey(String) -> void assigns the AI model definition key.
- getFileExtensions() -> List<String> filters files by extension.
- setFileExtensions(Collection<String>) -> void sets file extension filter.

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No explicit throws.
- Null handling in getFileExtensions() and setFileExtensions().
- Defensive copying prevents external mutation of fileExtensions.

#### Concurrency
- Not thread-safe due to mutable fields.
- No synchronization mechanisms.

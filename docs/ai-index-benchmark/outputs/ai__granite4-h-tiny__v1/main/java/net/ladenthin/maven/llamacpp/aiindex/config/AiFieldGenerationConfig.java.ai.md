### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:06:26Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Associates a prompt template key with an AI model definition key for field generation.

#### Type
- Class, final, no extends/implements, no generics.

#### Input
- Constructor and method parameters: `promptKey`, `aiDefinitionKey`, `fileExtensions`.
- Consumed fields: `promptKey`, `aiDefinitionKey`, `fileExtensions`.
- Read resources: none.

#### Output
- Return types: `String` for getters, `List<String>` for `getFileExtensions()`.
- Produced state: none.
- Mutated fields: `promptKey`, `aiDefinitionKey`, `fileExtensions`.
- Written resources: none.
- Side effects: none.

#### Core Logic
- Retrieves prompt key.
- Retrieves AI model definition key.
- Retrieves file extensions.
- Sets prompt key.
- Sets AI model definition key.
- Sets file extensions.

#### Public API
- `getPromptKey() -> String`: Returns the prompt template key.
- `setPromptKey(promptKey) -> void`: Sets the prompt template key.
- `getAiDefinitionKey() -> String`: Returns the AI model definition key.
- `setAiDefinitionKey(aiDefinitionKey) -> void`: Sets the AI model definition key.
- `getFileExtensions() -> @Nullable List<String>`: Returns the file extensions.
- `setFileExtensions(fileExtensions) -> void`: Sets the file extensions.

#### Dependencies
- Imports: `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- None notable.

#### Concurrency
- No threading, synchronization, immutability, or thread-safety notes.

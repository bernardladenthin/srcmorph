### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:03:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures a prompt template and AI model key for Maven index field generation.

#### Purpose
- Defines plugin configuration for a single AI field-generation step.
- Links prompt key to model definition for each file processed.

#### Type
- Class `AiFieldGenerationConfig`
- Mutability: mutable JavaBean
- Annotation: `@ToString`
- Suppresses initialization warnings.

#### Input
- Constructor: no-arg, no-op.
- Setters: `setPromptKey(String)`, `setAiDefinitionKey(String)`, `setFileExtensions(Collection<String>)`.
- Parameters: prompt key, model definition key, optional file extensions.

#### Output
- Getters: `getPromptKey()`, `getAiDefinitionKey()`, `getFileExtensions()`.
- `getFileExtensions()` returns unmodifiable list or null.
- Mutated internal state via setters.

#### Core logic
- Defensive copy of file extension collection on set.
- Null handling: null or empty collection → null fallback.
- `getFileExtensions()` wraps internal list with `Collections.unmodifiableList`.

#### Public API
- `AiFieldGenerationConfig() -> void` – create instance.
- `getPromptKey() -> String` – retrieve prompt key.
- `setPromptKey(String)` – set prompt key.
- `getAiDefinitionKey() -> String` – retrieve AI model key.
- `setAiDefinitionKey(String)` – set AI model key.
- `getFileExtensions() -> List<String>` – get unmodifiable file extensions.
- `setFileExtensions(Collection<String>)` – set file extensions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- Referenced types: `AiModelDefinition`, `AiPromptDefinition`, `AiFieldGenerationSelector`
- Maven plugin configuration context.

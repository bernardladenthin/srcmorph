### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T21:53:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines the configuration for a single AI field‑generation step, mapping a prompt key to a model key and optionally restricting it to file extensions.

#### Purpose
- Configure a single AI generation step.
- Associate a prompt template with a model definition.

#### Type
- `public class AiFieldGenerationConfig` – extends `Object`; annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: `AiFieldGenerationConfig()`.
- Setters: `setPromptKey(String)`, `setAiDefinitionKey(String)`, `setFileExtensions(Collection<String>)`.
- Maven injects values via setters.

#### Output
- `getPromptKey()` → `String` – returns prompt key.
- `getAiDefinitionKey()` → `String` – returns model key.
- `getFileExtensions()` → `@Nullable List<String>` – unmodifiable list or `null`.

#### Core logic
- Defensive copy of incoming file extensions (`new ArrayList<>(fileExtensions)`).
- Return unmodifiable view (`Collections.unmodifiableList`) in getter.
- Null handling: `null` or empty treated as fallback.

#### Public API
- `AiFieldGenerationConfig()` → void – default constructor.
- `String getPromptKey()` → prompt key.
- `void setPromptKey(String)` → set prompt key.
- `String getAiDefinitionKey()` → model key.
- `void setAiDefinitionKey(String)` → set model key.
- `@Nullable List<String> getFileExtensions()` → list or null.
- `void setFileExtensions(Collection<String>)` → copy list.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition`

#### Exceptions / Errors
- No checked or unchecked exceptions thrown or caught.

#### Concurrency
- None declared; class is mutable but no thread‑safety guarantees.

### AiFieldGenerationConfig.java
- H: 1.0
- C: D35DA51C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:05:26Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Configures AI field generation steps by linking prompt templates to model definitions and optional file extension filters.

#### Purpose
* Defines a mutable POJO for mapping prompt keys to AI model definitions within Maven plugin configuration.
* Selects field-generation operations based on source file extensions or as a fallback for all files.

#### Type
* Class: `AiFieldGenerationConfig`
* Annotations: `@ToString`, `@SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")`
* Fields: `private String promptKey`, `private String aiDefinitionKey`, `private @Nullable List<String> fileExtensions`

#### Input
* `String promptKey`: Key referencing an `AiPromptDefinition`.
* `String aiDefinitionKey`: Key referencing an `AiModelDefinition` for model parameters.
* `Collection<String> fileExtensions`: Optional list of file extensions (e.g., `.java`) to target.

#### Output
* `String promptKey`: Returns the configured prompt template key.
* `String aiDefinitionKey`: Returns the configured AI model definition key.
* `List<String> fileExtensions`: Returns an unmodifiable view or null for extension filtering.
* `void setFileExtensions(Collection<String>)`: Mutates internal list with defensive copy.

#### Core logic
* Associates a prompt template key with an AI model definition for single field-generation steps.
* Applies selection logic to files based on matching extensions or acts as a global fallback.
* Ensures immutability of returned lists via `Collections.unmodifiableList` and defensive copying during mutation.

#### Public API
* `getPromptKey() -> String`: Retrieves prompt template identifier.
* `setPromptKey(String) -> void`: Updates prompt template identifier.
* `getAiDefinitionKey() -> String`: Retrieves AI model definition identifier.
* `setAiDefinitionKey(String) -> void`: Updates AI model definition identifier.
* `getFileExtensions() -> List<String>`: Retrieves file extension filter list.
* `setFileExtensions(Collection<String>) -> void`: Sets file extension filter list with safety copy.

#### Dependencies
* `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`
* `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`
* `net.ladenthin.maven.llamacpp.aiindex.AiModelDefinition`
* `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* Handles null inputs gracefully in `setFileExtensions` by allowing null to become a fallback.
* No explicit exceptions thrown; relies on caller to provide valid keys and collection types.

#### Concurrency
* Thread-safe for read operations due to return of unmodifiable views or null.
* Mutable state in fields requires external synchronization if accessed concurrently by plugin framework.

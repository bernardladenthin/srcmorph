### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:51:26Z
- G: 
- A: 0.0.0
- X: file
---
> Root configuration object for the srcmorph engine, aggregating all parameters required for AI-driven source code analysis and generation.

#### Purpose
* Acts as a mutable JavaBean for binding configuration from Maven parameters, JSON/YAML files, or Java code.
* Centralizes settings for file filtering, AI provider selection, model definitions, and llama.cpp specific tuning.

#### Type
Class; public; final (implied by JavaBean usage, though not marked final). Annotated with `@ToString` (Lombok) and `@SuppressWarnings`.

#### Input
* Constructor: No-argument constructor.
* Setters: Accept `File`, `boolean`, `String`, `long`, `int`, `float`, and `List` of various configuration types (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`).

#### Output
* Getters: Return the configured state of the object.
* Side effects: None (pure data holder).

#### Core logic
* Maintains a set of default values for AI providers (`mock`), llama.cpp context size (`2048`), output tokens (`128`), temperature (`0.15f`), and AI version (`0.0.0`).
* Stores file system constraints including base directory, output directory, subtrees, glob excludes, and file size bounds.
* Manages AI routing via lists of prompt definitions, model definitions, and field generation configurations.
* Provides fallback parameters specifically for the llama.cpp JNI provider.

#### Public API
* `getBaseDirectory() -> File` / `setBaseDirectory(File) -> void`
* `getOutputDirectory() -> File` / `setOutputDirectory(File) -> void`
* `isForce() -> boolean` / `setForce(boolean) -> void`
* `getSubtrees() -> List<String>` / `setSubtrees(List<String>) -> void`
* `getExcludes() -> List<String>` / `setExcludes(List<String>) -> void`
* `getFileExtensions() -> List<String>` / `setFileExtensions(List<String>) -> void`
* `getMinFileSizeBytes() -> long` / `setMinFileSizeBytes(long) -> void`
* `getMaxFileSizeBytes() -> long` / `setMaxFileSizeBytes(long) -> void`
* `isPlanOnly() -> boolean` / `setPlanOnly(boolean) -> void`
* `getGenerationProvider() -> String` / `setGenerationProvider(String) -> void`
* `getPromptDefinitions() -> List<AiPromptDefinition>` / `setPromptDefinitions(List<AiPromptDefinition>) -> void`
* `getAiDefinitions() -> List<AiModelDefinition>` / `setAiDefinitions(List<AiModelDefinition>) -> void`
* `getModelDirectory() -> File` / `setModelDirectory(File) -> void`
* `getFieldGenerations() -> List<AiFieldGenerationConfig>` / `setFieldGenerations(List<AiFieldGenerationConfig>) -> void`
* `getFactDefinitions() -> List<AiFactDefinition>` / `setFactDefinitions(List<AiFactDefinition>) -> void`
* `getLlamaModelPath() -> String` / `setLlamaModelPath(String) -> void`
* `getLlamaContextSize() -> int` / `setLlamaContextSize(int) -> void`
* `getLlamaMaxOutputTokens() -> int` / `setLlamaMaxOutputTokens(int) -> void`
* `getLlamaTemperature() -> float` / `setLlamaTemperature(float) -> void`
* `getLlamaThreads() -> int` / `setLlamaThreads(int) -> void`
* `getPluginVersion() -> String` / `setPluginVersion(String) -> void`
* `getAiVersion() -> String` / `setAiVersion(String) -> void`
* `getProjectName() -> String` / `setProjectName(String) -> void`

#### Dependencies
* `java.io.File`
* `java.util.List`
* `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
* `AiModelDefinition`
* `AiFieldGenerationConfig`
* `AiFactDefinition`
* `AiGenerationConfig`
* `org.jspecify.annotations.Nullable`
* `lombok.ToString`

#### Concurrency
* Not thread-safe; designed as a mutable JavaBean for initialization.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:26:45Z
- G: 
- A: 0.0.0
- X: file
---
> Root configuration object holding all parameters required for a srcmorph AI generation run, designed for binding from Maven, JSON, or YAML.

#### Purpose
* Serves as a mutable JavaBean for centralizing AI generation settings.
* Provides a shared configuration schema across different binding surfaces (Maven plugin, CLI, etc.).

#### Type
* Class.
* Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
* Constructor: No-op default constructor.
* Setters: Accepts `File`, `String`, `boolean`, `long`, `int`, `float`, and `List` of configuration types (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`).

#### Output
* Getters: Returns the configured values or predefined defaults for AI providers, llama.cpp settings, and file system paths.

#### Core logic
* Maintains state for file system boundaries (`baseDirectory`, `outputDirectory`, `subtrees`, `excludes`, `fileExtensions`).
* Stores file size constraints (`minFileSizeBytes`, `maxFileSizeBytes`).
* Configures AI generation routing via `fieldGenerations`, `promptDefinitions`, and `aiDefinitions`.
* Provides fallback parameters specifically for `llama.cpp` JNI provider (context size, tokens, temperature, threads).
* Tracks metadata for output headers (`pluginVersion`, `aiVersion`).

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

#### Concurrency
* Mutable JavaBean; not thread-safe.

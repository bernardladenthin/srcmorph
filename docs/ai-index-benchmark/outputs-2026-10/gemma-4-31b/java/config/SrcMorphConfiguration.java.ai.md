### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:27:23Z
- G: 
- A: 0.0.0
- X: file
---
> Holds configuration parameters for AI-driven source code summarization and generation runs.

#### Purpose
* Centralized mutable configuration for the `srcmorph` engine.
* Provides a common binding target for Maven plugins, JSON/YAML configs, or manual Java instantiation.

#### Type
Class; mutable JavaBean. Annotated with `@ToString` and `@SuppressWarnings`.

#### Input
* No-arg constructor.
* Setters for all configuration fields, including `File` paths, `List` of strings/definitions, and primitive types (`long`, `int`, `float`, `boolean`).

#### Output
* Getters for all configuration fields.
* Default values provided for `outputDirectory`, `generationProvider`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, and `aiVersion`.

#### Core logic
* Acts as a Data Transfer Object (DTO) to decouple configuration sources (Maven, CLI, File) from the execution engine.
* Stores file system constraints (base/output directories, size bounds, extensions, and glob excludes).
* Manages AI routing and model parameters, including provider names, prompt templates, model definitions, and llama.cpp specific tuning.

#### Public API
`SrcMorphConfiguration()` -> void: initializes with defaults
`getBaseDirectory() -> File` / `setBaseDirectory(File) -> void`: project root directory
`getOutputDirectory() -> File` / `setOutputDirectory(File) -> void`: destination for .ai.md files
`isForce() -> boolean` / `setForce(boolean) -> void`: force regeneration of AI fields
`getSubtrees() -> List<String>` / `setSubtrees(List<String>) -> void`: source subdirectories to process
`getExcludes() -> List<String>` / `setExcludes(List<String>) -> void`: glob patterns to skip
`getFileExtensions() -> List<String>` / `setFileExtensions(List<String>) -> void`: extensions to index
`getMinFileSizeBytes() -> long` / `setMinFileSizeBytes(long) -> void`: lower file size bound
`getMaxFileSizeBytes() -> long` / `setMaxFileSizeBytes(long) -> void`: upper file size bound
`isPlanOnly() -> boolean` / `setPlanOnly(boolean) -> void`: stop after printing routing plan
`getGenerationProvider() -> String` / `setGenerationProvider(String) -> void`: AI provider name
`getPromptDefinitions() -> List<AiPromptDefinition>` / `setPromptDefinitions(List<AiPromptDefinition>) -> void`: prompt template definitions
`getAiDefinitions() -> List<AiModelDefinition>` / `setAiDefinitions(List<AiModelDefinition>) -> void`: AI model parameter mappings
`getModelDirectory() -> File` / `setModelDirectory(File) -> void`: base directory for relative model paths
`getFieldGenerations() -> List<AiFieldGenerationConfig>` / `setFieldGenerations(List<AiFieldGenerationConfig>) -> void`: per-field routing rules
`getFactDefinitions() -> List<AiFactDefinition>` / `setFactDefinitions(List<AiFactDefinition>) -> void`: reusable named fact groups
`getLlamaModelPath() -> String` / `setLlamaModelPath(String) -> void`: fallback GGUF model path
`getLlamaContextSize() -> int` / `setLlamaContextSize(int) -> void`: fallback context window size
`getLlamaMaxOutputTokens() -> int` / `setLlamaMaxOutputTokens(int) -> void`: fallback max output tokens
`getLlamaTemperature() -> float` / `setLlamaTemperature(float) -> void`: fallback sampling temperature
`getLlamaThreads() -> int` / `setLlamaThreads(int) -> void`: fallback CPU thread count
`getPluginVersion() -> String` / `setPluginVersion(String) -> void`: generator version for headers
`getAiVersion() -> String` / `setAiVersion(String) -> void`: AI logic version for headers
`getProjectName() -> String` / `setProjectName(String) -> void`: project title for aggregate index

#### Dependencies
* `java.io.File`
* `java.util.List`
* `lombok.ToString`
* `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
* `org.jspecify.annotations.Nullable`
* `AiModelDefinition`
* `AiFieldGenerationConfig`
* `AiFactDefinition`
* `AiGenerationConfig`

#### Concurrency
* Mutable JavaBean; not thread-safe.

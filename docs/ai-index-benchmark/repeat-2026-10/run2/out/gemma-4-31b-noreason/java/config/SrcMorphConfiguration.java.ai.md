### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:54:09Z
- G: 
- A: 0.0.0
- X: file
---
> Root configuration object holding all parameters for a srcmorph AI generation run, bindable from Maven, JSON, YAML, or Java code.

#### Purpose
* Acts as a mutable JavaBean for all engine parameters.
* Provides a single shared configuration surface for different binding technologies.

#### Type
Class; public; final-like (mutable JavaBean). Annotated with `@ToString` (Lombok) and `@SuppressWarnings`.

#### Input
* Constructor: No-arg default.
* Setters: Accept `File`, `boolean`, `String`, `long`, `int`, `float`, and `List` of configuration types (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`).

#### Output
* Getters: Return the configured state of the engine parameters.

#### Core logic
* Maintains state for file system boundaries (`baseDirectory`, `outputDirectory`, `subtrees`, `excludes`, `fileExtensions`).
* Stores file size constraints (`minFileSizeBytes`, `maxFileSizeBytes`).
* Manages AI provider settings (`generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`).
* Holds fallback llama.cpp specific parameters (`llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`).
* Tracks metadata for output headers (`pluginVersion`, `aiVersion`, `projectName`).

#### Public API
* `getBaseDirectory() -> File` / `setBaseDirectory(File)`
* `getOutputDirectory() -> File` / `setOutputDirectory(File)`
* `isForce() -> boolean` / `setForce(boolean)`
* `getSubtrees() -> List<String>` / `setSubtrees(List<String>)`
* `getExcludes() -> List<String>` / `setExcludes(List<String>)`
* `getFileExtensions() -> List<String>` / `setFileExtensions(List<String>)`
* `getMinFileSizeBytes() -> long` / `setMinFileSizeBytes(long)`
* `getMaxFileSizeBytes() -> long` / `setMaxFileSizeBytes(long)`
* `isPlanOnly() -> boolean` / `setPlanOnly(boolean)`
* `getGenerationProvider() -> String` / `setGenerationProvider(String)`
* `getPromptDefinitions() -> List<AiPromptDefinition>` / `setPromptDefinitions(List<AiPromptDefinition>)`
* `getAiDefinitions() -> List<AiModelDefinition>` / `setAiDefinitions(List<AiModelDefinition>)`
* `getModelDirectory() -> File` / `setModelDirectory(File)`
* `getFieldGenerations() -> List<AiFieldGenerationConfig>` / `setFieldGenerations(List<AiFieldGenerationConfig>)`
* `getFactDefinitions() -> List<AiFactDefinition>` / `setFactDefinitions(List<AiFactDefinition>)`
* `getLlamaModelPath() -> String` / `setLlamaModelPath(String)`
* `getLlamaContextSize() -> int` / `setLlamaContextSize(int)`
* `getLlamaMaxOutputTokens() -> int` / `setLlamaMaxOutputTokens(int)`
* `getLlamaTemperature() -> float` / `setLlamaTemperature(float)`
* `getLlamaThreads() -> int` / `setLlamaThreads(int)`
* `getPluginVersion() -> String` / `setPluginVersion(String)`
* `getAiVersion() -> String` / `setAiVersion(String)`
* `getProjectName() -> String` / `setProjectName(String)`

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
* Mutable JavaBean; not thread-safe.

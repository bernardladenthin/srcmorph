### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:42:44Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable configuration object aggregating all parameters for AI-driven source code generation and summarization.

#### Purpose
- Serves as the single shared configuration container for the `srcmorph` engine, bindable from Maven plugins, JSON/YAML files, or direct Java construction.
- Holds mutable JavaBean fields mirroring Maven `@Parameter` names to enable consistent configuration binding across different input surfaces.

#### Type
- `public class SrcMorphConfiguration`
- Modifiers: `public`, `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No explicit `extends` or `implements` clauses.
- Key generics: `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`.

#### Input
- **Constructor**: `SrcMorphConfiguration()` (no-arg, applies defaults).
- **Setter Parameters**:
  - `baseDirectory` (File): Project root.
  - `outputDirectory` (File): Destination for `.ai.md` files.
  - `force` (boolean): Force regeneration flag.
  - `subtrees` (List<String>): Source subdirectory paths.
  - `excludes` (List<String>): Glob patterns for files to skip.
  - `fileExtensions` (List<String>): Extensions to index.
  - `minFileSizeBytes` (long), `maxFileSizeBytes` (long): File size bounds.
  - `planOnly` (boolean): Stop after planning flag.
  - `generationProvider` (String): AI provider name (e.g., "mock", "llamacpp-jni").
  - `promptDefinitions` (List<AiPromptDefinition>): Prompt templates.
  - `aiDefinitions` (List<AiModelDefinition>): Model parameter definitions.
  - `modelDirectory` (File): Base directory for relative model paths.
  - `fieldGenerations` (List<AiFieldGenerationConfig>): Per-field routing rules.
  - `factDefinitions` (List<AiFactDefinition>): Reusable fact groups.
  - `llamaModelPath` (String): Path to GGUF model file.
  - `llamaContextSize` (int), `llamaMaxOutputTokens` (int), `llamaTemperature` (float), `llamaThreads` (int): llama.cpp inference parameters.
  - `pluginVersion` (String), `aiVersion` (String): Version strings for headers.
  - `projectName` (String): Project title.

#### Output
- **Return Types**:
  - `File`: `getBaseDirectory()`, `getOutputDirectory()`, `getModelDirectory()`.
  - `boolean`: `isForce()`, `isPlanOnly()`.
  - `List<String>`: `getSubtrees()`, `getExcludes()`, `getFileExtensions()`.
  - `long`: `getMinFileSizeBytes()`, `getMaxFileSizeBytes()`.
  - `String`: `getGenerationProvider()`, `getLlamaModelPath()`, `getPluginVersion()`, `getAiVersion()`, `getProjectName()`.
  - `int`: `getLlamaContextSize()`, `getLlamaMaxOutputTokens()`, `getLlamaThreads()`.
  - `float`: `getLlamaTemperature()`.
  - `List<?>`: `getPromptDefinitions()`, `getAiDefinitions()`, `getFieldGenerations()`, `getFactDefinitions()`.
- **State**: Mutates internal private fields via setters.

#### Core logic
- Initializes default values for output directory, generation provider, and llama.cpp parameters upon instantiation.
- Provides standard JavaBean getters and setters for all configuration fields to support reflection-based binding (Maven Plexus, Jackson, YAML).
- Distinguishes between global fallback parameters (e.g., `llamaContextSize`) and per-field routing configurations (`fieldGenerations`).
- Maintains separation of concerns by excluding lifecycle skip flags (e.g., `skipFile`), keeping them in the Maven mojo layer.
- Resolves relative model paths against `modelDirectory` if provided, otherwise defaults to JVM working directory.

#### Public API
- `getBaseDirectory() -> File`
- `setBaseDirectory(File) -> void`
- `getOutputDirectory() -> File`
- `setOutputDirectory(File) -> void`
- `isForce() -> boolean`
- `setForce(boolean) -> void`
- `getSubtrees() -> List<String>`
- `setSubtrees(List<String>) -> void`
- `getExcludes() -> List<String>`
- `setExcludes(List<String>) -> void`
- `getFileExtensions() -> List<String>`
- `setFileExtensions(List<String>) -> void`
- `getMinFileSizeBytes() -> long`
- `setMinFileSizeBytes(long) -> void`
- `getMaxFileSizeBytes() -> long`
- `setMaxFileSizeBytes(long) -> void`
- `isPlanOnly() -> boolean`
- `setPlanOnly(boolean) -> void`
- `getGenerationProvider() -> String`
- `setGenerationProvider(String) -> void`
- `getPromptDefinitions() -> List<AiPromptDefinition>`
- `setPromptDefinitions(List<AiPromptDefinition>) -> void`
- `getAiDefinitions() -> List<AiModelDefinition>`
- `setAiDefinitions(List<AiModelDefinition>) -> void`
- `getModelDirectory() -> File`
- `setModelDirectory(File) -> void`
- `getFieldGenerations() -> List<AiFieldGenerationConfig>`
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void`
- `getFactDefinitions() -> List<AiFactDefinition>`
- `setFactDefinitions(List<AiFactDefinition>) -> void`
- `getLlamaModelPath() -> String`
- `setLlamaModelPath(String) -> void`
- `getLlamaContextSize() -> int`
- `setLlamaContextSize(int) -> void`
- `getLlamaMaxOutputTokens() -> int`
- `setLlamaMaxOutputTokens(int) -> void`
- `getLlamaTemperature() -> float`
- `setLlamaTemperature(float) -> void`
- `getLlamaThreads() -> int`
- `setLlamaThreads(int) -> void`
- `getPluginVersion() -> String`
- `setPluginVersion(String) -> void`
- `getAiVersion() -> String`
- `setAiVersion(String) -> void`
- `getProjectName() -> String`
- `setProjectName(String) -> void`

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (via `DEFAULT_THREADS`)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No checked exceptions declared.
- Null handling: Several fields are annotated `@Nullable` and return `null` if not configured (e.g., `subtrees`, `excludes`, `promptDefinitions`).
- No explicit null checks or validation logic present in the class itself.

#### Concurrency
- No synchronization or thread-safety mechanisms.
- Mutable state implies the class is not thread-safe for concurrent modification without external synchronization.

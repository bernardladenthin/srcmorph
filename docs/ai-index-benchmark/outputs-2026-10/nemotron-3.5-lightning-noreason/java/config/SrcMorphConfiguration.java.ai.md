### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:51:36Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and holds all parameters required for a srcmorph AI code generation run, independent of the binding source (Maven, JSON/YAML, or direct Java construction).

#### Purpose
Root mutable JavaBean that centralizes every parameter a srcmorph run needs, designed to be bound from Maven mojos, JSON/YAML configs, or direct Java construction. Field names mirror Maven `@Parameter` names to enable unified JSON/YAML configuration.

#### Type
Class `net.ladenthin.srcmorph.config.SrcMorphConfiguration`. Mutable JavaBean with `@ToString`. No explicit `extends` or `implements`. Uses Lombok `@ToString`.

#### Input
Constructor: default no-arg constructor that applies all defaults. Parameters for all setter methods. Injected dependencies: none directly, but intended to be populated by Maven plexus reflection, Jackson/YAML Mappers, or direct code construction. Consumed fields from sibling modules (e.g., mojo classes in `net.ladenthin.maven.srcmorph.mojo`).

#### Output
Return types from getters: `File`, `String`, `int`, `long`, `float`, `boolean`, `@Nullable List<AiPromptDefinition>`, `@Nullable List<AiModelDefinition>`, `@Nullable List<AiFieldGenerationConfig>`, `@Nullable List<AiFactDefinition>`, `@Nullable File`, `@Nullable String`. Produces written `.ai.md` files via downstream processing. Side effects include setting configuration state used by the generation pipeline.

#### Core logic
- Holds default constants for output directory (`src/site/ai`), generation provider (`mock`), llama context size (2048), max output tokens (128), temperature (0.15f), threads (`AiGenerationConfig.DEFAULT_THREADS`), and AI version (`0.0.0`).
- Provides getters and setters for all configuration fields, enabling binding from any technology that can populate a mutable JavaBean.
- Fields control source processing: base directory, output directory, file size bounds (`minFileSizeBytes`, `maxFileSizeBytes`), file extensions, subtrees, excludes.
- AI generation parameters: provider name, model path, context size, max tokens, temperature, thread count.
- Routing configurations: prompt definitions, AI model definitions, field generation rules, fact definitions.
- Project metadata: plugin version, AI version, project name.

#### Public API
- `getBaseDirectory() -> File`
- `setBaseDirectory(File) -> void`
- `getOutputDirectory() -> File`
- `setOutputDirectory(File) -> void`
- `isForce() -> boolean`
- `setForce(boolean) -> void`
- `getSubtrees() -> @Nullable List<String>`
- `setSubtrees(@Nullable List<String>) -> void`
- `getExcludes() -> @Nullable List<String>`
- `setExcludes(@Nullable List<String>) -> void`
- `getFileExtensions() -> @Nullable List<String>`
- `setFileExtensions(@Nullable List<String>) -> void`
- `getMinFileSizeBytes() -> long`
- `setMinFileSizeBytes(long) -> void`
- `getMaxFileSizeBytes() -> long`
- `setMaxFileSizeBytes(long) -> void`
- `isPlanOnly() -> boolean`
- `setPlanOnly(boolean) -> void`
- `getGenerationProvider() -> String`
- `setGenerationProvider(String) -> void`
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>`
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void`
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>`
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void`
- `getModelDirectory() -> @Nullable File`
- `setModelDirectory(@Nullable File) -> void`
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>`
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void`
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>`
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void`
- `getLlamaModelPath() -> @Nullable String`
- `setLlamaModelPath(@Nullable String) -> void`
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
- `getProjectName() -> @Nullable String`
- `setProjectName(@Nullable String) -> void`

Total: 38 public methods (2 getters/setters per field for 19 fields).

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
No explicit exception throwing in source. Uses `@Nullable` on list-returning getters and fields (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`) to indicate may return null. No custom exceptions defined in this file.

#### Concurrency
No threading or synchronization mechanisms present. Designed as a mutable JavaBean for simple state holder use; thread-safety not guaranteed and not addressed in source.

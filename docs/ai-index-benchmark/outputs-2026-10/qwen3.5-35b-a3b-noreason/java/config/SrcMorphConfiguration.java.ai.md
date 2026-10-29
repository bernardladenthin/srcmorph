### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:04Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable configuration object aggregating all parameters required to execute AI-driven source code morphing operations, including file filtering, AI model routing, and generation provider settings.

#### Purpose
*   Serves as the single shared configuration bean for the `srcmorph` engine, bindable from Maven plugins, JSON/YAML files, or direct Java construction.
*   Holds mutable state for file scope, AI provider selection, model definitions, and output directory settings.

#### Type
*   `public class SrcMorphConfiguration`
*   Modifiers: `public`, `@ToString` (Lombok)
*   Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@Nullable` (JSpecify)
*   No explicit `extends` or `implements`.

#### Input
*   **Constructor**: `SrcMorphConfiguration()` (no-arg, applies defaults).
*   **Setter Parameters**:
    *   `File baseDirectory`, `File outputDirectory`, `File modelDirectory`.
    *   `boolean force`, `boolean planOnly`.
    *   `List<String> subtrees`, `List<String> excludes`, `List<String> fileExtensions`.
    *   `long minFileSizeBytes`, `long maxFileSizeBytes`.
    *   `String generationProvider`, `String pluginVersion`, `String aiVersion`, `String projectName`.
    *   `List<AiPromptDefinition> promptDefinitions`.
    *   `List<AiModelDefinition> aiDefinitions`.
    *   `List<AiFieldGenerationConfig> fieldGenerations`.
    *   `List<AiFactDefinition> factDefinitions`.
    *   `String llamaModelPath`.
    *   `int llamaContextSize`, `int llamaMaxOutputTokens`, `int llamaThreads`.
    *   `float llamaTemperature`.

#### Output
*   **Return Types**: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `int`, `float`.
*   **State Mutations**: Sets internal private fields for all configuration parameters.
*   **Side Effects**: None directly; acts as a data carrier for the engine.

#### Core logic
*   Initializes default values for output directory, generation provider, and llama.cpp parameters upon instantiation.
*   Provides standard JavaBean getters and setters to allow binding via Maven Plexus reflection, Jackson/YAML mappers, or direct code.
*   Stores routing rules (`fieldGenerations`), prompt templates (`promptDefinitions`), and model definitions (`aiDefinitions`) for the generation engine.
*   Configures file filtering via subtrees, excludes, extensions, and size bounds.
*   Maintains fallback llama.cpp settings (context size, tokens, temperature, threads) for when specific field generation rules are absent.
*   Records plugin and AI logic versions in generated `.ai.md` headers.

#### Public API
*   `getBaseDirectory() -> File`
*   `setBaseDirectory(File) -> void`
*   `getOutputDirectory() -> File`
*   `setOutputDirectory(File) -> void`
*   `isForce() -> boolean`
*   `setForce(boolean) -> void`
*   `getSubtrees() -> List<String>`
*   `setSubtrees(List<String>) -> void`
*   `getExcludes() -> List<String>`
*   `setExcludes(List<String>) -> void`
*   `getFileExtensions() -> List<String>`
*   `setFileExtensions(List<String>) -> void`
*   `getMinFileSizeBytes() -> long`
*   `setMinFileSizeBytes(long) -> void`
*   `getMaxFileSizeBytes() -> long`
*   `setMaxFileSizeBytes(long) -> void`
*   `isPlanOnly() -> boolean`
*   `setPlanOnly(boolean) -> void`
*   `getGenerationProvider() -> String`
*   `setGenerationProvider(String) -> void`
*   `getPromptDefinitions() -> List<AiPromptDefinition>`
*   `setPromptDefinitions(List<AiPromptDefinition>) -> void`
*   `getAiDefinitions() -> List<AiModelDefinition>`
*   `setAiDefinitions(List<AiModelDefinition>) -> void`
*   `getModelDirectory() -> File`
*   `setModelDirectory(File) -> void`
*   `getFieldGenerations() -> List<AiFieldGenerationConfig>`
*   `setFieldGenerations(List<AiFieldGenerationConfig>) -> void`
*   `getFactDefinitions() -> List<AiFactDefinition>`
*   `setFactDefinitions(List<AiFactDefinition>) -> void`
*   `getLlamaModelPath() -> String`
*   `setLlamaModelPath(String) -> void`
*   `getLlamaContextSize() -> int`
*   `setLlamaContextSize(int) -> void`
*   `getLlamaMaxOutputTokens() -> int`
*   `setLlamaMaxOutputTokens(int) -> void`
*   `getLlamaTemperature() -> float`
*   `setLlamaTemperature(float) -> void`
*   `getLlamaThreads() -> int`
*   `setLlamaThreads(int) -> void`
*   `getPluginVersion() -> String`
*   `setPluginVersion(String) -> void`
*   `getAiVersion() -> String`
*   `setAiVersion(String) -> void`
*   `getProjectName() -> String`
*   `setProjectName(String) -> void`

#### Dependencies
*   `java.io.File`
*   `java.util.List`
*   `lombok.ToString`
*   `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
*   `net.ladenthin.srcmorph.config.AiModelDefinition`
*   `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
*   `net.ladenthin.srcmorph.config.AiFactDefinition`
*   `net.ladenthin.srcmorph.config.AiGenerationConfig` (via `DEFAULT_THREADS`)
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   No checked exceptions declared.
*   No explicit null-handling logic; relies on `@Nullable` annotations for static analysis tools.

#### Concurrency
*   Mutable JavaBean; not thread-safe for concurrent modification.
*   No synchronization or immutability guarantees.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:59Z
- G: 
- A: 0.0.0
- X: file
---
> Holds every parameter a `srcmorph` run needs, independent of binding technology.

#### Purpose
- Holds configuration parameters for `srcmorph` runs.
- Supports multiple binding technologies (Maven, JSON/YAML, plain Java).

#### Type
- Class
- Public
- Lombok `@ToString`
- No modifiers
- No `extends` or `implements`
- No key generics or type bounds
- No notable annotations

#### Input
- Constructor parameters: none
- Injected dependencies: none
- Consumed fields: none
- Read resources: none

#### Output
- Return types: `File`, `boolean`, `List<String>`, `long`, `int`, `float`, `String`
- Produced state: none
- Mutated fields: `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`
- Written resources: none
- Side effects: none

#### Core logic
- Initializes default values for configuration parameters.
- Provides getter and setter methods for each configuration parameter.

#### Public API
- `getBaseDirectory() -> File`: Returns the project base directory.
- `setBaseDirectory(File baseDirectory)`: Sets the project base directory.
- `getOutputDirectory() -> File`: Returns the output directory.
- `setOutputDirectory(File outputDirectory)`: Sets the output directory.
- `isForce() -> boolean`: Returns whether AI fields are regenerated even when they already have a value.
- `setForce(boolean force)`: Sets whether AI fields are regenerated even when they already have a value.
- `getSubtrees() -> List<String>`: Returns the source subdirectory paths that restrict processing.
- `setSubtrees(List<String> subtrees)`: Sets the source subdirectory paths that restrict processing.
- `getExcludes() -> List<String>`: Returns the glob patterns for source files to skip.
- `setExcludes(List<String> excludes)`: Sets the glob patterns for source files to skip.
- `getFileExtensions() -> List<String>`: Returns the file extensions to index.
- `setFileExtensions(List<String> fileExtensions)`: Sets the file extensions to index.
- `getMinFileSizeBytes() -> long`: Returns the exclusive lower file-size bound in bytes.
- `setMinFileSizeBytes(long minFileSizeBytes)`: Sets the exclusive lower file-size bound in bytes.
- `getMaxFileSizeBytes() -> long`: Returns the inclusive upper file-size bound in bytes.
- `setMaxFileSizeBytes(long maxFileSizeBytes)`: Sets the inclusive upper file-size bound in bytes.
- `isPlanOnly() -> boolean`: Returns whether a `generate` run stops after printing the routing plan.
- `setPlanOnly(boolean planOnly)`: Sets whether a `generate` run stops after printing the routing plan.
- `getGenerationProvider() -> String`: Returns the name of the AI generation provider to use.
- `setGenerationProvider(String generationProvider)`: Sets the name of the AI generation provider to use.
- `getPromptDefinitions() -> List<AiPromptDefinition>`: Returns the prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition> promptDefinitions)`: Sets the prompt template definitions.
- `getAiDefinitions() -> List<AiModelDefinition>`: Returns the AI model definitions.
- `setAiDefinitions(List<AiModelDefinition> aiDefinitions)`: Sets the AI model definitions.
- `getModelDirectory() -> File`: Returns the directory a relative model path is resolved against.
- `setModelDirectory(File modelDirectory)`: Sets the directory a relative model path is resolved against.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>`: Returns the per-field AI generation configurations.
- `setFieldGenerations(List<AiFieldGenerationConfig> fieldGenerations)`: Sets the per-field AI generation configurations.
- `getFactDefinitions() -> List<AiFactDefinition>`: Returns the reusable, named `factDefinitions` groups.
- `setFactDefinitions(List<AiFactDefinition> factDefinitions)`: Sets the reusable, named `factDefinitions` groups.
- `getLlamaModelPath() -> String`: Returns the path to the GGUF model file.
- `setLlamaModelPath(String llamaModelPath)`: Sets the path to the GGUF model file.
- `getLlamaContextSize() -> int`: Returns the llama.cpp context window size.
- `setLlamaContextSize(int llamaContextSize)`: Sets the llama.cpp context window size.
- `getLlamaMaxOutputTokens() -> int`: Returns the maximum number of llama.cpp output tokens per call.
- `setLlamaMaxOutputTokens(int llamaMaxOutputTokens)`: Sets the maximum number of llama.cpp output tokens per call.
- `getLlamaTemperature() -> float`: Returns the llama.cpp sampling temperature.
- `setLlamaTemperature(float llamaTemperature)`: Sets the llama.cpp sampling temperature.
- `getLlamaThreads() -> int`: Returns the number of CPU threads for llama.cpp inference.
- `setLlamaThreads(int llamaThreads)`: Sets the number of CPU threads for llama.cpp inference.
- `getPluginVersion() -> String`: Returns the generator (plugin) version.
- `setPluginVersion(String pluginVersion)`: Sets the generator (plugin) version.
- `getAiVersion() -> String`: Returns the AI summarisation logic version.
- `setAiVersion(String aiVersion)`: Sets the AI summarisation logic version.
- `getProjectName() -> String`: Returns the project title.
- `setProjectName(String projectName)`: Sets the project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable exceptions
- Null-handling for optional fields (`@Nullable`)

#### Concurrency
- No threading or synchronization notes

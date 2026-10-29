### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:02:30Z
- G: 
- A: 0.0.0
- X: file
---
> Holds the single mutable configuration object binding all parameters for a srcmorph AI code generation run, unifying Maven Mojo fields, JSON/YAML configs, and direct instantiation.

#### Purpose
- Serves as the central mutable JavaBean holding all parameters required for a srcmorph AI code generation execution, independent of binding source.
- Provides getters/setters for directory paths, AI model settings, file filtering rules, and prompt definitions to be consumed by the engine.

#### Type
- Class `SrcMorphConfiguration` (public, final, mutable JavaBean).
- Extends `Object`.
- Annotations: `@ToString`, `@SuppressWarnings`.
- Key fields: `File baseDirectory`, `File outputDirectory`, `boolean force`, `List<String> subtrees`, `List<String> excludes`, `List<String> fileExtensions`, `long minFileSizeBytes`, `long maxFileSizeBytes`, `boolean planOnly`, `String generationProvider`, `List<AiPromptDefinition> promptDefinitions`, `List<AiModelDefinition> aiDefinitions`, `File modelDirectory`, `List<AiFieldGenerationConfig> fieldGenerations`, `List<AiFactDefinition> factDefinitions`, `String llamaModelPath`, `int llamaContextSize`, `int llamaMaxOutputTokens`, `float llamaTemperature`, `int llamaThreads`, `String pluginVersion`, `String aiVersion`, `String projectName`.

#### Input
- Constructor: None (uses defaults).
- Parameters: All fields accept `File`, `String`, `boolean`, `long`, `float`, `int`, or `List<T>` via setters.
- Dependencies: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `File`.
- Resources: None consumed directly; relies on external configuration sources (Maven, JSON, YAML, code).

#### Output
- Return types: `File`, `boolean`, `List<String>`, `long`, `int`, `float`, `String`.
- Produced state: None (pure data holder).
- Written resources: None (no direct I/O).
- Side effects: None.

#### Core logic
- Initializes all fields to default values or null in the constructor.
- Provides getter/setter pairs for every configuration parameter to allow binding from Maven, JSON, YAML, or direct code.
- Enforces field name mirroring with Maven Mojo `@Parameter` fields for future config compatibility.
- Excludes Maven lifecycle skip flags from this class; those remain mojo-side.

#### Public API
- `getBaseDirectory() -> File` + returns the project base directory.
- `setBaseDirectory(File) -> void` + sets the project base directory.
- `getOutputDirectory() -> File` + returns the output directory for .ai.md files.
- `setOutputDirectory(File) -> void` + sets the output directory for .ai.md files.
- `isForce() -> boolean` + returns whether AI fields are always regenerated.
- `setForce(boolean) -> void` + sets whether AI fields are always regenerated.
- `getSubtrees() -> List<String>` + returns source subdirectory paths restricting processing.
- `setSubtrees(List<String>) -> void` + sets source subdirectory paths restricting processing.
- `getExcludes() -> List<String>` + returns glob patterns for source files to skip.
- `setExcludes(List<String>) -> void` + sets glob patterns for source files to skip.
- `getFileExtensions() -> List<String>` + returns file extensions to index.
- `setFileExtensions(List<String>) -> void` + sets file extensions to index.
- `getMinFileSizeBytes() -> long` + returns the exclusive lower file-size bound in bytes.
- `setMinFileSizeBytes(long) -> void` + sets the exclusive lower file-size bound in bytes.
- `getMaxFileSizeBytes() -> long` + returns the inclusive upper file-size bound in bytes.
- `setMaxFileSizeBytes(long) -> void` + sets the inclusive upper file-size bound in bytes.
- `isPlanOnly() -> boolean` + returns whether a generate run stops after planning.
- `setPlanOnly(boolean) -> void` + sets whether a generate run stops after planning.
- `getGenerationProvider() -> String` + returns the AI generation provider name.
- `setGenerationProvider(String) -> void` + sets the AI generation provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>` + returns prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition>) -> void` + sets prompt template definitions.
- `getAiDefinitions() -> List<AiModelDefinition>` + returns AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>) -> void` + sets AI model definitions.
- `getModelDirectory() -> File` + returns the directory for resolving relative model paths.
- `setModelDirectory(File) -> void` + sets the directory for resolving relative model paths.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` + returns per-field AI generation configurations.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` + sets per-field AI generation configurations.
- `getFactDefinitions() -> List<AiFactDefinition>` + returns reusable fact definition groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` + sets reusable fact definition groups.
- `getLlamaModelPath() -> String` + returns the path to the GGUF model file for llama.cpp fallback.
- `setLlamaModelPath(String) -> void` + sets the path to the GGUF model file for llama.cpp fallback.
- `getLlamaContextSize() -> int` + returns the llama.cpp context window size for fallback.
- `setLlamaContextSize(int) -> void` + sets the llama.cpp context window size for fallback.
- `getLlamaMaxOutputTokens() -> int` + returns the max llama.cpp output tokens for fallback.
- `setLlamaMaxOutputTokens(int) -> void` + sets the max llama.cpp output tokens for fallback.
- `getLlamaTemperature() -> float` + returns the llama.cpp sampling temperature for fallback.
- `setLlamaTemperature(float) -> void` + sets the llama.cpp sampling temperature for fallback.
- `getLlamaThreads() -> int` + returns the CPU thread count for llama.cpp inference.
- `setLlamaThreads(int) -> void` + sets the CPU thread count for llama.cpp inference.
- `getPluginVersion() -> String` + returns the generator version recorded in .ai.md headers.
- `setPluginVersion(String) -> void` + sets the generator version recorded in .ai.md headers.
- `getAiVersion() -> String` + returns the AI summarisation logic version recorded in .ai.md headers.
- `setAiVersion(String) -> void` + sets the AI summarisation logic version recorded in .ai.md headers.
- `getProjectName() -> String` + returns the project title recorded as the aggregate-project index title.
- `setProjectName(String) -> void` + sets the project title recorded as the aggregate-project index title.
- Total: 36 public methods (18 getters, 18 setters).

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `AiModelDefinition`
- `AiFieldGenerationConfig`
- `AiFactDefinition`

#### Exceptions / Errors
- No exceptions thrown.
- Null handling: Fields are nullable (`@Nullable`), allowing null to indicate "use default" or "not configured".
- Error conditions: None; relies on caller to validate configuration values.

#### Concurrency
- Thread-safe: No shared mutable state; setters are the only entry points for modification.
- Immutability: None; designed as a mutable JavaBean for binding.
- Synchronization: None required.

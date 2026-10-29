### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:03:12Z
- G: 
- A: 0.0.0
- X: file
---
> This class serves as the centralized, mutable configuration object holding all parameters required for an AI-driven source code morphing process.

#### Purpose
* Holds all parameters for a `srcmorph` run, bindable from Maven, JSON/YAML, or direct Java construction.
* Acts as the single shared configuration object across different binding technologies.

#### Type
class SrcMorphConfiguration

#### Input
* Constructor: No arguments (applies defaults).
* Method parameters: `File baseDirectory`, `File outputDirectory`, `boolean force`, `List<String> subtrees`, `List<String> excludes`, `List<String> fileExtensions`, `long minFileSizeBytes`, `long maxFileSizeBytes`, `boolean planOnly`, `String generationProvider`, `List<AiPromptDefinition> promptDefinitions`, `List<AiModelDefinition> aiDefinitions`, `File modelDirectory`, `List<AiFieldGenerationConfig> fieldGenerations`, `List<AiFactDefinition> factDefinitions`, `String llamaModelPath`, `int llamaContextSize`, `int llamaMaxOutputTokens`, `float llamaTemperature`, `int llamaThreads`, `String pluginVersion`, `String aiVersion`, `String projectName`.

#### Output
* Return types: `File`, `boolean`, `List<String>`, `long`, `String`, `int`, `float`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`.
* Side effects: Mutates internal state fields upon setter calls.

#### Core logic
* Initializes with default values for configuration parameters (e.g., `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_LLAMA_CONTEXT_SIZE`).
* Provides getters and setters for all configuration aspects: file paths, inclusion/exclusion criteria, AI model definitions, prompt definitions, and LLama.cpp inference settings.

#### Public API
getBaseDirectory() -> File: Retrieves the project base directory.
setBaseDirectory(File baseDirectory) -> void: Sets the project base directory.
getOutputDirectory() -> File: Retrieves the output directory for `.ai.md` files.
setOutputDirectory(File outputDirectory) -> void: Sets the output directory for `.ai.md` files.
isForce() -> boolean: Checks if AI fields should always regenerate.
setForce(boolean force) -> void: Sets whether AI fields are always regenerated.
getSubtrees() -> List<String>: Retrieves configured source subdirectory paths.
setSubtrees(List<String> subtrees) -> void: Sets source subdirectory paths restricting processing.
getExcludes() -> List<String>: Retrieves glob patterns for source files to skip.
setExcludes(List<String> excludes) -> void: Sets glob patterns for source files to skip.
getFileExtensions() -> List<String>: Retrieves file extensions to index.
setFileExtensions(List<String> fileExtensions) -> void: Sets file extensions to index.
getMinFileSizeBytes() -> long: Retrieves the exclusive lower file-size bound.
setMinFileSizeBytes(long minFileSizeBytes) -> void: Sets the exclusive lower file-size bound.
getMaxFileSizeBytes() -> long: Retrieves the inclusive upper file-size bound.
setMaxFileSizeBytes(long maxFileSizeBytes) -> void: Sets the inclusive upper file-size bound.
isPlanOnly() -> boolean: Checks if the run stops after routing plan.
setPlanOnly(boolean planOnly) -> void: Sets if the run stops after planning.
getGenerationProvider() -> String: Retrieves the AI generation provider name.
setGenerationProvider(String generationProvider) -> void: Sets the AI generation provider name.
getPromptDefinitions() -> List<AiPromptDefinition>: Retrieves prompt template definitions.
setPromptDefinitions(List<AiPromptDefinition> promptDefinitions) -> void: Sets the prompt template definitions.
getAiDefinitions() -> List<AiModelDefinition>: Retrieves AI model definitions.
setAiDefinitions(List<AiModelDefinition> aiDefinitions) -> void: Sets the AI model definitions.
getModelDirectory() -> File: Retrieves the directory for relative model path resolution.
setModelDirectory(File modelDirectory) -> void: Sets the directory for relative model path resolution.
getFieldGenerations() -> List<AiFieldGenerationConfig>: Retrieves per-field AI generation rules.
setFieldGenerations(List<AiFieldGenerationConfig> fieldGenerations) -> void: Sets the per-field AI generation rules.
getFactDefinitions() -> List<AiFactDefinition>: Retrieves reusable fact definitions.
setFactDefinitions(List<AiFactDefinition> factDefinitions) -> void: Sets the reusable fact definitions.
getLlamaModelPath() -> String: Retrieves the GGUF model file path fallback.
setLlamaModelPath(String llamaModelPath) -> void: Sets the GGUF model file path fallback.
getLlamaContextSize() -> int: Retrieves the llama.cpp context window size fallback.
setLlamaContextSize(int llamaContextSize) -> void: Sets the llama.cpp context window size fallback.
getLlamaMaxOutputTokens() -> int: Retrieves the max output tokens fallback.
setLlamaMaxOutputTokens(int llamaMaxOutputTokens) -> void: Sets the max output tokens fallback.
getLlamaTemperature() -> float: Retrieves the llama.cpp sampling temperature fallback.
setLlamaTemperature(float llamaTemperature) -> void: Sets the llama.cpp sampling temperature fallback.
getLlamaThreads() -> int: Retrieves the CPU threads count fallback.
setLlamaThreads(int llamaThreads) -> void: Sets the CPU threads count fallback.
getPluginVersion() -> String: Retrieves the generator plugin version.
setPluginVersion(String pluginVersion) -> void: Sets the generator plugin version.
getAiVersion() -> String: Retrieves the AI summarisation logic version.
setAiVersion(String aiVersion) -> void: Sets the AI summarisation logic version.
getProjectName() -> String: Retrieves the project title name.
setProjectName(String projectName) -> void: Sets the project title name.

#### Dependencies
java.io.File, java.util.List, lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptDefinition, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiGenerationConfig (referenced via static default).

#### Exceptions / Errors
* Null handling is managed via `@Nullable` annotations for optional configuration fields.
* No explicit runtime exceptions are thrown by getters/setters shown.

#### Concurrency
* Class is mutable (JavaBean pattern), implying external synchronization is required for thread-safe configuration updates.
* Fields are not explicitly synchronized.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:56:17Z
- G: 
- A: 0.0.0
- X: file
---
> This class serves as the centralized, mutable configuration object holding all parameters required for an AI-driven source code analysis and generation run.

#### Purpose
* Holds all parameters for a srcmorph run, bindable from Maven, JSON/YAML, or direct Java construction.
* Acts as the single shared configuration object across different binding technologies.

#### Type
class SrcMorphConfiguration; @ToString

#### Input
* Constructor: No parameters (applies defaults).
* Fields: `baseDirectory` (File), `outputDirectory` (File), `force` (boolean), `subtrees` (List<String>), `excludes` (List<String>), `fileExtensions` (List<String>), `minFileSizeBytes` (long), `maxFileSizeBytes` (long), `planOnly` (boolean), `generationProvider` (String), `promptDefinitions` (List<AiPromptDefinition>), `aiDefinitions` (List<AiModelDefinition>), `modelDirectory` (File), `fieldGenerations` (List<AiFieldGenerationConfig>), `factDefinitions` (List<AiFactDefinition>), `llamaModelPath` (String), `llamaContextSize` (int), `llamaMaxOutputTokens` (int), `llamaTemperature` (float), `llamaThreads` (int), `pluginVersion` (String), `aiVersion` (String), `projectName` (String).

#### Output
* Return Types: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.
* Side Effects: Mutates internal state based on setter calls.

#### Core logic
* Initializes with default values for AI parameters (e.g., `DEFAULT_LLAMA_CONTEXT_SIZE`).
* Provides getters and setters for all configuration parameters, enabling external binding.
* Stores configuration for file filtering (subtrees, excludes, extensions, size bounds).
* Stores configuration for AI execution (provider, prompt definitions, model definitions, field generation rules, fact definitions).

#### Public API
getBaseDirectory() -> File: Retrieves the project base directory.
setBaseDirectory(File) -> void: Sets the project base directory.
getOutputDirectory() -> File: Retrieves the output directory for .ai.md files.
setOutputDirectory(File) -> void: Sets the output directory for .ai.md files.
isForce() -> boolean: Checks if AI fields should always regenerate.
setForce(boolean) -> void: Sets whether AI fields are always regenerated.
getSubtrees() -> List<String>: Retrieves source subdirectory paths restricting processing.
setSubtrees(List<String>) -> void: Sets source subdirectory paths restricting processing.
getExcludes() -> List<String>: Retrieves glob patterns for source files to skip.
setExcludes(List<String>) -> void: Sets glob patterns for source files to skip.
getFileExtensions() -> List<String>: Retrieves file extensions to index.
setFileExtensions(List<String>) -> void: Sets file extensions to index.
getMinFileSizeBytes() -> long: Retrieves the exclusive lower file-size bound.
setMinFileSizeBytes(long) -> void: Sets the exclusive lower file-size bound.
getMaxFileSizeBytes() -> long: Retrieves the inclusive upper file-size bound.
setMaxFileSizeBytes(long) -> void: Sets the inclusive upper file-size bound.
isPlanOnly() -> boolean: Checks if the run stops after printing the routing plan.
setPlanOnly(boolean) -> void: Sets whether the run stops after planning.
getGenerationProvider() -> String: Retrieves the AI generation provider name.
setGenerationProvider(String) -> void: Sets the AI generation provider name.
getPromptDefinitions() -> List<AiPromptDefinition>: Retrieves configured prompt template definitions.
setPromptDefinitions(List<AiPromptDefinition>) -> void: Sets the prompt template definitions.
getAiDefinitions() -> List<AiModelDefinition>: Retrieves AI model definitions.
setAiDefinitions(List<AiModelDefinition>) -> void: Sets the AI model definitions.
getModelDirectory() -> File: Retrieves the directory for resolving relative model paths.
setModelDirectory(File) -> void: Sets the directory for resolving relative model paths.
getFieldGenerations() -> List<AiFieldGenerationConfig>: Retrieves per-field AI generation rules.
setFieldGenerations(List<AiFieldGenerationConfig>) -> void: Sets the per-field AI generation rules.
getFactDefinitions() -> List<AiFactDefinition>: Retrieves reusable fact definitions.
setFactDefinitions(List<AiFactDefinition>) -> void: Sets the reusable fact definitions.
getLlamaModelPath() -> String: Retrieves the GGUF model file path fallback.
setLlamaModelPath(String) -> void: Sets the GGUF model file path fallback.
getLlamaContextSize() -> int: Retrieves the llama.cpp context window size fallback.
setLlamaContextSize(int) -> void: Sets the llama.cpp context window size fallback.
getLlamaMaxOutputTokens() -> int: Retrieves the max output tokens fallback.
setLlamaMaxOutputTokens(int) -> void: Sets the max output tokens fallback.
getLlamaTemperature() -> float: Retrieves the llama.cpp sampling temperature fallback.
setLlamaTemperature(float) -> void: Sets the llama.cpp sampling temperature fallback.
getLlamaThreads() -> int: Retrieves the llama.cpp inference thread count fallback.
setLlamaThreads(int) -> void: Sets the llama.cpp inference thread count fallback.
getPluginVersion() -> String: Retrieves the generator plugin version.
setPluginVersion(String) -> void: Sets the generator plugin version.
getAiVersion() -> String: Retrieves the AI summarisation logic version.
setAiVersion(String) -> void: Sets the AI summarisation logic version.
getProjectName() -> String: Retrieves the project title for indexing.
setProjectName(String) -> void: Sets the project title for indexing.

#### Dependencies
java.io.File, java.util.List, lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptDefinition, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiGenerationConfig, net.ladenthin.srcmorph.config.AiModelDefinition, net.ladenthin.srcmorph.config.AiFieldGenerationConfig, net.ladenthin.srcmorph.config.AiFactDefinition

#### Exceptions / Errors
* Handles nullability via @Nullable annotations; no explicit runtime exceptions shown for configuration loading.

#### Concurrency
* Mutable JavaBean structure implies potential for concurrent modification if not externally synchronized.

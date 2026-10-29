### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:31Z
- G: 
- A: 0.0.0
- X: file
---
> This class serves as the centralized, mutable configuration object holding all parameters required for an AI-driven source code modification process.

#### Purpose
* Holds all parameters for a srcmorph run, bindable from Maven, JSON/YAML, or direct Java construction.
* Acts as the single shared configuration object across different binding technologies.

#### Type
class SrcMorphConfiguration

#### Input
* Constructor: No arguments (applies defaults).
* Fields: `baseDirectory` (File), `outputDirectory` (File), `force` (boolean), `subtrees` (List<String>), `excludes` (List<String>), `fileExtensions` (List<String>), `minFileSizeBytes` (long), `maxFileSizeBytes` (long), `planOnly` (boolean), `generationProvider` (String), `promptDefinitions` (List<AiPromptDefinition>), `aiDefinitions` (List<AiModelDefinition>), `modelDirectory` (File), `fieldGenerations` (List<AiFieldGenerationConfig>), `factDefinitions` (List<AiFactDefinition>), `llamaModelPath` (String), `llamaContextSize` (int), `llamaMaxOutputTokens` (int), `llamaTemperature` (float), `llamaThreads` (int), `pluginVersion` (String), `aiVersion` (String), `projectName` (String).

#### Output
* Returns: `File` (getBaseDirectory, getOutputDirectory), `boolean` (isForce, isPlanOnly), `String` (getGenerationProvider, getPluginVersion, getAiVersion, getProjectName), `List<String>` (getSubtrees, getExcludes, getFileExtensions), `long` (getMinFileSizeBytes, getMaxFileSizeBytes), `List<AiPromptDefinition>` (getPromptDefinitions), `List<AiModelDefinition>` (getAiDefinitions), `File` (getModelDirectory), `List<AiFieldGenerationConfig>` (getFieldGenerations), `List<AiFactDefinition>` (getFactDefinitions), `String` (getLlamaModelPath), `int` (getLlamaContextSize, getLlamaMaxOutputTokens, getLlamaThreads).

#### Core logic
* Initialization applies numerous static defaults for AI parameters (e.g., `DEFAULT_LLAMA_CONTEXT_SIZE`).
* Provides getters and setters for all configuration parameters, ensuring mutability for various binding mechanisms.
* Logic separates lifecycle concerns (Maven skip flags) from execution configuration.

#### Public API
getBaseDirectory() -> File: Returns the project base directory.
setBaseDirectory(File) -> void: Sets the project base directory.
getOutputDirectory() -> File: Returns the output directory for .ai.md files.
setOutputDirectory(File) -> void: Sets the output directory for .ai.md files.
isForce() -> boolean: Checks if AI fields must always regenerate.
setForce(boolean) -> void: Sets whether AI fields are always regenerated.
getSubtrees() -> List<String>: Returns source subdirectories restricting processing.
setSubtrees(List<String>) -> void: Sets source subdirectories restricting processing.
getExcludes() -> List<String>: Returns glob patterns for source files to skip.
setExcludes(List<String>) -> void: Sets glob patterns for source files to skip.
getFileExtensions() -> List<String>: Returns file extensions to index.
setFileExtensions(List<String>) -> void: Sets file extensions to index.
getMinFileSizeBytes() -> long: Returns the exclusive lower file-size bound.
setMinFileSizeBytes(long) -> void: Sets the exclusive lower file-size bound.
getMaxFileSizeBytes() -> long: Returns the inclusive upper file-size bound.
setMaxFileSizeBytes(long) -> void: Sets the inclusive upper file-size bound.
isPlanOnly() -> boolean: Checks if generation stops after routing plan.
setPlanOnly(boolean) -> void: Sets if generation stops after routing plan.
getGenerationProvider() -> String: Returns the AI generation provider name.
setGenerationProvider(String) -> void: Sets the AI generation provider name.
getPromptDefinitions() -> List<AiPromptDefinition>: Returns configured prompt template definitions.
setPromptDefinitions(List<AiPromptDefinition>) -> void: Sets the prompt template definitions.
getAiDefinitions() -> List<AiModelDefinition>: Returns configured AI model definitions.
setAiDefinitions(List<AiModelDefinition>) -> void: Sets the AI model definitions.
getModelDirectory() -> File: Returns directory for resolving relative model paths.
setModelDirectory(File) -> void: Sets the directory for resolving relative model paths.
getFieldGenerations() -> List<AiFieldGenerationConfig>: Returns per-field AI generation rules.
setFieldGenerations(List<AiFieldGenerationConfig>) -> void: Sets the per-field AI generation rules.
getFactDefinitions() -> List<AiFactDefinition>: Returns reusable named fact definitions.
setFactDefinitions(List<AiFactDefinition>) -> void: Sets the reusable named fact definitions.
getLlamaModelPath() -> String: Returns path to GGUF model file fallback.
setLlamaModelPath(String) -> void: Sets the path to the GGUF model file fallback.
getLlamaContextSize() -> int: Returns llama.cpp context window size fallback.
setLlamaContextSize(int) -> void: Sets the llama.cpp context window size fallback.
getLlamaMaxOutputTokens() -> int: Returns max output tokens fallback.
setLlamaMaxOutputTokens(int) -> void: Sets the max output tokens fallback.
getLlamaTemperature() -> float: Returns llama.cpp sampling temperature fallback.
setLlamaTemperature(float) -> void: Sets the llama.cpp sampling temperature fallback.
getLlamaThreads() -> int: Returns llama.cpp inference thread count fallback.
setLlamaThreads(int) -> void: Sets the llama.cpp inference thread count fallback.
getPluginVersion() -> String: Returns the generator plugin version.
setPluginVersion(String) -> void: Sets the generator plugin version.
getAiVersion() -> String: Returns the AI summarisation logic version.
setAiVersion(String) -> void: Sets the AI summarisation logic version.
getProjectName() -> String: Returns the project title for indexing.
setProjectName(String) -> void: Sets the project title for indexing.

#### Dependencies
java.io.File, java.util.List, lombok.ToString, net.ladenthin.srcmorph.prompt.AiPromptDefinition, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiGenerationConfig, net.ladenthin.srcmorph.config.AiModelDefinition, net.ladenthin.srcmorph.config.AiFieldGenerationConfig, net.ladenthin.srcmorph.config.AiFactDefinition

#### Exceptions / Errors
* Null handling is explicitly managed via @Nullable annotations for optional configuration lists/fields.
* No explicit runtime exceptions are thrown by getters/setters shown.

#### Concurrency
* The class is designed as a mutable JavaBean, implying external synchronization is required if shared across threads.
* Lombok's @ToString suggests state tracking, but no explicit concurrency controls (locks, volatile) are present.

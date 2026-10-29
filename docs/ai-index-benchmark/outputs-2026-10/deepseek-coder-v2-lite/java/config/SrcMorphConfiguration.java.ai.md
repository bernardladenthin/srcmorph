### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:28:42Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- **Configuration management**: Manages all parameters for running a `srcmorph` tool, including AI generation settings, file processing rules, and output specifications.
- **Flexibility**: Allows configuration via various sources (Maven parameters, JSON/YAML config files, or direct Java construction), ensuring adaptability to different deployment scenarios.

#### Type
- **Class**: `SrcMorphConfiguration`
- **Modifiers**: `public`
- **Extends**: None
- **Implements**: None
- **Key Generics**: None

#### Input
- **Constructor Parameters**: None
- **Method Parameters**: 
  - `setBaseDirectory(File baseDirectory)`
  - `setOutputDirectory(File outputDirectory)`
  - `setForce(boolean force)`
  - `setSubtrees(List<String> subtrees)`
  - `setExcludes(List<String> excludes)`
  - `setFileExtensions(List<String> fileExtensions)`
  - `setMinFileSizeBytes(long minFileSizeBytes)`
  - `setMaxFileSizeBytes(long maxFileSizeBytes)`
  - `setPlanOnly(boolean planOnly)`
  - `setGenerationProvider(String generationProvider)`
  - `setPromptDefinitions(List<AiPromptDefinition> promptDefinitions)`
  - `setAiDefinitions(List<AiModelDefinition> aiDefinitions)`
  - `setModelDirectory(File modelDirectory)`
  - `setFieldGenerations(List<AiFieldGenerationConfig> fieldGenerations)`
  - `setFactDefinitions(List<AiFactDefinition> factDefinitions)`
  - `setLlamaModelPath(String llamaModelPath)`
  - `setLlamaContextSize(int llamaContextSize)`
  - `setLlamaMaxOutputTokens(int llamaMaxOutputTokens)`
  - `setLlamaTemperature(float llamaTemperature)`
  - `setLlamaThreads(int llamaThreads)`
  - `setPluginVersion(String pluginVersion)`
  - `setAiVersion(String aiVersion)`
  - `setProjectName(String projectName)`

#### Output
- **Return Types**: 
  - `File getBaseDirectory()`
  - `File getOutputDirectory()`
  - `boolean isForce()`
  - `List<String> getSubtrees()`
  - `List<String> getExcludes()`
  - `List<String> getFileExtensions()`
  - `long getMinFileSizeBytes()`
  - `long getMaxFileSizeBytes()`
  - `boolean isPlanOnly()`
  - `String getGenerationProvider()`
  - `List<AiPromptDefinition> getPromptDefinitions()`
  - `List<AiModelDefinition> getAiDefinitions()`
  - `File getModelDirectory()`
  - `List<AiFieldGenerationConfig> getFieldGenerations()`
  - `List<AiFactDefinition> getFactDefinitions()`
  - `String getLlamaModelPath()`
  - `int getLlamaContextSize()`
  - `int getLlamaMaxOutputTokens()`
  - `float getLlamaTemperature()`
  - `int getLlamaThreads()`
  - `String getPluginVersion()`
  - `String getAiVersion()`
  - `String getProjectName()`

#### Core logic
- **Initialization**: 
  - Constructs a `SrcMorphConfiguration` with default values for all parameters.
- **Field Setting**: 
  - Provides setter methods for each configuration parameter to allow dynamic setting of configuration values.

#### Public API
- `getBaseDirectory() -> File`: Returns the project base directory.
- `getOutputDirectory() -> File`: Returns the output directory for AI files.
- `isForce() -> boolean`: Checks if AI fields are forced to regenerate.
- `getSubtrees() -> List<String>`: Returns the list of subtrees for processing.
- `getExcludes() -> List<String>`: Returns the list of excluded files.
- `getFileExtensions() -> List<String>`: Returns the list of file extensions to process.
- `getMinFileSizeBytes() -> long`: Returns the minimum file size for processing.
- `getMaxFileSizeBytes() -> long`: Returns the maximum file size for processing.
- `isPlanOnly() -> boolean`: Checks if the tool should stop after planning.
- `getGenerationProvider() -> String`: Returns the AI generation provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>`: Returns the list of prompt definitions.
- `getAiDefinitions() -> List<AiModelDefinition>`: Returns the list of AI model definitions.
- `getModelDirectory() -> File`: Returns the model directory for resolving model paths.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>`: Returns the list of field generation configurations.
- `getFactDefinitions() -> List<AiFactDefinition>`: Returns the list of fact definitions.
- `getLlamaModelPath() -> String`: Returns the path to the llama.cpp model file.
- `getLlamaContextSize() -> int`: Returns the llama.cpp context window size.
- `getLlamaMaxOutputTokens() -> int`: Returns the maximum number of output tokens per call.
- `getLlamaTemperature() -> float`: Returns the llama.cpp sampling temperature.
- `getLlamaThreads() -> int`: Returns the number of CPU threads for llama.cpp inference.
- `getPluginVersion() -> String`: Returns the generator (plugin) version.
- `getAiVersion() -> String`: Returns the AI summarisation logic version.
- `getProjectName() -> String`: Returns the project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- **Null Handling**: 
  - Fields marked with `@Nullable` can be `null`.
- **Error Conditions**: 
  - None explicitly mentioned in the provided source.

#### Concurrency
- **Threading**: 
  - The class does not explicitly handle threading or synchronization.
- **Thread-Safety**: 
  - The class is mutable and does not ensure thread-safety when accessed by multiple threads concurrently.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T00:07:36Z
- T: 2026-10-29T00:08:12Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the `SrcMorphConfiguration.java` file:

```markdown
## Purpose
Calculates VAT for invoices

## Type
Class, final

## Input
- File baseDirectory
- File outputDirectory
- boolean force
- List<String> subtrees
- List<String> excludes
- List<String> fileExtensions
- long minFileSizeBytes
- long maxFileSizeBytes
- boolean planOnly
- String generationProvider
- List<AiPromptDefinition> promptDefinitions
- List<AiModelDefinition> aiDefinitions
- File modelDirectory
- List<AiFieldGenerationConfig> fieldGenerations
- List<AiFactDefinition> factDefinitions
- String llamaModelPath
- int llamaContextSize
- int llamaMaxOutputTokens
- float llamaTemperature
- int llamaThreads
- String pluginVersion
- String aiVersion
- String projectName

## Output
- File baseDirectory
- File outputDirectory
- boolean force
- List<String> subtrees
- List<String> excludes
- List<String> fileExtensions
- long minFileSizeBytes
- long maxFileSizeBytes
- boolean planOnly
- String generationProvider
- List<AiPromptDefinition> promptDefinitions
- List<AiModelDefinition> aiDefinitions
- File modelDirectory
- List<AiFieldGenerationConfig> fieldGenerations
- List<AiFactDefinition> factDefinitions
- String llamaModelPath
- int llamaContextSize
- int llamaMaxOutputTokens
- float llamaTemperature
- int llamaThreads
- String pluginVersion
- String aiVersion
- String projectName

## Core Logic
- Default values for various configuration parameters
- Setter methods for each configuration parameter

## Public API
- getBaseDirectory() -> File
- setBaseDirectory(File) -> void
- getOutputDirectory() -> File
- setOutputDirectory(File) -> void
- isForce() -> boolean
- setForce(boolean) -> void
- getSubtrees() -> List<String>
- setSubtrees(List<String>) -> void
- getExcludes() -> List<String>
- setExcludes(List<String>) -> void
- getFileExtensions() -> List<String>
- setFileExtensions(List<String>) -> void
- getMinFileSizeBytes() -> long
- setMinFileSizeBytes(long) -> void
- getMaxFileSizeBytes() -> long
- setMaxFileSizeBytes(long) -> void
- isPlanOnly() -> boolean
- setPlanOnly(boolean) -> void
- getGenerationProvider() -> String
- setGenerationProvider(String) -> void
- getPromptDefinitions() -> List<AiPromptDefinition>
- setPromptDefinitions(List<AiPromptDefinition>) -> void
- getAiDefinitions() -> List<AiModelDefinition>
- setAiDefinitions(List<AiModelDefinition>) -> void
- getModelDirectory() -> File
- setModelDirectory(File) -> void
- getFieldGenerations() -> List<AiFieldGenerationConfig>
- setFieldGenerations(List<AiFieldGenerationConfig>) -> void
- getFactDefinitions() -> List<AiFactDefinition>
- setFactDefinitions(List<AiFactDefinition>) -> void
- getLlamaModelPath() -> String
- setLlamaModelPath(String) -> void
- getLlamaContextSize() -> int
- setLlamaContextSize(int) -> void
- getLlamaMaxOutputTokens() -> int
- setLlamaMaxOutputTokens(int) -> void
- getLlamaTemperature() -> float
- setLlamaTemperature(float) -> void
- getLlamaThreads() -> int
- setLlamaThreads(int) -> void
- getPluginVersion() -> String
- setPluginVersion(String) -> void
- getAiVersion() -> String
- setAiVersion(String) -> void
- getProjectName() -> String
- setProjectName(String) -> void

## Dependencies
- java.io.File
- java.util.List
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- org.jspecify.annotations.Nullable

## Exceptions / Errors
- None

## Concurrency
- None
```

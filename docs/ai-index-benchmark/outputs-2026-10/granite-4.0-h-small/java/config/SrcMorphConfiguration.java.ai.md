### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:56:50Z
- G: 
- A: 0.0.0
- X: file
---
> Holds configuration parameters for a srcmorph run, mutable JavaBean bindable from Maven, JSON/YAML, or plain Java

#### Purpose
- Root mutable JavaBean holding every parameter a `srcmorph` run needs
- Bindable from Maven `@Parameter` fields, JSON/YAML config, or plain Java code
- Mirrors Maven `@Parameter` field names for future config file compatibility

#### Type
- Class `net.ladenthin.srcmorph.config.SrcMorphConfiguration`
- Public mutable JavaBean with setters
- Extends `java.lang.Object`

#### Input
- Constructor takes no arguments
- Fields populated via setters
- Dependencies on `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`

#### Output
- Holds configuration state for a `srcmorph` run
- Provides getters for all configuration parameters
- No direct output, used internally by `srcmorph` engine

#### Core logic
- Default values for output directory, generation provider, llama context size, etc.
- Fields mirror Maven `@Parameter` names for future config file compatibility
- Mutable JavaBean with setters for easy population from different binding technologies
- No direct logic, acts as a data container

#### Public API
- `getBaseDirectory() -> File`: returns project base directory
- `setBaseDirectory(File)`: sets project base directory
- `getOutputDirectory() -> File`: returns output directory for `.ai.md` files
- `setOutputDirectory(File)`: sets output directory for `.ai.md` files
- `isForce() -> boolean`: returns whether to always regenerate AI fields
- `setForce(boolean)`: sets whether to always regenerate AI fields
- `getSubtrees() -> List<String>`: returns source subdirectory paths to restrict processing
- `setSubtrees(List<String>)`: sets source subdirectory paths to restrict processing
- `getExcludes() -> List<String>`: returns glob patterns for source files to skip
- `setExcludes(List<String>)`: sets glob patterns for source files to skip
- `getFileExtensions() -> List<String>`: returns file extensions to index
- `setFileExtensions(List<String>)`: sets file extensions to index
- `getMinFileSizeBytes() -> long`: returns exclusive lower file-size bound in bytes
- `setMinFileSizeBytes(long)`: sets exclusive lower file-size bound in bytes
- `getMaxFileSizeBytes() -> long`: returns inclusive upper file-size bound in bytes
- `setMaxFileSizeBytes(long)`: sets inclusive upper file-size bound in bytes
- `isPlanOnly() -> boolean`: returns whether to stop after printing routing plan
- `setPlanOnly(boolean)`: sets whether to stop after printing routing plan
- `getGenerationProvider() -> String`: returns name of AI generation provider to use
- `setGenerationProvider(String)`: sets name of AI generation provider to use
- `getPromptDefinitions() -> List<AiPromptDefinition>`: returns prompt template definitions
- `setPromptDefinitions(List<AiPromptDefinition>)`: sets prompt template definitions
- `getAiDefinitions() -> List<AiModelDefinition>`: returns AI model definitions
- `setAiDefinitions(List<AiModelDefinition>)`: sets AI model definitions
- `getModelDirectory() -> File`: returns directory for relative model paths
- `setModelDirectory(File)`: sets directory for relative model paths
- `getFieldGenerations() -> List<AiFieldGenerationConfig>`: returns per-field AI generation configurations
- `setFieldGenerations(List<AiFieldGenerationConfig>)`: sets per-field AI generation configurations
- `getFactDefinitions() -> List<AiFactDefinition>`: returns reusable fact definition groups
- `setFactDefinitions(List<AiFactDefinition>)`: sets reusable fact definition groups
- `getLlamaModelPath() -> String`: returns path to GGUF model file for llama.cpp fallback
- `setLlamaModelPath(String)`: sets path to GGUF model file for llama.cpp fallback
- `getLlamaContextSize() -> int`: returns llama.cpp context window size for fallback
- `setLlamaContextSize(int)`: sets llama.cpp context window size for fallback
- `getLlamaMaxOutputTokens() -> int`: returns maximum llama.cpp output tokens per call for fallback
- `setLlamaMaxOutputTokens(int)`: sets maximum llama.cpp output tokens per call for fallback
- `getLlamaTemperature() -> float`: returns llama.cpp sampling temperature for fallback
- `setLlamaTemperature(float)`: sets llama.cpp sampling temperature for fallback
- `getLlamaThreads() -> int`: returns number of CPU threads for llama.cpp inference fallback
- `setLlamaThreads(int)`: sets number of CPU threads for llama.cpp inference fallback
- `getPluginVersion() -> String`: returns generator version recorded in `.ai.md` headers
- `setPluginVersion(String)`: sets generator version recorded in `.ai.md` headers
- `getAiVersion() -> String`: returns AI summarisation logic version recorded in `.ai.md` headers
- `setAiVersion(String)`: sets AI summarisation logic version recorded in `.ai.md` headers
- `getProjectName() -> String`: returns project title recorded as aggregate-project index title
- `setProjectName(String)`: sets project title recorded as aggregate-project index title

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable exceptions or errors

#### Concurrency
- No concurrency considerations, mutable JavaBean

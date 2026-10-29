### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:34:13Z
- G: 
- A: 0.0.0
- X: file
---
> This class serves as the central mutable configuration container for the srcmorph tool, aggregating all parameters for AI generation, file filtering, and model settings.

#### Purpose
* Acts as a unified JavaBean for configuration data, compatible with Maven plugin parameters, JSON/YAML files, or direct Java instantiation.
* Provides a single source of truth for all engine parameters, excluding lifecycle-specific Maven flags.

#### Type
class; public.

#### Input
* Constructor: No parameters.
* Setters: Accepts `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, and `float`.

#### Output
* Getters: Returns project paths, filtering rules, AI model definitions, and llama.cpp inference parameters.
* State: Maintains a mutable state of all configuration options.

#### Core logic
* Provides default values for AI generation (mock provider, context size, temperature, threads).
* Manages file system paths for base directories, output directories, and model locations.
* Stores filtering criteria including subtrees, excludes, file extensions, and file size bounds.
* Holds complex AI definitions including prompt templates, model parameters, and field-specific generation rules (routing).
* Tracks versioning information for the plugin and the AI summarization logic.

#### Public API
* `getBaseDirectory() / setBaseDirectory(File)` -> File (Project root)
* `getOutputDirectory() / setOutputDirectory(File)` -> File (Output path)
* `isForce() / setForce(boolean)` -> boolean (Force regeneration)
* `getSubtrees() / setSubtrees(List<String>)` -> List<String> (Source subdirectories)
* `getExcludes() / setExcludes(List<String>)` -> List<String> (Glob patterns)
* `getFileExtensions() / setFileExtensions(List<String>)` -> List<String> (Indexed extensions)
* `getMinFileSizeBytes() / setMinFileSizeBytes(long)` -> long (Lower size bound)
* `getMaxFileSizeBytes() / setMaxFileSizeBytes(long)` -> long (Upper size bound)
* `isPlanOnly() / setPlanOnly(boolean)` -> boolean (Stop after planning)
* `getGenerationProvider() / setGenerationProvider(String)` -> String (AI provider name)
* `getPromptDefinitions() / setPromptDefinitions(List<AiPromptDefinition>)` -> List<AiPromptDefinition> (Prompt templates)
* `getAiDefinitions() / setAiDefinitions(List<AiModelDefinition>)` -> List<AiModelDefinition> (Model parameters)
* `getModelDirectory() / setModelDirectory(File)` -> File (Model path resolution)
* `getFieldGenerations() / setFieldGenerations(List<AiFieldGenerationConfig>)` -> List<AiFieldGenerationConfig> (Routing rules)
* `getFactDefinitions() / setFactDefinitions(List<AiFactDefinition>)` -> List<AiFactDefinition> (Fact groups)
* `getLlamaModelPath() / setLlamaModelPath(String)` -> String (GGUF path)
* `getLlamaContextSize() / setLlamaContextSize(int)` -> int (Context window)
* `getLlamaMaxOutputTokens() / setLlamaMaxOutputTokens(int)` -> int (Max tokens)
* `getLlamaTemperature() / setLlamaTemperature(float)` -> float (Sampling temp)
* `getLlamaThreads() / setLlamaThreads(int)` -> int (CPU threads)
* `getPluginVersion() / setPluginVersion(String)` -> String (Plugin version)
* `getAiVersion() / setAiVersion(String)` -> String (AI logic version)
* `getProjectName() / setProjectName(String)` -> String (Project title)

#### Dependencies
* `java.io.File`
* `java.util.List`
* `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
* `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced via constant)
* `net.ladenthin.srcmorph.config.AiModelDefinition`
* `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
* `net.ladenthin.srcmorph.config.AiFactDefinition`
* `org.jspecify.annotations.Nullable`
* `lombok.ToString`

#### Concurrency
* Not thread-safe; designed as a mutable JavaBean for configuration binding.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:03Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A mutable configuration bean that aggregates all parameters required for a srcmorph run, mirroring Maven plugin fields for cross‑surface binding.

#### Purpose
- Holds runtime settings for AI‑powered source generation.
- Serves as a single source of truth for Maven, JSON/YAML, or programmatic configuration.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no special modifiers.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or reflection (Maven, Jackson, etc.).
- Dependencies: `File`, `List`, `AiPromptDefinition`, `Nullable` annotation.

#### Output
- Getter methods expose current configuration.
- Mutated fields updated by setters.
- No external resources written directly.

#### Core logic
- Provides default constants for output directory, generation provider, llama.cpp settings, and AI version.
- Stores optional lists (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`).
- Tracks size bounds (`minFileSizeBytes`, `maxFileSizeBytes`).
- Flags for force regeneration and plan‑only mode.
- Holds paths for model directory and llama.cpp model file.
- Records plugin and AI logic versions, and optional project title.

#### Public API
- `File getBaseDirectory() -> base directory`  
- `void setBaseDirectory(File) -> set base directory`  
- `File getOutputDirectory() -> output dir`  
- `void setOutputDirectory(File) -> set output dir`  
- `boolean isForce() -> force flag`  
- `void setForce(boolean) -> set force flag`  
- `List<String> getSubtrees() -> subtrees`  
- `void setSubtrees(List<String>) -> set subtrees`  
- `List<String> getExcludes() -> excludes`  
- `void setExcludes(List<String>) -> set excludes`  
- `List<String> getFileExtensions() -> extensions`  
- `void setFileExtensions(List<String>) -> set extensions`  
- `long getMinFileSizeBytes() -> min size`  
- `void setMinFileSizeBytes(long) -> set min size`  
- `long getMaxFileSizeBytes() -> max size`  
- `void setMaxFileSizeBytes(long) -> set max size`  
- `boolean isPlanOnly() -> plan‑only flag`  
- `void setPlanOnly(boolean) -> set plan‑only flag`  
- `String getGenerationProvider() -> provider name`  
- `void setGenerationProvider(String) -> set provider`  
- `List<AiPromptDefinition> getPromptDefinitions() -> prompts`  
- `void setPromptDefinitions(List<AiPromptDefinition>) -> set prompts`  
- `List<AiModelDefinition> getAiDefinitions() -> model defs`  
- `void setAiDefinitions(List<AiModelDefinition>) -> set model defs`  
- `File getModelDirectory() -> model dir`  
- `void setModelDirectory(File) -> set model dir`  
- `List<AiFieldGenerationConfig> getFieldGenerations() -> field rules`  
- `void setFieldGenerations(List<AiFieldGenerationConfig>) -> set field rules`  
- `List<AiFactDefinition> getFactDefinitions() -> fact groups`  
- `void setFactDefinitions(List<AiFactDefinition>) -> set fact groups`  
- `String getLlamaModelPath() -> llama model path`  
- `void setLlamaModelPath(String) -> set llama model path`  
- `int getLlamaContextSize() -> context size`  
- `void setLlamaContextSize(int) -> set context size`  
- `int getLlamaMaxOutputTokens() -> max tokens`  
- `void setLlamaMaxOutputTokens(int) -> set max tokens`  
- `float getLlamaTemperature() -> temperature`  
- `void setLlamaTemperature(float) -> set temperature`  
- `int getLlamaThreads() -> thread count`  
- `void setLlamaThreads(int) -> set thread count`  
- `String getPluginVersion() -> plugin version`  
- `void setPluginVersion(String) -> set plugin version`  
- `String getAiVersion() -> AI logic version`  
- `void setAiVersion(String) -> set AI logic version`  
- `String getProjectName() -> project title`  
- `void setProjectName(String) -> set project title`

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- Referenced types: `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null values allowed for optional fields; `@Nullable` indicates permissible nulls.

#### Concurrency
- No synchronization; intended for single‑threaded configuration assembly.

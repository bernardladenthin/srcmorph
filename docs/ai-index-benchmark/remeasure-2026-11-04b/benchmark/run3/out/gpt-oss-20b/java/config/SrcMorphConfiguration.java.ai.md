### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:31Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable configuration bean that aggregates all parameters required for a srcmorph run, mirroring Maven plugin fields for future JSON/YAML binding.

#### Purpose
- Holds runtime settings for AI‑powered source generation.
- Serves as a single source of truth for Maven, CLI, or programmatic use.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, mutable JavaBean  
  - Supertypes: none  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`  
  - Fields: 23 (see below)

#### Input
- Constructor: no parameters (defaults applied).  
- Setter methods for each field: `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`.  
- Dependencies: none injected; all values set via setters.

#### Output
- Getter methods expose current configuration values.  
- No state mutation beyond setters; no external resources written.

#### Core logic
- No algorithmic logic; the class solely stores and exposes configuration data.

#### Public API
- `SrcMorphConfiguration()` → new config instance  
- `File getBaseDirectory()` → base dir  
- `void setBaseDirectory(File)` → set base dir  
- `File getOutputDirectory()` → output dir  
- `void setOutputDirectory(File)` → set output dir  
- `boolean isForce()` → force flag  
- `void setForce(boolean)` → set force flag  
- `List<String> getSubtrees()` → subtrees list  
- `void setSubtrees(List<String>)` → set subtrees  
- `List<String> getExcludes()` → excludes list  
- `void setExcludes(List<String>)` → set excludes  
- `List<String> getFileExtensions()` → file extensions  
- `void setFileExtensions(List<String>)` → set file extensions  
- `long getMinFileSizeBytes()` → min file size  
- `void setMinFileSizeBytes(long)` → set min file size  
- `long getMaxFileSizeBytes()` → max file size  
- `void setMaxFileSizeBytes(long)` → set max file size  
- `boolean isPlanOnly()` → plan‑only flag  
- `void setPlanOnly(boolean)` → set plan‑only flag  
- `String getGenerationProvider()` → AI provider name  
- `void setGenerationProvider(String)` → set provider  
- `List<AiPromptDefinition> getPromptDefinitions()` → prompt defs  
- `void setPromptDefinitions(List<AiPromptDefinition>)` → set prompt defs  
- `List<AiModelDefinition> getAiDefinitions()` → AI model defs  
- `void setAiDefinitions(List<AiModelDefinition>)` → set AI model defs  
- `File getModelDirectory()` → model dir  
- `void setModelDirectory(File)` → set model dir  
- `List<AiFieldGenerationConfig> getFieldGenerations()` → field generation rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` → set field generations  
- `List<AiFactDefinition> getFactDefinitions()` → fact defs  
- `void setFactDefinitions(List<AiFactDefinition>)` → set fact defs  
- `String getLlamaModelPath()` → llama model path  
- `void setLlamaModelPath(String)` → set llama model path  
- `int getLlamaContextSize()` → llama context size  
- `void setLlamaContextSize(int)` → set llama context size  
- `int getLlamaMaxOutputTokens()` → llama max output tokens  
- `void setLlamaMaxOutputTokens(int)` → set llama max output tokens  
- `float getLlamaTemperature()` → llama temperature  
- `void setLlamaTemperature(float)` → set llama temperature  
- `int getLlamaThreads()` → llama thread count  
- `void setLlamaThreads(int)` → set llama threads  
- `String getPluginVersion()` → plugin version  
- `void setPluginVersion(String)` → set plugin version  
- `String getAiVersion()` → AI version  
- `void setAiVersion(String)` → set AI version  
- `String getProjectName()` → project title  
- `void setProjectName(String)` → set project title  

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiModelDefinition`  
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`  
- `net.ladenthin.srcmorph.config.AiFactDefinition`  
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (for `DEFAULT_LLAMA_THREADS`)

#### Exceptions / Errors
- No explicit exceptions thrown; all setters accept nullable values.

#### Concurrency
- No synchronization; instances are intended for single‑threaded configuration building.

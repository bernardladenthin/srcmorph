### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:58Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Holds all runtime parameters for a srcmorph run, from Maven plugin fields to future CLI or JSON/YAML configs.

#### Purpose
- Central configuration holder for srcmorph executions.
- Mutable JavaBean for binding by Maven, Jackson, or code.

#### Type
`public class SrcMorphConfiguration` – class, mutable, Lombok `@ToString`, suppresses null‑init warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for:
  - `File baseDirectory`
  - `File outputDirectory`
  - `boolean force`
  - `List<String> subtrees`
  - `List<String> excludes`
  - `List<String> fileExtensions`
  - `long minFileSizeBytes`
  - `long maxFileSizeBytes`
  - `boolean planOnly`
  - `String generationProvider`
  - `List<AiPromptDefinition> promptDefinitions`
  - `List<AiModelDefinition> aiDefinitions`
  - `File modelDirectory`
  - `List<AiFieldGenerationConfig> fieldGenerations`
  - `List<AiFactDefinition> factDefinitions`
  - `String llamaModelPath`
  - `int llamaContextSize`
  - `int llamaMaxOutputTokens`
  - `float llamaTemperature`
  - `int llamaThreads`
  - `String pluginVersion`
  - `String aiVersion`
  - `String projectName`

#### Output
- Getter methods expose all fields.
- No side effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama settings, and AI version.
- No additional computation; acts purely as a data container.

#### Public API
- `SrcMorphConfiguration()` – create with defaults  
- `File getBaseDirectory()` – base project directory  
- `void setBaseDirectory(File)` – set base directory  
- `File getOutputDirectory()` – output dir for .ai.md files  
- `void setOutputDirectory(File)` – set output dir  
- `boolean isForce()` – always regenerate AI fields?  
- `void setForce(boolean)` – set force flag  
- `List<String> getSubtrees()` – source subdirectory filters  
- `void setSubtrees(List<String>)` – set subtrees  
- `List<String> getExcludes()` – glob patterns to skip  
- `void setExcludes(List<String>)` – set excludes  
- `List<String> getFileExtensions()` – file extensions to index  
- `void setFileExtensions(List<String>)` – set extensions  
- `long getMinFileSizeBytes()` – lower file‑size bound  
- `void setMinFileSizeBytes(long)` – set lower bound  
- `long getMaxFileSizeBytes()` – upper file‑size bound  
- `void setMaxFileSizeBytes(long)` – set upper bound  
- `boolean isPlanOnly()` – stop after routing plan?  
- `void setPlanOnly(boolean)` – set plan‑only flag  
- `String getGenerationProvider()` – AI provider name  
- `void setGenerationProvider(String)` – set provider  
- `List<AiPromptDefinition> getPromptDefinitions()` – prompt templates  
- `void setPromptDefinitions(List<AiPromptDefinition>)` – set prompts  
- `List<AiModelDefinition> getAiDefinitions()` – AI model configs  
- `void setAiDefinitions(List<AiModelDefinition>)` – set models  
- `File getModelDirectory()` – directory for relative model paths  
- `void setModelDirectory(File)` – set model directory  
- `List<AiFieldGenerationConfig> getFieldGenerations()` – routing rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules  
- `List<AiFactDefinition> getFactDefinitions()` – reusable fact groups  
- `void setFactDefinitions(List<AiFactDefinition>)` – set facts  
- `String getLlamaModelPath()` – fallback GGUF model path  
- `void setLlamaModelPath(String)` – set fallback model path  
- `int getLlamaContextSize()` – llama.cpp context window  
- `void setLlamaContextSize(int)` – set context size  
- `int getLlamaMaxOutputTokens()` – llama.cpp max output tokens  
- `void setLlamaMaxOutputTokens(int)` – set max tokens  
- `float getLlamaTemperature()` – llama.cpp sampling temperature  
- `void setLlamaTemperature(float)` – set temperature  
- `int getLlamaThreads()` – llama.cpp CPU threads  
- `void setLlamaThreads(int)` – set thread count  
- `String getPluginVersion()` – generator plugin version  
- `void setPluginVersion(String)` – set plugin version  
- `String getAiVersion()` – AI summarisation logic version  
- `void setAiVersion(String)` – set AI version  
- `String getProjectName()` – project title for index  
- `void setProjectName(String)` – set project title  

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (for DEFAULT_THREADS)

#### Exceptions / Errors
- No checked exceptions thrown; setters accept null where annotated.

#### Concurrency
- No synchronization; instance is not thread‑safe.

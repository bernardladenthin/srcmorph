### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:17:25Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores runtime settings for AI‑powered source generation.
- Serves as a single mutable JavaBean for all binding mechanisms.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no inheritance.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or direct field injection.
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp settings, and AI version.
- No additional logic; purely data holder.

#### Public API
- `File getBaseDirectory()` – base project directory  
- `void setBaseDirectory(File)` – set base directory  
- `File getOutputDirectory()` – output dir for .ai.md files  
- `void setOutputDirectory(File)` – set output dir  
- `boolean isForce()` – always regenerate AI fields  
- `void setForce(boolean)` – set force flag  
- `List<String> getSubtrees()` – source subdirectory paths  
- `void setSubtrees(List<String>)` – set subtrees  
- `List<String> getExcludes()` – glob patterns to skip  
- `void setExcludes(List<String>)` – set excludes  
- `List<String> getFileExtensions()` – file extensions to index  
- `void setFileExtensions(List<String>)` – set extensions  
- `long getMinFileSizeBytes()` – lower file‑size bound  
- `void setMinFileSizeBytes(long)` – set lower bound  
- `long getMaxFileSizeBytes()` – upper file‑size bound  
- `void setMaxFileSizeBytes(long)` – set upper bound  
- `boolean isPlanOnly()` – stop after routing plan  
- `void setPlanOnly(boolean)` – set plan‑only flag  
- `String getGenerationProvider()` – AI generation provider name  
- `void setGenerationProvider(String)` – set provider  
- `List<AiPromptDefinition> getPromptDefinitions()` – prompt templates  
- `void setPromptDefinitions(List<AiPromptDefinition>)` – set prompts  
- `List<AiModelDefinition> getAiDefinitions()` – AI model definitions  
- `void setAiDefinitions(List<AiModelDefinition>)` – set models  
- `File getModelDirectory()` – directory for relative model paths  
- `void setModelDirectory(File)` – set model directory  
- `List<AiFieldGenerationConfig> getFieldGenerations()` – per‑field routing rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` – set field generations  
- `List<AiFactDefinition> getFactDefinitions()` – reusable fact groups  
- `void setFactDefinitions(List<AiFactDefinition>)` – set fact definitions  
- `String getLlamaModelPath()` – fallback GGUF model path  
- `void setLlamaModelPath(String)` – set fallback model path  
- `int getLlamaContextSize()` – llama.cpp context window size  
- `void setLlamaContextSize(int)` – set context size  
- `int getLlamaMaxOutputTokens()` – max output tokens per call  
- `void setLlamaMaxOutputTokens(int)` – set max tokens  
- `float getLlamaTemperature()` – llama.cpp sampling temperature  
- `void setLlamaTemperature(float)` – set temperature  
- `int getLlamaThreads()` – CPU threads for llama.cpp  
- `void setLlamaThreads(int)` – set thread count  
- `String getPluginVersion()` – plugin version in .ai.md header  
- `void setPluginVersion(String)` – set plugin version  
- `String getAiVersion()` – AI summarisation logic version  
- `void setAiVersion(String)` – set AI version  
- `String getProjectName()` – project title for aggregate index  
- `void setProjectName(String)` – set project name  

#### Dependencies
`java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.config.AiModelDefinition`, `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`, `net.ladenthin.srcmorph.config.AiFactDefinition`, `net.ladenthin.srcmorph.config.AiGenerationConfig`.

#### Exceptions / Errors
No checked exceptions thrown; all setters accept nullable values where annotated.

#### Concurrency
No synchronization; intended for single‑threaded configuration building. Immutable after construction if no further setters are called.

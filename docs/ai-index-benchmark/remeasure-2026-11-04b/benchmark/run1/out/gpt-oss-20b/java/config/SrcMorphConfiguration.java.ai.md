### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:52:19Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A mutable configuration bean that aggregates all parameters required for a srcmorph run, bindable from Maven, JSON/YAML, or Java code.

#### Purpose
- Holds runtime parameters for srcmorph AI generation.
- Provides defaults and mutable JavaBean accessors.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public  
  - Extends: none  
  - Implements: none  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`  
  - Fields: 23 (listed below)

#### Input
- Constructor: no parameters.  
- Setter methods accept: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `int`, `float`.  
- Dependencies: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods return the corresponding field values.  
- No state mutation beyond setters.

#### Core logic
- No additional logic; the class is a plain data holder.

#### Public API
- `SrcMorphConfiguration()` → constructor, no args  
- `File getBaseDirectory()` → base directory  
- `void setBaseDirectory(File)` → set base directory  
- `File getOutputDirectory()` → output directory  
- `void setOutputDirectory(File)` → set output directory  
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
- `String getGenerationProvider()` → provider name  
- `void setGenerationProvider(String)` → set provider name  
- `List<AiPromptDefinition> getPromptDefinitions()` → prompt definitions  
- `void setPromptDefinitions(List<AiPromptDefinition>)` → set prompt definitions  
- `List<AiModelDefinition> getAiDefinitions()` → AI model definitions  
- `void setAiDefinitions(List<AiModelDefinition>)` → set AI model definitions  
- `File getModelDirectory()` → model directory  
- `void setModelDirectory(File)` → set model directory  
- `List<AiFieldGenerationConfig> getFieldGenerations()` → field generation rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` → set field generation rules  
- `List<AiFactDefinition> getFactDefinitions()` → fact definitions  
- `void setFactDefinitions(List<AiFactDefinition>)` → set fact definitions  
- `String getLlamaModelPath()` → llama model path  
- `void setLlamaModelPath(String)` → set llama model path  
- `int getLlamaContextSize()` → llama context size  
- `void setLlamaContextSize(int)` → set llama context size  
- `int getLlamaMaxOutputTokens()` → llama max output tokens  
- `void setLlamaMaxOutputTokens(int)` → set llama max output tokens  
- `float getLlamaTemperature()` → llama temperature  
- `void setLlamaTemperature(float)` → set llama temperature  
- `int getLlamaThreads()` → llama threads  
- `void setLlamaThreads(int)` → set llama threads  
- `String getPluginVersion()` → plugin version  
- `void setPluginVersion(String)` → set plugin version  
- `String getAiVersion()` → AI version  
- `void setAiVersion(String)` → set AI version  
- `String getProjectName()` → project name  
- `void setProjectName(String)` → set project name  

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiModelDefinition`  
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`  
- `net.ladenthin.srcmorph.config.AiFactDefinition`  
- `net.ladenthin.srcmorph.config.AiGenerationConfig`

#### Exceptions / Errors
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; mutable state is not thread‑safe.

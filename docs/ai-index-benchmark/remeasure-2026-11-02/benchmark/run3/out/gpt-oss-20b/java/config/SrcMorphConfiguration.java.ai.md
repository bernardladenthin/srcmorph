### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:22Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A mutable configuration bean for the srcmorph AI code‑generation tool, aggregating all runtime parameters.

#### Purpose
- Holds all parameters required for a srcmorph run.
- Serves as a single source of truth for Maven, JSON/YAML, or programmatic configuration.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no inheritance.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or direct field injection.
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp settings, and AI version.
- Constructor initializes no fields; defaults are applied via field initializers.
- Getters return current values; setters assign new values.
- No additional computation or validation logic.

#### Public API
- `SrcMorphConfiguration()` → construct default config  
- `File getBaseDirectory()` → base project directory  
- `void setBaseDirectory(File)` → set base directory  
- `File getOutputDirectory()` → output directory for .ai.md files  
- `void setOutputDirectory(File)` → set output directory  
- `boolean isForce()` → always regenerate AI fields?  
- `void setForce(boolean)` → set force flag  
- `List<String> getSubtrees()` → source subdirectory paths  
- `void setSubtrees(List<String>)` → set subtrees  
- `List<String> getExcludes()` → glob patterns to skip  
- `void setExcludes(List<String>)` → set excludes  
- `List<String> getFileExtensions()` → file extensions to index  
- `void setFileExtensions(List<String>)` → set extensions  
- `long getMinFileSizeBytes()` → exclusive lower file‑size bound  
- `void setMinFileSizeBytes(long)` → set lower bound  
- `long getMaxFileSizeBytes()` → inclusive upper file‑size bound  
- `void setMaxFileSizeBytes(long)` → set upper bound  
- `boolean isPlanOnly()` → stop after routing plan?  
- `void setPlanOnly(boolean)` → set plan‑only flag  
- `String getGenerationProvider()` → AI generation provider name  
- `void setGenerationProvider(String)` → set provider name  
- `List<AiPromptDefinition> getPromptDefinitions()` → prompt templates  
- `void setPromptDefinitions(List<AiPromptDefinition>)` → set prompts  
- `List<AiModelDefinition> getAiDefinitions()` → AI model definitions  
- `void setAiDefinitions(List<AiModelDefinition>)` → set AI models  
- `File getModelDirectory()` → directory for relative model paths  
- `void setModelDirectory(File)` → set model directory  
- `List<AiFieldGenerationConfig> getFieldGenerations()` → per‑field routing rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` → set field generations  
- `List<AiFactDefinition> getFactDefinitions()` → reusable fact groups  
- `void setFactDefinitions(List<AiFactDefinition>)` → set fact definitions  
- `String getLlamaModelPath()` → fallback GGUF model path  
- `void setLlamaModelPath(String)` → set llama model path  
- `int getLlamaContextSize()` → llama.cpp context window size  
- `void setLlamaContextSize(int)` → set context size  
- `int getLlamaMaxOutputTokens()` → max output tokens per call  
- `void setLlamaMaxOutputTokens(int)` → set max tokens  
- `float getLlamaTemperature()` → llama.cpp sampling temperature  
- `void setLlamaTemperature(float)` → set temperature  
- `int getLlamaThreads()` → CPU threads for llama.cpp  
- `void setLlamaThreads(int)` → set thread count  
- `String getPluginVersion()` → generator plugin version  
- `void setPluginVersion(String)` → set plugin version  
- `String getAiVersion()` → AI summarisation logic version  
- `void setAiVersion(String)` → set AI version  
- `String getProjectName()` → project title for index  
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
- No checked exceptions thrown.
- Null values allowed for optional fields (`@Nullable`).

#### Concurrency
- No synchronization; instance is not thread‑safe. Use per‑thread copies or external locking.

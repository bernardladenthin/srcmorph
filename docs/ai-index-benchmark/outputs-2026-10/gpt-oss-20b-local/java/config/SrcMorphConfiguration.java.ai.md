### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:10:19Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all runtime parameters for a srcmorph run, exposed as a mutable JavaBean for Maven, JSON/YAML, or code binding.

#### Purpose
- Central configuration holder for srcmorph executions.
- Supplies defaults, paths, AI settings, and routing rules.

#### Type
- Class, public, mutable, annotated `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setters: `File`, `String`, `int`, `float`, `boolean`, `List<...>`, `@Nullable` variants.
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getters return configured values or defaults.
- Mutated fields via setters.
- No external resources written.

#### Core logic
- Stores configuration values with defaults.
- Provides getters/setters for each field.
- No additional computation or validation.

#### Public API
- `SrcMorphConfiguration() -> void` – create with defaults  
- `File getBaseDirectory() -> File` – project base dir  
- `void setBaseDirectory(File)` – set base dir  
- `File getOutputDirectory() -> File` – output dir for .ai.md  
- `void setOutputDirectory(File)` – set output dir  
- `boolean isForce() -> boolean` – always regenerate flag  
- `void setForce(boolean)` – set force flag  
- `List<String> getSubtrees() -> List<String>` – source subdirs  
- `void setSubtrees(List<String>)` – set subtrees  
- `List<String> getExcludes() -> List<String>` – exclude globs  
- `void setExcludes(List<String>)` – set excludes  
- `List<String> getFileExtensions() -> List<String>` – file extensions  
- `void setFileExtensions(List<String>)` – set extensions  
- `long getMinFileSizeBytes() -> long` – min file size  
- `void setMinFileSizeBytes(long)` – set min size  
- `long getMaxFileSizeBytes() -> long` – max file size  
- `void setMaxFileSizeBytes(long)` – set max size  
- `boolean isPlanOnly() -> boolean` – stop after plan  
- `void setPlanOnly(boolean)` – set planOnly  
- `String getGenerationProvider() -> String` – AI provider name  
- `void setGenerationProvider(String)` – set provider  
- `List<AiPromptDefinition> getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates  
- `void setPromptDefinitions(List<AiPromptDefinition>)` – set prompts  
- `List<AiModelDefinition> getAiDefinitions() -> List<AiModelDefinition>` – model definitions  
- `void setAiDefinitions(List<AiModelDefinition>)` – set models  
- `File getModelDirectory() -> File` – relative model path base  
- `void setModelDirectory(File)` – set model dir  
- `List<AiFieldGenerationConfig> getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` – set field generations  
- `List<AiFactDefinition> getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups  
- `void setFactDefinitions(List<AiFactDefinition>)` – set fact definitions  
- `String getLlamaModelPath() -> String` – fallback GGUF path  
- `void setLlamaModelPath(String)` – set llama model path  
- `int getLlamaContextSize() -> int` – llama context window  
- `void setLlamaContextSize(int)` – set context size  
- `int getLlamaMaxOutputTokens() -> int` – llama max output tokens  
- `void setLlamaMaxOutputTokens(int)` – set max tokens  
- `float getLlamaTemperature() -> float` – llama sampling temperature  
- `void setLlamaTemperature(float)` – set temperature  
- `int getLlamaThreads() -> int` – llama CPU threads  
- `void setLlamaThreads(int)` – set threads  
- `String getPluginVersion() -> String` – plugin version header  
- `void setPluginVersion(String)` – set plugin version  
- `String getAiVersion() -> String` – AI logic version header  
- `void setAiVersion(String)` – set AI version  
- `String getProjectName() -> String` – project title header  
- `void setProjectName(String)` – set project name  

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
- No synchronization; mutable state is not thread‑safe.

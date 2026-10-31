### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:03:57Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration needed for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Central mutable JavaBean for srcmorph configuration.
- Exposes defaults and setters for every runtime parameter.

#### Type
`public class SrcMorphConfiguration` – class, mutable, JavaBean, Lombok `@ToString`, suppresses null‑away warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or reflection (Maven, Jackson, plain code).

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Stores defaults for output directory, AI provider, llama.cpp settings, AI version, etc.
- Provides getters/setters for all configuration properties.
- No additional computation; purely data holder.

#### Public API
- `getBaseDirectory() -> File` – project base dir  
- `setBaseDirectory(File)` – set base dir  
- `getOutputDirectory() -> File` – output dir  
- `setOutputDirectory(File)` – set output dir  
- `isForce() -> boolean` – always regenerate flag  
- `setForce(boolean)` – set force flag  
- `getSubtrees() -> List<String>` – source subdirectories  
- `setSubtrees(List<String>)` – set subtrees  
- `getExcludes() -> List<String>` – exclude globs  
- `setExcludes(List<String>)` – set excludes  
- `getFileExtensions() -> List<String>` – file extensions  
- `setFileExtensions(List<String>)` – set extensions  
- `getMinFileSizeBytes() -> long` – min file size  
- `setMinFileSizeBytes(long)` – set min size  
- `getMaxFileSizeBytes() -> long` – max file size  
- `setMaxFileSizeBytes(long)` – set max size  
- `isPlanOnly() -> boolean` – stop after plan  
- `setPlanOnly(boolean)` – set plan‑only flag  
- `getGenerationProvider() -> String` – AI provider name  
- `setGenerationProvider(String)` – set provider  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts  
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model definitions  
- `setAiDefinitions(List<AiModelDefinition>)` – set AI models  
- `getModelDirectory() -> File` – relative model path base  
- `setModelDirectory(File)` – set model directory  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – per‑field routing rules  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set field generations  
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups  
- `setFactDefinitions(List<AiFactDefinition>)` – set fact definitions  
- `getLlamaModelPath() -> String` – fallback GGUF model path  
- `setLlamaModelPath(String)` – set llama model path  
- `getLlamaContextSize() -> int` – llama context window  
- `setLlamaContextSize(int)` – set context size  
- `getLlamaMaxOutputTokens() -> int` – max output tokens  
- `setLlamaMaxOutputTokens(int)` – set max tokens  
- `getLlamaTemperature() -> float` – sampling temperature  
- `setLlamaTemperature(float)` – set temperature  
- `getLlamaThreads() -> int` – CPU thread count  
- `setLlamaThreads(int)` – set thread count  
- `getPluginVersion() -> String` – generator version in .ai.md  
- `setPluginVersion(String)` – set plugin version  
- `getAiVersion() -> String` – AI logic version in .ai.md  
- `setAiVersion(String)` – set AI version  
- `getProjectName() -> String` – project title for index  
- `setProjectName(String)` – set project name  

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null values allowed for optional fields (`@Nullable`).

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.  
- Immutable after construction if no setters are called.

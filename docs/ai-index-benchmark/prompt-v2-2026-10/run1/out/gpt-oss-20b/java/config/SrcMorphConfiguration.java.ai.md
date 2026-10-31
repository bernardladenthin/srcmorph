### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:37:03Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all runtime parameters for a srcmorph run, from Maven plugin fields to future JSON/YAML configs.

#### Purpose
- Central mutable configuration holder for srcmorph.
- Exposes getters/setters for every runtime option.

#### Type
`public class SrcMorphConfiguration`  
class, public, mutable JavaBean, annotated `@ToString`.  
No inheritance or interfaces.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or direct field injection (Maven, Jackson, code).

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Stores defaults for output directory, generation provider, llama.cpp settings, AI version, etc.
- Provides getters/setters for all configuration properties.
- No additional computation or validation.

#### Public API
- `getBaseDirectory() -> File` – project base directory  
- `setBaseDirectory(File)` – set base directory  
- `getOutputDirectory() -> File` – output dir for .ai.md files  
- `setOutputDirectory(File)` – set output dir  
- `isForce() -> boolean` – always regenerate AI fields  
- `setForce(boolean)` – set force flag  
- `getSubtrees() -> List<String>` – source subdirectories  
- `setSubtrees(List<String>)` – set subtrees  
- `getExcludes() -> List<String>` – glob patterns to skip  
- `setExcludes(List<String>)` – set excludes  
- `getFileExtensions() -> List<String>` – file extensions to index  
- `setFileExtensions(List<String>)` – set extensions  
- `getMinFileSizeBytes() -> long` – lower file‑size bound  
- `setMinFileSizeBytes(long)` – set lower bound  
- `getMaxFileSizeBytes() -> long` – upper file‑size bound  
- `setMaxFileSizeBytes(long)` – set upper bound  
- `isPlanOnly() -> boolean` – stop after routing plan  
- `setPlanOnly(boolean)` – set plan‑only flag  
- `getGenerationProvider() -> String` – AI provider name  
- `setGenerationProvider(String)` – set provider  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts  
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model definitions  
- `setAiDefinitions(List<AiModelDefinition>)` – set models  
- `getModelDirectory() -> File` – directory for relative model paths  
- `setModelDirectory(File)` – set model directory  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – per‑field routing rules  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set routing rules  
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups  
- `setFactDefinitions(List<AiFactDefinition>)` – set fact groups  
- `getLlamaModelPath() -> String` – fallback GGUF model path  
- `setLlamaModelPath(String)` – set fallback model path  
- `getLlamaContextSize() -> int` – llama.cpp context window size  
- `setLlamaContextSize(int)` – set context size  
- `getLlamaMaxOutputTokens() -> int` – max output tokens per call  
- `setLlamaMaxOutputTokens(int)` – set max tokens  
- `getLlamaTemperature() -> float` – llama.cpp sampling temperature  
- `setLlamaTemperature(float)` – set temperature  
- `getLlamaThreads() -> int` – CPU threads for llama.cpp  
- `setLlamaThreads(int)` – set thread count  
- `getPluginVersion() -> String` – generator plugin version  
- `setPluginVersion(String)` – set plugin version  
- `getAiVersion() -> String` – AI summarisation logic version  
- `setAiVersion(String)` – set AI version  
- `getProjectName() -> String` – project title for aggregate index  
- `setProjectName(String)` – set project title  

**Total public methods: 46 (23 getter/setter pairs).**

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions thrown; all setters accept nullable values.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

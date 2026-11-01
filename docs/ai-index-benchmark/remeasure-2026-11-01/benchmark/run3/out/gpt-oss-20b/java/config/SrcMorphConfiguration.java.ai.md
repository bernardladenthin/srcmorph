### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:05:11Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores runtime settings for AI‑powered source generation.
- Serves as a single shared config object across plugin, CLI, and programmatic use.

#### Type
`public class SrcMorphConfiguration`  
final? none; mutable JavaBean; Lombok `@ToString`; suppresses null‑away warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or direct field injection (Maven, Jackson, etc.).
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp parameters, and AI version.
- No algorithmic processing; purely data holder.

#### Public API
- `getBaseDirectory() -> File` – project root.  
- `setBaseDirectory(File)` – set root.  
- `getOutputDirectory() -> File` – output dir, default `src/site/ai`.  
- `setOutputDirectory(File)` – set output dir.  
- `isForce() -> boolean` – always regenerate flag.  
- `setForce(boolean)` – set flag.  
- `getSubtrees() -> List<String>` – source subpaths.  
- `setSubtrees(List<String>)` – set subtrees.  
- `getExcludes() -> List<String>` – glob excludes.  
- `setExcludes(List<String>)` – set excludes.  
- `getFileExtensions() -> List<String>` – file extensions.  
- `setFileExtensions(List<String>)` – set extensions.  
- `getMinFileSizeBytes() -> long` – lower size bound.  
- `setMinFileSizeBytes(long)` – set lower bound.  
- `getMaxFileSizeBytes() -> long` – upper size bound.  
- `setMaxFileSizeBytes(long)` – set upper bound.  
- `isPlanOnly() -> boolean` – stop after routing plan.  
- `setPlanOnly(boolean)` – set flag.  
- `getGenerationProvider() -> String` – AI provider name.  
- `setGenerationProvider(String)` – set provider.  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set templates.  
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model configs.  
- `setAiDefinitions(List<AiModelDefinition>)` – set models.  
- `getModelDirectory() -> File` – base for relative model paths.  
- `setModelDirectory(File)` – set directory.  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.  
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.  
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.  
- `getLlamaModelPath() -> String` – fallback GGUF path.  
- `setLlamaModelPath(String)` – set path.  
- `getLlamaContextSize() -> int` – llama.cpp context window.  
- `setLlamaContextSize(int)` – set size.  
- `getLlamaMaxOutputTokens() -> int` – max output tokens.  
- `setLlamaMaxOutputTokens(int)` – set tokens.  
- `getLlamaTemperature() -> float` – sampling temperature.  
- `setLlamaTemperature(float)` – set temperature.  
- `getLlamaThreads() -> int` – CPU thread count.  
- `setLlamaThreads(int)` – set threads.  
- `getPluginVersion() -> String` – generator version.  
- `setPluginVersion(String)` – set version.  
- `getAiVersion() -> String` – AI logic version.  
- `setAiVersion(String)` – set AI version.  
- `getProjectName() -> String` – project title.  
- `setProjectName(String)` – set title.

#### Dependencies
- `java.io.File`, `java.util.List`, `org.jspecify.annotations.Nullable`, `lombok.ToString`,  
  `net.ladenthin.srcmorph.prompt.AiPromptDefinition`,  
  `net.ladenthin.srcmorph.config.AiModelDefinition`,  
  `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`,  
  `net.ladenthin.srcmorph.config.AiFactDefinition`,  
  `net.ladenthin.srcmorph.config.AiGenerationConfig`.

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept null where annotated.

#### Concurrency
- No synchronization; intended for single‑threaded configuration assembly.

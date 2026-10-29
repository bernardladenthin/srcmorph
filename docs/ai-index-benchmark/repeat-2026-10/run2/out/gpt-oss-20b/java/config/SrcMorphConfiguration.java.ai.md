### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:12:12Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Central configuration holder for srcmorph engine.
- Exposes mutable JavaBean properties for binding frameworks.

#### Type
- Class `SrcMorphConfiguration` (public, mutable, no inheritance).

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for all configuration fields.
- Injected via Maven `@Parameter`, Jackson, or direct code.

#### Output
- Getter methods expose current configuration state.
- `toString()` via Lombok for debugging.

#### Core logic
- Stores defaults for output directory, AI provider, llama.cpp settings, and version strings.
- Provides getters/setters for:
  - Project base and output directories.
  - Force regeneration flag.
  - Source subtrees, excludes, file extensions.
  - File size bounds.
  - Plan‑only mode.
  - AI generation provider.
  - Prompt, model, field, fact definitions.
  - Model directory and llama.cpp fallback model path.
  - Llama.cpp context size, max tokens, temperature, threads.
  - Plugin and AI version strings.
  - Project name for index title.

#### Public API
- `File getBaseDirectory() -> File` – retrieve base dir.
- `void setBaseDirectory(File)` – set base dir.
- `File getOutputDirectory() -> File` – get output dir.
- `void setOutputDirectory(File)` – set output dir.
- `boolean isForce() -> boolean` – force regeneration flag.
- `void setForce(boolean)` – set force flag.
- `List<String> getSubtrees() -> List<String>` – source subtrees.
- `void setSubtrees(List<String>)` – set subtrees.
- `List<String> getExcludes() -> List<String>` – exclude globs.
- `void setExcludes(List<String>)` – set excludes.
- `List<String> getFileExtensions() -> List<String>` – file extensions.
- `void setFileExtensions(List<String>)` – set extensions.
- `long getMinFileSizeBytes() -> long` – min file size.
- `void setMinFileSizeBytes(long)` – set min size.
- `long getMaxFileSizeBytes() -> long` – max file size.
- `void setMaxFileSizeBytes(long)` – set max size.
- `boolean isPlanOnly() -> boolean` – plan‑only mode.
- `void setPlanOnly(boolean)` – set plan‑only.
- `String getGenerationProvider() -> String` – AI provider name.
- `void setGenerationProvider(String)` – set provider.
- `List<AiPromptDefinition> getPromptDefinitions() -> List<AiPromptDefinition>` – prompt defs.
- `void setPromptDefinitions(List<AiPromptDefinition>)` – set prompt defs.
- `List<AiModelDefinition> getAiDefinitions() -> List<AiModelDefinition>` – model defs.
- `void setAiDefinitions(List<AiModelDefinition>)` – set model defs.
- `File getModelDirectory() -> File` – model dir.
- `void setModelDirectory(File)` – set model dir.
- `List<AiFieldGenerationConfig> getFieldGenerations() -> List<AiFieldGenerationConfig>` – field routing rules.
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` – set field rules.
- `List<AiFactDefinition> getFactDefinitions() -> List<AiFactDefinition>` – fact groups.
- `void setFactDefinitions(List<AiFactDefinition>)` – set fact groups.
- `String getLlamaModelPath() -> String` – fallback llama model path.
- `void setLlamaModelPath(String)` – set llama model path.
- `int getLlamaContextSize() -> int` – llama context window.
- `void setLlamaContextSize(int)` – set context size.
- `int getLlamaMaxOutputTokens() -> int` – max output tokens.
- `void setLlamaMaxOutputTokens(int)` – set max tokens.
- `float getLlamaTemperature() -> float` – sampling temperature.
- `void setLlamaTemperature(float)` – set temperature.
- `int getLlamaThreads() -> int` – CPU thread count.
- `void setLlamaThreads(int)` – set thread count.
- `String getPluginVersion() -> String` – plugin version.
- `void setPluginVersion(String)` – set plugin version.
- `String getAiVersion() -> String` – AI logic version.
- `void setAiVersion(String)` – set AI version.
- `String getProjectName() -> String` – project title.
- `void setProjectName(String)` – set project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions; setters accept null for optional fields.

#### Concurrency
- No synchronization; intended for single‑threaded configuration assembly.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:01:19Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds all configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores mutable JavaBean configuration for AI‑powered source generation.
- Provides defaults and exposes getters/setters for every parameter.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, final (implicitly), mutable JavaBean.  
  - Extends: none.  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`.  
  - Fields (23): `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`.  
  - Constants (9): `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- Constructor: no parameters.  
- Setter parameters: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.  
- Nullable annotations on list and file fields.

#### Output
- Getter return types: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.  
- No side effects beyond field mutation.

#### Core logic
- No algorithmic logic; only property accessors.  
- Constructor performs no initialization beyond defaults.

#### Public API
- `SrcMorphConfiguration()` -> new config instance.  
- `File getBaseDirectory()` -> base dir.  
- `void setBaseDirectory(File)` -> set base dir.  
- `File getOutputDirectory()` -> output dir.  
- `void setOutputDirectory(File)` -> set output dir.  
- `boolean isForce()` -> force flag.  
- `void setForce(boolean)` -> set force.  
- `List<String> getSubtrees()` -> subtrees.  
- `void setSubtrees(List<String>)` -> set subtrees.  
- `List<String> getExcludes()` -> excludes.  
- `void setExcludes(List<String>)` -> set excludes.  
- `List<String> getFileExtensions()` -> file extensions.  
- `void setFileExtensions(List<String>)` -> set extensions.  
- `long getMinFileSizeBytes()` -> min size.  
- `void setMinFileSizeBytes(long)` -> set min size.  
- `long getMaxFileSizeBytes()` -> max size.  
- `void setMaxFileSizeBytes(long)` -> set max size.  
- `boolean isPlanOnly()` -> plan‑only flag.  
- `void setPlanOnly(boolean)` -> set plan‑only.  
- `String getGenerationProvider()` -> provider name.  
- `void setGenerationProvider(String)` -> set provider.  
- `List<AiPromptDefinition> getPromptDefinitions()` -> prompt defs.  
- `void setPromptDefinitions(List<AiPromptDefinition>)` -> set prompt defs.  
- `List<AiModelDefinition> getAiDefinitions()` -> model defs.  
- `void setAiDefinitions(List<AiModelDefinition>)` -> set model defs.  
- `File getModelDirectory()` -> model dir.  
- `void setModelDirectory(File)` -> set model dir.  
- `List<AiFieldGenerationConfig> getFieldGenerations()` -> field gen rules.  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` -> set field gen rules.  
- `List<AiFactDefinition> getFactDefinitions()` -> fact groups.  
- `void setFactDefinitions(List<AiFactDefinition>)` -> set fact groups.  
- `String getLlamaModelPath()` -> llama model path.  
- `void setLlamaModelPath(String)` -> set llama model path.  
- `int getLlamaContextSize()` -> llama context size.  
- `void setLlamaContextSize(int)` -> set llama context size.  
- `int getLlamaMaxOutputTokens()` -> llama max tokens.  
- `void setLlamaMaxOutputTokens(int)` -> set llama max tokens.  
- `float getLlamaTemperature()` -> llama temperature.  
- `void setLlamaTemperature(float)` -> set llama temperature.  
- `int getLlamaThreads()` -> llama thread count.  
- `void setLlamaThreads(int)` -> set llama threads.  
- `String getPluginVersion()` -> plugin version.  
- `void setPluginVersion(String)` -> set plugin version.  
- `String getAiVersion()` -> AI logic version.  
- `void setAiVersion(String)` -> set AI version.  
- `String getProjectName()` -> project title.  
- `void setProjectName(String)` -> set project title.

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; mutable state is not thread‑safe.

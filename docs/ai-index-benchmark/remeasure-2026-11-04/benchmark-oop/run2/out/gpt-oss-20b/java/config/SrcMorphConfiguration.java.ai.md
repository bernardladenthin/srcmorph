### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:19Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Holds all mutable configuration parameters for a srcmorph run, including file paths, AI provider settings, and generation rules.

#### Purpose
- Stores runtime configuration for srcmorph.
- Provides defaults and mutable JavaBean accessors.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, final (implicitly by lack of subclassing).  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`.  
  - Supertypes: `java.lang.Object`.  
  - No generics.

#### Input
- Constructor: no parameters.  
- Setter parameters: various `File`, `String`, `int`, `float`, `boolean`, `List<...>`, `@Nullable` annotations.  
- No injected dependencies.

#### Output
- Getter return types: `File`, `boolean`, `long`, `int`, `float`, `String`, `List<...>`, `@Nullable`.  
- No side‑effects beyond field mutation.

#### Core logic
- Holds default constants for output directory, AI provider, llama.cpp settings, and AI version.  
- Provides getters and setters for each configuration field.  
- No additional computation or validation logic.

#### Public API
- `SrcMorphConfiguration()` → constructor, initializes defaults.  
- `File getBaseDirectory()` → base directory.  
- `void setBaseDirectory(File)` → set base directory.  
- `File getOutputDirectory()` → output directory.  
- `void setOutputDirectory(File)` → set output directory.  
- `boolean isForce()` → force regeneration flag.  
- `void setForce(boolean)` → set force flag.  
- `List<String> getSubtrees()` → source subtrees.  
- `void setSubtrees(List<String>)` → set subtrees.  
- `List<String> getExcludes()` → exclude globs.  
- `void setExcludes(List<String>)` → set excludes.  
- `List<String> getFileExtensions()` → file extensions.  
- `void setFileExtensions(List<String>)` → set extensions.  
- `long getMinFileSizeBytes()` → min file size.  
- `void setMinFileSizeBytes(long)` → set min size.  
- `long getMaxFileSizeBytes()` → max file size.  
- `void setMaxFileSizeBytes(long)` → set max size.  
- `boolean isPlanOnly()` → plan‑only flag.  
- `void setPlanOnly(boolean)` → set plan‑only flag.  
- `String getGenerationProvider()` → AI provider name.  
- `void setGenerationProvider(String)` → set provider.  
- `List<AiPromptDefinition> getPromptDefinitions()` → prompt templates.  
- `void setPromptDefinitions(List<AiPromptDefinition>)` → set prompts.  
- `List<AiModelDefinition> getAiDefinitions()` → AI model definitions.  
- `void setAiDefinitions(List<AiModelDefinition>)` → set models.  
- `File getModelDirectory()` → model directory.  
- `void setModelDirectory(File)` → set model directory.  
- `List<AiFieldGenerationConfig> getFieldGenerations()` → field generation rules.  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` → set field rules.  
- `List<AiFactDefinition> getFactDefinitions()` → fact definitions.  
- `void setFactDefinitions(List<AiFactDefinition>)` → set facts.  
- `String getLlamaModelPath()` → llama.cpp model path.  
- `void setLlamaModelPath(String)` → set llama model path.  
- `int getLlamaContextSize()` → llama context size.  
- `void setLlamaContextSize(int)` → set context size.  
- `int getLlamaMaxOutputTokens()` → llama max output tokens.  
- `void setLlamaMaxOutputTokens(int)` → set max tokens.  
- `float getLlamaTemperature()` → llama temperature.  
- `void setLlamaTemperature(float)` → set temperature.  
- `int getLlamaThreads()` → llama thread count.  
- `void setLlamaThreads(int)` → set thread count.  
- `String getPluginVersion()` → plugin version.  
- `void setPluginVersion(String)` → set plugin version.  
- `String getAiVersion()` → AI logic version.  
- `void setAiVersion(String)` → set AI version.  
- `String getProjectName()` → project title.  
- `void setProjectName(String)` → set project title.

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None declared or thrown.

#### Concurrency
- No synchronization or thread‑safety guarantees. All fields are mutable and accessed without locking.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:47Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds all mutable configuration needed to run a srcmorph AI generation job, exposed as a JavaBean for Maven, JSON/YAML, or code binding.

#### Purpose
- Stores AI generation parameters, file selection rules, and model settings.
- Provides defaults for all fields.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, final (class itself), mutable JavaBean  
  - Supertypes: none  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`  
  - Fields (23):  
  - `File baseDirectory`  
  - `File outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY)`  
  - `boolean force`  
  - `@Nullable List<String> subtrees`  
  - `@Nullable List<String> excludes`  
  - `@Nullable List<String> fileExtensions`  
  - `long minFileSizeBytes`  
  - `long maxFileSizeBytes`  
  - `boolean planOnly`  
  - `String generationProvider = DEFAULT_GENERATION_PROVIDER`  
  - `@Nullable List<AiPromptDefinition> promptDefinitions`  
  - `@Nullable List<AiModelDefinition> aiDefinitions`  
  - `@Nullable File modelDirectory`  
  - `@Nullable List<AiFieldGenerationConfig> fieldGenerations`  
  - `@Nullable List<AiFactDefinition> factDefinitions`  
  - `@Nullable String llamaModelPath`  
  - `int llamaContextSize = DEFAULT_LLAMA_CONTEXT_SIZE`  
  - `int llamaMaxOutputTokens = DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`  
  - `float llamaTemperature = DEFAULT_LLAMA_TEMPERATURE`  
  - `int llamaThreads = DEFAULT_LLAMA_THREADS`  
  - `String pluginVersion = ""`  
  - `String aiVersion = DEFAULT_AI_VERSION`  
  - `@Nullable String projectName`

#### Input
- Constructor: none (default constructor).  
- Setters accept: `File`, `boolean`, `@Nullable List<String>`, `long`, `String`, `@Nullable List<AiPromptDefinition>`, `@Nullable List<AiModelDefinition>`, `@Nullable File`, `@Nullable List<AiFieldGenerationConfig>`, `@Nullable List<AiFactDefinition>`, `@Nullable String`, `int`, `float`.

#### Output
- Getters return the corresponding field values.  
- No side‑effects beyond field mutation.

#### Core logic
- No algorithmic logic; only property accessors.

#### Public API
- `SrcMorphConfiguration()` -> constructs with defaults  
- `File getBaseDirectory()` -> base dir  
- `void setBaseDirectory(File)` -> set base dir  
- `File getOutputDirectory()` -> output dir  
- `void setOutputDirectory(File)` -> set output dir  
- `boolean isForce()` -> force flag  
- `void setForce(boolean)` -> set force flag  
- `@Nullable List<String> getSubtrees()` -> subtrees  
- `void setSubtrees(@Nullable List<String>)` -> set subtrees  
- `@Nullable List<String> getExcludes()` -> excludes  
- `void setExcludes(@Nullable List<String>)` -> set excludes  
- `@Nullable List<String> getFileExtensions()` -> file extensions  
- `void setFileExtensions(@Nullable List<String>)` -> set file extensions  
- `long getMinFileSizeBytes()` -> min size  
- `void setMinFileSizeBytes(long)` -> set min size  
- `long getMaxFileSizeBytes()` -> max size  
- `void setMaxFileSizeBytes(long)` -> set max size  
- `boolean isPlanOnly()` -> plan‑only flag  
- `void setPlanOnly(boolean)` -> set plan‑only flag  
- `String getGenerationProvider()` -> provider name  
- `void setGenerationProvider(String)` -> set provider  
- `@Nullable List<AiPromptDefinition> getPromptDefinitions()` -> prompt defs  
- `void setPromptDefinitions(@Nullable List<AiPromptDefinition>)` -> set prompt defs  
- `@Nullable List<AiModelDefinition> getAiDefinitions()` -> model defs  
- `void setAiDefinitions(@Nullable List<AiModelDefinition>)` -> set model defs  
- `@Nullable File getModelDirectory()` -> model dir  
- `void setModelDirectory(@Nullable File)` -> set model dir  
- `@Nullable List<AiFieldGenerationConfig> getFieldGenerations()` -> field rules  
- `void setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` -> set field rules  
- `@Nullable List<AiFactDefinition> getFactDefinitions()` -> fact groups  
- `void setFactDefinitions(@Nullable List<AiFactDefinition>)` -> set fact groups  
- `@Nullable String getLlamaModelPath()` -> llama model path  
- `void setLlamaModelPath(@Nullable String)` -> set llama model path  
- `int getLlamaContextSize()` -> context size  
- `void setLlamaContextSize(int)` -> set context size  
- `int getLlamaMaxOutputTokens()` -> max output tokens  
- `void setLlamaMaxOutputTokens(int)` -> set max output tokens  
- `float getLlamaTemperature()` -> temperature  
- `void setLlamaTemperature(float)` -> set temperature  
- `int getLlamaThreads()` -> thread count  
- `void setLlamaThreads(int)` -> set thread count  
- `String getPluginVersion()` -> plugin version  
- `void setPluginVersion(String)` -> set plugin version  
- `String getAiVersion()` -> AI logic version  
- `void setAiVersion(String)` -> set AI logic version  
- `@Nullable String getProjectName()` -> project title  
- `void setProjectName(@Nullable String)` -> set project title  

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiModelDefinition`  
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`  
- `net.ladenthin.srcmorph.config.AiFactDefinition`  
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (for DEFAULT_LLAMA_THREADS)

#### Exceptions / Errors
- None declared or thrown.

#### Concurrency
- No synchronization; instance is not thread‑safe.

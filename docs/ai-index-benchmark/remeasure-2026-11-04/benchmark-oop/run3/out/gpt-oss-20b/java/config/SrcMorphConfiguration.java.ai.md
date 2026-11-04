### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:52Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Holds all configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores mutable configuration for AI‑powered source generation.
- Provides defaults and binding hooks for Maven plugin and future CLI.

#### Type
- `public class SrcMorphConfiguration`  
  - Annotations: `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`, `@ToString`  
  - No superclass or interfaces.  
  - Fields (22):  
    - `private File baseDirectory;`  
    - `private File outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY);`  
    - `private boolean force;`  
    - `private @Nullable List<String> subtrees;`  
    - `private @Nullable List<String> excludes;`  
    - `private @Nullable List<String> fileExtensions;`  
    - `private long minFileSizeBytes;`  
    - `private long maxFileSizeBytes;`  
    - `private boolean planOnly;`  
    - `private String generationProvider = DEFAULT_GENERATION_PROVIDER;`  
    - `private @Nullable List<AiPromptDefinition> promptDefinitions;`  
    - `private @Nullable List<AiModelDefinition> aiDefinitions;`  
    - `private @Nullable File modelDirectory;`  
    - `private @Nullable List<AiFieldGenerationConfig> fieldGenerations;`  
    - `private @Nullable List<AiFactDefinition> factDefinitions;`  
    - `private @Nullable String llamaModelPath;`  
    - `private int llamaContextSize = DEFAULT_LLAMA_CONTEXT_SIZE;`  
    - `private int llamaMaxOutputTokens = DEFAULT_LLAMA_MAX_OUTPUT_TOKENS;`  
    - `private float llamaTemperature = DEFAULT_LLAMA_TEMPERATURE;`  
    - `private int llamaThreads = DEFAULT_LLAMA_THREADS;`  
    - `private String pluginVersion = "";`  
    - `private String aiVersion = DEFAULT_AI_VERSION;`  
    - `private @Nullable String projectName;`

#### Input
- Constructor: no parameters.  
- Setters accept: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `int`, `float`.

#### Output
- Getters return the corresponding field values.  
- No state mutation beyond field assignments.  
- `toString()` (via Lombok) outputs all fields.

#### Core logic
- None; purely a JavaBean with default values and accessor methods.

#### Public API
- `SrcMorphConfiguration()` → constructs with defaults  
- `File getBaseDirectory()` → base dir  
- `void setBaseDirectory(File)` → set base dir  
- `File getOutputDirectory()` → output dir  
- `void setOutputDirectory(File)` → set output dir  
- `boolean isForce()` → force flag  
- `void setForce(boolean)` → set force flag  
- `List<String> getSubtrees()` → subtrees  
- `void setSubtrees(List<String>)` → set subtrees  
- `List<String> getExcludes()` → excludes  
- `void setExcludes(List<String>)` → set excludes  
- `List<String> getFileExtensions()` → file extensions  
- `void setFileExtensions(List<String>)` → set file extensions  
- `long getMinFileSizeBytes()` → min size  
- `void setMinFileSizeBytes(long)` → set min size  
- `long getMaxFileSizeBytes()` → max size  
- `void setMaxFileSizeBytes(long)` → set max size  
- `boolean isPlanOnly()` → plan‑only flag  
- `void setPlanOnly(boolean)` → set plan‑only flag  
- `String getGenerationProvider()` → provider name  
- `void setGenerationProvider(String)` → set provider  
- `List<AiPromptDefinition> getPromptDefinitions()` → prompt defs  
- `void setPromptDefinitions(List<AiPromptDefinition>)` → set prompt defs  
- `List<AiModelDefinition> getAiDefinitions()` → AI model defs  
- `void setAiDefinitions(List<AiModelDefinition>)` → set AI model defs  
- `File getModelDirectory()` → model dir  
- `void setModelDirectory(File)` → set model dir  
- `List<AiFieldGenerationConfig> getFieldGenerations()` → field generation rules  
- `void setFieldGenerations(List<AiFieldGenerationConfig>)` → set field generation rules  
- `List<AiFactDefinition> getFactDefinitions()` → fact groups  
- `void setFactDefinitions(List<AiFactDefinition>)` → set fact groups  
- `String getLlamaModelPath()` → llama model path  
- `void setLlamaModelPath(String)` → set llama model path  
- `int getLlamaContextSize()` → llama context size  
- `void setLlamaContextSize(int)` → set llama context size  
- `int getLlamaMaxOutputTokens()` → llama max tokens  
- `void setLlamaMaxOutputTokens(int)` → set llama max tokens  
- `float getLlamaTemperature()` → llama temperature  
- `void setLlamaTemperature(float)` → set llama temperature  
- `int getLlamaThreads()` → llama thread count  
- `void setLlamaThreads(int)` → set llama thread count  
- `String getPluginVersion()` → plugin version  
- `void setPluginVersion(String)` → set plugin version  
- `String getAiVersion()` → AI logic version  
- `void setAiVersion(String)` → set AI logic version  
- `String getProjectName()` → project title  
- `void setProjectName(String)` → set project title  

#### Dependencies
- `java.io.File`, `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`  

#### Exceptions / Errors
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization or concurrent constructs; bean is not thread‑safe.

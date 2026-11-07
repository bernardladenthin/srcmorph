### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:59Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Holds all mutable configuration needed for a srcmorph run, from Maven parameters to future JSON/YAML or programmatic use.

#### Purpose
- Stores runtime parameters for AI‑based source generation.
- Serves as a single shared config object for Maven plugin, CLI, or code.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, mutable JavaBean.  
  - Supertypes: none.  
  - Annotations: `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`, `@ToString`.  
  - Fields (23):  
    - `File baseDirectory;`  
    - `File outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY);`  
    - `boolean force;`  
    - `@Nullable List<String> subtrees;`  
    - `@Nullable List<String> excludes;`  
    - `@Nullable List<String> fileExtensions;`  
    - `long minFileSizeBytes;`  
    - `long maxFileSizeBytes;`  
    - `boolean planOnly;`  
    - `String generationProvider = DEFAULT_GENERATION_PROVIDER;`  
    - `@Nullable List<AiPromptDefinition> promptDefinitions;`  
    - `@Nullable List<AiModelDefinition> aiDefinitions;`  
    - `@Nullable File modelDirectory;`  
    - `@Nullable List<AiFieldGenerationConfig> fieldGenerations;`  
    - `@Nullable List<AiFactDefinition> factDefinitions;`  
    - `@Nullable String llamaModelPath;`  
    - `int llamaContextSize = DEFAULT_LLAMA_CONTEXT_SIZE;`  
    - `int llamaMaxOutputTokens = DEFAULT_LLAMA_MAX_OUTPUT_TOKENS;`  
    - `float llamaTemperature = DEFAULT_LLAMA_TEMPERATURE;`  
    - `int llamaThreads = DEFAULT_LLAMA_THREADS;`  
    - `String pluginVersion = "";`  
    - `String aiVersion = DEFAULT_AI_VERSION;`  
    - `@Nullable String projectName;`  
  - Constants (11):  
    - `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- Constructor: no parameters, defaults applied.
- Setter methods accept values for each field (File, boolean, List, long, int, float, String, @Nullable variants).

#### Output
- Getter methods expose current field values.
- No state mutation beyond setters.
- No external resources written.

#### Core logic
- Pure data holder; no algorithmic behavior.

#### Public API
- `getBaseDirectory() -> File` – project base directory.  
- `setBaseDirectory(File)` – set base directory.  
- `getOutputDirectory() -> File` – output dir for .ai.md.  
- `setOutputDirectory(File)` – set output dir.  
- `isForce() -> boolean` – always regenerate AI fields.  
- `setForce(boolean)` – set force flag.  
- `getSubtrees() -> List<String>` – source subdirectory paths.  
- `setSubtrees(List<String>)` – set subtrees.  
- `getExcludes() -> List<String>` – glob patterns to skip.  
- `setExcludes(List<String>)` – set excludes.  
- `getFileExtensions() -> List<String>` – file extensions to index.  
- `setFileExtensions(List<String>)` – set extensions.  
- `getMinFileSizeBytes() -> long` – lower file‑size bound.  
- `setMinFileSizeBytes(long)` – set lower bound.  
- `getMaxFileSizeBytes() -> long` – upper file‑size bound.  
- `setMaxFileSizeBytes(long)` – set upper bound.  
- `isPlanOnly() -> boolean` – stop after routing plan.  
- `setPlanOnly(boolean)` – set plan‑only flag.  
- `getGenerationProvider() -> String` – AI provider name.  
- `setGenerationProvider(String)` – set provider.  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.  
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model definitions.  
- `setAiDefinitions(List<AiModelDefinition>)` – set models.  
- `getModelDirectory() -> File` – directory for relative model paths.  
- `setModelDirectory(File)` – set model directory.  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – per‑field routing rules.  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set routing rules.  
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.  
- `setFactDefinitions(List<AiFactDefinition>)` – set fact groups.  
- `getLlamaModelPath() -> String` – GGUF model path fallback.  
- `setLlamaModelPath(String)` – set GGUF path.  
- `getLlamaContextSize() -> int` – llama.cpp context window.  
- `setLlamaContextSize(int)` – set context size.  
- `getLlamaMaxOutputTokens() -> int` – max output tokens.  
- `setLlamaMaxOutputTokens(int)` – set max tokens.  
- `getLlamaTemperature() -> float` – sampling temperature.  
- `setLlamaTemperature(float)` – set temperature.  
- `getLlamaThreads() -> int` – CPU threads for llama.cpp.  
- `setLlamaThreads(int)` – set thread count.  
- `getPluginVersion() -> String` – plugin version in .ai.md header.  
- `setPluginVersion(String)` – set plugin version.  
- `getAiVersion() -> String` – AI logic version in .ai.md header.  
- `setAiVersion(String)` – set AI version.  
- `getProjectName() -> String` – project title for index.  
- `setProjectName(String)` – set project title.

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept null for nullable fields.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

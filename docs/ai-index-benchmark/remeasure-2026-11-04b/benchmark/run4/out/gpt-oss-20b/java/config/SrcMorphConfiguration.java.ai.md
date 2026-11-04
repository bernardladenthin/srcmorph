### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds all mutable configuration needed to run a srcmorph AI generation job, exposed as a JavaBean for Maven, JSON/YAML, or programmatic use.

#### Purpose
- Stores AI generation parameters, file selection rules, and model settings for a srcmorph run.
- Serves as the single shared config object for all binding surfaces.

#### Type
- `public class SrcMorphConfiguration`  
  - Modifiers: public, final (implicitly), mutable JavaBean.  
  - Supertypes: none.  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`.  
  - Fields (23):  
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
  - Constants (7): `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- Constructor: none.  
- Setters accept: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.  
- No injected dependencies; all values are set via setters.

#### Output
- Getters return the current field values.  
- No state mutation beyond setters.  
- No external resources written or read.

#### Core logic
- None; the class is a pure data holder with getters and setters.

#### Public API
- Constructor: `SrcMorphConfiguration() -> void` – creates defaults.  
- Getters (23):  
  - `getBaseDirectory() -> File` – base project dir.  
  - `getOutputDirectory() -> File` – output dir for .ai.md.  
  - `isForce() -> boolean` – always regenerate flag.  
  - `getSubtrees() -> List<String>` – source subdirs.  
  - `getExcludes() -> List<String>` – exclude globs.  
  - `getFileExtensions() -> List<String>` – file extensions.  
  - `getMinFileSizeBytes() -> long` – lower size bound.  
  - `getMaxFileSizeBytes() -> long` – upper size bound.  
  - `isPlanOnly() -> boolean` – stop after routing plan.  
  - `getGenerationProvider() -> String` – AI provider name.  
  - `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.  
  - `getAiDefinitions() -> List<AiModelDefinition>` – model definitions.  
  - `getModelDirectory() -> File` – model path base.  
  - `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.  
  - `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.  
  - `getLlamaModelPath() -> String` – fallback GGUF path.  
  - `getLlamaContextSize() -> int` – context window.  
  - `getLlamaMaxOutputTokens() -> int` – max tokens.  
  - `getLlamaTemperature() -> float` – sampling temperature.  
  - `getLlamaThreads() -> int` – CPU threads.  
  - `getPluginVersion() -> String` – generator version.  
  - `getAiVersion() -> String` – AI logic version.  
  - `getProjectName() -> String` – project title.  
- Setters (23):  
  - `setBaseDirectory(File)` – set base dir.  
  - `setOutputDirectory(File)` – set output dir.  
  - `setForce(boolean)` – set regenerate flag.  
  - `setSubtrees(List<String>)` – set subtrees.  
  - `setExcludes(List<String>)` – set excludes.  
  - `setFileExtensions(List<String>)` – set extensions.  
  - `setMinFileSizeBytes(long)` – set lower bound.  
  - `setMaxFileSizeBytes(long)` – set upper bound.  
  - `setPlanOnly(boolean)` – set plan‑only flag.  
  - `setGenerationProvider(String)` – set provider.  
  - `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.  
  - `setAiDefinitions(List<AiModelDefinition>)` – set models.  
  - `setModelDirectory(File)` – set model dir.  
  - `setFieldGenerations(List<AiFieldGenerationConfig>)` – set routing rules.  
  - `setFactDefinitions(List<AiFactDefinition>)` – set fact groups.  
  - `setLlamaModelPath(String)` – set fallback model path.  
  - `setLlamaContextSize(int)` – set context size.  
  - `setLlamaMaxOutputTokens(int)` – set max tokens.  
  - `setLlamaTemperature(float)` – set temperature.  
  - `setLlamaThreads(int)` – set thread count.  
  - `setPluginVersion(String)` – set generator version.  
  - `setAiVersion(String)` – set AI logic version.  
  - `setProjectName(String)` – set project title.

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiModelDefinition`  
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`  
- `net.ladenthin.srcmorph.config.AiFactDefinition`  
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (for `DEFAULT_LLAMA_THREADS`)

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Nullability handled via `@Nullable` annotations; callers must check.

#### Concurrency
- No synchronization; the bean is intended for single‑threaded configuration building.

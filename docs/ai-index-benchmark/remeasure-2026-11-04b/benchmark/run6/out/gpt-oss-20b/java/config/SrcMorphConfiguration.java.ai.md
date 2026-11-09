### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:35Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds all mutable configuration needed to run a srcmorph AI generation job, exposing Maven‑style parameters for later binding.

#### Purpose
- Stores runtime parameters for AI code generation.
- Provides defaults and mutable JavaBean accessors.

#### Type
- `public class SrcMorphConfiguration` (class, final, mutable JavaBean)  
  - Supertypes: none  
  - Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`  
  - Constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`  
  - Fields (22): `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`  
  - Constructor: `public SrcMorphConfiguration()` (no-op)

#### Input
- Constructor: none.  
- Setters accept: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `int`, `float`.

#### Output
- Getters return: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `File`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.  
- No side effects beyond field mutation.

#### Core logic
- No algorithmic logic; only property accessors and default field initializers.

#### Public API
- `getBaseDirectory() -> File` – retrieve base dir  
- `setBaseDirectory(File)` – set base dir  
- `getOutputDirectory() -> File` – retrieve output dir  
- `setOutputDirectory(File)` – set output dir  
- `isForce() -> boolean` – check force flag  
- `setForce(boolean)` – set force flag  
- `getSubtrees() -> List<String>` – get subtrees  
- `setSubtrees(List<String>)` – set subtrees  
- `getExcludes() -> List<String>` – get excludes  
- `setExcludes(List<String>)` – set excludes  
- `getFileExtensions() -> List<String>` – get file extensions  
- `setFileExtensions(List<String>)` – set file extensions  
- `getMinFileSizeBytes() -> long` – get min file size  
- `setMinFileSizeBytes(long)` – set min file size  
- `getMaxFileSizeBytes() -> long` – get max file size  
- `setMaxFileSizeBytes(long)` – set max file size  
- `isPlanOnly() -> boolean` – check plan‑only flag  
- `setPlanOnly(boolean)` – set plan‑only flag  
- `getGenerationProvider() -> String` – get AI provider name  
- `setGenerationProvider(String)` – set AI provider name  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – get prompt defs  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompt defs  
- `getAiDefinitions() -> List<AiModelDefinition>` – get AI model defs  
- `setAiDefinitions(List<AiModelDefinition>)` – set AI model defs  
- `getModelDirectory() -> File` – get model dir  
- `setModelDirectory(File)` – set model dir  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – get field gen rules  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set field gen rules  
- `getFactDefinitions() -> List<AiFactDefinition>` – get fact defs  
- `setFactDefinitions(List<AiFactDefinition>)` – set fact defs  
- `getLlamaModelPath() -> String` – get llama model path  
- `setLlamaModelPath(String)` – set llama model path  
- `getLlamaContextSize() -> int` – get llama context size  
- `setLlamaContextSize(int)` – set llama context size  
- `getLlamaMaxOutputTokens() -> int` – get llama max output tokens  
- `setLlamaMaxOutputTokens(int)` – set llama max output tokens  
- `getLlamaTemperature() -> float` – get llama temperature  
- `setLlamaTemperature(float)` – set llama temperature  
- `getLlamaThreads() -> int` – get llama thread count  
- `setLlamaThreads(int)` – set llama thread count  
- `getPluginVersion() -> String` – get plugin version  
- `setPluginVersion(String)` – set plugin version  
- `getAiVersion() -> String` – get AI version  
- `setAiVersion(String)` – set AI version  
- `getProjectName() -> String` – get project title  
- `setProjectName(String)` – set project title

#### Dependencies
- `java.io.File`  
- `java.util.List`  
- `lombok.ToString`  
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None declared; all setters accept nullable values where annotated.

#### Concurrency
- No synchronization; class is not thread‑safe.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:15:06Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A mutable configuration bean that aggregates all parameters required to run the srcmorph AI code‑generation engine, bindable from Maven, JSON/YAML, or Java code.

#### Purpose
- Holds all runtime settings for srcmorph.
- Serves as a single source of truth for plugin, CLI, or programmatic use.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no generics.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods receive values for:
  - File paths (`baseDirectory`, `outputDirectory`, `modelDirectory`, `llamaModelPath`).
  - Boolean flags (`force`, `planOnly`).
  - Lists of strings (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`).
  - Numeric limits (`minFileSizeBytes`, `maxFileSizeBytes`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`).
  - Strings (`generationProvider`, `pluginVersion`, `aiVersion`, `projectName`).

#### Output
- Getter methods expose the configured values.
- No side‑effects beyond field mutation.

#### Core logic
- No business logic; purely stores and exposes configuration values.
- Default constants provide fallback values when fields are unset.

#### Public API
- `getBaseDirectory() -> File` – project root.
- `setBaseDirectory(File)` – set root.
- `getOutputDirectory() -> File` – output folder.
- `setOutputDirectory(File)` – set output.
- `isForce() -> boolean` – always regenerate flag.
- `setForce(boolean)` – set flag.
- `getSubtrees() -> List<String>` – source subdirectories.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – glob patterns to skip.
- `setExcludes(List<String>)` – set excludes.
- `getFileExtensions() -> List<String>` – file extensions to index.
- `setFileExtensions(List<String>)` – set extensions.
- `getMinFileSizeBytes() -> long` – lower size bound.
- `setMinFileSizeBytes(long)` – set bound.
- `getMaxFileSizeBytes() -> long` – upper size bound.
- `setMaxFileSizeBytes(long)` – set bound.
- `isPlanOnly() -> boolean` – stop after routing plan.
- `setPlanOnly(boolean)` – set flag.
- `getGenerationProvider() -> String` – AI provider name.
- `setGenerationProvider(String)` – set provider.
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.
- `setPromptDefinitions(List<AiPromptDefinition>)` – set templates.
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model configs.
- `setAiDefinitions(List<AiModelDefinition>)` – set models.
- `getModelDirectory() -> File` – directory for relative model paths.
- `setModelDirectory(File)` – set directory.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set path.
- `getLlamaContextSize() -> int` – context window size.
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
- `setAiVersion(String)` – set version.
- `getProjectName() -> String` – project title.
- `setProjectName(String)` – set title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept null where annotated.

#### Concurrency
- No synchronization; intended for single‑threaded configuration assembly.

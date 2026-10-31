### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:53:38Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores runtime settings for AI‑powered source generation.
- Serves as a single shared config object across plugin, CLI, and programmatic use.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no special modifiers.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each field (22 fields).
- External bindings: Maven `@Parameter`, Jackson `ObjectMapper`, plain code.

#### Output
- Getter methods expose current configuration.
- No side effects beyond field mutation.

#### Core logic
- Default constants for output directory, provider, llama.cpp parameters, AI version.
- Empty constructor; all fields initialized to defaults or null.
- Getters return current field values.
- Setters assign provided values to fields.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir for .ai.md.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – always regenerate flag.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – source subdirectories.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – exclude globs.
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
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model configs.
- `setAiDefinitions(List<AiModelDefinition>)` – set models.
- `getModelDirectory() -> File` – base for relative model paths.
- `setModelDirectory(File)` – set model directory.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF path.
- `setLlamaModelPath(String)` – set fallback path.
- `getLlamaContextSize() -> int` – llama.cpp context window.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens.
- `setLlamaMaxOutputTokens(int)` – set max tokens.
- `getLlamaTemperature() -> float` – sampling temperature.
- `setLlamaTemperature(float)` – set temperature.
- `getLlamaThreads() -> int` – CPU threads for llama.cpp.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – generator version header.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI logic version header.
- `setAiVersion(String)` – set AI version.
- `getProjectName() -> String` – project title for index.
- `setProjectName(String)` – set project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null values allowed for optional fields (`@Nullable`).

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

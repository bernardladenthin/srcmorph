### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:03:27Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores runtime settings for AI‑powered source generation.
- Serves as a single shared config object for all binding surfaces.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, no generics.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or direct field injection (Maven, Jackson, etc.).
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration.
- Mutated fields reflect latest set values.
- No external resources written.

#### Core logic
- Default constants provide fallback values.
- Getters return current field values.
- Setters assign provided values to fields.
- No additional computation or validation.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir for .ai.md.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – always regenerate flag.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – source subdirectories.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – glob excludes.
- `setExcludes(List<String>)` – set excludes.
- `getFileExtensions() -> List<String>` – file extensions.
- `setFileExtensions(List<String>)` – set extensions.
- `getMinFileSizeBytes() -> long` – lower size bound.
- `setMinFileSizeBytes(long)` – set lower bound.
- `getMaxFileSizeBytes() -> long` – upper size bound.
- `setMaxFileSizeBytes(long)` – set upper bound.
- `isPlanOnly() -> boolean` – stop after routing plan.
- `setPlanOnly(boolean)` – set plan‑only flag.
- `getGenerationProvider() -> String` – AI provider name.
- `setGenerationProvider(String)` – set provider.
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model configs.
- `setAiDefinitions(List<AiModelDefinition>)` – set models.
- `getModelDirectory() -> File` – relative model path base.
- `setModelDirectory(File)` – set model directory.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set fallback path.
- `getLlamaContextSize() -> int` – llama.cpp context window.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens.
- `setLlamaMaxOutputTokens(int)` – set max tokens.
- `getLlamaTemperature() -> float` – sampling temperature.
- `setLlamaTemperature(float)` – set temperature.
- `getLlamaThreads() -> int` – CPU thread count.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – generator version header.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI logic version header.
- `setAiVersion(String)` – set AI version.
- `getProjectName() -> String` – project title header.
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
- `net.ladenthin.srcmorph.config.AiGenerationConfig`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null values allowed for optional fields (`@Nullable`).

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

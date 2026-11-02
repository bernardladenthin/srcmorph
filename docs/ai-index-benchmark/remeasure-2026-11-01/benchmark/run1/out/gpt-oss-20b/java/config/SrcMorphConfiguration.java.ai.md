### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:25:42Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Stores runtime settings for AI‑powered source generation.
- Serves as a single shared config object for all binding surfaces.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, suppresses null‑away warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or directly by binding frameworks (Maven, Jackson, etc.).

#### Output
- Getter methods expose current configuration.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp parameters, and AI version.
- No additional computation; purely holds state.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir for .ai.md files.
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
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set field generations.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set fact definitions.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set fallback model path.
- `getLlamaContextSize() -> int` – llama.cpp context window size.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens per call.
- `setLlamaMaxOutputTokens(int)` – set max tokens.
- `getLlamaTemperature() -> float` – sampling temperature.
- `setLlamaTemperature(float)` – set temperature.
- `getLlamaThreads() -> int` – CPU threads for llama.cpp.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – generator plugin version.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI summarisation logic version.
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
- No synchronization; intended for single‑threaded configuration assembly.

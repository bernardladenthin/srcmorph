### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:40:24Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, usable by Maven, JSON/YAML, or Java code.

#### Purpose
- Stores runtime settings for AI‑powered source transformations.
- Serves as a single, mutable JavaBean for binding from multiple sources.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, Lombok `@ToString`, suppresses null‑init warnings.

#### Input
- Constructor: no arguments, defaults applied.
- Setter methods for each field (e.g., `setBaseDirectory(File)`).
- External binding frameworks (Maven Plexus, Jackson, plain Java).

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp parameters, AI version.
- No algorithmic processing; purely data holder.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir for .ai.md files.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – always regenerate AI fields.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – source subdirectories.
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
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set fallback model path.
- `getLlamaContextSize() -> int` – llama.cpp context window.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens.
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
- Null handling via `@Nullable` annotations; fields may be null.

#### Concurrency
- No synchronization; intended for single‑threaded configuration assembly.

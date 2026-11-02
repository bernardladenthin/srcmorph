### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:42Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, usable by Maven, JSON/YAML, or Java code.

#### Purpose
- Stores runtime settings for AI‑powered source transformations.
- Serves as a single shared config object across all binding mechanisms.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, annotated `@ToString`, no inheritance.

#### Input
- Constructor: no‑arg, defaults only.
- Fields set via public setters or reflection (Maven, Jackson, plain code).
- Optional injected dependencies: none.

#### Output
- Getter methods expose current configuration.
- No side effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama.cpp parameters, and AI version.
- Holds lists of subtrees, excludes, file extensions, prompt definitions, model definitions, field generation rules, fact definitions.
- Tracks optional model directory, llama model path, and project name.
- No algorithmic processing; purely data storage.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – always regenerate flag.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – source subtrees.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – exclude globs.
- `setExcludes(List<String>)` – set excludes.
- `getFileExtensions() -> List<String>` – file extensions.
- `setFileExtensions(List<String>)` – set extensions.
- `getMinFileSizeBytes() -> long` – lower size bound.
- `setMinFileSizeBytes(long)` – set lower bound.
- `getMaxFileSizeBytes() -> long` – upper size bound.
- `setMaxFileSizeBytes(long)` – set upper bound.
- `isPlanOnly() -> boolean` – stop after planning.
- `setPlanOnly(boolean)` – set plan‑only flag.
- `getGenerationProvider() -> String` – AI provider name.
- `setGenerationProvider(String)` – set provider.
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model configs.
- `setAiDefinitions(List<AiModelDefinition>)` – set models.
- `getModelDirectory() -> File` – directory for relative model paths.
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
- `getLlamaThreads() -> int` – CPU thread count.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – plugin version for headers.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI logic version for headers.
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
- No explicit exceptions thrown; getters/setters assume non‑null where applicable.

#### Concurrency
- No synchronization; intended for single‑threaded configuration building.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:32Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A mutable configuration bean that aggregates all parameters required for a srcmorph run, including AI provider settings, file filtering, and model definitions.

#### Purpose
- Holds runtime configuration for srcmorph.
- Serves as a single source of truth for plugin, CLI, or programmatic use.

#### Type
`public class SrcMorphConfiguration` – JavaBean, mutable, annotated with `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setters: `File`, `String`, `int`, `float`, `boolean`, `List<...>`, `@Nullable` variants.
- Dependencies: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getters expose configuration values.
- Mutated fields via setters.
- No external resources written.

#### Core logic
- Provides default constants for output directory, provider, llama settings, and AI version.
- Stores configuration values in private fields.
- Getters return current field values; setters assign new values.
- No additional computation or validation.

#### Public API
- `getBaseDirectory() -> File` – project base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – output dir.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – force regeneration flag.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – source subtrees.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – exclude globs.
- `setExcludes(List<String>)` – set excludes.
- `getFileExtensions() -> List<String>` – file extensions.
- `setFileExtensions(List<String>)` – set extensions.
- `getMinFileSizeBytes() -> long` – min file size.
- `setMinFileSizeBytes(long)` – set min size.
- `getMaxFileSizeBytes() -> long` – max file size.
- `setMaxFileSizeBytes(long)` – set max size.
- `isPlanOnly() -> boolean` – plan‑only flag.
- `setPlanOnly(boolean)` – set plan‑only flag.
- `getGenerationProvider() -> String` – AI provider name.
- `setGenerationProvider(String)` – set provider.
- `getPromptDefinitions() -> List<AiPromptDefinition>` – prompt templates.
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.
- `getAiDefinitions() -> List<AiModelDefinition>` – AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>)` – set models.
- `getModelDirectory() -> File` – model directory.
- `setModelDirectory(File)` – set model directory.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – field generation rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set GGUF path.
- `getLlamaContextSize() -> int` – llama context window.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens.
- `setLlamaMaxOutputTokens(int)` – set max tokens.
- `getLlamaTemperature() -> float` – sampling temperature.
- `setLlamaTemperature(float)` – set temperature.
- `getLlamaThreads() -> int` – CPU thread count.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – plugin version.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI logic version.
- `setAiVersion(String)` – set AI version.
- `getProjectName() -> String` – project title.
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
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; instance is not thread‑safe.

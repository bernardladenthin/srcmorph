### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:18:27Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or code.

#### Purpose
- Central mutable JavaBean for srcmorph configuration.
- Exposes all runtime parameters for AI generation, file filtering, and output.

#### Type
`public class SrcMorphConfiguration` – mutable JavaBean, annotated `@ToString`, no inheritance.

#### Input
- Constructor: no arguments, defaults applied.
- Fields set via public setters or reflection (Maven, Jackson, etc.).
- Dependencies: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Stores defaults for output directory, generation provider, llama.cpp settings, and AI version.
- Provides getters/setters for all configuration fields.
- No additional computation or validation logic.

#### Public API
- `getBaseDirectory() -> File` – project base directory.
- `setBaseDirectory(File)` – set base directory.
- `getOutputDirectory() -> File` – output directory for .ai.md files.
- `setOutputDirectory(File)` – set output directory.
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
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – per‑field generation rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – reusable fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set facts.
- `getLlamaModelPath() -> String` – fallback GGUF model path.
- `setLlamaModelPath(String)` – set fallback model path.
- `getLlamaContextSize() -> int` – llama.cpp context window size.
- `setLlamaContextSize(int)` – set context size.
- `getLlamaMaxOutputTokens() -> int` – max output tokens per call.
- `setLlamaMaxOutputTokens(int)` – set max tokens.
- `getLlamaTemperature() -> float` – llama.cpp sampling temperature.
- `setLlamaTemperature(float)` – set temperature.
- `getLlamaThreads() -> int` – CPU threads for llama.cpp.
- `setLlamaThreads(int)` – set thread count.
- `getPluginVersion() -> String` – plugin version in .ai.md headers.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – AI logic version in .ai.md headers.
- `setAiVersion(String)` – set AI version.
- `getProjectName() -> String` – project title for aggregate index.
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
- Nullable fields handled via `@Nullable` annotations.

#### Concurrency
- No synchronization; instance is not thread‑safe. Use per‑thread copies or external locking.

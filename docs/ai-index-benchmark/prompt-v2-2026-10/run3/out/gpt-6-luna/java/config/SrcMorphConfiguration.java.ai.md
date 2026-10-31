### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:04Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source selection, AI generation, llama.cpp fallback settings, and metadata for producing `.ai.md` project indexes.

#### Purpose
- Holds the shared mutable `srcmorph` run configuration, independently of its binding source.
- Mirrors Maven parameter names for Maven, future JSON/YAML, and direct Java callers.

#### Type
- `public class SrcMorphConfiguration` — mutable class; no superclass or interfaces.
- `@ToString`; nullness annotations use JSpecify `@Nullable`.
- Seven public defaults: `DEFAULT_OUTPUT_DIRECTORY` (`"src/site/ai"`), `DEFAULT_GENERATION_PROVIDER` (`"mock"`), `DEFAULT_LLAMA_CONTEXT_SIZE` (`2048`), `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` (`128`), `DEFAULT_LLAMA_TEMPERATURE` (`0.15f`), `DEFAULT_LLAMA_THREADS` (`AiGenerationConfig.DEFAULT_THREADS`), `DEFAULT_AI_VERSION` (`"0.0.0"`).

#### Input
- No-argument constructor; initializes output directory and generation defaults.
- Setters accept project paths, source filters, size bounds, generation provider, prompt/model/rule/fact definitions, llama.cpp settings, and output metadata.
- Nullable fields allow omitted source filters and definitions, model paths/directories, and project title.

#### Output
- Getters expose all configuration values; mutable fields are updated directly by setters.
- Defaults include `force=false`, `planOnly=false`, zero size bounds, empty `pluginVersion`, and the documented provider, output directory, llama.cpp, and AI version values.

#### Core logic
- Stores project base/output directories and source-selection options: subtrees, exclude globs, extensions, and file-size bounds.
- Stores AI routing inputs: provider, prompt definitions, model definitions, per-field generation rules, and reusable fact definitions.
- Supplies fallback llama.cpp model path, context size, output token limit, temperature, and thread count; documentation limits these fallbacks to applicable configurations.
- Carries plugin/AI versions and optional project title for generated index metadata.
- Leaves Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `SrcMorphConfiguration()` -> construct configuration with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated files directory.
- `isForce()`/`setForce(boolean)` -> force field regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed file extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`; `java.util.List`.
- `lombok.ToString`; `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`.
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.

#### Concurrency
- Mutable JavaBean with unsynchronized fields; no thread-safety guarantees stated.

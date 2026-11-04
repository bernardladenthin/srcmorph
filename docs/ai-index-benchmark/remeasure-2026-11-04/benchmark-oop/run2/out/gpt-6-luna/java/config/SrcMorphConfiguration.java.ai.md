### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:35:59Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures source-file discovery, AI generation routing, llama.cpp fallbacks, and metadata for producing `.ai.md` files.

#### Purpose
- `SrcMorphConfiguration` holds mutable run settings shared across Maven, future CLI/config-file binding, and plain Java callers.
- Keeps per-goal Maven lifecycle skip flags outside the engine configuration.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; annotated `@ToString` and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- No declared supertypes or generics.

#### Input
- Constructor accepts no arguments; initializes configured defaults.
- Setters accept base/output directories, source filters and file-size bounds, planning/force flags, provider and prompt/model/fact/routing definitions, llama.cpp fallback settings, version metadata, and project name.
- Nullable fields: `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, and `projectName`.
- No resources are read by this class.

#### Output
- Getters expose the configured values; setters mutate the bean’s fields.
- Configuration values describe source selection, model-path resolution, AI generation, and metadata for generated `.ai.md` files.
- Lombok `@ToString` generates a `toString()` representation.

#### Core logic
- Initializes defaults: output directory `src/site/ai`, provider `mock`, llama.cpp context size `2048`, maximum output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, and AI version `0.0.0`.
- Leaves other values at Java defaults unless set; supports null to represent unspecified optional collections, paths, and project name.
- Stores source roots/subtrees, excludes, extensions, and size limits for processing configuration.
- Stores prompt, model, field-generation routing, and reusable fact definitions.
- Provides llama.cpp settings as fallbacks when field-generation rules are empty; model paths may be resolved against `modelDirectory`.
- Stores plugin and AI versions and an optional aggregate-project title.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY: String` — default output path.
- `DEFAULT_GENERATION_PROVIDER: String` — default provider.
- `DEFAULT_LLAMA_CONTEXT_SIZE: int` — default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS: int` — default output token limit.
- `DEFAULT_LLAMA_TEMPERATURE: float` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS: int` — default inference thread count.
- `DEFAULT_AI_VERSION: String` — default AI logic version.
- `SrcMorphConfiguration()` — construct configuration with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> regenerate existing values.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subtree filter.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context size.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback CPU thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.
- `toString()` -> Lombok-generated field representation.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `org.jspecify.annotations.Nullable`

#### Concurrency
- Mutable bean with no synchronization or thread-safety guarantees.

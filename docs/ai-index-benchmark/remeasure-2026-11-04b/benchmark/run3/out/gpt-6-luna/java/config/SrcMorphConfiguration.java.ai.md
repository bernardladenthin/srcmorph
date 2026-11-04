### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:05:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures source indexing, AI field generation, model routing, and metadata for a `srcmorph` run.

#### Purpose
- Provides the shared mutable configuration for binding `srcmorph` run parameters from Maven, future JSON/YAML inputs, or Java callers.
- Defines defaults and JavaBean accessors for source selection, AI providers and models, generation behavior, and output metadata.

#### Type
- `public class SrcMorphConfiguration` — class; no explicit superclass or interfaces; annotated `@ToString`.
- No type parameters.

#### Input
- `SrcMorphConfiguration()` takes no arguments.
- Setters accept project and output directories; source filters and file-size bounds; generation provider, prompt/model/rule/fact definitions; llama.cpp fallback parameters; and project/version metadata.
- Nullable inputs: `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, and `projectName`.

#### Output
- Getters expose all 23 configuration fields.
- Setters mutate the corresponding fields.
- Defaults: `outputDirectory` = `src/site/ai`; `generationProvider` = `mock`; llama.cpp context size = `2048`, max output tokens = `128`, temperature = `0.15f`, threads = `AiGenerationConfig.DEFAULT_THREADS`; `aiVersion` = `0.0.0`; `pluginVersion` = empty string.
- `force`, `planOnly`, and numeric file-size bounds default to Java zero values; `baseDirectory` is unset.
- Lombok `@ToString` generates a string representation.

#### Core logic
- Acts as a mutable JavaBean; the constructor performs no work.
- Stores source scope and filtering in `baseDirectory`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, and `maxFileSizeBytes`.
- Stores generation behavior and routing in `force`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, and `factDefinitions`.
- Stores model lookup and llama.cpp fallback settings in `modelDirectory`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, and `llamaThreads`.
- Stores generated-output metadata in `pluginVersion`, `aiVersion`, and `projectName`.
- `modelDirectory` is the configured base for relative model paths; absolute paths remain usable. `llamaModelPath` and llama.cpp fallback parameters apply when `fieldGenerations` is empty.
- Does not contain Maven lifecycle skip flags; an engine using this configuration executes when asked.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY: String` — default output path.
- `DEFAULT_GENERATION_PROVIDER: String` — default provider name.
- `DEFAULT_LLAMA_CONTEXT_SIZE: int` — default llama.cpp context size.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS: int` — default output-token limit.
- `DEFAULT_LLAMA_TEMPERATURE: float` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS: int` — default CPU thread count.
- `DEFAULT_AI_VERSION: String` — default AI logic version.
- `SrcMorphConfiguration()` — creates configuration with defaults.
- `getBaseDirectory()` / `setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()` / `setOutputDirectory(File)` -> generated-file directory.
- `isForce()` / `setForce(boolean)` -> regenerate existing AI fields.
- `getSubtrees()` / `setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()` / `setExcludes(List<String>)` -> excluded source-file globs.
- `getFileExtensions()` / `setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()` / `setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()` / `setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()` / `setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()` / `setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()` / `setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()` / `setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()` / `setModelDirectory(File)` -> base for relative model paths.
- `getFieldGenerations()` / `setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()` / `setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()` / `setLlamaModelPath(String)` -> llama.cpp fallback model path.
- `getLlamaContextSize()` / `setLlamaContextSize(int)` -> llama.cpp context window.
- `getLlamaMaxOutputTokens()` / `setLlamaMaxOutputTokens(int)` -> llama.cpp output-token limit.
- `getLlamaTemperature()` / `setLlamaTemperature(float)` -> llama.cpp sampling temperature.
- `getLlamaThreads()` / `setLlamaThreads(int)` -> llama.cpp CPU thread count.
- `getPluginVersion()` / `setPluginVersion(String)` -> generated-file plugin version.
- `getAiVersion()` / `setAiVersion(String)` -> generated-file AI logic version.
- `getProjectName()` / `setProjectName(String)` -> aggregate-project title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition` (same package)
- `AiGenerationConfig.DEFAULT_THREADS`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exception handling, thrown exceptions, or validation checks.
- Nullable fields and parameters are marked with `@Nullable`; `baseDirectory` and several reference fields are otherwise uninitialized until configured.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

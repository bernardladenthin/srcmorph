### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:59Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Configures source-file selection, AI prompt/model routing, llama.cpp fallbacks, and metadata for generating `.ai.md` files.

#### Purpose
- `SrcMorphConfiguration` holds the shared mutable settings for a `srcmorph` run, independent of configuration source.
- Provides defaults and JavaBean accessors for file selection, AI generation, and output metadata.

#### Type
- `public class SrcMorphConfiguration` — class; no explicit superclass or interfaces.
- Annotated `@ToString`; suppresses `NullAway.Init` and `initialization.fields.uninitialized` warnings.

#### Input
- Setters accept base/output directories, file-selection rules, AI definitions and routing, llama.cpp settings, versions, and project name.
- Nullable lists and values represent omitted optional configuration.
- Defaults: output directory `src/site/ai`; provider `mock`; llama.cpp context size `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.

#### Output
- Getters expose the configured values; setters mutate the bean’s fields.
- Configuration describes `.ai.md` output location, input scope, AI generation behavior, and generated-file metadata.
- Lombok `@ToString` provides a string representation.

#### Core logic
- Stores run configuration without executing generation or validating settings.
- `subtrees`, `excludes`, and `fileExtensions` control source scope and file selection; non-positive minimum size disables the lower bound, and non-positive maximum size means unlimited.
- `force` enables regenerating populated AI fields; `planOnly` requests stopping after routing-plan output.
- `generationProvider` selects the default provider; model definitions may override it.
- `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, and `factDefinitions` define prompts, models, per-field routing, and reusable fact groups.
- `modelDirectory` is the base for relative model paths; absolute paths remain usable. `llamaModelPath` and llama.cpp parameters are fallback settings when `fieldGenerations` is empty.
- `pluginVersion` and `aiVersion` are recorded in generated headers; `projectName` supplies the aggregate-project index title.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY: String` — default output directory.
- `DEFAULT_GENERATION_PROVIDER: String` — default provider name.
- `DEFAULT_LLAMA_CONTEXT_SIZE: int` — default llama.cpp context size.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS: int` — default llama.cpp output-token limit.
- `DEFAULT_LLAMA_TEMPERATURE: float` — default llama.cpp sampling temperature.
- `DEFAULT_LLAMA_THREADS: int` — default llama.cpp CPU thread count.
- `DEFAULT_AI_VERSION: String` — default summarisation version.
- `SrcMorphConfiguration()` — initializes defaults.
- `getBaseDirectory()` / `setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()` / `setOutputDirectory(File)` -> `.ai.md` output directory.
- `isForce()` / `setForce(boolean)` -> regenerate populated fields.
- `getSubtrees()` / `setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()` / `setExcludes(List<String>)` -> excluded file globs.
- `getFileExtensions()` / `setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()` / `setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()` / `setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()` / `setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()` / `setGenerationProvider(String)` -> default generation provider.
- `getPromptDefinitions()` / `setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()` / `setAiDefinitions(List<AiModelDefinition>)` -> AI model definitions.
- `getModelDirectory()` / `setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()` / `setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()` / `setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()` / `setLlamaModelPath(String)` -> llama.cpp fallback model path.
- `getLlamaContextSize()` / `setLlamaContextSize(int)` -> llama.cpp fallback context size.
- `getLlamaMaxOutputTokens()` / `setLlamaMaxOutputTokens(int)` -> llama.cpp fallback token limit.
- `getLlamaTemperature()` / `setLlamaTemperature(float)` -> llama.cpp fallback temperature.
- `getLlamaThreads()` / `setLlamaThreads(int)` -> llama.cpp fallback thread count.
- `getPluginVersion()` / `setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()` / `setAiVersion(String)` -> generated-header AI version.
- `getProjectName()` / `setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks.
- Nullable fields include source-selection lists, AI definition lists, model directory/path, and project name; `baseDirectory` is not annotated nullable.

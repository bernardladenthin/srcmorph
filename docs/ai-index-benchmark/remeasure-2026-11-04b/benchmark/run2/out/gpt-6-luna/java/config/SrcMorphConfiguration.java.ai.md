### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:58Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures source indexing and AI-generated documentation, including file selection, model routing, inference settings, and output metadata.

#### Purpose
- `SrcMorphConfiguration` is the mutable shared configuration for a `srcmorph` run, usable from Maven, future JSON/YAML binding, or Java callers.
- Holds source-selection rules, AI prompt/model/routing definitions, llama.cpp fallbacks, and generated-documentation metadata.

#### Type
- `public class SrcMorphConfiguration` — class; no declared superclass or interfaces; annotated `@ToString`.
- Public static final constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.
- Public no-argument constructor; mutable JavaBean fields and setters.

#### Input
- Constructor takes no parameters.
- Setters accept project/output directories; force and plan-only flags; source subtree, exclude, and extension lists; file-size bounds; generation provider; prompt, model, field-generation, and fact-definition lists; model directory and llama model path; llama context, token, temperature, and thread settings; plugin/AI versions; and project name.
- Nullable inputs: `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, and `projectName`.

#### Output
- Getters expose the configured values; setters mutate the corresponding fields.
- Default values: output directory `src/site/ai`; provider `mock`; llama context size `2048`; max output tokens `128`; temperature `0.15f`; threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.
- Other fields default to Java defaults: `null`, `false`, or `0`; `pluginVersion` defaults to `""`.
- `@ToString` provides a generated string representation.

#### Core logic
- Defines defaults for output location, generation provider, llama.cpp inference parameters, and AI summarisation version.
- Stores source-selection configuration: `baseDirectory`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, and `maxFileSizeBytes`.
- Stores generation controls: `force`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, and `factDefinitions`.
- Stores model resolution and fallback settings: `modelDirectory`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, and `llamaThreads`.
- Stores output metadata: `pluginVersion`, `aiVersion`, and `projectName`.
- `DEFAULT_LLAMA_THREADS` delegates to `AiGenerationConfig.DEFAULT_THREADS`; other defaults are literals.
- Accessors assign and return fields directly; no processing or validation is shown.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY: String`, `DEFAULT_GENERATION_PROVIDER: String`, `DEFAULT_LLAMA_CONTEXT_SIZE: int`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS: int`, `DEFAULT_LLAMA_TEMPERATURE: float`, `DEFAULT_LLAMA_THREADS: int`, `DEFAULT_AI_VERSION: String` — configuration defaults.
- `SrcMorphConfiguration()` — initializes default configuration.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated file directory.
- `isForce()`/`setForce(boolean)` -> force regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subtree scope.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> planning-only execution.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> AI provider name.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> llama.cpp fallback model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-file plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-file AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or error handling.
- Nullable configuration values are annotated `@Nullable`.
- File-size documentation defines `minFileSizeBytes <= 0` as disabled and `maxFileSizeBytes <= 0` as unlimited.

#### Concurrency
- Mutable fields; no synchronization or thread-safety guarantees shown.

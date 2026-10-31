### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:31:12Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI generation routing and model settings, and metadata for generated documentation.

#### Purpose
- `SrcMorphConfiguration` is the mutable shared configuration for a `srcmorph` run, independent of its binding source.
- Keeps Maven lifecycle skip flags outside the engine configuration.

#### Type
- `public class SrcMorphConfiguration` — class; Lombok `@ToString`.
- No superclass or implemented interfaces declared.
- Public defaults: output directory `src/site/ai`, generation provider `mock`, llama.cpp context size `2048`, maximum output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, and AI version `0.0.0`.

#### Input
- No-argument constructor; initializes defaults.
- Setters accept project/output directories, file-selection filters and size bounds, generation settings, prompt/model/rule/fact definitions, llama.cpp options, and project/version metadata.
- Nullable lists and values represent optional configuration; values are stored directly.

#### Output
- Getters expose configured values; setters mutate the bean’s fields.
- Configuration describes source scope, AI routing and model parameters, output location, and generated-file metadata.
- Lombok `@ToString` provides a string representation.

#### Core logic
- Holds source-processing scope: base directory, subtrees, excludes, extensions, minimum and maximum file sizes.
- Holds generation controls: `force`, `planOnly`, provider, prompt definitions, field-generation rules, and fact definitions.
- Holds model configuration: AI definitions, model directory, and llama.cpp fallback path and sampling/inference settings.
- Holds output and metadata settings: output directory, plugin version, AI version, and project name.
- Relative model paths resolve against `modelDirectory` when configured; otherwise against the JVM working directory. Absolute paths remain usable as supplied.
- `llamaModelPath` and llama.cpp fallback settings apply when field-generation rules are empty.

#### Public API
- `SrcMorphConfiguration()` — creates configuration with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> regenerate existing AI fields.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source-file globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> AI model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI logic version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.
- `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION` -> public configuration defaults.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or error handling.
- Nullable fields and parameters are annotated with `@Nullable`; required reference fields are not initialized by the constructor.

#### Concurrency
- Mutable bean with no synchronization or thread-safety guarantees.

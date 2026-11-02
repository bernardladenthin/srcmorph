### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:35Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source indexing and AI-generated documentation, including file selection, model routing, inference settings, and output metadata.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, independent of Maven or other binding surfaces.
- Keeps Maven lifecycle skip flags outside the engine configuration.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; annotated `@ToString`.
- Public constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.
- Nullable list and value fields are annotated `@Nullable`.

#### Input
- Setters accept base/output directories, source selection filters, generation/provider definitions, llama.cpp fallback parameters, version strings, and project name.
- Configuration includes prompt definitions, AI model definitions, field-generation routing rules, and reusable fact definitions.
- Relative model paths can be resolved against `modelDirectory`; absolute paths remain usable.

#### Output
- Stores configuration state for consumers; getters expose all 23 instance properties.
- Defaults: output directory `src/site/ai`; provider `mock`; llama context size `2048`; max output tokens `128`; temperature `0.15f`; threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.
- Other primitive fields default to `false` or `0`; `pluginVersion` defaults to `""`.

#### Core logic
- Acts as a binding-neutral configuration object; constructor performs no additional work.
- Exposes `force` to control regeneration of populated AI fields and `planOnly` to stop after routing-plan output.
- Provides source subtrees, exclude globs, extensions, and file-size bounds for processing scope.
- Supplies provider/model definitions, prompt templates, per-field routing, and fact groups.
- Provides fallback llama.cpp model path and inference parameters when field-generation rules are empty.
- Carries plugin/AI versions and project title for generated documentation metadata.

#### Public API
- `SrcMorphConfiguration()` -> `SrcMorphConfiguration` — construct with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> regeneration override.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subdirectories.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded file globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> plan-only execution.
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
- `getPluginVersion()`/`setPluginVersion(String)` -> recorded generator version.
- `getAiVersion()`/`setAiVersion(String)` -> recorded AI logic version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition` (referenced types)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No exceptions declared or handled.
- Nullable configuration values are accepted for optional lists, model paths, model directory, and project name; no validation is performed here.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:33Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures source-file selection, AI generation and model routing, output locations, and metadata for a srcmorph run.

#### Purpose
- Provides one mutable configuration for source indexing and `.ai.md` generation, independent of Maven or other binding mechanisms.
- Supplies defaults for output location, generation provider, llama.cpp inference, and AI version.

#### Type
- `public class SrcMorphConfiguration` — class; `@ToString`; no superclass or interfaces declared.
- Public constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- JavaBean setters accept base/output directories; file-selection filters and size limits; planning/force flags; provider and prompt/model/fact/routing definitions; llama.cpp settings; plugin/AI versions; and project name.
- Nullable lists and paths may be unset; no files or streams are read directly.

#### Output
- Getters expose all configuration values; setters mutate the corresponding fields.
- Configuration supplies output and model paths, source-selection rules, generation settings, and metadata to consumers.

#### Core logic
- Initializes output directory to `src/site/ai`, provider to `mock`, llama.cpp context size to `2048`, maximum output tokens to `128`, temperature to `0.15f`, threads to `AiGenerationConfig.DEFAULT_THREADS`, and AI version to `0.0.0`.
- Leaves other fields at Java defaults or `null`; constructor performs no additional work.
- Holds source subtrees, exclusion globs, extensions, and exclusive-minimum/inclusive-maximum file-size bounds.
- Holds prompt, model, fact, and per-field generation definitions for AI routing; model directory resolves relative model paths.
- Holds llama.cpp fallback settings, used when field-generation rules are empty; `planOnly` requests routing-plan output without model loading or generation.
- Holds plugin and AI versions for generated headers and an optional project title for the aggregate index.

#### Public API
- `SrcMorphConfiguration()` — initializes default configuration.
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
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate-index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks; nullable fields and parameters are marked with `@Nullable`.

#### Concurrency
- Mutable JavaBean with unsynchronized fields; no thread-safety guarantees.

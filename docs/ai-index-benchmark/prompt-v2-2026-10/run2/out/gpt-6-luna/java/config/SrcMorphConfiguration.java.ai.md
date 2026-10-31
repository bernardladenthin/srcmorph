### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:53Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file filtering, AI prompt/model routing, llama.cpp fallback settings, and metadata for generating AI summaries.

#### Purpose
- Provides the shared mutable configuration for a `srcmorph` run, independent of its binding source.
- Holds source selection, generation, model, and output settings; Maven lifecycle skip flags are excluded.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; annotated `@ToString`.
- No superclass or implemented interfaces.
- Seven public defaults: output directory `src/site/ai`, provider `mock`, llama.cpp context size `2048`, maximum output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, and AI version `0.0.0`.

#### Input
- Public no-argument constructor; initializes field defaults.
- Setters accept project/output directories; source subtrees, exclude globs, extensions, and file-size limits; planning and force flags; prompt, model, field-generation, and fact definitions; provider and llama.cpp settings; versions and project name.
- Nullable configuration lists and model-related values may be left unset.

#### Output
- Getters expose the configured values; no computation or external resource access.
- Configuration describes `.ai.md` output location, generation routing, and metadata recorded in generated files.

#### Core logic
- Stores source-selection controls: base directory, subtrees, excludes, extensions, and exclusive lower/inclusive upper file-size bounds.
- Stores generation controls: force regeneration, plan-only mode, provider name, prompt definitions, per-field routing rules, and reusable fact definitions.
- Stores AI model definitions and an optional model directory for resolving relative model paths.
- Stores llama.cpp fallback model path, context size, output-token limit, temperature, and thread count; fallback settings apply when field-generation rules are empty.
- Stores plugin and AI versions plus an optional project title.
- Defaults output directory to `src/site/ai`; limits and flags otherwise use Java zero/false defaults unless specified above.

#### Public API
- `SrcMorphConfiguration()` — constructs configuration with defaults.
- `DEFAULT_OUTPUT_DIRECTORY` — default output directory.
- `DEFAULT_GENERATION_PROVIDER` — default provider name.
- `DEFAULT_LLAMA_CONTEXT_SIZE` — default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` — default output token limit.
- `DEFAULT_LLAMA_TEMPERATURE` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS` — default inference thread count.
- `DEFAULT_AI_VERSION` — default AI logic version.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> force regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source-file globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed file extensions.
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
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project types: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable fields and lists are exposed as configured; setters do not guard against null.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

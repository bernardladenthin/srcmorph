### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:30Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures source-file discovery, AI generation and model routing, llama.cpp fallbacks, and metadata for generated project indexes.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, bindable from Maven, future JSON/YAML configuration, or Java callers.
- Carries source filters, AI prompt/model/fact definitions, generation settings, and output metadata.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`; no superclass or implemented interfaces.
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- Public defaults: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- No-argument constructor; initializes field defaults.
- Setters accept base/output directories, source scope and file-size filters, plan/force flags, provider, prompt/model/routing/fact definitions, llama.cpp fallback settings, version strings, and project name.
- Nullable lists and values represent optional configuration; `null` commonly means use engine defaults or no restrictions.

#### Output
- Getter methods expose the configured values; no processing, file writes, or other side effects.
- Configuration describes `.ai.md` output location, routing inputs, generation parameters, and generated-header/index metadata.

#### Core logic
- Initializes output directory to `src/site/ai`, provider to `mock`, llama.cpp context size to `2048`, maximum output tokens to `128`, temperature to `0.15f`, threads to `AiGenerationConfig.DEFAULT_THREADS`, and AI version to `0.0.0`.
- Leaves primitive flags and size bounds at Java defaults (`false` / `0`); other unset configuration values are `null`, except plugin version (`""`).
- Stores source subtrees, exclude globs, extensions, and exclusive-lower / inclusive-upper file-size bounds.
- Stores prompt definitions, model definitions, per-field generation rules, and reusable fact definitions for AI routing.
- Stores llama.cpp model path and inference settings as fallback configuration; model paths may be resolved relative to `modelDirectory`.
- Keeps Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` — default output path.
- `DEFAULT_GENERATION_PROVIDER` — default provider.
- `DEFAULT_LLAMA_CONTEXT_SIZE` — default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` — default output-token limit.
- `DEFAULT_LLAMA_TEMPERATURE` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS` — default inference thread count.
- `DEFAULT_AI_VERSION` — default AI logic version.
- `SrcMorphConfiguration()` — creates configuration with defaults.
- `getBaseDirectory()` / `setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()` / `setOutputDirectory(File)` -> `.ai.md` output directory.
- `isForce()` / `setForce(boolean)` -> regenerate existing fields.
- `getSubtrees()` / `setSubtrees(List<String>)` -> source subdirectory scope.
- `getExcludes()` / `setExcludes(List<String>)` -> excluded source globs.
- `getFileExtensions()` / `setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()` / `setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()` / `setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()` / `setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()` / `setGenerationProvider(String)` -> default generation provider.
- `getPromptDefinitions()` / `setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()` / `setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()` / `setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()` / `setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()` / `setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()` / `setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()` / `setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()` / `setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()` / `setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()` / `setLlamaThreads(int)` -> fallback CPU thread count.
- `getPluginVersion()` / `setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()` / `setAiVersion(String)` -> generated-header AI version.
- `getProjectName()` / `setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `AiGenerationConfig.DEFAULT_THREADS`
- `org.jspecify.annotations.Nullable`

#### Concurrency
- Mutable JavaBean with no synchronization; callers must coordinate concurrent mutation and access.

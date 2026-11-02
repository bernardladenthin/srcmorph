### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:43:21Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configure source-file AI generation, model routing, filtering, and output metadata for a `srcmorph` run.

#### Purpose
- `SrcMorphConfiguration` centralizes mutable run settings shared across Maven, future JSON/YAML, and direct Java callers.
- Holds source selection, AI prompts/models/facts, llama.cpp fallbacks, output settings, and generated-index metadata.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`.
- Mutable JavaBean with a public no-argument constructor; no superclass or implemented interfaces.
- 7 public static final defaults: output directory `src/site/ai`, provider `mock`, llama context size `2048`, maximum output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`, and AI version `0.0.0`.

#### Input
- Constructor takes no parameters.
- Setters accept project/output directories; force and planning flags; subtree, exclusion, extension, and file-size filters; provider, prompt, model, field-generation, and fact definitions; model paths and llama.cpp settings; plugin/AI versions and project name.
- Nullable lists and values represent unconfigured options; configuration is populated through setters.

#### Output
- Getters expose all configured values; setters mutate the configuration instance.
- Settings describe `.ai.md` output location, source selection, model routing and fallback inference parameters, and output metadata.
- No files are read or written by this type.

#### Core logic
- Initializes output directory, provider, llama.cpp fallback parameters, and AI version to declared defaults; other fields retain Java defaults or `null`.
- Stores source subtrees, exclude globs, extensions, and exclusive lower / inclusive upper file-size bounds for engine filtering.
- Stores prompt definitions, model definitions, field-generation routing rules, and reusable fact definitions.
- Provides model-directory configuration for resolving relative model paths; absolute paths remain usable.
- Exposes `force` regeneration and `planOnly` routing-plan controls.
- Keeps Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `SrcMorphConfiguration()` -> initialize defaults.
- `DEFAULT_OUTPUT_DIRECTORY` (`String`), `DEFAULT_GENERATION_PROVIDER` (`String`), `DEFAULT_LLAMA_CONTEXT_SIZE` (`int`), `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` (`int`), `DEFAULT_LLAMA_TEMPERATURE` (`float`), `DEFAULT_LLAMA_THREADS` (`int`), `DEFAULT_AI_VERSION` (`String`) -> configuration defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> `.ai.md` output directory.
- `isForce()`/`setForce(boolean)` -> force AI field regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> restrict source subdirectories.
- `getExcludes()`/`setExcludes(List<String>)` -> source-file exclude globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed file extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> llama.cpp fallback model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI logic version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- SrcMorph: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks.
- Nullable configuration values are stored and returned as supplied; non-null fields are not validated.

#### Concurrency
- Mutable instance with unsynchronized getters and setters; no thread-safety guarantees.

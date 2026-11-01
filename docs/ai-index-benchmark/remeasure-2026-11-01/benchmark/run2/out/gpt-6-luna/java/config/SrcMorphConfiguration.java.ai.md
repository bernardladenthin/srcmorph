### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:42:08Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source indexing and AI-generated documentation, including file selection, model routing, generation parameters, and output metadata.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, bindable from Maven, future JSON/YAML inputs, or Java callers.
- Keeps Maven lifecycle skip flags out of engine configuration.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`.
- Mutable JavaBean; no superclass or implemented interfaces.

#### Input
- Constructor takes no arguments.
- Setters accept project and output directories, source filters, size limits, planning and force flags, provider and prompt/model/routing/fact definitions, llama.cpp settings, versions, and project name.
- Configuration values reference `File`, `List<String>`, and lists of prompt, model, field-generation, and fact definitions.

#### Output
- Getter methods expose configured values; no processing or resource writes occur in this class.
- Defaults: output directory `src/site/ai`, provider `mock`, llama.cpp context size `2048`, max output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`; other primitive fields default to zero/false and plugin version to empty string.
- `baseDirectory` and nullable configuration fields default to `null`.

#### Core logic
- Stores configuration as mutable fields; setters assign values directly without validation or transformation.
- Supplies defaults for output location, provider, llama.cpp fallback parameters, and AI version.
- Documents source subtrees, exclusions, extensions, and size bounds as processing filters; `planOnly` and `force` control generation behavior.
- Stores routing definitions for prompts, models, field generations, and reusable fact groups.
- Provides model-directory and fallback GGUF path settings; model-directory semantics are documented for relative model paths.
- Stores plugin/AI versions and project title for generated output metadata.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` — default output directory.
- `DEFAULT_GENERATION_PROVIDER` — default generation provider.
- `DEFAULT_LLAMA_CONTEXT_SIZE` — default llama.cpp context size.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` — default output token limit.
- `DEFAULT_LLAMA_TEMPERATURE` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS` — default CPU thread count.
- `DEFAULT_AI_VERSION` — default AI logic version.
- `SrcMorphConfiguration()` -> `SrcMorphConfiguration` — creates default configuration.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> force regeneration flag.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subtree filters.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> planning-only flag.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default provider name.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> model path base directory.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Project types: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.
- Libraries: Lombok `ToString`; JSpecify `Nullable`.

#### Exceptions / Errors
- No explicit exceptions, validation, or error handling.
- Nullable fields and parameters are marked `@Nullable`; `baseDirectory` is not nullable-annotated but has no constructor initialization.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

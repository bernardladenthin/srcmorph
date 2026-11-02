### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:47Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file discovery, AI generation routing, model settings, and metadata for a srcmorph run.

#### Purpose
- Provides the shared mutable configuration for srcmorph, independent of Maven or future JSON/YAML binding.
- Holds source selection, output, AI prompts/models/rules, llama.cpp fallback settings, and generated-file metadata.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; `@ToString`.
- Suppresses `NullAway.Init` and uninitialized-field warnings.

#### Input
- Setters accept project/output directories, source filters and size limits, generation provider, prompt/model/fact definitions, field-generation rules, llama.cpp settings, versions, and project name.
- Uses `AiGenerationConfig.DEFAULT_THREADS` for the default llama.cpp thread count.

#### Output
- Getters expose the configuration state; no files or external resources are read or written.
- `@ToString` supplies a generated string representation.

#### Core logic
- Initializes defaults for output directory (`src/site/ai`), provider (`mock`), llama.cpp context size (`2048`), output tokens (`128`), temperature (`0.15f`), threads, and AI version (`0.0.0`).
- Stores all configuration values through direct getters and setters; performs no validation or processing.
- Uses nullable lists and values to represent unconfigured options; documents engine fallback behavior for extensions, model paths, and project title.
- Keeps Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `SrcMorphConfiguration()` -> constructs with defaults.
- `DEFAULT_OUTPUT_DIRECTORY` -> default output path.
- `DEFAULT_GENERATION_PROVIDER` -> default provider name.
- `DEFAULT_LLAMA_CONTEXT_SIZE` -> default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` -> default output-token limit.
- `DEFAULT_LLAMA_TEMPERATURE` -> default sampling temperature.
- `DEFAULT_LLAMA_THREADS` -> default inference thread count.
- `DEFAULT_AI_VERSION` -> default summarisation version.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated-file directory.
- `isForce()`/`setForce(boolean)` -> force AI field regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subtree scope.
- `getExcludes()`/`setExcludes(List<String>)` -> excluded source-file globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive minimum file size.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive maximum file size.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> AI model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> base for relative model paths.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> llama.cpp fallback model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No exceptions are thrown or caught.
- Most reference fields accept `null`; no validation is performed.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

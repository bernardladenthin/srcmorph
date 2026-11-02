### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:54Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Configures source-file indexing and AI-generated documentation, including file selection, model routing, inference settings, and output metadata.

#### Purpose
- `SrcMorphConfiguration` centralizes mutable `srcmorph` run settings for Maven binding, future JSON/YAML binding, or direct Java construction.
- Keeps lifecycle skip flags outside the shared configuration.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`.
- No superclass or implemented interfaces declared.
- Nullable collection and optional-value fields use JSpecify `@Nullable`.

#### Input
- Setters accept project/output directories, source-selection filters, file-size bounds, generation provider, prompt/model/routing/fact definitions, llama.cpp fallback settings, versions, and project name.
- Default values: output `src/site/ai`, provider `mock`, llama context `2048`, output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.
- Other primitive fields default to Java zero values; nullable fields default to `null`; plugin version defaults to `""`.

#### Output
- Getters expose configured values; `@ToString` provides generated string representation.
- Describes `.ai.md` output location, source processing scope, AI generation configuration, and output-header metadata; this class itself performs no generation or I/O.

#### Core logic
- Stores configuration as mutable bean properties, with each setter assigning its corresponding field.
- Provides defaults for output location, generation provider, llama.cpp fallback parameters, and AI summarisation version.
- Represents source scope through subtrees, exclusion globs, extensions, and exclusive minimum/inclusive maximum file sizes.
- Represents AI routing through prompt definitions, model definitions, per-field generation rules, and reusable fact definitions.
- Supports `planOnly` routing-plan runs and `force` regeneration.
- Model paths may be resolved against `modelDirectory`; absolute paths remain usable, while absent model directory leaves relative paths JVM-working-directory-relative.
- Llama.cpp model and inference settings are fallback configuration when `fieldGenerations` is empty.

#### Public API
- `SrcMorphConfiguration()` -> construct configuration with defaults
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory
- `getOutputDirectory()`/`setOutputDirectory(File)` -> `.ai.md` output directory
- `isForce()`/`setForce(boolean)` -> force regeneration
- `getSubtrees()`/`setSubtrees(List<String>)` -> source subdirectory scope
- `getExcludes()`/`setExcludes(List<String>)` -> source exclusion globs
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed file extensions
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive lower size bound
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive upper size bound
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model path base
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback CPU thread count
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-file plugin version
- `getAiVersion()`/`setAiVersion(String)` -> generated-file AI version
- `getProjectName()`/`setProjectName(String)` -> aggregate index title
- `DEFAULT_OUTPUT_DIRECTORY` -> `"src/site/ai"`
- `DEFAULT_GENERATION_PROVIDER` -> `"mock"`
- `DEFAULT_LLAMA_CONTEXT_SIZE` -> `2048`
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` -> `128`
- `DEFAULT_LLAMA_TEMPERATURE` -> `0.15f`
- `DEFAULT_LLAMA_THREADS` -> `AiGenerationConfig.DEFAULT_THREADS`
- `DEFAULT_AI_VERSION` -> `"0.0.0"`

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig` (referenced types)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or error handling.
- Nullable configuration values are accepted; no null checks are performed.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

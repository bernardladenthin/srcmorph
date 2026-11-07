### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:52Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures source-file selection, AI prompt/model routing, llama.cpp fallback settings, and metadata for generating `.ai.md` project documentation.

#### Purpose
- Shared mutable configuration for `srcmorph` runs, bindable from Maven, future JSON/YAML, or Java callers.
- Holds source filtering, generation routing, model settings, output location, and generated-document metadata.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`.
- No superclass or interfaces declared.

#### Input
- `File` paths for project base, output, and model directories; `String` provider, model path, versions, and project name.
- Lists of source subtrees, excludes, extensions, prompt definitions, model definitions, field-generation rules, and fact definitions.
- File-size bounds, force and plan-only flags, and llama.cpp context, token, temperature, and thread settings.

#### Output
- Exposes configuration values through JavaBean getters; setters replace stored values.
- Defaults: output `src/site/ai`, provider `mock`, llama context `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.
- Other fields default to `false`, numeric zero, empty plugin version, or `null`.

#### Core logic
- Stores a unified configuration for source indexing and AI documentation generation; performs no generation itself.
- Uses `fieldGenerations` for per-field routing; llama.cpp settings and `llamaModelPath` are documented as fallbacks when field-generation rules are empty.
- `modelDirectory` specifies the base for relative model paths; absolute paths remain usable, and absent a model directory, relative paths use the JVM working directory.
- Source scope is controlled by base-relative subtrees, exclude globs, extensions, and file-size limits; nonpositive size bounds disable their respective limits.
- `force` requests regeneration of populated AI fields; `planOnly` stops a generate run after its routing plan.
- Maven lifecycle skip flags are deliberately not part of this configuration.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` -> default output path; `DEFAULT_GENERATION_PROVIDER` -> default provider; `DEFAULT_LLAMA_CONTEXT_SIZE` -> default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` -> default output limit; `DEFAULT_LLAMA_TEMPERATURE` -> default sampling temperature; `DEFAULT_LLAMA_THREADS` -> default CPU threads.
- `DEFAULT_AI_VERSION` -> default summarisation version.
- `SrcMorphConfiguration()` -> initializes defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> `.ai.md` output directory.
- `isForce()`/`setForce(boolean)` -> regenerate populated fields.
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
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback CPU thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`; `java.util.List`.
- `lombok.ToString`; `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`.
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.

#### Exceptions / Errors
- No explicit exceptions, validation, or error handling.
- Nullable lists and values may be unset; getters return stored values directly.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.

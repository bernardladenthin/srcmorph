### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:20:26Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI generation routing and model settings, and metadata for producing `.ai.md` indexes.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, independent of its binding source.
- Holds file-selection rules, prompt/model/fact definitions, llama.cpp fallback settings, and output metadata; excludes Maven lifecycle skip flags.

#### Type
- `SrcMorphConfiguration` — public class; annotated `@ToString`.
- Public constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.
- Mutable JavaBean; nullable collection and optional-value fields use JSpecify `@Nullable`.

#### Input
- Public no-argument constructor; initializes fields with defaults.
- Setters accept base/output directories, file-selection rules, generation settings, prompt/model/fact definitions, fallback llama.cpp parameters, versions, and project name.
- Configuration may be populated by Maven, a future JSON/YAML binder, or plain Java callers.

#### Output
- Getters expose the configured values; setters mutate the configuration object.
- Output and model directories are represented as `File`; paths, versions, and provider names as `String`.
- Defaults: output `src/site/ai`; provider `mock`; llama context size `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.
- Lombok `@ToString` supplies a string representation.

#### Core logic
- Stores source roots and file filters: subtrees, exclude globs, extensions, and minimum/maximum file sizes.
- Stores generation controls: `force`, `planOnly`, provider, and per-field routing rules.
- Stores prompt templates, model definitions, reusable fact definitions, and a model directory for resolving relative model paths.
- Provides fallback llama.cpp model path and inference settings, documented as used when `fieldGenerations` is empty.
- Stores plugin/AI versions for generated headers and an optional project title for the aggregate index.
- Applies no validation, path resolution, file processing, or generation logic itself.

#### Public API
- `SrcMorphConfiguration()` → `void` — construct with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` → `File`/`void` — project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` → `File`/`void` — `.ai.md` output directory.
- `isForce()`/`setForce(boolean)` → `boolean`/`void` — force regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` → `List<String>`/`void` — restrict source subtrees.
- `getExcludes()`/`setExcludes(List<String>)` → `List<String>`/`void` — exclude source globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` → `List<String>`/`void` — select file extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` → `long`/`void` — exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` → `long`/`void` — inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` → `boolean`/`void` — stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` → `String`/`void` — choose generation provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` → `List<AiPromptDefinition>`/`void` — configure prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` → `List<AiModelDefinition>`/`void` — configure AI models.
- `getModelDirectory()`/`setModelDirectory(File)` → `File`/`void` — resolve relative model paths.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` → `List<AiFieldGenerationConfig>`/`void` — configure field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` → `List<AiFactDefinition>`/`void` — configure reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` → `String`/`void` — set fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` → `int`/`void` — set fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` → `int`/`void` — set fallback output limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` → `float`/`void` — set fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` → `int`/`void` — set fallback inference threads.
- `getPluginVersion()`/`setPluginVersion(String)` → `String`/`void` — set generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` → `String`/`void` — set generated-header AI version.
- `getProjectName()`/`setProjectName(String)` → `String`/`void` — set aggregate-index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig` (same package)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Declares no exceptions and performs no validation.
- Nullable fields and unset `baseDirectory` may remain `null`; nonpositive file-size bounds are documented as disabled or unlimited.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:07Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file discovery, AI generation routing, model inference defaults, and metadata for producing `.ai.md` documentation.

#### Purpose
- Shared mutable configuration bean for `srcmorph` runs, bindable from Maven, future JSON/YAML configuration, or Java callers.
- Holds source filters, generation rules, provider/model settings, and output metadata.

#### Type
- `public class SrcMorphConfiguration`; no superclass or implemented interfaces declared.
- `@ToString`; suppresses `NullAway.Init` and `initialization.fields.uninitialized` warnings.
- Nullable collection and optional settings use `@Nullable`.

#### Input
- Setters accept project/output/model directories, source filters, file-size limits, generation provider, prompt/model/field/fact definitions, llama.cpp parameters, versions, and project name.
- Configuration types: `File`, `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, and `List<AiFactDefinition>`.

#### Output
- Getters expose all configuration values.
- Defaults: output directory `src/site/ai`; provider `mock`; llama.cpp context size `2048`, maximum output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`; plugin version empty string.
- `force`, `planOnly`, size bounds, and optional references default to Java zero/null values.

#### Core logic
- Acts as a mutable data holder; constructor performs no additional work.
- Defines source scope (`subtrees`, `excludes`, `fileExtensions`) and exclusive minimum / inclusive maximum file-size bounds; non-positive bounds disable their respective limits.
- Supplies generation provider and per-field routing configuration, including prompt, model, and reusable fact definitions.
- Provides model-directory resolution context and llama.cpp fallback parameters; documented fallback settings apply when `fieldGenerations` is empty.
- Carries plugin/AI versions and project title for generated documentation metadata.
- Leaves Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `SrcMorphConfiguration()` → constructor; defaults applied by field initializers.
- `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION` → public static final defaults.
- `getBaseDirectory()` → `File`; read project base.
- `setBaseDirectory(File)` → `void`; set project base.
- `getOutputDirectory()` → `File`; read documentation output path.
- `setOutputDirectory(File)` → `void`; set documentation output path.
- `isForce()` → `boolean`; read regeneration override.
- `setForce(boolean)` → `void`; set regeneration override.
- `getSubtrees()` → `@Nullable List<String>`; read source subdirectory scope.
- `setSubtrees(@Nullable List<String>)` → `void`; set source subdirectory scope.
- `getExcludes()` → `@Nullable List<String>`; read excluded source globs.
- `setExcludes(@Nullable List<String>)` → `void`; set excluded source globs.
- `getFileExtensions()` → `@Nullable List<String>`; read indexed extensions.
- `setFileExtensions(@Nullable List<String>)` → `void`; set indexed extensions.
- `getMinFileSizeBytes()` → `long`; read exclusive minimum size.
- `setMinFileSizeBytes(long)` → `void`; set exclusive minimum size.
- `getMaxFileSizeBytes()` → `long`; read inclusive maximum size.
- `setMaxFileSizeBytes(long)` → `void`; set inclusive maximum size.
- `isPlanOnly()` → `boolean`; read planning-only mode.
- `setPlanOnly(boolean)` → `void`; set planning-only mode.
- `getGenerationProvider()` → `String`; read default AI provider.
- `setGenerationProvider(String)` → `void`; set default AI provider.
- `getPromptDefinitions()` → `@Nullable List<AiPromptDefinition>`; read prompt templates.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>)` → `void`; set prompt templates.
- `getAiDefinitions()` → `@Nullable List<AiModelDefinition>`; read model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>)` → `void`; set model definitions.
- `getModelDirectory()` → `@Nullable File`; read relative-model base directory.
- `setModelDirectory(@Nullable File)` → `void`; set relative-model base directory.
- `getFieldGenerations()` → `@Nullable List<AiFieldGenerationConfig>`; read field routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` → `void`; set field routing rules.
- `getFactDefinitions()` → `@Nullable List<AiFactDefinition>`; read reusable fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>)` → `void`; set reusable fact groups.
- `getLlamaModelPath()` → `@Nullable String`; read fallback GGUF model path.
- `setLlamaModelPath(@Nullable String)` → `void`; set fallback GGUF model path.
- `getLlamaContextSize()` → `int`; read fallback context window.
- `setLlamaContextSize(int)` → `void`; set fallback context window.
- `getLlamaMaxOutputTokens()` → `int`; read fallback token limit.
- `setLlamaMaxOutputTokens(int)` → `void`; set fallback token limit.
- `getLlamaTemperature()` → `float`; read fallback sampling temperature.
- `setLlamaTemperature(float)` → `void`; set fallback sampling temperature.
- `getLlamaThreads()` → `int`; read fallback inference thread count.
- `setLlamaThreads(int)` → `void`; set fallback inference thread count.
- `getPluginVersion()` → `String`; read generated-header plugin version.
- `setPluginVersion(String)` → `void`; set generated-header plugin version.
- `getAiVersion()` → `String`; read AI logic version.
- `setAiVersion(String)` → `void`; set AI logic version.
- `getProjectName()` → `@Nullable String`; read aggregate index title.
- `setProjectName(@Nullable String)` → `void`; set aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project types: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks.
- Required-reference fields such as `baseDirectory` have no initializer and may be null until set.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:20:01Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI prompt/model routing, llama.cpp fallback inference, and metadata for generating `.ai.md` documentation.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable JavaBean for configuring a srcmorph run, independent of whether Maven, a future CLI, or Java code supplies the values.
- Holds source filtering, generation settings, model and prompt definitions, and output metadata; Maven lifecycle skip flags are explicitly excluded.

#### Type
- Public class `SrcMorphConfiguration`; mutable, non-final; no superclass or implemented interfaces declared.
- Annotated `@ToString`; suppresses `NullAway.Init` and initialization warnings.
- Nullable references use JSpecify `@Nullable`.

#### Input
- Setters accept the project/output/model directories, source filters and size bounds, generation provider, prompt/model/routing/fact definitions, llama.cpp fallback settings, versions, and project name.
- Configuration is intended for binding by Maven reflection, Jackson JSON/YAML mappers, or direct Java construction.

#### Output
- Stores configuration state for consumers; getters expose that state, including nullable optional lists and paths.
- Defaults: output directory `src/site/ai`; provider `mock`; llama context size `2048`; max output tokens `128`; temperature `0.15f`; threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.
- Other primitive defaults are Java defaults: booleans `false`, size bounds `0`; `pluginVersion` defaults to `""`.
- Defines public constants `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, and `DEFAULT_AI_VERSION`.

#### Core logic
- Acts as a mutable data carrier; constructor performs no additional initialization.
- Source selection is configured through base directory, optional subtrees, exclude globs, extensions, and minimum/maximum file sizes.
- Generation is configured through provider selection, prompt definitions, model definitions, per-field routing rules, and reusable fact definitions.
- Provides fallback llama.cpp model path and inference parameters; documentation states these fallbacks apply when `fieldGenerations` is empty.
- Carries output location, force-regeneration and plan-only flags, and version/project metadata for generated documentation.
- Relative model paths may resolve against `modelDirectory`; absolute paths remain usable, and absent `modelDirectory` leaves relative paths based on the JVM working directory.

#### Public API
- `SrcMorphConfiguration()` → `void` — creates default configuration.
- `getBaseDirectory()` → `File` — returns project base directory.
- `setBaseDirectory(File)` → `void` — sets project base directory.
- `getOutputDirectory()` → `File` — returns generated-file directory.
- `setOutputDirectory(File)` → `void` — sets generated-file directory.
- `isForce()` → `boolean` — checks forced regeneration.
- `setForce(boolean)` → `void` — configures forced regeneration.
- `getSubtrees()` → `@Nullable List<String>` — returns source subtrees.
- `setSubtrees(@Nullable List<String>)` → `void` — configures source subtrees.
- `getExcludes()` → `@Nullable List<String>` — returns exclude globs.
- `setExcludes(@Nullable List<String>)` → `void` — configures exclude globs.
- `getFileExtensions()` → `@Nullable List<String>` — returns indexed extensions.
- `setFileExtensions(@Nullable List<String>)` → `void` — configures indexed extensions.
- `getMinFileSizeBytes()` → `long` — returns exclusive lower size bound.
- `setMinFileSizeBytes(long)` → `void` — configures lower size bound.
- `getMaxFileSizeBytes()` → `long` — returns inclusive upper size bound.
- `setMaxFileSizeBytes(long)` → `void` — configures upper size bound.
- `isPlanOnly()` → `boolean` — checks plan-only mode.
- `setPlanOnly(boolean)` → `void` — configures plan-only mode.
- `getGenerationProvider()` → `String` — returns default generation provider.
- `setGenerationProvider(String)` → `void` — configures default generation provider.
- `getPromptDefinitions()` → `@Nullable List<AiPromptDefinition>` — returns prompt templates.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>)` → `void` — configures prompt templates.
- `getAiDefinitions()` → `@Nullable List<AiModelDefinition>` — returns AI model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>)` → `void` — configures AI model definitions.
- `getModelDirectory()` → `@Nullable File` — returns model path base directory.
- `setModelDirectory(@Nullable File)` → `void` — configures model path base directory.
- `getFieldGenerations()` → `@Nullable List<AiFieldGenerationConfig>` — returns field routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` → `void` — configures field routing rules.
- `getFactDefinitions()` → `@Nullable List<AiFactDefinition>` — returns reusable fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>)` → `void` — configures reusable fact groups.
- `getLlamaModelPath()` → `@Nullable String` — returns fallback GGUF model path.
- `setLlamaModelPath(@Nullable String)` → `void` — configures fallback GGUF model path.
- `getLlamaContextSize()` → `int` — returns fallback context size.
- `setLlamaContextSize(int)` → `void` — configures fallback context size.
- `getLlamaMaxOutputTokens()` → `int` — returns fallback output-token limit.
- `setLlamaMaxOutputTokens(int)` → `void` — configures fallback output-token limit.
- `getLlamaTemperature()` → `float` — returns fallback sampling temperature.
- `setLlamaTemperature(float)` → `void` — configures fallback sampling temperature.
- `getLlamaThreads()` → `int` — returns fallback inference thread count.
- `setLlamaThreads(int)` → `void` — configures fallback inference thread count.
- `getPluginVersion()` → `String` — returns generated-header plugin version.
- `setPluginVersion(String)` → `void` — configures generated-header plugin version.
- `getAiVersion()` → `String` — returns AI logic version.
- `setAiVersion(String)` → `void` — configures AI logic version.
- `getProjectName()` → `@Nullable String` — returns aggregate index title.
- `setProjectName(@Nullable String)` → `void` — configures aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Srcmorph: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks.
- Optional values are represented by nullable references; non-null fields and setter inputs are not validated here.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

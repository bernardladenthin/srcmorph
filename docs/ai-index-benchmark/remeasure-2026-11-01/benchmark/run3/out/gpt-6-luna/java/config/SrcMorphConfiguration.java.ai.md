### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:59:21Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file processing, AI generation routing, model settings, and metadata for generating `.ai.md` documentation.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, independent of its binding source.
- Holds source filters, generation rules, model settings, and output metadata; excludes Maven lifecycle skip flags.

#### Type
- `SrcMorphConfiguration` — public class; Lombok `@ToString`.
- Mutable JavaBean with a public no-argument constructor.

#### Input
- JavaBean setters accept project/output directories, source filters and size bounds, AI prompt/model/fact definitions, generation routing, llama.cpp settings, versions, and project name.
- Lists and several paths/names may be `null`; defaults are applied to selected fields at initialization.

#### Output
- Stores configuration in mutable fields for consumers to read through getters.
- Supplies defaults: output directory `src/site/ai`; provider `mock`; llama.cpp context size `2048`, max output tokens `128`, temperature `0.15`, and thread count `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.
- Plugin version defaults to the empty string; primitive booleans and size bounds default to `false` and `0`.

#### Core logic
- Defines shared configuration keys intended to mirror Maven parameter names for binding from Maven, future JSON/YAML, or Java callers.
- Holds source scope and filtering settings: base directory, subtrees, excludes, extensions, and minimum/maximum file sizes.
- Holds prompt, model, fact, and per-field generation definitions used to configure AI routing.
- Provides llama.cpp fallback settings; documentation notes these apply when field-generation rules are empty.
- Carries plugin/AI versions and project title for generated documentation metadata.
- Constructor performs no additional work; field initializers establish defaults.

#### Public API
- `SrcMorphConfiguration()` → `void` — creates default configuration.
- `DEFAULT_OUTPUT_DIRECTORY` → `String` — default output path.
- `DEFAULT_GENERATION_PROVIDER` → `String` — default provider name.
- `DEFAULT_LLAMA_CONTEXT_SIZE` → `int` — default context window.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` → `int` — default output token limit.
- `DEFAULT_LLAMA_TEMPERATURE` → `float` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS` → `int` — default inference thread count.
- `DEFAULT_AI_VERSION` → `String` — default AI logic version.
- `getBaseDirectory()` / `setBaseDirectory(File)` → `File` / `void` — project base directory.
- `getOutputDirectory()` / `setOutputDirectory(File)` → `File` / `void` — generated documentation directory.
- `isForce()` / `setForce(boolean)` → `boolean` / `void` — force regeneration.
- `getSubtrees()` / `setSubtrees(List<String>)` → `List<String>` / `void` — restrict source subdirectories.
- `getExcludes()` / `setExcludes(List<String>)` → `List<String>` / `void` — source-file exclusion globs.
- `getFileExtensions()` / `setFileExtensions(List<String>)` → `List<String>` / `void` — extensions to index.
- `getMinFileSizeBytes()` / `setMinFileSizeBytes(long)` → `long` / `void` — exclusive lower size bound.
- `getMaxFileSizeBytes()` / `setMaxFileSizeBytes(long)` → `long` / `void` — inclusive upper size bound.
- `isPlanOnly()` / `setPlanOnly(boolean)` → `boolean` / `void` — stop after routing plan.
- `getGenerationProvider()` / `setGenerationProvider(String)` → `String` / `void` — default AI provider.
- `getPromptDefinitions()` / `setPromptDefinitions(List<AiPromptDefinition>)` → `List<AiPromptDefinition>` / `void` — prompt templates.
- `getAiDefinitions()` / `setAiDefinitions(List<AiModelDefinition>)` → `List<AiModelDefinition>` / `void` — model definitions.
- `getModelDirectory()` / `setModelDirectory(File)` → `File` / `void` — base for relative model paths.
- `getFieldGenerations()` / `setFieldGenerations(List<AiFieldGenerationConfig>)` → `List<AiFieldGenerationConfig>` / `void` — per-field routing rules.
- `getFactDefinitions()` / `setFactDefinitions(List<AiFactDefinition>)` → `List<AiFactDefinition>` / `void` — reusable fact groups.
- `getLlamaModelPath()` / `setLlamaModelPath(String)` → `String` / `void` — fallback GGUF model path.
- `getLlamaContextSize()` / `setLlamaContextSize(int)` → `int` / `void` — fallback context window.
- `getLlamaMaxOutputTokens()` / `setLlamaMaxOutputTokens(int)` → `int` / `void` — fallback output token limit.
- `getLlamaTemperature()` / `setLlamaTemperature(float)` → `float` / `void` — fallback sampling temperature.
- `getLlamaThreads()` / `setLlamaThreads(int)` → `int` / `void` — fallback inference thread count.
- `getPluginVersion()` / `setPluginVersion(String)` → `String` / `void` — generated-header plugin version.
- `getAiVersion()` / `setAiVersion(String)` → `String` / `void` — generated-header AI logic version.
- `getProjectName()` / `setProjectName(String)` → `String` / `void` — aggregate index title.
- **47 public methods total:** constructor and 46 accessors.

#### Dependencies
- `java.io.File`, `java.util.List`
- `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `AiGenerationConfig.DEFAULT_THREADS`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Declares no exceptions and performs no explicit validation.
- Nullable configuration values are retained as supplied; documented consumers may apply their own defaults.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

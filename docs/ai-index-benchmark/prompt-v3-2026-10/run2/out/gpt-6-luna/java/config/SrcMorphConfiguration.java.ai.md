### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:11:09Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source indexing, AI generation, model routing, and metadata for a srcmorph run.

#### Purpose
- Central mutable configuration for source selection, `.ai.md` output, and AI generation.
- Bindable from Maven, future JSON/YAML configuration, or direct Java construction.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean.
- Annotated `@ToString`; no superclass or implemented interfaces declared.
- Public constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- Setters accept project/output directories, source filters, file-size limits, generation settings, prompt/model/fact definitions, llama.cpp parameters, versions, and project name.
- Configuration types include `File`, `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, and `List<AiFactDefinition>`.
- Nullable lists and model/project paths may be unset; `baseDirectory` and other non-nullable fields have no explicit null validation.

#### Output
- Getters expose all configuration values; setters mutate the corresponding fields.
- Defaults: output directory `src/site/ai`, provider `mock`, llama context size `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.
- Other primitive fields default to Java zero/false; plugin version defaults to an empty string.

#### Core logic
- Holds source scope and filtering: base directory, subtrees, excludes, extensions, and minimum/maximum file sizes.
- Controls output behavior through output directory, force regeneration, and plan-only mode.
- Supplies AI provider selection, prompt definitions, model definitions/directory, field-generation routing rules, and reusable fact definitions.
- Provides llama.cpp fallback settings; documented as used only when field-generation rules are empty.
- Stores plugin/AI versions and optional project title for generated output metadata.
- Leaves Maven lifecycle skip flags outside this shared configuration.

#### Public API
- `SrcMorphConfiguration()` → constructor applying field defaults.
- `getBaseDirectory()` / `setBaseDirectory(File)` → project base directory.
- `getOutputDirectory()` / `setOutputDirectory(File)` → generated `.ai.md` output directory.
- `isForce()` / `setForce(boolean)` → force field regeneration.
- `getSubtrees()` / `setSubtrees(List<String>)` → source subdirectory scope.
- `getExcludes()` / `setExcludes(List<String>)` → source-file exclusion globs.
- `getFileExtensions()` / `setFileExtensions(List<String>)` → indexed file extensions.
- `getMinFileSizeBytes()` / `setMinFileSizeBytes(long)` → exclusive lower size bound.
- `getMaxFileSizeBytes()` / `setMaxFileSizeBytes(long)` → inclusive upper size bound.
- `isPlanOnly()` / `setPlanOnly(boolean)` → stop after routing plan.
- `getGenerationProvider()` / `setGenerationProvider(String)` → default AI provider.
- `getPromptDefinitions()` / `setPromptDefinitions(List<AiPromptDefinition>)` → prompt templates.
- `getAiDefinitions()` / `setAiDefinitions(List<AiModelDefinition>)` → AI model definitions.
- `getModelDirectory()` / `setModelDirectory(File)` → relative model-path base.
- `getFieldGenerations()` / `setFieldGenerations(List<AiFieldGenerationConfig>)` → field-generation routing rules.
- `getFactDefinitions()` / `setFactDefinitions(List<AiFactDefinition>)` → reusable fact groups.
- `getLlamaModelPath()` / `setLlamaModelPath(String)` → fallback GGUF model path.
- `getLlamaContextSize()` / `setLlamaContextSize(int)` → fallback context window.
- `getLlamaMaxOutputTokens()` / `setLlamaMaxOutputTokens(int)` → fallback token limit.
- `getLlamaTemperature()` / `setLlamaTemperature(float)` → fallback sampling temperature.
- `getLlamaThreads()` / `setLlamaThreads(int)` → fallback inference thread count.
- `getPluginVersion()` / `setPluginVersion(String)` → generated-header plugin version.
- `getAiVersion()` / `setAiVersion(String)` → generated-header AI version.
- `getProjectName()` / `setProjectName(String)` → aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- srcmorph: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Exceptions / Errors
- No exceptions declared or handled.
- Nullable configuration values are accepted by selected setters; no validation is performed in this class.

#### Concurrency
- Mutable fields; no synchronization or thread-safety guarantees.

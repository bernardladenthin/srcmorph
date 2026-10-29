### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:46:44Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable configuration bean that binds all parameters (source filters, AI model settings, llama.cpp inference options, and generation routing rules) required to execute a srcmorph AI code-indexing run.

#### Purpose
- Single shared configuration object for the `srcmorph` engine, bindable from Maven `@Parameter` fields, JSON/YAML files, or direct Java construction.
- Holds source discovery filters, AI generation provider settings, llama.cpp inference parameters, and per-field routing rules.

#### Type
- `public class SrcMorphConfiguration`
- Annotated with `@ToString` (Lombok) and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No inheritance; pure mutable JavaBean with getters/setters.

#### Input
- Populated via setters from Maven plugin mojos, Jackson `ObjectMapper`/`YAMLMapper`, or direct Java assignment.
- Consumes `File` paths, `List<String>` filters, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, and scalar inference parameters.

#### Output
- Returns configured values via getters for the srcmorph engine to consume.
- No side effects; pure data holder.

#### Core logic
- Stores 22 instance fields covering: base/output directories, force/planOnly flags, source filters (subtrees, excludes, fileExtensions, size bounds), generation provider, prompt/model/fact/field-generation definitions, llama.cpp fallback parameters (modelPath, contextSize, maxOutputTokens, temperature, threads), and version/project metadata.
- Applies defaults in field initializers: `outputDirectory` = `src/site/ai`, `generationProvider` = `mock`, `llamaContextSize` = 2048, `llamaMaxOutputTokens` = 128, `llamaTemperature` = 0.15f, `llamaThreads` = `AiGenerationConfig.DEFAULT_THREADS`, `aiVersion` = `0.0.0`.
- Deliberately excludes Maven lifecycle skip flags (`skip`, `skipFile`, `skipPackage`, `skipProject`); those remain mojo-side.

#### Public API
- `getBaseDirectory() -> File` — project root directory
- `setBaseDirectory(File) -> void` — set project root
- `getOutputDirectory() -> File` — `.ai.md` output location
- `setOutputDirectory(File) -> void` — set output location
- `isForce() -> boolean` — always regenerate flag
- `setForce(boolean) -> void` — set force flag
- `getSubtrees() -> @Nullable List<String>` — source subtree filters
- `setSubtrees(@Nullable List<String>) -> void` — set subtree filters
- `getExcludes() -> @Nullable List<String>` — glob exclude patterns
- `setExcludes(@Nullable List<String>) -> void` — set exclude patterns
- `getFileExtensions() -> @Nullable List<String>` — file extension filter
- `setFileExtensions(@Nullable List<String>) -> void` — set extension filter
- `getMinFileSizeBytes() -> long` — lower size bound
- `setMinFileSizeBytes(long) -> void` — set lower size bound
- `getMaxFileSizeBytes() -> long` — upper size bound
- `setMaxFileSizeBytes(long) -> void` — set upper size bound
- `isPlanOnly() -> boolean` — stop after routing plan
- `setPlanOnly(boolean) -> void` — set plan-only flag
- `getGenerationProvider() -> String` — AI provider name
- `setGenerationProvider(String) -> void` — set provider name
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` — prompt templates
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` — set prompt templates
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` — model definitions
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` — set model definitions
- `getModelDirectory() -> @Nullable File` — model path resolution base
- `setModelDirectory(@Nullable File) -> void` — set model directory
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` — per-field routing rules
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` — set routing rules
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` — named fact groups
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` — set fact groups
- `getLlamaModelPath() -> @Nullable String` — GGUF model fallback path
- `setLlamaModelPath(@Nullable String) -> void` — set model path
- `getLlamaContextSize() -> int` — llama.cpp context window
- `setLlamaContextSize(int) -> void` — set context window
- `getLlamaMaxOutputTokens() -> int` — max output tokens
- `setLlamaMaxOutputTokens(int) -> void` — set max tokens
- `getLlamaTemperature() -> float` — sampling temperature
- `setLlamaTemperature(float) -> void` — set temperature
- `getLlamaThreads() -> int` — CPU thread count
- `setLlamaThreads(int) -> void` — set thread count
- `getPluginVersion() -> String` — generator version string
- `setPluginVersion(String) -> void` — set generator version
- `getAiVersion() -> String` — AI logic version
- `setAiVersion(String) -> void` — set AI version
- `getProjectName() -> @Nullable String` — aggregate index title
- `setProjectName(@Nullable String) -> void` — set project title

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced via `DEFAULT_THREADS`)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No exceptions thrown or caught.
- `@Nullable` annotations on list and string fields indicate optional configuration; callers must handle null.
- `@SuppressWarnings("NullAway.Init")` acknowledges intentionally uninitialized fields (e.g., `baseDirectory`, `subtrees`).

#### Concurrency
- No synchronization; mutable bean not thread-safe by design.
- Intended to be constructed and populated before being passed to the engine, then treated as read-only during execution.

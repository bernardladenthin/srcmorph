### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:49:00Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI generation routing and model fallbacks, and metadata for producing `.ai.md` outputs.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for `srcmorph` runs, independent of whether values come from Maven, a future JSON/YAML CLI, or Java callers.
- Holds source filters, generation rules, model settings, and output metadata; Maven lifecycle skip flags are intentionally excluded.

#### Type
- Public class; no explicit superclass or implemented interfaces.
- `@ToString`; suppresses `NullAway.Init` and `initialization.fields.uninitialized` warnings.
- Designed as a mutable JavaBean with setters for binding compatibility.

#### Input
- Constructor accepts no arguments; callers populate values through setters.
- Configuration values include base/output/model directories; source subtrees, excludes, and extensions; file-size bounds; provider and AI prompt/model/fact/routing definitions; llama.cpp settings; versions and project name.

#### Output
- Stores configuration state for consumers; getters expose the configured values.
- Default output directory: `src/site/ai`; default provider: `mock`; default llama.cpp context: `2048`, max output tokens: `128`, temperature: `0.15f`, threads: `AiGenerationConfig.DEFAULT_THREADS`; default AI version: `0.0.0`.
- Other defaults: `force` and `planOnly` are `false`; size bounds are `0`; `pluginVersion` is empty; nullable settings start `null`.

#### Core logic
- Acts as a data holder; constructor performs no work.
- Supports source scope and filtering via `subtrees`, `excludes`, `fileExtensions`, and min/max file-size bounds.
- Holds AI prompt definitions, model definitions, reusable fact definitions, and field-generation routing rules.
- Provides provider-wide generation settings and llama.cpp fallback parameters; documentation specifies fallback settings apply when `fieldGenerations` is empty.
- Documents model-path resolution against `modelDirectory` for relative paths; absolute model paths remain usable, and absent `modelDirectory` leaves relative paths based on the JVM working directory.
- Carries generator/AI versions and project title for output metadata.

#### Public API
- `SrcMorphConfiguration()` → constructor; creates default configuration.
- `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION` → public default constants.
- `getBaseDirectory()` → `File`; reads project base directory.
- `setBaseDirectory(File)` → `void`; sets project base directory.
- `getOutputDirectory()` → `File`; reads output directory.
- `setOutputDirectory(File)` → `void`; sets output directory.
- `isForce()` → `boolean`; reads forced regeneration flag.
- `setForce(boolean)` → `void`; sets forced regeneration flag.
- `getSubtrees()` → `@Nullable List<String>`; reads source scope.
- `setSubtrees(@Nullable List<String>)` → `void`; sets source scope.
- `getExcludes()` → `@Nullable List<String>`; reads excluded-file globs.
- `setExcludes(@Nullable List<String>)` → `void`; sets excluded-file globs.
- `getFileExtensions()` → `@Nullable List<String>`; reads indexed extensions.
- `setFileExtensions(@Nullable List<String>)` → `void`; sets indexed extensions.
- `getMinFileSizeBytes()` → `long`; reads exclusive lower size bound.
- `setMinFileSizeBytes(long)` → `void`; sets exclusive lower size bound.
- `getMaxFileSizeBytes()` → `long`; reads inclusive upper size bound.
- `setMaxFileSizeBytes(long)` → `void`; sets inclusive upper size bound.
- `isPlanOnly()` → `boolean`; reads planning-only flag.
- `setPlanOnly(boolean)` → `void`; sets planning-only flag.
- `getGenerationProvider()` → `String`; reads default provider name.
- `setGenerationProvider(String)` → `void`; sets default provider name.
- `getPromptDefinitions()` → `@Nullable List<AiPromptDefinition>`; reads prompt templates.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>)` → `void`; sets prompt templates.
- `getAiDefinitions()` → `@Nullable List<AiModelDefinition>`; reads AI model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>)` → `void`; sets AI model definitions.
- `getModelDirectory()` → `@Nullable File`; reads model path base directory.
- `setModelDirectory(@Nullable File)` → `void`; sets model path base directory.
- `getFieldGenerations()` → `@Nullable List<AiFieldGenerationConfig>`; reads field routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` → `void`; sets field routing rules.
- `getFactDefinitions()` → `@Nullable List<AiFactDefinition>`; reads reusable fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>)` → `void`; sets reusable fact groups.
- `getLlamaModelPath()` → `@Nullable String`; reads fallback GGUF model path.
- `setLlamaModelPath(@Nullable String)` → `void`; sets fallback GGUF model path.
- `getLlamaContextSize()` → `int`; reads fallback context size.
- `setLlamaContextSize(int)` → `void`; sets fallback context size.
- `getLlamaMaxOutputTokens()` → `int`; reads fallback output-token limit.
- `setLlamaMaxOutputTokens(int)` → `void`; sets fallback output-token limit.
- `getLlamaTemperature()` → `float`; reads fallback sampling temperature.
- `setLlamaTemperature(float)` → `void`; sets fallback sampling temperature.
- `getLlamaThreads()` → `int`; reads fallback inference thread count.
- `setLlamaThreads(int)` → `void`; sets fallback inference thread count.
- `getPluginVersion()` → `String`; reads recorded generator version.
- `setPluginVersion(String)` → `void`; sets recorded generator version.
- `getAiVersion()` → `String`; reads recorded AI logic version.
- `setAiVersion(String)` → `void`; sets recorded AI logic version.
- `getProjectName()` → `@Nullable String`; reads aggregate index title.
- `setProjectName(@Nullable String)` → `void`; sets aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exception handling or validation.
- Several values are nullable; getters can return `null` as documented.
- Size-bound semantics: minimum `<= 0` disables the lower bound; maximum `<= 0` means unlimited.

#### Concurrency
- Mutable fields with no synchronization; no thread-safety guarantees stated.

### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:36:49Z
- G: 
- A: 0.0.0
- X: file
---
> Mutable JavaBean holding all parameters for a srcmorph AI documentation generation run, bindable from Maven plugin XML, future JSON/YAML config, or plain Java code.

#### Purpose
- Single shared configuration object for srcmorph engine runs across all binding surfaces
- Field names mirror Maven @Parameter names so JSON/YAML keys match existing plugin XML

#### Type
- Class, public, non-final, non-abstract, mutable JavaBean in package `net.ladenthin.srcmorph.config`
- No extends, no implements
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- No-arg constructor `SrcMorphConfiguration()` applying all defaults
- 23 setter methods for plexus reflection, Jackson/YAML binding, or direct Java calls
- Default-initialized fields: `outputDirectory` (`src/site/ai`), `generationProvider` (`mock`), `llamaContextSize` (2048), `llamaMaxOutputTokens` (128), `llamaTemperature` (0.15f), `llamaThreads` (`AiGenerationConfig.DEFAULT_THREADS`), `pluginVersion` (`""`), `aiVersion` (`0.0.0`)

#### Output
- 23 getter methods returning current configuration values
- Lombok-generated `toString()` representation
- No side effects, no resource writes, no produced files

#### Core logic
- Holds 23 fields covering: base/output directories, file filtering (subtrees, excludes, extensions, size bounds), execution mode (force, planOnly), AI provider selection, prompt/model/fact definition lists, llama.cpp fallback parameters, and version metadata for `.ai.md` headers
- Size bounds semantics: `minFileSizeBytes` exclusive lower bound (`<=0` disables), `maxFileSizeBytes` inclusive upper bound (`<=0` unlimited)
- Nullable collections default to `null` meaning "not configured" (engine uses its own defaults)
- Deliberately excludes Maven lifecycle skip flags (`skip`/`skipFile`/`skipPackage`/`skipProject`) which remain mojo-side

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` -> String — default output directory constant
- `DEFAULT_GENERATION_PROVIDER` -> String — default AI provider name
- `DEFAULT_LLAMA_CONTEXT_SIZE` -> int — default llama context size
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` -> int — default max output tokens
- `DEFAULT_LLAMA_TEMPERATURE` -> float — default sampling temperature
- `DEFAULT_LLAMA_THREADS` -> int — default CPU thread count
- `DEFAULT_AI_VERSION` -> String — default AI version string
- `SrcMorphConfiguration()` -> SrcMorphConfiguration — no-arg constructor with defaults
- `getBaseDirectory() -> File` — returns project base directory
- `setBaseDirectory(File) -> void` — sets project base directory
- `getOutputDirectory() -> File` — returns .ai.md output directory
- `setOutputDirectory(File) -> void` — sets .ai.md output directory
- `isForce() -> boolean` — true to always regenerate fields
- `setForce(boolean) -> void` — sets force regeneration flag
- `getSubtrees() -> @Nullable List<String>` — returns source subtrees filter
- `setSubtrees(@Nullable List<String>) -> void` — sets source subtrees filter
- `getExcludes() -> @Nullable List<String>` — returns exclude glob patterns
- `setExcludes(@Nullable List<String>) -> void` — sets exclude glob patterns
- `getFileExtensions() -> @Nullable List<String>` — returns file extensions to index
- `setFileExtensions(@Nullable List<String>) -> void` — sets file extensions to index
- `getMinFileSizeBytes() -> long` — returns exclusive lower size bound
- `setMinFileSizeBytes(long) -> void` — sets exclusive lower size bound
- `getMaxFileSizeBytes() -> long` — returns inclusive upper size bound
- `setMaxFileSizeBytes(long) -> void` — sets inclusive upper size bound
- `isPlanOnly() -> boolean` — true to stop after planning
- `setPlanOnly(boolean) -> void` — sets plan-only mode flag
- `getGenerationProvider() -> String` — returns AI provider name
- `setGenerationProvider(String) -> void` — sets AI provider name
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` — returns prompt templates
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` — sets prompt templates
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` — returns AI model definitions
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` — sets AI model definitions
- `getModelDirectory() -> @Nullable File` — returns model path base directory
- `setModelDirectory(@Nullable File) -> void` — sets model path base directory
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` — returns routing rules
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` — sets routing rules
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` — returns named fact groups
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` — sets named fact groups
- `getLlamaModelPath() -> @Nullable String` — returns GGUF model fallback path
- `setLlamaModelPath(@Nullable String) -> void` — sets GGUF model fallback path
- `getLlamaContextSize() -> int` — returns fallback context window size
- `setLlamaContextSize(int) -> void` — sets fallback context window size
- `getLlamaMaxOutputTokens() -> int` — returns fallback max output tokens
- `setLlamaMaxOutputTokens(int) -> void` — sets fallback max output tokens
- `getLlamaTemperature() -> float` — returns fallback sampling temperature
- `setLlamaTemperature(float) -> void` — sets fallback sampling temperature
- `getLlamaThreads() -> int` — returns fallback CPU thread count
- `setLlamaThreads(int) -> void` — sets fallback CPU thread count
- `getPluginVersion() -> String` — returns generator version for headers
- `setPluginVersion(String) -> void` — sets generator version for headers
- `getAiVersion() -> String` — returns AI logic version for headers
- `setAiVersion(String) -> void` — sets AI logic version for headers
- `getProjectName() -> @Nullable String` — returns aggregate index title
- `setProjectName(@Nullable String) -> void` — sets aggregate index title

#### Dependencies
- java.io.File
- java.util.List
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- org.jspecify.annotations.Nullable
- AiGenerationConfig (same package, referenced in `DEFAULT_LLAMA_THREADS`)
- AiModelDefinition (same package, field type)
- AiFieldGenerationConfig (same package, field type)
- AiFactDefinition (same package, field type)

#### Exceptions / Errors
- No checked exceptions thrown by any method
- `@SuppressWarnings("NullAway.Init")`: `baseDirectory` may be null/uninitialized at construction
- `@Nullable` annotations on: `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`
- `minFileSizeBytes`/`maxFileSizeBytes`: values `<= 0` disable the respective bound

#### Concurrency
- Mutable JavaBean; not thread-safe, no synchronization
- Intended for single-threaded population (Maven plexus, Jackson, or direct calls) before engine execution

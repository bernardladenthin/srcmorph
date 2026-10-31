### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:25Z
- G: 
- A: 0.0.0
- X: file
---
> Root mutable JavaBean holding every parameter a `srcmorph` run needs, bindable from Maven mojos, a future CLI config file, or plain Java code.

#### Purpose
- Single shared config object for all `srcmorph` runs, independent of binding surface (Maven `@Parameter` reflection, Jackson ObjectMapper/YAMLMapper, direct construction).
- Field names intentionally mirror Maven `@Parameter` field names so future JSON/YAML keys match plugin XML.

#### Type
- Public class, mutable JavaBean with setters, no inheritance or interfaces.
- Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString` (Lombok).
- Package: `net.ladenthin.srcmorph.config`.

#### Input
- Constructor `SrcMorphConfiguration()` applies all defaults, no parameters.
- 22 setters accept `File`, `String`, `boolean`, `long`, `int`, `float`, or `@Nullable List<T>` values.
- Fields consumed: `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`.

#### Output
- 22 getters return field values; `getBaseDirectory`, `getOutputDirectory`, `getSubtrees`, `getExcludes`, `getFileExtensions`, `getMinFileSizeBytes`, `getMaxFileSizeBytes`, `getGenerationProvider`, `getPromptDefinitions`, `getAiDefinitions`, `getModelDirectory`, `getFieldGenerations`, `getFactDefinitions`, `getLlamaModelPath`, `getLlamaContextSize`, `getLlamaMaxOutputTokens`, `getLlamaTemperature`, `getLlamaThreads`, `getPluginVersion`, `getAiVersion`, `getProjectName`, `isForce`, `isPlanOnly`.
- 7 public static constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Core logic
- No business logic; pure data container with default field values.
- Defaults applied at field declaration: output dir `src/site/ai`, provider `mock`, context `2048`, max tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.
- Deliberately excludes per-goal Maven flags (`skip`, `skipFile`, `skipPackage`, `skipProject`); those stay mojo-side.
- Must remain mutable with setters for universal binding compatibility.

#### Public API
- `SrcMorphConfiguration() -> void` — constructs with defaults applied.
- `DEFAULT_OUTPUT_DIRECTORY -> String` — default relative output directory.
- `DEFAULT_GENERATION_PROVIDER -> String` — default provider name `mock`.
- `DEFAULT_LLAMA_CONTEXT_SIZE -> int` — default llama.cpp context size.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS -> int` — default max output tokens.
- `DEFAULT_LLAMA_TEMPERATURE -> float` — default sampling temperature.
- `DEFAULT_LLAMA_THREADS -> int` — default CPU thread count.
- `DEFAULT_AI_VERSION -> String` — default AI version.
- `getBaseDirectory() -> File` — returns project base directory.
- `setBaseDirectory(File) -> void` — sets project base directory.
- `getOutputDirectory() -> File` — returns AI output directory.
- `setOutputDirectory(File) -> void` — sets AI output directory.
- `isForce() -> boolean` — returns always-regenerate flag.
- `setForce(boolean) -> void` — sets always-regenerate flag.
- `getSubtrees() -> List<String>` — returns scoped subdirectory paths.
- `setSubtrees(List<String>) -> void` — sets scoped subdirectory paths.
- `getExcludes() -> List<String>` — returns skip glob patterns.
- `setExcludes(List<String>) -> void` — sets skip glob patterns.
- `getFileExtensions() -> List<String>` — returns extensions to index.
- `setFileExtensions(List<String>) -> void` — sets extensions to index.
- `getMinFileSizeBytes() -> long` — returns exclusive lower size bound.
- `setMinFileSizeBytes(long) -> void` — sets exclusive lower size bound.
- `getMaxFileSizeBytes() -> long` — returns inclusive upper size bound.
- `setMaxFileSizeBytes(long) -> void` — sets inclusive upper size bound.
- `isPlanOnly() -> boolean` — returns stop-after-planning flag.
- `setPlanOnly(boolean) -> void` — sets stop-after-planning flag.
- `getGenerationProvider() -> String` — returns AI provider name.
- `setGenerationProvider(String) -> void` — sets AI provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>` — returns prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition>) -> void` — sets prompt template definitions.
- `getAiDefinitions() -> List<AiModelDefinition>` — returns AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>) -> void` — sets AI model definitions.
- `getModelDirectory() -> File` — returns relative model path base.
- `setModelDirectory(File) -> void` — sets relative model path base.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` — returns per-field routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` — sets per-field routing rules.
- `getFactDefinitions() -> List<AiFactDefinition>` — returns named fact groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` — sets named fact groups.
- `getLlamaModelPath() -> String` — returns GGUF fallback model path.
- `setLlamaModelPath(String) -> void` — sets GGUF fallback model path.
- `getLlamaContextSize() -> int` — returns fallback context window size.
- `setLlamaContextSize(int) -> void` — sets fallback context window size.
- `getLlamaMaxOutputTokens() -> int` — returns fallback max output tokens.
- `setLlamaMaxOutputTokens(int) -> void` — sets fallback max output tokens.
- `getLlamaTemperature() -> float` — returns fallback sampling temperature.
- `setLlamaTemperature(float) -> void` — sets fallback sampling temperature.
- `getLlamaThreads() -> int` — returns fallback CPU thread count.
- `setLlamaThreads(int) -> void` — sets fallback CPU thread count.
- `getPluginVersion() -> String` — returns generator version for headers.
- `setPluginVersion(String) -> void` — sets generator version for headers.
- `getAiVersion() -> String` — returns AI logic version for headers.
- `setAiVersion(String) -> void` — sets AI logic version for headers.
- `getProjectName() -> String` — returns aggregate index title.
- `setProjectName(String) -> void` — sets aggregate index title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `AiGenerationConfig` (same package, referenced for `DEFAULT_THREADS`)
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition` (same package, referenced in types)

#### Exceptions / Errors
- Null handling via `@Nullable` annotations; null fields are valid config states (e.g., no subtrees means all roots in scope).
- No exceptions thrown or caught; no validation performed.

#### Concurrency
- Not thread-safe by design; mutable JavaBean with no synchronization.
- No shared state beyond instance fields; each config is independently constructed and populated.

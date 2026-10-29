### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:34:43Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and holds all parameters a srcmorph run needs, independent of binding source: Maven plugin fields, JSON/YAML configs, or direct Java construction.

#### Purpose
- Root mutable JavaBean holding every parameter a `srcmorph` run needs, independent of how it is bound: from Maven `@Parameter` fields, JSON/YAML config files, or plain Java code constructing one directly.
- Field names intentionally mirror Maven `@Parameter` field names so that a future JSON/YAML config's keys read the same as the existing plugin XML — this class is the single shared config object bindable from every surface.
- Deliberately excludes per-goal `skip`/`skipFile`/`skipPackage`/`skipProject` flags; those are a Maven lifecycle concern and stay mojo-side; an engine constructed from this configuration always executes when asked.
- Must remain a mutable JavaBean with setters so every binding technology (Maven plexus reflection, Jackson/YAML mapper, or plain caller) can populate it the same way.

#### Type
- Class: `public final class SrcMorphConfiguration` (implicitly mutable JavaBean, not `final`, uses `@ToString` from Lombok)
- Extends: none; implements: none
- Key generics: none; type bounds: none
- Notable annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString`

#### Input
- Constructor: `SrcMorphConfiguration()` — no-op, applies all defaults
- Setter methods for every field accept typed parameters
- Consumed fields: `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`

#### Output
- Getter methods return the current state of each field
- Produces written `.ai.md` files via downstream engine (not in this file)
- Side effects: mutates internal state via setters; records `pluginVersion` and `aiVersion` in written headers (handled by caller)

#### Core logic
- Serves as the single shared configuration object bindable from every surface (Maven plugin, JSON/YAML, direct Java)
- All fields have corresponding getters/setters enabling binding technologies (Maven plexus reflection, Jackson/YAML mapper, plain callers) to populate it uniformly
- Default constants applied in constructor: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`
- Deliberately excludes per-goal `skip`/`skipFile`/`skipPackage`/`skipProject` flags; those are Maven lifecycle concerns kept mojo-side
- Field names mirror today's Maven `@Parameter` field names (`outputDirectory`, `fieldGenerations`, `llamaContextSize`) so future JSON/YAML config keys read the same as existing plugin XML

#### Public API
- `getBaseDirectory() -> File`
- `setBaseDirectory(File baseDirectory) -> void`
- `getOutputDirectory() -> File`
- `setOutputDirectory(File outputDirectory) -> void`
- `isForce() -> boolean`
- `setForce(boolean force) -> void`
- `getSubtrees() -> @Nullable List<String>`
- `setSubtrees(@Nullable List<String> subtrees) -> void`
- `getExcludes() -> @Nullable List<String>`
- `setExcludes(@Nullable List<String> excludes) -> void`
- `getFileExtensions() -> @Nullable List<String>`
- `setFileExtensions(@Nullable List<String> fileExtensions) -> void`
- `getMinFileSizeBytes() -> long`
- `setMinFileSizeBytes(long minFileSizeBytes) -> void`
- `getMaxFileSizeBytes() -> long`
- `setMaxFileSizeBytes(long maxFileSizeBytes) -> void`
- `isPlanOnly() -> boolean`
- `setPlanOnly(boolean planOnly) -> void`
- `getGenerationProvider() -> String`
- `setGenerationProvider(String generationProvider) -> void`
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>`
- `setPromptDefinitions(@Nullable List<AiPromptDefinition> promptDefinitions) -> void`
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>`
- `setAiDefinitions(@Nullable List<AiModelDefinition> aiDefinitions) -> void`
- `getModelDirectory() -> @Nullable File`
- `setModelDirectory(@Nullable File modelDirectory) -> void`
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>`
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig> fieldGenerations) -> void`
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>`
- `setFactDefinitions(@Nullable List<AiFactDefinition> factDefinitions) -> void`
- `getLlamaModelPath() -> @Nullable String`
- `setLlamaModelPath(@Nullable String llamaModelPath) -> void`
- `getLlamaContextSize() -> int`
- `setLlamaContextSize(int llamaContextSize) -> void`
- `getLlamaMaxOutputTokens() -> int`
- `setLlamaMaxOutputTokens(int llamaMaxOutputTokens) -> void`
- `getLlamaTemperature() -> float`
- `setLlamaTemperature(float llamaTemperature) -> void`
- `getLlamaThreads() -> int`
- `setLlamaThreads(int llamaThreads) -> void`
- `getPluginVersion() -> String`
- `setPluginVersion(String pluginVersion) -> void`
- `getAiVersion() -> String`
- `setAiVersion(String aiVersion) -> void`
- `getProjectName() -> @Nullable String`
- `setProjectName(@Nullable String projectName) -> void`

Total: 36 public methods (20 getters + 16 setters)

#### Dependencies
- `java.io.File`
- `java.util.List`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- No explicit `throws` clauses on any method
- `@Nullable` annotations on `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`; callers must handle potential null
- No custom exceptions defined in this file

#### Concurrency
- No synchronization, volatile, or immutable guarantees
- Mutable JavaBean design implies not thread-safe; intended for single-threaded construction or external synchronization by caller

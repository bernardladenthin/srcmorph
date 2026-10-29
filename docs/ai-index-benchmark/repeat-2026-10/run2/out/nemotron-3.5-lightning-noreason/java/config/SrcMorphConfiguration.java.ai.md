### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:31Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and holds all parameters a srcmorph run needs, independent of binding source: Maven plugin fields, JSON/YAML configs, or direct Java construction.

#### Purpose
- Root mutable JavaBean holding every parameter a srcmorph run needs, independent of how it is bound: from Maven `@Parameter` fields, JSON/YAML config files, or plain Java construction.
- Field names intentionally mirror Maven `@Parameter` field names so a future JSON/YAML config's keys read the same as the existing plugin XML — this class is the single shared config object bindable from every surface.
- Deliberately excludes per-goal skip/skipFile/skipPackage/skipProject flags; those are a Maven lifecycle concern and stay mojo-side; an engine constructed from this configuration always executes when asked.
- Must remain a mutable JavaBean with setters so every binding technology (Maven plexus reflection, Jackson ObjectMapper/YAMLMapper, or plain caller) can populate it the same way.

#### Type
- Class: `public class SrcMorphConfiguration`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- Key defaults defined as `public static final` fields: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`
- No explicit `extends` or `implements`; uses Lombok `@ToString`

#### Input
- Constructor: `SrcMorphConfiguration()` (no-op, applies defaults)
- Setter methods for every field accept parameters of corresponding types
- Consumed fields: `baseDirectory`, `outputDirectory`, `force`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `planOnly`, `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `pluginVersion`, `aiVersion`, `projectName`

#### Output
- Getter methods return the current state of each field
- Produces written `.ai.md` files (external side effect, not modeled as return type)
- Side effects: config values drive AI generation routing, file filtering, model path resolution, and output directory selection

#### Core logic
- Serves as the central configuration hub for srcmorph AI-assisted source processing
- Enables binding from multiple sources (Maven, JSON/YAML, direct Java) through a uniform JavaBean interface
- Default values applied on construction: output dir `"src/site/ai"`, provider `"mock"`, llama context 2048, max tokens 128, temperature 0.15f, threads from `AiGenerationConfig.DEFAULT_THREADS`
- All fields have corresponding getters/setters for flexible population via reflection, Jackson, or direct code
- Excludes per-goal lifecycle flags (skip/skipFile/etc.) intentionally omitted; engine always executes when configured

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

Total: 38 public methods (20 getters + 18 setters)

#### Dependencies
- `java.io.File`
- `java.util.List`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`
- Referenced types: `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`

#### Exceptions / Errors
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` indicates potential uninitialized field access awareness
- No explicit thrown exceptions in source; nullability handled via `@Nullable` on getters returning `List`, `File`, `String` and field types
- Default values ensure sensible behavior when fields are not explicitly set

#### Concurrency
- No threading, synchronization, or immutability concerns documented
- Mutable JavaBean design implies not thread-safe by default; intended for single-run configuration population

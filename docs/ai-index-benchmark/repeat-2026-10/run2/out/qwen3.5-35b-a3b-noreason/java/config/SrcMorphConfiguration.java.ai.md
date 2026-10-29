### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:43Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable configuration object aggregating all parameters for AI-driven source code generation and summarization.

#### Purpose
- Serves as the single shared configuration object bindable from Maven plugins, JSON/YAML files, or direct Java code.
- Holds mutable JavaBean fields mirroring Maven `@Parameter` names to enable consistent binding across technologies.

#### Type
- `public class SrcMorphConfiguration`
- Modifiers: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No explicit `extends` or `implements`.
- Key fields: `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `File`.

#### Input
- Constructor: `SrcMorphConfiguration()` (no-arg, applies defaults).
- Setter parameters: `baseDirectory` (File), `outputDirectory` (File), `force` (boolean), `subtrees` (List<String>), `excludes` (List<String>), `fileExtensions` (List<String>), `minFileSizeBytes` (long), `maxFileSizeBytes` (long), `planOnly` (boolean), `generationProvider` (String), `promptDefinitions` (List<AiPromptDefinition>), `aiDefinitions` (List<AiModelDefinition>), `modelDirectory` (File), `fieldGenerations` (List<AiFieldGenerationConfig>), `factDefinitions` (List<AiFactDefinition>), `llamaModelPath` (String), `llamaContextSize` (int), `llamaMaxOutputTokens` (int), `llamaTemperature` (float), `llamaThreads` (int), `pluginVersion` (String), `aiVersion` (String), `projectName` (String).

#### Output
- Return types: `File`, `boolean`, `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`.
- Mutated fields: All private fields are mutable via setters.
- Side effects: None directly; acts as a data carrier for the generation engine.

#### Core logic
- Initializes default values for output directory, generation provider, and llama.cpp parameters (context size, tokens, temperature, threads).
- Stores project base and output directories for file I/O operations.
- Configures file filtering via subtrees, excludes, extensions, and size bounds.
- Defines AI generation strategy via `generationProvider`, `promptDefinitions`, `aiDefinitions`, and `fieldGenerations`.
- Manages fallback llama.cpp settings (`llamaModelPath`, `llamaContextSize`, etc.) when specific field generation rules are absent.
- Records versioning (`pluginVersion`, `aiVersion`) and project metadata (`projectName`) in generated output headers.

#### Public API
- `getBaseDirectory() -> File` returns project base directory.
- `setBaseDirectory(File) -> void` sets project base directory.
- `getOutputDirectory() -> File` returns output directory for `.ai.md` files.
- `setOutputDirectory(File) -> void` sets output directory.
- `isForce() -> boolean` returns force regeneration flag.
- `setForce(boolean) -> void` sets force regeneration flag.
- `getSubtrees() -> List<String>` returns source subdirectory paths.
- `setSubtrees(List<String>) -> void` sets source subdirectory paths.
- `getExcludes() -> List<String>` returns exclude glob patterns.
- `setExcludes(List<String>) -> void` sets exclude glob patterns.
- `getFileExtensions() -> List<String>` returns file extensions to index.
- `setFileExtensions(List<String>) -> void` sets file extensions to index.
- `getMinFileSizeBytes() -> long` returns lower file size bound.
- `setMinFileSizeBytes(long) -> void` sets lower file size bound.
- `getMaxFileSizeBytes() -> long` returns upper file size bound.
- `setMaxFileSizeBytes(long) -> void` sets upper file size bound.
- `isPlanOnly() -> boolean` returns plan-only mode flag.
- `setPlanOnly(boolean) -> void` sets plan-only mode flag.
- `getGenerationProvider() -> String` returns AI generation provider name.
- `setGenerationProvider(String) -> void` sets AI generation provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>` returns prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition>) -> void` sets prompt template definitions.
- `getAiDefinitions() -> List<AiModelDefinition>` returns AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>) -> void` sets AI model definitions.
- `getModelDirectory() -> File` returns model directory for path resolution.
- `setModelDirectory(File) -> void` sets model directory for path resolution.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` returns per-field generation rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` sets per-field generation rules.
- `getFactDefinitions() -> List<AiFactDefinition>` returns fact definition groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` sets fact definition groups.
- `getLlamaModelPath() -> String` returns llama.cpp model path fallback.
- `setLlamaModelPath(String) -> void` sets llama.cpp model path fallback.
- `getLlamaContextSize() -> int` returns llama.cpp context window size.
- `setLlamaContextSize(int) -> void` sets llama.cpp context window size.
- `getLlamaMaxOutputTokens() -> int` returns max output tokens per call.
- `setLlamaMaxOutputTokens(int) -> void` sets max output tokens per call.
- `getLlamaTemperature() -> float` returns sampling temperature.
- `setLlamaTemperature(float) -> void` sets sampling temperature.
- `getLlamaThreads() -> int` returns CPU thread count.
- `setLlamaThreads(int) -> void` sets CPU thread count.
- `getPluginVersion() -> String` returns generator version.
- `setPluginVersion(String) -> void` sets generator version.
- `getAiVersion() -> String` returns AI summarization logic version.
- `setAiVersion(String) -> void` sets AI summarization logic version.
- `getProjectName() -> String` returns project title.
- `setProjectName(String) -> void` sets project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced in `DEFAULT_LLAMA_THREADS`)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: Several fields and return types are annotated `@Nullable` (e.g., `subtrees`, `excludes`, `projectName`).
- No explicit error conditions or validation logic present in the class.

#### Concurrency
- Mutable JavaBean; not thread-safe for concurrent modification without external synchronization.
- No internal synchronization or immutability guarantees.

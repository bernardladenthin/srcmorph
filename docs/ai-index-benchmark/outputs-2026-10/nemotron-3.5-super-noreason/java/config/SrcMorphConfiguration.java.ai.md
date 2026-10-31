### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:01:43Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, including output paths, file filters, AI provider and model settings, prompt and field generation rules, and header version metadata.

#### Purpose
- Central mutable JavaBean for every parameter a srcmorph run needs, independent of binding source.
- Mirrors Maven @Parameter field names so future JSON/YAML config keys match existing plugin XML.

#### Type
- `public class SrcMorphConfiguration`
- Modifiers: public, non-final, mutable JavaBean
- Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString` (Lombok)
- No extends or implements.

#### Input
- Constructor: `SrcMorphConfiguration()` applies defaults via field initializers.
- Setter parameters for every field: `File`, `boolean`, `List<String>`, `long`, `String`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `int`, `float`.
- Default constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Output
- Getter return types matching each field.
- Mutated instance fields via setters.
- No external resources written directly.

#### Core logic
- Stores configuration state as mutable fields with default values applied at declaration.
- `baseDirectory` has no default and must be set externally.
- `outputDirectory` defaults to `src/site/ai` relative to base directory.
- `force` defaults to false; controls regeneration of existing AI fields.
- `subtrees`, `excludes`, `fileExtensions` are nullable lists restricting source file scope.
- `minFileSizeBytes` and `maxFileSizeBytes` bound indexed files by size; `<= 0` disables or means unlimited.
- `planOnly` stops a generate run after printing the routing plan without loading a model.
- `generationProvider` defaults to `mock`; supports `llamacpp-jni` and `openai-compatible`.
- `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions` are nullable lists of AI routing and template definitions.
- `modelDirectory` resolves relative model paths; absolute paths are used as-is.
- `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads` are llama.cpp fallback settings used when fieldGenerations is empty.
- `pluginVersion` and `aiVersion` are recorded in every written `.ai.md` header.
- `projectName` is the aggregate-project index title, nullable to fall back to engine default.

#### Public API
- `getBaseDirectory() -> File` returns project base directory.
- `setBaseDirectory(File) -> void` sets project base directory.
- `getOutputDirectory() -> File` returns output directory for `.ai.md` files.
- `setOutputDirectory(File) -> void` sets output directory.
- `isForce() -> boolean` returns whether AI fields are always regenerated.
- `setForce(boolean) -> void` sets force regeneration flag.
- `getSubtrees() -> List<String>` returns source subdirectory paths restricting processing.
- `setSubtrees(List<String>) -> void` sets source subtrees.
- `getExcludes() -> List<String>` returns glob patterns for source files to skip.
- `setExcludes(List<String>) -> void` sets exclude globs.
- `getFileExtensions() -> List<String>` returns file extensions to index.
- `setFileExtensions(List<String>) -> void` sets file extensions.
- `getMinFileSizeBytes() -> long` returns exclusive lower file-size bound.
- `setMinFileSizeBytes(long) -> void` sets lower file-size bound.
- `getMaxFileSizeBytes() -> long` returns inclusive upper file-size bound.
- `setMaxFileSizeBytes(long) -> void` sets upper file-size bound.
- `isPlanOnly() -> boolean` returns whether generate stops after planning.
- `setPlanOnly(boolean) -> void` sets plan-only flag.
- `getGenerationProvider() -> String` returns AI generation provider name.
- `setGenerationProvider(String) -> void` sets provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>` returns prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition>) -> void` sets prompt definitions.
- `getAiDefinitions() -> List<AiModelDefinition>` returns AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>) -> void` sets AI model definitions.
- `getModelDirectory() -> File` returns directory for resolving relative model paths.
- `setModelDirectory(File) -> void` sets model directory.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` returns per-field generation routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` sets field generation rules.
- `getFactDefinitions() -> List<AiFactDefinition>` returns reusable named fact definition groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` sets fact definitions.
- `getLlamaModelPath() -> String` returns GGUF model path fallback.
- `setLlamaModelPath(String) -> void` sets llama model path.
- `getLlamaContextSize() -> int` returns llama context window size.
- `setLlamaContextSize(int) -> void` sets llama context size.
- `getLlamaMaxOutputTokens() -> int` returns max output tokens.
- `setLlamaMaxOutputTokens(int) -> void` sets max output tokens.
- `getLlamaTemperature() -> float` returns sampling temperature.
- `setLlamaTemperature(float) -> void` sets sampling temperature.
- `getLlamaThreads() -> int` returns CPU thread count.
- `setLlamaThreads(int) -> void` sets CPU thread count.
- `getPluginVersion() -> String` returns generator plugin version.
- `setPluginVersion(String) -> void` sets plugin version.
- `getAiVersion() -> String` returns AI summarisation logic version.
- `setAiVersion(String) -> void` sets AI version.
- `getProjectName() -> String` returns project title for aggregate index.
- `setProjectName(String) -> void` sets project name.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced via `AiGenerationConfig.DEFAULT_THREADS`)
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`

#### Exceptions / Errors
- No explicit throws clauses.
- Nullable fields may return null; callers must handle null for `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`.
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"}` acknowledges uninitialized non-null fields at construction.

#### Concurrency
- No synchronization; mutable JavaBean not thread-safe for concurrent read/write.
- Intended for single-threaded configuration population before engine use.

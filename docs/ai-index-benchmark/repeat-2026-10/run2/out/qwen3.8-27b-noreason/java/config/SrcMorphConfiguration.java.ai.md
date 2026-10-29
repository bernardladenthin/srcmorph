### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:00Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all runtime parameters (source filters, AI model settings, output paths) required to execute a srcmorph AI code summarization and generation run.

#### Purpose
- Root mutable JavaBean configuration object for the `srcmorph` engine.
- Serves as a single bindable config surface for Maven plugins, future CLI (JSON/YAML), or direct Java construction.

#### Type
- `public class SrcMorphConfiguration`
- Annotated with `@ToString` (Lombok) and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
- Populated via setters from Maven `@Parameter` fields, JSON/YAML mappers, or direct Java code.
- Consumes `File` paths for directories, `List<String>` for filters/extensions, and `List<Ai*Definition>` objects for AI routing.

#### Output
- Provides configuration values via getters to the `srcmorph` engine.
- No side effects; pure data holder.

#### Core logic
- Defines 7 static default constants: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.
- Initializes instance fields with defaults where applicable (e.g., `outputDirectory`, `generationProvider`, `llamaContextSize`).
- Excludes Maven lifecycle `skip` flags, which remain in the Mojo layer.
- Supports nullable collections for optional filters (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`).

#### Public API
- `getBaseDirectory() -> File` - Returns project base directory.
- `setBaseDirectory(File) -> void` - Sets project base directory.
- `getOutputDirectory() -> File` - Returns AI output directory.
- `setOutputDirectory(File) -> void` - Sets AI output directory.
- `isForce() -> boolean` - Returns force regeneration flag.
- `setForce(boolean) -> void` - Sets force regeneration flag.
- `getSubtrees() -> @Nullable List<String>` - Returns source subtree filters.
- `setSubtrees(@Nullable List<String>) -> void` - Sets source subtree filters.
- `getExcludes() -> @Nullable List<String>` - Returns file exclude globs.
- `setExcludes(@Nullable List<String>) -> void` - Sets file exclude globs.
- `getFileExtensions() -> @Nullable List<String>` - Returns file extension filters.
- `setFileExtensions(@Nullable List<String>) -> void` - Sets file extension filters.
- `getMinFileSizeBytes() -> long` - Returns min file size bound.
- `setMinFileSizeBytes(long) -> void` - Sets min file size bound.
- `getMaxFileSizeBytes() -> long` - Returns max file size bound.
- `setMaxFileSizeBytes(long) -> void` - Sets max file size bound.
- `isPlanOnly() -> boolean` - Returns plan-only execution flag.
- `setPlanOnly(boolean) -> void` - Sets plan-only execution flag.
- `getGenerationProvider() -> String` - Returns AI provider name.
- `setGenerationProvider(String) -> void` - Sets AI provider name.
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` - Returns prompt template definitions.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` - Sets prompt template definitions.
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` - Returns AI model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` - Sets AI model definitions.
- `getModelDirectory() -> @Nullable File` - Returns model path base directory.
- `setModelDirectory(@Nullable File) -> void` - Sets model path base directory.
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` - Returns field generation routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` - Sets field generation routing rules.
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` - Returns fact definition groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` - Sets fact definition groups.
- `getLlamaModelPath() -> @Nullable String` - Returns llama.cpp model path.
- `setLlamaModelPath(@Nullable String) -> void` - Sets llama.cpp model path.
- `getLlamaContextSize() -> int` - Returns llama.cpp context size.
- `setLlamaContextSize(int) -> void` - Sets llama.cpp context size.
- `getLlamaMaxOutputTokens() -> int` - Returns llama.cpp max tokens.
- `setLlamaMaxOutputTokens(int) -> void` - Sets llama.cpp max tokens.
- `getLlamaTemperature() -> float` - Returns llama.cpp temperature.
- `setLlamaTemperature(float) -> void` - Sets llama.cpp temperature.
- `getLlamaThreads() -> int` - Returns llama.cpp thread count.
- `setLlamaThreads(int) -> void` - Sets llama.cpp thread count.
- `getPluginVersion() -> String` - Returns generator version string.
- `setPluginVersion(String) -> void` - Sets generator version string.
- `getAiVersion() -> String` - Returns AI logic version.
- `setAiVersion(String) -> void` - Sets AI logic version.
- `getProjectName() -> @Nullable String` - Returns project title.
- `setProjectName(@Nullable String) -> void` - Sets project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No exceptions thrown.
- Uses `@Nullable` annotations to indicate optional fields.
- Suppresses NullAway initialization warnings.

#### Concurrency
- Mutable JavaBean; not thread-safe.
- Intended to be constructed and populated before use, then treated as read-only during execution.

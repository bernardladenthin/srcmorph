### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:57:26Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable JavaBean holding all parameters for a srcmorph AI code-indexing run, bindable from Maven, JSON/YAML, or direct Java construction.

#### Purpose
- Single shared configuration object for the `srcmorph` engine, decoupling parameter binding from the execution logic.
- Mirrors Maven `@Parameter` field names to allow seamless migration to JSON/YAML config files.

#### Type
- `public class SrcMorphConfiguration`
- Annotated with `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` and `@ToString` (Lombok).

#### Input
- Populated via setters from Maven Plexus reflection, Jackson `ObjectMapper`/`YAMLMapper`, or direct Java assignment.
- Consumes `File` paths, `List<String>` globs/extensions, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, and scalar primitives for llama.cpp tuning.

#### Output
- Provides read-only access to all configuration state via getters.
- No side effects; purely a data carrier.

#### Core logic
- Holds 24 instance fields representing source scope, AI provider settings, llama.cpp inference parameters, and versioning metadata.
- Applies default values for `outputDirectory`, `generationProvider`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, and `aiVersion` at field declaration.
- Excludes Maven lifecycle skip flags (`skip`, `skipFile`, etc.), keeping those concerns in the Mojo layer.
- Uses `@Nullable` annotations to distinguish "not configured" (null) from explicit empty collections.

#### Public API
- `getBaseDirectory() -> File` - Returns project base directory
- `setBaseDirectory(File) -> void` - Sets project base directory
- `getOutputDirectory() -> File` - Returns AI output directory
- `setOutputDirectory(File) -> void` - Sets AI output directory
- `isForce() -> boolean` - Returns force regeneration flag
- `setForce(boolean) -> void` - Sets force regeneration flag
- `getSubtrees() -> @Nullable List<String>` - Returns source subtree filters
- `setSubtrees(@Nullable List<String>) -> void` - Sets source subtree filters
- `getExcludes() -> @Nullable List<String>` - Returns file exclude globs
- `setExcludes(@Nullable List<String>) -> void` - Sets file exclude globs
- `getFileExtensions() -> @Nullable List<String>` - Returns file extension filters
- `setFileExtensions(@Nullable List<String>) -> void` - Sets file extension filters
- `getMinFileSizeBytes() -> long` - Returns min file size bound
- `setMinFileSizeBytes(long) -> void` - Sets min file size bound
- `getMaxFileSizeBytes() -> long` - Returns max file size bound
- `setMaxFileSizeBytes(long) -> void` - Sets max file size bound
- `isPlanOnly() -> boolean` - Returns plan-only execution flag
- `setPlanOnly(boolean) -> void` - Sets plan-only execution flag
- `getGenerationProvider() -> String` - Returns AI provider name
- `setGenerationProvider(String) -> void` - Sets AI provider name
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` - Returns prompt template definitions
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` - Sets prompt template definitions
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` - Returns AI model definitions
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` - Sets AI model definitions
- `getModelDirectory() -> @Nullable File` - Returns model path base directory
- `setModelDirectory(@Nullable File) -> void` - Sets model path base directory
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` - Returns field routing rules
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` - Sets field routing rules
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` - Returns fact definition groups
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` - Sets fact definition groups
- `getLlamaModelPath() -> @Nullable String` - Returns llama.cpp model path
- `setLlamaModelPath(@Nullable String) -> void` - Sets llama.cpp model path
- `getLlamaContextSize() -> int` - Returns llama.cpp context size
- `setLlamaContextSize(int) -> void` - Sets llama.cpp context size
- `getLlamaMaxOutputTokens() -> int` - Returns llama.cpp max tokens
- `setLlamaMaxOutputTokens(int) -> void` - Sets llama.cpp max tokens
- `getLlamaTemperature() -> float` - Returns llama.cpp temperature
- `setLlamaTemperature(float) -> void` - Sets llama.cpp temperature
- `getLlamaThreads() -> int` - Returns llama.cpp thread count
- `setLlamaThreads(int) -> void` - Sets llama.cpp thread count
- `getPluginVersion() -> String` - Returns generator version string
- `setPluginVersion(String) -> void` - Sets generator version string
- `getAiVersion() -> String` - Returns AI logic version
- `setAiVersion(String) -> void` - Sets AI logic version
- `getProjectName() -> @Nullable String` - Returns project title
- `setProjectName(@Nullable String) -> void` - Sets project title

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
- No exceptions thrown or caught.
- `@Nullable` fields may return `null`, requiring callers to handle absence of configuration.
- `@SuppressWarnings` suppresses NullAway initialization warnings for fields set externally.

#### Concurrency
- Mutable JavaBean; not thread-safe.
- Intended to be constructed and populated before being passed to the engine, then treated as read-only during execution.

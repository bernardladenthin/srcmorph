### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:37Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI generation routing, model parameters, and metadata for producing `.ai.md` documentation.

#### Purpose
- `SrcMorphConfiguration` is the mutable, binding-independent configuration for a srcmorph run.
- Exposes source filters, generation rules, provider fallbacks, and output metadata through JavaBean accessors.

#### Type
- `SrcMorphConfiguration` — public class.
- Annotated with Lombok `@ToString`; suppresses initialization warnings with `@SuppressWarnings`.
- 23 mutable instance fields; no superclass or implemented interfaces.
- Public defaults: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Input
- Setter parameters configure base/output directories, file-selection filters, generation behavior, AI prompt/model/fact/routing definitions, llama.cpp fallbacks, and version/project metadata.
- Uses `File`, nullable lists and strings, primitive numeric values, and `AiPromptDefinition`.
- No files, streams, or configuration resources are read directly.

#### Output
- Stores configuration values for caller retrieval; does not execute generation or write files.
- `outputDirectory` defaults to `src/site/ai`; `generationProvider` to `mock`.
- llama.cpp defaults: context size `2048`, max output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`.
- `aiVersion` defaults to `0.0.0`; `pluginVersion` defaults to empty string.
- Other primitive flags and size bounds default to `false` and `0`; nullable fields default to `null`.

#### Core logic
- Provides mutable JavaBean access to settings shared across Maven, future JSON/YAML binding, and direct Java construction.
- Captures source scope (`subtrees`, `excludes`, `fileExtensions`) and exclusive minimum/inclusive maximum file-size bounds; non-positive bounds disable the respective limit.
- Supplies prompt definitions, model definitions, per-field generation routing, and reusable fact definitions.
- Allows a model directory for resolving relative model paths; absolute paths remain usable independently.
- Provides llama.cpp fallback settings and model path; documented fallback use applies when `fieldGenerations` is empty.
- Tracks `force` regeneration, `planOnly` routing-plan execution, and metadata for generated headers and aggregate-project title.
- Deliberately excludes Maven lifecycle skip flags.

#### Public API
- `SrcMorphConfiguration()` → constructor applying field defaults
- `getBaseDirectory()`/`setBaseDirectory(File)` → project base directory
- `getOutputDirectory()`/`setOutputDirectory(File)` → generated documentation directory
- `isForce()`/`setForce(boolean)` → force field regeneration
- `getSubtrees()`/`setSubtrees(List<String>)` → restrict source subdirectories
- `getExcludes()`/`setExcludes(List<String>)` → source exclusion globs
- `getFileExtensions()`/`setFileExtensions(List<String>)` → indexed file extensions
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` → exclusive minimum file size
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` → inclusive maximum file size
- `isPlanOnly()`/`setPlanOnly(boolean)` → stop after routing plan
- `getGenerationProvider()`/`setGenerationProvider(String)` → default AI provider
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` → prompt templates
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` → model definitions
- `getModelDirectory()`/`setModelDirectory(File)` → relative model path base
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` → per-field routing rules
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` → reusable fact groups
- `getLlamaModelPath()`/`setLlamaModelPath(String)` → fallback GGUF model path
- `getLlamaContextSize()`/`setLlamaContextSize(int)` → llama.cpp context window
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` → llama.cpp output token limit
- `getLlamaTemperature()`/`setLlamaTemperature(float)` → llama.cpp sampling temperature
- `getLlamaThreads()`/`setLlamaThreads(int)` → llama.cpp inference thread count
- `getPluginVersion()`/`setPluginVersion(String)` → generated header plugin version
- `getAiVersion()`/`setAiVersion(String)` → generated header AI version
- `getProjectName()`/`setProjectName(String)` → aggregate index title

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable fields and parameters are marked with `@Nullable`; non-null fields are not validated by setters.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

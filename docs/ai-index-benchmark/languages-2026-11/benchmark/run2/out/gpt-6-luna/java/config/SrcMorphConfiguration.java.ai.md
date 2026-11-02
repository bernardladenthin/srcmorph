### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:33Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file selection, AI prompt/model routing, llama.cpp fallbacks, and metadata for generating AI documentation.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a srcmorph run, independent of its binding source.
- Keeps Maven lifecycle skip flags outside the engine configuration.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; annotated `@ToString`.
- No superclass or implemented interfaces declared.
- Defaults: output directory `src/site/ai`, provider `mock`, llama context size `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.

#### Input
- Constructor takes no parameters.
- Setters accept project paths, source filters, file-size bounds, generation options, prompt/model/rule/fact definitions, llama.cpp settings, versions, and project name.
- Lists and several path/name values may be `null`.

#### Output
- Getters expose configuration values to consumers; setters mutate the bean’s fields.
- Configuration describes source selection and `.ai.md` output, AI routing and model resolution, fallback inference parameters, and generated-file metadata.
- Lombok `@ToString` provides a string representation.

#### Core logic
- Initializes defaults for output directory, provider, llama.cpp parameters, and AI version; other primitive fields use Java defaults.
- Stores source subtrees, exclusion globs, extensions, and exclusive/inclusive file-size bounds.
- Stores field-generation routing rules and referenced prompt, model, and fact definitions.
- Supports model-directory resolution configuration and fallback llama.cpp model parameters.
- Carries plugin/AI versions and an optional project title for generated output.

#### Public API
- `SrcMorphConfiguration()` → constructor; creates default configuration.
- `getBaseDirectory()`/`setBaseDirectory(File)` → project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` → generated-file output directory.
- `isForce()`/`setForce(boolean)` → regenerate populated AI fields.
- `getSubtrees()`/`setSubtrees(List<String>)` → source subdirectory scope.
- `getExcludes()`/`setExcludes(List<String>)` → source-file exclusion globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` → indexed file extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` → exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` → inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` → stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` → default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` → prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` → AI model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` → base for relative model paths.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` → per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` → reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` → fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` → fallback llama context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` → fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` → fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` → fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` → generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` → generated-header AI logic version.
- `getProjectName()`/`setProjectName(String)` → aggregate-project index title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `AiGenerationConfig.DEFAULT_THREADS`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exception handling or validation.
- Nullable fields and parameters are annotated where shown; other reference fields are not annotated nullable.

#### Concurrency
- Mutable fields are unsynchronized; no thread-safety guarantees are declared.

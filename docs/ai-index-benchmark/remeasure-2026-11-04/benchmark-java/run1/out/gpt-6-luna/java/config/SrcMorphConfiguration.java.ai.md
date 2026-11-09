### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:39Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Configures source-file indexing and AI-generated documentation, including file scope, model routing, inference settings, and output metadata.

#### Purpose
- Provides a mutable, binding-independent configuration for `srcmorph` runs.
- Supplies source filtering, AI prompt/model/fact routing, generation defaults, and `.ai.md` output settings.

#### Type
- `public class SrcMorphConfiguration` — class; Lombok `@ToString`; mutable JavaBean.
- No superclass or implemented interfaces declared.

#### Input
- Constructor takes no arguments.
- Setters accept project/output/model directories; source subtrees, excludes, extensions, and size limits; AI provider, prompts, models, field-generation and fact definitions; llama.cpp settings; version metadata and project title.
- Configuration can be populated by Maven, future JSON/YAML binding, or direct Java callers.

#### Output
- Getter methods expose configured values to `srcmorph` consumers.
- Configuration describes `.ai.md` output location, processing scope, AI generation behavior, and index metadata; this type performs no generation or file writes.

#### Core logic
- Initializes defaults: output directory `src/site/ai`, provider `mock`, llama.cpp context size `2048`, output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`, AI version `0.0.0`.
- Leaves `force`, `planOnly`, and file-size bounds at Java defaults (`false`, `false`, `0`); other optional fields are nullable.
- Stores source selection controls: base-relative subtrees and exclude globs, file extensions, exclusive minimum and inclusive maximum byte sizes.
- Stores AI routing inputs: prompt definitions, model definitions, per-field generation rules, and reusable fact definitions.
- Stores llama.cpp fallback model path and inference parameters; documentation identifies fallback settings as applicable when `fieldGenerations` is empty.
- Stores plugin/AI versions and optional project title for generated metadata.
- Excludes Maven lifecycle skip flags; an engine built from this configuration runs when invoked.

#### Public API
- `SrcMorphConfiguration()` → constructor; initializes field defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` → project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` → generated `.ai.md` directory.
- `isForce()`/`setForce(boolean)` → regenerate populated AI fields.
- `getSubtrees()`/`setSubtrees(List<String>)` → restrict source roots.
- `getExcludes()`/`setExcludes(List<String>)` → skip matching source files.
- `getFileExtensions()`/`setFileExtensions(List<String>)` → select indexed file extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` → exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` → inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` → stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` → default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` → prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` → model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` → resolve relative model paths.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` → per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` → reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` → fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` → fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` → fallback token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` → fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` → fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` → generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` → generated-header AI logic version.
- `getProjectName()`/`setProjectName(String)` → aggregate index title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No exceptions declared or caught.
- Nullable lists, paths, and project name represent unspecified configuration; nonpositive minimum disables its bound, and nonpositive maximum means unlimited.

#### Concurrency
- Mutable, unsynchronized JavaBean; no thread-safety guarantees.

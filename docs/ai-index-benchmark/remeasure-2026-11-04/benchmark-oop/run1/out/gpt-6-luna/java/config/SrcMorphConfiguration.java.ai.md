### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:20:27Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Centralizes source indexing and AI-generation settings, including file selection, prompt/model routing, llama.cpp fallbacks, and generated index metadata.

#### Purpose
- Provides the mutable configuration shared by `srcmorph` execution and binding surfaces such as Maven, future JSON/YAML, and plain Java callers.
- Excludes Maven lifecycle skip flags; an engine built from this configuration executes when requested.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`; mutable JavaBean with no declared superclass or interfaces.
- Public constants: `DEFAULT_OUTPUT_DIRECTORY` (`String`), `DEFAULT_GENERATION_PROVIDER` (`String`), `DEFAULT_LLAMA_CONTEXT_SIZE` (`int`), `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` (`int`), `DEFAULT_LLAMA_TEMPERATURE` (`float`), `DEFAULT_LLAMA_THREADS` (`int`), `DEFAULT_AI_VERSION` (`String`).

#### Input
- No-argument constructor; defaults output directory to `src/site/ai`, provider to `mock`, llama.cpp context size to `2048`, maximum output tokens to `128`, temperature to `0.15f`, thread count to `AiGenerationConfig.DEFAULT_THREADS`, and AI version to `0.0.0`.
- Setters accept base/output directories; force and plan-only flags; source subtrees, exclude globs, extensions, and file-size bounds; provider; prompt, model, field-generation, and fact definitions; model paths and llama.cpp parameters; plugin/AI versions; project name.
- Nullable configuration includes lists, model directory, llama model path, and project name; several other fields have no initializer and are expected to be populated by a binding or caller.

#### Output
- Mutable configuration state consumed by source discovery, file filtering, AI routing/generation, and output metadata.
- Supplies output directory for `.ai.md` files, model path resolution base, and generator/project version and title metadata.
- Getters return configured values directly; list values are not copied.

#### Core logic
- Stores 23 configuration properties as mutable fields, exposing JavaBean getters and setters for binding compatibility.
- Defines `minFileSizeBytes` as an exclusive lower bound (`<= 0` disables it) and `maxFileSizeBytes` as an inclusive upper bound (`<= 0` means unlimited).
- Uses `subtrees` to restrict source roots, `excludes` to skip base-relative glob paths, and `fileExtensions` to override engine-default extensions.
- Uses `fieldGenerations` as per-field routing rules; prompt definitions, AI model definitions, and reusable fact definitions supply referenced configuration.
- Provides llama.cpp fallback settings when field-generation rules are empty; model paths may resolve against `modelDirectory`, while absolute paths remain usable.
- Stores `force` for regenerating populated AI fields and `planOnly` for stopping after routing-plan output.

#### Public API
- `SrcMorphConfiguration()` -> constructor; initializes defaults.
- `DEFAULT_OUTPUT_DIRECTORY: String`, `DEFAULT_GENERATION_PROVIDER: String`, `DEFAULT_LLAMA_CONTEXT_SIZE: int`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS: int`, `DEFAULT_LLAMA_TEMPERATURE: float`, `DEFAULT_LLAMA_THREADS: int`, `DEFAULT_AI_VERSION: String` -> public defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> generated file directory.
- `isForce()`/`setForce(boolean)` -> regenerate populated fields.
- `getSubtrees()`/`setSubtrees(List<String>)` -> source-root restrictions.
- `getExcludes()`/`setExcludes(List<String>)` -> skipped source-file globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> indexed extensions.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> exclusive size lower bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> inclusive size upper bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> relative model-path base.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> generated-header plugin version.
- `getAiVersion()`/`setAiVersion(String)` -> generated-header AI logic version.
- `getProjectName()`/`setProjectName(String)` -> aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions, validation, or null checks.
- Nullable fields are annotated `@Nullable`; non-null-annotated reference fields are not validated by this class.

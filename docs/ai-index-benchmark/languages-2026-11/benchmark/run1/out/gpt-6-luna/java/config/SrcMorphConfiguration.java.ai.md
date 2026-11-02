### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:14:19Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file processing, AI generation and routing, model settings, and metadata for producing `.ai.md` documentation.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable configuration for a `srcmorph` run, independent of how settings are bound.
- Carries file-selection options, AI prompt/model/routing definitions, llama.cpp fallbacks, and output metadata.

#### Type
- `public class SrcMorphConfiguration` — class; mutable JavaBean; annotated `@ToString`.
- No superclass or implemented interfaces declared.
- Nullability is marked with JSpecify `@Nullable` on optional fields and accessors.

#### Input
- Public no-argument constructor; all configuration is supplied through setters.
- Project and output directories; force and plan-only flags; source subtrees, excludes, extensions, and size bounds.
- Generation provider; prompt, model, field-generation, and fact-definition lists; model directory and llama.cpp fallback settings.
- Plugin version, AI version, and optional project name.

#### Output
- Configuration values are returned through getters; setters mutate the corresponding fields.
- No processing, resource reads, or writes are performed by this class.

#### Core logic
- Initializes defaults: output directory `src/site/ai`, provider `mock`, llama.cpp context size `2048`, max output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`, and AI version `0.0.0`.
- Leaves unspecified fields at Java defaults, including `false`, zero, empty string for `pluginVersion`, or `null`.
- Stores source-selection bounds and patterns without applying them; `minFileSizeBytes <= 0` disables the lower bound and `maxFileSizeBytes <= 0` means unlimited.
- Holds AI routing inputs: prompt definitions, model definitions, per-field generation rules, and reusable fact definitions.
- Documents model-directory resolution for relative model paths; absolute paths remain usable.
- Excludes Maven lifecycle skip flags; execution decisions remain outside this configuration.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` — default output directory (`src/site/ai`).
- `DEFAULT_GENERATION_PROVIDER` — default provider (`mock`).
- `DEFAULT_LLAMA_CONTEXT_SIZE` — default llama.cpp context size (`2048`).
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` — default llama.cpp output-token limit (`128`).
- `DEFAULT_LLAMA_TEMPERATURE` — default llama.cpp temperature (`0.15f`).
- `DEFAULT_LLAMA_THREADS` — default llama.cpp thread count.
- `DEFAULT_AI_VERSION` — default AI version (`0.0.0`).
- `SrcMorphConfiguration()` -> `void` — creates configuration with defaults.
- `getBaseDirectory()`/`setBaseDirectory(File)` -> `File`/`void` — project base directory.
- `getOutputDirectory()`/`setOutputDirectory(File)` -> `File`/`void` — `.ai.md` output directory.
- `isForce()`/`setForce(boolean)` -> `boolean`/`void` — force AI field regeneration.
- `getSubtrees()`/`setSubtrees(List<String>)` -> `List<String>`/`void` — restrict source subdirectories.
- `getExcludes()`/`setExcludes(List<String>)` -> `List<String>`/`void` — source-file exclude globs.
- `getFileExtensions()`/`setFileExtensions(List<String>)` -> `List<String>`/`void` — extensions to index.
- `getMinFileSizeBytes()`/`setMinFileSizeBytes(long)` -> `long`/`void` — exclusive lower size bound.
- `getMaxFileSizeBytes()`/`setMaxFileSizeBytes(long)` -> `long`/`void` — inclusive upper size bound.
- `isPlanOnly()`/`setPlanOnly(boolean)` -> `boolean`/`void` — stop after routing plan.
- `getGenerationProvider()`/`setGenerationProvider(String)` -> `String`/`void` — default AI provider.
- `getPromptDefinitions()`/`setPromptDefinitions(List<AiPromptDefinition>)` -> `List<AiPromptDefinition>`/`void` — prompt templates.
- `getAiDefinitions()`/`setAiDefinitions(List<AiModelDefinition>)` -> `List<AiModelDefinition>`/`void` — AI model definitions.
- `getModelDirectory()`/`setModelDirectory(File)` -> `File`/`void` — base for relative model paths.
- `getFieldGenerations()`/`setFieldGenerations(List<AiFieldGenerationConfig>)` -> `List<AiFieldGenerationConfig>`/`void` — per-field routing rules.
- `getFactDefinitions()`/`setFactDefinitions(List<AiFactDefinition>)` -> `List<AiFactDefinition>`/`void` — reusable fact groups.
- `getLlamaModelPath()`/`setLlamaModelPath(String)` -> `String`/`void` — fallback GGUF model path.
- `getLlamaContextSize()`/`setLlamaContextSize(int)` -> `int`/`void` — fallback context window.
- `getLlamaMaxOutputTokens()`/`setLlamaMaxOutputTokens(int)` -> `int`/`void` — fallback output-token limit.
- `getLlamaTemperature()`/`setLlamaTemperature(float)` -> `float`/`void` — fallback sampling temperature.
- `getLlamaThreads()`/`setLlamaThreads(int)` -> `int`/`void` — fallback inference thread count.
- `getPluginVersion()`/`setPluginVersion(String)` -> `String`/`void` — generator version metadata.
- `getAiVersion()`/`setAiVersion(String)` -> `String`/`void` — AI logic version metadata.
- `getProjectName()`/`setProjectName(String)` -> `String`/`void` — aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`.
- `lombok.ToString`.
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`.
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- Declares no exceptions and performs no validation.
- Optional values may be `null`; `baseDirectory` and several non-nullable fields are not initialized by the constructor.

#### Concurrency
- Mutable fields with no synchronization or thread-safety guarantees.
